package com.swmem.voicetoview.data;

public class User {
	private int mode;
	private int gender;

	public User() {
	}

	public User(int mode, int gender) {
		this.mode = mode;
		this.gender = gender;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}
}
