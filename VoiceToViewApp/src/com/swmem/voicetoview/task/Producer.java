package com.swmem.voicetoview.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.swmem.voicetoview.data.Chunk;

public class Producer implements Runnable {
	private BlockingQueue<Chunk> queue;
	private byte[] pcm;
	private ExecutorService executor;
	
	public Producer(BlockingQueue<Chunk> queue, byte[] pcm) {
		this.queue = queue;
		this.pcm = pcm;
		this.executor = Executors.newFixedThreadPool(2);
	}
	
	@Override
	public void run() {
		Future<String> future = executor.submit(new CallableImpl());
		
		Chunk senderChunk = new Chunk();
		senderChunk.setText(future.get()); // speech is Completed
		queue.add(senderChunk);
	}

}
