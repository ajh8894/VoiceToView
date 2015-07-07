import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.swmem.voicetoview.data.Chunk;

public class Client implements Runnable {
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private BlockingQueue<Chunk> senderQueue = new ArrayBlockingQueue<Chunk>(512);
	private String type;
	private String from, to;

	public Client(Socket socket) {
		setSocket(socket);
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
		try {
			this.oos = new ObjectOutputStream(socket.getOutputStream());
			this.ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public BlockingQueue<Chunk> getSenderQueue() {
		return senderQueue;
	}

	public void setSenderQueue(BlockingQueue<Chunk> senderQueue) {
		this.senderQueue = senderQueue;
	}

	public void putSenderQueue(Chunk c) {
		try {
			this.senderQueue.put(c);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			ois.close();
			oos.close();
			socket.close();
			ServerData.clients.remove(from);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void run() {
		try {
			String[] header = (String[]) ois.readObject();
			type = header[0];
			from = header[1];
			to = header[2];
			System.out.println(type + " " + from + " " + to);
			
			if(ServerData.clients.containsKey(from)) {
				Client client = ServerData.clients.get(from);
				if(!client.getSenderQueue().isEmpty()) {
					senderQueue.addAll(client.getSenderQueue());
					client.close();
				}
				ServerData.clients.replace(from, this);
			} else {
				System.out.println("put!!");
				ServerData.clients.put(from, this);
			}
			
			
			if(type.equals(Constants.KIND_SEND)) { // stt on
				if(!ServerData.clients.containsKey(to)) {
					synchronized (this) {
						System.out.println("wait!!");
						wait(Constants.TIMEOUT);
						System.out.println("wake up!");
						oos.writeBoolean(true);
						oos.flush();
					}
				}
				while(socket.isConnected() && !socket.isClosed()) {
					Chunk c = (Chunk) ois.readObject();
					System.out.println("receive "+ c.getFrom() + " " + c.getTo() + " " + c.getText());
					ServerData.receiverQueue.put(c);
				}
			} else if(type.equals(Constants.KIND_RECEIVE)) { // stt off
				if(ServerData.clients.containsKey(to)) {
					synchronized (ServerData.clients.get(to)) {
						System.out.println("notify!!");
						ServerData.clients.get(to).notify();
					}
				}
				while(socket.isConnected() && !socket.isClosed()) {
					Chunk c = senderQueue.poll(Constants.TIMEOUT, TimeUnit.MILLISECONDS);
					oos.reset();
					oos.writeObject(c);
					oos.flush();
					System.out.println("send "+ c.getFrom() + " " + c.getTo() + " " + c.getText());
				}
			}
		} catch (ClassNotFoundException | IOException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

}