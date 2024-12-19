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
            this.user = serverSocket.accept();
            this.username = "Serveur";
            super.start(primaryStage);
        } catch (IOException e) {
            showError("Impossible de lancer le serveur: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
