package controllers;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.stream.Stream;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import views.HomeUI;
import views.MainApp;
import views.Util;
import views.ViewsMethods;

public class HomeController implements ViewsMethods {
    private final HomeUI ui;
    private final MainApp app;
    private final KeyboardController keyboardController;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    
    // Couleurs pour le thème principal (ces valeurs peuvent être déplacées dans un fichier de constantes)
    private final String SENT_MESSAGE_COLOR = "#F59C01";
    private final String SENT_MESSAGE_HOVER_COLOR = "#BF7A01";
    private final String RECEIVED_MESSAGE_COLOR = "#FFFFFF";
    private final String RECEIVED_MESSAGE_HOVER_COLOR = "#F5F5F5";

    public HomeController(HomeUI ui, MainApp app) {
        this.ui = ui;
        this.app = app;
        this.keyboardController = new KeyboardController(app);
    }

    public void start() {
        // Configuration des gestionnaires d'événements avec animations
        ui.audioCallButton.setOnAction(e -> {
            animateButtonClick(ui.audioCallButton);
            app.showAudioCallUI();
        });
        
        ui.closeButton.setOnAction(e -> {
            animateButtonClick(ui.closeButton);
            animateDialogClose(ui.addContactBox);
        });
        
        ui.profileButton.setOnAction(e -> {
            animateButtonClick(ui.profileButton);
            app.showInfosUser();
        });
        
        ui.videoCallButton.setOnAction(e -> {
            animateButtonClick(ui.videoCallButton);
            app.showVideoCallUI();
        });
        
        ui.sendButton.setOnAction(e -> {
            animateSendButton();
            // Le code pour envoyer le message serait ici
        });
        
        ui.chooseFileButton.setOnAction(e -> {
            animateButtonClick(ui.chooseFileButton);
            File file = chooseFile();
            if (file != null && !isImage(file.getName())) {
                // Les fichiers non-image sont envoyés directement
                addFile(file, LocalTime.now(), true);
            }
        });
        
        keyboardController.bindEscapeKey();

        Text textMeasure = new Text();
        textMeasure.setFont(ui.inputField.getFont());
        textMeasure.wrappingWidthProperty().bind(ui.inputField.widthProperty().subtract(10));
        
        // Animation d'entrée pour l'interface de démarrage
        animateStartupInterface();
    }
    
    /**
     * Anime l'interface au démarrage de l'application
     */
    private void animateStartupInterface() {
        // Animer les éléments principaux avec un délai progressif
        double delay = 0.1;
        
        for (Node child : ui.contactsBox.getChildren()) {
            FadeTransition fade = new FadeTransition(Duration.seconds(0.5), child);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setDelay(Duration.seconds(delay));
            fade.play();
            
            TranslateTransition translate = new TranslateTransition(Duration.seconds(0.4), child);
            translate.setFromX(-30);
            translate.setToX(0);
            translate.setDelay(Duration.seconds(delay));
            translate.play();
            
            delay += 0.05;
        }
        
        // Animation du titre de l'application
        ScaleTransition scaleLogo = new ScaleTransition(Duration.seconds(0.8), ui.conversZone);
        scaleLogo.setFromX(0.95);
        scaleLogo.setFromY(0.95);
        scaleLogo.setToX(1);
        scaleLogo.setToY(1);
        scaleLogo.play();
        
        FadeTransition fadeLogo = new FadeTransition(Duration.seconds(1), ui.conversZone);
        fadeLogo.setFromValue(0.5);
        fadeLogo.setToValue(1);
        fadeLogo.play();
    }

