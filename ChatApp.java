
package app;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
//import javafx.application.HostServices;
import javafx.scene.shape.SVGPath;



import filetransfer.fileReceiveThread;
import filetransfer.fileSendThread;

public class ChatApp extends Application {

    private static final double ICON_SIZE = 24; // Taille des icônes

    private ChatClient client;
    private VBox messagesBox;
    private VBox contactsBox;
    private ScrollPane scrollPane;
    private TextField inputField;
    private String filePath;


    private final String SERVER = "localhost";
    private final int PORT = 7000;

    @Override
    public void start(@SuppressWarnings("exports") Stage primaryStage) {
        try {
            client = new ChatClient(SERVER, PORT);
           // String saveDirectory = System.getProperty("user.home") + "/Downloads"; 
            fileReceiveThread receive = new fileReceiveThread(client.socket);
            receive.start();

            // Barre supérieure (Header)
            HBox header = new HBox();
            header.setPadding(new Insets(10));
            header.setStyle("-fx-background-color: #FFFFFF;");

            // Espaceur dynamique pour pousser le bouton à droite
            Region spacerHeader = new Region();
            HBox.setHgrow(spacerHeader, Priority.ALWAYS);

            Text contactName = new Text("Contact Name");
            contactName.setTranslateX(10);
            contactName.setTranslateY(8);
            contactName.setStyle("-fx-fill: black;-fx-font-size: 16px;");
            Button userimage = createIconButton("/icons/user.png");
            userimage.setStyle("-fx-background-color: #B9BCC6;" +
                               "-fx-background-radius: 50%;" +
                               "-fx-min-width:40;"+
                               "-fx-min-height:40;"+
                               "-fx-max-width:40;"+
                               "-fx-max-width:40;");

            Button videoCallButton = createIconButton("/icons/video-camera-alt.png");
            //videoCallButton.setStyle();
            //videoCallButton.setAlignment(Pos.TOP_LEFT);
            header.getChildren().addAll(userimage,contactName,spacerHeader, videoCallButton);

            // Zone des messages (ScrollPane)
            messagesBox = new VBox(1);
            messagesBox.setPadding(new Insets(10));
            messagesBox.setStyle("-fx-background-color: #ECE5DD;"); // Fond clair, comme WhatsApp

            VBox messageBoxBig = new VBox(1);
            messageBoxBig.setPadding(new Insets(10));
            messageBoxBig.setStyle("-fx-background-color: #ECE5DD;");


            // Fond d'écran dynamique
            BackgroundImage backgroundImage = new BackgroundImage(new Image(getClass().getResource("/backgrounds/chat-bg.jpg").toExternalForm()),
            BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            BackgroundSize.DEFAULT
    );
            messagesBox.setBackground(new Background(backgroundImage));

            scrollPane = new ScrollPane(messagesBox);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: #ECE5DD; " +
                    "-fx-hbar-policy: never; " + // Ne jamais afficher la barre horizontale
                    "-fx-vbar-policy: never; "); // Ne jamais afficher la barre verticale

            messageBoxBig.getChildren().add(scrollPane);

            //Zone de contacts 
            contactsBox = new VBox(10);
            contactsBox.setPrefWidth(250); // Largeur préférée
            contactsBox.setMinWidth(200); // Largeur minimale
            contactsBox.setPadding(new Insets(10));
            contactsBox.setStyle("-fx-background-color: #FFFFFF;");
            ScrollPane scrollPaneContact = new ScrollPane(contactsBox);
            scrollPaneContact.setFitToWidth(true);
            scrollPaneContact.setStyle("-fx-background-color: #FFFFFF;");


            // Barre inférieure (Input) avec effet flottant

            HBox inputBox = new HBox(2);
            inputBox.setPadding(new Insets(0, 5, 0 , 0)); // Espacement bas
            inputBox.setAlignment(Pos.CENTER);
            inputBox.setStyle("-fx-background-color: ECE5DD;");

            StackPane inputZone = new StackPane();
            inputZone.setPadding(new Insets(10, 10, 10, 10)); // Espacement bas
            inputZone.setAlignment(Pos.CENTER);
            inputZone.setStyle("-fx-background-color: ECE5DD;");


            // TextField stylisé (InputField)
            inputField = new TextField();
            inputField.setPromptText("Type a message...");
            inputField.setStyle("-fx-background-color: white; " +
                                "-fx-text-fill: #333333; " +
                                "-fx-font-size: 14px; " +
                                "-fx-border-color: transparent; " +
                                "-fx-background-radius: 30; " +
                                "-fx-border-radius: 30; " +
                                "-fx-padding: 10; " +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0.1, 0, 2);");
            HBox.setHgrow(inputField, Priority.ALWAYS);
            
            // Bouton d'envoi  des messages
            Button sendButton = createIconButton("/icons/paper-plane.png");
            sendButton.setStyle("-fx-background-color: #25D366; " + 
                    "-fx-background-radius: 50%; " +
                    "-fx-min-width: 50px; " +
                    "-fx-min-height: 50px; " +
                    "-fx-max-width: 50px; " +
                    "-fx-max-height: 50px; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0.1, 0, 2);");
            
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

            HBox.setHgrow(inputZone,Priority.ALWAYS);
            inputZone.getChildren().addAll(inputField,chooseFileButton);
           
            inputBox.getChildren().addAll(inputZone,sendButton);
            

            BorderPane conversZone = new BorderPane();
            conversZone.setTop(header);
            conversZone.setCenter(messageBoxBig);
            conversZone.setBottom(inputBox);
    


            // Actions des boutons
            sendButton.setOnAction(e -> {
                String message = inputField.getText();
                if (!message.isEmpty()) {
                    client.sendMessage(message);
                    addMessage(message, true); // Message envoyé
                    inputField.clear();
                }
            });

            chooseFileButton.setOnAction(e -> chooseFile());




            // Mise en page principale
            SplitPane root = new SplitPane();
            root.getItems().addAll(conversZone);
            //root.setDividerPositions(0.3); // 30% pour les contacts
            root.setStyle("-fx-background-color: transparent; -fx-margin:0");
           

            // Créer la scène
            Scene scene = new Scene(root, 400, 600);
            primaryStage.setTitle("Chat App");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

            // Thread pour recevoir les messages
            new Thread(() -> {
                try {
                    String response;
                    while ((response = client.receiveMessage()) != null) {
                        String finalResponse = response;
                        Platform.runLater(() -> addMessage(finalResponse, false)); // Message reçu
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            showError("Impossible de se connecter au serveur");
        }
    }

    // Ajouter un message dans la boîte de messages
    private void addMessage(String message, boolean isSentByUser) {
        HBox messageContainer = new HBox();
        messageContainer.setPadding(new Insets(5));
        
        VBox messageBox = new VBox(2);
        
        // Création d'un HBox pour contenir le message et l'heure
        HBox bubbleContent = new HBox(1);

        HBox bottomLine = new HBox(1);
        bottomLine.setAlignment(Pos.CENTER_RIGHT);
        
        Text messageText = new Text(message);
    

        // Wrapping pour les longs messages uniquement
        double maxWidth = 250; // Limite de largeur maximale en pixels
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
                + (isSentByUser? "-fx-background-radius: 15 0 15 15; -fx-border-radius: 15 0 15 15;" : "-fx-background-radius: 0 15 15 15; -fx-border-radius: 0 15 15 15;"));
    
        String user = (isSentByUser ? "" : "");
        TextFlow userName = new TextFlow(new Text(user));
        userName.setPadding(new Insets(5));
        userName.setStyle("-fx-background-color : #ECE5DD");

        Region spacer = new Region();
        spacer.minHeight(1);

        messageBubble.getChildren().addAll(bubbleContent,spacer,bottomLine);
        messageBox.getChildren().addAll(userName, messageBubble);
    
        if (isSentByUser) {
            messageContainer.setAlignment(Pos.CENTER_RIGHT);
            messageBox.setAlignment(Pos.CENTER_RIGHT);
            // Fait défiler le ScrollPane vers le bas
            Platform.runLater(() -> scrollPane.setVvalue(2.0));
        } else {
            messageContainer.setAlignment(Pos.CENTER_LEFT);
            messageBox.setAlignment(Pos.CENTER_LEFT);
            // Fait défiler le ScrollPane vers le bas
            Platform.runLater(() -> scrollPane.setVvalue(2.0));
        }
    
        messageContainer.getChildren().add(messageBox);
        messagesBox.getChildren().add(messageContainer);


    
    }



   /*private void addMessage(String message, boolean isSentByUser) {
    HBox messageContainer = new HBox();
    messageContainer.setPadding(new Insets(5));

    // Conteneur de la bulle (avec SVG personnalisé)
    StackPane bubbleContainer = new StackPane();
    bubbleContainer.setPadding(new Insets(10));
    bubbleContainer.setMaxWidth(300);

    // Texte du message
    Text messageText = new Text(message);
    messageText.setWrappingWidth(280);
    messageText.setFill(Color.BLACK);
    messageText.setStyle("-fx-font-size: 14px;");

    // Heure (en bas à droite)
    Text timeStamp = new Text(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
    timeStamp.setStyle("-fx-fill: #666666; -fx-font-size: 10px;");

    HBox bottomLine = new HBox(timeStamp);
    bottomLine.setAlignment(Pos.CENTER_RIGHT);
    bottomLine.setPadding(new Insets(0, 0, 0, 5));

    // Conteneur du texte et de l'heure
    VBox contentBox = new VBox(2);
    contentBox.getChildren().addAll(messageText, bottomLine);

    // Forme de la bulle avec SVGPath
    SVGPath bubbleShape = new SVGPath();
    if (isSentByUser) {
        // Forme pour messages envoyés (pointe à droite)
        bubbleShape.setContent("M 0 10 Q 0 0 10 0 H 280 Q 290 0 290 10 V 60 Q 290 70 280 70 H 20 L 10 80 Z");
        bubbleShape.setFill(Color.web("#DCF8C6")); // Vert foncé
    } else {
        // Forme pour messages reçus (pointe à gauche)
        bubbleShape.setContent("M 10 0 H 280 Q 290 0 290 10 V 60 Q 290 70 280 70 H 10 L 0 80 Z");
        bubbleShape.setFill(Color.web("#FFFFFF")); // Blanc
        bubbleShape.setStroke(Color.web("#E5E5E5")); // Bordure légère
        bubbleShape.setStrokeWidth(1);
    }

    // Ajout des éléments à la bulle
    bubbleContainer.getChildren().addAll(bubbleShape, contentBox);

    // Gestion de l'alignement
    if (isSentByUser) {
        messageContainer.setAlignment(Pos.CENTER_RIGHT);
    } else {
        messageContainer.setAlignment(Pos.CENTER_LEFT);
    }

    messageContainer.getChildren().add(bubbleContainer);
    messagesBox.getChildren().add(messageContainer);
}*/

    // Ajouter un fichier dans la boîte de messages
    private void addFile(String iconPath, String fileName, boolean isSentByUser) {
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
        fileBubble.setOnMouseEntered(e -> 
            fileBubble.setStyle("-fx-background-color: " + (isSentByUser ? "#c5e1b0" : "#f5f5f5") + ";"
                    + "-fx-background-radius: 15; -fx-border-radius: 15;"));
        
        fileBubble.setOnMouseExited(e -> 
            fileBubble.setStyle("-fx-background-color: " + (isSentByUser ? "#DCF8C6" : "#FFFFFF") + ";"
                    + "-fx-background-radius: 15; -fx-border-radius: 15;"));
        
        // Ajout d'un curseur pointer pour indiquer que c'est cliquable
        fileBubble.setStyle(fileBubble.getStyle() + "-fx-cursor: hand;");
        
        // Gestion de l'alignement
        if (isSentByUser) {
            messageContainer.setAlignment(Pos.CENTER_RIGHT);
            messageBox.setAlignment(Pos.CENTER_RIGHT);
            Platform.runLater(() -> scrollPane.setVvalue(2.0));
        } else {
            messageContainer.setAlignment(Pos.CENTER_LEFT);
            messageBox.setAlignment(Pos.CENTER_LEFT);
            Platform.runLater(() -> scrollPane.setVvalue(2.0));
        }
        
        messageContainer.getChildren().add(messageBox);
        messagesBox.getChildren().add(messageContainer);
        
        // Option : Ajouter un effet de clic pour ouvrir le fichier
       /* fileBubble.setOnMouseClicked(e -> {
            if (filePath != null) {
                try {
                    File file = new File(filePath);
                    if (file.exists()) {
                        HostServices.show(file.toURI().toString());
                    }
                } catch (IOException ex) {
                    showError("Impossible d'ouvrir le fichier");
                }
            }
        });*/
    }


    // Gestion de la sélection de fichier
    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            filePath = file.getAbsolutePath();
            addFile("/icons/document-signed.png",file.getName(), true);

        /*Envoi du fichier
        new Thread(() -> {
            fileSendThread fileSender = new fileSendThread(client.socket);
            fileSender.sendFile(filePath);
        }).start();
        */

        } else {
            showError("No file selected");
        }
    }
    

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
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

