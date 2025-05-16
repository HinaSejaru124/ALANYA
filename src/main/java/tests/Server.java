
package tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Server {
    private static final int PORT = 5001;
    private static final int MAX_THREADS = 10;
    private static final String AUDIO_DIRECTORY = "audio_messages";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private static ExecutorService threadPool = Executors.newFixedThreadPool(MAX_THREADS);

    public static void main(String[] args) {
        createAudioDirectory();
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur vocal démarré sur le port " + PORT);
            System.out.println("En attente de connexions clients...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nouvelle connexion : " + socket.getInetAddress());
                threadPool.execute(new ClientHandler(socket));
            }
        } catch (IOException e) {
            System.err.println("Erreur du serveur : " + e.getMessage());
        } finally {
            threadPool.shutdown();
        }
    }

    private static void createAudioDirectory() {
        File directory = new File(AUDIO_DIRECTORY);
        if (!directory.exists()) {
            if (directory.mkdir()) {
                System.out.println("Répertoire audio créé : " + directory.getAbsolutePath());
            } else {
                System.err.println("Impossible de créer le répertoire audio");
            }
        }
    }

    private static String generateFileName() {
        return AUDIO_DIRECTORY + "/message_" + DATE_FORMAT.format(new Date()) + ".wav";
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            String fileName = generateFileName();
            
            try (InputStream inputStream = socket.getInputStream();
                 FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {

                System.out.println("Réception du message vocal : " + fileName);
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }

                System.out.println("Message vocal sauvegardé : " + fileName);
                playAudioFile(fileName);

            } catch (IOException e) {
                System.err.println("Erreur avec le client " + socket.getInetAddress() + ": " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Erreur lors de la fermeture du socket: " + e.getMessage());
                }
            }
        }

        private void playAudioFile(String filePath) {
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filePath));
                AudioFormat format = audioStream.getFormat();
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info);

                audioLine.open(format);
                audioLine.start();

                System.out.println("Lecture du message vocal...");
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = audioStream.read(buffer)) != -1) {
                    audioLine.write(buffer, 0, bytesRead);
                }

                audioLine.drain();
                audioLine.close();
                audioStream.close();

                System.out.println("Lecture terminée.");

            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.err.println("Erreur lors de la lecture du fichier audio: " + e.getMessage());
            }
        }
    }
}