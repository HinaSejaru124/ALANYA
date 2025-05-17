package video;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class WebcamFXClient extends Application {
    private static final String HOST = "localhost";
    private static final int VIDEO_PORT = 2000;
    private static final int AUDIO_PORT = 2001;

    private ImageView imageView;
    private SourceDataLine speakers;

    private PriorityBlockingQueue<VideoFrame> videoQueue = new PriorityBlockingQueue<>();
    private PriorityBlockingQueue<AudioChunk> audioQueue = new PriorityBlockingQueue<>();

    @Override
    public void start(Stage stage) {
        imageView = new ImageView();
        StackPane root = new StackPane(imageView);
        stage.setScene(new Scene(root, 640, 480));
        stage.setTitle("Webcam synchronisée");
        stage.show();

        initAudioLine();
        startVideoReceiver();
        startAudioReceiver();

        new Thread(this::playoutLoop).start();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void initAudioLine() {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 2, true, false); // little-endian
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            speakers = (SourceDataLine) AudioSystem.getLine(info);
            speakers.open(format);
            speakers.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startVideoReceiver() {
        new Thread(() -> {
            try (Socket socket = new Socket(HOST, VIDEO_PORT);
                 DataInputStream dis = new DataInputStream(socket.getInputStream())) {
                while (true) {
                    long ts = dis.readLong();
                    int length = dis.readInt();
                    byte[] data = new byte[length];
                    dis.readFully(data);
                    BufferedImage bufImg = ImageIO.read(new ByteArrayInputStream(data));
                    if (bufImg != null) {
                        videoQueue.put(new VideoFrame(ts, bufImg));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startAudioReceiver() {
        new Thread(() -> {
            try (Socket socket = new Socket(HOST, AUDIO_PORT);
                 DataInputStream dis = new DataInputStream(socket.getInputStream())) {

                while (true) {
                    long ts = dis.readLong();
                    int length = dis.readInt();
                    byte[] buffer = new byte[length];
                    dis.readFully(buffer);
                    audioQueue.put(new AudioChunk(ts, buffer, length));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void playoutLoop() {
        try {
            while (true) {
                long now = System.nanoTime();
                VideoFrame vf = videoQueue.peek();
                if (vf != null && vf.timestamp <= now) {
                    videoQueue.poll();
                    Image fxImage = convertToFxImage(vf.bufferedImage);
                    Platform.runLater(() -> imageView.setImage(fxImage));
                }
                AudioChunk ac = audioQueue.peek();
                if (ac != null && ac.timestamp <= now) {
                    audioQueue.poll();
                    speakers.write(ac.data, 0, ac.length);
                }
                TimeUnit.MILLISECONDS.sleep(5);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Image convertToFxImage(BufferedImage bufferedImage) {
        WritableImage writableImage = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
        PixelWriter pw = writableImage.getPixelWriter();
        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                pw.setArgb(x, y, bufferedImage.getRGB(x, y));
            }
        }
        return writableImage;
    }

    private static class VideoFrame implements Comparable<VideoFrame> {
        final long timestamp;
        final BufferedImage bufferedImage;
        VideoFrame(long ts, BufferedImage img) {
            this.timestamp = ts;
            this.bufferedImage = img;
        }
        @Override public int compareTo(VideoFrame o) {
            return Long.compare(this.timestamp, o.timestamp);
        }
    }

    private static class AudioChunk implements Comparable<AudioChunk> {
        final long timestamp;
        final byte[] data;
        final int length;
        AudioChunk(long ts, byte[] data, int len) {
            this.timestamp = ts;
            this.data = data;
            this.length = len;
        }
        @Override public int compareTo(AudioChunk o) {
            return Long.compare(this.timestamp, o.timestamp);
        }
    }
}
