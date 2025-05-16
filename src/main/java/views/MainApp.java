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
    public LoginUI loginUI;
    public SignUI signUI;
    public HomeUI homeUI;
    public AudioCallUI audioCallUI;
    public VideoCallUI videoCallUI;
    public InfosUserUI infosUserUI;

    public LoginController loginController;
    public SignController signController;
    public HomeController homeController;
    public CallController callController;
    public InfosUserController infosUserController;

    public int interlocuteurId;

    @Override
    public void start(Stage primaryStage) {
        loginUI = new LoginUI();
        signUI = new SignUI();
        homeUI = new HomeUI();
        audioCallUI = new AudioCallUI();
        videoCallUI = new VideoCallUI();
        infosUserUI = new InfosUserUI();

        scene = new Scene(loginUI.getRoot(), 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/LoginUI.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(getClass().getResource("/images/Logo1-2.png").toExternalForm()));
        primaryStage.setTitle("ALANYA");
        primaryStage.show();

        loginController = new LoginController(loginUI, this);
        loginController.start();

        signController = new SignController(signUI, this);
        signController.start();

        homeController = new HomeController(homeUI, this);
        homeController.start();

        callController = new CallController(audioCallUI, this);
        callController.start();
        
        infosUserController = new InfosUserController(infosUserUI, this);
        infosUserController.start();
    }

    public Scene getScene() {
        return scene;
    }

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

    public void showAudioCallUI() {
        scene.setRoot(audioCallUI.getRoot());
    }

    public void showVideoCallUI() {
        scene.setRoot(videoCallUI.getRoot());
    }

    public static void main(String[] args) {
        launch(args);
    }
}