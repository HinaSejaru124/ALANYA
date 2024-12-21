module alanya {
    requires javafx.controls;
    requires javafx.fxml;
    // requires javafx.media; // Pour gérer l'audio/vidéo
    requires java.desktop; // Pour des API comme javax.sound.sampled (microphone)

    opens alanya to javafx.fxml;
    exports alanya;
}
