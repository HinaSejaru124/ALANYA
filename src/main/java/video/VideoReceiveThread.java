package video;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import javax.imageio.ImageIO;

import alanya.ChatApp;
import audio.AudioReceiveThread;

public class VideoReceiveThread extends Thread {

    private final Socket audioSocket;
    private final Socket videoSocket;
    private final VideoSetup videoSetup;
    private final ChatApp chatApp;

    public VideoReceiveThread(Socket audioSocket, Socket videoSocket, VideoSetup videoSetup, ChatApp chatApp) {
        this.audioSocket = audioSocket;
        this.videoSocket = videoSocket;
        this.videoSetup = videoSetup;
        this.chatApp = chatApp;
    }

    @Override
    public void run() {
        try {
            InputStream videoIn = videoSocket.getInputStream();

            while (true) {
                new AudioReceiveThread(audioSocket, videoSetup).start();

                new Thread(() -> {
                    if (videoSetup.onCall) {
                        try {
                            chatApp.image = ImageIO.read(videoIn);
                        } catch (IOException ex) {
                            System.out.println("Erreur dans la réception des packets video: " + ex.getMessage());
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            System.out.println("Erreur dans la réception des packets audio: " + e.getMessage());
            // audioSetup.onCall = false;
        }
    }
}
