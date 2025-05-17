package video;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

public class WebcamFXServer {
    private Webcam webcam;

    public void start() {
        webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcam.open();

        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(2000)) {
                Socket socket = serverSocket.accept();
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                while (true) {
                    BufferedImage bufferedImage = webcam.getImage();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    if (bufferedImage != null) {
                        ImageIO.write(bufferedImage, "JPG", baos);

                        byte[] jpegData = baos.toByteArray();
                        dos.writeInt(jpegData.length); // 4 octets de longueur
                        dos.write(jpegData); // les octets de l’image
                        dos.flush();
                    }
                }
            } catch (IOException ex) {
            }

        }).start();

        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(2001)) {
                AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
                DataLine.Info microphoneInfo = new DataLine.Info(TargetDataLine.class, format);

                TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(microphoneInfo);
                microphone.open(format);
                microphone.start();

                Socket socket = serverSocket.accept();

                byte[] buffer = new byte[8192];
                OutputStream out = socket.getOutputStream();

                while (true) {
                    int count = microphone.read(buffer, 0, buffer.length);
                    if (count > 0) {
                        out.write(buffer, 0, count);
                    }
                }
            } catch (LineUnavailableException ex) {
            } catch (IOException ex) {
            }
        }).start();
    }

    public static void main(String[] args) {
        new WebcamFXServer().start();
    }
}