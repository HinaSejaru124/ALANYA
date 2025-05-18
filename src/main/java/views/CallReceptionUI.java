package views;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * Interface pour la réception d'un appel (quand l'utilisateur reçoit un appel)
 */
public class CallReceptionUI implements ViewsMethods {
    private final BorderPane root;
    private final VBox callBox;
    private final Label callerNameLabel;
    private final Label callerStatusLabel;
    private final Button callerPicture;
    private final Button acceptCallButton;
    private final Button rejectCallButton;
    private final Button declineMessageButton;
    private final Button silenceButton;
    private final HBox answerButtonsBox;
    private final HBox secondaryButtonsBox;
    private Timeline pulseAnimation;
    private Timeline shakeAnimation;
    
    // Couleurs pour les boutons (conservant le thème existant)
    private final String ORANGE_GRADIENT = "linear-gradient(from 0% 100% to 0% 0%, #FFA726, #FB8C00)";
    private final String RED_GRADIENT = "linear-gradient(from 0% 100% to 0% 0%, #EF5350, #D32F2F)";
    private final String GREEN_GRADIENT = "linear-gradient(from 0% 100% to 0% 0%, #66BB6A, #43A047)";
    private final String BLUE_GRADIENT = "linear-gradient(from 0% 100% to 0% 0%, #90CAF9, #42A5F5)";

    public CallReceptionUI() {
        // Conteneur principal
        callBox = new VBox(15);
        callBox.setAlignment(Pos.CENTER);
        callBox.setPadding(new Insets(40));
        callBox.getStyleClass().add("call-container");
        
        // Labels pour le nom et le statut de l'appelant
        callerNameLabel = new Label("Jean Dupont");
        callerNameLabel.getStyleClass().add("caller-name");
        callerNameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        callerStatusLabel = new Label("Appel entrant");
        callerStatusLabel.getStyleClass().add("call-status");
        callerStatusLabel.setStyle("-fx-font-size: 14px; -fx-opacity: 0.8;");
        
        // Image de l'appelant
        callerPicture = createIconButton("/icons/user.png", 80);
        callerPicture.setStyle(
            "-fx-background-radius: 80px;" +
            "-fx-min-width: 120px; -fx-min-height: 120px;" +
            "-fx-max-width: 120px; -fx-max-height: 120px;" +
            "-fx-background-color: white;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 3);"
        );
        
        // Boutons pour répondre/rejeter
        acceptCallButton = createIconButton("/icons/telephone.png", 25);
        rejectCallButton = createIconButton("/icons/hangup.png", 25);
        declineMessageButton = createIconButton("/icons/phone-slash.png", 22);
        silenceButton = createIconButton("/icons/volume-mute.png", 22);
        
        // Style des boutons
        acceptCallButton.setStyle(createButtonStyle(GREEN_GRADIENT, 50));
        rejectCallButton.setStyle(createButtonStyle(RED_GRADIENT, 50));
        declineMessageButton.setStyle(createButtonStyle(BLUE_GRADIENT, 36));
        silenceButton.setStyle(createButtonStyle(ORANGE_GRADIENT, 36));
        
        // Organisation des boutons
        answerButtonsBox = new HBox(40, acceptCallButton, rejectCallButton);
        answerButtonsBox.setAlignment(Pos.CENTER);
        
        secondaryButtonsBox = new HBox(30, silenceButton, declineMessageButton);
        secondaryButtonsBox.setAlignment(Pos.CENTER);
        
        // Conteneur pour l'image avec animation de secousse
        StackPane callerImageContainer = new StackPane();
        
        // Cercles concentriques pour l'effet de pulsation
        Circle outerCircle = new Circle(70);
        outerCircle.setFill(Color.TRANSPARENT);
        outerCircle.setStroke(Color.web("#43A047"));
        outerCircle.setStrokeWidth(2);
        outerCircle.setOpacity(0.6);
        
        Circle innerCircle = new Circle(60);
        innerCircle.setFill(Color.TRANSPARENT);
        innerCircle.setStroke(Color.web("#43A047"));
        innerCircle.setStrokeWidth(1.5);
        innerCircle.setOpacity(0.4);
        
        callerImageContainer.getChildren().addAll(outerCircle, innerCircle, callerPicture);
        
        // Organisation globale
        callBox.getChildren().addAll(
            callerNameLabel, 
            callerStatusLabel, 
            callerImageContainer, 
            answerButtonsBox, 
            secondaryButtonsBox
        );
        
        // Conteneur racine
        root = new BorderPane();
        root.setCenter(callBox);
        root.getStyleClass().add("border-pane");
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #FFFFFF, #F5F5F5);");
        
        // Démarrer les animations
        startIncomingCallAnimation(outerCircle, innerCircle);
    }
    
