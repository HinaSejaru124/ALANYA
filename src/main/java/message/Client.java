package message ;
import java.io.*;
import java.net.Socket;

public class Client {

    // main
    public static void main(String[] ars) {
        // essaye la connexion reseau via le socket et a travers le port ... et l adresse ip est ...
        try (Socket socket = new Socket("172.20.10.3", 7000)) {
            System.out.println("connectée au serveur");// si la connexion est reussie au serveur

            // on essaye d envoyer les messages et la lecture des messages
           MessageWriteThread receiveThread = new MessageWriteThread(socket) ;
           MessageReadThread lectureThread = new MessageReadThread(socket);

           lectureThread.start();
            receiveThread.start();

            lectureThread.join();
            receiveThread.join();

        } catch (Exception e) {
            System.out.println("Erreur lors de la creation du socket : " + e); // s il y a erreur de
            // connexion au socket
        }
    }
}