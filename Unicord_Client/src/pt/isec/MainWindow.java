package pt.isec;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.print.attribute.standard.NumberUp;
//import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainWindow implements Initializable {
    public ScrollPane channelsScrollPane;
    public ScrollPane messageFileScrollPane;
    public VBox channelsVBox;
    public VBox messagesFilesVBox;

    private static App app;
    public TextField messageTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        app = App.getApp();
        try {
            Command command = app.sendAndReceive(Constants.GET_CHANNELS, null);
            app.setChannels((List<Channel>) command.extras);
            for (var channel: app.getChannels()) {
                updateChannelList(channel);
            }
        } catch (IOException | InterruptedException e) {
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
        HBox box = new HBox();
        box.setPrefWidth(Region.USE_COMPUTED_SIZE);
        box.setFillHeight(true);

        Label label = new Label(channel.name);
        label.setOnMouseClicked(event -> {
            try {
                channelListOnClick(channel.name);
                app.setSelectedChannel(channel);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        box.getChildren().add(label);
        if (channel.creatorId == app.getUser().id){
            ImageView image = new ImageView("//Images//gear.png");
            image.setOnMouseClicked(event -> {
                app.setSelectedChannel(channel);
                openEditChannel();
            });
            box.getChildren().add(image);
        }

        channelsVBox.getChildren().add(box);
    }

    private void channelListOnClick(String name) throws IOException, InterruptedException {
        //TODO FAZER ISTO
        app.sendAndReceive(Constants.GET_MESSAGES,app.getSelectedChannel().id);

    }


    private void openEditChannel() {
        try {
            app.setWindowRoot("EditChannel.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void aboutMenuItem(ActionEvent actionEvent) {
    }

    public void SendButton(ActionEvent actionEvent) {
        //Message message = new Message();
        /*try {
            Command command = app.sendAndReceive(Constants.NEW_CHANNEL, channel);
            if (command.protocol.equals(Constants.ERROR)){
                app.openMessageDialog(Alert.AlertType.ERROR,"Channel Creation", (String) command.extras);
            }else {
                updateChannelList(channel);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    public void SendFileButton(ActionEvent actionEvent) {

    }
}
