package alanya;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public final class Server implements Terminal {
    private static int noUsers;
    private static final HashMap<Integer, String> users = new HashMap<>();

    public static void main(String[] args) {
        final String dbUrl = "jdbc:mysql://localhost:3306/ALANYA";
        final String dbUser = "n";
        final String dbPassword = readDatabasePassword();

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            noUsers = fetchInitialUserCount(connection);
            new Thread(() -> runServer(connection)).start();
            // new Thread(() -> {
            //     try {
            //         while (true) {
            //             updateClientDico(connection);
            //             System.out.println("Mis à jour");
            //         }
            //     } catch (SQLException e) {
            //         System.out.println("Impossible de mettre à jour le dictionnaire utilisateur: " + e.getMessage());
            //     }
            // }).start();
        } catch (SQLException e) {
            System.out.println("Erreur de connexion: " + e.getMessage());
        }
    }

    // Lit le mot de passe de la base de données depuis la console
    private static String readDatabasePassword() {
        System.out.println("Entrer le mot de passe");
        return new String(System.console().readPassword());
    }

    // Récupère le nombre initial d'utilisateurs dans la base de données
    private static int fetchInitialUserCount(Connection connection) throws SQLException {
        final String query = "SELECT COUNT(*) AS noUsers FROM Utilisateur";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt("noUsers") : 0;
        }
    }

    // Lance le serveur pour accepter les connexions clientes
    private static void runServer(Connection connection) {
        try (ServerSocket serverSocket = new ServerSocket(MESSAGE_PORT)) {
            System.out.println("En attente de connexion...");
            while (true) {
                handleClientConnection(serverSocket.accept(), connection);
                for (Map.Entry entry: users.entrySet())
                    System.out.println(entry.getKey() + " " + entry.getValue());
            }
        } catch (IOException e) {
            System.out.println("Impossible de connecter le client: " + e.getMessage());
        }
    }

    // Gère la connexion d'un client en lui attribuant un numéro et en enregistrant son IP dans la base de données si besoin
    private static void handleClientConnection(Socket clientSocket, Connection connection) {
        try {
            System.out.println("Nouvel utilisateur connecté: " + clientSocket.getInetAddress());
            int userId = ++noUsers;

            // Envoi du numéro unique au client
            new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true).println(userId);

            // Récupération du signal du client
            boolean signal = Boolean.parseBoolean(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine());

            if (signal) {
                updateUserIp(connection, clientSocket.getInetAddress().toString(), userId);
            }
        } catch (IOException | SQLException e) {
            try {
                clientSocket.close();
            } catch (IOException ignore) {}
        }
    }

    // Met à jour l'adresse IP de l'utilisateur dans la base de données
    private static void updateUserIp(Connection connection, String ip, int userId) throws SQLException {
        final String query = "UPDATE Utilisateur SET ip = ? WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, ip);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    // Mettre dans le dictionnaire tous les clients connectés
    private static void updateClientDico(Connection connection) throws SQLException {
        String query = "SELECT user_id, ip from Utilisateur WHERE connecte = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setBoolean(1, true);

        ResultSet results = statement.executeQuery();
        while (results.next()) {
            users.put(results.getInt("user_id"), results.getString("ip"));
        }
    }
}