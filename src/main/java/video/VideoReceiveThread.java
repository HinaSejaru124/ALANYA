package video;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import audio.AudioReceiveThread;

public class VideoReceiveThread extends Thread {

    private final Socket audioSocket;
    private final Socket videoSocket;
    private final VideoSetup videoSetup;

    public VideoReceiveThread(Socket audioSocket, Socket videoSocket, VideoSetup videoSetup) {
        this.audioSocket = audioSocket;
        this.videoSocket = videoSocket;
        this.videoSetup = videoSetup;
    }

    @Override
    public void run() {
        try {
            InputStream videoIn = videoSocket.getInputStream();

            while (true) {
                new AudioReceiveThread(audioSocket, videoSetup).start();

                new Thread(() -> {
                }).start();
            }
        } catch (IOException e) {
            System.out.println("Erreur dans la réception des packets audio: " + e.getMessage());
        }
    }
}
