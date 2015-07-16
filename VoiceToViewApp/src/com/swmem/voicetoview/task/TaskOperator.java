package com.swmem.voicetoview.task;

import java.io.IOException;
import java.net.UnknownHostException;

import android.os.Handler;

import com.swmem.voicetoview.data.Connection;
import com.swmem.voicetoview.data.Constants;

public class TaskOperator extends Thread {
	private int type;
	private Handler senderHandler;
	private Handler receiverHandler;

	public TaskOperator(int type, Handler senderHandler, Handler receiverHandler) {
		this.type = type;
		this.senderHandler = senderHandler;
		this.receiverHandler = receiverHandler;
	}
	
	@Override
	public void run() {
		super.run();
		try {
			Connection.connect(Constants.CONNECT);
			
			if (type == Constants.CONNECT_INIT) {
				boolean response = Connection.ois.readBoolean(); // true = STT_ON
				Connection.oos.writeBoolean(true);
				Connection.oos.flush();
				if (response) { // write
					senderHandler.sendEmptyMessage(Constants.CONNECT);
				}
				if (Connection.header[0].equals(Constants.KIND_RECEIVE)) { //VIEW_ON
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
			Connection.disconnect();
			senderHandler.sendEmptyMessageDelayed(type, Constants.TASK_DELAY_RECONNECT);
		}
	}
}
