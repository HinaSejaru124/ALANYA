package video;

import java.io.IOException;
import java.net.Socket;

import javax.sound.sampled.LineUnavailableException;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

import alanya.ChatApp;
import audio.AudioSetup;

public final class VideoSetup extends AudioSetup {

    public final Webcam webcam;

    public VideoSetup(Socket socket, ChatApp chatApp) throws LineUnavailableException {
        super(socket, chatApp);

        // Initialisation de la webcam
        webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());
    }

    @Override
    public void run() {
        new Thread(() -> {
            while (true) {
                if (onCall) {
                    try {
                        webcam.open();
                        microphone.open(format);
                        microphone.start();

                        if (!chatApp.onCall) {
                            chatApp.onCall = true;
                            // chatApp.startCall();
                            // chatApp.addAudioCallBubble(false, this);
                        }

                    } catch (LineUnavailableException e) {
                        System.out.println("Impossible d'établir la communication" + e.getMessage());
                    }
                } else {
                    microphone.stop();
                    microphone.close();
                    webcam.close();

                    if (chatApp.onCall) {
                        chatApp.onCall = false;
                        // chatApp.endCall();
                    }
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
