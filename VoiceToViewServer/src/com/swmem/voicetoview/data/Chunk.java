package com.swmem.voicetoview.data;

import java.io.Serializable;

public class Chunk implements Serializable {
	private static final long serialVersionUID = 1L;
	private int order;
	private String from, to;
	private String text;
	private int emotion;
	private String date;

	public Chunk(int order, String from, String to, String text) {
		this.order = order;
		this.from = from;
		this.to = to;
		this.text = text;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

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

	public int getEmotion() {
		return emotion;
	}

	public void setEmotion(int emotion) {
		this.emotion = emotion;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}