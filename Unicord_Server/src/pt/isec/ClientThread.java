package pt.isec;

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
		try {
			BlockingQueue<Command> queue = receiver.addListener();
			
			while (true) {
				Command command = queue.take();
				handleCommand(command);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void handleCommand(Command command) throws Exception {
		switch (command.protocol) {
			case Constants.REGISTER -> protocolRegister((User) command.extras);
			case Constants.LOGIN -> protocolLogin((User) command.extras);
			case Constants.LOGOUT -> protocolLogout();
			case Constants.GET_CHANNELS -> protocolGetChannels();
			case Constants.NEW_CHANNEL -> protocolNewChannel((Channel) command.extras);
			case Constants.GET_MESSAGES -> protocolGetMessages((int) command.extras);
			case Constants.NEW_MESSAGE -> protocolNewMessage((Message) command.extras);
			case Constants.DOWNLOAD_MESSAGES -> protocolDownloadMessage((Message) command.extras);
			case Constants.EDIT_CHANNEL -> protocolEditChannel((Command[]) command.extras);
		}
	}
	
	private void protocolRegister(User user) {
	
	}
	
	private void protocolLogin(User user) throws SQLException, IOException {
		// verificar se a palavra passe está correta com o username
		if (!Validator.checkPasswordMatchUsername(user, app.database)) {
			sendCommand(Constants.ERROR, "Password does not match username");
		}
		// se estiver guardar o utilizador neste objeto
		this.user = app.database.User.getByUsername(user.username);
		sendCommand(Constants.SUCCESS, null);
	}
	
	private void protocolLogout() {
	
	}
	
	private void protocolGetChannels() throws SQLException, IOException {
		if (!isLoggedIn()) sendCommand(Constants.ERROR, null);
		ArrayList<Channel> userChannels = app.database.Channel.getUserChannels(user.id);
		sendCommand(Constants.SUCCESS, userChannels);
	}
	
	private void protocolGetMessages(int channelId) {
	
	}
	
	private void protocolNewMessage(Message message) {
	
	}
	
	private void protocolDownloadMessage(Message message) throws InterruptedException {
		
		/*
		BlockingQueue<Command> commands = receiver.addListener();
		while (true) {
			Command command = commands.take();
			//do stuff with it
		}
		*/
	}
	
	private void protocolNewChannel(Channel channel) {
	
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
