package message; // le package message
import java.io.BufferedReader; //
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class MessageReadThread extends Thread {

    // Thread reception
    private Socket socket; // attribut de la classe

    public MessageReadThread(Socket socket)
    { // constructeur
        this.socket = socket;
    }

    @Override
    // la methode run qui permet la lecture des messages
    public void run() {

        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            // on va dans le imput stream et on recupere le contenu du socket
            // on transforme cela a un objet bufferreader
            String reponse;
            while ((reponse = in.readLine()) != null) {
                // tant que le contenu du bufferreader est non vide on affiche le message
                System.out.println("==> " + reponse);
            }

        } catch (IOException e) {
            System.err.println("Error in receive thread: " + e.getMessage());
        }

    }
}




