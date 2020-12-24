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
	
	private Socket socket;
	private ObjectInputStream ois;
	private List<BlockingQueue<Command>> list;
	
	public MainReceiver(Socket socket) throws IOException {
		this.socket = socket;
		this.ois = new ObjectInputStream(socket.getInputStream());
		this.list = Collections.synchronizedList(new ArrayList<>());
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				
				Command command = (Command) ois.readObject();
				System.out.println(command);
				for (var queue : list) {
					queue.offer(command);
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("Client went down, sad");
	}
	
	public BlockingQueue<Command> addListener() {
		BlockingQueue<Command> queue = new LinkedBlockingQueue<>();
		list.add(queue);
		return queue;
	}
	
	public boolean removeListener(BlockingQueue<Command> queue) {
		return list.remove(queue);
	}
}
