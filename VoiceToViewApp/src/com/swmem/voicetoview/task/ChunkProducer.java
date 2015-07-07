package com.swmem.voicetoview.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.util.Log;

import com.swmem.voicetoview.data.Chunk;
import com.swmem.voicetoview.data.ConnectionInfo;

public class ChunkProducer implements Runnable {
	private BlockingQueue<Chunk> queue;
	private byte[] pcm;
	
	public ChunkProducer(BlockingQueue<Chunk> queue, byte[] pcm) {
		this.queue = queue;
		this.pcm = pcm;
	}

	@Override
	public void run() {
		ExecutorService executor = Executors.newFixedThreadPool(1);
		Future<String> fSpeech = executor.submit(new SpeechRecognitionV1(pcm)); //speech task
		//Future<double[]> fEmotion = executor.submit(new EmotionRecognition(pcm)); //emotion task
		
		String text = null;
		//double[] features = null;
		try {
			text = fSpeech.get();
			//features = fEmotion.get();
			
			if(text != null /*&& features != null*/) {
				//Chunk senderChunk = new Chunk("from", "to", text);
				//senderChunk.setText(text);
				//senderChunk.setFeatures(features);
				Log.d("result", text);
				queue.put(new Chunk(ConnectionInfo.header[1], ConnectionInfo.header[2], text)); // block
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} 
	}

}