    /**
     * Ajoute un contact avec une animation d'entrée
     */
    public Contact addContact(int id, String name, Boolean status, String imagePath) {
        Contact contact = new Contact(id, name, status, imagePath);
        contact.setOnMouseClicked(event -> {
            animateContactSelection(contact);
            onContactSelected(contact);
        });
        
        // Configuration des animations de survol
        contact.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), contact);
            scale.setToX(1.02);
            scale.setToY(1.02);
            scale.play();
            
            contact.setStyle("-fx-background-color: #f5f5f5; -fx-cursor: hand;");
        });
        
        contact.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), contact);
            scale.setToX(1);
            scale.setToY(1);
            scale.play();
            
            contact.setStyle("-fx-background-color: white; -fx-cursor: hand;");
        });
        
        // Animation d'entrée du contact
        contact.setOpacity(0);
        contact.setTranslateY(20);
        
        ui.contactsBox.getChildren().add(contact);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), contact);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(400), contact);
        slideUp.setFromY(20);
        slideUp.setToY(0);
        
        ParallelTransition transition = new ParallelTransition(fadeIn, slideUp);
        transition.play();
        
        return contact;
    }
    
    /**
     * Anime la sélection d'un contact
     */
    private void animateContactSelection(Contact contact) {
        // Animation de focus sur le contact sélectionné
        FadeTransition fadeHighlight = new FadeTransition(Duration.millis(200), contact);
        fadeHighlight.setFromValue(1);
        fadeHighlight.setToValue(0.7);
        fadeHighlight.setCycleCount(2);
        fadeHighlight.setAutoReverse(true);
        fadeHighlight.play();
        
        // Animation de légère pulsation
        ScaleTransition pulse = new ScaleTransition(Duration.millis(200), contact);
        pulse.setFromX(1);
        pulse.setFromY(1);
        pulse.setToX(1.05);
        pulse.setToY(1.05);
        pulse.setCycleCount(2);
        pulse.setAutoReverse(true);
        pulse.play();
    }

    /**
     * Gère la sélection d'un contact avec animations
     */
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

        // Nettoyer le contenu précédent avec une animation de fondu
        FadeTransition fadeOutHeader = new FadeTransition(Duration.millis(150), ui.header);
        fadeOutHeader.setFromValue(1);
        fadeOutHeader.setToValue(0);
        fadeOutHeader.setOnFinished(e -> {
            // Une fois le fondu terminé, remplacer le contenu
            ui.header.getChildren().clear();
            ui.header.getChildren().addAll(ui.userImage, new VBox(contactName, onlineStatus), ui.spacerHeader,
                    ui.audioCallButton, ui.videoCallButton);
            
            // Puis faire apparaître le nouveau contenu
            FadeTransition fadeInHeader = new FadeTransition(Duration.millis(200), ui.header);
            fadeInHeader.setFromValue(0);
            fadeInHeader.setToValue(1);
            fadeInHeader.play();
        });
        fadeOutHeader.play();

        // Animation pour la zone de messages
        FadeTransition fadeOutMessages = new FadeTransition(Duration.millis(150), ui.messagesBox);
        fadeOutMessages.setFromValue(1);
        fadeOutMessages.setToValue(0);
        fadeOutMessages.setOnFinished(e -> {
            ui.messagesBox.getChildren().clear();
            ui.lastDisplayedDate = null;
            
            BorderPane pane = new BorderPane();
            pane.setTop(ui.mainContent);
            pane.setCenter(ui.messageBoxBig);
            pane.setBottom(ui.inputBox);
            
            ui.conversZone.getChildren().clear();
            ui.conversZone.getChildren().add(pane);
            
            // Charger les messages avec animations
            loadContactMessagesWithAnimation(contact);
            
            // Faire réapparaître les messages avec animation
            FadeTransition fadeInMessages = new FadeTransition(Duration.millis(300), ui.messagesBox);
            fadeInMessages.setFromValue(0);
            fadeInMessages.setToValue(1);
            fadeInMessages.play();
        });
        fadeOutMessages.play();

        ui.inputField.setText(contact.getCache());
        ui.inputField.setText(contact.getCache());
        ui.inputField.setDisable(!contact.getStatus());
        ui.sendButton.setDisable(!contact.getStatus());
        ui.chooseFileButton.setDisable(!contact.getStatus());
        ui.audioButton.setDisable(!contact.getStatus());
        
        // Animation pour mettre en évidence le champ de saisie
        DropShadow glow = new DropShadow();
        glow.setColor(Color.rgb(247, 175, 51, 0.5));
        glow.setBlurType(BlurType.GAUSSIAN);
        glow.setRadius(15);
        
        ui.inputField.setEffect(glow);
        
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(glow.radiusProperty(), 15)),
            new KeyFrame(Duration.millis(800), new KeyValue(glow.radiusProperty(), 0))
        );
        timeline.play();
    }
    
    /**
     * Charge les messages d'un contact avec animation
     */
    private void loadContactMessagesWithAnimation(Contact contact) {
        final int[] messageIndex = {0}; // Utilisation d'un tableau pour une "variable finale modifiable"
        
        // Si pas de messages, sortir immédiatement
        if (contact.getConversation().isEmpty()) {
            return;
        }
        
        // Animation séquentielle pour afficher les messages un par un
        PauseTransition pause = new PauseTransition(Duration.millis(50));
        pause.setOnFinished(e -> {
            if (messageIndex[0] < contact.getConversation().size()) {
                Message msg = contact.getConversation().get(messageIndex[0]);
                if (msg.getTypeMessage().equals("text")) {
                    addMessageWithAnimation(msg.getTextContent(), msg.getTime(), msg.isSentByUser());
                } else {
                    addFileWithAnimation(msg.getFile(), msg.getTime(), msg.isSentByUser());
                }
                messageIndex[0]++;
                pause.playFromStart();
            }
        });
        pause.play();
    }

    /**
     * Ajoute un contact avec l'image par défaut
     */
    public final Contact addContact(int id, String name, Boolean status) {
        return addContact(id, name, status, "/icons/user.png");
    }

    /**
     * Formate une date pour l'affichage
     */
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

    /**
     * Ajoute un message texte à la conversation avec animation
     */
    private void addMessageWithAnimation(String message, LocalTime time, boolean isSentByUser) {
        LocalDate currentDate = LocalDate.now();

        // Affiche la date si elle n'a pas encore été affichée aujourd'hui
        if (!currentDate.equals(ui.lastDisplayedDate)) {
            ui.lastDisplayedDate = currentDate;

            Label dateLabel = new Label(formatDate(currentDate));
            dateLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 11px; -fx-padding: 5px;");
            dateLabel.setMaxWidth(Double.MAX_VALUE);
            dateLabel.setAlignment(Pos.CENTER);
            dateLabel.setOpacity(0);
            
            HBox dateContainer = new HBox(dateLabel);
            dateContainer.setAlignment(Pos.CENTER);
            
            ui.messagesBox.getChildren().add(dateContainer);
            
            // Animation pour l'apparition de la date
            FadeTransition fadeDateIn = new FadeTransition(Duration.millis(500), dateLabel);
            fadeDateIn.setFromValue(0);
            fadeDateIn.setToValue(1);
            fadeDateIn.play();
        }

        // Création du contenu du message
        HBox bubbleContent = new HBox(1);
        bubbleContent.setAlignment(Pos.CENTER_RIGHT);

        Text messageText = new Text(message);
        messageText.setFont(setFontPerso("/fonts/Montserrat-Regular.ttf", 14));
        double maxWidth = 200;
        if (messageText.getBoundsInLocal().getWidth() > maxWidth) {
            messageText.setWrappingWidth(maxWidth);
        }

        Text timeStamp = new Text(time.format(TIME_FMT));
        timeStamp.setStyle("-fx-fill: #666666;");
        timeStamp.setFont(setFontPerso("/fonts/Montserrat-Regular.ttf", 10));

        HBox bottomLine = new HBox(timeStamp);
        bottomLine.setAlignment(Pos.CENTER_RIGHT);

        VBox messageBubble = new VBox(1);
        messageBubble.setPadding(new Insets(10));
        messageBubble.getStyleClass().add(isSentByUser ? "message-sent" : "message-received");
        
        // Style de base de la bulle
        messageBubble.setStyle("-fx-background-color: " + (isSentByUser ? SENT_MESSAGE_COLOR : RECEIVED_MESSAGE_COLOR) + ";"
                + (isSentByUser ? "-fx-background-radius: 15 0 15 15; -fx-border-radius: 15 0 15 15;"
                               : "-fx-background-radius: 0 15 15 15; -fx-border-radius: 0 15 15 15;"));

        // Effets de survol avec animation
        messageBubble.setOnMouseEntered(e -> {
            String hoverColor = isSentByUser ? SENT_MESSAGE_HOVER_COLOR : RECEIVED_MESSAGE_HOVER_COLOR;
            FadeTransition colorTransition = new FadeTransition(Duration.millis(150), messageBubble);
            colorTransition.setFromValue(0.95);
            colorTransition.setToValue(1);
            colorTransition.play();
            
            messageBubble.setStyle("-fx-background-color: " + hoverColor + ";"
                    + (isSentByUser ? "-fx-background-radius: 15 0 15 15; -fx-border-radius: 15 0 15 15;"
                                   : "-fx-background-radius: 0 15 15 15; -fx-border-radius: 0 15 15 15;"));
        });
        
        messageBubble.setOnMouseExited(e -> {
            FadeTransition colorTransition = new FadeTransition(Duration.millis(150), messageBubble);
            colorTransition.setFromValue(1);
            colorTransition.setToValue(0.95);
            colorTransition.play();
            
            messageBubble.setStyle("-fx-background-color: " + (isSentByUser ? SENT_MESSAGE_COLOR : RECEIVED_MESSAGE_COLOR) + ";"
                    + (isSentByUser ? "-fx-background-radius: 15 0 15 15; -fx-border-radius: 15 0 15 15;"
                                   : "-fx-background-radius: 0 15 15 15; -fx-border-radius: 0 15 15 15;"));
        });

        bubbleContent.getChildren().add(messageText);
        messageBubble.getChildren().addAll(bubbleContent, bottomLine);

        HBox container = new HBox(messageBubble);
        container.setAlignment(isSentByUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        container.setOpacity(0);
        container.setTranslateY(20);
        
        ui.messagesBox.getChildren().add(container);
        
        // Animation d'apparition du message
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), container);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), container);
        slideUp.setFromY(20);
        slideUp.setToY(0);
        
        ParallelTransition transition = new ParallelTransition(fadeIn, slideUp);
        transition.play();
        
        // Faire défiler automatiquement vers le bas
        Platform.runLater(() -> ui.scrollPane.setVvalue(1.0));
    }
    
    /**
     * Ajoute un message à la conversation (méthode publique)
     */
    public void addMessage(String message, LocalTime time, boolean isSentByUser) {
        addMessageWithAnimation(message, time, isSentByUser);
    }

    /**
     * Ajoute un fichier à la conversation avec animation
     */
    public void addFile(File file, LocalTime time, boolean sentByUser) {
        addFileWithAnimation(file, time, sentByUser);
    }
    
    /**
     * Ajoute un fichier à la conversation avec animation
     */
    private void addFileWithAnimation(File file, LocalTime time, boolean sentByUser) {
        if (isImage(file.getName()))
            afficherImageWithAnimation(file.getAbsolutePath(), time, sentByUser);
        else
            addClassicFileWithAnimation(file.getName(), time, sentByUser);
    }

    /**
     * Affiche une image dans la conversation avec animation
     */
    private void afficherImageWithAnimation(String path, LocalTime time, boolean sentByUser) {
        ImageView view = loadAnyImage(toFileURL(path));
        view.setPreserveRatio(true);
        view.setFitWidth(250);
        
        // Appliquer un effet de recadrage arrondi
        Rectangle clip = new Rectangle(view.getFitWidth(), view.getFitHeight());
        clip.setArcWidth(20);
        clip.setArcHeight(20);
       //view.setClip(clip);
        
        // Mettre à jour la hauteur du clip lorsque l'image est chargée
        view.setOnMouseEntered(e -> {
            Glow glow = new Glow();
            glow.setLevel(0.3);
            view.setEffect(glow);
        });
        
        view.setOnMouseExited(e -> {
            view.setEffect(null);
        });
        
        // Créer la bulle de message avec animation
        HBox container = createMediaBubble(view, time, sentByUser);
        container.setOpacity(0);
        container.setScaleX(0.95);
        container.setScaleY(0.95);
        
        ui.messagesBox.getChildren().add(container);
        
        // Animation d'apparition
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), container);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(400), container);
        scaleUp.setFromX(0.95);
        scaleUp.setFromY(0.95);
        scaleUp.setToX(1);
        scaleUp.setToY(1);
        
        ParallelTransition transition = new ParallelTransition(fadeIn, scaleUp);
        transition.play();
        
        // Faire défiler automatiquement vers le bas
        Platform.runLater(() -> ui.scrollPane.setVvalue(1.0));
    }

    /**
     * Crée une bulle de message pour les médias avec animations
     */
    private HBox createMediaBubble(Node content, LocalTime time, boolean sentByUser) {
        VBox fileBubble = new VBox(5);
        fileBubble.setPadding(new Insets(8));
        fileBubble.setStyle("-fx-background-color: " + (sentByUser ? SENT_MESSAGE_COLOR : RECEIVED_MESSAGE_COLOR) + ";"
                + "-fx-background-radius: 15; -fx-border-radius: 15;");

        HBox bottomLine = new HBox();
        bottomLine.setAlignment(Pos.CENTER_RIGHT);
        Text timeStamp = new Text(time.format(TIME_FMT));
        timeStamp.setStyle("-fx-fill: #667781; -fx-font-size: 11px;");
        bottomLine.getChildren().add(timeStamp);

        fileBubble.getChildren().addAll(content, bottomLine);

        // Animation sur survol
        fileBubble.setOnMouseEntered(e -> {
            String hoverColor = sentByUser ? SENT_MESSAGE_HOVER_COLOR : RECEIVED_MESSAGE_HOVER_COLOR;
            
            // Animation de transition de couleur
            FadeTransition colorTransition = new FadeTransition(Duration.millis(150), fileBubble);
            colorTransition.setFromValue(0.95);
            colorTransition.setToValue(1);
            colorTransition.play();
            
            fileBubble.setStyle("-fx-background-color: " + hoverColor + ";"
                    + "-fx-background-radius: 15; -fx-border-radius: 15;"
                    + "-fx-cursor: hand;");
            
            // Légère animation d'échelle
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), fileBubble);
            scale.setToX(1.02);
            scale.setToY(1.02);
            scale.play();
        });

        fileBubble.setOnMouseExited(e -> {
            // Animation de transition de couleur
            FadeTransition colorTransition = new FadeTransition(Duration.millis(150), fileBubble);
            colorTransition.setFromValue(1);
            colorTransition.setToValue(0.95);
            colorTransition.play();
            
            fileBubble.setStyle("-fx-background-color: " + (sentByUser ? SENT_MESSAGE_COLOR : RECEIVED_MESSAGE_COLOR) + ";"
                    + "-fx-background-radius: 15; -fx-border-radius: 15;"
                    + "-fx-cursor: hand;");
            
            // Retour à l'échelle normale
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), fileBubble);
            scale.setToX(1);
            scale.setToY(1);
            scale.play();
        });

        HBox container = new HBox(fileBubble);
        container.setPadding(new Insets(5));
        container.setAlignment(sentByUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        return container;
    }

    /**
     * Ajoute un fichier classique à la conversation avec animation
     */
    private void addClassicFileWithAnimation(String fileName, LocalTime time, boolean sentByUser) {
        VBox fileBubble = new VBox(5);
        fileBubble.setPadding(new Insets(8));
        fileBubble.setStyle("-fx-background-color: " + (sentByUser ? SENT_MESSAGE_COLOR : RECEIVED_MESSAGE_COLOR) + ";"
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

        // Animation sur survol
        fileBubble.setOnMouseEntered(e -> {
            String hoverColor = sentByUser ? SENT_MESSAGE_HOVER_COLOR : RECEIVED_MESSAGE_HOVER_COLOR;
            
            // Animation de transition de couleur
            FadeTransition colorTransition = new FadeTransition(Duration.millis(150), fileBubble);
            colorTransition.setFromValue(0.95);
            colorTransition.setToValue(1);
            colorTransition.play();
            
            fileBubble.setStyle("-fx-background-color: " + hoverColor + ";"
                    + "-fx-background-radius: 15; -fx-border-radius: 15;"
                    + "-fx-cursor: hand;");
            
            // Légère animation d'échelle
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), fileBubble);
            scale.setToX(1.02);
            scale.setToY(1.02);
            scale.play();
            
            // Animation subtile de l'icône
            DropShadow glow = new DropShadow();
            glow.setColor(Color.rgb(247, 175, 51, 0.5));
            glow.setRadius(10);
            fileIcon.setEffect(glow);
        });

        fileBubble.setOnMouseExited(e -> {
            // Animation de transition de couleur
            FadeTransition colorTransition = new FadeTransition(Duration.millis(150), fileBubble);
            colorTransition.setFromValue(1);
            colorTransition.setToValue(0.95);
            colorTransition.play();
            
            fileBubble.setStyle("-fx-background-color: " + (sentByUser ? SENT_MESSAGE_COLOR : RECEIVED_MESSAGE_COLOR) + ";"
                    + "-fx-background-radius: 15; -fx-border-radius: 15;"
                    + "-fx-cursor: hand;");
            
            // Retour à l'échelle normale
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), fileBubble);
            scale.setToX(1);
            scale.setToY(1);
            scale.play();
            
            // Retirer l'effet de l'icône
            fileIcon.setEffect(null);
        });

        HBox container = new HBox(fileBubble);
        container.setPadding(new Insets(5));
        container.setAlignment(sentByUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        container.setOpacity(0);
        container.setTranslateY(20);
        
        ui.messagesBox.getChildren().add(container);
        
        // Animation d'apparition
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), container);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), container);
        slideUp.setFromY(20);
        slideUp.setToY(0);
        
        ParallelTransition transition = new ParallelTransition(fadeIn, slideUp);
        transition.play();
        
        // Faire défiler automatiquement vers le bas
        Platform.runLater(() -> ui.scrollPane.setVvalue(1.0));
    }
    
    /**
     * Anime un bouton lors d'un clic
     */
    private void animateButtonClick(Button button) {
        ScaleTransition pulse = new ScaleTransition(Duration.millis(100), button);
        pulse.setFromX(1);
        pulse.setFromY(1);
        pulse.setToX(0.85);
        pulse.setToY(0.85);
        pulse.setCycleCount(2);
        pulse.setAutoReverse(true);
        pulse.play();
    }
    
    /**
     * Anime le bouton d'envoi
     */
    public void animateSendButton() {
        // Animation de pulsation
        ScaleTransition pulse = new ScaleTransition(Duration.millis(100), ui.sendButton);
        pulse.setFromX(1);
        pulse.setFromY(1);
        pulse.setToX(0.85);
        pulse.setToY(0.85);
        pulse.setCycleCount(2);
        pulse.setAutoReverse(true);
        pulse.play();
        
        // Animation subtile de rotation
        Timeline rotateAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(ui.sendButton.rotateProperty(), 0)),
            new KeyFrame(Duration.millis(50), new KeyValue(ui.sendButton.rotateProperty(), 15)),
            new KeyFrame(Duration.millis(100), new KeyValue(ui.sendButton.rotateProperty(), 0))
        );
        rotateAnimation.play();
    }
    
    /**
     * Anime l'affichage d'une boîte de dialogue
     */
    public void animateDialogDisplay(VBox dialogBox) {
        // Préparation de l'animation
        dialogBox.setVisible(true);
        dialogBox.setOpacity(0);
        dialogBox.setScaleX(0.8);
        dialogBox.setScaleY(0.8);
        
        // Animation de fondu
        FadeTransition fadeIn = new FadeTransition(Duration.millis(250), dialogBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        // Animation d'échelle
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(250), dialogBox);
        scaleUp.setFromX(0.8);
        scaleUp.setFromY(0.8);
        scaleUp.setToX(1);
        scaleUp.setToY(1);
        
        // Exécuter les animations en parallèle
        ParallelTransition transition = new ParallelTransition(fadeIn, scaleUp);
        transition.play();
    }
    
    /**
     * Anime la fermeture d'une boîte de dialogue
     */
    public void animateDialogClose(VBox dialogBox) {
        // Animation de fondu
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), dialogBox);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        
        // Animation d'échelle
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), dialogBox);
        scaleDown.setFromX(1);
        scaleDown.setFromY(1);
        scaleDown.setToX(0.8);
        scaleDown.setToY(0.8);
        
        // Exécuter les animations en parallèle
        ParallelTransition transition = new ParallelTransition(fadeOut, scaleDown);
        transition.setOnFinished(e -> dialogBox.setVisible(false));
        transition.play();
    }

    /**
     * Affiche un aperçu de l'image sélectionnée avant l'envoi
     */
    public void showImagePreview(File file) {
        // Vérifier si c'est une image
        if (file != null && isImage(file.getName())) {
            // Créer une boîte de dialogue pour l'aperçu
            VBox previewBox = new VBox(10);
            previewBox.setAlignment(Pos.CENTER);
            previewBox.setPadding(new Insets(15));
            previewBox.setMaxWidth(350);
            previewBox.setMaxHeight(450);
            previewBox.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
            
            // Titre de la boîte de dialogue
            Label previewTitle = new Label("Aperçu de l'image");
            previewTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            
            // Charger et afficher l'image
            ImageView imagePreview = loadAnyImage(toFileURL(file.getAbsolutePath()));
            imagePreview.setPreserveRatio(true);
            imagePreview.setFitWidth(300);
            
            // Appliquer un effet de recadrage arrondi
            Rectangle clip = new Rectangle(imagePreview.getFitWidth(), imagePreview.getFitHeight());
            clip.setArcWidth(10);
            clip.setArcHeight(10);
            //imagePreview.setClip(clip);
            
            // Mettre à jour la hauteur du clip lorsque l'image est chargée
            imagePreview.imageProperty().addListener((obs, oldImg, newImg) -> {
                if (newImg != null) {
                    clip.setHeight(imagePreview.getBoundsInLocal().getHeight());
                }
            });
            
            // Nom du fichier
            Label fileName = new Label(file.getName());
            fileName.setStyle("-fx-font-size: 12px;");
            
            // Boutons d'action
            HBox buttonBox = new HBox(15);
            buttonBox.setAlignment(Pos.CENTER);
            
            Button cancelButton = new Button("Annuler");
            cancelButton.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 20; -fx-padding: 8 15;");
            
            Button sendButton = new Button("Envoyer");
            sendButton.setStyle("-fx-background-color: " + SENT_MESSAGE_COLOR + "; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 8 15;");
            
            // Ajouter les effets de survol pour les boutons
            Stream.of(cancelButton, sendButton).forEach(btn -> {
                btn.setOnMouseEntered(e -> {
                    ScaleTransition scale = new ScaleTransition(Duration.millis(150), btn);
                    scale.setToX(1.05);
                    scale.setToY(1.05);
                    scale.play();
                    
                    btn.setCursor(Cursor.HAND);
                });
                
                btn.setOnMouseExited(e -> {
                    ScaleTransition scale = new ScaleTransition(Duration.millis(150), btn);
                    scale.setToX(1);
                    scale.setToY(1);
                    scale.play();
                    
                    btn.setCursor(Cursor.DEFAULT);
                });
            });
            
            buttonBox.getChildren().addAll(cancelButton, sendButton);
            
            // Ajouter tous les éléments à la boîte de dialogue
            previewBox.getChildren().addAll(previewTitle, imagePreview, fileName, buttonBox);
            
            // Positionner la boîte de dialogue au centre de l'écran
            StackPane overlay = new StackPane(previewBox);
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
            
            // Ajouter la boîte de dialogue à la scène principale
            ui.mainPane.getChildren().add(overlay);
            
            // Animer l'apparition de la boîte de dialogue
            previewBox.setOpacity(0);
            previewBox.setScaleX(0.8);
            previewBox.setScaleY(0.8);
            
            FadeTransition fadeIn = new FadeTransition(Duration.millis(250), previewBox);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(250), previewBox);
            scaleUp.setFromX(0.8);
            scaleUp.setFromY(0.8);
            scaleUp.setToX(1);
            scaleUp.setToY(1);
            
            ParallelTransition showTransition = new ParallelTransition(fadeIn, scaleUp);
            showTransition.play();
            
            // Gérer les clics sur les boutons
            cancelButton.setOnAction(e -> {
                // Animer la fermeture de la boîte de dialogue
                FadeTransition fadeOut = new FadeTransition(Duration.millis(200), overlay);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                
                ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), previewBox);
                scaleDown.setFromX(1);
                scaleDown.setFromY(1);
                scaleDown.setToX(0.8);
                scaleDown.setToY(0.8);
                
                ParallelTransition hideTransition = new ParallelTransition(fadeOut, scaleDown);
                hideTransition.setOnFinished(event -> ui.mainPane.getChildren().remove(overlay));
                hideTransition.play();
            });
            
            sendButton.setOnAction(e -> {
                // Animer la fermeture de la boîte de dialogue
                FadeTransition fadeOut = new FadeTransition(Duration.millis(200), overlay);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                
                ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), previewBox);
                scaleDown.setFromX(1);
                scaleDown.setFromY(1);
                scaleDown.setToX(0.8);
                scaleDown.setToY(0.8);
                
                ParallelTransition hideTransition = new ParallelTransition(fadeOut, scaleDown);
                hideTransition.setOnFinished(event -> {
                    ui.mainPane.getChildren().remove(overlay);
                    
                    // Ajouter le fichier à la conversation (avec l'heure actuelle)
                    addFile(file, LocalTime.now(), true);
                });
                hideTransition.play();
            });
        } else {
            // Si ce n'est pas une image, on peut juste envoyer le fichier directement
            addFile(file, LocalTime.now(), true);
        }
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
        chooser.setTitle("Sélectionner un fichier à envoyer");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Tous les fichiers", "*.*"));
        File f = chooser.showOpenDialog(null);
        
        if (f == null) {
            Util.showError("Aucun fichier sélectionné");
        } else {
            // Animation de confirmation pour la sélection de fichier
            animateButtonClick(ui.chooseFileButton);
            
            // Afficher l'aperçu si c'est une image
            showImagePreview(f);
        }
        
        return f;
    }
}