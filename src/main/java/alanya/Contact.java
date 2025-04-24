package alanya;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Contact extends HBox {
    private final int id;

    public Contact(int id, String name, String status, String imagePath) {
        super(10);
        this.id = id;
        setPadding(new Insets(10));
        setStyle("-fx-background-color: white; -fx-cursor: hand;");

        // Effet de survol
        setOnMouseEntered(e -> setStyle("-fx-background-color: #f0f0f0; -fx-cursor: hand;"));
        setOnMouseExited(e -> setStyle("-fx-background-color: white; -fx-cursor: hand;"));

        // Image du contact
        ImageView profilePic = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
        profilePic.setFitHeight(40);
        profilePic.setFitWidth(40);
        profilePic.setStyle("-fx-background-radius: 50%;");

        // Informations du contact
        VBox contactInfo = new VBox(2);
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-weight: bold;-fx-text-fill: #666666; ");
        Label statusLabel = new Label(status);
        statusLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px;");
        contactInfo.getChildren().addAll(nameLabel, statusLabel);

        getChildren().addAll(profilePic, contactInfo);
    }

    public int getUserId() {
        return id;
    }
}
