// SignController.java
package controllers;

import java.io.File;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import views.MainApp;
import views.SignUI;
import views.ViewsMethods;

public class SignController implements ViewsMethods {
    private final SignUI ui;
    private final MainApp app;
    private String selectedProfileImage;

    public SignController(SignUI ui, MainApp app) {
        this.ui = ui;
        this.app = app;
    }

    public void start() {
        ui.getBackButton().setOnAction(e -> app.showLogin());

        ui.getChooseProfilePicButton().setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choisir une image de profil");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));

            File selectedFile = fileChooser.showOpenDialog(new Stage());
            if (selectedFile != null) {
                selectedProfileImage = selectedFile.toURI().toString();

                ImageView imageView = loadAnyImage(selectedProfileImage);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(false);
                imageView.setSmooth(true);

                Circle clip = new Circle(50, 50, 55);
                imageView.setClip(clip);

                SnapshotParameters parameters = new SnapshotParameters();
                parameters.setFill(Color.TRANSPARENT);
                WritableImage clippedImage = imageView.snapshot(parameters, null);

                ImageView finalView = new ImageView(clippedImage);
                finalView.setFitWidth(100);
                finalView.setFitHeight(100);

                ui.getChooseProfilePicButton().setGraphic(finalView);
            }
        });
    }

    public String getSelectedProfileImage() {
        return selectedProfileImage;
    }
}
