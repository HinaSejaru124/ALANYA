package file;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public final class FileSendThread
{
    private final Socket socket;

    public FileSendThread(Socket socket)
    {
        this.socket = socket;
    }

    public void send(String filePath)
    {
        if (!(filePath == null || filePath.isEmpty()))
            sendFile(filePath);
    }

    public void sendFile(String filePath)
    {
        try
        {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            File file = new File(filePath);
    
            String fullFileName = file.getName();
            long fileSize = file.length();
    
            // Envoyer les métadonnées
            dos.writeUTF(fullFileName);
            dos.writeLong(fileSize);
    
            // Envoyer le contenu du fichier
            try (FileInputStream fis = new FileInputStream(file))
            {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1)
                    dos.write(buffer, 0, bytesRead);
            }
            dos.flush(); // Forcer l'envoi des données
        } catch (IOException e)
        {
            System.err.println("Erreur lors de l'envoi du fichier : " + e.getMessage());
        }
    }    
}
