package video;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class WebcamFXServer {
    private static final int VIDEO_PORT = 2000;
    private static final int AUDIO_PORT = 2001;

    public static void main(String[] args) throws Exception {
        new WebcamFXServer().start();
    }

    public void start() {
        // Thread vidéo
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(VIDEO_PORT);
                 Socket socket = serverSocket.accept();
                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

                // Libération de la webcam
                com.github.sarxos.webcam.Webcam webcam = com.github.sarxos.webcam.Webcam.getDefault();
                webcam.setViewSize(com.github.sarxos.webcam.WebcamResolution.VGA.getSize());
                webcam.open();

                while (true) {
                    long ts = System.nanoTime();
                    BufferedImage img = webcam.getImage();
                    if (img != null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(img, "JPG", baos);
                        byte[] data = baos.toByteArray();

                        dos.writeLong(ts);               // Timestamp
                        dos.writeInt(data.length);      // Longueur
                        dos.write(data);                // Image
                        dos.flush();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // Thread audio
        new Thread(() -> {
            AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            try (ServerSocket serverSocket = new ServerSocket(AUDIO_PORT);
                 Socket socket = serverSocket.accept();
                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

                TargetDataLine mic = (TargetDataLine) AudioSystem.getLine(info);
                mic.open(format);
                mic.start();

                byte[] buffer = new byte[4096];
                while (true) {
                    int count = mic.read(buffer, 0, buffer.length);
                    if (count > 0) {
                        long ts = System.nanoTime();
                        dos.writeLong(ts);           // Timestamp
                        dos.writeInt(count);         // Nombre d'octets
                        dos.write(buffer, 0, count);
                        dos.flush();
                    }
                }
            } catch (LineUnavailableException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
