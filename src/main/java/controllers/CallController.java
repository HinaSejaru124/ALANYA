package controllers;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import views.CallInitiationUI;
import views.CallManager;
import views.CallReceptionUI;
import views.MainApp;
import views.OngoingCallUI;
import views.VideoCallUI;

public class CallController {
    private final VideoCallUI videoCallUI;
    private final MainApp app;
    private final CallManager callManager;
    
    private Timer callTimer;
    private Timeline pulseAnimation;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("mm:ss");

    public CallController(MainApp app) {
        this.videoCallUI = app.videoCallUI;
        this.app = app;
        
        // Initialiser le gestionnaire d'appel
        this.callManager = app.callManager;
    }

    public void start() {
        // Configurer les gestionnaires d'événements pour le VideoCallUI existant
        setupVideoCallUIEvents();
        
        // Configurer les gestionnaires d'événements pour les nouvelles interfaces
        setupCallManagerEvents();
    }
    
    /**
     * Configure les événements pour VideoCallUI
     */
    private void setupVideoCallUIEvents() {
        videoCallUI.getBackButton().setOnAction(e -> {
            animateButtonClick(videoCallUI.getBackButton());
            app.showHome();
        });
        
        videoCallUI.getEndCallButton().setOnAction(e -> {
            // Animation du bouton
            animateButtonClick(videoCallUI.getEndCallButton());
            
            // Animer la fin de l'appel
            videoCallUI.endCall();
            
            // Retourner à l'écran d'accueil
            Platform.runLater(() -> {
                try {
                    Thread.sleep(500); // Petit délai pour l'animation
                    app.showHome();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            });
        });
        
        // Configurer d'autres boutons vidéo si nécessaire
    }
    
    /**
     * Configure les événements pour les interfaces du CallManager
     */
    private void setupCallManagerEvents() {
        // Interface d'initiation d'appel
        CallInitiationUI initiationUI = callManager.getInitiationUI();
        initiationUI.getEndCallButton().setOnAction(e -> {
            animateButtonClick(initiationUI.getEndCallButton());
            initiationUI.endCall();
            
            // Retourner à l'écran d'accueil après un délai
            Platform.runLater(() -> {
                try {
                    Thread.sleep(500);
                    app.showHome();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            });
        });
        
        initiationUI.getBackButton().setOnAction(e -> {
            animateButtonClick(initiationUI.getBackButton());
            app.showHome();
        });
        
        // Interface de réception d'appel
        CallReceptionUI receptionUI = callManager.getReceptionUI();
        receptionUI.getAcceptCallButton().setOnAction(e -> {
            animateButtonClick(receptionUI.getAcceptCallButton());
            receptionUI.acceptCall();
            
            // Passer à l'interface d'appel en cours après un délai
            Platform.runLater(() -> {
                try {
                    Thread.sleep(1500);
                    app.showOngoingCallUI();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            });
        });
        
        receptionUI.getRejectCallButton().setOnAction(e -> {
            animateButtonClick(receptionUI.getRejectCallButton());
            receptionUI.rejectCall();
            
            // Retourner à l'écran d'accueil après un délai
            Platform.runLater(() -> {
                try {
                    Thread.sleep(500);
                    app.showHome();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            });
        });
        
        // Interface d'appel en cours
        OngoingCallUI ongoingCallUI = callManager.getOngoingCallUI();
        ongoingCallUI.getEndCallButton().setOnAction(e -> {
            animateButtonClick(ongoingCallUI.getEndCallButton());
            ongoingCallUI.endCall();
            
            // Retourner à l'écran d'accueil après un délai
            Platform.runLater(() -> {
                try {
                    Thread.sleep(800);
                    app.showHome();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            });
        });
        
        // Configuration des autres boutons pour les interfaces
        configureMuteButton(ongoingCallUI.getMuteButton());
        configureSpeakerButton(ongoingCallUI.getSpeakerButton());
        configureKeypadButton(ongoingCallUI.getKeypadButton());
        configureVideoButton(ongoingCallUI.getVideoButton());
    }
    
    /**
     * Configure le bouton mute
     */
    private void configureMuteButton(Button muteButton) {
        muteButton.setOnAction(e -> {
            animateButtonClick(muteButton);
            
            // Vérifier l'état actuel (basé sur le style)
            boolean isMuted = muteButton.getStyle().contains("RED_GRADIENT");
            
            if (!isMuted) {
                // Passer en mode muet
                muteButton.setGraphic(loadImage("/icons/microphone-slash.png"));
                muteButton.setStyle(createButtonStyle("RED_GRADIENT", 40));
                
                // Afficher un message
                showStatusMessage("Microphone désactivé", Color.RED);
            } else {
                // Réactiver le micro
                muteButton.setGraphic(loadImage("/icons/microphone.png"));
                muteButton.setStyle(createButtonStyle("BLUE_GRADIENT", 40));
                
                // Afficher un message
                showStatusMessage("Microphone activé", Color.GREEN);
            }
        });
    }
    
    /**
     * Configure le bouton haut-parleur
     */
    private void configureSpeakerButton(Button speakerButton) {
        speakerButton.setOnAction(e -> {
            animateButtonClick(speakerButton);
            
            // Vérifier l'état actuel (basé sur le style)
            boolean isSpeakerOn = speakerButton.getStyle().contains("GREEN_GRADIENT");
            
            if (isSpeakerOn) {
                // Désactiver le haut-parleur
                speakerButton.setGraphic(loadImage("/icons/volume-mute.png"));
                speakerButton.setStyle(createButtonStyle("GRAY_GRADIENT", 40));
                
                // Afficher un message
                showStatusMessage("Haut-parleur désactivé", Color.GRAY);
            } else {
                // Activer le haut-parleur
                speakerButton.setGraphic(loadImage("/icons/volume-up.png"));
                speakerButton.setStyle(createButtonStyle("GREEN_GRADIENT", 40));
                
                // Afficher un message
                showStatusMessage("Haut-parleur activé", Color.GREEN);
            }
        });
    }
    
    /**
     * Configure le bouton clavier
     */
    private void configureKeypadButton(Button keypadButton) {
        keypadButton.setOnAction(e -> {
            animateButtonClick(keypadButton);
            
            // Ici, vous pourriez afficher une interface de clavier numérique
            showStatusMessage("Clavier numérique", Color.BLUE);
            
            // Pour l'instant, juste l'animation
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), keypadButton);
            scale.setToX(1.2);
            scale.setToY(1.2);
            scale.setCycleCount(2);
            scale.setAutoReverse(true);
            scale.play();
        });
    }
    
    /**
     * Configure le bouton vidéo
     */
    private void configureVideoButton(Button videoButton) {
        videoButton.setOnAction(e -> {
            animateButtonClick(videoButton);
            
            // Ici, vous pourriez basculer vers un appel vidéo
            showStatusMessage("Passage à l'appel vidéo...", Color.BLUE);
            
            // Pour l'instant, juste l'animation
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), videoButton);
            scale.setToX(1.2);
            scale.setToY(1.2);
            scale.setCycleCount(2);
            scale.setAutoReverse(true);
            scale.play();
            
            // Animation de transition vers la vidéo
            Platform.runLater(() -> {
                try {
                    Thread.sleep(500);
                    app.showVideoCallUI();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            });
        });
    }
    
    /**
     * Charge une image depuis le classpath
     */
    private javafx.scene.image.ImageView loadImage(String path) {
        return new javafx.scene.image.ImageView(new javafx.scene.image.Image(
            getClass().getResourceAsStream(path), 22, 22, true, true));
    }
    
    /**
     * Crée le style CSS pour un bouton
     */
    private String createButtonStyle(String gradientName, int size) {
        String gradient;
        
        switch (gradientName) {
            case "RED_GRADIENT":
                gradient = "linear-gradient(from 0% 100% to 0% 0%, #EF5350, #D32F2F)";
                break;
            case "BLUE_GRADIENT":
                gradient = "linear-gradient(from 0% 100% to 0% 0%, #90CAF9, #42A5F5)";
                break;
            case "GREEN_GRADIENT":
                gradient = "linear-gradient(from 0% 100% to 0% 0%, #66BB6A, #43A047)";
                break;
            case "GRAY_GRADIENT":
                gradient = "linear-gradient(from 0% 100% to 0% 0%, #BDBDBD, #9E9E9E)";
                break;
            case "ORANGE_GRADIENT":
            default:
                gradient = "linear-gradient(from 0% 100% to 0% 0%, #FFA726, #FB8C00)";
                break;
        }
        
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
     * Affiche un message de statut
     */
    private void showStatusMessage(String message, Color color) {
        // Cette méthode est appelée mais ne fait rien actuellement
        // Elle pourrait afficher un message temporaire sur l'interface
        System.out.println("Status: " + message);
    }
    
    /**
     * Animation de clic pour un bouton
     */
    private void animateButtonClick(Button button) {
        javafx.animation.ScaleTransition scale = new javafx.animation.ScaleTransition(Duration.millis(100), button);
        scale.setFromX(1);
        scale.setFromY(1);
        scale.setToX(0.9);
        scale.setToY(0.9);
        scale.setCycleCount(2);
        scale.setAutoReverse(true);
        scale.play();
    }
    
    /**
     * Animation pour la fin d'un appel
     */
    private void animateEndCall(javafx.scene.Parent root) {
        FadeTransition fade = new FadeTransition(Duration.millis(500), root);
        fade.setFromValue(1);
        fade.setToValue(0.7);
        
        javafx.animation.TranslateTransition translate = new javafx.animation.TranslateTransition(Duration.millis(500), root);
        translate.setByY(20);
        
        ParallelTransition transition = new ParallelTransition(fade, translate);
        transition.play();
    }
    
    /**
     * Démarre un timer pour un appel
     */
    private void startCallTimer(Label timerLabel) {
        if (callTimer != null) {
            callTimer.cancel();
        }
        
        callTimer = new Timer();
        final long startTime = System.currentTimeMillis();
        
        callTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long elapsedTime = System.currentTimeMillis() - startTime;
                long elapsedSeconds = elapsedTime / 1000;
                
                LocalTime time = LocalTime.ofSecondOfDay(elapsedSeconds);
                String formattedTime = time.format(TIME_FMT);
                
                Platform.runLater(() -> timerLabel.setText(formattedTime));
            }
        }, 0, 1000);
    }
    
