
import com.swmem.voicetoview.data.Chunk;

public class SVM extends Thread {
	@Override
	public void run() {
		super.run();
		while (true) {
			try {
				Chunk c = ServerData.receiverQueue.take();
				
				ServerData.senderQueue.put(c);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
