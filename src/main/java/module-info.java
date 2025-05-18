module alanya {
    requires transitive javafx.controls;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.swing;
    requires java.desktop;
    requires java.prefs;

    requires java.sql;
    
    requires webcam.capture;
    requires java.logging;

    opens views to javafx.graphics;
    opens alanya to javafx.graphics, javafx.media;
    opens video to javafx.graphics;
    
    exports alanya;
}
