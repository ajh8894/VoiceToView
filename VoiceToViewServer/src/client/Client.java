package client;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import com.swmem.voicetoview.data.Chunk;

import data.Constants;
import data.ServerData;

public class Client implements Runnable {
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private String type;
	private String from, to;
	private boolean isActivated;
	private BlockingDeque<Chunk> senderDeque;
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

	synchronized public BlockingDeque<Chunk> getSenderDeque() {
		return senderDeque;
	}

	synchronized public void putSenderDeque(Chunk c) {
		try {
			this.senderDeque.put(c);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	synchronized public void sendToClient(Chunk c) throws IOException {
		oos.reset();
		oos.writeObject(c);
		oos.flush();
	}
	
	public ClientWriter getClientWriter() {
		return clientWriter;
	}

	synchronized public void close() {
		System.out.println(from + " Bye Bye");
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
					client.close();
					synchronized (client) {
						client.notify();
					}
					if(client.getType().equals(Constants.KIND_RECEIVE)) {
						if(client.getClientWriter() != null) {
							if(client.getClientWriter().isAlive()) {
								client.getClientWriter().interrupt();
							}
						}
					}
					ServerData.clients.remove(from, client);
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
					if(ServerData.clients.get(to) != null)
						checkEachOther(ServerData.clients.containsKey(to), ServerData.clients.get(to).getType().equals(Constants.KIND_RECEIVE));
				} else { //call receiver flow
					Client callSender = ServerData.clients.get(to);
					if (callSender != null) {
						synchronized (callSender) {
							System.out.println(from + "->" + to + " notify!!");
							callSender.notify();
						}
						checkEachOther(callSender.getSocket().isConnected(), callSender.getType().equals(Constants.KIND_RECEIVE));
					}
				}
				
			} else { // reconnect
				Client client = ServerData.clients.get(from);
				if (client != null) {
					System.out.println("reconnect: " + client.isActivated);
					synchronized (client) {
						client.notify();
					}
					checkEachOther(!client.isActivated, ServerData.clients.containsKey(to));
					if (isActivated) {
						if (client.getType().equals(Constants.KIND_RECEIVE)) {
							if (client.getClientWriter() != null) {
								if (client.getClientWriter().isAlive()) {
									client.getClientWriter().interrupt();
								}
							}
						}
						if (client.getSenderDeque() != null && !client.getSenderDeque().isEmpty())
							senderDeque.addAll(client.getSenderDeque());
					}

					System.out.println("Client reconnect");
					client.close();
					ServerData.clients.replace(from, this);
				}
			}
			
			System.out.println("result:" + isActivated);
			if(isActivated) { //operation
				if (type.equals(Constants.KIND_RECEIVE)) { // writer
					System.out.println("writer run");
					this.senderDeque = new LinkedBlockingDeque<Chunk>(512);
					this.clientWriter = new ClientWriter(this, senderDeque);
					this.clientWriter.start();
				}
				if (ServerData.clients.get(to).getType().equals(Constants.KIND_RECEIVE)) { // read
					while (isActivated && socket.isConnected() && !socket.isClosed()) {
						Object receiveObject = ois.readObject();
						if(receiveObject != null) {
							ServerData.receiverQueue.put(receiveObject);
						}
					}
					close();
				}
			} else {
				System.out.println("Connect init fail");
				ServerData.clients.remove(from, this);
			}
		} catch (ClassNotFoundException | IOException | InterruptedException e) {
			e.printStackTrace();
			close();
		}
	}
}