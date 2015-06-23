package com.swmem.voicetoview.task;

import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import android.util.Log;

import com.swmem.voicetoview.data.Chunk;

public class Consumer implements Runnable {
	private BlockingQueue<Chunk> queue;

	public Consumer(BlockingQueue<Chunk> queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		if (queue.isEmpty()) {
			try {
				Chunk c = queue.take();
				Log.d("Consumer", c.toString());

				if (c.getText() != null)
					Log.d("text", ":" + c.getText());
				else
					Log.d("text", ": text null");

				if (c.getFeatures() != null)
					Log.d("features", ":" + c.getFeatures().toString());
				else
					Log.d("features", ": features null");

				try {
					InetAddress serverAddr = InetAddress.getByName("211.189.127.200");

					Log.d("TCP", "C: Connecting...");
					Socket socket = new Socket(serverAddr, 8080);

					try {
						ObjectOutputStream oops = new ObjectOutputStream(socket.getOutputStream());
						oops.writeObject(c);
						Log.d("TCP", "C: Sent.");
						Log.d("TCP", "C: Done.");
						
						//oops.reset();
						oops.close();
					} catch (Exception e) {
						Log.e("TCP", "S: Error", e);
					} finally {
						socket.close();
					}
				} catch (Exception e) {
					Log.e("TCP", "C: Error", e);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
