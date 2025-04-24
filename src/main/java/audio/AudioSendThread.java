package audio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class AudioSendThread extends Thread {

    private final Socket socket;
    private final AudioSetup audioSetup;
    public int count = 0;

    public AudioSendThread(Socket socket, AudioSetup audioSetup) {
        this.socket = socket;
        this.audioSetup = audioSetup;
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[8192];
            OutputStream out = socket.getOutputStream();

            while (true) {
                if (audioSetup.onCall)
                {
                    count = audioSetup.microphone.read(buffer, 0, buffer.length);
                    if (count > 0) {
                        out.write(buffer, 0, count);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur dans l'envoi des packets audio: " + e.getMessage());
        }
    }
}
