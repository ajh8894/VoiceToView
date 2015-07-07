package com.swmem.voicetoview.task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;

import android.os.Handler;
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
	private boolean isActivated;

	public ChunkReceiver(BlockingQueue<Chunk> receiverQueue, Handler receiverHandler) {
		this.receiverQueue = receiverQueue;
		this.receiverHandler = receiverHandler;
		this.isActivated = true;
	}

	public boolean isActivated() {
		return isActivated;
	}

	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}

	public void close() {
		isActivated = false;
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
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
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
			while(isActivated && socket.isConnected() && !socket.isClosed()) {
				if(ois == null) {
					ois = new ObjectInputStream(socket.getInputStream());
				}
				Chunk c = (Chunk) ois.readObject();
				Log.d("Receiver", c.getText() + " Chunk receive succsess");
				c.setDate(timeFormat.format(new Date()));
				receiverQueue.put(c);
				receiverHandler.sendEmptyMessage(Constants.REFRESH);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
			if(isActivated)
				receiverHandler.sendEmptyMessage(Constants.RECONNECT);
		} catch (IOException e) {
			e.printStackTrace();
			if(isActivated)
				receiverHandler.sendEmptyMessage(Constants.RECONNECT);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			Log.d("Receiver", "Receiver close");
			close();
		}
	}
}