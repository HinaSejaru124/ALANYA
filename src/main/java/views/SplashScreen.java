package views;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class SplashScreen extends Application implements ViewsMethods {

    StackPane root;

    @Override
    public void start(Stage splashStage) {
        ImageView logo = loadImage("/images/Logo1-2.png"); // Mets ici le bon chemin
        logo.setFitWidth(200);
        logo.setPreserveRatio(true);

        root = new StackPane(logo);

        // Attente de 3 secondes avant de démarrer l'application principale
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {}
            javafx.application.Platform.runLater(() -> {
                splashStage.close();
                new MainApp().start(new Stage());
            });
        }).start();
    }

    public Parent getRoot() {
        return  root;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
