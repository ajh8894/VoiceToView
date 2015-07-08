import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import com.swmem.voicetoview.data.Chunk;

public class Client implements Runnable {
	private boolean isActivated;
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private BlockingDeque<Chunk> senderDeque = new LinkedBlockingDeque<Chunk>(512);
	private String type;
	private String from, to;

	public Client(Socket socket) {
		this.socket = socket;
		this.isActivated = true;
		try {
			this.oos = new ObjectOutputStream(socket.getOutputStream());
			this.ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isActivated() {
		return isActivated;
	}

	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}

	public BlockingDeque<Chunk> getSenderQueue() {
		return senderDeque;
	}

	public void putSenderDeque(Chunk c) {
		try {
			this.senderDeque.put(c);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		System.out.println(from + " Bye Bye");
		isActivated = false;
		try {
			if (ois != null)
				ois.close();
			if (oos != null)
				oos.close();
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		Chunk sendChunk = null;
		try {
			System.out.println("RUN");
			String[] header = (String[]) ois.readObject();
			type = header[0];
			from = header[1];
			to = header[2];
			System.out.println("user: " + type + " " + from + " " + to);
			
			// client 최초, 재접속 확인
			if (ServerData.clients.containsKey(from)) {
				Client client = ServerData.clients.get(from);
				client.setActivated(false);
				synchronized (client) {
					client.notify();
					if (!client.getSenderQueue().isEmpty()) {
						//client.getSenderQueue().notify();
						senderDeque.addAll(client.getSenderQueue());
					}
				}


				System.out.println("replace!!");
				ServerData.clients.replace(from, this);
			} else {
				System.out.println("put!!");
				ServerData.clients.put(from, this);
			}

			// client 발신자, 수신자 구분
			if (type.equals(Constants.KIND_SEND)) { // stt on
				if (!ServerData.clients.containsKey(to)) {
					synchronized (this) {
						System.out.println("wait!!");
						wait(Constants.SENDER_TIMEOUT); //block
						System.out.println("wake up!!");
					}
				}
				while (isActivated && socket.isConnected() && !socket.isClosed()) {
					oos.reset();
					oos.writeBoolean(true);
					oos.flush();
					Chunk c = (Chunk) ois.readObject(); //block
					System.out.println("receive " + c.getFrom() + " " + c.getTo() + " " + c.getText());
					ServerData.receiverQueue.put(c);
				}
			} else if (type.equals(Constants.KIND_RECEIVE)) { // stt off
				if (ServerData.clients.containsKey(to)) {
					Client client = ServerData.clients.get(to);
					synchronized (client) {
						if (client.isActivated())
							System.out.println("notify!!");
						client.notify();
					}
				}
				while (isActivated && socket.isConnected() && !socket.isClosed()) {
					sendChunk = senderDeque.poll(Constants.RECEIVER_TIMEOUT, TimeUnit.MILLISECONDS); //block
					
					if (sendChunk == null) {
						System.out.println("TIMEOUT");
						break;
					}

					oos.reset();
					oos.writeObject(sendChunk);
					oos.flush();
					System.out.println("send " + sendChunk.getFrom() + " " + sendChunk.getTo() + " " + sendChunk.getText());
					sendChunk = null;

				}
			}
		} 
		catch (ClassNotFoundException | IOException | InterruptedException e) {
			e.printStackTrace();
			if(sendChunk != null) {
				System.out.println("recover");
				senderDeque.addFirst(sendChunk);
			}
		} finally {
			close();
		}
	}

}