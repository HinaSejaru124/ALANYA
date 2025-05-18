package controllers;

import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class Contact extends HBox {
    private final int id;
    private final String name;
    private Boolean status;
    private final Label statusLabel;
    private final String imagePath;
    private final ArrayList<Message> conversation = new ArrayList<>();
    private String cache = "";

    public Contact(int id, String name, Boolean status, String imagePath) {
        super(10);
        this.id = id;
        this.name = name;
        this.status = status;
        this.imagePath = imagePath;

        setDisable(!status);

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
        nameLabel.setStyle("-fx-text-fill: #666666; ");
        nameLabel.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/Montserrat-Bold.ttf"), 16));

        statusLabel = new Label(status ? "En ligne" : "Hors ligne");
        statusLabel.setStyle("-fx-text-fill: #666666;");
        statusLabel.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/Montserrat-Regular.ttf"), 12));

        contactInfo.getChildren().addAll(nameLabel, statusLabel);

        getChildren().addAll(profilePic, contactInfo);
    }

    public int getUserId() {
        return id;
    }

    public ArrayList<Message> getConversation() {
        return conversation;
    }

    public String getName() {
        return name;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Label getStatusLabel() {
        return statusLabel;
    }

    public String getCache() {
        return cache;
    }

    public void setCache(String cache) {
        this.cache = cache;
    }
}