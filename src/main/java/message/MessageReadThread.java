package message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MessageReadThread extends Thread
{
    private final Socket socket;

    public MessageReadThread(Socket socket)
    {
        this.socket = socket;
    }

    public String receive()
    {
        String message = "";
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String incoming;
            while ((incoming = reader.readLine()) != null)
                message += incoming;
  
        } catch (IOException e)
        {
            System.out.println("Lecture imposssible: " + e.getMessage());
        }
        return message;
    }
}