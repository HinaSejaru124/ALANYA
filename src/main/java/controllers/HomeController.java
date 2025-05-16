package controllers;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.stream.Stream;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import views.HomeUI;
import views.MainApp;
import views.Util;
import views.ViewsMethods;

public class HomeController implements ViewsMethods {
    private final HomeUI ui;
    private final MainApp app;
    private final KeyboardController keyboardController;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public HomeController(HomeUI ui, MainApp app) {
        this.ui = ui;
        this.app = app;
        this.keyboardController = new KeyboardController(app);
    }

    public void start() {
        ui.audioCallButton.setOnAction(e -> app.showCallUI());
        ui.closeButton.setOnAction(e -> ui.addContactBox.setVisible(false));
        ui.item1.setOnAction(e -> app.showInfosUser());
        ui.item2.setOnAction(e -> app.showCallUI());
        ui.videoCallButton.setOnAction(e -> app.showVideoCallUI());
        keyboardController.bindEscapeKey();

        Text textMeasure = new Text();
        textMeasure.setFont(ui.inputField.getFont());
        textMeasure.wrappingWidthProperty().bind(ui.inputField.widthProperty().subtract(10));
    }



    public Contact addContact(int id, String name, Boolean status, String imagePath) {
        Contact contact = new Contact(id, name, status, imagePath);
        contact.setOnMouseClicked(event -> onContactSelected(contact));
        ui.contactsBox.getChildren().add(contact);
        return contact;
    }

    private void onContactSelected(Contact contact) {
        app.interlocuteurId = contact.getUserId();

        Text contactName = new Text(contact.getName());
        contactName.setTranslateX(10);
        contactName.setTranslateY(6);
        contactName.setStyle("-fx-fill: black;-fx-font-size: 16px;");

        ui.userImage = ui.createIconButton(contact.getImagePath(), ui.ICON_SIZE);
        ui.userImage.setStyle("-fx-background-color: #AB6D00;"
                + "-fx-background-radius: 50%;"
                + "-fx-min-width:40;"
                + "-fx-min-height:40;"
                + "-fx-max-width:40;"
                + "-fx-max-width:40;");

        Text onlineStatus = new Text(contact.getStatus() ? "En ligne" : "Hors ligne");
        onlineStatus.setTranslateX(10);
        onlineStatus.setTranslateY(8);
        onlineStatus.setStyle("-fx-fill: black;-fx-font-size: 12px;");

        ui.header.getChildren().clear();
        ui.header.getChildren().addAll(ui.userImage, new VBox(contactName, onlineStatus), ui.spacerHeader,
                ui.audioCallButton,
                ui.videoCallButton);

        ui.messagesBox.getChildren().clear();

        BorderPane pane = new BorderPane();
        pane.setTop(ui.mainContent);
        pane.setCenter(ui.messageBoxBig);
        pane.setBottom(ui.inputBox);

        ui.conversZone.getChildren().clear();
        ui.conversZone.getChildren().add(pane);

        contact.getConversation().stream().forEach(msg -> {
            if (msg.getTypeMessage().equals("text"))
                addMessage(msg.getTextContent(), msg.getTime(), msg.isSentByUser());
            else
                addFile(msg.getFile(), msg.getTime(), msg.isSentByUser());
        });

        ui.inputField.setText(contact.getCache());
    }

    public final Contact addContact(int id, String name, Boolean status) {
        return addContact(id, name, status, "/icons/user.png");
    }

