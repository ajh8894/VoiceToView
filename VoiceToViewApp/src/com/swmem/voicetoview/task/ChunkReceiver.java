package com.swmem.voicetoview.task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.swmem.voicetoview.data.Chunk;
import com.swmem.voicetoview.data.ConnectionInfo;
import com.swmem.voicetoview.data.Constants;

public class ChunkReceiver extends Thread {
	private BlockingQueue<Chunk> receiverQueue;
	private Handler receiverHandler;
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	public ChunkReceiver(BlockingQueue<Chunk> receiverQueue, Handler receiverHandler) {
		this.receiverQueue = receiverQueue;
		this.receiverHandler = receiverHandler;
	}

	public void close() {
		try {
			if (oos != null)
				oos.close();
			if (ois != null)
				ois.close();
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		super.run();
		Message msg = null;
		try {
			if (socket == null || !socket.isConnected()) {
				socket = new Socket(Constants.SERVER_IP, Constants.SERVER_PORT);
				if(oos == null) {
					oos = new ObjectOutputStream(socket.getOutputStream());
					oos.writeObject(ConnectionInfo.header);
					oos.flush();
					Log.d("Receiver", "is Connected true");
				}
			}
			while(socket.isConnected() && !socket.isClosed()) {
				if(ois == null) {
					ois = new ObjectInputStream(socket.getInputStream());
				}
				Log.d("Receiver", "read");
				Chunk c = (Chunk) ois.readObject();
				Log.d("receive", c.getText());
				receiverQueue.put(c);
				receiverHandler.sendEmptyMessage(Constants.REFRESH);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Log.d("Receiver", "destroy");
		close();
	}
}