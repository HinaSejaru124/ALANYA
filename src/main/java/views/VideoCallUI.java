package views;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * Interface responsive pour les appels vidéo
 */
public class VideoCallUI implements ViewsMethods {
    private final BorderPane root;
    private final StackPane videoContainer;
    private final VBox controlsBox;
    private final HBox topControls;
    private final HBox bottomControls;
    private final Label callerNameLabel;
    private final Label statusLabel;
    private Button endCallButton;
    private Button muteButton;
    private Button switchCameraButton;
    private Button videoToggleButton;
    private Button speakerButton;
    private Button backButton;
    private Button effectsButton;
    private StackPane localVideoPreview;
    private Timeline pulseAnimation;
    private boolean isMuted = false;
    private boolean isVideoEnabled = true;
    private boolean isSpeakerOn = true;
    
    // Constante pour la taille des boutons
    private static final int BUTTON_SIZE = 22;
    
    // Taille de l'icône pour le bouton de fin d'appel (légèrement plus grand)
    private static final int END_CALL_BUTTON_SIZE = 25;
    
    // Largeur minimale pour l'interface desktop
    private static final double MIN_DESKTOP_WIDTH = 600;
    
    // Couleurs pour les boutons
    private final String ORANGE_GRADIENT = "linear-gradient(from 0% 100% to 0% 0%, #FFA726, #FB8C00)";
    private final String RED_GRADIENT = "linear-gradient(from 0% 100% to 0% 0%, #EF5350, #D32F2F)";
    private final String BLUE_GRADIENT = "linear-gradient(from 0% 100% to 0% 0%, #90CAF9, #42A5F5)";
    private final String GREEN_GRADIENT = "linear-gradient(from 0% 100% to 0% 0%, #66BB6A, #43A047)";
    private final String GRAY_GRADIENT = "linear-gradient(from 0% 100% to 0% 0%, #BDBDBD, #9E9E9E)";

    // Mode actuel de l'interface (desktop ou mobile)
    private boolean isCompactMode = false;
    
    // Listener pour détecter les changements de taille
    private ChangeListener<Number> widthChangeListener;

    public VideoCallUI() {
        // Conteneur principal
        root = new BorderPane();
        root.getStyleClass().add("video-call-container");
        
        // Conteneur pour la vidéo - prendra toute la place disponible
        videoContainer = new StackPane();
        videoContainer.setStyle("-fx-background-color: #3C4043;");
        
        // Créer l'aperçu local
        createLocalVideoPreview();
        
        // Bouton retour
        backButton = createIconButton("/icons/go-back.png", BUTTON_SIZE);
        backButton.getStyleClass().add("back-button");
        
        // Label pour le nom de l'appelant
        callerNameLabel = new Label("Jean Dupont");
        callerNameLabel.getStyleClass().add("caller-name-video");
        callerNameLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        
        // Label pour le statut
        statusLabel = new Label("Appel en cours - 00:00");
        statusLabel.getStyleClass().add("call-status-video");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #E0E0E0; -fx-opacity: 0.8;");
        
        // Créer tous les boutons de contrôle
        createControlButtons();
        
        // Organisation des contrôles du haut
        VBox nameContainer = new VBox(5, callerNameLabel, statusLabel);
        nameContainer.setAlignment(Pos.CENTER_LEFT);
        
        topControls = new HBox(10, backButton, nameContainer);
        topControls.setPadding(new Insets(15));
        topControls.setAlignment(Pos.CENTER_LEFT);
        
        // Organisation des contrôles du bas - espacés proportionnellement
        bottomControls = new HBox(20);
        bottomControls.setAlignment(Pos.CENTER);
        bottomControls.setPadding(new Insets(15));
        bottomControls.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-background-radius: 30;");
        
        // Container pour tous les contrôles
        controlsBox = new VBox();
        controlsBox.setFillWidth(true);
        controlsBox.getChildren().addAll(topControls, createSpacer(), bottomControls);
        controlsBox.setAlignment(Pos.TOP_LEFT);
        
        // Superposer les contrôles sur la vidéo
        StackPane mainContainer = new StackPane(videoContainer, controlsBox);
        root.setCenter(mainContainer);
        
        // Configurer la mise en page initiale (desktop ou mobile)
        updateLayoutForCurrentWidth(800); // Valeur par défaut pour l'initialisation
        
        // Ajouter un écouteur pour détecter les changements de taille
        setupResizeListener();
        
        // Setup animations et événements
        setupButtonActions();
        animateEntrance();
    }
    
