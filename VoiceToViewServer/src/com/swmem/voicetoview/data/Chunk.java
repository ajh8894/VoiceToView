package com.swmem.voicetoview.data;
import java.io.Serializable;

public class Chunk implements Serializable {
	private static final long serialVersionUID = 1L;
	private String from, to;
	private String text;
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
