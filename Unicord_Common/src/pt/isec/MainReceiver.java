package pt.isec;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MainReceiver extends Thread {
	
	Socket socket;
	ObjectInputStream ois;
	List<BlockingQueue<Command>> list;
	
	MainReceiver(Socket socket) throws IOException {
		this.socket = socket;
		this.ois = new ObjectInputStream(socket.getInputStream());
		this.list = Collections.synchronizedList(new ArrayList<>());
	}
	
	@Override
	public void run() {
		
		while (true) {
			Command command;
			try {
				command = (Command) ois.readObject();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				continue;
			}
			
			for (var queue : list) {
				queue.offer(command);
			}
		}
	}
	
	BlockingQueue<Command> addListener() {
		BlockingQueue<Command> queue = new LinkedBlockingQueue<>();
		list.add(queue);
		return queue;
	}
	
	boolean removeListener(BlockingQueue<Command> queue) {
		return list.remove(queue);
	}
}
