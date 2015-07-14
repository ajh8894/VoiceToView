package com.swmem.voicetoview.task;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;

import android.os.Handler;
import android.util.Log;

import com.swmem.voicetoview.data.Chunk;
import com.swmem.voicetoview.data.Constants;
import com.swmem.voicetoview.service.Connection;

public class ChunkReceiver extends Thread {
	private BlockingQueue<Chunk> receiverQueue;
	private Handler receiverHandler;
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

	@Override
	public void run() {
		super.run();
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
		try {
			Connection.init(Constants.CONNECT_INIT);
			
			while(isActivated && Connection.socket.isConnected() && !Connection.socket.isClosed()) {
				Chunk c = (Chunk) Connection.ois.readObject();
				Log.d("Receiver", c.getText() + " Chunk receive succsess");
				c.setDate(timeFormat.format(new Date()));
				receiverQueue.put(c);
				receiverHandler.sendEmptyMessage(Constants.REFRESH);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
			if(isActivated)
				receiverHandler.sendEmptyMessageDelayed(Constants.RECONNECT, Constants.TASK_DELAY_STOP);
		} catch (IOException e) {
			e.printStackTrace();
			if(isActivated)
				receiverHandler.sendEmptyMessageDelayed(Constants.RECONNECT, Constants.TASK_DELAY_STOP);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			Log.d("Receiver", "Receiver close");
			Connection.close();
		}
	}
}