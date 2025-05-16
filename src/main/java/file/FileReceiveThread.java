package file;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.stream.Stream;

import javafx.stage.DirectoryChooser;

public final class FileReceiveThread {
    private final Socket socket;

    public FileReceiveThread(Socket socket) {
        this.socket = socket;
    }

    public File receive() throws IOException {
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        String fullFileName = dis.readUTF();
        long fileSize = dis.readLong();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Enregistrer le fichier reçu");

        File selectedDirectory;
        if (isImage(fullFileName))
            selectedDirectory = new File(System.getProperty("user.home"), "Alanya/Alanya Images");
        else if (isVideo(fullFileName))
            selectedDirectory = new File(System.getProperty("user.home"), "Alanya/Alanya Videos");
        else if (isAudio(fullFileName))
            selectedDirectory = new File(System.getProperty("user.home"), "Alanya/Alanya Audios");
        else
            selectedDirectory = new File(System.getProperty("user.home"), "Alanya/Alanya Documents");

        File file = new File(selectedDirectory, fullFileName);
        file.createNewFile();

        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            long receivedDataSize = 0;
            int bytesRead;

            while ((bytesRead = dis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
                receivedDataSize += bytesRead;
                if (receivedDataSize >= fileSize)
                    break;
            }
        }

        return file;
    }

    private boolean isImage(String name) {
        return Stream.of(".jpg", ".jpeg", ".png").anyMatch(name::endsWith);
    }

    private boolean isVideo(String name) {
        return Stream.of(".mp4", ".mov", ".avi", ".gif").anyMatch(name::endsWith);
    }

    private boolean isAudio(String name) {
        return Stream.of(".mp3", ".m4a").anyMatch(name::endsWith);
    }
}
