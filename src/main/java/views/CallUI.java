package views;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class CallUI implements ViewsMethods {
    private final BorderPane root;
    private final VBox callBox;
    private final HBox initialButtons;
    private final HBox activeCallButtons;
    private final Label callLabel;
    private final Label timerLabel;

    private final Button callButton;
    private final Button hangupButton;
    private final Button muteButton;
    private final Button endCallButton;
    private final Button userCallerPic;
    public Button backButton;

    public CallUI() {
        // Conteneur principal
        callBox = new VBox(15);
        callBox.setAlignment(Pos.CENTER);
        callBox.setPadding(new Insets(40));
        callBox.getStyleClass().add("call-container");

        // Bouton retour
        backButton = createIconButton("/icons/go-back.png", 24);
        backButton.getStyleClass().add("back-button");

        // Barre supérieure
        HBox topBar = new HBox(backButton);
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPadding(new Insets(10, 0, 0, 10));
        topBar.setPickOnBounds(false); // Important


        // Nom de l’appelant (réduit et en haut)
        callLabel = new Label("Appel de Jean Dupont");
        callLabel.getStyleClass().add("caller-name");

        // Minuteur
        timerLabel = new Label("");
        timerLabel.getStyleClass().add("call-timer");

        // Image de l’appelant
        userCallerPic = createIconButton("/icons/user.png", 80);
        userCallerPic.setStyle(
            "-fx-background-radius: 80px;" +
            "-fx-min-width: 100px; -fx-min-height: 100px;" +
            "-fx-max-width: 100px; -fx-max-height: 100px;" +
            "-fx-background-color: white;"
        );

        // Boutons appel / raccrocher
        callButton = createIconButton("/icons/telephone.png", 25);
        hangupButton = createIconButton("/icons/hangup.png", 25);

        callButton.setStyle(buttonStyle("#FFA726", "#FB8C00"));
        hangupButton.setStyle(buttonStyle("#EF5350", "#D32F2F"));

        initialButtons = new HBox(40, callButton, hangupButton);
        initialButtons.setAlignment(Pos.CENTER);

        // Boutons en appel actif
        muteButton = createIconButton("/icons/microphone-slash.png", 25);
        endCallButton = createIconButton("/icons/hangup.png", 25);
        muteButton.setStyle(buttonStyle("#90CAF9", "#42A5F5"));
        endCallButton.setStyle(buttonStyle("#EF5350", "#D32F2F"));
        activeCallButtons = new HBox(40, muteButton, endCallButton);
        activeCallButtons.setAlignment(Pos.CENTER);
        activeCallButtons.setVisible(false);
        activeCallButtons.setOpacity(0);

        // Organisation
        callBox.getChildren().addAll(callLabel, timerLabel, userCallerPic, initialButtons, activeCallButtons);
        StackPane.setAlignment(topBar, Pos.TOP_LEFT);
        StackPane.setMargin(topBar, new Insets(10));

        root = new BorderPane();
        root.setCenter(new StackPane(callBox, topBar));
        root.getStyleClass().add("border-pane");
    }

    private String buttonStyle(String color1, String color2) {
        return "-fx-background-color: linear-gradient(from 0% 100% to 0% 0%, " + color1 + ", " + color2 + ");" +
               "-fx-background-radius: 40;" +
               "-fx-cursor: hand;" +
               "-fx-border-color: white;" +
               "-fx-border-radius: 40;" +
               "-fx-border-width: 0;" +
               "-fx-min-width: 40px; -fx-min-height: 40px;" +
               "-fx-max-width: 40px; -fx-max-height: 40px;";
    }

    public void startCall() {
        // 1. Animation disparition des anciens boutons
        TranslateTransition slideDown = new TranslateTransition(Duration.millis(300), initialButtons);
        slideDown.setByY(50);
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), initialButtons);
        fadeOut.setToValue(0);

        // 2. Animation apparition des nouveaux boutons
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), activeCallButtons);
        slideUp.setFromY(50);
        slideUp.setToY(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), activeCallButtons);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        slideUp.setOnFinished(e -> activeCallButtons.setVisible(true));

        SequentialTransition sequence = new SequentialTransition(
            slideDown, fadeOut,
            new javafx.animation.PauseTransition(Duration.millis(100)),
            new javafx.animation.Transition() {
                {
                    setCycleDuration(Duration.ZERO);
                }
                @Override protected void interpolate(double frac) {
                    initialButtons.setVisible(false);
                    activeCallButtons.setVisible(true);
                }
            },
            slideUp, fadeIn
        );
        sequence.play();

        // Affichage du timer fictif (exemple statique ici)
        timerLabel.setText("00:00");

        // Positionne le nom + timer plus haut
        callLabel.setStyle("-fx-font-size: 16px;");
        VBox.setMargin(callLabel, new Insets(0, 0, 5, 0));
        VBox.setMargin(timerLabel, new Insets(0, 0, 10, 0));
    }

    public Parent getRoot() {
        return root;
    }

    // Méthodes pour les boutons
    public Button getCallButton() { return callButton; }
    public Button getHangupButton() { return hangupButton; }
    public Button getMuteButton() { return muteButton; }
    public Button getEndCallButton() { return endCallButton; }
    public Button getUserCallerPic() { return userCallerPic; }
    public Button getBackButton() { return backButton; }
    public Label getTimerLabel() { return timerLabel; }
    public Label getCallLabel() { return callLabel; }
}
