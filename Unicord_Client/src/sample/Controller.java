package sample;

import javafx.scene.control.Alert;

import java.net.Socket;

public class Controller {
    private Socket socket;
    private final MainReceiver mainReceiver;
    private User  user;
    private List<Channel> channels;

    void openMessageDialog(Alert.AlertType type, String title, String message){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);

        alert.showAndWait();
    }
}
