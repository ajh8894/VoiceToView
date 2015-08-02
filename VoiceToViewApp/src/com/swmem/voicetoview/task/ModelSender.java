package com.swmem.voicetoview.task;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;

import android.os.Handler;
import android.util.Log;

import com.swmem.voicetoview.data.Constants;
import com.swmem.voicetoview.data.Model;

public class ModelSender extends Thread {
	private boolean mIsActivated;
	private BlockingQueue<Model> mSenderQueue;
	private Handler mSenderHandler;
	private Socket socket;
	private ObjectOutputStream oos;

	public ModelSender(BlockingQueue<Model> mSenderQueue, Handler mSenderHandler) {
		this.mIsActivated = true;
		this.mSenderQueue = mSenderQueue;
		this.mSenderHandler = mSenderHandler;
	}

	public void setActivated(boolean isActivated) {
		this.mIsActivated = isActivated;
	}

	public void close() {
		try {
			if (oos != null)
				oos.close();
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			Log.e(ModelSender.class.getName(), "IOException");
			e.printStackTrace();
		} finally {
			oos = null;
			socket = null;
		}
	}

	@Override
	public void run() {
		super.run();
		Model m = null;
		try {
			while (mIsActivated) {
				m = mSenderQueue.take();

				socket = new Socket(Constants.SVM_SERVER_IP, Constants.SVM_SERVER_PORT);
				oos = new ObjectOutputStream(socket.getOutputStream());
				//oos.reset();
				oos.writeObject(m);
				oos.flush();
				close();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(ModelSender.class.getName(), "IOException");
			e.printStackTrace();
			if (m != null) {
				Log.e(ModelSender.class.getName(), "restore: " + m.getMessageNum() + " " + m.getEmotionType() + " "+ m.getTextResult());
				try {
					mSenderQueue.put(m);
				} catch (InterruptedException e1) {
					Log.e(ModelSender.class.getName(), "InterruptedException");
					e1.printStackTrace();
				}
			}
			if(mIsActivated)
				mSenderHandler.sendEmptyMessageDelayed(Constants.RECONNECT, Constants.TASK_DELAY_RECONNECT);
		} catch (InterruptedException e) {
			Log.e(ModelSender.class.getName(), "InterruptedException");
			e.printStackTrace();
		} finally {
			close();
		}
	}
}
