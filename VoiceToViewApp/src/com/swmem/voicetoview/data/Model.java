package com.swmem.voicetoview.data;

import java.io.Serializable;

public class Model implements Serializable, Comparable<Model> {
	private static final long serialVersionUID = 1L;

	// header
	private String from;
	private String to;
	private boolean man;
	private int messageNum;

	// input signal
	private byte buffers[];

	// return
	private String textResult;
	private int emotionType;

	// view
	private String date;

	public Model() {

	}

	public Model(String from, String to, boolean man, int messageNum,
			byte[] buffers) {
		this.from = from;
		this.to = to;
		this.man = man;
		this.messageNum = messageNum;
		this.buffers = buffers;
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

	public boolean isMan() {
		return man;
	}

	public void setMan(boolean man) {
		this.man = man;
	}

	@Override
	public int compareTo(Model m) {
		return this.messageNum - m.getMessageNum();
	}

	public void setInitValues(Model model) {
		this.from = model.from;
		this.to = model.to;
		this.messageNum = model.messageNum;
	}
}