    /**
     * Démarre un appel en utilisant l'interface d'initiation
     */
    public void initiateCall(String contactName) {
        // Mettre à jour le nom du contact
        callManager.setContactName(contactName);
        
        // Afficher l'interface d'initiation d'appel
        app.getScene().setRoot(callManager.getInitiationUI().getRoot());
        
        // Simuler la progression de l'appel
        callManager.getInitiationUI().simulateCallProgress();
    }
    
    /**
     * Simule la réception d'un appel
     */
    public void receiveCall(String callerName) {
        // Mettre à jour le nom de l'appelant
        callManager.setContactName(callerName);
        
        // Afficher l'interface de réception d'appel
        app.getScene().setRoot(callManager.getReceptionUI().getRoot());
    }
    
    /**
     * Affiche l'interface d'appel en cours
     */
    public void showOngoingCallUI() {
        app.getScene().setRoot(callManager.getOngoingCallUI().getRoot());
    }
    
    /**
     * Affiche l'interface VideoCallUI existante
     */
    public void showVideoCallUI() {
        app.showVideoCallUI();
    }
    
    /**
     * Simule une interruption d'appel
     */
    public void simulateCallInterruption() {
        callManager.simulateCallInterruption();
    }
    
    /**
     * Obtient le gestionnaire d'appel
     */
    public CallManager getCallManager() {
        return callManager;
    }
}