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

public final class Server implements Terminal {
    private static int noUsers;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/ALANYA";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = readDatabasePassword();

    public static void main(String[] args) {

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            noUsers = fetchInitialUserCount(connection);
            new Thread(() -> runServer()).start();
        } catch (SQLException e) {
            System.out.println("Erreur de connexion: " + e.getMessage());
        }
    }

    private static String readDatabasePassword() {
        System.out.println("Entrer le mot de passe");
        return new String(System.console().readPassword());
    }

    private static int fetchInitialUserCount(Connection connection) throws SQLException {
        final String query = "SELECT COUNT(*) AS noUsers FROM Utilisateur";
        try (PreparedStatement ps = connection.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt("noUsers") : 0;
        }
    }

    private static void runServer() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("En attente de connexion...");
            while (true) {
                handleClientConnection(serverSocket.accept());
            }
        } catch (IOException e) {
            System.out.println("Impossible de connecter le client: " + e.getMessage());
        }
    }

    @SuppressWarnings("resource")
    private static void handleClientConnection(Socket clientSocket) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("Utilisateur connecté: " + clientSocket.getInetAddress().getHostAddress());
            int userId = ++noUsers;

            new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true).println(userId);

            userId = Integer
                    .parseInt(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine());

            updateUserIp(connection, clientSocket.getInetAddress().getHostAddress(), userId);
        } catch (IOException | SQLException e) {
            try {
                clientSocket.close();
            } catch (IOException ignore) {
            }
        }
    }

    private static void updateUserIp(Connection connection, String ip, int userId) throws SQLException {
        final String query = "UPDATE Utilisateur SET ip = ? WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, ip);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }
}