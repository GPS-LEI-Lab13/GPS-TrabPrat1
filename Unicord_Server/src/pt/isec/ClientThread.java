package pt.isec;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class ClientThread extends Thread {
	
	private final Socket socket;
	private final ObjectOutputStream oos;
	private final MainServer app;
	private final MainReceiver receiver;
	private User user;
	private int currentChannel;
	
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
	
	private void handleCommand(Command command) throws InterruptedException {
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
	
	private void protocolLogin(User user) {
	
	}
	
	private void protocolLogout() {
	
	}
	
	private void protocolGetChannels() {
	
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
}
