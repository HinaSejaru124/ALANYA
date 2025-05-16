package views;

import java.time.LocalDate;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class HomeUI implements ViewsMethods {

	/* Widgets globaux au projet */
	public final double ICON_SIZE = 24;
	public VBox contactsBox = new VBox(5);;
	public TextField searchField;
	public TextField inputField;
	public Button sendButton;
	public Button audioButton;
	public Button userImage;
	public Button videoCallButton;
	public Button audioCallButton;
	public Button chooseFileButton;
	public StackPane mainContent = new StackPane();
	public VBox messageBoxBig = new VBox(10);
	public ScrollPane scrollPane;
	public HBox inputBox = new HBox(2);
	public StackPane conversZone = new StackPane();
	public BorderPane conversation = new BorderPane();
	public Scene scene;
	public HBox header = new HBox();
	public Region spacerHeader = new Region();
	public Button addButton;
	public VBox addContactBox;
	public TextField idField;
	public Button closeButton;
	public VBox messagesBox;
	public MenuItem item1;
	public MenuItem item2;
	public LocalDate lastDisplayedDate = null;
	private final StackPane root;

	protected final VBox newContactBox() {
		VBox newcontactBox = new VBox(10);
		newcontactBox.setAlignment(Pos.CENTER);

		newcontactBox.setStyle("-fx-background-color: #ffffff;" +
				"-fx-background-radius: 15px;" +
				"-fx-padding: 30px;" +
				"-fx-spacing: 10px;" +
				"-fx-min-width: 500px;" +
				"-fx-max-width: 500px;" +
				"-fx-min-height: 400px;" +
				"-fx-max-height: 400px;" +
				"-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 50, 0.3, 0, 5);");

		closeButton = createIconButton("/icons/cross.png", ICON_SIZE);
		closeButton.setStyle("-fx-background-color: #ffffff;" +
				"-fx-background-radius: 50%;" +
				"-fx-min-width: 30px;" +
				"-fx-min-height: 30px;" +
				"-fx-max-width: 30px;" +
				"-fx-max-height: 30px;");
		closeButton.setTranslateX(-220);
		closeButton.setTranslateY(-100);

		Label addLabel = new Label("Ajouter un nouveau contact");
		addLabel.setStyle("-fx-font-size: 22px;\n" +
				"    -fx-font-weight: bold;\n" +
				"    -fx-text-fill: #333;\n" +
				"    -fx-alignment: center;");

		idField = new TextField();
		idField.setPromptText("Identifiant du contact");
		idField.setStyle("-fx-pref-width: 250px;" +
				"-fx-font-size: 14px;" +
				"-fx-background-radius: 8px;" +
				"-fx-border-radius: 8px;" +
				"-fx-padding: 10px;" +
				"-fx-border-color: #ddd;" +
				"-fx-border-width: 1px;");

		addButton = new Button("Enregistrer");
		addButton.getStyleClass().add("login-button");
		addButton.setStyle(
				"-fx-background-color: #F7AF33;-fx-text-fill: white;-fx-font-size: 14px;-fx-font-weight: bold;-fx-background-radius: 8px;-fx-padding: 10px 20px;-fx-cursor: hand;");

		newcontactBox.getChildren().addAll(closeButton, addLabel, idField, addButton);

		return newcontactBox;
	}

	public HomeUI() {
		Region spacerMainContent1 = new Region();
		HBox.setHgrow(spacerMainContent1, Priority.ALWAYS);

		Region spacerMainContent2 = new Region();
		HBox.setHgrow(spacerMainContent2, Priority.ALWAYS);

		header.setPadding(new Insets(10));
		header.setStyle("-fx-background-color: #F7AF33;");

		HBox.setHgrow(spacerHeader, Priority.ALWAYS);

		// Boutons d'appel vidéo et audio
		audioCallButton = createIconButton("/icons/call-noir.png", ICON_SIZE);
		videoCallButton = createIconButton("/icons/video-camera.png", ICON_SIZE);

		// Zone des messages (ScrollPane)
		messagesBox = new VBox(2);
		messagesBox.setPadding(new Insets(2));
		messagesBox.setStyle("-fx-background-color: #ECE5DD;");

		messageBoxBig.setPadding(new Insets(10));
		messageBoxBig.setStyle("-fx-background-color: #ECE5DD;");

		scrollPane = new ScrollPane(messagesBox);
		scrollPane.setFitToWidth(true);
		scrollPane.setStyle("-fx-background-color: #ECE5DD; "
				+ "-fx-hbar-policy: never; "   // Ne jamais afficher la barre horizontale
				+ "-fx-vbar-policy: never; "); // Ne jamais afficher la barre verticale

		messageBoxBig.getChildren().clear();
		messageBoxBig.getChildren().add(scrollPane);

		// Barre inférieure (Input) avec effet flottant
		inputBox.setPadding(new Insets(0, 5, 0, 0)); // Espacement bas
		inputBox.setAlignment(Pos.CENTER);
		inputBox.setStyle("-fx-background-color: #ECE5DD;");

		HBox inputZone = new HBox();
		inputZone.setPadding(new Insets(10, 10, 10, 10)); // Espacement bas
		inputZone.setAlignment(Pos.CENTER);
		inputZone.setStyle("-fx-background-color: #ECE5DD;");


		inputField = new TextField();
		inputField.setPromptText("Message...");
		//inputField.setWrapText(true);
		//inputField.setPrefRowCount(2);
		inputField.setStyle("-fx-background-color: white; "
				+ "-fx-text-fill: #333333; "
				+ "-fx-font-size: 14px; "
				+ "-fx-border-color: transparent; "
				+ "-fx-background-radius: 50px; "
				+ "-fx-border-radius: 50px;"
				+ "-fx-min-height: 50px; "
				+ "-fx-max-height: 50px; "
				+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0.1, 0, 2);-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
		HBox.setHgrow(inputField, Priority.ALWAYS);

		sendButton = createIconButton("/icons/paper-plane.png", ICON_SIZE);
		sendButton.setStyle("-fx-background-color: #C47C00; "
				+ "-fx-background-radius: 50%; "
				+ "-fx-min-width: 50px; "
				+ "-fx-min-height: 50px; "
				+ "-fx-max-width: 50px; "
				+ "-fx-max-height: 50px; "
				+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0.1, 0, 2);");

		audioButton = createIconButton("/icons/microphone.png", ICON_SIZE);
		audioButton.setStyle("-fx-background-color: #C47C00; "
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
		audioButton.setPrefWidth(60);
		audioButton.setPrefHeight(60);

		// Comportement dynamique comme WhatsApp : affichage selon texte
		inputField.textProperty().addListener((obs, oldText, newText) -> {
			if (newText.trim().isEmpty()) {
				audioButton.setVisible(true);
				sendButton.setVisible(false);
			} else {
				audioButton.setVisible(false);
				sendButton.setVisible(true);
			}
		});

		// Initialement : micro visible, avion caché
		sendButton.setVisible(false);
		audioButton.setVisible(true);
		

		// Positionnement du bouton (overlay) dans la StackPane
		chooseFileButton = createIconButton("/icons/clip.png", ICON_SIZE);
		chooseFileButton.setPrefWidth(50);
		//StackPane.setMargin(chooseFileButton, new Insets(10)); // Marges pour l'espacement

		Region spacerChooseBox = new Region();
		HBox.setHgrow(spacerChooseBox, Priority.ALWAYS);
		HBox chooseFileBox = new HBox(10,spacerChooseBox, chooseFileButton);
		chooseFileBox.setStyle("-fx-background-color: white; "
				+ "-fx-text-fill: #333333; "
				+ "-fx-font-size: 14px; "
				+ "-fx-border-color: transparent; "
				+ "-fx-background-radius: 50px; "
				+ "-fx-border-radius: 0 50px 50px 0;"
				+ "-fx-min-height: 50px; "
				+ "-fx-max-height: 50px; "
				+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0.1, 0, 2);-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");


		chooseFileButton.setFocusTraversable(false);


		StackPane sendaudioStackPane = new StackPane(sendButton,audioButton);

		HBox.setHgrow(inputZone, Priority.ALWAYS);
		inputZone.getChildren().clear();
		inputZone.getChildren().addAll(inputField, chooseFileBox);

		inputBox.getChildren().clear();
		inputBox.getChildren().addAll(inputZone, sendaudioStackPane);

		mainContent.getChildren().addAll(header);

		// Zone des contacts
		VBox contactsZone = new VBox(10);
		contactsZone.setPadding(new Insets(15));
		contactsZone.setStyle("-fx-background-color: white;");
		contactsZone.setPrefWidth(300);

		// Nom de l'application
		Label appName = new Label("ALANYA");
		appName.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: linear-gradient(from 0% 100% to 0% 0%,#C45800,#F7AF33);");

		item1 = new MenuItem("Profil");
		item1.getStyleClass().add("custom-menu-item");

		item2 = new MenuItem("Déconnexion");
		item2.getStyleClass().add("custom-menu-item");

		ContextMenu menu = new ContextMenu(item1, item2);
		menu.getStyleClass().add("custom-menu");

		Button menuButton = createIconButton("/icons/dots.png", ICON_SIZE);
		menuButton.setOnAction(e -> menu.show(menuButton, Side.BOTTOM, 0, 0));

		HBox.setHgrow(spacerHeader, Priority.ALWAYS);
		HBox appBox = new HBox(5, appName, spacerHeader, menuButton);


		// Barre de recherche
		searchField = new TextField();
		searchField.setPromptText("Rechercher un contact...");
		searchField.setStyle("-fx-padding: 10px; " +
				"-fx-background-radius: 20px; " +
				"-fx-border-radius: 20px; " +
				"-fx-border-color: linear-gradient(from 0% 100% to 0% 0%,#C45800,#F7AF33); " +
				"-fx-background-color: white;");
		searchField.setFocusTraversable(false);

		// Liste des contacts
		contactsBox.setStyle("-fx-background-color: white;");

		ScrollPane contactsScroll = new ScrollPane(contactsBox);
		contactsScroll.setFitToWidth(true);
		contactsScroll.setStyle("-fx-background-color: transparent; " +
				"-fx-background: transparent; " +
				"-fx-border-color: transparent;");

		contactsZone.getChildren().addAll(appBox, searchField, contactsScroll);

		conversation.setCenter(new Label("Selectionner une discussion pour commencer"));
		conversation.setStyle("-fx-background-color: #ECE5DD;");

		conversZone.getChildren().add(conversation);

		Button addContactButton = createIconButton("/icons/add-user.png", ICON_SIZE);
		addContactButton.setStyle("-fx-background-color : linear-gradient(from 0% 100% to 0% 0%,#C45800,#C47C00); "
				+ "-fx-background-radius: 50%; "
				+ "-fx-min-width: 50px; "
				+ "-fx-min-height: 50px; "
				+ "-fx-max-width: 50px; "
				+ "-fx-max-height: 50px; "
				+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0.1, 0, 2);");

		addContactButton.setTranslateX(160);
		addContactButton.setTranslateY(250);

		StackPane contactsStackPane = new StackPane();
		contactsStackPane.getChildren().addAll(contactsZone, addContactButton);

		// Mise en page principale
		SplitPane app = new SplitPane();
		app.getItems().addAll(contactsStackPane, conversZone);
		app.setStyle("-fx-background-color: transparent; -fx-margin:0");

		addContactBox = newContactBox();
		addContactBox.setVisible(false);
		addContactBox.setTranslateY(170);
		addContactButton.setOnAction(e -> addContactBox.setVisible(true));

		root = new StackPane();
		root.getChildren().addAll(app, addContactBox);
	}

	public Parent getRoot() {
		return root;
	}
}