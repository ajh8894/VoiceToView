package com.swmem.voicetoview.task;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;

import android.os.Handler;

import com.swmem.voicetoview.data.Constants;
import com.swmem.voicetoview.data.Model;

public class ModelSender extends Thread {
	private boolean isActivated;
	private BlockingQueue<Model> senderQueue;
	private Handler senderHandler;
	private Socket socket;
	private ObjectOutputStream oos;

	public ModelSender(BlockingQueue<Model> senderQueue, Handler senderHandler) {
		this.isActivated = true;
		this.senderQueue = senderQueue;
		this.senderHandler = senderHandler;
	}

	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}

	public void close() {
		try {
			if (oos != null)
				oos.close();
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			oos = null;
			socket = null;
		}
	}

	@Override
	public void run() {
		super.run();
		Model m = null;
		try {
			while (isActivated) {
				m = senderQueue.take();

				socket = new Socket(Constants.SVM_SERVER_IP, Constants.SVM_SERVER_PORT);
				oos = new ObjectOutputStream(socket.getOutputStream());
				//oos.reset();
				oos.writeObject(m);
				oos.flush();
				close();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			if (m != null) {
				try {
					senderQueue.put(m);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			senderHandler.sendEmptyMessageDelayed(Constants.RECONNECT, Constants.TASK_DELAY_RECONNECT);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
}
