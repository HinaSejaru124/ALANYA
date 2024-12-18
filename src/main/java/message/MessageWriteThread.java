package message;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MessageWriteThread extends Thread
{

    private final Socket socket;

    public MessageWriteThread(Socket socket)
    {
        this.socket = socket;
    }

    public void send(String message)
    {
        try
        {
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            writer.println(message);

        } catch (IOException e)
        {
            System.out.println("Écriture impossible: " + e.getMessage());
        }
    }
}
