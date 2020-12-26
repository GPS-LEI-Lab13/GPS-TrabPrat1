package pt.isec;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindow {
    public ScrollPane channelsScrollPane;
    public ScrollPane messageFileScrollPane;

    public void createMenuItem(ActionEvent actionEvent) {
        //dialog Create Channel
        try {
            App app = App.getApp();
            Stage createChannel = new Stage();
            createChannel.initModality(Modality.APPLICATION_MODAL);
            createChannel.setTitle("Unicord - Create channel");
            Scene cC = new Scene(app.loadFxml("fxml/CreateChannel.fxml"));
            createChannel.setScene(cC);
            createChannel.setResizable(false);
            createChannel.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        String name = "";
        int creatorId = App.getApp().getUser().id;
        Channel channel = new Channel(creatorId, name);
        try {
            App.getApp().sendCommand(Constants.NEW_CHANNEL, channel);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

    public void aboutMenuItem(ActionEvent actionEvent) {
    }
}
