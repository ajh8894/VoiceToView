package client;

import java.io.IOException;

import com.swmem.voicetoview.data.Model;

import data.Constants;

public class ClientWriter extends Thread {
	private Client client;

	public ClientWriter(Client client) {
		this.client = client;
	}
	
	@Override
	public void run() {
		Model m = null;
/*		long start = System.currentTimeMillis();
		long end = 0;*/
		try {
			while (client.isActivated() && client.getSocket().isConnected() && !client.getSocket().isClosed()) {
				m = client.getSenderQueue().take();
				//Thread.sleep(1500);
				//end = System.currentTimeMillis();
				if (isAlive()) {
					System.out.println();
					System.out.println(client.getFrom() + " - current order: " + client.getOrder().intValue() + " completed: " + client.getCompleted().intValue());
					System.out.println(client.getFrom() + " - take() model - order: " + m.getMessageNum() + " emotion: " + m.getEmotionType() + " text: " + m.getTextResult());
					if (m != null) {
						if (client.getOrder().intValue() > m.getMessageNum()) {
							System.out.println(client.getFrom() + " - 1. either junk - order: " + m.getMessageNum() + " emotion: " + m.getEmotionType() + " text: " + m.getTextResult());
							continue;
						}
						if (client.getOrder().intValue() == m.getMessageNum()) {
							if (m.getTextResult() != null && m.getTextResult().equals(Constants.SPEECH_FAIL)) {
								System.out.println(client.getFrom() + " - 2. speech fail junk - order: " + m.getMessageNum() + " emotion: " + m.getEmotionType() + " text: " + m.getTextResult());
								//client.setCompleted(0);
								//client.setOrder(client.getOrder() + 1);
								//continue;
							} else if (m.getEmotionType() == Constants.SILENCE) {
								System.out.println(client.getFrom() + " - 3. slience junk - order: " + m.getMessageNum() + " emotion: " + m.getEmotionType() + " text: " + m.getTextResult());
								client.setCompleted(0);
								client.setOrder(client.getOrder() + 1);
								continue;
							} else {
								client.sendToClient(m);
								if (client.readFromClient()) {
									System.out.println(client.getFrom() + " - *send model - order: " + m.getMessageNum() + " emotion: " + m.getEmotionType() + " text: " + m.getTextResult());
									client.setCompleted(client.getCompleted() + 1);
								}
							}
						} else {
/*							if (Constants.MESSAGE_TIMEOUT <= (end - start) / 1000.0) {
								System.out.println("--4. 시간초과 버림: " + m.getMessageNum() + " emotion: " + m.getEmotionType() + " text: " + m.getTextResult());
								isCompleted = Integer.valueOf(0);
								order = Integer.valueOf(order.intValue() + 1);
							} else {

							}*/
							System.out.println(client.getFrom() + "- restore - order: " + m.getMessageNum() + " emotion: " + m.getEmotionType() + " text: " + m.getTextResult());
							if(client != null && client.getSenderQueue() != null)
								client.getSenderQueue().put(m);
						}

						if (client.getCompleted().intValue() == Constants.MESSAGE_SEND_COMPLETE) {
							System.out.println(client.getFrom() + " - **complete -" + client.getOrder());
							client.setCompleted(0);
							client.setOrder(client.getOrder() + 1);
							//start = System.currentTimeMillis();
						}
					}
				}
			}
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
			if (m != null) {
				try {
					System.out.println();
					System.out.println(client.getFrom() + " - current order: " + client.getOrder().intValue() + " completed: " + client.getCompleted().intValue());
					System.out.println(client.getFrom() + " - InterruptedException restore - order: " + m.getMessageNum() + " emotion: " + m.getEmotionType() + " text: " + m.getTextResult());
					
					if(client != null && client.getSenderQueue() != null)
						client.getSenderQueue().put(m);
					
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		} finally {
			client.close();
		}
	}
}