    private String formatDate(LocalDate date) {
    LocalDate today = LocalDate.now();
    if (date.equals(today)) {
        return "Aujourd'hui";
    } else if (date.equals(today.minusDays(1))) {
        return "Hier";
    } else if (date.isAfter(today.minusDays(7))) {
        return date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.FRENCH);
    } else {
        return date.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH));
    }
}


   public void addMessage(String message, LocalTime time, boolean isSentByUser) {
    LocalDate currentDate = LocalDate.now();

    // Affiche la date si elle n’a pas encore été affichée aujourd’hui
    if (!currentDate.equals(ui.lastDisplayedDate)) {
        ui.lastDisplayedDate = currentDate;

        Label dateLabel = new Label(formatDate(currentDate));
        dateLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 11px; -fx-padding: 5px;");
        dateLabel.setMaxWidth(Double.MAX_VALUE);
        dateLabel.setAlignment(Pos.CENTER);
        HBox dateContainer = new HBox(dateLabel);
        dateContainer.setAlignment(Pos.CENTER);

        ui.messagesBox.getChildren().add(dateContainer);
    }

    // Message bubble comme avant
    HBox bubbleContent = new HBox(1);
    bubbleContent.setAlignment(Pos.CENTER_RIGHT);

    Text messageText = new Text(message);
    messageText.setFont(setFontPerso("/fonts/Montserrat-Regular.ttf", 14));
    double maxWidth = 200;
    if (messageText.getBoundsInLocal().getWidth() > maxWidth) {
        messageText.setWrappingWidth(maxWidth);
    }

    Text timeStamp = new Text(time.format(TIME_FMT));
    timeStamp.setStyle("-fx-fill: #666666; -fx-font-size: 10px;");

    HBox bottomLine = new HBox(timeStamp);
    bottomLine.setAlignment(Pos.CENTER_RIGHT);

    VBox messageBubble = new VBox(1);
    messageBubble.setPadding(new Insets(10));
    messageBubble.setStyle("-fx-background-color: " + (isSentByUser ? "#F59C01" : "#FFFFFF") + ";"
            + (isSentByUser ? "-fx-background-radius: 15 0 15 15; -fx-border-radius: 15 0 15 15;"
                            : "-fx-background-radius: 0 15 15 15; -fx-border-radius: 0 15 15 15;"));

    bubbleContent.getChildren().add(messageText);
    messageBubble.getChildren().addAll(bubbleContent, bottomLine);

    HBox container = new HBox(messageBubble);
    container.setAlignment(isSentByUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
    ui.messagesBox.getChildren().add(container);
}


    public void addFile(File file, LocalTime time, boolean sentByUser) {
        if (isImage(file.getName()))
            afficherImage(file.getAbsolutePath(), time, sentByUser);
        else
            addClassicFile(file.getName(), time, sentByUser);
    }

    private void afficherImage(String path, LocalTime time, boolean sentByUser) {
        ImageView view = loadAnyImage(toFileURL(path));
        view.setPreserveRatio(true);
        view.setFitWidth(250);
        ui.messagesBox.getChildren().add(createMediaBubble(view, time, sentByUser));
    }

    private HBox createMediaBubble(Node content, LocalTime time, boolean sentByUser) {
        VBox fileBubble = new VBox(5);
        fileBubble.setPadding(new Insets(8));
        fileBubble.setStyle("-fx-background-color: " + (sentByUser ? "#F59C01" : "#FFFFFF") + ";"
                + "-fx-background-radius: 15; -fx-border-radius: 15;");

        HBox bottomLine = new HBox();
        bottomLine.setAlignment(Pos.CENTER_RIGHT);
        Text timeStamp = new Text(time.format(TIME_FMT));
        timeStamp.setStyle("-fx-fill: #667781; -fx-font-size: 11px;");
        bottomLine.getChildren().add(timeStamp);

        fileBubble.getChildren().addAll(content, bottomLine);

        fileBubble.setOnMouseEntered(
                e -> fileBubble.setStyle("-fx-background-color: " + (sentByUser ? "#BF7A01" : "#f5f5f5") + ";"
                        + "-fx-background-radius: 15; -fx-border-radius: 15;"));

        fileBubble.setOnMouseExited(
                e -> fileBubble.setStyle("-fx-background-color: " + (sentByUser ? "#F59C01" : "#FFFFFF") + ";"
                        + "-fx-background-radius: 15; -fx-border-radius: 15;"));

        fileBubble.setStyle(fileBubble.getStyle() + "-fx-cursor: hand;");

        HBox container = new HBox(fileBubble);
        container.setPadding(new Insets(5));
        container.setAlignment(sentByUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        return container;
    }

    public void addClassicFile(String fileName, LocalTime time, boolean sentByUser) {
        VBox fileBubble = new VBox(5);
        fileBubble.setPadding(new Insets(8));
        fileBubble.setStyle("-fx-background-color: " + (sentByUser ? "#F59C01" : "#FFFFFF") + ";"
                + "-fx-background-radius: 15; -fx-border-radius: 15;");

        HBox fileInfoLine = new HBox(10);
        ImageView fileIcon = isVideo(fileName) ? loadImage("/icons/video.png")
                : (isAudio(fileName) ? loadImage("/icons/audio.png") : loadImage("/icons/document-signed.png"));
        fileIcon.setFitWidth(35);
        fileIcon.setFitHeight(35);
        fileIcon.setPreserveRatio(true);

        VBox fileDetails = new VBox(2);
        Text fileNameText = new Text(fileName);

        double maxWidth = 200;
        if (fileNameText.getBoundsInLocal().getWidth() > maxWidth) {
            fileNameText.setWrappingWidth(maxWidth);
        }
        fileNameText.setStyle("-fx-font-size: 14px;");

        Text filedesc = new Text(isVideo(fileName) ? "Video" : (isAudio(fileName) ? "Audio" : "Document"));
        filedesc.setStyle("-fx-fill: #667781; -fx-font-size: 12px;");

        fileDetails.getChildren().addAll(fileNameText, filedesc);
        fileInfoLine.getChildren().addAll(fileIcon, fileDetails);

        HBox bottomLine = new HBox();
        bottomLine.setAlignment(Pos.CENTER_RIGHT);
        Text timeStamp = new Text(time.format(TIME_FMT));
        timeStamp.setStyle("-fx-fill: #667781; -fx-font-size: 11px;");
        bottomLine.getChildren().add(timeStamp);

        fileBubble.getChildren().addAll(fileInfoLine, bottomLine);

        fileBubble.setOnMouseEntered(
                e -> fileBubble.setStyle("-fx-background-color: " + (sentByUser ? "#BF7A01" : "#f5f5f5") + ";"
                        + "-fx-background-radius: 15; -fx-border-radius: 15;"));

        fileBubble.setOnMouseExited(
                e -> fileBubble.setStyle("-fx-background-color: " + (sentByUser ? "#F59C01" : "#FFFFFF") + ";"
                        + "-fx-background-radius: 15; -fx-border-radius: 15;"));

        fileBubble.setStyle(fileBubble.getStyle() + "-fx-cursor: hand;");

        HBox container = new HBox(fileBubble);
        container.setPadding(new Insets(5));
        container.setAlignment(sentByUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        ui.messagesBox.getChildren().add(container);
    }

    private static String toFileURL(String path) {
        return new File(path).toURI().toString();
    }

    private boolean isImage(String name) {
        return Stream.of(".jpg", ".jpeg", ".png").anyMatch(name::endsWith);
    }

    private boolean isVideo(String name) {
        return Stream.of(".mp4", ".mov", ".avi", ".gif").anyMatch(name::endsWith);
    }

    private boolean isAudio(String name) {
        return Stream.of(".mp3", ".m4a").anyMatch(name::endsWith);
    }

    public File chooseFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Enregistrer le fichier reçu");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File f = chooser.showOpenDialog(null);
        if (f == null)
            Util.showError("No file selected");
        return f;
    }
}