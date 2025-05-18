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
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ALANYA";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = readDatabasePassword();

    public static void main(String[] args) {
        new Server().runServer();
    }

    private static String readDatabasePassword() {
        System.out.println("Entrer le mot de passe");
        return new String(System.console().readPassword());
    }

    private static int fetchUserCount(Connection connection) throws SQLException {
        final String query = "SELECT COUNT(*) AS noUsers FROM Utilisateur";
        try (PreparedStatement ps = connection.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt("noUsers") : 0;
        }
    }

    private void runServer() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("En attente de connexion...");
            while (true) {
                handleClientConnection(serverSocket.accept());
            }
        } catch (IOException e) {
            System.out.println("Impossible de lancer le serveur");
        }
    }

    private void handleClientConnection(Socket clientSocket) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("Utilisateur connecté: " + clientSocket.getInetAddress().getHostAddress());
            int userId = fetchUserCount(connection) + 1;

            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);

            if (Boolean.parseBoolean(reader.readLine()))
                writer.println(userId);

            userId = Integer.parseInt(reader.readLine());

            updateUserIp(connection, clientSocket.getInetAddress().getHostAddress(), userId);

            
        // ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        // scheduler.scheduleAtFixedRate(this::refreshUserStatus, 1, 1, TimeUnit.SECONDS);
        } catch (IOException | SQLException e) {
            try {
                clientSocket.close();
            } catch (IOException ignore) {
            }
        }
    }

    // private void refreshUserStatus() {
    //     try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
    //         PrintWriter writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);

    //         if (Boolean.parseBoolean(reader.readLine()))
    //             writer.println(userId);

    //         userId = Integer.parseInt(reader.readLine());

    //         updateUserIp(connection, clientSocket.getInetAddress().getHostAddress(), userId);

            
    //     ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    //     scheduler.scheduleAtFixedRate(this::refreshContactsStatus, 1, 1, TimeUnit.SECONDS);
    //     } catch (IOException | SQLException e) {
    //         try 0{
    //             clientSocket.close();
    //         } catch (IOException ignore) {
    //         }
    //     }
    // }

    private static void updateUserIp(Connection connection, String ip, int userId) throws SQLException {
        final String query = "UPDATE Utilisateur SET ip = ? WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, ip);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }
}