package alanya;

import java.io.File;
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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import message.MessageWriteThread;

public abstract class ChatApp extends Application
{
    private static final double ICON_SIZE = 24; // Taille des icônes
    private VBox messagesBox;
    private TextField inputField;
    private static StackPane root;

    private String filePath;

    protected Socket user;

    @SuppressWarnings("exports")
    @Override
    public void start(Stage primaryStage)
    {
        // Threads de communication
        MessageWriteThread messageSend = new MessageWriteThread(user);
        // MessageReadThread messageReceive = new MessageReadThread(user);
        FileSendThread fileSend = new FileSendThread(user);
        FileReceiveThread fileReceive = new FileReceiveThread(user);

        //messageSend.start();
        // messageReceive.start();
        //fileSend.start();
        fileReceive.start();

        // Barre supérieure (Header)
        HBox header = new HBox(10);
        header.setPadding(new Insets(10));
        header.setStyle("-fx-background-color: #37383B;");
        Text contactName = new Text("Contact Name");
        contactName.setStyle("-fx-fill: white; -fx-font-size: 16px;");
        Button userimage = createIconButton("/icons/user.png");
        userimage.setStyle("-fx-background-color: #B9BCC6; -fx-text-fill: white; -fx-background-radius: 40;");
        header.getChildren().addAll(userimage, contactName);

        // Zone des messages (ScrollPane)
        messagesBox = new VBox(5);
        messagesBox.setPadding(new Insets(10));
        messagesBox.setStyle("-fx-background-color: #ECE5DD;"); // Fond clair, comme WhatsApp

        VBox messageBoxBig = new VBox(5);
        messageBoxBig.setPadding(new Insets(5));
        messageBoxBig.setStyle("-fx-background-color: #ECE5DD;");

        ScrollPane scrollPane = new ScrollPane(messagesBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #ECE5DD;");

        messageBoxBig.getChildren().add(scrollPane);

        // Barre inférieure (Input)
        HBox inputBox = new HBox(10);
        inputBox.setPadding(new Insets(10));
        inputBox.setStyle("-fx-background-color: #37383B;");

        inputField = new TextField();
        inputField.setPromptText("Type a message...");
        inputField.setStyle("-fx-background-color: #464A4D; -fx-text-fill: white; -fx-border-radius: 20;");
        inputField.setPrefWidth(300);

        Button sendButton = createIconButton("/icons/paper-plane.png");
        sendButton.setStyle("-fx-background-color: #25D366; -fx-text-fill: white; -fx-background-radius: 15;");

        Button chooseFileButton = createIconButton("/icons/clip.png");
        chooseFileButton
                .setStyle("-fx-background-color: #075E54; -fx-text-fill: white; -fx-background-radius: 15;");

        inputBox.getChildren().addAll(inputField, sendButton, chooseFileButton);

        BorderPane conversZone = new BorderPane();
        conversZone.setTop(header);
        conversZone.setCenter(messageBoxBig);
        conversZone.setBottom(inputBox);

        // Actions des boutons
        sendButton.setOnAction(e -> {
            String message = inputField.getText();
            if (!message.isEmpty())
            {
                messageSend.send(message);
                addMessage(message, true); // Message envoyé
                inputField.clear();
            }
        });

        inputField.setOnAction(e -> {
            String message = inputField.getText();
            if (!message.isEmpty())
            {
                // messageSend.send(message);
                addMessage(message, true); // Message envoyé
                inputField.clear();
            }
        });

        chooseFileButton.setOnAction(e -> {
            chooseFile();
            if (filePath != null)
            {
                fileSend.send(filePath);
                fileSend.start();
            }
        });

        // Mise en page principale
        BorderPane root = new BorderPane();
        root.setCenter(conversZone);

        // Créer la scène
        Scene scene = new Scene(root, 400, 600);
        primaryStage.setTitle("Alanya");
        primaryStage.setScene(scene);
        primaryStage.show();

        // new Thread(() -> {
        //     String response;
        //     if ((response = messageReceive.receive()) != null) {
        //         Platform.runLater(() -> addMessage(response, false)); // Message reçu
        //     }
        // }).start();

    }

    // Ajouter un message dans la boîte de messages
    private void addMessage(String message, boolean isSentByUser)
    {
        HBox messageContainer = new HBox();
        messageContainer.setPadding(new Insets(5));

        VBox messageBox = new VBox(5); // Conteneur pour le message et l'heure

        TextFlow messageBubble = new TextFlow(new Text(message));
        messageBubble.setPadding(new Insets(10));
        messageBubble.setStyle("-fx-background-color: " + (isSentByUser ? "#DCF8C6" : "#FFFFFF") + ";"
                + "-fx-background-radius: 15; -fx-border-radius: 15;");

        String sender = (isSentByUser ? "Vous" : "Autre");
        TextFlow userName = new TextFlow(new Text(sender));
        userName.setPadding(new Insets(5));
        userName.setStyle("-fx-background-color : #ECE5DD");

        // Ajout de l'heure
        Text timeStamp = new Text(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeStamp.setStyle("-fx-fill: #666666; -fx-font-size: 10px;");

        messageBox.getChildren().addAll(userName, messageBubble, timeStamp);

        if (isSentByUser)
        {
            messageContainer.setAlignment(Pos.CENTER_RIGHT);
            messageBox.setAlignment(Pos.CENTER_RIGHT);
        }
        else
        {
            messageContainer.setAlignment(Pos.CENTER_LEFT);
            messageBox.setAlignment(Pos.CENTER_LEFT);
        }

        messageContainer.getChildren().add(messageBox);
        messagesBox.getChildren().add(messageContainer);
    }

    // Ajouter un fichier dans la boîte de messages
    private void addFile(String iconPath, String message, boolean isSentByUser)
    {
        HBox messageContainer = new HBox();
        messageContainer.setPadding(new Insets(5));

        VBox messageBox = new VBox(5);

        HBox contentBox = new HBox(5);

        ImageView fileIcon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
        fileIcon.setFitWidth(20);
        fileIcon.setFitHeight(20);
        fileIcon.setPreserveRatio(true);

        TextFlow messageBubble = new TextFlow(new Text(message));
        messageBubble.setPadding(new Insets(10));
        messageBubble.setStyle("-fx-background-color: " + (isSentByUser ? "#DCF8C6" : "#FFFFFF") + ";"
                + "-fx-background-radius: 15; -fx-border-radius: 15;");

        Text timeStamp = new Text(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeStamp.setStyle("-fx-fill: #666666; -fx-font-size: 10px;");

        if (isSentByUser)
        {
            contentBox.getChildren().addAll(messageBubble, fileIcon);
            messageBox.setAlignment(Pos.CENTER_RIGHT);
            messageContainer.setAlignment(Pos.CENTER_RIGHT);
        }
        else
        {
            contentBox.getChildren().addAll(fileIcon, messageBubble);
            messageBox.setAlignment(Pos.CENTER_LEFT);
            messageContainer.setAlignment(Pos.CENTER_LEFT);
        }

        messageBox.getChildren().addAll(contentBox, timeStamp);
        messageContainer.getChildren().add(messageBox);
        messagesBox.getChildren().add(messageContainer);
    }

    // Gestion de la sélection de fichier
    private void chooseFile()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null)
        {
            filePath = file.getAbsolutePath();
            addFile("/icons/document-signed.png", file.getName(), true);
        }
    }

    protected void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Créer un bouton avec une icône redimensionnée
    private Button createIconButton(String iconPath)
    {
        Button button = new Button();
        ImageView icon = new ImageView(loadImage(iconPath));
        icon.setFitWidth(ICON_SIZE);
        icon.setFitHeight(ICON_SIZE);
        button.setGraphic(icon);
        button.setStyle("-fx-background-color: transparent;");
        return button;
    }

    // Charger une image depuis les ressources
    private Image loadImage(String path)
    {
        return new Image(getClass().getResourceAsStream(path));
    }


    public static StackPane getRoot() {
        return root;
    }
}