package pt.isec;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EditChannel implements Initializable {
    public TextField usernameTextField1;
    public ListView membersListView;
    public ListView inviteListView;
    public Button applyBtn;
    public Button deleteBtn;
    public Button closeBtn;
    public VBox membersVbox;
    public VBox inviteVbox;
    private ChannelEditor channelEditor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        App app = App.getApp();
        try {
            Command command = app.sendAndReceive(Constants.EDIT_CHANNEL_GET_USERS, app.getSelectedChannel().id); //TODO Check problems
            if (command.protocol.equals(Constants.ERROR)){
                app.openMessageDialog(Alert.AlertType.ERROR,"Channel Creation", (String) command.extras);
            }else {
                channelEditor = (ChannelEditor) command.extras;
                usernameTextField1.setText(channelEditor.name);
                scrollPanesEditChannel(channelEditor.usersIn, true, membersVbox);
                scrollPanesEditChannel(channelEditor.usersOut, false, inviteVbox);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void scrollPanesEditChannel(ArrayList<String> users, boolean bool, VBox vBox){
        for (var user : users) {
            HBox hBox = new HBox();
            Label label = new Label(user);
            ImageView imageView = new ImageView(bool ? "Images/delete_user.png" : "Images/add_user.png");
            if (bool){
                imageView.setOnMouseClicked(event -> {

                });
            }else{
                imageView.setOnMouseClicked(event -> {

                });
            }
            hBox.getChildren().addAll(label, imageView);
            vBox.getChildren().add(hBox);
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
