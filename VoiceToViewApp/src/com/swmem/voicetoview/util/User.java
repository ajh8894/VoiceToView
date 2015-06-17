package com.swmem.voicetoview.util;

public class User {
	private int mode;
	private int textSize;
	private String textStyle;

	public User(int mode, int textSize, String textStyle) {
		this.mode = mode;
		this.textStyle = textStyle;
		this.textSize = textSize;
	}

	public User() {
		// TODO Auto-generated constructor stub
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public String getTextStyle() {
		return textStyle;
	}

	public void setTextStyle(String textStyle) {
		this.textStyle = textStyle;
	}

	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}
}
