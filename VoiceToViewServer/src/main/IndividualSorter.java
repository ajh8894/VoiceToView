package main;
import com.swmem.voicetoview.data.Model;

import data.ServerData;

public class IndividualSorter extends Thread {
	@Override
	public void run() {
		super.run();
		while (true) {
			try {
				Model m = (Model) ServerData.receiverQueue.take();
				if (ServerData.clients.containsKey(m.getTo())) {
					if(ServerData.clients.get(m.getTo()).getSenderQueue() != null) {
						ServerData.clients.get(m.getTo()).putSenderQueue(m);
					}
				} else {
					ServerData.receiverQueue.put(m);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
