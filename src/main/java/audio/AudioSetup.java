package audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class AudioSetup {

    public static AudioFormat format;
    public TargetDataLine microphone;
    public SourceDataLine speakers;

    public AudioSetup() {
        try {
            format = new AudioFormat(44100.0f, 16, 1, true, true);
            DataLine.Info microphoneInfo = new DataLine.Info(TargetDataLine.class, format);
            DataLine.Info speakersInfo = new DataLine.Info(SourceDataLine.class, format);

            microphone = (TargetDataLine) AudioSystem.getLine(microphoneInfo);
            speakers = (SourceDataLine) AudioSystem.getLine(speakersInfo);
        } catch (LineUnavailableException error) {
            System.out.println(error.getMessage());
        }
    }
}
