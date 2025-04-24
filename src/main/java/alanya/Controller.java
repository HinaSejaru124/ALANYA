package alanya;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class Controller {
      private Timeline callDurationUpdater;


      public void startAudioCall(HBox callBar, Label callTimer) {
          // Afficher la barre d'appel
          callBar.setVisible(true);
  
          // Démarrer le minuteur
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
          // Masquer la barre d'appel
          callBar.setVisible(false);
  
          // Arrêter le minuteur
          if (callDurationUpdater != null) {
              callDurationUpdater.stop();
          }    
      }
  
  
    public Controller() {
      View.audioCallButton.setOnAction(e -> startAudioCall(View.callBar, View.callTimer));
    }
}
