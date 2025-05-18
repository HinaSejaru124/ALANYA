package views;

import java.time.LocalDate;

import controllers.HomeController;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

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
	public Button profileButton;
	public Button logoutButton;
	public LocalDate lastDisplayedDate = null;
	public ChangeListener<Number> widthListener;
    public StackPane mainPane;
	private final StackPane root;
	
	// Nouvelles variables pour la barre latérale
	private VBox sidebarMenu;
	private Button menuButton;
	private boolean isSidebarVisible = false;

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
		addLabel.setStyle(
				"    -fx-font-weight: bold;\n" +
				"    -fx-text-fill: #333;\n" +
				"    -fx-alignment: center;");
        addLabel.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/Montserrat-Regular.ttf"), 22));


		idField = new TextField();
		idField.setPromptText("Identifiant du contact");
        idField.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/Montserrat-Regular.ttf"), 14));

		idField.setStyle("-fx-pref-width: 250px;" +
				"-fx-background-radius: 8px;" +
				"-fx-border-radius: 8px;" +
				"-fx-padding: 10px;" +
				"-fx-border-color: #ddd;" +
				"-fx-border-width: 1px;");

		addButton = new Button("Enregistrer");
        addButton.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/Montserrat-Bold.ttf"), 14));

		addButton.setStyle(
				"-fx-background-color: #F7AF33;-fx-text-fill: white;-fx-background-radius: 8px;-fx-padding: 10px 20px;-fx-cursor: hand;");

		newcontactBox.getChildren().addAll(closeButton, addLabel, idField, addButton);
        newcontactBox.setPickOnBounds(false);

		return newcontactBox;
	}
	
	/**
	 * Crée la barre latérale cachée style Telegram
	 * @return VBox contenant la barre latérale
	 */
	private VBox createSidebar() {
	    VBox sidebar = new VBox(20); // 20 px d'espacement entre les éléments
	    sidebar.setPadding(new Insets(20));
	    sidebar.setStyle("-fx-background-color: #45B5AA;" + // Couleur de fond
	                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 0);");
	    sidebar.setMaxWidth(250); // Largeur de la barre latérale
	    sidebar.setAlignment(Pos.TOP_CENTER);
	    sidebar.setTranslateX(-550); // Initialement hors écran
	    
	    // En-tête avec le logo de l'application
	    Label sidebarTitle = new Label("ALANYA");
	    sidebarTitle.setFont(setFontPerso("/fonts/Montserrat-Bold.ttf", 24));
	    sidebarTitle.setStyle("-fx-text-fill: white;");
	    
	    // Ligne de séparation
	    Region separator = new Region();
	    separator.setPrefHeight(1);
	    separator.setStyle("-fx-background-color: rgba(255,255,255,0.2);");
	    separator.setPrefWidth(180);
	    
	    // Bouton Profil
	    profileButton = new Button("Profil");
	    profileButton.setFont(setFontPerso("/fonts/Montserrat-Bold.ttf", 14));
	    profileButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER-LEFT;");
	    profileButton.setMaxWidth(Double.MAX_VALUE); // pour occuper toute la largeur
	    profileButton.setPadding(new Insets(10, 0, 10, 10));
	    profileButton.setOnAction(e -> {
	        // Action vers une vue de profil
	        MainApp mainApp = new MainApp();
	        mainApp.showInfosUser();
	        toggleSidebar(); // Fermer le menu après la sélection
	    });
	    profileButton.setOnMouseEntered(e -> 
	        profileButton.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-alignment: CENTER-LEFT;"));
	    profileButton.setOnMouseExited(e -> 
	        profileButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER-LEFT;"));
	    
	    // Espace flexible qui pousse le bouton Déconnexion vers le bas
	    Region spacer = new Region();
	    VBox.setVgrow(spacer, Priority.ALWAYS);
	    
	    // Bouton Déconnexion
	    logoutButton = new Button("Déconnexion");
	    logoutButton.setFont(setFontPerso("/fonts/Montserrat-Bold.ttf", 14));
	    logoutButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER-LEFT;");
	    logoutButton.setMaxWidth(Double.MAX_VALUE);
	    logoutButton.setPadding(new Insets(10, 0, 10, 10));
	    logoutButton.setOnAction(e -> {
	        // Action pour se déconnecter et revenir à la page de login
	        MainApp mainApp = new MainApp();
	        mainApp.showLogin();
	        toggleSidebar(); // Fermer le menu après la sélection
	    });
	    logoutButton.setOnMouseEntered(e -> 
	        logoutButton.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-alignment: CENTER-LEFT;"));
	    logoutButton.setOnMouseExited(e -> 
	        logoutButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER-LEFT;"));
	    
	    sidebar.getChildren().addAll(sidebarTitle, separator, profileButton, spacer, logoutButton);
	    
	    return sidebar;
	}
	
	/**
	 * Bascule l'affichage de la barre latérale
	 */
	public void toggleSidebar() {
	    TranslateTransition transition = new TranslateTransition(Duration.millis(250), sidebarMenu);
	    
	    if (isSidebarVisible) {
	        
	        transition.setToX(-550);
	        isSidebarVisible = false;
	    } else {
	        
	        transition.setToX(-280);
	        isSidebarVisible = true;
	    }
	    
	    transition.play();
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
        inputField.setFont(setFontPerso("/fonts/Montserrat-Regular.ttf", 16));
		//inputField.setWrapText(true);
		//inputField.setPrefRowCount(2);
		inputField.setStyle("-fx-background-color: white; "
				+ "-fx-text-fill: #333333; "
				+ "-fx-font-size: 14px; "
				+ "-fx-border-color: transparent; "
				+ "-fx-background-radius: 50px 0 0 50px;"
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

		audioButton = createIconButton("/icons/microphone-noir.png", ICON_SIZE);
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
		chooseFileBox.setPadding(new Insets(5));
		chooseFileBox.setStyle("-fx-background-color: white; "
				+ "-fx-text-fill: #333333; "
				+ "-fx-font-size: 14px; "
				+ "-fx-border-color: transparent; "
				+ "-fx-background-radius: 0 50px 50px 0;"
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
		appName.setStyle("-fx-text-fill: linear-gradient(from 0% 100% to 0% 0%,#C45800,#F7AF33);");
		appName.setFont(setFontPerso("/fonts/Montserrat-Bold.ttf", 24));

		// Création du bouton de menu
		menuButton = createIconButton("/icons/menu-burger.png", ICON_SIZE);
		menuButton.setOnAction(e -> toggleSidebar());

		HBox.setHgrow(spacerMainContent1, Priority.ALWAYS);
		HBox appBox = new HBox(5, appName, spacerMainContent1, menuButton);

		// Barre de recherche
		searchField = new TextField();
		searchField.setPromptText("Rechercher un contact...");
		searchField.setFont(setFontPerso("/fonts/Montserrat-Regular.ttf", 16));
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

		Region spacerAddButton = new Region();
		HBox.setHgrow(spacerAddButton, Priority.ALWAYS);
		HBox addContactButtonBox = new HBox(10, spacerAddButton, addContactButton);

		BorderPane addContactButtonStackPane = new BorderPane();
		addContactButtonStackPane.setBottom(addContactButtonBox);
		addContactButtonStackPane.setPadding(new Insets(20));
		addContactButtonStackPane.setPickOnBounds(false);

		StackPane contactsStackPane = new StackPane();
		contactsStackPane.getChildren().addAll(contactsZone, addContactButtonStackPane);

		// Mise en page principale
		SplitPane app = new SplitPane();
		app.getItems().addAll(contactsStackPane, conversZone);
		app.setStyle("-fx-background-color: transparent; -fx-margin:0");


		addContactBox = newContactBox();
		addContactBox.setVisible(false);
		addContactBox.setTranslateY(170);
		addContactButton.setOnAction(e -> addContactBox.setVisible(true));
		MainApp app1 = new MainApp();
		HomeController homeController = new HomeController(this, app1);
		homeController.addContact(1, "Jeff", true);

		// Écoute de la largeur de la scène
    	widthListener = (obs, oldVal, newVal) -> {
            double width = newVal.doubleValue();
            if (width < 700) {
                app.setDividerPositions(0.5); // Pour petits écrans

            } else {
                app.setDividerPositions(0.3); // Pour grands écrans
				addContactBox.setTranslateY(300);
                
            }
        };
        
        // Créer la barre latérale
        sidebarMenu = createSidebar();
        //sidebarMenu.setVisible(isSidebarVisible);
        
        // Configurer la zone principale
        mainPane = new StackPane();
        mainPane.getChildren().add(app);
        
        // Créer un panneau transparent pour détecter les clics en dehors de la barre latérale
        Region overlay = new Region();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0);");
        overlay.setVisible(false);
        overlay.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (isSidebarVisible) {
                toggleSidebar();
                overlay.setVisible(false);
            }
        });
        
        // Écouter l'état de la barre latérale
        sidebarMenu.translateXProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() >= -1) {
                // Barre latérale visible, activer l'overlay
                overlay.setVisible(true);
                overlay.toFront();
                sidebarMenu.toFront();
            } else {
                // Barre latérale cachée, désactiver l'overlay
                overlay.setVisible(false);
            }
        });

		root = new StackPane();
		root.getChildren().addAll(mainPane, overlay, sidebarMenu, addContactBox);
	}

	public Parent getRoot() {
		return root;
	}
	
	/**
	 * Méthode pour savoir si la barre latérale est visible
	 * @return true si la barre latérale est visible, false sinon
	 */
	public boolean isSidebarVisible() {
	    return isSidebarVisible;
	}
	
	/**
	 * Méthode pour fermer la barre latérale si elle est ouverte
	 */
	public void closeSidebarIfOpen() {
	    if (isSidebarVisible) {
	        toggleSidebar();
	    }
	}
}