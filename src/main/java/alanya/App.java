package alanya;

import javafx.stage.Stage;

public class App {
    private View view;
    private Controller controller;

    public App() {
        view = new View();
        // controller = new Controller(view);
    }

    public static void main(String[] args) {
        App app = new App();
        app.view.start(new Stage());
    }
}
