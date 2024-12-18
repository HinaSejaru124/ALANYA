package file;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.Socket;

/*
* Le principe est que le thread tourne sans rien faire (car on ne redéfini pas la méthode run())
* puis on crée une fonction receive() qui sera exécutée par ce thread lorsque le frontend
* fera appel à ce thread
*/
public class FileReceiveThread extends Thread
{
    private Socket socket;

    public FileReceiveThread(Socket socket)
    {
        this.socket = socket;
    }

    @Override
    public void run()
    {
        try
        {
            // get the DataInputStream linked to the socket
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            // get the name of the file which is sent : file with his extension
            String fullFileName = dis.readUTF();

            // get the user's home path
            String userHomePath = System.getProperty("user.home");

            // The name of the directory in with received files will be located
            String directoryName = "ALANYA_receivedFiles";

            // create the directory in the receiver's computer
            File directory = new File(userHomePath, directoryName);
            directory.mkdir();

            // create the file in the previous directory
            File file = new File(directory.getAbsolutePath(), fullFileName);
            file.createNewFile();

            // create the FileOutputStream to the yet created file
            FileOutputStream fos = new FileOutputStream(file);

            // get the length of received data
            long fileSize = dis.readLong();

            // reception of data
            long yetReceivedDataSize = 0;
            byte[] buffer = new byte[8192];
            int dataRead;

            while (yetReceivedDataSize < fileSize && (dataRead = dis.read(buffer)) != -1)
            {
                fos.write(buffer, 0, dataRead);
                yetReceivedDataSize += dataRead;
            }
            System.out.println("Reception of the file " + fullFileName + " succeeded");
            fos.close();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

}