package views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class InfosUserUI implements ViewsMethods {

    private final BorderPane root;
    private final Button backButton;
    private final Button profilePicButton;

    public Label nameLabel;
    public Label prenomLabel;
    public Label idLabel;

    public InfosUserUI() {
        // Bouton retour stylé Telegram
        backButton = createIconButton("/icons/go-back.png", 30);
        backButton.getStyleClass().add("back-button");

        HBox backBox = new HBox(backButton);
        backBox.setAlignment(Pos.TOP_LEFT);
        backBox.setPadding(new Insets(20, 0, 0, 20));

        // Photo de profil circulaire
        profilePicButton = createIconButton("/icons/utilisateur.png", 100);
        profilePicButton.setStyle(
              "-fx-background-color: #E1ECF4;"
            + "-fx-background-radius: 80;"
            + "-fx-min-width: 140px; -fx-min-height: 140px;"
            + "-fx-max-width: 140px; -fx-max-height: 140px;"
        );

        VBox profilePicBox = new VBox(profilePicButton);
        profilePicBox.setAlignment(Pos.CENTER);
        profilePicBox.setPadding(new Insets(10, 0, 10, 0));

        // Labels avec icônes
        nameLabel = new Label("Nom :");
        prenomLabel = new Label("Prénom :");
        idLabel = new Label("Identifiant :");

        nameLabel.getStyleClass().add("user-info-label");
        prenomLabel.getStyleClass().add("user-info-label");
        idLabel.getStyleClass().add("user-info-label");

        HBox nameBox = createInfoLine("/icons/utilisateur.png", nameLabel);
        HBox prenomBox = createInfoLine("/icons/utilisateur.png", prenomLabel);
        HBox idBox = createInfoLine("/icons/info.png", idLabel);

        VBox infosBox = new VBox(18, nameBox, prenomBox, idBox);
        infosBox.setAlignment(Pos.CENTER_LEFT);
        infosBox.setPadding(new Insets(0, 0, 0, 50));

        VBox centerBox = new VBox(30, profilePicBox, infosBox);
        centerBox.setAlignment(Pos.CENTER);

        // Layout principal
        root = new BorderPane();
        root.getStyleClass().add("root-infos-user");
        root.setTop(backBox);
        root.setCenter(centerBox);
    }

    private HBox createInfoLine(String iconPath, Label label) {
        ImageView icon = new ImageView(getClass().getResource(iconPath).toExternalForm());
        icon.setFitHeight(24);
        icon.setFitWidth(24);
        HBox line = new HBox(12, icon, label);
        line.setAlignment(Pos.CENTER_LEFT);
        return line;
    }


    public Parent getRoot() {
        return root;
    }

    public Button getBackButton() {
        return backButton;
    }

    public Button getProfilePicButton() {
        return profilePicButton;
    }
}
