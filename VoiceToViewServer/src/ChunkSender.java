import com.swmem.voicetoview.data.Chunk;

public class ChunkSender extends Thread {

	@Override
	public void run() {
		super.run();
		while (true) {
			try {
				Chunk c = ServerData.senderQueue.take();
				if (ServerData.clients.containsKey(c.getTo())) {
					ServerData.clients.get(c.getTo()).putSenderQueue(c);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
