package views;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Interface pour un appel en cours (après connexion)
 */
public class OngoingCallUI implements ViewsMethods {
    private final BorderPane root;
    private final VBox callBox;
    private final Label contactNameLabel;
    private final Label timerLabel;
    private final Label callStatusLabel;
    private final Button contactPicture;
    private final Button endCallButton;
    private final Button muteButton;
    private final Button speakerButton;
    private final Button videoButton;
    private final Button keypadButton;
    private final Button recordButton;
    private final HBox controlButtonsBox;
    private final HBox secondaryButtonsBox;
    private Timeline callTimer;
    private boolean isMuted = false;
    private boolean isSpeakerOn = false;
    private boolean isRecording = false;
    
    // Couleurs pour les boutons (conservant le thème existant)
    private final String ORANGE_GRADIENT = "linear-gradient(from 0% 100% to 0% 0%, #FFA726, #FB8C00)";
    private final String RED_GRADIENT = "linear-gradient(from 0% 100% to 0% 0%, #EF5350, #D32F2F)";
    private final String BLUE_GRADIENT = "linear-gradient(from 0% 100% to 0% 0%, #90CAF9, #42A5F5)";
    private final String GREEN_GRADIENT = "linear-gradient(from 0% 100% to 0% 0%, #66BB6A, #43A047)";
    private final String GRAY_GRADIENT = "linear-gradient(from 0% 100% to 0% 0%, #BDBDBD, #9E9E9E)";

    public OngoingCallUI() {
        // Conteneur principal
        callBox = new VBox(15);
        callBox.setAlignment(Pos.CENTER);
        callBox.setPadding(new Insets(40));
        callBox.getStyleClass().add("call-container");
        
        // Labels pour afficher les informations
        contactNameLabel = new Label("Jean Dupont");
        contactNameLabel.getStyleClass().add("contact-name");
        contactNameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        timerLabel = new Label("00:00");
        timerLabel.getStyleClass().add("timer-label");
        timerLabel.setStyle("-fx-font-size: 16px; -fx-opacity: 0.8;");
        
        callStatusLabel = new Label("Appel en cours");
        callStatusLabel.getStyleClass().add("call-status");
        callStatusLabel.setStyle("-fx-font-size: 14px; -fx-opacity: 0.7;");
        
        // Image du contact
        contactPicture = createIconButton("/icons/user.png", 80);
        contactPicture.setStyle(
            "-fx-background-radius: 80px;" +
            "-fx-min-width: 120px; -fx-min-height: 120px;" +
            "-fx-max-width: 120px; -fx-max-height: 120px;" +
            "-fx-background-color: white;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 3);"
        );
        
        // Créer l'effet de lueur verte pour indiquer la connexion
        DropShadow connectedGlow = new DropShadow();
        connectedGlow.setColor(Color.GREEN);
        connectedGlow.setRadius(15);
        connectedGlow.setSpread(0.2);
        contactPicture.setEffect(connectedGlow);
        
        // Boutons de contrôle d'appel
        muteButton = createIconButton("/icons/microphone.png", 22);
        speakerButton = createIconButton("/icons/volume.png", 22);
        videoButton = createIconButton("/icons/video-camera-blanc.png", 22);
        endCallButton = createIconButton("/icons/hangup.png", 25);
        keypadButton = createIconButton("/icons/keyboard.png", 22);
        recordButton = createIconButton("/icons/dot-circle.png", 22);
        
        // Style des boutons
        muteButton.setStyle(createButtonStyle(BLUE_GRADIENT, 40));
        speakerButton.setStyle(createButtonStyle(BLUE_GRADIENT, 40));
        videoButton.setStyle(createButtonStyle(BLUE_GRADIENT, 40));
        endCallButton.setStyle(createButtonStyle(RED_GRADIENT, 50));
        keypadButton.setStyle(createButtonStyle(GRAY_GRADIENT, 40));
        recordButton.setStyle(createButtonStyle(GRAY_GRADIENT, 40));
        
        // Organisation des boutons
        controlButtonsBox = new HBox(25, muteButton, speakerButton, videoButton);
        controlButtonsBox.setAlignment(Pos.CENTER);
        
        secondaryButtonsBox = new HBox(25, keypadButton, recordButton);
        secondaryButtonsBox.setAlignment(Pos.CENTER);
        
        // Organisation verticale des boutons
        VBox buttonsContainer = new VBox(20, controlButtonsBox, secondaryButtonsBox, endCallButton);
        buttonsContainer.setAlignment(Pos.CENTER);
        
        // Organisation globale
        callBox.getChildren().addAll(
            contactNameLabel,
            timerLabel,
            callStatusLabel,
            contactPicture,
            buttonsContainer
        );
        
        // Conteneur racine
        root = new BorderPane();
        root.setCenter(callBox);
        root.getStyleClass().add("border-pane");
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #FFFFFF, #F5F5F5);");
        
        // Animation d'entrée
        animateEntrance();
        
        // Démarrer le timer
        startCallTimer();
        
        // Configurer les actions des boutons
        setupButtonActions();
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
     * Animation d'entrée pour l'interface
     */
    private void animateEntrance() {
        // Préparer l'animation
        callBox.setOpacity(0);
        callBox.setTranslateY(30);
        
        // Animation de fondu
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), callBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        // Animation de translation
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(500), callBox);
        slideUp.setFromY(30);
        slideUp.setToY(0);
        
