package file;

import java.net.ServerSocket;
import java.net.Socket;

public class Client
{
    public static void main(String[] args)
    {
        try
        {
            Socket socket = new Socket("localhost", 7000);
            System.out.println("Connection succeeded.");

            FileReceiveThread fileReceiveThread = new FileReceiveThread(socket);

            fileReceiveThread.start();
            fileReceiveThread.join();

            socket.close();
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
