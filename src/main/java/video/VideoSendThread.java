package video;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.imageio.ImageIO;

import audio.AudioSendThread;

public final class VideoSendThread extends Thread {

    private final Socket audiosocket;
    private final Socket videosocket;
    private final VideoSetup videoSetup;
    public int count = 0;

    public VideoSendThread(Socket audioSocket, Socket videoSocket, VideoSetup videoSetup) {
        this.audiosocket = audioSocket;
        this.videosocket = videoSocket;
        this.videoSetup = videoSetup;
    }

    @Override
    public void run() {
        try {
            OutputStream videOut = videosocket.getOutputStream();

            while (true) {
                new AudioSendThread(audiosocket, videoSetup).start();

                new Thread(() -> {
                    try {
                        BufferedImage bufferedImage = videoSetup.webcam.getImage();
                        if (bufferedImage != null) {
                            ImageIO.write(bufferedImage, "JPG", videOut);
                            videOut.flush();
                        }
                    } catch (IOException e) {
                        System.out.println("Erreur dans l'envoi des packets video: " + e.getMessage());
                    }
                }).start();
            }
        } catch (IOException ex) {
        }
    }
}