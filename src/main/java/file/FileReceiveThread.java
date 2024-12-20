package file;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import alanya.ChatApp;
import javafx.application.Platform;

public class FileReceiveThread extends Thread
{
    private final Socket socket;
    private final ChatApp chatApp;
    
    public static boolean receiving = false;

    public FileReceiveThread(Socket socket, ChatApp chatApp)
    {
        this.socket = socket;
        this.chatApp = chatApp;
    }

    @Override
    public void run()
    {
        try
        {
            receiving = true;
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            while (true) // Toujours à l'écoute
            {
                // Lire les métadonnées
                String fullFileName = dis.readUTF();
                long fileSize = dis.readLong();

                // Préparer le répertoire et le fichier
                String userHomePath = System.getProperty("user.home");
                File directory = new File(userHomePath, "ALANYA_receivedFiles");
                directory.mkdir();
                File file = new File(directory, fullFileName);
                file.createNewFile();

                // Recevoir le fichier
                try (FileOutputStream fos = new FileOutputStream(file))
                {
                    byte[] buffer = new byte[4096];
                    long receivedDataSize = 0;
                    int bytesRead;

                    while ((bytesRead = dis.read(buffer)) != -1)
                    {
                        fos.write(buffer, 0, bytesRead);
                        receivedDataSize += bytesRead;
                        if (receivedDataSize >= fileSize)
                            break;
                    }
                }
                System.out.println("Fichier reçu : " + fullFileName);
                Platform.runLater(() -> this.chatApp.addFile("/icons/document-signed.png", fullFileName, false));
            }
        } catch (IOException e)
        {
            System.err.println("Erreur dans la réception : " + e.getMessage());
        }
        finally
        {
            receiving = false;
        }
    }
}
