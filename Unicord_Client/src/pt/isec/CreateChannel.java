package pt.isec;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class CreateChannel {

    public TextField channelNameTextField;
    public Button createButton;

    public void createButton(ActionEvent actionEvent) {

        String channelName = channelNameTextField.getText();

        Channel channel = new Channel(channelName);

        try {
            App.getApp().sendCommand(Constants.NEW_CHANNEL, channel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}