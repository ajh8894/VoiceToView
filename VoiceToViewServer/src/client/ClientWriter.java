package client;

import java.io.IOException;

import com.swmem.voicetoview.data.Model;

import data.Constants;

public class ClientWriter extends Thread {
	private Client client;

	public ClientWriter(Client client) {
		this.client = client;
	}

	private String getEmotion(int e) {
		String r = null;
		switch (e) {
		case Constants.EMOTION_NOT_COMPLETE:
			r = "¹Ì¿Ï·á";
			break;
		case Constants.SAD:
			r = "½½ÇÄ";
			break;
		case Constants.NATURAL:
			r = "º¸Åë";
			break;
		case Constants.ANGRY:
			r = "È­³²";
			break;
		case Constants.HAPPY:
			r = "±â»Ý";
			break;
		default:
			r = "¹¬À½";
			break;
		}
		return r;
	}

	@Override
	public void run() {
		Model m = null;
		
		try {
			while (client.isActivated() && client.getSocket().isConnected() && !client.getSocket().isClosed()) {
				m = client.getSenderQueue().take();
				//Thread.sleep(1500);
				if (isAlive()) {
					System.out.println(client.getFrom() + " - current order: " + client.getOrder().intValue() + " completed: " + client.getCompleted().intValue());
					System.out.println(client.getFrom() + " - take() model - order: " + m.getMessageNum() + " emotion: " + getEmotion(m.getEmotionType()) + " text: " + m.getTextResult());
					if (m != null) {
						if (m.getMessageNum() == -1) {
							client.sendToClient(m);
							if (client.readFromClient()) {
								System.out.println(client.getFrom() + " - send ready model - order: " + m.getMessageNum() + " emotion: " + getEmotion(m.getEmotionType()) + " text: " + m.getTextResult());
								continue;
							}
						} else if (client.getOrder().intValue() > m.getMessageNum()) {
							System.out.println(client.getFrom() + " - 1. either junk - order: " + m.getMessageNum() + " emotion: " + getEmotion(m.getEmotionType()) + " text: " + m.getTextResult());
							continue;
						} else if (client.getOrder().intValue() == m.getMessageNum()) {
							if (m.getTextResult() != null && m.getTextResult().equals(Constants.SPEECH_FAIL)) {
								System.out.println(client.getFrom() + " - 2. speech fail junk - order: " + m.getMessageNum() + " emotion: " + getEmotion(m.getEmotionType()) + " text: " + m.getTextResult());
								client.sendToClient(m);
								client.setCompleted(0);
								client.setOrder(client.getOrder() + 1);
								continue;
							} else if (m.getEmotionType() == Constants.SILENCE) {
								System.out.println(client.getFrom() + " - 3. slience junk - order: " + m.getMessageNum() + " emotion: " + getEmotion(m.getEmotionType()) + " text: " + m.getTextResult());
								client.setCompleted(0);
								client.setOrder(client.getOrder() + 1);
								continue;
							} else {
								if(m.getEmotionType() != Constants.EMOTION_NOT_COMPLETE && m.getEmotionType() != Constants.SILENCE && client.getCompleted() == 0) {
									System.out.println(client.getFrom() + "- (1). emotion restore - order: " + m.getMessageNum() + " emotion: " + getEmotion(m.getEmotionType()) + " text: " + m.getTextResult());
									if (client != null && client.getSenderQueue() != null) {
										client.getSenderQueue().put(m);
										synchronized (this) {
											wait(Constants.RESTORE_TIMEOUT);
										}
									}
								} else {
									client.sendToClient(m);
									if (client.readFromClient()) {
										System.out.println(client.getFrom() + " - *send model - order: " + m.getMessageNum() + " emotion: " + getEmotion(m.getEmotionType()) + " text: " + m.getTextResult());
										client.setCompleted(client.getCompleted() + 1);
									}
								}
							}
						} else {
							System.out.println(client.getFrom() + "- (2). order restore - order: " + m.getMessageNum() + " emotion: " + getEmotion(m.getEmotionType()) + " text: " + m.getTextResult());
							if (client != null && client.getSenderQueue() != null) {
								client.getSenderQueue().put(m);
								synchronized (this) {
									wait(Constants.RESTORE_TIMEOUT);
								}
							}
						}

						if (client.getCompleted().intValue() == 2) {
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
					System.out.println(client.getFrom() + " - (3). InterruptedException restore - order: " + m.getMessageNum() + " emotion: " + getEmotion(m.getEmotionType()) + " text: " + m.getTextResult());

					if (client != null && client.getSenderQueue() != null)
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
