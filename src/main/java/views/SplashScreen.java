package views;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Écran de démarrage (splashscreen) pour l'application ALANYA
 */
public class SplashScreen {
    private Stage splashStage;
    private ProgressBar progressBar;
    private Label progressText;
    private ImageView logoImage;
    private final MainApp mainApp;
    private StackPane root;
    
    // Dimensions et positions
    private final int WIDTH = 800;
    private final int HEIGHT = 650;
    
    public SplashScreen(Stage primaryStage, MainApp mainApp) {
        this.mainApp = mainApp;
        this.splashStage = new Stage();
        splashStage.initStyle(StageStyle.TRANSPARENT);
        createSplashScreen();
        
        // Centrer le splashscreen par rapport à la fenêtre principale
        splashStage.setX(primaryStage.getX() + (primaryStage.getWidth() - WIDTH) / 2);
        splashStage.setY(primaryStage.getY() + (primaryStage.getHeight() - HEIGHT) / 2);
    }

    private void createSplashScreen() {
        // Créer la structure de base
        root = new StackPane();
        root.setStyle("-fx-background-color: transparent;");
        
        // Créer le fond avec dégradé et coins arrondis
        Rectangle background = new Rectangle(WIDTH, HEIGHT);
        background.setArcWidth(30);
        background.setArcHeight(30);
        
        // Dégradé orange élégant
        LinearGradient gradient = new LinearGradient(
            0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#FFFFFF")),
            new Stop(0.5, Color.web("#FFFFFF")),
            new Stop(1, Color.web("#FFFFFF"))
        );
        background.setFill(gradient);
        
        // Ajouter une ombre pour un effet 3D
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.4));
        shadow.setRadius(15);
        shadow.setOffsetX(5);
        shadow.setOffsetY(5);
        background.setEffect(shadow);
        
        // Conteneur principal
        BorderPane mainContent = new BorderPane();
        mainContent.setPadding(new Insets(30));
        
        // Logo de l'application
        VBox logoBox = new VBox(15);
        logoBox.setAlignment(Pos.CENTER);
        
        // Logo (cercle animé)
        Circle logoCircle = new Circle(60);
        logoCircle.setFill(Color.WHITE);
        
    
        // Utiliser une image pour le logo
        logoImage = new ImageView(new Image(getClass().getResourceAsStream("/images/Logo1-2.png")));
        // Redimensionner l'image pour qu'elle s'adapte au cercle
        logoImage.setFitWidth(200);
        logoImage.setFitHeight(200);
        logoImage.setPreserveRatio(true);
        logoImage.setSmooth(true);
        
        StackPane logoPane = new StackPane(logoImage);
        
        // Nom de l'application
        Text appNameText = new Text("ALANYA");
        appNameText.setFont(Font.font("Montserrat", FontWeight.BOLD, 48));
        appNameText.setFill(Color.WHITE);
        
        // Slogan
        Text sloganText = new Text("Restez connecté, communiquez en toute simplicité");
        sloganText.setFont(Font.font("Montserrat", 16));
        sloganText.setFill(Color.WHITE);
        sloganText.setTextAlignment(TextAlignment.CENTER);
        
        logoBox.getChildren().addAll(logoPane);
        
        // Barre de progression et texte
        VBox progressBox = new VBox(10);
        progressBox.setAlignment(Pos.CENTER);
        
       // Remplacer le code existant de la barre de progression par celui-ci
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(WIDTH - 80);
        progressBar.setPrefHeight(6); // Barre plus fine
        progressBar.getStyleClass().add("modern-progress-bar");

        // Ajouter ce CSS à votre méthode createSplashScreen juste avant de créer la scène
        Scene splashScene = new Scene(root, WIDTH, HEIGHT);
        splashScene.setFill(Color.TRANSPARENT);

        // Ajouter ces styles CSS pour la barre de progression
        progressBar.getStyleClass().add( "modern-progress-bar");
  
        
        progressText = new Label("Initialisation...");
        progressText.setFont(Font.font("Montserrat", 14));
        progressText.setTextFill(Color.rgb(247, 175, 51));
        
        progressBox.getChildren().addAll(progressBar, progressText);
        
        // Assemblage final
        mainContent.setCenter(logoBox);
        mainContent.setBottom(progressBox);
        
        // Éléments décoratifs (cercles translucides)
        addDecorativeElements();
        
        root.getChildren().addAll(background, mainContent);
        
        splashStage.setScene(splashScene);
        splashStage.setResizable(false);
    }
    
    private void addDecorativeElements() {
        // Ajouter des cercles décoratifs au fond
        for (int i = 0; i < 8; i++) {
            Circle circle = new Circle(Math.random() * 30 + 10);
            circle.setFill(Color.rgb(247, 175, 51));
            circle.setOpacity(0.1 + Math.random() * 0.15);
            
            // Position aléatoire
            double x = Math.random() * WIDTH - WIDTH/2;
            double y = Math.random() * HEIGHT - HEIGHT/2;
            
            circle.setTranslateX(x);
            circle.setTranslateY(y);
            
            // Animation légère
            RotateTransition rotate = new RotateTransition(Duration.seconds(20), circle);
            rotate.setByAngle(360);
            rotate.setCycleCount(RotateTransition.INDEFINITE);
            rotate.setAutoReverse(false);
            rotate.play();
            
            root.getChildren().add(circle);
        }
    }
    
    /**
     * Affiche le splashscreen et commence à charger l'application
     */
    public void show() {
        splashStage.show();
        
        // Animation d'entrée
        root.setOpacity(0);
        root.setScaleX(0.9);
        root.setScaleY(0.9);
        
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        ScaleTransition scaleUp = new ScaleTransition(Duration.seconds(0.5), root);
        scaleUp.setFromX(0.9);
        scaleUp.setFromY(0.9);
        scaleUp.setToX(1);
        scaleUp.setToY(1);
        
        ParallelTransition startAnimation = new ParallelTransition(fadeIn, scaleUp);
        startAnimation.play();
        
        // Lancer le processus de chargement
        startLoadingProcess();
    }
    
    /**
     * Simule le processus de chargement de l'application
     * Dans une application réelle, cette méthode lancerait les tâches de chargement effectives
     */
    private void startLoadingProcess() {
        Task<Void> loadingTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Simuler le chargement de différentes parties de l'application
                String[] loadingSteps = {
                    "Initialisation des composants...",
                    "Chargement de l'interface utilisateur...",
                    "Connexion à la base de données...",
                    "Chargement des contacts...",
                    "Configuration du système de messagerie...",
                    "Finalisation..."
                };
                
                double totalSteps = loadingSteps.length;
                
                for (int i = 0; i < loadingSteps.length; i++) {
                    // Mise à jour du progrès
                    final int stepIndex = i;
                    Platform.runLater(() -> {
                        double progress = (stepIndex + 1) / totalSteps;
                        progressBar.setProgress(progress);
                        progressText.setText(loadingSteps[stepIndex]);
                    });
                    
                    // Simuler le temps de chargement
                    Thread.sleep((long) (Math.random() * 500 + 500));
                }
                
                return null;
            }
        };
        
        // Lorsque le chargement est terminé
        loadingTask.setOnSucceeded(event -> {
            // Animation finale pour indiquer que le chargement est terminé
            progressText.setText("Chargement terminé !");
            
            // Animation de la barre de progression à 100%
            Timeline fillProgress = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), progressBar.getProgress())),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(progressBar.progressProperty(), 1.0))
            );
            
            // Ajouter un effet de brillance sur le logo
            Glow glow = new Glow();
            glow.setLevel(0);
            
            // Appliquer l'effet sur l'image du logo au lieu du texte
            logoImage.setEffect(glow);
            
            Timeline glowAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(glow.levelProperty(), 0)),
                new KeyFrame(Duration.seconds(1), new KeyValue(glow.levelProperty(), 0.8)),
                new KeyFrame(Duration.seconds(2), new KeyValue(glow.levelProperty(), 0))
            );
            
            // Pause avant de fermer
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            
            // Animation de sortie
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.8), root);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            
            ScaleTransition scaleDown = new ScaleTransition(Duration.seconds(0.8), root);
            scaleDown.setFromX(1);
            scaleDown.setFromY(1);
            scaleDown.setToX(1.1);
            scaleDown.setToY(1.1);
            
            ParallelTransition endAnimation = new ParallelTransition(fadeOut, scaleDown);
            
            // Enchaîner les animations
            SequentialTransition sequence = new SequentialTransition(
                fillProgress, 
                glowAnimation,
                pause, 
                endAnimation
            );
            
            // Fermer le splashscreen et afficher l'application principale
            sequence.setOnFinished(e -> {
                splashStage.close();
                mainApp.startApplicationAfterSplash();
            });
            
            sequence.play();
        });
        
        // Démarrer le chargement
        Thread loadingThread = new Thread(loadingTask);
        loadingThread.setDaemon(true);
        loadingThread.start();
    }
}