package video;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class WebcamFXClient extends Application {
    private ImageView imageView;
    private final String host = "192.168.1.117";

    @Override
    public void start(Stage stage) {
        new Thread(() -> {
            try (Socket socket = new Socket(host, 2000)) {
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                while (true) {
                    int length = dis.readInt(); // lit la longueur
                    byte[] data = new byte[length];
                    dis.readFully(data); // lit exactement length octets
                    ByteArrayInputStream bais = new ByteArrayInputStream(data);
                    BufferedImage bufferedImage = ImageIO.read(bais);
                    if (bufferedImage != null) {
                        Image fxImage = convertToFxImage(bufferedImage);
                        Platform.runLater(() -> imageView.setImage(fxImage));
                    }
                }
            } catch (IOException e) {
            }
        }).start();

        new Thread(() -> {
            try {// CLIENT – à mettre dans le même thread UDP, avant et pendant la receive-loop
                DatagramSocket udp = new DatagramSocket();
                InetAddress serverAddr = InetAddress.getByName(host);
                byte[] ping = new byte[] { 0 };

                ScheduledExecutorService ka = Executors.newSingleThreadScheduledExecutor();
                ka.scheduleAtFixedRate(() -> {
                    try {
                        udp.send(new DatagramPacket(ping, ping.length, serverAddr, 2001));
                    } catch (IOException ignored) {
                    }
                }, 0, 5, TimeUnit.SECONDS);

                // Puis votre boucle udp.receive(…)

                // 3) on initialise la ligne audio
                AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine speakers = (SourceDataLine) AudioSystem.getLine(info);
                speakers.open(format);
                speakers.start();

                // 4) boucle de réception/réception audio
                byte[] buf = new byte[4096 + Long.BYTES + Integer.BYTES];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                while (true) {
                    udp.receive(packet);
                    ByteBuffer bb = ByteBuffer.wrap(packet.getData(), 0, packet.getLength())
                            .order(ByteOrder.BIG_ENDIAN);

                    long ts = bb.getLong();
                    int len = bb.getInt();
                    byte[] pcm = new byte[len];
                    bb.get(pcm);

                    speakers.write(pcm, 0, len);
                }

            } catch (LineUnavailableException | IOException ex) {
                ex.printStackTrace();
            }
        }).start();

        imageView = new ImageView();
        StackPane root = new StackPane(imageView);
        Scene scene = new Scene(root, 640, 480);

        stage.setScene(scene);
        stage.setTitle("Webcam avec JavaFX");
        stage.show();
    }

    private Image convertToFxImage(BufferedImage bufferedImage) {
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    public static void main(String[] args) {
        launch(args);
    }
}