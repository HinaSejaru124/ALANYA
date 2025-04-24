module alanya {
    requires javafx.controls;
    requires javafx.fxml;
    requires webcam.capture;
    requires java.desktop; // Pour des API comme javax.sound.sampled (microphone)
    requires java.sql;

    opens alanya to javafx.graphics;
    opens video to javafx.graphics;
    exports alanya;
}
