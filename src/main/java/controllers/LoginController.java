// LoginController.java
package controllers;

import views.LoginUI;
import views.MainApp;

public class LoginController {
    private final LoginUI ui;
    private final MainApp app;

    public LoginController(LoginUI ui, MainApp app) {
        this.ui = ui;
        this.app = app;
    }

    public void start() {
        ui.getLien().setOnMouseClicked(e -> app.showSignIn());
    }
}
