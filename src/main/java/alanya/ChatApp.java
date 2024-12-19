package alanya;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import file.FileReceiveThread;
import file.FileSendThread;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public abstract class ChatApp extends Application {

    private static final double ICON_SIZE = 24; // Taille des icônes
    private VBox messagesBox;
    private TextField inputField;

    private String filePath;

    protected Socket user;
    protected String username;

    @SuppressWarnings("exports")
    @Override
    public void start(Stage primaryStage) throws IOException
    {
        // Threads de communication
        // MessageWriteThread messageSend = new MessageWriteThread(user);
        // messageSend.start();
        // MessageReadThread messageReceive = new MessageReadThread(user);
        // messageReceive.start();
            
        FileSendThread fileSend = new FileSendThread(user);
        fileSend.start();
        new FileReceiveThread(user, this).start();


        // Barre supérieure (Header)
        HBox header = new HBox();
        header.setPadding(new Insets(10));
        header.setStyle("-fx-background-color: #FFFFFF;"); // #37383B

        // Espaceur dynamique pour pousser le bouton à droite
        Region spacerHeader = new Region();
        HBox.setHgrow(spacerHeader, Priority.ALWAYS);

        Text contactName = new Text(username);
        contactName.setTranslateX(10);
        contactName.setTranslateY(8);
        contactName.setStyle("-fx-fill: black;-fx-font-size: 16px;");
        Button userimage = createIconButton("/icons/user.png");
        userimage.setStyle("-fx-background-color: #B9BCC6;"
                + "-fx-background-radius: 50%;"
                + "-fx-min-width:40;"
                + "-fx-min-height:40;"
                + "-fx-max-width:40;"
                + "-fx-max-width:40;");

        Button videoCallButton = createIconButton("/icons/video-camera-alt.png");
        //videoCallButton.setStyle();
        //videoCallButton.setAlignment(Pos.TOP_LEFT);
        header.getChildren().addAll(userimage, contactName, spacerHeader, videoCallButton);

        // Zone des messages (ScrollPane)
        messagesBox = new VBox(2);
        messagesBox.setPadding(new Insets(2));
        messagesBox.setStyle("-fx-background-color: #ECE5DD;"); // Fond clair, comme WhatsApp

        VBox messageBoxBig = new VBox(10);
        messageBoxBig.setPadding(new Insets(10));
        messageBoxBig.setStyle("-fx-background-color: #ECE5DD;");

        ScrollPane scrollPane = new ScrollPane(messagesBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #ECE5DD; "
                + "-fx-hbar-policy: never; "
                + // Ne jamais afficher la barre horizontale
                "-fx-vbar-policy: never; "); // Ne jamais afficher la barre verticale

        messageBoxBig.getChildren().add(scrollPane);

        // Barre inférieure (Input) avec effet flottant
        HBox inputBox = new HBox(2);
        inputBox.setPadding(new Insets(0, 5, 0, 0)); // Espacement bas
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setStyle("-fx-background-color: ECE5DD;");

        StackPane inputZone = new StackPane();
        inputZone.setPadding(new Insets(10, 10, 10, 10)); // Espacement bas
        inputZone.setAlignment(Pos.CENTER);
        inputZone.setStyle("-fx-background-color: ECE5DD;");

        // TextField stylisé (InputField)
        inputField = new TextField();
        inputField.setPromptText("Type a message...");
        inputField.setStyle("-fx-background-color: white; "
                + "-fx-text-fill: #333333; "
                + "-fx-font-size: 14px; "
                + "-fx-border-color: transparent; "
                + "-fx-background-radius: 30; "
                + "-fx-border-radius: 30; "
                + "-fx-padding: 10; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0.1, 0, 2);");
        HBox.setHgrow(inputField, Priority.ALWAYS);

        // Bouton d'envoi  des messages
        Button sendButton = createIconButton("/icons/paper-plane.png");
        sendButton.setStyle("-fx-background-color: #25D366; "
                + "-fx-background-radius: 50%; "
                + "-fx-min-width: 50px; "
                + "-fx-min-height: 50px; "
                + "-fx-max-width: 50px; "
                + "-fx-max-height: 50px; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0.1, 0, 2);");

        // Bouton d'envoie de fichiers
        Button chooseFileButton = createIconButton("/icons/clip.png");

        // Espacement automatique pour un alignement parfait
        inputField.setPrefWidth(1000);
        sendButton.setPrefWidth(60);
        sendButton.setPrefHeight(60);
        chooseFileButton.setPrefWidth(50);

        // Positionnement du bouton (overlay) dans la StackPane
        //sendButton.setTranslateX(230); // Alignement en haut à droite
        //StackPane.setMargin(sendButton, new Insets(10));   // Marges pour l'espacement
        chooseFileButton.setTranslateX(140);
        StackPane.setMargin(chooseFileButton, new Insets(10));   // Marges pour l'espacement

        HBox.setHgrow(inputZone, Priority.ALWAYS);
        inputZone.getChildren().addAll(inputField, chooseFileButton);

        inputBox.getChildren().addAll(inputZone, sendButton);

        BorderPane conversZone = new BorderPane();
        conversZone.setTop(header);
        conversZone.setCenter(messageBoxBig);
        conversZone.setBottom(inputBox);

        // Actions des boutons
        sendButton.setOnAction(e -> {
            String message = inputField.getText();
            if (!message.isEmpty()) {
                // messageSend.send(message);
                addMessage(message, true); // Message envoyé
                inputField.clear();
            }
        });

        inputField.setOnAction(e -> {
            String message = inputField.getText();
            if (!message.isEmpty()) {
                // messageSend.send(message);
                addMessage(message, true); // Message envoyé
                inputField.clear();
            }
        });

        chooseFileButton.setOnAction(e -> {
            chooseFile();
            if (filePath != null) {
                fileSend.sendFile(filePath);
            }
        });

        // Mise en page principale
        SplitPane root = new SplitPane();
        root.getItems().addAll(conversZone);
        //root.setDividerPositions(0.3); // 30% pour les contacts
        root.setStyle("-fx-background-color: transparent; -fx-margin:0");

        // Créer la scène
        Scene scene = new Scene(root, 400, 600);
        primaryStage.setTitle("Alanya");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // new Thread(() -> {
        //     String response;
        //     if ((response = messageReceive.receive()) != null) {
        //         Platform.runLater(() -> addMessage(response, false)); // Message reçu
        //     }
        // }).start();
    }

    // Ajouter un message dans la boîte de messages
    public void addMessage(String message, boolean isSentByUser) {
        HBox messageContainer = new HBox();
        messageContainer.setPadding(new Insets(1));

        VBox messageBox = new VBox(2);

        // Création d'un HBox pour contenir le message et l'heure
        HBox bubbleContent = new HBox(1);

        HBox bottomLine = new HBox(1);
        bottomLine.setAlignment(Pos.CENTER_RIGHT);

        Text messageText = new Text(message);

        // Wrapping pour les longs messages uniquement
        double maxWidth = 200; // Limite de largeur maximale en pixels
        if (messageText.getBoundsInLocal().getWidth() > maxWidth) {
            messageText.setWrappingWidth(maxWidth); // Active le retour à la ligne si le texte dépasse la largeur
        }
        Text timeStamp = new Text(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeStamp.setStyle("-fx-fill: #666666; -fx-font-size: 10px;");

        bubbleContent.getChildren().addAll(messageText);
        bubbleContent.setAlignment(Pos.CENTER_RIGHT);

        bottomLine.getChildren().add(timeStamp);

        VBox messageBubble = new VBox(1);
        messageBubble.setPadding(new Insets(10));
        messageBubble.setStyle("-fx-background-color: " + (isSentByUser ? "#DCF8C6" : "#FFFFFF") + ";"
                + (isSentByUser ? "-fx-background-radius: 15 0 15 15; -fx-border-radius: 15 0 15 15;" : "-fx-background-radius: 0 15 15 15; -fx-border-radius: 0 15 15 15;"));

        String user = (isSentByUser ? "" : "");
        TextFlow userName = new TextFlow(new Text(user));
        userName.setPadding(new Insets(5));
        userName.setStyle("-fx-background-color : #ECE5DD");

        Region spacer = new Region();
        spacer.minHeight(1);

        messageBubble.getChildren().addAll(bubbleContent, spacer, bottomLine);
        messageBox.getChildren().addAll(userName, messageBubble);

        if (isSentByUser) {
            messageContainer.setAlignment(Pos.CENTER_RIGHT);
            messageBox.setAlignment(Pos.CENTER_RIGHT);
        } else {
            messageContainer.setAlignment(Pos.CENTER_LEFT);
            messageBox.setAlignment(Pos.CENTER_LEFT);
        }

        messageContainer.getChildren().add(messageBox);
        messagesBox.getChildren().add(messageContainer);
    }

    // Ajouter un fichier dans la boîte de messages
    public void addFile(String iconPath, String fileName, boolean isSentByUser) {
        HBox messageContainer = new HBox();
        messageContainer.setPadding(new Insets(5));

        VBox messageBox = new VBox(2);

        // Conteneur principal de la bulle
        VBox fileBubble = new VBox(5);
        fileBubble.setPadding(new Insets(8));
        fileBubble.setStyle("-fx-background-color: " + (isSentByUser ? "#DCF8C6" : "#FFFFFF") + ";"
                + "-fx-background-radius: 15; -fx-border-radius: 15;");

        // Ligne supérieure avec icône et nom du fichier
        HBox fileInfoLine = new HBox(10);

        ImageView fileIcon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
        fileIcon.setFitWidth(35);
        fileIcon.setFitHeight(35);
        fileIcon.setPreserveRatio(true);

        // Zone de texte avec nom du fichier
        VBox fileDetails = new VBox(2);
        Text fileNameText = new Text(fileName);

        // Wrapping pour les longs messages uniquement
        double maxWidth = 250; // Limite de largeur maximale en pixels
        if (fileNameText.getBoundsInLocal().getWidth() > maxWidth)
            fileNameText.setWrappingWidth(maxWidth); // Active le retour à la ligne si le texte dépasse la largeur

        fileNameText.setStyle("-fx-font-size: 14px;");

        // Taille du fichier (exemple)
        Text fileSize = new Text("Document");
        fileSize.setStyle("-fx-fill: #667781; -fx-font-size: 12px;");

        fileDetails.getChildren().addAll(fileNameText, fileSize);
        fileInfoLine.getChildren().addAll(fileIcon, fileDetails);

        // Ligne inférieure avec l'heure
        HBox bottomLine = new HBox();
        bottomLine.setAlignment(Pos.CENTER_RIGHT);
        Text timeStamp = new Text(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeStamp.setStyle("-fx-fill: #667781; -fx-font-size: 11px;");
        bottomLine.getChildren().add(timeStamp);

        // Séparateur
        Region spacer = new Region();
        spacer.setMinHeight(5);

        fileBubble.getChildren().addAll(fileInfoLine, spacer, bottomLine);

        String user = (isSentByUser ? "" : "");
        TextFlow userName = new TextFlow(new Text(user));
        userName.setPadding(new Insets(5));
        userName.setStyle("-fx-background-color : #ECE5DD");

        messageBox.getChildren().addAll(userName, fileBubble);

        // Ajout d'un effet de survol pour indiquer que c'est cliquable
        fileBubble.setOnMouseEntered(e
                -> fileBubble.setStyle("-fx-background-color: " + (isSentByUser ? "#c5e1b0" : "#f5f5f5") + ";"
                        + "-fx-background-radius: 15; -fx-border-radius: 15;"));

        fileBubble.setOnMouseExited(e
                -> fileBubble.setStyle("-fx-background-color: " + (isSentByUser ? "#DCF8C6" : "#FFFFFF") + ";"
                        + "-fx-background-radius: 15; -fx-border-radius: 15;"));

        // Ajout d'un curseur pointer pour indiquer que c'est cliquable
        fileBubble.setStyle(fileBubble.getStyle() + "-fx-cursor: hand;");

        // Gestion de l'alignement
        if (isSentByUser) {
            messageContainer.setAlignment(Pos.CENTER_RIGHT);
            messageBox.setAlignment(Pos.CENTER_RIGHT);
        } else {
            messageContainer.setAlignment(Pos.CENTER_LEFT);
            messageBox.setAlignment(Pos.CENTER_LEFT);
        }

        messageContainer.getChildren().add(messageBox);
        messagesBox.getChildren().add(messageContainer);
    }

    // Gestion de la sélection de fichier
    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            filePath = file.getAbsolutePath();
            addFile("/icons/document-signed.png", file.getName(), true);
        } else {
            showError("No file selected");
        }
    }

    // Créer un bouton avec une icône redimensionnée
    private Button createIconButton(String iconPath) {
        Button button = new Button();
        ImageView icon = new ImageView(loadImage(iconPath));
        icon.setFitWidth(ICON_SIZE);
        icon.setFitHeight(ICON_SIZE);
        button.setGraphic(icon);
        button.setStyle("-fx-background-color: transparent;");
        return button;
    }

    // Charger une image depuis les ressources
    private Image loadImage(String path) {
        return new Image(getClass().getResourceAsStream(path));
    }

    protected void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
