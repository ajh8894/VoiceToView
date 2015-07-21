package main;

import com.swmem.voicetoview.data.Model;

import data.Constants;

public class IndividualSorter extends Thread {
	@Override
	public void run() {
		super.run();
		while (true) {
			try {
				Model m = (Model) Constants.receiverQueue.take();

				if (Constants.clients.containsKey(m.getTo()) && Constants.clients.get(m.getTo()).getSenderQueue() != null) {
					Constants.clients.get(m.getTo()).putSenderQueue(m);
				} else {
					//Constants.receiverQueue.put(m);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
