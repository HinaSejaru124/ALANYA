package alanya;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class ServerTask implements Runnable {
    private final Label statusLabel;

    public ServerTask(Label statusLabel) {
        this.statusLabel = statusLabel;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(7000)) {
            updateLabel("En attente de connexion sur le port 12345...");

            // Attente d'une connexion (tâche bloquante)
            Socket clientSocket = serverSocket.accept();

            // Mise à jour de l'interface utilisateur lorsque la connexion est établie
            updateLabel("Connexion acceptée depuis : " + clientSocket.getInetAddress());
        } catch (IOException e) {
            updateLabel("Erreur du serveur : " + e.getMessage());
        }
    }

    private void updateLabel(String message) {
        // Mise à jour du label dans le thread JavaFX
        Platform.runLater(() -> statusLabel.setText(message));
    }
}

