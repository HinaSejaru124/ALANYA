package views;

import controllers.CallController;
import controllers.HomeController;
import controllers.InfosUserController;
import controllers.LoginController;
import controllers.SignController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application implements ViewsMethods {
    private Scene scene;
    
    // Interfaces UI
    public LoginUI loginUI;
    public SignUI signUI;
    public HomeUI homeUI;
    public VideoCallUI videoCallUI;
    public InfosUserUI infosUserUI;
    
    // Nouvelles interfaces d'appel
    public CallManager callManager;
    
    // Contrôleurs
    public LoginController loginController;
    public SignController signController;
    public HomeController homeController;
    public CallController callController;
    public InfosUserController infosUserController;

    public int interlocuteurId;

    @Override
    public void start(Stage primaryStage) {
        // Initialiser les interfaces UI existantes
        loginUI = new LoginUI();
        signUI = new SignUI();
        homeUI = new HomeUI();
        videoCallUI = new VideoCallUI();
        infosUserUI = new InfosUserUI();
        
        // Initialiser les nouvelles interfaces d'appel
        callManager = new CallManager();

        // Configurer la scène principale
        scene = new Scene(loginUI.getRoot(), 800, 600);
        scene.getStylesheets().addAll(
            getClass().getResource("/styles/LoginUI.css").toExternalForm(),
            getClass().getResource("/styles/CallUI.css").toExternalForm()
        );

        primaryStage.setScene(scene);
        scene.widthProperty().addListener(homeUI.widthListener);

        primaryStage.getIcons().add(new Image(getClass().getResource("/images/Logo1-2.png").toExternalForm()));
        primaryStage.setTitle("ALANYA");
        primaryStage.show();

        // Initialiser les contrôleurs mais ne pas les démarrer encore
        loginController = new LoginController(loginUI, this);
        signController = new SignController(signUI, this);
        homeController = new HomeController(homeUI, this);
        callController = new CallController(this);
        infosUserController = new InfosUserController(infosUserUI, this);

        // Créer et afficher l'écran de démarrage
        SplashScreen splashScreen = new SplashScreen(primaryStage, this);
        splashScreen.show();
    }

    public void startApplicationAfterSplash() {
        // Démarrer tous les contrôleurs
        loginController.start();
        signController.start();
        homeController.start();
        callController.start();
        infosUserController.start();
        
        // Afficher la fenêtre principale
        Stage primaryStage = (Stage) scene.getWindow();
        primaryStage.show();
        
        // Afficher l'écran de connexion initialement
        showLogin();
    }

    public Scene getScene() {
        return scene;
    }

    // Méthodes pour afficher les interfaces existantes
    public void showLogin() {
        scene.setRoot(loginUI.getRoot());
    }

    public void showSignIn() {
        scene.setRoot(signUI.getRoot());
    }

    public void showHome() {
        scene.setRoot(homeUI.getRoot());
    }

    public void showInfosUser() {
        scene.setRoot(infosUserUI.getRoot());
    }

    public void showVideoCallUI() {
        scene.setRoot(videoCallUI.getRoot());
    }

    // Méthodes pour les nouvelles interfaces d'appel
    public void showCallInitiationUI() {
        scene.setRoot(callManager.getInitiationUI().getRoot());
    }

    public void showCallReceptionUI() {
        scene.setRoot(callManager.getReceptionUI().getRoot());
    }

    public void showOngoingCallUI() {
        scene.setRoot(callManager.getOngoingCallUI().getRoot());
    }

    /**
     * Méthode pour initier un appel avec animation complète
     */
    public void initiateCall(String contactName) {
        callController.initiateCall(contactName);
    }

    /**
     * Méthode pour simuler la réception d'un appel
     */
    public void receiveCall(String callerName) {
        callController.receiveCall(callerName);
    }

    /**
     * Obtient l'interface HomeUI
     */
    public HomeUI getHomeUI() {
        return homeUI;
    }

    /**
     * Obtient le gestionnaire d'appel
     */
    public CallManager getCallManager() {
        return callManager;
    }

    /**
     * Affiche les préférences utilisateur
     * Cette méthode est un placeholder pour l'instant
     */
    public void showPreferences() {
        System.out.println("La fonctionnalité des préférences n'est pas encore implémentée");
    }

    /**
     * Remplace la fonction showAudioCallUI qui n'existe plus
     * maintenant nous utilisons l'interface OngoingCallUI
     */
    public void showAudioCallUI() {
        // Utiliser l'interface d'appel en cours à la place de l'interface audio
        showOngoingCallUI();
    }

    public static void main(String[] args) {
        launch(args);
    }
}