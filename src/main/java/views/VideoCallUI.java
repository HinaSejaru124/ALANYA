package views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class VideoCallUI implements ViewsMethods {

    private boolean videoActive = true;
    public StackPane videoLayer;
    public ImageView interlocuteurView;
    public ImageView selfView;
    public Button btnRaccrocher, btnToggleVideo;

    public VideoCallUI() {
        // Vue principale (interlocuteur)
        interlocuteurView = new ImageView();
        interlocuteurView.setFitWidth(800);
        interlocuteurView.setFitHeight(600);
        interlocuteurView.setPreserveRatio(true);
        interlocuteurView.setStyle("-fx-background-color: #000;");

        // Vue miniature (moi)
        selfView = new ImageView();
        selfView.setFitWidth(160);
        selfView.setFitHeight(120);
        selfView.setPreserveRatio(true);
        selfView.setStyle("-fx-border-color: white; -fx-border-width: 2; -fx-background-color: #333;");

        // Bouton raccrocher
        btnRaccrocher = createIconButton("/icons/hangup.png", 24);
        btnRaccrocher.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        btnRaccrocher.setOnAction(e -> System.out.println("Appel terminé"));

        // Bouton activer/désactiver vidéo
        btnToggleVideo = createIconButton("/icons/video-camera-blanc.png", 24);
        ImageView icon1 = loadImage("/icons/video-camera-blanc.png");
        icon1.setFitWidth(24);
        icon1.setFitHeight(24);
        ImageView icon2 = loadImage("/icons/video-slash-blanc.png");
        icon2.setFitWidth(24);
        icon2.setFitHeight(24);
        btnToggleVideo.setOnAction(e -> {
            videoActive = !videoActive;
            if (videoActive) {
                btnToggleVideo.setGraphic(icon1);
                System.out.println("Vidéo activée");
                selfView.setOpacity(1);
            } else {
                btnToggleVideo.setGraphic(icon2);
                System.out.println("Vidéo désactivée");
                selfView.setOpacity(0.2); // ou .setVisible(false)
            }
        });

        // Barre des boutons
        HBox buttonBar = new HBox(10, btnToggleVideo, btnRaccrocher);
        buttonBar.setAlignment(Pos.CENTER);
        StackPane.setMargin(buttonBar, new Insets(0, 300, 20, 300));
        buttonBar.setPadding(new Insets(10));
        buttonBar.setStyle("-fx-background-color:rgb(213, 110, 0); -fx-background-radius: 50px; -fx-min-height: 50px; -fx-max-height: 50px;");

        // Empilement des composants
        videoLayer = new StackPane(interlocuteurView);
        StackPane.setAlignment(buttonBar, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(selfView, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(selfView, new Insets(0, 20, 20, 0));

        videoLayer.getChildren().addAll(selfView, buttonBar);

    }

    public Parent getRoot() {
        return videoLayer;
    }
}

