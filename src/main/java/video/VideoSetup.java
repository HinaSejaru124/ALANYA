package video;

import javax.sound.sampled.LineUnavailableException;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

import audio.AudioSetup;

public final class VideoSetup extends AudioSetup {

    public final Webcam webcam;

    public VideoSetup() throws LineUnavailableException {
        super();

        // Initialisation de la webcam
        webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());
    }
}
