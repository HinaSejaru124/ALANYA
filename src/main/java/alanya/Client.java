package alanya;

import java.io.IOException;
import java.net.Socket;

import javafx.stage.Stage;

public class Client extends ChatApp
{
    private static final String SERVER = "localhost"; 
    
    @SuppressWarnings("exports")
    @Override
    public void start(Stage primaryStage)
    {
        try
        {
            this.messageSocket = new Socket(SERVER, MESSAGE_PORT);
            this.fileSocket = new Socket(SERVER, FILE_PORT);
            this.username = "Client";
            super.start(primaryStage);
        } 
        catch (IOException e)
        {
            showError("Impossible de se connecter au serveur");
        }
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}