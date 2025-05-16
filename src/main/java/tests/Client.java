package tests;


import java.awt.FlowLayout;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 5001;
    private static final String FILE_NAME = "message_audio.wav";
    
    private static TargetDataLine microphone;
    private static ByteArrayOutputStream byteArrayOutputStream;
    private static boolean isRecording = false;
    private static JButton recordButton;
    private static JLabel statusLabel;

    public static void main(String[] args) {
        createGUI();
    }

    private static void createGUI() {
        JFrame frame = new JFrame("Client Vocal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 150);
        frame.setLayout(new FlowLayout());

        recordButton = new JButton("Démarrer l'enregistrement");
        statusLabel = new JLabel("Prêt");

        recordButton.addActionListener(e -> {
            if (!isRecording) {
                startRecording();
                recordButton.setText("Arrêter et envoyer");
                statusLabel.setText("Enregistrement en cours...");
            } else {
                stopRecordingAndSend();
                recordButton.setText("Démarrer l'enregistrement");
                statusLabel.setText("Message envoyé!");
            }
        });

        frame.add(recordButton);
        frame.add(statusLabel);
        frame.setVisible(true);
    }

    private static void startRecording() {
        try {
            AudioFormat format = new AudioFormat(16000, 16, 2, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();

            byteArrayOutputStream = new ByteArrayOutputStream();
            isRecording = true;

            Thread recordingThread = new Thread(() -> {
                byte[] buffer = new byte[4096];
                int bytesRead;

                while (isRecording) {
                    bytesRead = microphone.read(buffer, 0, buffer.length);
                    if (bytesRead > 0) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }
                }
            });

            recordingThread.start();

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Erreur: " + e.getMessage());
        }
    }

    private static void stopRecordingAndSend() {
        isRecording = false;
        
        try {
            if (microphone != null) {
                microphone.stop();
                microphone.close();
            }

            if (byteArrayOutputStream != null) {
                byte[] audioData = byteArrayOutputStream.toByteArray();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
                AudioFormat format = new AudioFormat(16000, 16, 2, true, true);
                AudioInputStream audioInputStream = new AudioInputStream(
                    byteArrayInputStream, format, 
                    audioData.length / format.getFrameSize());
                
                AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File(FILE_NAME));
                sendAudio();
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Erreur lors de l'envoi: " + e.getMessage());
        }
    }

    private static void sendAudio() {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             FileInputStream fileInputStream = new FileInputStream(FILE_NAME);
             OutputStream outputStream = socket.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Erreur d'envoi: " + e.getMessage());
        }
    }
}

