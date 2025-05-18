package alanya;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import audio.AudioReceiveThread;
import audio.AudioSendThread;
import audio.AudioSetup;
import controllers.Contact;
import controllers.Message;
import file.FileReceiveThread;
import file.FileSendThread;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import message.MessageReadThread;
import message.MessageWriteThread;
import views.CallInitiationUI;
import views.CallReceptionUI;
import views.MainApp;
import views.OngoingCallUI;
import views.Util;

public final class Client extends MainApp implements Terminal {
    private static final String SERVER = "localhost";

    private final String dbUrl = String.format("jdbc:mysql://%s:3306/ALANYA", SERVER);
    private final String dbUser = "root";
    private final String dbPassword = "jeff@14022003";

    private int user_id;
    private String nom;
    private String prenom;
    private String userPassword;
    private String profilePic;

    private final File clientFile = new File(System.getProperty("user.home"), ".client.aly");
    private final List<Contact> contacts = new ArrayList<>();

    private static final int THREAD_POOL_SIZE = 5;
    private final ExecutorService pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private final AudioSetup audioSetup = new AudioSetup();
    private volatile boolean inCall = false;
    private DatagramSocket audioSocket;
    private AudioSendThread audioSendThread;
    private AudioReceiveThread audioReceiveThread;

    // Références aux nouvelles interfaces d'appel
    private OngoingCallUI ongoingCallUI;
    private CallInitiationUI callInitiationUI;
    private CallReceptionUI callReceptionUI;

    @Override
    public void start(Stage primaryStage) {
        super.start(primaryStage);

        // Initialiser les références aux interfaces d'appel depuis callManager
        ongoingCallUI = callManager.getOngoingCallUI();
        callInitiationUI = callManager.getInitiationUI();
        callReceptionUI = callManager.getReceptionUI();

        createAppDirectories();
        setupUIHandlers(primaryStage);
        initializeUser();
        loadContacts();
        scheduleStatusRefresh();
        startListeners();
    }

    private void createAppDirectories() {
        String home = System.getProperty("user.home");
        String[] dirs = { "Alanya", "Alanya/Alanya Images", "Alanya/Alanya Videos",
                "Alanya/Alanya Audios", "Alanya/Alanya Documents", "Alanya/Alanya Profile_pictures" };
        Arrays.stream(dirs).forEach(path -> new File(home, path).mkdirs());
    }

    private void setupUIHandlers(Stage primaryStage) {
        homeUI.addButton.setOnAction(e -> handleAddContact());
        homeUI.sendButton.setOnAction(e -> sendMessage());
        homeUI.inputField.setOnKeyPressed(event -> {
            contacts.stream()
                    .filter(c -> c.getUserId() == interlocuteurId)
                    .findFirst()
                    .ifPresent(c -> c.setCache(homeUI.inputField.getText()));
            if (event.getCode() == KeyCode.ENTER)
                sendMessage();
        });
        homeUI.chooseFileButton.setOnAction(e -> handleFileSend());
        homeUI.profileButton.setOnAction(e -> {
            showLogin();
            try {
                updateUserStatus(false);
            } catch (SQLException ex) {
            }
        });
        homeUI.audioCallButton.setOnAction(e -> handleAudioCall());
        primaryStage.setOnCloseRequest(event -> handleAppClose());
    }

    private void initializeUser() {
        if (clientFile.exists()) {
            loadUserFromFile();
            loginUI.getLoginButton().setOnAction(e -> handleLogin());
        } else {
            signUI.getBackButton().setVisible(false);
            showSignIn();
            signUI.getRegisterButton().setOnAction(e -> handleRegister());
        }
    }

    public void sendMessage() {
        String text = homeUI.inputField.getText();
        contacts.stream()
                .filter(c -> c.getUserId() == interlocuteurId)
                .findFirst()
                .ifPresent(c -> c.getConversation().add(new Message(text, LocalTime.now(), true)));
        homeController.addMessage(text, LocalTime.now(), true);

        try {
            String ip = findUserById(interlocuteurId);
            try (Socket socket = new Socket(ip, MESSAGE_PORT)) {
                new MessageWriteThread(socket).send(text);
            }
        } catch (IOException ex) {
            Util.showError("Message non envoyé");
        } finally {
            homeUI.inputField.clear();
        }
    }

