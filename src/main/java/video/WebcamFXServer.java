package video;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::sendVideo, 30, 30, TimeUnit.MILLISECONDS);
        
        new Thread(() -> {
            final int AUDIO_PORT = 2001;
            try (DatagramSocket udpSocket = new DatagramSocket(AUDIO_PORT)) {
                byte[] pingBuf = new byte[1];
                DatagramPacket pingPacket = new DatagramPacket(pingBuf, pingBuf.length);
                udpSocket.receive(pingPacket);
                InetAddress clientAddr = pingPacket.getAddress();
                int clientPort = pingPacket.getPort();

                AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                TargetDataLine mic = (TargetDataLine) AudioSystem.getLine(info);
                mic.open(format);
                mic.start();

                byte[] audioBuf = new byte[4096];
                while (true) {
                    int count = mic.read(audioBuf, 0, audioBuf.length);
                    if (count > 0) {
                        ByteBuffer bb = ByteBuffer.allocate(Long.BYTES + Integer.BYTES + count)
                                .order(ByteOrder.BIG_ENDIAN);
                        bb.putLong(System.nanoTime());
                        bb.putInt(count);
                        bb.put(audioBuf, 0, count);
                        byte[] packetData = bb.array();

                        DatagramPacket packet = new DatagramPacket(
                                packetData, packetData.length, clientAddr, clientPort);
                        udpSocket.send(packet);
                    }
                }
            } catch (IOException | LineUnavailableException e) {}
        }).start();
    }

    public void sendVideo() {
        try (ServerSocket serverSocket = new ServerSocket(2000)) {
            Socket socket = serverSocket.accept();
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            while (true) {
                BufferedImage bufferedImage = webcam.getImage();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                if (bufferedImage != null) {
                    ImageIO.write(bufferedImage, "png", baos);

                    byte[] jpegData = baos.toByteArray();
                    dos.writeInt(jpegData.length);
                    dos.write(jpegData);
                    dos.flush();
                }
            }
        } catch (IOException ex) {
        }
    }

    public static void main(String[] args) {
        new WebcamFXServer().start();
    }
}