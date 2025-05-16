package controllers;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import views.MainApp;

public class KeyboardController {
    private final MainApp app;

    public KeyboardController(MainApp app) {
        this.app = app;
    }
    public void bindEscapeKey() {
        app.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                app.homeUI.conversZone.getChildren().removeIf(node -> node instanceof BorderPane);
                app.homeUI.conversZone.getChildren().add(app.homeUI.conversation);
            }
        });
    }
}