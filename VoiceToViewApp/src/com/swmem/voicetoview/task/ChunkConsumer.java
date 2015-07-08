package com.swmem.voicetoview.task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.swmem.voicetoview.data.Chunk;
import com.swmem.voicetoview.data.ConnectionInfo;
import com.swmem.voicetoview.data.Constants;

public class ChunkConsumer extends Thread {
	private BlockingQueue<Chunk> senderQueue;
	private Handler senderHandler;
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
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

	public void close() {
		isActivated = false;
		try {
			if(ois != null)
				ois.close();
			if(oos != null)
				oos.close();
			if(socket != null)
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		super.run();
		Message msg = null;
		try {
			if (socket == null || !socket.isConnected()) {
				socket = new Socket(Constants.SERVER_IP, Constants.SERVER_PORT);
			}
			
			while(isActivated && socket.isConnected() && !socket.isClosed()) {
				if(oos == null && ois == null) {
					oos = new ObjectOutputStream(socket.getOutputStream());
					ois = new ObjectInputStream(socket.getInputStream());
					
					oos.writeObject(ConnectionInfo.header);
					oos.flush();
					Log.d("Consumer", "is Connected true!");
					
					boolean response = ois.readBoolean();
					Log.d("Consumer", "response is " + response);
					if (response) {
						if (reChunk == null && ConnectionInfo.call == Constants.KIND_CALL_SENDER)
							senderHandler.sendEmptyMessage(Constants.CONNECT);
					} else {
						break;
					}
				}
				if(reChunk == null)
					reChunk = senderQueue.take();
				
				oos.reset();
				oos.writeObject(reChunk);
				oos.flush();
				reChunk = null;
				Log.i("Consumer", "Chunk send succsess");
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
			if (reChunk != null) {
				msg.what = Constants.RECONNECT;
				msg.obj = reChunk;
				senderHandler.sendMessageDelayed(msg, Constants.TASK_DELAY_STOP);
			}
		} catch (IOException e) {
			e.printStackTrace();
			if (reChunk != null) {
				msg.what = Constants.RECONNECT;
				msg.obj = reChunk;
				senderHandler.sendMessageDelayed(msg, Constants.TASK_DELAY_STOP);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			Log.d("Consumer", "Consumer close");
			close();
		}
	}
}

