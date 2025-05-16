package video;

import java.awt.image.BufferedImage;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class WebcamFX extends Application {
    private Webcam webcam;
    private ImageView imageView;

    @Override
    public void start(Stage stage) {
        // Initialisation de la webcam
        webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcam.open();

        imageView = new ImageView();
        StackPane root = new StackPane(imageView);
        Scene scene = new Scene(root, 640, 480);

        // Démarrer un thread pour capturer les images en continu
        new Thread(() -> {
            while (true) {
                BufferedImage bufferedImage = webcam.getImage();
                if (bufferedImage != null) {
                    Image fxImage = convertToFxImage(bufferedImage);
                    Platform.runLater(() -> imageView.setImage(fxImage));
                }
            }
        }).start();

        stage.setScene(scene);
        stage.setTitle("Webcam avec JavaFX");
        stage.setOnCloseRequest(e -> webcam.close()); // Fermer la webcam lors de la fermeture
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
