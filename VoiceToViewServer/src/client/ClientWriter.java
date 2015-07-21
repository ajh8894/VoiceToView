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
				Thread.sleep(1500);
				//end = System.currentTimeMillis();
				if (isAlive()) {
					System.out.println();
					System.out.println("-- ���� ����: " + client.getOrder().intValue() + " ����: " + client.getIsCompleted().intValue());
					System.out.println("1. take() �޼��� ����: " + m.getMessageNum() + " ����: " + m.getEmotionType() + " �ؽ�Ʈ: " + m.getTextResult());
					if (m != null) {
						if (client.getOrder().intValue() > m.getMessageNum()) {
							System.out.println("--1. �� �� �ϳ� ���� ����: -- ����: " + m.getMessageNum() + " ����: " + m.getEmotionType() + " �ؽ�Ʈ: " + m.getTextResult());
							continue;
						}
						if (client.getOrder().intValue() == m.getMessageNum()) {
							if (m.getTextResult() != null && m.getTextResult().equals(Constants.SPEECH_FAIL)) {
								System.out.println("-- 2. ���� �ν� ���� ����-- ����: " + m.getMessageNum() + " ����: " + m.getEmotionType() + " �ؽ�Ʈ: " + m.getTextResult());
								client.setIsCompleted(0);
								client.setOrder(client.getOrder() + 1);
								continue;
							} else if (m.getEmotionType() == Constants.SILENCE) {
								System.out.println("-- 3. ���� ����-- ����: " + m.getMessageNum() + " ����: " + m.getEmotionType() + " �ؽ�Ʈ: " + m.getTextResult());
								client.setIsCompleted(0);
								client.setOrder(client.getOrder() + 1);
								continue;
							} else {
								client.sendToClient(m);
								if (client.readFromClient()) {
									System.out.println("-- ���� -- ����: " + m.getMessageNum() + " ����: " + m.getEmotionType() + " �ؽ�Ʈ: " + m.getTextResult());
									client.setIsCompleted(client.getIsCompleted() + 1);
								}
								/*
								else
									senderQueue.put(m);*/
							}
						} else {
/*							if (Constants.MESSAGE_TIMEOUT <= (end - start) / 1000.0) {
								System.out.println("--4. �ð��ʰ� ����: " + m.getMessageNum() + " ����: " + m.getEmotionType() + " �ؽ�Ʈ: " + m.getTextResult());
								isCompleted = Integer.valueOf(0);
								order = Integer.valueOf(order.intValue() + 1);
							} else {

							}*/
							System.out.println("-- ���� -- ����: " + m.getMessageNum() + " ����: " + m.getEmotionType() + " �ؽ�Ʈ: " + m.getTextResult());
							client.getSenderQueue().put(m);
						}

						if (client.getIsCompleted().intValue() == Constants.MESSAGE_SEND_COMPLETE) {
							System.out.println("�ڡڡڡڡڴ� ����");
							client.setIsCompleted(0);
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
					System.out.println("-- ���� ����: " + client.getOrder().intValue() + " ����: " + client.getIsCompleted().intValue());
					System.out.println("--���ͷ�Ʈ ����-- ����: " + m.getMessageNum() + " ����: " + m.getEmotionType() + " �ؽ�Ʈ: " + m.getTextResult());
					if(client != null && client.getSenderQueue() != null)
						client.getSenderQueue().put(m);
					else 
						System.out.println("queue null!!");
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		} finally {
			client.close();
		}
	}
}