    /**
     * Crée l'aperçu local de la vidéo
     */
    private void createLocalVideoPreview() {
        localVideoPreview = new StackPane();
        localVideoPreview.getStyleClass().add("local-video-preview");
        localVideoPreview.setStyle("-fx-background-color: #202124; -fx-background-radius: 10;");
        
        // Simuler une webcam avec une couleur de fond
        Circle webcamPlaceholder = new Circle(40);
        webcamPlaceholder.setFill(Color.web("#FB8C00", 0.5));
        localVideoPreview.getChildren().add(webcamPlaceholder);
        
        // Position par défaut en bas à droite avec une marge
        StackPane.setAlignment(localVideoPreview, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(localVideoPreview, new Insets(0, 20, 20, 0));
        
        // Lier la taille de l'aperçu à la taille du conteneur vidéo (responsive)
        localVideoPreview.prefWidthProperty().bind(
            Bindings.max(120, videoContainer.widthProperty().multiply(0.25))
        );
        localVideoPreview.prefHeightProperty().bind(
            Bindings.max(90, videoContainer.heightProperty().multiply(0.25))
        );
        
        // Limiter la taille maximale
        localVideoPreview.maxWidthProperty().bind(
            Bindings.min(250, videoContainer.widthProperty().multiply(0.3))
        );
        localVideoPreview.maxHeightProperty().bind(
            Bindings.min(187.5, videoContainer.heightProperty().multiply(0.3))
        );
        
        // Ajouter l'aperçu au conteneur vidéo
        videoContainer.getChildren().add(localVideoPreview);
    }
    
    /**
     * Crée tous les boutons de contrôle
     */
    private void createControlButtons() {
        // Créer les boutons avec leurs icônes
        muteButton = createIconButton("/icons/microphone.png", BUTTON_SIZE);
        switchCameraButton = createIconButton("/icons/camera-rotate.png", BUTTON_SIZE);
        videoToggleButton = createIconButton("/icons/video-camera-blanc.png", BUTTON_SIZE);
        speakerButton = createIconButton("/icons/volume.png", BUTTON_SIZE);
        endCallButton = createIconButton("/icons/hangup.png", END_CALL_BUTTON_SIZE);
        effectsButton = createIconButton("/icons/customization.png", BUTTON_SIZE);
        
        // Appliquer le style aux boutons
        muteButton.setStyle(createButtonStyle(BLUE_GRADIENT, BUTTON_SIZE));
        switchCameraButton.setStyle(createButtonStyle(BLUE_GRADIENT, BUTTON_SIZE));
        videoToggleButton.setStyle(createButtonStyle(BLUE_GRADIENT, BUTTON_SIZE));
        speakerButton.setStyle(createButtonStyle(GREEN_GRADIENT, BUTTON_SIZE));
        endCallButton.setStyle(createButtonStyle(RED_GRADIENT, END_CALL_BUTTON_SIZE));
        effectsButton.setStyle(createButtonStyle(ORANGE_GRADIENT, BUTTON_SIZE));
    }
    
    /**
     * Configure un écouteur pour détecter les changements de taille
     */
    private void setupResizeListener() {
        widthChangeListener = (obs, oldVal, newVal) -> {
            updateLayoutForCurrentWidth(newVal.doubleValue());
        };
        
        // Ajouter l'écouteur lorsque la scène est disponible
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (oldScene != null) {
                oldScene.widthProperty().removeListener(widthChangeListener);
            }
            if (newScene != null) {
                newScene.widthProperty().addListener(widthChangeListener);
                // Appliquer immédiatement
                updateLayoutForCurrentWidth(newScene.getWidth());
            }
        });
    }
    
    /**
     * Met à jour la disposition en fonction de la largeur actuelle
     */
    private void updateLayoutForCurrentWidth(double width) {
        boolean shouldBeCompact = width < MIN_DESKTOP_WIDTH;
        
        // Ne rien faire si le mode n'a pas changé
        if (shouldBeCompact == isCompactMode) {
            return;
        }
        
        isCompactMode = shouldBeCompact;
        
        // Réorganiser les boutons en fonction du mode
        bottomControls.getChildren().clear();
        
        if (isCompactMode) {
            // Mode compact (mobile) - réduire l'espacement, montrer moins de boutons
            bottomControls.setSpacing(15);
            bottomControls.getChildren().addAll(muteButton, videoToggleButton, endCallButton, speakerButton);
            
            // Ajuster la position de l'aperçu vidéo local
            StackPane.setAlignment(localVideoPreview, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(localVideoPreview, new Insets(0, 10, 10, 0));
            
            // Réduire la taille des labels
            callerNameLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
            statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #E0E0E0; -fx-opacity: 0.8;");
            
        } else {
            // Mode desktop - montrer tous les boutons avec plus d'espace
            bottomControls.setSpacing(20);
            bottomControls.getChildren().addAll(
                muteButton, videoToggleButton, endCallButton, 
                speakerButton, switchCameraButton, effectsButton
            );
            
            // Rétablir la position de l'aperçu vidéo local
            StackPane.setAlignment(localVideoPreview, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(localVideoPreview, new Insets(0, 20, 20, 0));
            
            // Rétablir la taille des labels
            callerNameLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
            statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #E0E0E0; -fx-opacity: 0.8;");
        }
    }
    
    /**
     * Crée le style CSS pour un bouton rond avec dégradé
     */
    private String createButtonStyle(String gradient, int size) {
        // Taille du cercle - ajouter une marge autour de l'icône
        int buttonSize = size + 20; // Pour une apparence cohérente
        
        return "-fx-background-color: " + gradient + ";" +
               "-fx-background-radius: " + buttonSize + ";" +
               "-fx-cursor: hand;" +
               "-fx-border-color: white;" +
               "-fx-border-radius: " + buttonSize + ";" +
               "-fx-border-width: 0;" +
               "-fx-min-width: " + buttonSize + "px; -fx-min-height: " + buttonSize + "px;" +
               "-fx-max-width: " + buttonSize + "px; -fx-max-height: " + buttonSize + "px;" +
               "-fx-pref-width: " + buttonSize + "px; -fx-pref-height: " + buttonSize + "px;" +
               "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 3);";
    }
    
    /**
     * Crée un spacer qui prend tout l'espace disponible
     */
    private VBox createSpacer() {
        VBox spacer = new VBox();
        VBox.setVgrow(spacer, Priority.ALWAYS); // Permet au spacer de prendre tout l'espace vertical disponible
        return spacer;
    }
    
    /**
     * Animation d'entrée pour l'interface
     */
    private void animateEntrance() {
        // Animer l'apparition des contrôles
        FadeTransition fadeControls = new FadeTransition(Duration.millis(800), controlsBox);
        fadeControls.setFromValue(0);
        fadeControls.setToValue(1);
        fadeControls.play();
        
        // Animation pour simuler la connexion vidéo
        StackPane connectionOverlay = new StackPane();
        connectionOverlay.setStyle("-fx-background-color: black;");
        videoContainer.getChildren().add(0, connectionOverlay);
        
        Label connectingLabel = new Label("Connexion en cours...");
        connectingLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
        connectionOverlay.getChildren().add(connectingLabel);
        
        // Animation de disparition de l'overlay
        FadeTransition fadeOverlay = new FadeTransition(Duration.seconds(2), connectionOverlay);
        fadeOverlay.setFromValue(1);
        fadeOverlay.setToValue(0);
        fadeOverlay.setOnFinished(e -> videoContainer.getChildren().remove(connectionOverlay));
        
        // Animation de pulsation pour le label
        Timeline pulsate = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(connectingLabel.scaleXProperty(), 1),
                new KeyValue(connectingLabel.scaleYProperty(), 1)
            ),
            new KeyFrame(Duration.seconds(0.5), 
                new KeyValue(connectingLabel.scaleXProperty(), 1.1),
                new KeyValue(connectingLabel.scaleYProperty(), 1.1)
            ),
            new KeyFrame(Duration.seconds(1), 
                new KeyValue(connectingLabel.scaleXProperty(), 1),
                new KeyValue(connectingLabel.scaleYProperty(), 1)
            )
        );
        pulsate.setCycleCount(2);
        
        // Exécuter les animations séquentiellement
        SequentialTransition sequence = new SequentialTransition(pulsate, fadeOverlay);
        sequence.play();
        
        // Démarrer le timer
        startCallTimer();
    }
    
    /**
     * Configure les actions des boutons
     */
    private void setupButtonActions() {
        // Action pour le bouton muet
        muteButton.setOnAction(e -> {
            isMuted = !isMuted;
            
            // Animation du bouton - sans changer sa taille
            FadeTransition fade = new FadeTransition(Duration.millis(150), muteButton);
            fade.setFromValue(0.7);
            fade.setToValue(1);
            fade.setCycleCount(2);
            fade.setAutoReverse(true);
            fade.play();
            
            if (isMuted) {
                muteButton.setGraphic(loadImage("/icons/microphone-slash.png", BUTTON_SIZE));
                muteButton.setStyle(createButtonStyle(RED_GRADIENT, BUTTON_SIZE));
                showStatusMessage("Microphone désactivé", Color.RED);
            } else {
                muteButton.setGraphic(loadImage("/icons/microphone.png", BUTTON_SIZE));
                muteButton.setStyle(createButtonStyle(BLUE_GRADIENT, BUTTON_SIZE));
                showStatusMessage("Microphone activé", Color.GREEN);
            }
        });
        
        // Action pour le bouton vidéo
        videoToggleButton.setOnAction(e -> {
            isVideoEnabled = !isVideoEnabled;
            
            // Animation du bouton - sans changer sa taille
            FadeTransition fade = new FadeTransition(Duration.millis(150), videoToggleButton);
            fade.setFromValue(0.7);
            fade.setToValue(1);
            fade.setCycleCount(2);
            fade.setAutoReverse(true);
            fade.play();
            
            if (!isVideoEnabled) {
                videoToggleButton.setGraphic(loadImage("/icons/video-slash-blanc.png", BUTTON_SIZE));
                videoToggleButton.setStyle(createButtonStyle(RED_GRADIENT, BUTTON_SIZE));
                showStatusMessage("Caméra désactivée", Color.RED);
                
                // Simuler la désactivation de la vidéo
                videoContainer.setStyle("-fx-background-color: #202124;");
            } else {
                videoToggleButton.setGraphic(loadImage("/icons/video-camera-blanc.png", BUTTON_SIZE));
                videoToggleButton.setStyle(createButtonStyle(BLUE_GRADIENT, BUTTON_SIZE));
                showStatusMessage("Caméra activée", Color.GREEN);
                
                // Simuler la réactivation de la vidéo
                videoContainer.setStyle("-fx-background-color: #3C4043;");
            }
        });
        
        // Action pour le bouton haut-parleur
        speakerButton.setOnAction(e -> {
            isSpeakerOn = !isSpeakerOn;
            
            // Animation du bouton - sans changer sa taille
            FadeTransition fade = new FadeTransition(Duration.millis(150), speakerButton);
            fade.setFromValue(0.7);
            fade.setToValue(1);
            fade.setCycleCount(2);
            fade.setAutoReverse(true);
            fade.play();
            
            if (!isSpeakerOn) {
                speakerButton.setGraphic(loadImage("/icons/volume-mute.png", BUTTON_SIZE));
                speakerButton.setStyle(createButtonStyle(GRAY_GRADIENT, BUTTON_SIZE));
                showStatusMessage("Haut-parleur désactivé", Color.LIGHTGRAY);
            } else {
                speakerButton.setGraphic(loadImage("/icons/volume-up.png", BUTTON_SIZE));
                speakerButton.setStyle(createButtonStyle(GREEN_GRADIENT, BUTTON_SIZE));
                showStatusMessage("Haut-parleur activé", Color.GREEN);
            }
        });
        
        // Action pour le bouton de basculement de caméra
        switchCameraButton.setOnAction(e -> {
            // Animation du bouton - sans changer sa taille
            FadeTransition fade = new FadeTransition(Duration.millis(150), switchCameraButton);
            fade.setFromValue(0.7);
            fade.setToValue(1);
            fade.play();
            
            // Animation de rotation
            RotateTransitionExt rotate = new RotateTransitionExt(Duration.millis(500), switchCameraButton);
            rotate.setFromAngle(0);
            rotate.setToAngle(180);
            rotate.setCycleCount(1);
            rotate.play();
            
            showStatusMessage("Caméra basculée", Color.WHITE);
        });
        
        // Action pour le bouton d'effets
        effectsButton.setOnAction(e -> {
            // Animation du bouton - sans changer sa taille
            FadeTransition fade = new FadeTransition(Duration.millis(150), effectsButton);
            fade.setFromValue(0.7);
            fade.setToValue(1);
            fade.setCycleCount(2);
            fade.setAutoReverse(true);
            fade.play();
            
            showStatusMessage("Effets vidéo activés", Color.ORANGE);
            
            // Ajouter un effet visuel temporaire à l'arrière-plan
            String[] effects = {"-fx-blend-mode: exclusion;", "-fx-blend-mode: multiply;", "-fx-blend-mode: screen;"};
            int effectIndex = (int) (Math.random() * effects.length);
            
            videoContainer.setStyle("-fx-background-color: #3C4043; " + effects[effectIndex]);
            
            // Revenir à la normale après quelques secondes
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(event -> {
                if (isVideoEnabled) {
                    videoContainer.setStyle("-fx-background-color: #3C4043;");
                } else {
                    videoContainer.setStyle("-fx-background-color: #202124;");
                }
            });
            pause.play();
        });
        
        // Action pour le bouton de fin d'appel
        endCallButton.setOnAction(e -> {
            // Animation du bouton - sans changer sa taille
            FadeTransition fade = new FadeTransition(Duration.millis(150), endCallButton);
            fade.setFromValue(0.7);
            fade.setToValue(1);
            fade.setCycleCount(2);
            fade.setAutoReverse(true);
            fade.play();
            
            // Terminer l'appel
            endCall();
        });
    }
    
    /**
     * Charge une image en spécifiant sa taille
     */
    private javafx.scene.image.ImageView loadImage(String path, int size) {
        return new javafx.scene.image.ImageView(new javafx.scene.image.Image(
            getClass().getResourceAsStream(path), size, size, true, true));
    }
    
    /**
     * Affiche un message de statut temporaire
     */
    private void showStatusMessage(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setTextFill(color);
        
        // Animation du message
        FadeTransition fade = new FadeTransition(Duration.millis(300), statusLabel);
        fade.setFromValue(0.5);
        fade.setToValue(1);
        fade.setAutoReverse(true);
        fade.setCycleCount(2);
        fade.play();
        
        // Rétablir le message d'appel en cours après un délai
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> {
            // Restaurer le statut d'origine avec le temps d'appel mis à jour
            updateCallTimer();
        });
        pause.play();
    }
    
    /**
     * Démarre un timer pour l'appel
     */
    private void startCallTimer() {
        final long startTime = System.currentTimeMillis();
        
        // Créer une timeline qui met à jour le temps toutes les secondes
        Timeline timer = new Timeline(
            new KeyFrame(Duration.seconds(1), event -> {
                updateCallTimer();
            })
        );
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }
    
    /**
     * Met à jour le texte du timer d'appel
     */
    private void updateCallTimer() {
        // Dans une application réelle, on calculerait le temps écoulé depuis le début de l'appel
        // Ici, on simule simplement l'incrémentation du temps
        String currentText = statusLabel.getText();
        
        if (currentText.startsWith("Appel en cours")) {
            // Extraire le temps actuel
            String timeText = currentText.substring("Appel en cours - ".length());
            String[] parts = timeText.split(":");
            
            try {
                int minutes = Integer.parseInt(parts[0]);
                int seconds = Integer.parseInt(parts[1]);
                
                // Incrémenter
                seconds++;
                if (seconds >= 60) {
                    minutes++;
                    seconds = 0;
                }
                
                // Formater et mettre à jour
                statusLabel.setText(String.format("Appel en cours - %02d:%02d", minutes, seconds));
                statusLabel.setTextFill(Color.WHITE);
            } catch (Exception e) {
                // En cas d'erreur, réinitialiser
                statusLabel.setText("Appel en cours - 00:00");
                statusLabel.setTextFill(Color.WHITE);
            }
        } else {
            // Si le texte a été modifié, le réinitialiser
            statusLabel.setText("Appel en cours - 00:00");
            statusLabel.setTextFill(Color.WHITE);
        }
    }
    
    /**
     * Simule la fin de l'appel avec animation
     */
    public void endCall() {
        // Arrêter le timer
        if (pulseAnimation != null) {
            pulseAnimation.stop();
        }
        
        // Changer le statut
        statusLabel.setText("Appel terminé");
        statusLabel.setTextFill(Color.GRAY);
        
        // Animation de fondu
        FadeTransition fade = new FadeTransition(Duration.millis(800), root);
        fade.setFromValue(1);
        fade.setToValue(0.5);
        
        // Animation de sortie vers le bas
        TranslateTransition translate = new TranslateTransition(Duration.millis(800), root);
        translate.setToY(50);
        
        ParallelTransition exit = new ParallelTransition(fade, translate);
        exit.play();
    }
    
    /**
     * Simule une qualité de connexion variable
     */
    public void simulateConnectionQualityChange() {
        String[] qualities = {"Excellente", "Bonne", "Moyenne", "Faible"};
        int randomIndex = (int) (Math.random() * qualities.length);
        
        Color[] colors = {Color.GREEN, Color.LIGHTGREEN, Color.ORANGE, Color.RED};
        
        showStatusMessage("Qualité de connexion: " + qualities[randomIndex], colors[randomIndex]);
        
        // Si la qualité est faible, simuler des problèmes de connexion
        if (randomIndex >= 2) {
            // Effet visuel pour la mauvaise connexion
            FadeTransition flicker = new FadeTransition(Duration.millis(100), videoContainer);
            flicker.setFromValue(1);
            flicker.setToValue(0.7);
            flicker.setCycleCount(6);
            flicker.setAutoReverse(true);
            flicker.play();
        }
    }
    
    // Classe utilitaire pour la rotation (JavaFX n'a pas de RotateTransition)
    private class RotateTransitionExt {
        private final Timeline timeline;
        
        public RotateTransitionExt(Duration duration, Button node) {
            timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(node.rotateProperty(), 0)),
                new KeyFrame(duration, new KeyValue(node.rotateProperty(), 180))
            );
        }
        
        public void setFromAngle(double angle) {
            // Non nécessaire pour cette implémentation simple
        }
        
        public void setToAngle(double angle) {
            // Non nécessaire pour cette implémentation simple
        }
        
        public void setCycleCount(int count) {
            timeline.setCycleCount(count);
        }
        
        public void play() {
            timeline.play();
        }
    }
    
    // Getters pour accéder aux composants
    public Parent getRoot() { return root; }
    public Button getBackButton() { return backButton; }
    public Button getEndCallButton() { return endCallButton; }
    public Button getMuteButton() { return muteButton; }
    public Button getVideoToggleButton() { return videoToggleButton; }
    public Button getSpeakerButton() { return speakerButton; }
    public Button getSwitchCameraButton() { return switchCameraButton; }
    public Button getEffectsButton() { return effectsButton; }
    public Label getCallerNameLabel() { return callerNameLabel; }
    public Label getStatusLabel() { return statusLabel; }
}