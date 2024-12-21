package audio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.sound.sampled.TargetDataLine;

public class AudioSendThread extends Thread
{
    private final Socket socket;
    private final TargetDataLine microphone;

    public AudioSendThread(Socket socket, TargetDataLine microphone)
    {
        this.socket = socket;
        this.microphone = microphone;
    }

    public void sendAudio(Boolean isRunning)
    {
        try {
            byte[] buffer = new byte[1024];
            OutputStream out = socket.getOutputStream();

            while (isRunning)
            {
                int count = microphone.read(buffer, 0, buffer.length);
                if (count > 0)
                    out.write(buffer, 0, count);
            }
        } catch (IOException e)
        {
            System.out.println("Erreur lors de l'envoi des packets audio: " + e.getMessage());
        }
    }
}
