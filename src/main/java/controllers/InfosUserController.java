package controllers;

import views.InfosUserUI;
import views.MainApp;
import views.ViewsMethods;

public class InfosUserController implements ViewsMethods {
    private final InfosUserUI ui;
    private final MainApp app;

    public InfosUserController(InfosUserUI ui, MainApp app) {
        this.ui = ui;
        this.app = app;
    }

    public void start() {
        ui.getBackButton().setOnAction(e -> app.showHome());
    }

    public void loadInfo(String path, String nom, String prenom, int id) {
        ui.getProfilePicButton().setGraphic(loadImage((path != null) ? path : "/icons/utilisateur.png"));
        ui.nameLabel.setText(ui.nameLabel.getText() + nom);
        ui.prenomLabel.setText(ui.prenomLabel.getText() + prenom);
        ui.idLabel.setText(ui.idLabel.getText() + id);
    }
}
