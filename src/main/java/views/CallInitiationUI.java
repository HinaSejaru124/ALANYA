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
 * Interface pour l'initiation d'un appel (quand l'utilisateur appelle quelqu'un)
 */
public class CallInitiationUI implements ViewsMethods {
    private final BorderPane root;
    private final VBox callBox;
    private final Label statusLabel;
    private final Label nameLabel;
    private final Label connectingLabel;
    private final Button contactPicture;
    private final Button endCallButton;
    private final Button muteButton;
    private final Button speakerButton;
    private final Button videoButton;
    private final Button backButton;
    private final Circle pulseCircle;
    private Timeline pulseAnimation;
    
    // Couleurs pour les boutons (conservant le thème existant)
    private final String ORANGE_GRADIENT = "linear-gradient(from 0% 100% to 0% 0%, #FFA726, #FB8C00)";
    private final String RED_GRADIENT = "linear-gradient(from 0% 100% to 0% 0%, #EF5350, #D32F2F)";
    private final String BLUE_GRADIENT = "linear-gradient(from 0% 100% to 0% 0%, #90CAF9, #42A5F5)";
    private final String GREEN_GRADIENT = "linear-gradient(from 0% 100% to 0% 0%, #66BB6A, #43A047)";

    public CallInitiationUI() {
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
        topBar.setPickOnBounds(false);
        
        // Labels pour le statut et informations
        nameLabel = new Label("Jean Dupont");
        nameLabel.getStyleClass().add("caller-name");
        nameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        statusLabel = new Label("Appel en cours...");
        statusLabel.getStyleClass().add("call-status");
        
        connectingLabel = new Label("Connexion en cours");
        connectingLabel.getStyleClass().add("connecting-label");
        connectingLabel.setOpacity(0.8);
        
        // Image du contact
        contactPicture = createIconButton("/icons/user.png", 80);
        contactPicture.setStyle(
            "-fx-background-radius: 80px;" +
            "-fx-min-width: 120px; -fx-min-height: 120px;" +
            "-fx-max-width: 120px; -fx-max-height: 120px;" +
            "-fx-background-color: white;"
        );
        
        // Effet de pulsation autour de l'image
        pulseCircle = new Circle(60);
        pulseCircle.setFill(Color.TRANSPARENT);
        pulseCircle.setStroke(Color.web("#FB8C00"));
        pulseCircle.setStrokeWidth(2);
        pulseCircle.setOpacity(0.6);
        
        StackPane contactContainer = new StackPane(pulseCircle, contactPicture);
        
        // Boutons d'actions pendant l'appel
        muteButton = createIconButton("/icons/microphone-slash.png", 22);
        speakerButton = createIconButton("/icons/volume.png", 22);
        videoButton = createIconButton("/icons/video-camera-blanc.png", 22);
        endCallButton = createIconButton("/icons/hangup.png", 22);
        
        // Style des boutons
        muteButton.setStyle(createButtonStyle(BLUE_GRADIENT, 32));
        speakerButton.setStyle(createButtonStyle(BLUE_GRADIENT, 32));
        videoButton.setStyle(createButtonStyle(BLUE_GRADIENT, 32));
        endCallButton.setStyle(createButtonStyle(RED_GRADIENT, 40));
        
        // Organisation des boutons
        HBox secondaryButtons = new HBox(30, muteButton, speakerButton, videoButton);
        secondaryButtons.setAlignment(Pos.CENTER);
        
        VBox endCallContainer = new VBox(20, secondaryButtons, endCallButton);
        endCallContainer.setAlignment(Pos.CENTER);
        
        // Organisation globale
        callBox.getChildren().addAll(nameLabel, statusLabel, contactContainer, connectingLabel, endCallContainer);
        
        // Conteneur principal
        StackPane.setAlignment(topBar, Pos.TOP_LEFT);
        StackPane.setMargin(topBar, new Insets(10));
        
        root = new BorderPane();
        root.setCenter(new StackPane(callBox, topBar));
        root.getStyleClass().add("border-pane");
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #FFFFFF, #F5F5F5);");
        
        // Démarrer l'animation de pulsation
        startPulseAnimation();
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
     * Démarre l'animation de pulsation autour de l'image du contact
     */
    private void startPulseAnimation() {
        pulseAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(pulseCircle.radiusProperty(), 60),
                new KeyValue(pulseCircle.opacityProperty(), 0.7)
            ),
            new KeyFrame(Duration.seconds(1.5), 
                new KeyValue(pulseCircle.radiusProperty(), 80),
                new KeyValue(pulseCircle.opacityProperty(), 0.1)
            )
        );
        pulseAnimation.setCycleCount(Timeline.INDEFINITE);
        pulseAnimation.setAutoReverse(true);
        pulseAnimation.play();
    }
    
    /**
     * Simule la progression de l'appel (connexion -> en cours -> terminé)
     */
    public void simulateCallProgress() {
        // Première phase: connexion
        PauseTransition phase1 = new PauseTransition(Duration.seconds(2));
        phase1.setOnFinished(e -> {
            statusLabel.setText("Téléphone sonne...");
            
            // Animation de secousse pour le téléphone qui sonne
            Timeline shakeAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(contactPicture.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(100), new KeyValue(contactPicture.translateXProperty(), -3)),
                new KeyFrame(Duration.millis(200), new KeyValue(contactPicture.translateXProperty(), 3)),
                new KeyFrame(Duration.millis(300), new KeyValue(contactPicture.translateXProperty(), -3)),
                new KeyFrame(Duration.millis(400), new KeyValue(contactPicture.translateXProperty(), 0))
            );
            shakeAnimation.setCycleCount(6);
            shakeAnimation.play();
        });

        // Deuxième phase: appel connecté
        PauseTransition phase2 = new PauseTransition(Duration.seconds(5));
        phase2.setOnFinished(e -> {
            statusLabel.setText("Appel connecté");
            connectingLabel.setText("00:00");
            
            // Arrêter l'animation de pulsation
            pulseAnimation.stop();
            
            // Animation de confirmation
            ScaleTransition scale1 = new ScaleTransition(Duration.millis(200), contactPicture);
            scale1.setToX(1.1);
            scale1.setToY(1.1);
            
            ScaleTransition scale2 = new ScaleTransition(Duration.millis(200), contactPicture);
            scale2.setToX(1.0);
            scale2.setToY(1.0);
            
            // Ajouter un effet de lueur verte pour indiquer la connexion
            DropShadow glow = new DropShadow();
            glow.setColor(Color.GREEN);
            glow.setRadius(20);
            contactPicture.setEffect(glow);
            
            new SequentialTransition(scale1, scale2).play();
            
            // Démarrer un timer fictif
            startCallTimer();
        });

        // Exécuter la séquence
        new SequentialTransition(phase1, phase2).play();
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
                connectingLabel.setText(String.format("%02d:%02d", mins, secs));
            })
        );
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }
    
    /**
     * Simule la fin de l'appel avec animation
     */
    public void endCall() {
        if (pulseAnimation != null) {
            pulseAnimation.stop();
        }
        
        statusLabel.setText("Appel terminé");
        
        // Animation de fondu pour toute l'interface
        FadeTransition fade = new FadeTransition(Duration.millis(500), callBox);
        fade.setToValue(0.5);
        
        // Animation de sortie vers le bas
        TranslateTransition translate = new TranslateTransition(Duration.millis(500), callBox);
        translate.setToY(50);
        
        ParallelTransition exit = new ParallelTransition(fade, translate);
        exit.play();
    }
    
    // Getters pour accéder aux composants
    public Parent getRoot() { return root; }
    public Button getContactPicture() { return contactPicture; }
    public Button getEndCallButton() { return endCallButton; }
    public Button getMuteButton() { return muteButton; }
    public Button getSpeakerButton() { return speakerButton; }
    public Button getVideoButton() { return videoButton; }
    public Button getBackButton() { return backButton; }
    public Label getNameLabel() { return nameLabel; }
    public Label getStatusLabel() { return statusLabel; }
}
