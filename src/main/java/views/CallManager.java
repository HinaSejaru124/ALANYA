package views;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * Classe principale pour gérer les différentes interfaces d'appel
 * Cette classe sert de contrôleur pour naviguer entre les différentes interfaces d'appel
 */
public class CallManager implements ViewsMethods {
    private final BorderPane root;
    private final CallInitiationUI initiationUI;
    private final CallReceptionUI receptionUI;
    private final OngoingCallUI ongoingCallUI;
    private final AudioCallUI audioCallUI;
    
    // Interface active
    private Parent activeUI;
    
    // Conteneur pour toutes les interfaces
    private final StackPane callContainer;
    
    // Nom du contact actuel
    private String currentContactName = "Jean Dupont";
    
    public CallManager() {
        // Créer les différentes interfaces
        initiationUI = new CallInitiationUI();
        receptionUI = new CallReceptionUI();
        ongoingCallUI = new OngoingCallUI();
        audioCallUI = new AudioCallUI();
        
        // Conteneur pour toutes les interfaces
        callContainer = new StackPane();
        
        // Définir l'interface active par défaut (aucune)
        activeUI = null;
        
        // Conteneur racine
        root = new BorderPane();
        root.setCenter(callContainer);
        root.getStyleClass().add("call-manager");
        
        // Configurer les actions des boutons pour toutes les interfaces
        setupButtonActions();
    }
    
    /**
     * Configure les actions des boutons
     */
    private void setupButtonActions() {
        // Actions pour l'interface d'initiation
        initiationUI.getEndCallButton().setOnAction(e -> {
            initiationUI.endCall();
            hideUIWithAnimation(initiationUI.getRoot());
        });
        
        initiationUI.getBackButton().setOnAction(e -> {
            hideUIWithAnimation(initiationUI.getRoot());
        });
        
        // Actions pour l'interface de réception
        receptionUI.getAcceptCallButton().setOnAction(e -> {
            receptionUI.acceptCall();
            // Attendre un moment puis passer à l'interface d'appel en cours
            javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(Duration.seconds(2));
            delay.setOnFinished(evt -> showOngoingCallUI());
            delay.play();
        });
        
        receptionUI.getRejectCallButton().setOnAction(e -> {
            receptionUI.rejectCall();
            hideUIWithAnimation(receptionUI.getRoot());
        });
        
        receptionUI.getSilenceButton().setOnAction(e -> {
            receptionUI.silenceRinger();
        });
        
        // Actions pour l'interface d'appel en cours
        ongoingCallUI.getEndCallButton().setOnAction(e -> {
            ongoingCallUI.endCall();
            hideUIWithAnimation(ongoingCallUI.getRoot());
        });
        
        // Actions pour AudioCallUI
        audioCallUI.getCallButton().setOnAction(e -> {
            audioCallUI.startCall();
        });
        
        audioCallUI.getEndCallButton().setOnAction(e -> {
            hideUIWithAnimation(audioCallUI.getRoot());
        });
        
        audioCallUI.getHangupButton().setOnAction(e -> {
            hideUIWithAnimation(audioCallUI.getRoot());
        });
        
        audioCallUI.getBackButton().setOnAction(e -> {
            hideUIWithAnimation(audioCallUI.getRoot());
        });
    }
    
    /**
     * Affiche l'interface d'initiation d'appel
     */
    public void showCallInitiationUI() {
        // Mettre à jour le nom du contact
        initiationUI.getNameLabel().setText(currentContactName);
        
        // Afficher l'interface avec animation
        showUIWithAnimation(initiationUI.getRoot());
        
        // Simuler la progression de l'appel
        initiationUI.simulateCallProgress();
    }
    
    /**
     * Affiche l'interface de réception d'appel
     */
    public void showCallReceptionUI() {
        // Mettre à jour le nom de l'appelant
        receptionUI.getCallerNameLabel().setText("Appel de " + currentContactName);
        
        // Afficher l'interface avec animation
        showUIWithAnimation(receptionUI.getRoot());
    }
    
    /**
     * Affiche l'interface d'appel en cours
     */
    public void showOngoingCallUI() {
        // Mettre à jour le nom du contact
        ongoingCallUI.getContactNameLabel().setText(currentContactName);
        
        // Si une autre interface est active, la masquer d'abord
        if (activeUI != null && activeUI != ongoingCallUI.getRoot()) {
            hideUIWithAnimation(activeUI);
        }
        
        // Afficher l'interface avec animation
        showUIWithAnimation(ongoingCallUI.getRoot());
    }
    
    /**
     * Affiche l'interface AudioCallUI existante
     */
    public void showAudioCallUI() {
        // Mettre à jour le label d'appel
        audioCallUI.getCallLabel().setText("Appel de " + currentContactName);
        
        // Afficher l'interface avec animation
        showUIWithAnimation(audioCallUI.getRoot());
    }
    
    /**
     * Affiche une interface avec animation
     */
    private void showUIWithAnimation(Parent ui) {
        // Supprimer l'interface active si elle existe
        if (activeUI != null) {
            callContainer.getChildren().remove(activeUI);
        }
        
        // Préparer l'animation
        ui.setOpacity(0);
        ui.setTranslateY(30);
        
        // Ajouter la nouvelle interface
        callContainer.getChildren().add(ui);
        
        // Animation de fondu
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), ui);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        // Animation de translation
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), ui);
        slideUp.setFromY(30);
        slideUp.setToY(0);
        
        // Exécuter les animations en parallèle
        ParallelTransition entrance = new ParallelTransition(fadeIn, slideUp);
        entrance.play();
        
        // Mettre à jour l'interface active
        activeUI = ui;
    }
    
    /**
     * Masque une interface avec animation
     */
    private void hideUIWithAnimation(Parent ui) {
        // Animation de fondu
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), ui);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        
        // Animation de translation
        TranslateTransition slideDown = new TranslateTransition(Duration.millis(300), ui);
        slideDown.setToY(30);
        
        // Exécuter les animations en parallèle
        ParallelTransition exit = new ParallelTransition(fadeOut, slideDown);
        exit.setOnFinished(e -> callContainer.getChildren().remove(ui));
        exit.play();
        
        // Réinitialiser l'interface active
        if (activeUI == ui) {
            activeUI = null;
        }
    }
    
    /**
     * Simule une interruption d'appel (pour démonstration)
     */
    public void simulateCallInterruption() {
        if (activeUI == ongoingCallUI.getRoot()) {
            ongoingCallUI.simulateCallInterruption();
        }
    }
    
    /**
     * Définit le nom du contact pour l'appel
     */
    public void setContactName(String name) {
        this.currentContactName = name;
        
        // Mettre à jour les interfaces existantes si nécessaire
        if (activeUI == initiationUI.getRoot()) {
            initiationUI.getNameLabel().setText(name);
        } else if (activeUI == receptionUI.getRoot()) {
            receptionUI.getCallerNameLabel().setText("Appel de " + name);
        } else if (activeUI == ongoingCallUI.getRoot()) {
            ongoingCallUI.getContactNameLabel().setText(name);
        } else if (activeUI == audioCallUI.getRoot()) {
            audioCallUI.getCallLabel().setText("Appel de " + name);
        }
    }
    
    // Getters pour accéder aux composants
    public Parent getRoot() { return root; }
    public CallInitiationUI getInitiationUI() { return initiationUI; }
    public CallReceptionUI getReceptionUI() { return receptionUI; }
    public OngoingCallUI getOngoingCallUI() { return ongoingCallUI; }
    public AudioCallUI getAudioCallUI() { return audioCallUI; }
}
