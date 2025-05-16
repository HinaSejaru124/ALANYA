package audio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class AudioSetup extends Thread {

    protected static AudioFormat format;
    public TargetDataLine microphone;
    public SourceDataLine speakers;
    protected BufferedReader reader;
    public volatile Boolean onCall = false;

    public AudioSetup(Socket socket) throws LineUnavailableException {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


        format = new AudioFormat(16000, 16, 2, true, true);
        DataLine.Info microphoneInfo = new DataLine.Info(TargetDataLine.class, format);
        DataLine.Info speakersInfo = new DataLine.Info(SourceDataLine.class, format);

        microphone = (TargetDataLine) AudioSystem.getLine(microphoneInfo);
        speakers = (SourceDataLine) AudioSystem.getLine(speakersInfo);

        speakers.open(format);
        speakers.start();
    }

    @Override
    public void run() {
        new Thread(() -> {
            while (true) {
                if (onCall) {
                    try {
                        microphone.open(format);
                        microphone.start();

                    } catch (LineUnavailableException e) {
                        System.out.println("Impossible d'établir la communication" + e.getMessage());
                    }
                } else {
                    microphone.stop();
                    microphone.close();
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                try {
                    onCall = Boolean.valueOf(reader.readLine());
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }).start();
    }
}
