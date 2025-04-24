package alanya;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import javafx.stage.Stage;

public class View extends Application {

	/* Widgets globaux au projet */
	protected static final double ICON_SIZE = 24;
	protected static VBox contactsBox = new VBox(5);;
	public static TextField searchField;
	public static TextField inputField;
	public static Button sendButton;
	public static Button hangUpButton;
	public static Button userImage;
	public static Button videoCallButton;
	public static Button audioCallButton;
	public static Button chooseFileButton;
	public static HBox callBar;
	public static Label callTimer;

	// Charger une image depuis les ressources
	protected final Image loadImage(String path) {
		return new Image(getClass().getResourceAsStream(path));
	}

	// Créer un bouton avec une icône redimensionnée
	protected final Button createIconButton(String iconPath) {
		Button button = new Button();
		ImageView icon = new ImageView(loadImage(iconPath));
		icon.setFitWidth(ICON_SIZE);
		icon.setFitHeight(ICON_SIZE);
		button.setGraphic(icon);
		button.setStyle("-fx-background-color: transparent;");
		// Change le curseur en "main" lorsqu'on survole le bouton
		button.setOnMouseEntered(event -> button.setCursor(Cursor.HAND));

		button.setOnMouseExited(event -> button.setCursor(Cursor.DEFAULT));
		return button;
	}

	protected final void addContact(String name, String status, String imagePath) {
		contactsBox.getChildren().add(new Contact(0, name, status, imagePath));
	}

