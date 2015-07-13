package com.swmem.voicetoview.task;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.swmem.voicetoview.data.Chunk;
import com.swmem.voicetoview.data.Connection;
import com.swmem.voicetoview.data.Constants;

public class ChunkConsumer extends Thread {
	private BlockingQueue<Chunk> senderQueue;
	private Handler senderHandler;
	private Chunk reChunk;
	private boolean isActivated;

	public ChunkConsumer(BlockingQueue<Chunk> senderQueue, Handler senderHandler) {
		this.senderQueue = senderQueue;
		this.senderHandler = senderHandler;
		this.reChunk = null;
		this.isActivated = true;
	}
	
	public ChunkConsumer(BlockingQueue<Chunk> senderQueue, Handler senderHandler, Chunk reChunk) {
		this.senderQueue = senderQueue;
		this.senderHandler = senderHandler;
		this.reChunk = reChunk;
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
		Message msg = null;
		try {
			Connection.init(Constants.CONNECT_INIT);
			
			while(isActivated && Connection.socket.isConnected() && !Connection.socket.isClosed()) {

				if(reChunk == null)
					reChunk = senderQueue.take();
				
				Connection.oos.reset();
				Connection.oos.writeObject(reChunk);
				Connection.oos.flush();
				reChunk = null;
				Log.i("Consumer", "Chunk send succsess");
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
			if (isActivated && reChunk != null) {
				msg.what = Constants.RECONNECT;
				msg.obj = reChunk;
				senderHandler.sendMessageDelayed(msg, Constants.TASK_DELAY_STOP);
			}
		} catch (IOException e) {
			e.printStackTrace();
			if (isActivated && reChunk != null) {
				msg.what = Constants.RECONNECT;
				msg.obj = reChunk;
				senderHandler.sendMessageDelayed(msg, Constants.TASK_DELAY_STOP);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			Log.d("Consumer", "Consumer close");
			Connection.close();
		}
	}
}

