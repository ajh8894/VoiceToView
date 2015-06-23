package com.swmem.voicetoview.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
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
		Future<String> fSpeech = executor.submit(new SpeechRecognition(pcm)); //speech task
		Future<double[]> fEmotion = executor.submit(new EmotionRecognition(pcm)); //emotion task
		
		String text = null;
		double[] features = null;
		try {
			text = fSpeech.get();
			features = fEmotion.get();
			
			if(text != null /*&& features != null*/) {
				Chunk senderChunk = new Chunk();
				senderChunk.setText(text);
				senderChunk.setFeatures(features);
				queue.add(senderChunk);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} 
		
	}

}
