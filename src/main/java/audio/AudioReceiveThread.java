package audio;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class AudioReceiveThread extends Thread {

    private final Socket socket;
    private final AudioSetup audioSetup;

    public AudioReceiveThread(Socket socket, AudioSetup audioSetup) {
        this.socket = socket;
        this.audioSetup = audioSetup;
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[8192];
            InputStream in = socket.getInputStream();

            while (true) {
                int count = in.read(buffer, 0, buffer.length);
                if (count > 0) {
                    audioSetup.getSpeakers().write(buffer, 0, count);
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur dans la réception des packets audio: " + e.getMessage());
        }
    }
}
