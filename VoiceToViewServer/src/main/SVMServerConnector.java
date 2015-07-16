package main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import com.swmem.voicetoview.data.Model;

import data.Constants;
import data.ServerData;

public class SVMServerConnector extends Thread {
	private Socket socket;
	private ObjectInputStream ois;

	public void close() {
		System.out.println("svm disconnect()");
		try {
			if (ois != null) {
				ois.close();
			}
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			ois = null;
			socket = null;
		}
	}

	@Override
	public void run() {
		try {
			System.out.println("run");
			if (socket == null || !socket.isConnected() || socket.isClosed()) {
				socket = new Socket(Constants.SVM_SERVER_IP, Constants.SVM_SERVER_PORT);
				ois = new ObjectInputStream(socket.getInputStream());
				System.out.println("Svm server connect()");
			}

			while (true) {
				Model m = (Model) ois.readObject();
				System.out.println(m.getMessageNum() + " " + m.getFrom() + " " + m.getTo() + " " + m.getTextResult());
				ServerData.receiverQueue.put(m);
			}
		} catch (IOException | InterruptedException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
}
