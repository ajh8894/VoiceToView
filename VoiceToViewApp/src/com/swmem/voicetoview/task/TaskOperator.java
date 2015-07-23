package com.swmem.voicetoview.task;

import java.io.IOException;
import java.net.UnknownHostException;

import android.os.Handler;
import android.util.Log;

import com.swmem.voicetoview.data.Connection;
import com.swmem.voicetoview.data.Constants;

public class TaskOperator extends Thread {
	private boolean isActivated;
	private int type;
	private Handler senderHandler;
	private Handler receiverHandler;

	public TaskOperator(int type, Handler senderHandler, Handler receiverHandler) {
		isActivated = true;
		this.type = type;
		this.senderHandler = senderHandler;
		this.receiverHandler = receiverHandler;
	}
	
	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}
	
	@Override
	public void run() {
		super.run();
		try {
			Connection.connect(Constants.CONNECT);
			
			if (type == Constants.CONNECT_INIT) {
				boolean response = Connection.ois.readBoolean(); // true = STT_ON, false = STT_OFF
				Connection.oos.writeBoolean(true);
				Connection.oos.flush();
				if (response) { // write, STT_ON
					senderHandler.sendEmptyMessage(Constants.CONNECT);
				}
				if (Connection.header[0].equals(Constants.KIND_RECEIVE)) { // read, VIEW_ON
					receiverHandler.sendEmptyMessage(Constants.CONNECT);
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
			if(isActivated)
				senderHandler.sendEmptyMessageDelayed(type, Constants.TASK_DELAY_RECONNECT);
		}
	}
}
