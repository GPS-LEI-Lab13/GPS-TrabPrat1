package pt.isec;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class ClientThread extends Thread {
	
	private final Socket socket;
	private final ObjectOutputStream oos;
	private final MainServer app;
	private final MainReceiver receiver;
	private User user = null;
	private int currentChannel = -1;
	
	public ClientThread(Socket socket, MainServer mainServer) throws IOException {
		this.socket = socket;
		this.oos = new ObjectOutputStream(socket.getOutputStream());
		this.app = mainServer;
		this.receiver = new MainReceiver(socket);
	}
	
	@Override
	public void run() {
		BlockingQueue<Command> queue = receiver.addListener();
		try {
			while (true) {
				Command command = queue.take();
				handleCommand(command);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		receiver.removeListener(queue);
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
					case Constants.EDIT_CHANNEL -> protocolEditChannel((Command[]) command.extras);
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
	
	private void protocolGetMessages(int channelId) throws IOException {
		if (!isLoggedIn()) sendCommand(Constants.ERROR, null);
		
	}
	
	private void protocolNewMessage(Message message) throws SQLException, IOException, InterruptedException {
		message.senderId = user.id;
		message.channelId = currentChannel;
		boolean success = app.database.Message.createMessage(message);
		if (!success) {
			sendCommand(Constants.ERROR, "Server Error");
			return;
		}
		if (message.type.equals(Message.TYPE_FILE)) {
			BlockingQueue<Command> commandQueue = receiver.addListener();
			sendCommand(Constants.SUCCESS, null);
			message.content = Utils.addTimestampFileName(message.content);
			
			new Thread(() -> {
				try {
					FileOutputStream fos = new FileOutputStream(Constants.getFile(message.content));
					
					while (true) {
						Command command = commandQueue.take();
						
						if (command.protocol.equals(Constants.FILE_BLOCK) && command.extras instanceof FileBlock) {
							FileBlock fileBlock = (FileBlock) command.extras;
							
							if (fileBlock.identifier.equals("UPLOAD_" + message.id)) { //  <---- TODO check this shit out
								
								if (fileBlock.bytes.length == 0) {
									fos.close();
									break;
								}
								fos.write(fileBlock.bytes);
							}
						}
					}
					app.sendToAll(Constants.NEW_MESSAGE, message);
					receiver.removeListener(commandQueue);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
		} else {
			app.sendToAll(Constants.NEW_MESSAGE, message);
		}
	}
	
	private void protocolDownloadMessage(int messageId) throws IOException {
		new Thread(() -> {
			try {
				Message message = app.database.Message.getByID(messageId);
				if(message == null)
				
				//FileInputStream fis = new FileInputStream(Constants.getFile(message.content));
				//FileBlock fileBlock = new FileBlock("DOWNLOAD_" + messageId)
				
				while (true) {
				
					break;
				}
				app.sendToAll(Constants.NEW_MESSAGE, message);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
		
		/*BlockingQueue<Command> commands = receiver.addListener();
		while (true) {
			Command command = commands.take();
			//do stuff with it
			//if(command.protocol.equals())
		}*/
		
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
	
	private void protocolEditChannel(Command[] commands) {
		for (var command : commands) {
			switch (command.protocol) {
				case Constants.ADD_CHANNEL_USER -> {
				
				}
				case Constants.REMOVE_CHANNEL_USER -> {
				
				}
				case Constants.EDIT_CHANNEL_NAME -> {
				
				}
				default -> System.out.println("syke, something went wrong!");
			}
		}
	}
	
	public void sendCommand(String protocol, Object extras) throws IOException {
		Command obj = new Command(protocol, extras);
		oos.writeObject(obj);
		System.out.println(obj);
	}
	
	private boolean isLoggedIn() {
		return user != null;
	}
}
