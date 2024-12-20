package message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import alanya.ChatApp;
import javafx.application.Platform;

public class MessageReadThread extends Thread {

    private final Socket socket;
    private final ChatApp chatApp;

    public MessageReadThread(Socket socket, ChatApp chatApp) {
        this.socket = socket;
        this.chatApp = chatApp;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message;
            while ((message = reader.readLine()) != null) {
                String finalMessage = message;
                Platform.runLater(() -> this.chatApp.addMessage(finalMessage, false));
            }
        } catch (IOException e) {
            System.out.println("Lecture impossible: " + e.getMessage());
        }
    }

}
