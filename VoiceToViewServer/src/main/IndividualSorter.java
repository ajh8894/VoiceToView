package main;

import client.Client;

import com.swmem.voicetoview.data.Model;

import data.Constants;

public class IndividualSorter extends Thread {
	@Override
	public void run() {
		super.run();
		while (true) {
			try {
				Model m = (Model) Constants.receiverQueue.take();

				if (Constants.clients.containsKey(m.getTo())) {
					Client c = Constants.clients.get(m.getTo());
					if(c.getSenderQueue() != null) {
						c.putSenderQueue(m);
						Thread writer = c.getClientWriter();
						if(writer != null && writer.isAlive() && !writer.isInterrupted()) {
							if(writer.getState() == State.TIMED_WAITING) {
								synchronized (writer) {
									writer.notify();
								}
							}
						}
					}
				} else {
					//Constants.receiverQueue.put(m);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
