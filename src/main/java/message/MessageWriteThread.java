package message;
import java.io.BufferedReader;// convertir le flux de donn2es envoyee via le socket en un texte
import java.io.IOException;// gerer les exceptions de lecture ecriture du message
import java.io.InputStreamReader;// pour lire les donnes qui arrivent/du clavier
import java.io.OutputStreamWriter;// ecrire dans le socket
import java.io.PrintWriter; // afficher le texte dans le socket
import java.net.Socket; // le socket

public class MessageWriteThread extends Thread
{
    private Socket socket;// declaration de l objet de type socket comme attribut de la classe

    public MessageWriteThread(Socket socket) { // constructeur
        this.socket = socket;
    }//

    @Override // redefinir les differentes methodes
    // l executant du thread
    // c est lui qui contient le code qui consiste  a envoyer les messages
    public void run()
    {
       // System.out.println(send());
        send() ;// voici donc la fonction send
    }

    public String send()
    {
        String fullMessage = ""; // le message total qu on va envoyer
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            // on recupere ce qui est dqns le clavier dans l imputstream le mettre dans le buffeer
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            // on ecrit et on envoie dans le socket
            String message;
            while ((message = reader.readLine()) != null)
            {
                fullMessage += message;// on ajoute au message final chaque mot du message
                writer.println(message);// chaque ligne du message est print au fur et a mesure
            }

            writer.close() ;// fermeture des buffer d ecriture et de lecture
            reader.close() ;

        } catch (IOException e) { // gestion de l exception
            System.out.println("Écriture impossible: " + e.getMessage());
        }

        return fullMessage;

    }
}


