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
	private Socket socket;
	public ObjectInputStream ois;
	public ObjectOutputStream oos;
	private String type;
	private String from, to;
	private Integer order;
	private Integer isCompleted;
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

	public Socket getSocket() {
		return socket;
	}

	public String getType() {
		return type;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Integer getIsCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(Integer isCompleted) {
		this.isCompleted = isCompleted;
	}

	synchronized public BlockingQueue<Model> getSenderQueue() {
		return senderQueue;
	}

	public void setSenderQueue(BlockingQueue<Model> senderQueue) {
		this.senderQueue = senderQueue;
	}

	synchronized public void putSenderQueue(Model m) {
		try {
			this.senderQueue.put(m);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void sendToClient(Model m) throws IOException {
		oos.reset();
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
				if(Constants.clients.containsKey(from)) {
					Client client = Constants.clients.get(from);
					client.setActivated(false);
					client.setSenderQueue(null);
					
					synchronized (client) {
						client.close();
						client.notify();
					}
					if (client.getType().equals(Constants.KIND_RECEIVE)
							&& client.getClientWriter() != null
							&& client.getClientWriter().isAlive()) {
						client.getClientWriter().interrupt();
					}
					close();
					Constants.clients.remove(from, client);
					System.out.println(from + " Bye Bye");
				}
				return;
			}
			// client connect, reconnect distinguish
			if (!Constants.clients.containsKey(from)) { // connect
				System.out.println("client connect");
				Constants.clients.put(from, this);
				
				if (!Constants.clients.containsKey(to)) { //call sender flow
					synchronized (this) {
						System.out.println(from + " wait!!");
						wait(Constants.SENDER_TIMEOUT); //block
						System.out.println(from + " wake up!!");
					}
					checkEachOther(Constants.clients.containsKey(to));
					
				} else { //call receiver flow
					if (Constants.clients.get(to) != null) {
						synchronized (Constants.clients.get(to)) {
							System.out.println(from + "->" + to + " notify!!");
							Constants.clients.get(to).notify();
						}
						checkEachOther(Constants.clients.get(to).getSocket().isConnected());
					}
				}
			} else { // reconnect
				Client client = Constants.clients.get(from);
				if (client != null) {
					isActivated = client.isActivated();
					if (isActivated) {
						if (client.getType().equals(Constants.KIND_RECEIVE)) {
							client.setActivated(false);
							
							if (client.getOrder() != null) {
								this.order = client.getOrder();
							}
							else {
								this.order = new Integer(0);
							}

							if (client.getIsCompleted() != null)
								this.isCompleted = client.getIsCompleted();
							else
								this.isCompleted = new Integer(0);

							if (client.getClientWriter() != null && client.getClientWriter().isAlive()) {
								client.getClientWriter().interrupt();
							}
							
							if (client.getSenderQueue() != null && !client.getSenderQueue().isEmpty()) {
								this.senderQueue = client.getSenderQueue();
							}
						}
					} else {
						checkEachOther(Constants.clients.containsKey(to));
					}
					
					synchronized (client) {
						client.close();
						client.notify();
					}
					System.out.println("Client reconnect");
					Constants.clients.replace(from, this);
				}
			}
			
			if(isActivated) { //operation
				System.out.println("Connect success");
				if (type.equals(Constants.KIND_RECEIVE)) { // writer
					System.out.println("Writer run");
					if(senderQueue == null)
						this.senderQueue = new PriorityBlockingQueue<Model>(512);
					if(order == null) {
						this.order = new Integer(0);
					}
					if(isCompleted == null)
						this.isCompleted = new Integer(0);
					
					this.clientWriter = new ClientWriter(this);
					this.clientWriter.start();
				}
/*				if (Constants.clients.get(to).getType().equals(Constants.KIND_RECEIVE)) { // read
					while (isActivated && socket.isConnected() && !socket.isClosed()) {
						Object receiveObject = ois.readObject();
						if(receiveObject != null) {
							Constants.receiverQueue.put(receiveObject);
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
		} finally {
			System.out.println("-----------------------");
			System.out.println("현재 사용자 수 " + Constants.clients.size());
			System.out.println("-----------------------");
			System.out.println("");
		}
	}
}