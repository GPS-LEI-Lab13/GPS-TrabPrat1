package pt.isec;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.print.attribute.standard.NumberUp;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainWindow implements Initializable {
    public ScrollPane channelsScrollPane;
    public ScrollPane messageFileScrollPane;

    private static App app;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Command command = app.sendAndReceive(Constants.GET_CHANNELS, null);
            app.setChannels((List<Channel>) command.extras);
            for (var channel: app.getChannels()) {
                updateChannelList(channel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void createMenuItem(ActionEvent actionEvent) {
        //dialog Create Channel
        try {
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

        String name = "";
        int creatorId = App.getApp().getUser().id;
        Channel channel = new Channel(creatorId, name);
        try {
            Command command = app.sendAndReceive(Constants.NEW_CHANNEL, channel);
            if (command.protocol.equals(Constants.ERROR)){
                app.openMessageDialog(Alert.AlertType.ERROR,"Channel Creation", (String) command.extras);
            }else {
                updateChannelList(channel);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void updateChannelList(Channel channel) {

    }

    public void aboutMenuItem(ActionEvent actionEvent) {
    }

}
