package com.swmem.voicetoview.task;

import java.io.IOException;
import java.net.UnknownHostException;

import android.os.Handler;
import android.util.Log;

import com.swmem.voicetoview.data.Connection;
import com.swmem.voicetoview.data.Constants;

public class TaskOperator extends Thread {
	private boolean mIsActivated;
	private int mType;
	private Handler mSenderHandler;
	private Handler mReceiverHandler;

	public TaskOperator(int mType, Handler mSenderHandler, Handler mReceiverHandler) {
		this.mIsActivated = true;
		this.mType = mType;
		this.mSenderHandler = mSenderHandler;
		this.mReceiverHandler = mReceiverHandler;
	}
	
	public void setActivated(boolean isActivated) {
		this.mIsActivated = isActivated;
	}
	
	@Override
	public void run() {
		super.run();
		try {
			Connection.connect(Constants.CONNECT);
			
			if (mType == Constants.CONNECT_INIT) {
				boolean response = Connection.ois.readBoolean(); // true = STT_ON, false = STT_OFF
				Connection.oos.writeBoolean(true);
				Connection.oos.flush();
				if (response) { // write, STT_ON
					mSenderHandler.sendEmptyMessage(Constants.CONNECT);
				}
				if (Connection.header[0].equals(Constants.KIND_RECEIVE)) { // read, VIEW_ON
					mReceiverHandler.sendEmptyMessage(Constants.CONNECT);
				}
			} else {
				Connection.disconnect();
				Connection.connect(Constants.DISCONNECT);
				Connection.disconnect();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TaskOperator.class.getName(), "IOException");
			Connection.disconnect();
			if(mIsActivated)
				mSenderHandler.sendEmptyMessageDelayed(mType, Constants.TASK_DELAY_RECONNECT);
		}
	}
}
