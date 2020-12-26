package pt.isec;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;

public class ClientThread extends Thread {
	
	private final Socket socket;
	private ObjectOutputStream oos;
	private MainReceiver receiver;
	private final MainServer app;
	private User user = null;
	private int currentChannel = -1;
	
	public ClientThread(Socket socket, MainServer mainServer) {
		this.socket = socket;
		this.app = mainServer;
	}
	
	@Override
	public void run() {
		BlockingQueue<Command> queue = null;
		try {
			this.oos = new ObjectOutputStream(socket.getOutputStream());
			this.receiver = new MainReceiver(socket);
			this.receiver.start();
			queue = receiver.addListener();
			
			while (true) {
				Command command = queue.take();
				System.out.println(command);
				handleCommand(command);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (queue != null) receiver.removeListener(queue);
			app.clients.remove(this);
		}
	}
	
	private void handleCommand(Command command) throws Exception {
		switch (command.protocol) {
			case Constants.REGISTER -> protocolRegister((User) command.extras);
			case Constants.LOGIN -> protocolLogin((User) command.extras);
			default -> {
				if (!isLoggedIn()) {
					sendCommand(Constants.ERROR, "User is not logged in");
					break;
				}
				switch (command.protocol) {
					case Constants.LOGOUT -> protocolLogout();
					case Constants.GET_CHANNELS -> protocolGetChannels();
					case Constants.NEW_CHANNEL -> protocolNewChannel((Channel) command.extras);
					case Constants.GET_MESSAGES -> protocolGetMessages((int) command.extras);
					case Constants.NEW_MESSAGE -> protocolNewMessage((Message) command.extras);
					case Constants.DOWNLOAD_MESSAGES -> protocolDownloadMessage((int) command.extras);
					case Constants.EDIT_CHANNEL -> protocolEditChannel((ChannelEditor) command.extras);
					case Constants.DELETE_CHANNEL -> protocolDeleteChannel((int) command.extras);
				}
			}
		}
	}

	private void protocolRegister(User user) throws IOException, SQLException {
		// Colocar na base de dados o user
		// Enviar success ou error
		if (app.database.User.createUser(user)) {
			sendCommand(Constants.SUCCESS, null);
		} else {
			sendCommand(Constants.ERROR, "Register failed!");
		}
	}
	
	private void protocolLogin(User user) throws SQLException, IOException {
		// verificar se a palavra passe está correta com o username
		if (!Validator.checkPasswordMatchUsername(user, app.database)) {
			sendCommand(Constants.ERROR, "Password does not match username");
			return;
		}
		// se estiver guardar o utilizador neste objeto
		this.user = app.database.User.getByUsername(user.username);
		sendCommand(Constants.SUCCESS, null);
	}
	
	private void protocolLogout() throws IOException {
		this.user = null;
		sendCommand(Constants.SUCCESS, null);
	}
	
	private void protocolGetChannels() throws SQLException, IOException {
		ArrayList<Channel> userChannels = app.database.Channel.getUserChannels(user.id);
		sendCommand(Constants.SUCCESS, userChannels);
	}
	
	private void protocolGetMessages(int channelId) throws IOException, SQLException {
		ArrayList<Message> messages = app.database.Message.getAll(channelId);
		sendCommand(Constants.SUCCESS, messages);
		currentChannel = channelId;
	}
	
	private void protocolNewMessage(Message message) throws SQLException, IOException {
		message.senderId = user.id;
		message.channelId = currentChannel;
		
		boolean success = app.database.Message.createMessage(message);
		if (!success) {
			sendCommand(Constants.ERROR, "Server Error");
			return;
		}
		if (message.type.equals(Message.TYPE_FILE)) {
			message.content = Utils.addTimestampFileName(message.content);
			BlockingQueue<Command> commandQueue = receiver.addListener();
			sendCommand(Constants.SUCCESS, null);
			new Thread(() -> {
				try {
					FileOutputStream fos = new FileOutputStream(Constants.getFile(message.content));
					while (true) {
						Command command = commandQueue.take();
						
						if (command.protocol.equals(Constants.FILE_BLOCK) && command.extras instanceof FileBlock) {
							FileBlock fileBlock = (FileBlock) command.extras;
							// This is upload from the client to the server
							if (fileBlock.identifier.equals(Constants.UPLOAD_IDENTIFIER + message.id)) { //  <---- TODO check this shit out
								
								if (fileBlock.bytes.length == 0) {
									fos.close();
									break;
								}
								fos.write(fileBlock.bytes);
							}
						}
					}
					app.sendToAll(Constants.NEW_MESSAGE, message);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				receiver.removeListener(commandQueue);
			}).start();
		} else {
			app.sendToAll(Constants.NEW_MESSAGE, message);
		}
	}

	private void protocolDeleteChannel(int channelId) throws SQLException, IOException {
		Channel channel = app.database.Channel.getByID(channelId);
		if (channel.creatorId == user.id) {
			if (app.database.Channel.deleteChannel(channelId))
				sendCommand(Constants.SUCCESS, null);
			else
				sendCommand(Constants.ERROR, "Could not delete channel!");
		} else {
			sendCommand(Constants.ERROR, "You are not the channel owner!");
		}
	}

	private void protocolDownloadMessage(int messageId) {
		// Actually uploads
		new Thread(() -> {
			try {
				Message message = app.database.Message.getByID(messageId);
				if (message == null) {
					sendCommand(Constants.ERROR, "Message does not exist");
					return;
				}
				
				FileInputStream fis = new FileInputStream(Constants.getFile(message.content));
				// This is download from the client to the server
				FileBlock fileBlock = new FileBlock(Constants.DOWNLOAD_IDENTIFIER + messageId);
				var bytes = fileBlock.bytes;
				
				while (true) {
					int readAmount = fis.read(bytes);
					if (readAmount <= 0) {
						fileBlock.bytes = new byte[0];
						sendCommand(Constants.DOWNLOAD_MESSAGES, fileBlock);
						fis.close();
						break;
					}
					if (readAmount < fileBlock.bytes.length) {
						fileBlock.bytes = Arrays.copyOfRange(bytes, 0, readAmount);
					}
					sendCommand(Constants.DOWNLOAD_MESSAGES, fileBlock);
					fileBlock.bytes = bytes;
				}
				app.sendToAll(Constants.NEW_MESSAGE, message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	private void protocolNewChannel(Channel channel) throws SQLException, IOException {
		channel.creatorId = user.id;
		boolean success = app.database.Channel.createChannel(channel);
		if (!success) {
			sendCommand(Constants.ERROR, "Server Error");
			return;
		}
		sendCommand(Constants.SUCCESS, null);
		app.sendToAll(Constants.NEW_MESSAGE, channel);
	}
	
	private void protocolEditChannel(ChannelEditor channelChanges) throws IOException, SQLException {
		var channel = app.database.Channel.getByID(channelChanges.channelId);
		if (channel.creatorId != user.id) sendCommand(Constants.ERROR, "User is  not channel owner");
		
		channel.name = channelChanges.name;
		
		if (channelChanges.name != null) {
			if (!app.database.Channel.editChannel(channel)) {
				sendCommand(Constants.ERROR, "Name already in use");
				return;
			}
		}
		if (channelChanges.usersToAdd != null) {
			for (var userId : channelChanges.usersToAdd) {
				if (!app.database.Channel.addUser(userId, channel.id)) {
					sendCommand(Constants.ERROR, "Something went wrong");
					return;
				}
			}
		}
		if (channelChanges.usersToRemove != null) {
			for (var userId : channelChanges.usersToRemove) {
				if (!app.database.Channel.removeUser(userId, channel.id)) {
					sendCommand(Constants.ERROR, "Something went wrong");
					return;
				}
			}
		}
		sendCommand(Constants.SUCCESS, null);
		app.sendToAll(Constants.EDIT_CHANNEL, channel);
	}
	
	public void sendCommand(String protocol, Object extras) throws IOException {
		Command obj = new Command(protocol, extras);
		oos.writeUnshared(obj);
		oos.flush();
		if (!(extras instanceof FileBlock)) {
			System.out.println(obj);
		}
	}
	
	private boolean isLoggedIn() {
		return user != null;
	}
}
