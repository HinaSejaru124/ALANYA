package views;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LoginUI implements ViewsMethods {
    private final BorderPane root;
    private final Label errorAuth;
    private final TextField nameField;
    private final PasswordField passwordField;
    private final String profilePic;
    private final Button loginButton;
    private final Label lien;

    public LoginUI() {
        VBox loginBox = new VBox(15);
        loginBox.getStyleClass().add("login-box");
        loginBox.setAlignment(Pos.CENTER);

        Button backButton = new Button("<");
        backButton.getStyleClass().add("back-button");
        backButton.setTranslateX(-170);
        backButton.setTranslateY(-100);

        Label loginLabel = new Label("Connexion");
        loginLabel.getStyleClass().add("login-title");
        loginLabel.setFont(setFontPerso("/fonts/Montserrat-Bold.ttf", 22));

        profilePic = "/icons/utilisateur.png";
        ImageView profileView = loadImage(profilePic);
        profileView.setStyle("-fx-border-width: 3px;-fx-border-color: #000000; ");
        profileView.setFitHeight(60);
        profileView.setFitWidth(60);
        profileView.setTranslateY(10);

        errorAuth = new Label("Mot de passe incorrect");
        errorAuth.getStyleClass().add("error-auth");
        errorAuth.setVisible(false);

        nameField = new TextField();
        nameField.setPromptText("Nom/Telephone");
        nameField.setFont(setFontPerso("/fonts/Montserrat-Regular.ttf", 14));
        nameField.setFocusTraversable(false);

        passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        passwordField.setFont(setFontPerso("/fonts/Montserrat-Regular.ttf", 14));
        passwordField.setFocusTraversable(false);

        loginButton = new Button("Se connecter");
        loginButton.getStyleClass().add("login-button");
        loginButton.setFont(setFontPerso("/fonts/Montserrat-Bold.ttf", 14));

        Label label = new Label("Pas de compte ?");
        label.setFont(setFontPerso("/fonts/Montserrat-Regular.ttf", 14));
        lien = new Label("S'inscrire");
        lien.getStyleClass().add("lien");
        lien.setFont(setFontPerso("/fonts/Montserrat-Regular.ttf", 14));
        HBox textBox = new HBox(5, label, lien);

        loginBox.getChildren().addAll(loginLabel, profileView, errorAuth, nameField ,passwordField, loginButton, textBox);

        root = new BorderPane();
        root.setCenter(loginBox);
        root.getStyleClass().add("border-pane");
    }

    public Parent getRoot() {
        return root;
    }

    public Button getLoginButton() {
        return loginButton;
    }

    public Label getLien() {
        return lien;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public Label getErrorAuth() {
        return errorAuth;
    }
}