	@Override
	public void start(Stage primaryStage) {
		// Barre d'appel (invisible par défaut)
		callBar = new HBox(10);
		callBar.setPadding(new Insets(10));
		callBar.setStyle("-fx-background-color: #f4f4f4;");
		callBar.setAlignment(Pos.CENTER_LEFT);
		callBar.setVisible(false); // Masqué par défaut

		Label callUsernameLabel = new Label("Interlocuteur");
		callUsernameLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");

		callTimer = new Label("00:00");
		callTimer.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

		hangUpButton = createIconButton("/icons/hangup.png");
		hangUpButton.setStyle("-fx-background-color: #ff4c4c;"
				+ "-fx-background-radius: 50%; "
				+ "-fx-min-width: 50px; "
				+ "-fx-min-height: 50px; "
				+ "-fx-max-width: 50px; "
				+ "-fx-max-height: 50px; "
				+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0.1, 0, 2);");

		// Espaceur dynamique pour pousser le bouton à droite
		Region spacerMainContent1 = new Region();
		HBox.setHgrow(spacerMainContent1, Priority.ALWAYS);

		Region spacerMainContent2 = new Region();
		HBox.setHgrow(spacerMainContent2, Priority.ALWAYS);

		callBar.getChildren().addAll(callUsernameLabel, spacerMainContent1, callTimer,
				spacerMainContent2, hangUpButton);
		callBar.setSpacing(20);

		// // Section d'appel video
		// imageView = new ImageView();
		// imageView.setVisible(false);

		// Barre supérieure (Header)
		HBox header = new HBox();
		header.setPadding(new Insets(10));
		header.setStyle("-fx-background-color: #F7AF33;");

		// Espaceur dynamique pour pousser le bouton à droite
		Region spacerHeader = new Region();
		HBox.setHgrow(spacerHeader, Priority.ALWAYS);

		Text contactName = new Text("Interlocuteur");
		contactName.setTranslateX(10);
		contactName.setTranslateY(8);
		contactName.setStyle("-fx-fill: black;-fx-font-size: 16px;");

		userImage = createIconButton("/icons/user.png");
		userImage.setStyle("-fx-background-color: #AB6D00;"
				+ "-fx-background-radius: 50%;"
				+ "-fx-min-width:40;"
				+ "-fx-min-height:40;"
				+ "-fx-max-width:40;"
				+ "-fx-max-width:40;");

		// Boutons d'appel vidéo et audio
		audioCallButton = createIconButton("/icons/phone-call.png");
		videoCallButton = createIconButton("/icons/video-camera-alt.png");
		header.getChildren().addAll(userImage, contactName, spacerHeader, audioCallButton, videoCallButton);

		// Zone des messages (ScrollPane)
		VBox messagesBox = new VBox(2);
		messagesBox.setPadding(new Insets(2));
		messagesBox.setStyle("-fx-background-color: #ECE5DD;"); // Fond clair, comme WhatsApp

		VBox messageBoxBig = new VBox(10);
		messageBoxBig.setPadding(new Insets(10));
		messageBoxBig.setStyle("-fx-background-color: #ECE5DD;");

		ScrollPane scrollPane = new ScrollPane(messagesBox);
		scrollPane.setFitToWidth(true);
		scrollPane.setStyle("-fx-background-color: #FDEBCC; "
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

		// Bouton d'envoi des messages
		sendButton = createIconButton("/icons/paper-plane.png");
		sendButton.setStyle("-fx-background-color: #C47C00; "
				+ "-fx-background-radius: 50%; "
				+ "-fx-min-width: 50px; "
				+ "-fx-min-height: 50px; "
				+ "-fx-max-width: 50px; "
				+ "-fx-max-height: 50px; "
				+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0.1, 0, 2);");

		// Bouton d'envoie de fichiers

		// Espacement automatique pour un alignement parfait
		inputField.setPrefWidth(1000);
		sendButton.setPrefWidth(60);
		sendButton.setPrefHeight(60);

		// Positionnement du bouton (overlay) dans la StackPane
		chooseFileButton = createIconButton("/icons/clip.png");
		chooseFileButton.setPrefWidth(50);
		chooseFileButton.setTranslateX(140);
		StackPane.setMargin(chooseFileButton, new Insets(10)); // Marges pour l'espacement

		HBox.setHgrow(inputZone, Priority.ALWAYS);
		inputZone.getChildren().addAll(inputField, chooseFileButton);

		inputBox.getChildren().addAll(inputZone, sendButton);

		// Ajouter la barre d'appel au dessus de la zone de conversation
		StackPane mainContent = new StackPane();
		mainContent.getChildren().addAll(header, callBar);

		// Zone des contacts
		VBox contactsZone = new VBox(10);
		contactsZone.setPadding(new Insets(15));
		contactsZone.setStyle("-fx-background-color: white;");
		contactsZone.setPrefWidth(300);

		// Nom de l'application
		Label appName = new Label("ALANYA");
		appName.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #F7AF33;");

		// Barre de recherche
		searchField = new TextField();
		searchField.setPromptText("Rechercher un contact...");
		searchField.setStyle("-fx-padding: 10px; " +
				"-fx-background-radius: 20px; " +
				"-fx-border-radius: 20px; " +
				"-fx-border-color: #F7AF33; " +
				"-fx-background-color: white;");
		searchField.setFocusTraversable(false);

		// Liste des contacts
		contactsBox.setStyle("-fx-background-color: white;");

		// Création des contacts (exemples)
		addContact("Sophie Martin", "En ligne", "/icons/user.png");
		addContact("Lucas Bernard", "Hors ligne", "/icons/user.png");
		addContact("Emma Dubois", "En ligne", "/icons/user.png");

		ScrollPane contactsScroll = new ScrollPane(contactsBox);
		contactsScroll.setFitToWidth(true);
		contactsScroll.setStyle("-fx-background-color: transparent; " +
				"-fx-background: transparent; " +
				"-fx-border-color: transparent;");

		contactsZone.getChildren().addAll(appName, searchField, contactsScroll);

		BorderPane conversZone = new BorderPane();
		conversZone.setTop(mainContent);
		conversZone.setCenter(messageBoxBig);
		conversZone.setBottom(inputBox);

		// Mise en page principale
		SplitPane root = new SplitPane();
		root.getItems().addAll(contactsZone, conversZone);
		root.setStyle("-fx-background-color: transparent; -fx-margin:0");

		// Créer la scène
		Scene scene = new Scene(root, 800, 600);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/Logo1-2.png")));
		primaryStage.setTitle("ALANYA");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	protected final void showError(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public static void main(String[] args) {
		launch(args);
	}
}