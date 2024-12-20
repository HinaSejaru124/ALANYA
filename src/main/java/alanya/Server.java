package alanya;

import java.io.IOException;
import java.net.ServerSocket;

import javafx.stage.Stage;

public class Server extends ChatApp {  

    @SuppressWarnings("exports")
    @Override
    public void start(Stage primaryStage) {
        try (ServerSocket messageServerSocket = new ServerSocket(MESSAGE_PORT);
            ServerSocket filServerSocket = new ServerSocket(FILE_PORT))
        {
            this.messageSocket = messageServerSocket.accept();
            this.fileSocket = filServerSocket.accept();
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
