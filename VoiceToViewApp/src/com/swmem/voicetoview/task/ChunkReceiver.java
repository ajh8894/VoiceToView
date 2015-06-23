package com.swmem.voicetoview.task;

import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import android.os.Handler;
import android.util.Log;

import com.swmem.voicetoview.data.Chunk;

public class ChunkReceiver implements Runnable {
	private BlockingQueue<Chunk> queue;
	private Handler handler;
	
	public ChunkReceiver(BlockingQueue<Chunk> queue, Handler handler) {
		this.queue = queue;
		this.handler = handler;
	}
	
	@Override
	public void run() {
		try {
			InetAddress serverAddr = InetAddress.getByName("211.189.127.200");

			Log.d("TCP", "C: Connecting...");
			Socket socket = new Socket(serverAddr, 8080);
			socket.setSoTimeout(3000);
			try {
				ObjectInputStream iops = new ObjectInputStream(socket.getInputStream());
				Chunk c = (Chunk) iops.readObject();
				queue.put(c);
				// queue.
				Log.d("TCP", "C: Receive.");
				Log.d("TCP", "C: Done.");
				// iops.reset();
				iops.close();
			} catch (Exception e) {
				Log.e("TCP", "S: Error", e);
			} finally {
				socket.close();
			}
		} catch (Exception e) {
			Log.e("TCP", "C: Error", e);
		}

	}
}