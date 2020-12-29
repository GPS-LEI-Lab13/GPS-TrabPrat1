package pt.isec;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EditChannel implements Initializable {
	public Button applyBtn;
	public Button deleteBtn;
	public Button closeBtn;
	public VBox membersVbox;
	public VBox inviteVbox;
	public TextField channelNameTextField;
	private ChannelEditor oldChannelEditor;
	private ChannelEditor newChannelEditor;
	
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		App app = App.getApp();
		try {
			Command command = app.sendAndReceive(Constants.EDIT_CHANNEL_GET_USERS, app.getSelectedChannel().id); //TODO Check problems
			if (command.protocol.equals(Constants.ERROR)) {
				app.openMessageDialog(Alert.AlertType.ERROR, "Channel Editing", (String) command.extras);
			} else {
				oldChannelEditor = (ChannelEditor) command.extras;
				channelNameTextField.setText(oldChannelEditor.name);
				scrollPanesEditChannel(oldChannelEditor.usersIn, true);
				scrollPanesEditChannel(oldChannelEditor.usersOut, false);
				
				newChannelEditor = new ChannelEditor(oldChannelEditor.channelId);
				newChannelEditor.usersIn = new ArrayList<>();
				newChannelEditor.usersOut = new ArrayList<>();
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void scrollPanesEditChannel(ArrayList<String> users, boolean isIn) {
		for (var user : users) {
			HBox hBox = new HBox();
			Label label = new Label(user);
			
			var newUsersIn = newChannelEditor.usersIn;
			var newUsersOut = newChannelEditor.usersOut;
			
			ImageView imageView = new ImageView(isIn ? getClass().getResource("Images/delete_user.png").toExternalForm() : getClass().getResource("Images/add_user.png").toExternalForm());
			imageView.setFitWidth(15);
			imageView.setFitHeight(15);
			
			if (isIn) { // If is being removed
				imageView.setOnMouseClicked(event -> {
					membersVbox.getChildren().remove(label);
					inviteVbox.getChildren().add(label);
					
					oldChannelEditor.usersIn.removeIf(username -> username.equals(user));
					newChannelEditor.usersIn.removeIf(username -> username.equals(user));
					
					newChannelEditor.usersOut.add(user);
				});
			} else { // If is being added
				imageView.setOnMouseClicked(event -> {
					inviteVbox.getChildren().remove(label);
					membersVbox.getChildren().add(label);
					
					oldChannelEditor.usersIn.removeIf(username -> username.equals(user));
					newChannelEditor.usersIn.removeIf(username -> username.equals(user));
					
					newChannelEditor.usersIn.add(user);
				});
			}
			hBox.getChildren().addAll(label, imageView);
			if (isIn) {
				membersVbox.getChildren().add(hBox);
			} else {
				inviteVbox.getChildren().add(hBox);
			}
		}
	}
	
	public void applyButton(ActionEvent actionEvent) {
		App app = App.getApp();
		String channelName = channelNameTextField.getText();
		
		if (!channelName.isBlank() && !channelName.equals(oldChannelEditor.name)) {
			newChannelEditor.name = channelName;
		}
		
		if (newChannelEditor.usersIn != null && newChannelEditor.usersIn.size() == 0)
			newChannelEditor.usersIn = null;
		if (newChannelEditor.usersOut != null && newChannelEditor.usersOut.size() == 0)
			newChannelEditor.usersOut = null;
		
		try {
			Command command = app.sendAndReceive(Constants.EDIT_CHANNEL, newChannelEditor);
			if (command.protocol.equals(Constants.ERROR)) {
				app.openMessageDialog(Alert.AlertType.ERROR, Constants.ERROR, (String) command.extras);
			} else {
				closeButton(null);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	public void deleteButton(ActionEvent actionEvent) {
		App app = App.getApp();
		try {
			boolean bool = app.openMessageDialogDeleteChannel(Alert.AlertType.CONFIRMATION, "Delete channel", "Do you want to delete this channel?");
			if (bool) {
				Command command = app.sendAndReceive(Constants.DELETE_CHANNEL, app.getSelectedChannel().id);
				if (command.protocol.equals(Constants.ERROR)) {
					app.openMessageDialog(Alert.AlertType.ERROR, Constants.ERROR, (String) command.extras);
				} else {
					closeButton(null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeButton(ActionEvent actionEvent) {
		Stage thisStage = (Stage) channelNameTextField.getScene().getWindow();
		thisStage.close();
	}
}
