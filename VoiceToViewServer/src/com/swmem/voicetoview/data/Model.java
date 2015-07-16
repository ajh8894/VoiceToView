package com.swmem.voicetoview.data;

import java.io.Serializable;

public class Model implements Serializable, Comparable<Model> {
	private static final long serialVersionUID = 1L;
	
	// header
	private String from;
	private String to;

	// input signal
	private byte buffers[];

	// return
	private int messageNum;
	private String textResult;
	private int emotionType;
	
	// view
	private String date;

	public Model(byte[] buffers) {
		this.buffers = buffers;
	}

	public Model() {
	}

	public int getMessageNum() {
		return messageNum;
	}

	public void setMessageNum(int messageNum) {
		this.messageNum = messageNum;
	}

	public byte[] getBuffers() {
		return buffers;
	}

	public void setBuffers(byte[] buffers) {
		this.buffers = buffers;
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

	public String getTextResult() {
		return textResult;
	}

	public void setTextResult(String textResult) {
		this.textResult = textResult;
	}

	public int getEmotionType() {
		return emotionType;
	}

	public void setEmotionType(int emotionType) {
		this.emotionType = emotionType;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public int compareTo(Model m) {
		return this.messageNum - m.getMessageNum();
	}
}