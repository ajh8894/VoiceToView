package com.swmem.voicetoview.task;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;

import android.os.Handler;
import android.util.Log;

import com.swmem.voicetoview.data.Connection;
import com.swmem.voicetoview.data.Constants;
import com.swmem.voicetoview.data.Model;

public class ModelReceiver extends Thread {
	private BlockingQueue<Model> receiverQueue;
	private Handler receiverHandler;
	private boolean isActivated;

	public ModelReceiver(BlockingQueue<Model> receiverQueue, Handler receiverHandler) {
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
			Connection.connect(Constants.CONNECT);
			
			while(isActivated && Connection.socket.isConnected() && !Connection.socket.isClosed()) {
				Model m = (Model) Connection.ois.readObject();
				Log.d("Receiver", m.getTextResult() + " Chunk receive succsess");
				m.setDate(timeFormat.format(new Date()));
				receiverQueue.put(m);
				receiverHandler.sendEmptyMessage(Constants.REFRESH);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			if(isActivated)
				receiverHandler.sendEmptyMessageDelayed(Constants.RECONNECT, Constants.TASK_DELAY_RECONNECT);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			Log.d("Receiver", "Receiver close");
			Connection.disconnect();
		}
	}
}