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
					System.out.println("-- 현재 순서: " + client.getOrder().intValue() + " 상태: " + client.getIsCompleted().intValue());
					System.out.println("1. take() 메세지 순서: " + m.getMessageNum() + " 감정: " + m.getEmotionType() + " 텍스트: " + m.getTextResult());
					if (m != null) {
						if (client.getOrder().intValue() > m.getMessageNum()) {
							System.out.println("--1. 둘 중 하나 실패 버림: -- 순서: " + m.getMessageNum() + " 감정: " + m.getEmotionType() + " 텍스트: " + m.getTextResult());
							continue;
						}
						if (client.getOrder().intValue() == m.getMessageNum()) {
							if (m.getTextResult() != null && m.getTextResult().equals(Constants.SPEECH_FAIL)) {
								System.out.println("-- 2. 음성 인식 실패 버림-- 순서: " + m.getMessageNum() + " 감정: " + m.getEmotionType() + " 텍스트: " + m.getTextResult());
								client.setIsCompleted(0);
								client.setOrder(client.getOrder() + 1);
								continue;
							} else if (m.getEmotionType() == Constants.SILENCE) {
								System.out.println("-- 3. 묵음 버림-- 순서: " + m.getMessageNum() + " 감정: " + m.getEmotionType() + " 텍스트: " + m.getTextResult());
								client.setIsCompleted(0);
								client.setOrder(client.getOrder() + 1);
								continue;
							} else {
								client.sendToClient(m);
								if (client.readFromClient()) {
									System.out.println("-- 보냄 -- 순서: " + m.getMessageNum() + " 감정: " + m.getEmotionType() + " 텍스트: " + m.getTextResult());
									client.setIsCompleted(client.getIsCompleted() + 1);
								}
								/*
								else
									senderQueue.put(m);*/
							}
						} else {
/*							if (Constants.MESSAGE_TIMEOUT <= (end - start) / 1000.0) {
								System.out.println("--4. 시간초과 버림: " + m.getMessageNum() + " 감정: " + m.getEmotionType() + " 텍스트: " + m.getTextResult());
								isCompleted = Integer.valueOf(0);
								order = Integer.valueOf(order.intValue() + 1);
							} else {

							}*/
							System.out.println("-- 복구 -- 순서: " + m.getMessageNum() + " 감정: " + m.getEmotionType() + " 텍스트: " + m.getTextResult());
							client.getSenderQueue().put(m);
						}

						if (client.getIsCompleted().intValue() == Constants.MESSAGE_SEND_COMPLETE) {
							System.out.println("★★★★★다 보냄");
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
					System.out.println("-- 현재 순서: " + client.getOrder().intValue() + " 상태: " + client.getIsCompleted().intValue());
					System.out.println("--인터럽트 복구-- 순서: " + m.getMessageNum() + " 감정: " + m.getEmotionType() + " 텍스트: " + m.getTextResult());
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
