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
	private boolean mIsActivated;
	private BlockingQueue<Model> mReceiverQueue;
	private Handler mReceiverHandler;

	public ModelReceiver(BlockingQueue<Model> mReceiverQueue, Handler mReceiverHandler) {
		this.mIsActivated = true;
		this.mReceiverQueue = mReceiverQueue;
		this.mReceiverHandler = mReceiverHandler;
	}

	public boolean isActivated() {
		return mIsActivated;
	}

	public void setActivated(boolean isActivated) {
		this.mIsActivated = isActivated;
	}

	@Override
	public void run() {
		super.run();
		//SimpleDateFormat timeFormat = new SimpleDateFormat("a hh:mm", Locale.KOREA);
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
		try {
			Connection.connect(Constants.CONNECT);
			mReceiverHandler.sendEmptyMessage(Constants.CREATE);
			while(mIsActivated && Connection.socket.isConnected() && !Connection.socket.isClosed()) {
				Model m = (Model) Connection.ois.readObject();
				Log.d("Receiver", m.getMessageNum() + " " + m.getEmotionType() + " " + m.getTextResult() + " " + m.getConfidence() + " Model receive succsess");
				m.setTime(timeFormat.format(new Date()));
				if(m.getTextResult() != null && m.getTextResult().equals(Constants.SPEECH_FAIL)) {
					mReceiverHandler.sendEmptyMessage(Constants.REMOVE);
					continue;
				}
				mReceiverQueue.put(m);
				mReceiverHandler.sendEmptyMessage(Constants.REFRESH);
				
				Connection.oos.writeBoolean(true);
				Connection.oos.flush();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(ModelReceiver.class.getName(), "IOException");
			e.printStackTrace();
			if(mIsActivated)
				mReceiverHandler.sendEmptyMessageDelayed(Constants.RECONNECT, Constants.TASK_DELAY_RECONNECT);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			Log.e(ModelReceiver.class.getName(), "InterruptedException");
			e.printStackTrace();
		} finally {
			Log.d(ModelReceiver.class.getName(), "Receiver close");
			Connection.disconnect();
		}
	}
}