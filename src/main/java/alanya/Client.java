package alanya;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import javafx.stage.Stage;

public final class Client extends View implements Terminal {
    private static final String SERVER = "localhost";

    private int user_id;
    private String nom;
    private String prenom;
    private String userPassword;

    private int interlocuteurId;

    private final String dbUrl = String.format("jdbc:mysql://%s:3306/ALANYA", SERVER);
    private final String dbUser = "n";
    private final String dbPassword = "1650oben"; // mot de passe pour la BDD

    private final Scanner input = new Scanner(System.in);
    private final File clientFile = new File("/home/n/.client.aly");

    private Socket serverSocket;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Si le fichier client existe, on le lit pour récupérer les infos
            if (clientFile.exists()) {
                loadUserFromFile();
                updateUserStatus(true);    // Met à jour connecte=true dans la BDD
                authenticateUser();
                serverSocket = new Socket(SERVER, MESSAGE_PORT);
            } else {
                // On demande les infos à l'utilisateur s'il s'agit de la première connexion
                System.out.print("Entrer votre nom: ");
                nom = input.next();
                System.out.print("Entrer votre prénom: ");
                prenom = input.next();
                System.out.print("Entrer votre mot de passe: ");
                userPassword = input.next();

                serverSocket = new Socket(SERVER, MESSAGE_PORT);
                // Récupère l'id unique envoyé par le serveur
                user_id = Integer.parseInt(
                    new BufferedReader(new InputStreamReader(serverSocket.getInputStream())).readLine()
                );
                enrollUser();
                // Notifie le serveur afin qu'il enregistre l'IP
                new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream()), true).println(true);
            }

            // Gestion de la fermeture de la fenêtre
            primaryStage.setOnCloseRequest(event -> {
                try {
                    updateUserStatus(false); // Ouvre une nouvelle connexion pour mettre à jour l'état
                } catch (SQLException ex) {
                    showError("Erreur lors de la mise à jour de l'état de connexion: " + ex.getMessage());
                }
                System.exit(0);
            });

            super.start(primaryStage);
            new Controller();
        } catch (IOException | SQLException e) {
            showError("Impossible de se connecter au serveur: " + e.getMessage());
        }
    }

    // Met à jour le champ "connecte" dans la base de données en ouvrant une nouvelle connexion
    private void updateUserStatus(boolean connected) throws SQLException {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement("UPDATE Utilisateur SET connecte = ? WHERE user_id = ?")) {
            ps.setBoolean(1, connected);
            ps.setInt(2, user_id);
            ps.executeUpdate();
        }
    }

    // Lit le fichier client pour récupérer l'id, le nom et le prénom
    private void loadUserFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(clientFile))) {
            user_id = Integer.parseInt(reader.readLine());
            nom = reader.readLine();
            prenom = reader.readLine();
        } catch (IOException e) {
            showError("Erreur lors de la lecture du fichier client: " + e.getMessage());
        }
    }

    // Authentifie l'utilisateur en comparant le mot de passe saisi avec celui stocké dans la BDD
    private void authenticateUser() {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement("SELECT mot_de_passe FROM Utilisateur WHERE user_id = ?")) {
            ps.setInt(1, user_id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    final String dbUserPassword = rs.getString("mot_de_passe");
                    while (true) {
                        System.out.print("Entrer votre mot de passe: ");
                        if (input.next().equals(dbUserPassword)) break;
                        System.err.println("Mot de passe incorrect !");
                    }
                } else {
                    showError("Utilisateur non trouvé dans la base de données.");
                }
            }
        } catch (SQLException e) {
            showError("Erreur d'authentification: " + e.getMessage());
        }
    }

    // Enregistre l'utilisateur dans la BDD et sauvegarde ses infos dans le fichier client
    private void enrollUser() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(clientFile))) {
            writer.write(user_id + "\n" + nom + "\n" + prenom + "\n");
        } catch (IOException e) {
            showError("Impossible de sauvegarder les infos du client: " + e.getMessage());
        }

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement ps = connection.prepareStatement(
                 "INSERT INTO Utilisateur(user_id, nom, prenom, mot_de_passe, connecte) VALUES (?, ?, ?, ?, ?)")
        ) {
            ps.setInt(1, user_id);
            ps.setString(2, nom);
            ps.setString(3, prenom);
            ps.setString(4, userPassword);
            ps.setBoolean(5, true);
            ps.executeUpdate();
        } catch (SQLException e) {
            showError("Erreur lors de l'inscription de l'utilisateur: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}