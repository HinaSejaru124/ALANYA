package alanya;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox();
        root.setSpacing(10);
        root.setStyle("-fx-alignment: center;");

        Label statusLabel = new Label("En attente de connexion...");

        // Lancer le serveur dans un thread séparé
        ServerTask serverTask = new ServerTask(statusLabel);
        Thread serverThread = new Thread(serverTask);
        serverThread.setDaemon(true); // Assurez-vous que le thread se termine avec l'application
        serverThread.start();

        root.getChildren().add(statusLabel);

        Scene scene = new Scene(root, 400, 200);
        primaryStage.setTitle("Serveur JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
