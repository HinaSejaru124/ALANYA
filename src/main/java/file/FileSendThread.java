package file;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

public class FileSendThread extends Thread
{
    private Socket socket ; //socket sur lequel on écrira
    private String filePath ; //chemin d'accès
    private boolean sendFileFlag ; // booleen pour savoir si on doit envoyer un fichier ou pas

    public FileSendThread(Socket socket)
    {
        this.socket = socket;
    }

    @Override
    public void run()
    {
        if(sendFileFlag)
        {
            sendFile(filePath) ; // envoie du fichier
            sendFileFlag = false ;
        }
    }

    public void send(String filePath) //Fontion qui appelle la fonction d'envoi du fichier et met l'envoie du fichier à true
    {
        this.filePath = filePath ;
        if (!((filePath == null) || (filePath.isEmpty()))){
            sendFileFlag = true ;
        }
    }

    public void sendFile(String filePath)
    {
        try
        {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            FileInputStream fis = new FileInputStream(filePath);

            File file = new File(filePath);
            String fullFileName = file.getName() ; // nom complet du fichier
            long fileSize= file.length() ;

            // Envoyer le nom du fichier
            dos.writeUTF(fullFileName);

            // Envoyer la taille du fichier
            dos.writeLong(fileSize);

            // Envoyer le contenu du fichier
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1)
            {
                dos.write(buffer, 0, bytesRead);
            }

            System.out.println(file.getName() + " envoyé avec succès au serveur !");
            fis.close();
        }
        catch (Exception e)
        {
            System.out.println("Erreur lors de l'envoi du fichier: " + e.getMessage());
        }

    }
}