package client;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import com.swmem.voicetoview.data.Model;

import data.Constants;
import data.ServerData;

public class Client implements Runnable {
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private String type;
	private String from, to;
	private boolean isActivated;
	private BlockingQueue<Model> senderQueue;
	private ClientWriter clientWriter;

	public Client(Socket socket) {
		this.isActivated = false;
		this.socket = socket;
		try {
			this.oos = new ObjectOutputStream(socket.getOutputStream());
			this.ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Socket getSocket() {
		return socket;
	}

	public String getType() {
		return type;
	}

	public boolean isActivated() {
		return isActivated;
	}

	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}

	synchronized public BlockingQueue<Model> getSenderQueue() {
		return senderQueue;
	}

	synchronized public void putSenderQueue(Model m) {
		try {
			this.senderQueue.put(m);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void sendToClient(Model m) throws IOException {
		//oos.reset();
		oos.writeObject(m);
		oos.flush();
	}
	
	public ClientWriter getClientWriter() {
		return clientWriter;
	}

	synchronized public void close() {
		System.out.println(from + " close()");
		try {
			if (ois != null)
				ois.close();
			if (oos != null)
				oos.close();
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			ois = null;
			oos = null;
			socket = null;
		}
	}

	private void checkEachOther(boolean target, boolean mode) throws IOException {
		if(target) {
			if (mode) {
				System.out.println(target + " "	+ Constants.KIND_RECEIVE);
				oos.writeBoolean(true);
				oos.flush();
			} else {
				System.out.println(target + " "	+ Constants.KIND_SEND);
				oos.writeBoolean(false);
				oos.flush();
			}
			isActivated = ois.readBoolean();
		} else {
			isActivated = false;
		}
	}
	
	@Override
	public void run() {
		try {
			String[] header = (String[]) ois.readObject();
			type = header[0];
			from = header[1];
			to = header[2];
			System.out.println("user: " + type + " " + from + " " + to);
			
			//client disconnect
			if(header[0].equals(Constants.KIND_END)) {
				if(ServerData.clients.containsKey(from)) {
					Client client = ServerData.clients.get(from);
					client.setActivated(false);
					synchronized (client) {
						client.close();
						client.notify();
					}
					if(client.getType().equals(Constants.KIND_RECEIVE)) {
						if(client.getClientWriter() != null) {
							if(client.getClientWriter().isAlive()) {
								client.getClientWriter().interrupt();
							}
						}
					}
					close();
					ServerData.clients.remove(from, client);
					//ServerData.clients.remove(from, this);
					System.out.println(from + " Bye Bye");
					return;
				}
			}
			// client connect, reconnect distinguish
			if (!ServerData.clients.containsKey(from)) { // connect
				System.out.println("client connect");
				ServerData.clients.put(from, this);
				
				if (!ServerData.clients.containsKey(to)) { //call sender flow
					synchronized (this) {
						System.out.println(from + " wait!!");
						wait(Constants.SENDER_TIMEOUT); //block
						System.out.println(from + " wake up!!");
					}
					if(ServerData.clients.get(to) != null) {
						checkEachOther(ServerData.clients.containsKey(to), ServerData.clients.get(to).getType().equals(Constants.KIND_RECEIVE));
					} else {
						isActivated = false;
					}
				} else { //call receiver flow
					Client callSender = ServerData.clients.get(to);
					if (callSender != null) {
						synchronized (callSender) {
							System.out.println(from + "->" + to + " notify!!");
							callSender.notify();
						}
						if(ServerData.clients.get(to) != null) {
							checkEachOther(callSender.getSocket().isConnected(), callSender.getType().equals(Constants.KIND_RECEIVE));
						} else {
							isActivated = false;
						}
					}
				}
			} else { // reconnect
				Client client = ServerData.clients.get(from);
				if (client != null) {
					isActivated = client.isActivated();
					if (isActivated) {
						if (client.getType().equals(Constants.KIND_RECEIVE)) {
							client.isActivated = false;
							this.senderQueue = new PriorityBlockingQueue<Model>(512);
							this.clientWriter = new ClientWriter(this, senderQueue);
							this.clientWriter.start();
							if (client.getSenderQueue() != null && !client.getSenderQueue().isEmpty()) {
								if(client.getSenderQueue() == null)
									System.out.println("null");
								else 
									System.out.println("not null");
								this.senderQueue.addAll(client.getSenderQueue());
							}
							if (client.getClientWriter() != null && client.getClientWriter().isAlive()) {
								client.getClientWriter().interrupt();
							}
						}
					} else {
						checkEachOther(ServerData.clients.containsKey(to), ServerData.clients.get(to).getType().equals(Constants.KIND_RECEIVE));
					}
					synchronized (client) {
						client.close();
						client.notify();
					}
					System.out.println("Client reconnect");
					ServerData.clients.replace(from, this);
				}
			}
			
			if(isActivated) { //operation
				System.out.println("Connect success");
				if (type.equals(Constants.KIND_RECEIVE)) { // writer
					System.out.println("writer run");
					this.senderQueue = new PriorityBlockingQueue<Model>(512);
					this.clientWriter = new ClientWriter(this, senderQueue);
					this.clientWriter.start();
				}
/*				if (ServerData.clients.get(to).getType().equals(Constants.KIND_RECEIVE)) { // read
					while (isActivated && socket.isConnected() && !socket.isClosed()) {
						Object receiveObject = ois.readObject();
						if(receiveObject != null) {
							ServerData.receiverQueue.put(receiveObject);
						}
					}
					close();
				}*/
			} else {
				System.out.println("Connect fail");
				close();
			}
		} catch (ClassNotFoundException | IOException | InterruptedException e) {
			e.printStackTrace();
			close();
		}
	}
}