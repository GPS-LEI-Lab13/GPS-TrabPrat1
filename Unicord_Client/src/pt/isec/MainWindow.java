package pt.isec;

import javafx.event.ActionEvent;
import javafx.scene.control.ScrollPane;

import java.io.IOException;

public class MainWindow {
    public ScrollPane channelsScrollPane;
    public ScrollPane messageFileScrollPane;

    public void createMenuItem(ActionEvent actionEvent) {
        //dialog Create Channel
        String name = "";
        int creatorId = App.getApp().getUser().id;
        Channel channel = new Channel(creatorId, name);
        try {
            App.getApp().sendCommand(Constants.NEW_CHANNEL, channel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void aboutMenuItem(ActionEvent actionEvent) {
    }
}
