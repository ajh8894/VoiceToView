package com.swmem.voicetoview.data;

public class Talk {
	private int key;
	private String date;
	private String id;

	public Talk(String date, String id) {
		this.date = date;
		this.id = id;
	}

	public Talk(int key, String date, String id) {
		this(date, id);
		this.key = key;
	}
	
	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
