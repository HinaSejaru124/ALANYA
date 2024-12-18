package alanya;

import java.io.IOException;
import java.net.ServerSocket;

import javafx.stage.Stage;

public class Server extends ChatApp {
    private static final int PORT = 7000;   

    @SuppressWarnings("exports")
    @Override
    public void start(Stage primaryStage) {
        try (ServerSocket serverSocket = new ServerSocket(PORT))
        {
            super.start(primaryStage);
            // VBox root = new VBox(new Label("En attente de connexion..."));
            // root.setSpacing(10);
            // root.setStyle("-fx-alignment: center;");
    
            // PageManager.showPage(root);
            // primaryStage.show();

            this.user = serverSocket.accept();
        } catch (IOException e) {
            showError("Impossible de lancer le serveur: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
