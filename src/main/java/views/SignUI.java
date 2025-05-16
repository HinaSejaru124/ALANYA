package views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class SignUI implements ViewsMethods {
    private final BorderPane root;
    private final Button backButton, registerButton, chooseProfilePic;
    private final TextField firstNameField, lastNameField, numberField;
    private final PasswordField passwordField;

    public SignUI() {
        VBox loginBox = new VBox(10);
        loginBox.getStyleClass().add("login-box");
        loginBox.setAlignment(Pos.CENTER);

        backButton = createIconButton("/icons/go-back.png", 30);
        backButton.getStyleClass().add("back-button");
        backButton.setTranslateX(-170);
        backButton.setTranslateY(-10);

        Label loginLabel = new Label("Inscription");
        loginLabel.getStyleClass().add("login-title");
        loginLabel.setFont(setFontPerso("/fonts/Montserrat-Bold.ttf", 22));

        chooseProfilePic = createIconButton("/icons/utilisateur.png", 60);
        chooseProfilePic.setPadding(new Insets(10));
        chooseProfilePic.setMinSize(90, 90);
        chooseProfilePic.setPrefSize(90, 90);
        chooseProfilePic.setMaxSize(90, 90);
        chooseProfilePic.setStyle("-fx-background-color: transparent;"
                + "-fx-border-color: black;"
                + "-fx-border-width: 1px;"
                + "-fx-background-radius: 50px;"
                + "-fx-border-radius: 50px;");

        firstNameField = new TextField();
        firstNameField.setPromptText("Nom de famille");
        firstNameField.setFont(setFontPerso("/fonts/Montserrat-Regular.ttf", 14));

        lastNameField = new TextField();
        lastNameField.setPromptText("Prénom");
        lastNameField.setFont(setFontPerso("/fonts/Montserrat-Regular.ttf", 14));

        numberField = new TextField();
        numberField.setPromptText("Téléphone");
        numberField.setFont(setFontPerso("/fonts/Montserrat-Regular.ttf", 14));
        

        passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        passwordField.setFont(setFontPerso("/fonts/Montserrat-Regular.ttf", 14));

        registerButton = new Button("S'enregistrer");
        registerButton.getStyleClass().add("login-button");
        registerButton.setFont(setFontPerso("/fonts/Montserrat-Bold.ttf", 14));

        loginBox.getChildren().addAll(backButton, loginLabel, chooseProfilePic, new Region(), firstNameField,
                lastNameField, numberField, passwordField, registerButton);

        root = new BorderPane();
        root.setCenter(loginBox);
        root.getStyleClass().add("border-pane");
    }

    public Parent getRoot() {
        return root;
    }

    public Button getBackButton() {
        return backButton;
    }

    public Button getRegisterButton() {
        return registerButton;
    }

    public TextField getFirstNameField() {
        return firstNameField;
    }

    public TextField getLastNameField() {
        return lastNameField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public Button getChooseProfilePicButton() {
        return chooseProfilePic;
    }

}
