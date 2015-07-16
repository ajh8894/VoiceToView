package client;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import com.swmem.voicetoview.data.Model;

public class ClientWriter extends Thread {
	private Client client;
	private BlockingQueue<Model> senderQueue;

	public ClientWriter(Client client, BlockingQueue<Model> senderDeque) {
		this.client = client;
		this.senderQueue = senderDeque;
	}

	@Override
	public void run() {
		Model m = null;
		try {
			while (client.isActivated() && client.getSocket().isConnected() && !client.getSocket().isClosed()) {
				m = senderQueue.take();
				if (m != null)
					client.sendToClient(m);
			}
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
			if (m != null) {
				try {
					senderQueue.put(m);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		} finally {
			//client.close();
		}
	}
}