        // Exécuter les animations en parallèle
        ParallelTransition entrance = new ParallelTransition(fadeIn, slideUp);
        entrance.play();
        
        // Animation de pulsation pour l'image de contact
        Timeline pulsate = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(contactPicture.scaleXProperty(), 1),
                new KeyValue(contactPicture.scaleYProperty(), 1)
            ),
            new KeyFrame(Duration.seconds(0.5), 
                new KeyValue(contactPicture.scaleXProperty(), 1.05),
                new KeyValue(contactPicture.scaleYProperty(), 1.05)
            ),
            new KeyFrame(Duration.seconds(1), 
                new KeyValue(contactPicture.scaleXProperty(), 1),
                new KeyValue(contactPicture.scaleYProperty(), 1)
            )
        );
        pulsate.setCycleCount(3);
        pulsate.play();
    }
    
    /**
     * Démarre le timer de l'appel
     */
    private void startCallTimer() {
        final int[] seconds = {0};
        callTimer = new Timeline(
            new KeyFrame(Duration.seconds(1), event -> {
                seconds[0]++;
                int mins = seconds[0] / 60;
                int secs = seconds[0] % 60;
                timerLabel.setText(String.format("%02d:%02d", mins, secs));
                
                // Mise à jour périodique de la qualité d'appel (simulation)
                if (seconds[0] % 10 == 0) {
                    updateConnectionQuality();
                }
            })
        );
        callTimer.setCycleCount(Timeline.INDEFINITE);
        callTimer.play();
    }
    
    /**
     * Simule les changements de qualité de connexion
     */
    private void updateConnectionQuality() {
        // Simuler des variations de qualité de connexion
        String[] qualities = {"Excellente", "Bonne", "Moyenne", "Faible"};
        int randomIndex = (int) (Math.random() * qualities.length);
        
        callStatusLabel.setText("Qualité: " + qualities[randomIndex]);
        
        // Ajuster la couleur en fonction de la qualité
        switch (randomIndex) {
            case 0: // Excellente
                callStatusLabel.setTextFill(Color.GREEN);
                break;
            case 1: // Bonne
                callStatusLabel.setTextFill(Color.LIGHTGREEN);
                break;
            case 2: // Moyenne
                callStatusLabel.setTextFill(Color.ORANGE);
                break;
            case 3: // Faible
                callStatusLabel.setTextFill(Color.RED);
                break;
        }
        
        // Animation pour la mise à jour
        FadeTransition fade = new FadeTransition(Duration.millis(300), callStatusLabel);
        fade.setFromValue(0.7);
        fade.setToValue(1);
        fade.setCycleCount(2);
        fade.setAutoReverse(true);
        fade.play();
    }
    
    /**
     * Configure les actions des boutons
     */
    private void setupButtonActions() {
        // Action du bouton muet
        muteButton.setOnAction(e -> {
            isMuted = !isMuted;
            if (isMuted) {
                muteButton.setGraphic(loadImage("/icons/microphone-slash.png"));
                muteButton.setStyle(createButtonStyle(RED_GRADIENT, 40));
                
                // Animation pour le bouton muet
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), muteButton);
                scale.setToX(1.2);
                scale.setToY(1.2);
                scale.setCycleCount(2);
                scale.setAutoReverse(true);
                scale.play();
                
                // Afficher brièvement un message
                callStatusLabel.setText("Microphone désactivé");
                callStatusLabel.setTextFill(Color.RED);
            } else {
                muteButton.setGraphic(loadImage("/icons/microphone.png"));
                muteButton.setStyle(createButtonStyle(BLUE_GRADIENT, 40));
                
                // Afficher brièvement un message
                callStatusLabel.setText("Microphone activé");
                callStatusLabel.setTextFill(Color.GREEN);
            }
            
            // Animation pour le message de statut
            FadeTransition fade = new FadeTransition(Duration.millis(300), callStatusLabel);
            fade.setFromValue(1);
            fade.setToValue(0.7);
            fade.play();
            
            // Rétablir le statut après un délai
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(evt -> updateConnectionQuality());
            delay.play();
        });
        
        // Action du bouton haut-parleur
        speakerButton.setOnAction(e -> {
            isSpeakerOn = !isSpeakerOn;
            if (isSpeakerOn) {
                speakerButton.setStyle(createButtonStyle(GREEN_GRADIENT, 40));
                
                // Animation pour le bouton haut-parleur
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), speakerButton);
                scale.setToX(1.2);
                scale.setToY(1.2);
                scale.setCycleCount(2);
                scale.setAutoReverse(true);
                scale.play();
                
                // Afficher brièvement un message
                callStatusLabel.setText("Haut-parleur activé");
                callStatusLabel.setTextFill(Color.GREEN);
            } else {
                speakerButton.setStyle(createButtonStyle(BLUE_GRADIENT, 40));
                
                // Afficher brièvement un message
                callStatusLabel.setText("Haut-parleur désactivé");
                callStatusLabel.setTextFill(Color.GRAY);
            }
            
            // Animation pour le message de statut
            FadeTransition fade = new FadeTransition(Duration.millis(300), callStatusLabel);
            fade.setFromValue(1);
            fade.setToValue(0.7);
            fade.play();
            
            // Rétablir le statut après un délai
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(evt -> updateConnectionQuality());
            delay.play();
        });
        
        // Action du bouton d'enregistrement
        recordButton.setOnAction(e -> {
            isRecording = !isRecording;
            if (isRecording) {
                recordButton.setStyle(createButtonStyle(RED_GRADIENT, 40));
                
                // Animation pour le bouton d'enregistrement (clignotement)
                Timeline blink = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(recordButton.opacityProperty(), 1)),
                    new KeyFrame(Duration.seconds(0.5), new KeyValue(recordButton.opacityProperty(), 0.6)),
                    new KeyFrame(Duration.seconds(1), new KeyValue(recordButton.opacityProperty(), 1))
                );
                blink.setCycleCount(Timeline.INDEFINITE);
                blink.play();
                
                // Afficher brièvement un message
                callStatusLabel.setText("Enregistrement démarré");
                callStatusLabel.setTextFill(Color.RED);
            } else {
                recordButton.setStyle(createButtonStyle(GRAY_GRADIENT, 40));
                recordButton.setOpacity(1); // Arrêter le clignotement
                
                // Afficher brièvement un message
                callStatusLabel.setText("Enregistrement arrêté");
                callStatusLabel.setTextFill(Color.GRAY);
            }
            
            // Animation pour le message de statut
            FadeTransition fade = new FadeTransition(Duration.millis(300), callStatusLabel);
            fade.setFromValue(1);
            fade.setToValue(0.7);
            fade.play();
            
            // Rétablir le statut après un délai
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(evt -> updateConnectionQuality());
            delay.play();
        });
        
        // Action du bouton vidéo
        videoButton.setOnAction(e -> {
            // Animation pour le bouton vidéo
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), videoButton);
            scale.setToX(1.2);
            scale.setToY(1.2);
            scale.setCycleCount(2);
            scale.setAutoReverse(true);
            scale.play();
            
            // Afficher brièvement un message
            callStatusLabel.setText("Passage à l'appel vidéo...");
            callStatusLabel.setTextFill(Color.BLUE);
            
            // Animation pour le message de statut
            FadeTransition fade = new FadeTransition(Duration.millis(300), callStatusLabel);
            fade.setFromValue(1);
            fade.setToValue(0.7);
            fade.play();
        });
    }
    
    /**
     * Simule la fin de l'appel avec animation
     */
    public void endCall() {
        // Arrêter le timer
        if (callTimer != null) {
            callTimer.stop();
        }
        
        // Changer le statut
        callStatusLabel.setText("Appel terminé");
        callStatusLabel.setTextFill(Color.GRAY);
        
        // Animation de sortie pour l'image de contact
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(300), contactPicture);
        scaleDown.setToX(0.8);
        scaleDown.setToY(0.8);
        
        FadeTransition fadeOutContact = new FadeTransition(Duration.millis(300), contactPicture);
        fadeOutContact.setToValue(0.5);
        
        // Animation de sortie pour les boutons de contrôle
        FadeTransition fadeOutMainButtons = new FadeTransition(Duration.millis(500), controlButtonsBox);
        fadeOutMainButtons.setToValue(0);
        
        // Animation de sortie pour les boutons secondaires
        FadeTransition fadeOutSecondButtons = new FadeTransition(Duration.millis(500), secondaryButtonsBox);
        fadeOutSecondButtons.setToValue(0);
        
        // Animation de sortie pour toute l'interface
        TranslateTransition slideDown = new TranslateTransition(Duration.millis(800), callBox);
        slideDown.setToY(50);
        
        FadeTransition fadeOutAll = new FadeTransition(Duration.millis(800), callBox);
        fadeOutAll.setToValue(0.3);
        
        // Exécuter toutes les animations
        ParallelTransition exitAnimation = new ParallelTransition(
            scaleDown, fadeOutContact, fadeOutMainButtons, fadeOutSecondButtons, slideDown, fadeOutAll
        );
        exitAnimation.play();
    }
    
    /**
     * Simule une interruption de l'appel (perte de signal, etc.)
     */
    public void simulateCallInterruption() {
        // Mettre en pause le timer
        if (callTimer != null) {
            callTimer.pause();
        }
        
        // Changer le statut
        callStatusLabel.setText("Signal perdu");
        callStatusLabel.setTextFill(Color.RED);
        
        // Animation pour le status
        FadeTransition blinkStatus = new FadeTransition(Duration.millis(300), callStatusLabel);
        blinkStatus.setFromValue(1);
        blinkStatus.setToValue(0.5);
        blinkStatus.setCycleCount(6);
        blinkStatus.setAutoReverse(true);
        blinkStatus.play();
        
        // Animation pour l'image (effet de secousse)
        Timeline shakeAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(contactPicture.translateXProperty(), 0)),
            new KeyFrame(Duration.millis(100), new KeyValue(contactPicture.translateXProperty(), -5)),
            new KeyFrame(Duration.millis(200), new KeyValue(contactPicture.translateXProperty(), 5)),
            new KeyFrame(Duration.millis(300), new KeyValue(contactPicture.translateXProperty(), -5)),
            new KeyFrame(Duration.millis(400), new KeyValue(contactPicture.translateXProperty(), 0))
        );
        shakeAnimation.setCycleCount(3);
        shakeAnimation.play();
        
        // Effet de lueur rouge
        DropShadow errorGlow = new DropShadow();
        errorGlow.setColor(Color.RED);
        errorGlow.setRadius(15);
        errorGlow.setSpread(0.2);
        
        DropShadow originalGlow = (DropShadow) contactPicture.getEffect();
        
        // Appliquer l'effet d'erreur
        contactPicture.setEffect(errorGlow);
        
        // Rétablir l'appel après un délai
        PauseTransition recoveryDelay = new PauseTransition(Duration.seconds(3));
        recoveryDelay.setOnFinished(evt -> {
            // Rétablir le timer
            if (callTimer != null) {
                callTimer.play();
            }
            
            // Rétablir l'effet original
            contactPicture.setEffect(originalGlow);
            
            // Rétablir le statut
            callStatusLabel.setText("Connexion rétablie");
            callStatusLabel.setTextFill(Color.GREEN);
            
            // Animation pour le statut
            FadeTransition fadeStatusBack = new FadeTransition(Duration.millis(300), callStatusLabel);
            fadeStatusBack.setFromValue(1);
            fadeStatusBack.setToValue(0.7);
            fadeStatusBack.play();
            
            // Rétablir le statut de qualité après un court délai
            PauseTransition statusDelay = new PauseTransition(Duration.seconds(2));
            statusDelay.setOnFinished(e -> updateConnectionQuality());
            statusDelay.play();
        });
        recoveryDelay.play();
    }
    
    // Getters pour accéder aux composants
    public Parent getRoot() { return root; }
    public Button getContactPicture() { return contactPicture; }
    public Button getEndCallButton() { return endCallButton; }
    public Button getMuteButton() { return muteButton; }
    public Button getSpeakerButton() { return speakerButton; }
    public Button getVideoButton() { return videoButton; }
    public Button getKeypadButton() { return keypadButton; }
    public Button getRecordButton() { return recordButton; }
    public Label getContactNameLabel() { return contactNameLabel; }
    public Label getTimerLabel() { return timerLabel; }
    public Label getCallStatusLabel() { return callStatusLabel; }
}