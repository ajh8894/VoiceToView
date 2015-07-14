package client;
import java.io.IOException;
import java.util.concurrent.BlockingDeque;

import com.swmem.voicetoview.data.Chunk;


public class ClientWriter extends Thread {
	private Client client;
	private BlockingDeque<Chunk> senderDeque;
	
	public ClientWriter(Client client, BlockingDeque<Chunk> senderDeque) {
		this.client = client;
		this.senderDeque = senderDeque;
	}

	@Override
	public void run() {
		Chunk sendChunk = null;
		try {
			while(client.isActivated() && client.getSocket().isConnected() && !client.getSocket().isClosed()) {
				sendChunk = senderDeque.takeFirst();
				if(sendChunk != null)
					client.sendToClient(sendChunk);
				sendChunk = null;
			}
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
			System.out.println("Interrupted");
		} finally {
			client.close();
/*			if(sendChunk != null) {
				try {
					senderDeque.putFirst(sendChunk);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}*/
		}
		super.run();
	}
}
