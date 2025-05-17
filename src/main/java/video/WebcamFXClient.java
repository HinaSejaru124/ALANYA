package video;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
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
    private ImageView imageView;

    @Override
    public void start(Stage stage) {
        new Thread(() -> {
            try (Socket socket = new Socket("localhost", 2000)) {
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
            try (Socket socket = new Socket("localhost", 2001)) {
                AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
                DataLine.Info speakersInfo = new DataLine.Info(SourceDataLine.class, format);

                SourceDataLine speakers = (SourceDataLine) AudioSystem.getLine(speakersInfo);
                speakers.open(format);
                speakers.start();

                byte[] buffer = new byte[8192];
                InputStream in = socket.getInputStream();

                while (true) {
                    int count = in.read(buffer, 0, buffer.length);
                    if (count > 0) {
                        speakers.write(buffer, 0, count);
                    }
                }
            } catch (LineUnavailableException ex) {
            } catch (IOException ex) {
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
        WritableImage writableImage = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
        PixelWriter pw = writableImage.getPixelWriter();

        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                pw.setArgb(x, y, bufferedImage.getRGB(x, y));
            }
        }
        return writableImage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}