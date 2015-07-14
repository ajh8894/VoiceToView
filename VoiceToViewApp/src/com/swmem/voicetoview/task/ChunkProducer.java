package com.swmem.voicetoview.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.util.Log;

import com.swmem.voicetoview.data.Chunk;
import com.swmem.voicetoview.data.Signal;
import com.swmem.voicetoview.service.Connection;

public class ChunkProducer implements Runnable {
	private BlockingQueue<Object> senderQueue;
	private int order;
	private byte[] pcm;

	public ChunkProducer(BlockingQueue<Object> senderQueue, int order, byte[] pcm) {
		this.senderQueue = senderQueue;
		this.order = order;
		this.pcm = pcm;
	}

	@Override
	public void run() {
		ExecutorService executor = Executors.newFixedThreadPool(1);
		Future<String> fSpeech = executor.submit(new SpeechRecognitionV1(pcm)); //speech task
		
		String text = null;
		try {
			senderQueue.put(new Signal(order, pcm));
			text = fSpeech.get();
			Log.d("result", "Complete!");
			
			if(text != null) {
				Log.d("result", text);
				senderQueue.put(new Chunk(order ,Connection.header[1], Connection.header[2], text)); // block
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

}
