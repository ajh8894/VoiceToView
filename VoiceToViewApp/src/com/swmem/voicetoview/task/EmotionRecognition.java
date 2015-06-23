package com.swmem.voicetoview.task;

import java.util.concurrent.Callable;

public class EmotionRecognition implements Callable<double[]> {
	private byte[] pcm;
	
	public EmotionRecognition(byte[] pcm) {
		this.pcm = pcm;
	}

	@Override
	public double[] call() throws Exception {
		double[] features = new double[10];
		
		for(int i = 0; i < 10; i++) {
			features[i] = i + 0.0;
		}
		
		return features;
	}
}
