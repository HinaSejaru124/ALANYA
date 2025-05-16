package audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import views.Util;

public class AudioSetup extends Thread {

    private AudioFormat format;
    private TargetDataLine microphone;
    private SourceDataLine speakers;

    public AudioSetup() {
        try {
            format = new AudioFormat(16000, 16, 2, true, true);
            DataLine.Info microphoneInfo = new DataLine.Info(TargetDataLine.class, format);
            DataLine.Info speakersInfo = new DataLine.Info(SourceDataLine.class, format);

            microphone = (TargetDataLine) AudioSystem.getLine(microphoneInfo);
            speakers = (SourceDataLine) AudioSystem.getLine(speakersInfo);
        } catch (LineUnavailableException ex) {
        }
    }

    public TargetDataLine getMicrophone() {
        return microphone;
    }

    public SourceDataLine getSpeakers() {
        return speakers;
    }

    public void openMicrophone() {
        try {
            microphone.open(format);
            microphone.start();
        } catch (LineUnavailableException e) {
            Util.showError("Impossible d'ouvrir le microphone");
        }
    }

    public void openSpeakers() {
        try {
            speakers.open(format);
            speakers.start();
        } catch (LineUnavailableException e) {
            Util.showError("Impossible d'ouvrir le microphone");
        }
    }
}
