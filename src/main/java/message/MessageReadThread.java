package message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public final class MessageReadThread {

    private final Socket socket;

    public MessageReadThread(Socket socket) {
        this.socket = socket;
    }

    public String receive() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return reader.readLine();
    }
}
