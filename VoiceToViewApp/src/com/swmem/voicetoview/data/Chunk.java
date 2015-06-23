package com.swmem.voicetoview.data;

import java.io.Serializable;

public class Chunk implements Serializable {
	private static final long serialVersionUID = 1L;
	private String phoneNumber;
	private String gcmId;
	private String ip;
	private String text;
	private double[] features;
	private String emotion;
	private boolean completed;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public double[] getFeatures() {
		return features;
	}
	public void setFeatures(double[] features) {
		this.features = features;
	}
	public String getEmotion() {
		return emotion;
	}
	public void setEmotion(String emotion) {
		this.emotion = emotion;
	}
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted() {
		if(text != null && emotion != null)
			completed = true;
		else 
			completed = false;
	}
}
