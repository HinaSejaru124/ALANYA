package video;

import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;

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
                    if (bufferedImage != null) {
                        ImageIO.write(bufferedImage, "JPG", dos);
                        dos.flush();
                    }
                }
            } catch (IOException ex) {
            }

        }).start();
    }

    public static void main(String[] args) {
        new WebcamFXServer().start();
        System.out.println("Server on");
    }
}
