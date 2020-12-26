package pt.isec;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EditChannel implements Initializable {
    public TextField usernameTextField1;
    public ListView membersListView;
    public ListView inviteListView;
    public Button applyBtn;
    public Button deleteBtn;
    public Button closeBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        App app = App.getApp();
        try {
            Command command = app.sendAndReceive(Constants.EDIT_CHANNEL, null);
            app.setChannels((List<Channel>) command.extras);
            for (var channel: app.getChannels()) {
                //updateChannelList(channel);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void ApplyButton(ActionEvent actionEvent) {
        App app = App.getApp();
        String channel_name = usernameTextField1.getText();
        /*try {
            if (!channel_name.isEmpty()) {
                //Command command = app.sendAndReceive(Constants.EDIT_CHANNEL, EditChannel);
            } else {
                app.openMessageDialog(Alert.AlertType.ERROR, Constants.ERROR, "Channel name cannot be empty!");

            }
            if (!command.protocol.equals(Constants.SUCCESS)) {
                app.openMessageDialog(Alert.AlertType.ERROR, Constants.ERROR, (String) command.extras);
            } else {
                app.setWindowRoot("Login.fxml");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }*/

    }

    public void DeleteButton(ActionEvent actionEvent) {
        App app = App.getApp();
        try {
            boolean bool = app.openMessageDialogDeleteChannel(Alert.AlertType.CONFIRMATION, "Delete channel", "Do you want to delete this channel?");
            if (bool) {
                Command command = app.sendAndReceive(Constants.DELETE_CHANNEL, app.getSelectedChannel().id);
                if (!command.protocol.equals(Constants.SUCCESS)){
                    app.openMessageDialog(Alert.AlertType.ERROR,Constants.ERROR, (String) command.extras);
                }else {
                    app.setWindowRoot("MainWindow.fxml");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void CloseButton(ActionEvent actionEvent) {
        try {
            App.getApp().setWindowRoot("MainWindow.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
