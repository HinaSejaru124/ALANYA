package alanya;

import javafx.scene.Node;

public class PageManager
{
    @SuppressWarnings("exports")
    public static void showPage(Node page)
    {
        ChatApp.getRoot().getChildren().clear();
        ChatApp.getRoot().getChildren().add(page);
    }
}
