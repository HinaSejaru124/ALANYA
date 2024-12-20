package message;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MessageWriteThread extends Thread
{
    private final PrintWriter writer;

    public MessageWriteThread(Socket socket) throws IOException
    {
        this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
    }
    
    public void send(String message)
    {
        if (message != null)
            writer.println(message);
    }    
}
