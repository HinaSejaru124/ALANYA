module alanya {
    requires javafx.controls;
    requires javafx.fxml;

    opens alanya to javafx.fxml;
    exports alanya;
}