    private void handleAddContact() {
        String saisie = homeUI.idField.getText();
        if (saisie == null || saisie.trim().isEmpty()) {
            Util.showError("Veuillez saisir l'identifiant du contact.");
            return;
        }
        int contactId;
        try {
            contactId = Integer.parseInt(saisie.trim());
            if (contactId == user_id) {
                Util.showError("Vous ne pouvez pas vous ajouter comme contact.");
                return;
            }
        } catch (NumberFormatException ex) {
            Util.showError("ID invalide : Entrez un nombre entier");
            return;
        }
        homeUI.addContactBox.setVisible(false);
        homeUI.idField.clear();

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT nom, prenom, connecte FROM Utilisateur WHERE user_id = ?")) {
            ps.setInt(1, contactId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                try (PreparedStatement ins = conn.prepareStatement(
                        "INSERT INTO Contacts(user_id, contact_id) VALUES (?, ?)")) {
                    ins.setInt(1, user_id);
                    ins.setInt(2, contactId);
                    ins.executeUpdate();
                }
                Contact c = homeController.addContact(
                        contactId,
                        rs.getString("nom") + " " + rs.getString("prenom"),
                        rs.getBoolean("connecte"));
                contacts.add(c);
                Util.showInfo("Contact ajouté avec succès");
            } else {
                Util.showError("Utilisateur non trouvé");
            }
        } catch (SQLException ex) {
            Util.showError("Vous ne pouvez pas ajouter ce contact à nouveau");
        }
    }

    private void handleFileSend() {
        File file = homeController.chooseFile();
        if (file != null) {
            contacts.stream()
                    .filter(c -> c.getUserId() == interlocuteurId)
                    .findFirst()
                    .ifPresent(c -> c.getConversation().add(new Message(file, LocalTime.now(), true)));
            homeController.addFile(file, LocalTime.now(), true);

            try {
                String ip = findUserById(interlocuteurId);
                try (Socket socket = new Socket(ip, FILE_PORT)) {
                    new FileSendThread(socket).send(file.getAbsolutePath());
                }
            } catch (IOException ex) {
                Util.showError("Fichier non envoyé");
            }
        }
    }

    private void handleLogin() {
        if (authenticateUser()) {
            try {
                updateUserStatus(true);
            } catch (SQLException ex) {
                Util.showError("Erreur mise à jour statut: " + ex.getLocalizedMessage());
            }

            try (Socket socket = new Socket(SERVER, SERVER_PORT);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println(false);
                out.println(user_id);
            } catch (IOException ignored) {
            }

            infosUserController.loadInfo(profilePic, nom, prenom, user_id);
            showHome();
        } else {
            loginUI.getErrorAuth().setVisible(true);
            loginUI.getPasswordField().clear();
        }
    }

    private void handleRegister() {
        nom = signUI.getFirstNameField().getText();
        prenom = signUI.getLastNameField().getText();
        userPassword = signUI.getPasswordField().getText();
        profilePic = signController.getSelectedProfileImage();

        if (nom.isEmpty() || prenom.isEmpty() || userPassword.isEmpty()) {
            Util.showError("Veuillez remplir tous les champs.");
        } else {
            try (Socket socket = new Socket(SERVER, SERVER_PORT);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println(true);
                user_id = Integer.parseInt(in.readLine());
                enrollUser();
                out.println(user_id);
                updateUserStatus(true);
                showHome();
                infosUserController.loadInfo(profilePic, nom, prenom, user_id);
            } catch (IOException | SQLException ex) {
                Util.showError("Erreur lors de la connexion: " + ex.getLocalizedMessage());
            }
        }
    }

    private void handleAudioCall() {
        // Mettre à jour le nom du contact dans callManager
        String contactName = contacts.stream()
                .filter(c -> c.getUserId() == interlocuteurId)
                .map(Contact::getName)
                .findFirst()
                .orElse("Contact");
        
        callManager.setContactName(contactName);
        
        // Utiliser l'interface d'initiation d'appel
        showCallInitiationUI();
        
        // Démarrer la progression simulée de l'appel
        callInitiationUI.simulateCallProgress();

        new Thread(() -> {
            try {
                String ip = findUserById(interlocuteurId);
                try (Socket socket = new Socket(ip, AUDIO_INFO_PORT)) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    if (Boolean.parseBoolean(reader.readLine())) {
                        // L'appel a été accepté, passer à l'interface d'appel en cours
                        Platform.runLater(() -> {
                            showOngoingCallUI();
                            
                            // Configurer l'interface d'appel en cours après connexion
                            setupAudioCallInterface();
                        });

                        audioSetup.getMicrophone().start();
                        audioSetup.getSpeakers().start();

                        // DatagramSocket audioSocket = new DatagramSocket(AUDIO_PORT);

                        // audioSendThread = new AudioSendThread(audioSocket, audioSetup);
                        // audioSendThread.start();

                        // audioReceiveThread = new AudioReceiveThread(audioSocket, audioSetup,
                        // socket.getInetAddress().getHostAddress(), AUDIO_PORT);
                        // audioReceiveThread.start();
                    } else {
                        // L'appel a été refusé, retourner à l'accueil
                        Platform.runLater(this::showHome);
                    }
                }
            } catch (IOException ex) {
                Platform.runLater(() -> Util.showError("Erreur lors de l'appel: " + ex.getMessage()));
                Platform.runLater(this::showHome);
            }
        }).start();
    }

    private void loadContacts() {
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT U.user_id, U.nom, U.prenom, U.connecte " +
                                "FROM Utilisateur U JOIN Contacts C ON U.user_id = C.contact_id " +
                                "WHERE C.user_id = ?")) {
            ps.setInt(1, user_id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                contacts.add(homeController.addContact(
                        rs.getInt("user_id"),
                        rs.getString("nom") + " " + rs.getString("prenom"),
                        rs.getBoolean("connecte")));
            }
        } catch (SQLException ex) {
            Util.showError("Erreur chargement contacts: " + ex.getLocalizedMessage());
            System.exit(0);
        }
    }

    private void scheduleStatusRefresh() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::refreshContactsStatus, 1, 1, TimeUnit.SECONDS);
    }

    private void startListeners() {
        new Thread(this::runMessageListener, "MessageListener").start();
        new Thread(this::runFileListener, "FileListener").start();
        new Thread(this::runAudioCallListener, "AudioCallListener").start();
    }

    private void handleAppClose() {
        try {
            updateUserStatus(false);
        } catch (SQLException ex) {
            Util.showError("Erreur mise à jour statut: " + ex.getLocalizedMessage());
        }
        System.exit(0);
    }

    private void refreshContactsStatus() {
        if (contacts.isEmpty())
            return;
        String inClause = contacts.stream()
                .map(c -> String.valueOf(c.getUserId()))
                .collect(Collectors.joining(",", "(", ")"));
        String sql = "SELECT user_id, connecte FROM Utilisateur WHERE user_id IN " + inClause;
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("user_id");
                boolean now = rs.getBoolean("connecte");
                contacts.stream()
                        .filter(c -> c.getUserId() == id)
                        .findFirst()
                        .ifPresent(contact -> {
                            if (contact.getStatus() != now) {
                                Platform.runLater(() -> {
                                    contact.setStatus(now);
                                    contact.getStatusLabel().setText(now ? "En ligne" : "Hors ligne");
                                });
                            }
                        });
            }
        } catch (SQLException ex) {
            Platform.runLater(() -> Util
                    .showError("Impossible de mettre à jour les états des contacts: " + ex.getLocalizedMessage()));
            System.exit(0);
        }
    }

    private void runMessageListener() {
        try (ServerSocket messageServerSocket = new ServerSocket(MESSAGE_PORT)) {
            while (true) {
                Socket client = messageServerSocket.accept();
                pool.submit(() -> handleMessageConnection(client));
            }
        } catch (IOException ex) {
            Platform.runLater(() -> Util.showError(ex.getLocalizedMessage()));
        }
    }

    private void runFileListener() {
        try (ServerSocket fileServerSocket = new ServerSocket(FILE_PORT)) {
            while (true) {
                Socket client = fileServerSocket.accept();
                pool.submit(() -> handleFileConnection(client));
            }
        } catch (IOException ex) {
            Platform.runLater(() -> Util.showError(ex.getLocalizedMessage()));
        }
    }

    private void runAudioCallListener() {
        try (ServerSocket audioCallServerSocket = new ServerSocket(AUDIO_INFO_PORT)) {
            while (true) {
                Socket client = audioCallServerSocket.accept();
                pool.submit(() -> handleAudioConnection(client));
            }
        } catch (IOException ex) {
            Platform.runLater(() -> Util.showError(ex.getLocalizedMessage()));
        }
    }

    private int findUserByIp(String ip) {
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT user_id FROM Utilisateur WHERE ip = ?")) {
            ps.setString(1, ip);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt("user_id");
        } catch (SQLException ex) {
            Util.showError(ex.getLocalizedMessage());
        }
        return 0;
    }

    private String findUserById(int id) {
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT ip FROM Utilisateur WHERE user_id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getString("ip");
        } catch (SQLException ex) {
            Util.showError(ex.getLocalizedMessage());
        }
        return "";
    }

    private void handleMessageConnection(Socket socket) {
        try {
            String message = new MessageReadThread(socket).receive();
            int communicantId = findUserByIp(socket.getInetAddress().getHostAddress());

            Optional<Contact> optContact = contacts.stream()
                    .filter(c -> c.getUserId() == communicantId)
                    .findFirst();

            if (optContact.isPresent()) {
                Contact contact = optContact.get();
                Message msgObj = new Message(message, LocalTime.now(), false);
                contact.getConversation().add(msgObj);

                if (communicantId == interlocuteurId) {
                    Platform.runLater(() -> homeController.addMessage(message, LocalTime.now(), false));
                }
            } else {
                Platform.runLater(() -> {
                    try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                        PreparedStatement ps = connection
                                .prepareStatement("SELECT * from Utilisateur WHERE user_id = ?");
                        ps.setInt(1, user_id);
                        ResultSet rs = ps.executeQuery();
                        rs.next();

                        Contact unsaved = homeController.addContact(rs.getInt("user_id"),
                                rs.getString("nom") + " " + rs.getString("prenom"),
                                rs.getBoolean("connecte"));
                        unsaved.getConversation().add(new Message(message, LocalTime.now(), false));

                        contacts.add(unsaved);

                    } catch (SQLException ex) {
                        Util.showError(ex.getLocalizedMessage());
                    }
                });
            }
        } catch (IOException e) {
            Platform.runLater(() -> Util.showError("Impossible de lire le message"));
        } finally {
            closeQuietly(socket);
        }
    }

    private void handleFileConnection(Socket socket) {
        try {
            File file = new FileReceiveThread(socket).receive();
            int communicantId = findUserByIp(socket.getInetAddress().getHostAddress());

            Optional<Contact> optContact = contacts.stream()
                    .filter(c -> c.getUserId() == communicantId)
                    .findFirst();

            if (optContact.isPresent()) {
                Contact contact = optContact.get();
                Message msgObj = new Message(file, LocalTime.now(), false);
                contact.getConversation().add(msgObj);

                if (communicantId == interlocuteurId) {
                    Platform.runLater(() -> homeController.addFile(file, LocalTime.now(), false));
                }
            } else {
                Platform.runLater(() -> {
                    try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                        PreparedStatement ps = connection
                                .prepareStatement("SELECT * from Utilisateur WHERE user_id = ?");
                        ps.setInt(1, user_id);
                        ResultSet rs = ps.executeQuery();
                        rs.next();

                        Contact unsaved = homeController.addContact(rs.getInt("user_id"),
                                rs.getString("nom") + " " + rs.getString("prenom"),
                                rs.getBoolean("connecte"));
                        unsaved.getConversation().add(new Message(file, LocalTime.now(), false));

                        contacts.add(unsaved);

                    } catch (SQLException ex) {
                        Util.showError(ex.getLocalizedMessage());
                    }
                });
            }
        } catch (IOException e) {
            Platform.runLater(() -> Util.showError("Impossible de recevoir le fichier"));
        } finally {
            closeQuietly(socket);
        }
    }

    private void handleAudioConnection(Socket signalSocket) {
        if (inCall) {
            try (PrintWriter writer = new PrintWriter(signalSocket.getOutputStream(), true)) {
                writer.println(false);
            } catch (IOException ignored) {
            }
            closeQuietly(signalSocket);
            return;
        }

        inCall = true;
        
        // Trouver l'ID de l'appelant
        int callerId = findUserByIp(signalSocket.getInetAddress().getHostAddress());
        
        // Trouver le nom de l'appelant
        String callerName = contacts.stream()
                .filter(c -> c.getUserId() == callerId)
                .map(Contact::getName)
                .findFirst()
                .orElse("Contact inconnu");
        
        // Mettre à jour le nom de l'appelant
        callManager.setContactName(callerName);
        
        // Afficher l'interface de réception d'appel
        Platform.runLater(() -> {
            showCallReceptionUI();
            
            // Configurer les boutons pour la réception d'appel
            setupIncomingCallInterface(signalSocket);
        });
    }
    
    /**
     * Configure l'interface pour un appel entrant
     */
    private void setupIncomingCallInterface(Socket signalSocket) {
        // Configurer le bouton pour accepter l'appel
        Button acceptButton = callReceptionUI.getAcceptCallButton();
        acceptButton.setOnAction(e -> {
            try {
                // Confirmer l'acceptation de l'appel
                PrintWriter writer = new PrintWriter(signalSocket.getOutputStream(), true);
                writer.println(true);
                
                // Animer l'acceptation de l'appel
                callReceptionUI.acceptCall();
                
                // Passer à l'interface d'appel en cours après un délai
                Platform.runLater(() -> {
                    // Attendre que l'animation se termine
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    
                    // Passer à l'interface d'appel en cours
                    showOngoingCallUI();
                    
                    // Configurer l'interface d'appel en cours
                    setupAudioCallInterface();
                });
                
                // Démarrer le pipeline audio
                audioSetup.getMicrophone().start();
                audioSetup.getSpeakers().start();
                
                // Ajouter ici le code pour démarrer les threads audio
                // ...
                
            } catch (IOException ex) {
                Platform.runLater(() -> Util.showError("Impossible d'accepter l'appel"));
                cleanupAfterCall();
            }
        });
        
        // Configurer le bouton pour rejeter l'appel
        Button rejectButton = callReceptionUI.getRejectCallButton();
        rejectButton.setOnAction(e -> {
            try {
                // Rejeter l'appel
                PrintWriter writer = new PrintWriter(signalSocket.getOutputStream(), true);
                writer.println(false);
                
                // Animer le rejet de l'appel
                callReceptionUI.rejectCall();
                
                // Nettoyer les ressources
                cleanupAfterCall();
                
                // Retourner à l'écran d'accueil après un délai
                Platform.runLater(() -> {
                    // Attendre que l'animation se termine
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    
                    // Retourner à l'écran d'accueil
                    showHome();
                });
                
            } catch (IOException ex) {
                Platform.runLater(() -> Util.showError("Erreur lors du rejet de l'appel"));
                cleanupAfterCall();
                showHome();
            }
        });
    }
    
    /**
     * Configure l'interface pour un appel en cours
     */
    private void setupAudioCallInterface() {
        // Configurer le bouton pour terminer l'appel
        Button endCallButton = ongoingCallUI.getEndCallButton();
        endCallButton.setOnAction(e -> {
            // Animer la fin de l'appel
            ongoingCallUI.endCall();
            
            // Nettoyer les ressources
            cleanupAfterCall();
            
            // Retourner à l'écran d'accueil après un délai
            Platform.runLater(() -> {
                // Attendre que l'animation se termine
                try {
                    Thread.sleep(800);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                
                // Retourner à l'écran d'accueil
                showHome();
            });
        });
        
        // Configurer le bouton pour couper le micro
        Button muteButton = ongoingCallUI.getMuteButton();
        muteButton.setOnAction(e -> {
            // Logique pour couper/activer le micro
            // ...
        });
        
        // Configurer le bouton pour activer/désactiver le haut-parleur
        Button speakerButton = ongoingCallUI.getSpeakerButton();
        speakerButton.setOnAction(e -> {
            // Logique pour activer/désactiver le haut-parleur
            // ...
        });
    }

    private void cleanupAfterCall() {
        inCall = false;
        if (audioSendThread != null)
            audioSendThread.interrupt();
        if (audioReceiveThread != null)
            audioReceiveThread.interrupt();
        if (audioSocket != null && !audioSocket.isClosed())
            audioSocket.close();
    }

    private void closeQuietly(Socket s) {
        try {
            if (s != null && !s.isClosed())
                s.close();
        } catch (IOException ignored) {
        }
    }

    private void updateUserStatus(boolean connected) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE Utilisateur SET connecte = ? WHERE user_id = ?")) {
            ps.setBoolean(1, connected);
            ps.setInt(2, user_id);
            ps.executeUpdate();
        }
    }

    private void loadUserFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(clientFile))) {
            user_id = Integer.parseInt(reader.readLine());
            nom = reader.readLine();
            prenom = reader.readLine();
        } catch (IOException ex) {
            Util.showError("Erreur lecture fichier client: " + ex.getLocalizedMessage());
        }
    }

    private boolean authenticateUser() {
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT mot_de_passe FROM Utilisateur WHERE user_id = ?")) {
            ps.setInt(1, user_id);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && loginUI.getPasswordField().getText().equals(rs.getString("mot_de_passe"))) {
                return true;
            }
        } catch (SQLException ex) {
            Util.showError("Erreur d'authentification: " + ex.getLocalizedMessage());
        }
        return false;
    }

    private void enrollUser() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(clientFile))) {
            writer.write(user_id + "\n" + nom + "\n" + prenom + "\n");
        } catch (IOException ex) {
            Util.showError("Impossible de sauvegarder les infos client: " + ex.getLocalizedMessage());
        }
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO Utilisateur(user_id, nom, prenom, mot_de_passe, connecte, profil) VALUES (?, ?, ?, ?, ?, ?)")) {
            ps.setInt(1, user_id);
            ps.setString(2, nom);
            ps.setString(3, prenom);
            ps.setString(4, userPassword);
            ps.setBoolean(5, true);
            ps.setString(6, profilePic);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Util.showError("Erreur inscription utilisateur: " + ex.getLocalizedMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}