    /**
     * Crée le style CSS pour un bouton rond avec dégradé
     */
    private String createButtonStyle(String gradient, int size) {
        return "-fx-background-color: " + gradient + ";" +
               "-fx-background-radius: " + size + ";" +
               "-fx-cursor: hand;" +
               "-fx-border-color: white;" +
               "-fx-border-radius: " + size + ";" +
               "-fx-border-width: 0;" +
               "-fx-min-width: " + size + "px; -fx-min-height: " + size + "px;" +
               "-fx-max-width: " + size + "px; -fx-max-height: " + size + "px;" +
               "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 3);";
    }
    
    /**
     * Démarre l'animation pour l'appel entrant
     */
    private void startIncomingCallAnimation(Circle outerCircle, Circle innerCircle) {
        // Animation de pulsation pour les cercles
        pulseAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(outerCircle.radiusProperty(), 70),
                new KeyValue(outerCircle.opacityProperty(), 0.6),
                new KeyValue(innerCircle.radiusProperty(), 60),
                new KeyValue(innerCircle.opacityProperty(), 0.4)
            ),
            new KeyFrame(Duration.seconds(1), 
                new KeyValue(outerCircle.radiusProperty(), 85),
                new KeyValue(outerCircle.opacityProperty(), 0),
                new KeyValue(innerCircle.radiusProperty(), 75),
                new KeyValue(innerCircle.opacityProperty(), 0.1)
            )
        );
        pulseAnimation.setCycleCount(Timeline.INDEFINITE);
        pulseAnimation.play();
        
        // Animation de secousse pour l'image
        shakeAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(callerPicture.translateXProperty(), 0)),
            new KeyFrame(Duration.millis(100), new KeyValue(callerPicture.translateXProperty(), -3)),
            new KeyFrame(Duration.millis(200), new KeyValue(callerPicture.translateXProperty(), 3)),
            new KeyFrame(Duration.millis(300), new KeyValue(callerPicture.translateXProperty(), -3)),
            new KeyFrame(Duration.millis(400), new KeyValue(callerPicture.translateXProperty(), 0))
        );
        shakeAnimation.setCycleCount(Timeline.INDEFINITE);
        shakeAnimation.play();
        
        // Animation de pulsation pour les boutons de réponse
        Timeline buttonPulse = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(acceptCallButton.scaleXProperty(), 1),
                new KeyValue(acceptCallButton.scaleYProperty(), 1),
                new KeyValue(rejectCallButton.scaleXProperty(), 1),
                new KeyValue(rejectCallButton.scaleYProperty(), 1)
            ),
            new KeyFrame(Duration.seconds(1), 
                new KeyValue(acceptCallButton.scaleXProperty(), 1.1),
                new KeyValue(acceptCallButton.scaleYProperty(), 1.1),
                new KeyValue(rejectCallButton.scaleXProperty(), 1.1),
                new KeyValue(rejectCallButton.scaleYProperty(), 1.1)
            ),
            new KeyFrame(Duration.seconds(2), 
                new KeyValue(acceptCallButton.scaleXProperty(), 1),
                new KeyValue(acceptCallButton.scaleYProperty(), 1),
                new KeyValue(rejectCallButton.scaleXProperty(), 1),
                new KeyValue(rejectCallButton.scaleYProperty(), 1)
            )
        );
        buttonPulse.setCycleCount(Timeline.INDEFINITE);
        buttonPulse.play();
    }
    
    /**
     * Simule l'action d'accepter un appel
     */
    public void acceptCall() {
        // Arrêter les animations
        stopAllAnimations();
        
        // Mettre à jour les statuts
        callerStatusLabel.setText("Connexion en cours...");
        
        // Animation de disparition des boutons de rejet
        FadeTransition fadeOutReject = new FadeTransition(Duration.millis(300), rejectCallButton);
        fadeOutReject.setToValue(0);
        
        // Animation de déplacement du bouton d'acceptation
        TranslateTransition moveAccept = new TranslateTransition(Duration.millis(500), acceptCallButton);
        moveAccept.setToY(50);
        
        // Animation de disparition des boutons secondaires
        FadeTransition fadeOutSecondary = new FadeTransition(Duration.millis(300), secondaryButtonsBox);
        fadeOutSecondary.setToValue(0);
        
        // Exécuter les animations en parallèle
        ParallelTransition transition = new ParallelTransition(
            fadeOutReject, moveAccept, fadeOutSecondary
        );
        
        transition.setOnFinished(e -> {
            // Changer le style du bouton accepter pour le transformer en bouton "raccrocher"
            acceptCallButton.setStyle(createButtonStyle(RED_GRADIENT, 50));
            acceptCallButton.setGraphic(loadImage("/icons/hangup.png"));
            
            // Ajouter un effet de lueur verte pour indiquer la connexion
            DropShadow glow = new DropShadow();
            glow.setColor(Color.GREEN);
            glow.setRadius(20);
            callerPicture.setEffect(glow);
            
            // Mettre à jour le statut
            callerStatusLabel.setText("Appel en cours - 00:00");
            
            // Animation de remontée du bouton raccrocher
            TranslateTransition moveBack = new TranslateTransition(Duration.millis(300), acceptCallButton);
            moveBack.setToY(0);
            moveBack.play();
            
            // Démarrer un timer fictif
            startCallTimer();
        });
        
        transition.play();
    }
    
    /**
     * Simule l'action de rejeter un appel
     */
    public void rejectCall() {
        // Arrêter les animations
        stopAllAnimations();
        
        // Mettre à jour les statuts
        callerStatusLabel.setText("Appel rejeté");
        
        // Animation de disparition de l'interface
        FadeTransition fade = new FadeTransition(Duration.millis(500), callBox);
        fade.setToValue(0.5);
        
        // Animation de sortie vers le bas
        TranslateTransition translate = new TranslateTransition(Duration.millis(500), callBox);
        translate.setToY(50);
        
        ParallelTransition exit = new ParallelTransition(fade, translate);
        exit.play();
    }
    
    /**
     * Silence la sonnerie mais garde l'appel actif
     */
    public void silenceRinger() {
        // Arrêter l'animation de secousse
        if (shakeAnimation != null) {
            shakeAnimation.stop();
        }
        
        // Changer l'apparence du bouton silence
        silenceButton.setStyle(createButtonStyle(RED_GRADIENT, 36));
        
        // Mettre à jour le statut
        callerStatusLabel.setText("Sonnerie silencieuse");
        
        // Animation de confirmation
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), silenceButton);
        scale.setToX(1.2);
        scale.setToY(1.2);
        scale.setCycleCount(2);
        scale.setAutoReverse(true);
        scale.play();
    }
    
    /**
     * Démarre un timer fictif pour l'appel
     */
    private void startCallTimer() {
        final int[] seconds = {0};
        Timeline timer = new Timeline(
            new KeyFrame(Duration.seconds(1), event -> {
                seconds[0]++;
                int mins = seconds[0] / 60;
                int secs = seconds[0] % 60;
                callerStatusLabel.setText(String.format("Appel en cours - %02d:%02d", mins, secs));
            })
        );
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }
    
    /**
     * Arrête toutes les animations en cours
     */
    private void stopAllAnimations() {
        if (pulseAnimation != null) {
            pulseAnimation.stop();
        }
        if (shakeAnimation != null) {
            shakeAnimation.stop();
        }
    }
    
    // Getters pour accéder aux composants
    public Parent getRoot() { return root; }
    public Button getCallerPicture() { return callerPicture; }
    public Button getAcceptCallButton() { return acceptCallButton; }
    public Button getRejectCallButton() { return rejectCallButton; }
    public Button getDeclineMessageButton() { return declineMessageButton; }
    public Button getSilenceButton() { return silenceButton; }
    public Label getCallerNameLabel() { return callerNameLabel; }
    public Label getCallerStatusLabel() { return callerStatusLabel; }
}
