package file;

import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    public static void main(String[] args)
    {
        try
        {
            ServerSocket serverSocket = new ServerSocket(7000);
            System.out.println("Waiting for a connexion...");
            Socket socket = serverSocket.accept();
            System.out.println("Connected to " + socket.getInetAddress());

            FileSendThread fileSendThread = new FileSendThread(socket);

            fileSendThread.send("/media/djahappi/Nouveau nom/Informatique/Learning ressources/Livres/Theories des codes.pdf");

            fileSendThread.run();

            fileSendThread.join();

            socket.close();
            serverSocket.close();
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
