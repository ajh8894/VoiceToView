package com.swmem.voicetoview.task;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.swmem.voicetoview.data.Constants;
import com.swmem.voicetoview.service.Connection;

public class ChunkConsumer extends Thread {
	private BlockingQueue<Object> senderQueue;
	private Handler senderHandler;
	private Object resend;
	private boolean isActivated;

	public ChunkConsumer(BlockingQueue<Object> senderQueue, Handler senderHandler) {
		this.senderQueue = senderQueue;
		this.senderHandler = senderHandler;
		this.resend = null;
		this.isActivated = true;
	}
	
	public ChunkConsumer(BlockingQueue<Object> senderQueue, Handler senderHandler, Object resend) {
		this.senderQueue = senderQueue;
		this.senderHandler = senderHandler;
		this.resend = resend;
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
		Message msg = new Message();
		try {
			Connection.init(Constants.CONNECT_INIT);
			
			while(isActivated && Connection.socket.isConnected() && !Connection.socket.isClosed()) {
				if(resend == null)
					resend = senderQueue.take();
				
				Connection.oos.reset();
				Connection.oos.writeObject(resend);
				Connection.oos.flush();
				resend = null;
				Log.i("Consumer", "Chunk send succsess");
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
			if (isActivated && resend != null) {
				msg.what = Constants.RECONNECT;
				msg.obj = resend;
				senderHandler.sendMessageDelayed(msg, Constants.TASK_DELAY_STOP);
			}
		} catch (IOException e) {
			e.printStackTrace();
			if (isActivated && resend != null) {
				msg.what = Constants.RECONNECT;
				msg.obj = resend;
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

