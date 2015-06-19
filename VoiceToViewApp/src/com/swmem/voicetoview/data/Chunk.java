package com.swmem.voicetoview.data;

import java.io.Serializable;

public class Chunk implements Serializable {
	private String phoneNumber;
	private String ip;
	private String text;
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
