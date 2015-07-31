package client;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import com.swmem.voicetoview.data.Model;

import data.Constants;

public class Client implements Runnable {
	private boolean isActivated;
	private int state;
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private String type;
	private String from, to;
	private Integer order;
	private Integer completed;
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

	public boolean isActivated() {
		return isActivated;
	}

	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Socket getSocket() {
		return socket;
	}

	public String getType() {
		return type;
	}
	
	public String getFrom() {
		return from;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}
	
	public Integer getCompleted() {
		return completed;
	}

	public void setCompleted(Integer completed) {
		this.completed = completed;
	}

	public BlockingQueue<Model> getSenderQueue() {
		return senderQueue;
	}

	public void setSenderQueue(BlockingQueue<Model> senderQueue) {
		this.senderQueue = senderQueue;
	}

	public void putSenderQueue(Model m) {
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
	
	public boolean readFromClient() throws IOException {
		return ois.readBoolean();
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

	private void checkEachOther(boolean target) throws IOException {
		if(target) {
			if (Constants.clients.get(to).getType().equals(Constants.KIND_RECEIVE)) {
				//System.out.println(target + " "	+ Constants.KIND_RECEIVE + " operator");
				oos.writeBoolean(true);
				oos.flush();
			} else {
				//System.out.println(target + " "	+ Constants.KIND_SEND + " operator");
				oos.writeBoolean(false);
				oos.flush();
			}
			isActivated = ois.readBoolean();
		} else {
			isActivated = false;
		}
	}
	
	private void interaction() throws InterruptedException, IOException {
		if (!Constants.clients.containsKey(to)) { //call sender flow
			synchronized (this) {
				System.out.println(from + " - state waitting");
				wait(Constants.SENDER_TIMEOUT); //block
				System.out.println(from + " - state running");
				checkEachOther(Constants.clients.containsKey(to));
			}
		} else { //call receiver flow
			if (Constants.clients.get(to) != null) {
				synchronized (Constants.clients.get(to)) {
					System.out.println(from + "->" + to + " notify");
					Constants.clients.get(to).notify();
					checkEachOther(Constants.clients.get(to).getSocket().isConnected());
				}
			}
		}
	}
	
	@Override
	public void run() {
		try {
			String[] header = (String[]) ois.readObject();
			if(header != null) {
				type = header[0];
				from = header[1];
				to = header[2];
				System.out.println("client connect - id(from): " + from + ", type: " + type + ", to: " + to);
			} else {
				return;
			}
			
			//client disconnect
			if(header[0].equals(Constants.KIND_END)) {
				if(Constants.clients.containsKey(from)) {
					Client client = Constants.clients.get(from);
					client.setActivated(false);
					
					if (!client.isActivated()) {
						synchronized (client) {
							client.close();
							client.notify();
						}
					}
					
					if (client.getType().equals(Constants.KIND_RECEIVE)
							&& client.getClientWriter() != null
							&& client.getClientWriter().isAlive()) {
						client.getClientWriter().interrupt();
						client.setSenderQueue(null);
					}
					
					close();
					Constants.clients.remove(from, client);
					System.out.println(from + " - disconnect");
				}
				return;
			}
			// client connect, reconnect distinguish
			if (!Constants.clients.containsKey(from)) { // connect
				System.out.println(from + " - new connect");
				Constants.clients.put(from, this);
				interaction();
			} else { // reconnect
				Client client = Constants.clients.get(from);
				if (client != null) {
					isActivated = client.isActivated();
					if (isActivated) {
						if (client.getType().equals(Constants.KIND_RECEIVE)) {
							client.setActivated(false);
							
							if (client.getOrder() != null)
								this.order = client.getOrder();
							else
								this.order = new Integer(0);

							if (client.getCompleted() != null)
								this.completed = client.getCompleted();
							else
								this.completed = new Integer(0);

							if (client.getClientWriter() != null && client.getClientWriter().isAlive()) {
								client.getClientWriter().interrupt();
							}
							
							if (client.getSenderQueue() != null && !client.getSenderQueue().isEmpty()) {
								this.senderQueue = client.getSenderQueue();
							}
						}
						System.out.println(from + " - reconnect");
						Constants.clients.replace(from, this);
					} else {
						if (!client.isActivated()) {
							synchronized (client) {
								client.close();
								client.notify();
							}
						}
						System.out.println(from + " - reconnect");
						Constants.clients.replace(from, this);
						interaction();
					}
				}
			}
			
			if(isActivated) { //operation
				System.out.println(from + " - connect success");
				if (type.equals(Constants.KIND_RECEIVE)) { // writer
					System.out.println(from + " - writer run");
					if(this.senderQueue == null)
						this.senderQueue = new PriorityBlockingQueue<Model>(512);
					if(this.order == null) {
						this.order = new Integer(0);
					}
					if(this.completed == null)
						this.completed = new Integer(0);
					
					this.clientWriter = new ClientWriter(this);
					this.clientWriter.start();
				}
			} else {
				System.out.println(from +" - connect fail");
				close();
			}
		} catch (ClassNotFoundException | IOException | InterruptedException e) {
			e.printStackTrace();
			close();
		} finally {
			System.out.println("-----------------------");
			System.out.println("current user - " + Constants.clients.size());
			System.out.println("-----------------------");
		}
	}
}