    public static void main(String[] args) {
        launch(args);
    }
}



/*Reprends ce code, custome ca bien pour que ca ressemble a whatsapp, genre lorsqu'un message est envoye , il faut qu'il yait la bulle comme sur whatsapp, et aussi fais une analyse du code , package app;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import filetransfer.fileReceiveThread;
import filetransfer.fileSendThread;

public class ChatApp extends Application {
    private ChatClient client;
    private TextArea messagesArea;
    private TextField inputField;
    private String filePath;

    private final String SERVER = "192.168.43.126";
    private final int PORT = 9000;

    @Override
    public void start(@SuppressWarnings("exports") Stage primaryStage) {
        try {
            client = new ChatClient(SERVER, PORT);
            fileReceiveThread receive = new fileReceiveThread(client.socket);
            receive.start();

            // Barre supérieure (Header)
            HBox header = new HBox(10);
            header.setPadding(new Insets(10));
            header.setStyle("-fx-background-color: #37383B;");
            Text contactName = new Text("Contact Name");
            contactName.setStyle("-fx-fill: white; -fx-font-size: 16px;");
            
            // Barre inférieure (Input)
            HBox inputBox = new HBox(10);
            inputBox.setPadding(new Insets(10));
            inputBox.setStyle("-fx-background-color: #37383B;");


            messagesArea = new TextArea();
            messagesArea.setEditable(false);

            inputField = new TextField();
            inputField.setOnAction(e -> {
                String message = inputField.getText();
                if (!message.isEmpty()) {
                    client.sendMessage(message);
                    inputField.clear();
                }
            });

            inputField.setStyle("-fx-background-color: #464A4D; -fx-text-fill: white; -fx-border-radius: 15 px");
        
        Button chooseFileButton = new Button("Choisir un fichier");
        Button sendFileButton = new Button("Envoyer le fichier");

        // Action when 'Choose File' button is clicked
        chooseFileButton.setOnAction(e -> chooseFile());

        // Action when 'Send File' button is clicked
        sendFileButton.setOnAction(e -> {
                fileSendThread send = new fileSendThread(client.socket, filePath);
                send.start();
        });
            //VBox layout = new VBox(10, messagesArea, inputField, new HBox(chooseFileButton, sendFileButton));
            
            //Add Button on a Footer
            inputBox.getChildren().addAll(inputField, chooseFileButton, sendFileButton);

            // Main 
            BorderPane root = new BorderPane();
            root.setTop(header);
            root.setCenter(messagesArea);
            root.setBottom(inputBox);


            //Create the scene
            Scene scene = new Scene(root, 400, 400);
            primaryStage.setTitle("Chat App");
            primaryStage.setScene(scene);
            primaryStage.show();

            new Thread(() -> {
                try {
                    String response;
                    while ((response = client.receiveMessage()) != null) {
                        String finalResponse = response;
                        javafx.application.Platform.runLater(() -> messagesArea.appendText(finalResponse + "\n"));
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            showError("Impossible de se connecter au serveur");
        }
    }

    // Open FileChooser to select a file
    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = fileChooser.showOpenDialog(null);
        filePath = file.getAbsolutePath();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}*/
