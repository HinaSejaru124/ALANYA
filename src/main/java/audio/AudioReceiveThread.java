package audio;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import javax.sound.sampled.SourceDataLine;

public class AudioReceiveThread extends Thread
{
    private final Socket socket;
    private final SourceDataLine speakers;

    public AudioReceiveThread(Socket socket, SourceDataLine speakers)
    {
        this.socket = socket;
        this.speakers = speakers;
    }

    public void receiveAudio(Boolean isRunning) {
        try {
            byte[] buffer = new byte[1024];
            InputStream in = socket.getInputStream();

            while (isRunning) {
                int count = in.read(buffer, 0, buffer.length);
                if (count > 0)
                    speakers.write(buffer, 0, count);
            }
        } catch (IOException e) {
            System.out.println("Erreur dans la réception des packets audio: " + e.getMessage());
        }
    }
}
