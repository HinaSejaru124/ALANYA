package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import views.AudioCallUI;
import views.MainApp;

public class CallController {
    private Timeline callDurationUpdater;
    private final AudioCallUI ui;
    private final MainApp app;

    public CallController(AudioCallUI ui, MainApp app) {
        this.ui = ui;
        this.app = app;
    }

    public void startAudioCall(HBox callBar, Label callTimer) {
        callBar.setVisible(true);
        callTimer.setText("00:00");
        callDurationUpdater = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            String currentTime = callTimer.getText();
            String[] parts = currentTime.split(":");
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);
            seconds++;
            if (seconds == 60) {
                seconds = 0;
                minutes++;
            }
            callTimer.setText(String.format("%02d:%02d", minutes, seconds));
        }));
        callDurationUpdater.setCycleCount(Timeline.INDEFINITE);
        callDurationUpdater.play();
    }

    public void endAudioCall(HBox callBar) {
        callBar.setVisible(false);
        if (callDurationUpdater != null) {
            callDurationUpdater.stop();
        }
    }

    

    public void start() {
        ui.getBackButton().setOnAction(e -> app.showHome());
        ui.getCallButton().setOnAction(e -> ui.startCall());
        ui.getHangupButton().setOnAction(e -> app.showHome());
        ui.getEndCallButton().setOnAction(e -> app.showHome());
    }

}