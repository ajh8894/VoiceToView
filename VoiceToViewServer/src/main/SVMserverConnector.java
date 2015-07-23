package main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import com.swmem.voicetoview.data.Model;

import data.Constants;

public class SVMserverConnector extends Thread {
	private Socket socket;
	private ObjectInputStream ois;

	@Override
	public void run() {
		try {
			if (socket == null || !socket.isConnected() || socket.isClosed()) {
				socket = new Socket(Constants.SVM_SERVER_IP, Constants.SVM_SERVER_PORT);
				ois = new ObjectInputStream(socket.getInputStream());
				System.out.println("SVM server " + Constants.SVM_SERVER_IP + "/" + Constants.SVM_SERVER_PORT + " connect");
			}

			while (true) {
				Model m = (Model) ois.readObject();
				System.out.println("SVM result: " + m.getMessageNum() + " " + m.getFrom() + " " + m.getTo() + " " + m.getEmotionType() + " " + m.getTextResult());
				Constants.receiverQueue.put(m);
			}
		} catch (IOException | InterruptedException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
