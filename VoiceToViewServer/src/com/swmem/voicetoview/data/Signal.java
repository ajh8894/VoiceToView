package com.swmem.voicetoview.data;

import java.io.Serializable;

public class Signal implements Serializable {
	private static final long serialVersionUID = 1L;
	private int order;
	private String from, to;
	private byte[] pcm;
	
	public Signal(int order, byte[] pcm) {
		this.order = order;
		this.pcm = pcm;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
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
	public byte[] getPcm() {
		return pcm;
	}
	public void setPcm(byte[] pcm) {
		this.pcm = pcm;
	}
}
