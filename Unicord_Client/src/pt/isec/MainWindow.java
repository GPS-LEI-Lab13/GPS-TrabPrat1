package pt.isec;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;

public class MainWindow implements Initializable {
	public ScrollPane channelsScrollPane;
	public ScrollPane messageFileScrollPane;
	public VBox channelsVBox;
	public VBox messagesFilesVBox;
	
	private static App app;
	public TextField messageTextField;
	
	private List<Message> messages = new ArrayList<>();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		app = App.getApp();
		try {
			Command command = app.sendAndReceive(Constants.GET_CHANNELS, null);
			var channels = (List<Channel>) command.extras;
			app.setChannels(channels);
			var channel = channels.get(0);
			app.setSelectedChannel(channel);
			updateChannelList();
			channelListOnClick(channel.name);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		getUpdates();
	}
	
	public void getUpdates() {
		new Thread(() -> {
			try {
				BlockingQueue<Command> objectQueue = app.getReceivedObjectQueue();
				
				while (true) {
					Command command = objectQueue.take();
					System.out.println("GetUpdates : " + command);
					switch (command.protocol) {
						case Constants.NEW_CHANNEL -> {
							Channel channel = (Channel) command.extras;
							app.getChannels().add(channel);
							Platform.runLater(() -> updateChannelList());
						}
						case Constants.EDIT_CHANNEL -> {
							Channel channel = (Channel) command.extras;
							app.getChannels().remove(channel);
							app.getChannels().add(channel);
							Platform.runLater(() -> updateChannelList());
						}
						case Constants.DELETE_CHANNEL -> {
							Channel channel = (Channel) command.extras;
							app.getChannels().remove(channel);
							Platform.runLater(() -> updateChannelList());
						}
						case Constants.NEW_MESSAGE -> {
							Message message = (Message) command.extras;
							messages.add(message);
							Platform.runLater(() -> messagesFilesVBox.getChildren().add(insertLine(message)));
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
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
	}
	
	private void updateChannelList() {
		for (Channel channel : app.getChannels()) {
			
			HBox box = new HBox();
			box.setFillHeight(true);
			
			Label label = new Label(channel.name);
			if (channel.id == app.getSelectedChannel().id) {
				box.setStyle("-fx-background-color: cyan;");
			}
			
			label.setOnMouseClicked(event -> {
				try {
					app.setSelectedChannel(channel);
					channelListOnClick(channel.name);
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			});
			box.getChildren().add(label);
			if (channel.creatorId == app.getUser().id) {
				ImageView image = new ImageView(getClass().getResource("Images/gear.png").toExternalForm());
				image.setOnMouseClicked(event -> {
					app.setSelectedChannel(channel);
					openEditChannel();
				});
				box.getChildren().add(image);
			}
			channelsVBox.getChildren().add(box);
		}
	}
	
	private void channelListOnClick(String name) throws IOException, InterruptedException {
		Command command = app.sendAndReceive(Constants.GET_MESSAGES, app.getSelectedChannel().id);
		if (!command.protocol.equals(Constants.SUCCESS)) {
			return;
		}
		messages = (ArrayList<Message>) command.extras;
		updateMessageList(messages);
	}
	
	private void updateMessageList(List<Message> messages) {
		messagesFilesVBox.getChildren().clear();
		for (var message : messages) {
			messagesFilesVBox.getChildren().add(insertLine(message));
		}
	}
	
	private HBox insertLine(Message message) {
		HBox box = new HBox();
		box.setFillHeight(true);
		
		Label label = new Label(message.content);
		
		if (app.getUser().id != message.senderId) {
			Label usernameLabel = new Label(message.senderUsername + ": ");
			usernameLabel.setTextFill(Color.web("#7D82B8"));
			box.getChildren().add(usernameLabel);
			box.setAlignment(Pos.BASELINE_LEFT);
		} else {
			box.setAlignment(Pos.BASELINE_RIGHT);
		}
		Button downloadBtn = null;
		if (message.type.equals(Message.TYPE_FILE)) {
			downloadBtn = new Button();
			//TODO METER IMAGEM NO BUTTON
			downloadBtn.setOnAction(event -> {
				DirectoryChooser directoryChooser = new DirectoryChooser();
				File fileDirectory = directoryChooser.showDialog(app.getStage());
				if (fileDirectory == null) {
					return;
				}
				try {
					app.downloadFile(message, fileDirectory.getAbsolutePath());
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			});
		}
		
		box.getChildren().add(label);
		if (downloadBtn != null) {
			box.getChildren().add(downloadBtn);
		}
		return box;
	}
	
	private void openEditChannel() {
		try {
			Stage editChannelStage = new Stage();
			editChannelStage.initModality(Modality.APPLICATION_MODAL);
			editChannelStage.setTitle("Unicord - Edit channel");
			Scene cC = new Scene(app.loadFxml("fxml/EditChannel.fxml"));
			editChannelStage.setScene(cC);
			editChannelStage.setResizable(false);
			editChannelStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void aboutMenuItem(ActionEvent actionEvent) {
	}
	
	public void SendButton(ActionEvent actionEvent) {
		String messageText = messageTextField.getText();
		if (messageText.isBlank()) return;
		messageTextField.setText("");
		Message message = new Message(0, app.getUser().id, app.getSelectedChannel().id, Message.TYPE_TEXT, messageText, 0, app.getUser().username);
		try {
			Thread td = new Thread(() -> {
				try {
					app.sendCommand(Constants.NEW_MESSAGE, message);
					//Command command = app.sendAndReceive(Constants.NEW_MESSAGE, message);
					/*if (command.protocol.equals(Constants.SUCCESS)) {
						message.senderUsername = app.getUser().username;
						messagesFilesVBox.getChildren().add(insertLine(message));
					} else {
						System.out.println("?¿Erro a enviar mensagem?¿");
					}*/
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			td.setDaemon(true);
			td.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void SendFileButton(ActionEvent actionEvent) {
		App app = App.getApp();
		if (app.getSelectedChannel() == null) {
			app.openMessageDialog(Alert.AlertType.ERROR, "Error Dialog", "Select a channel to send a file!");
			return;
		}
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select the file");
		File file = fileChooser.showOpenDialog(app.getStage());
		if (file == null) {
			return;
		}
		app.uploadFile(file);
	}
	
	public void onEnterPressed(KeyEvent keyEvent) {
		if (keyEvent.getCode() == KeyCode.ENTER) {
			SendButton(null);
		}
	}
}
