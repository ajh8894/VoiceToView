package com.swmem.voicetoview.data;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class Chunk implements Serializable, Parcelable {
	private static final long serialVersionUID = 1L;
	private String from, to;
	private String text;
	private String date;

	public Chunk(String from, String to, String text) {
		this.from = from;
		this.to = to;
		this.text = text;
	}

	public Chunk(Parcel in) {
		readFromParcel(in);
	}

	public static final Parcelable.Creator<Chunk> CREATOR = new Parcelable.Creator<Chunk>() {
		@Override
		public Chunk createFromParcel(Parcel in) {
			return new Chunk(in);
		}

		@Override
		public Chunk[] newArray(int size) {
			return new Chunk[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(from);
		dest.writeString(to);
		dest.writeString(text);
		// dest.writeDoubleArray(features);
	}

	private void readFromParcel(Parcel in) {
		to = in.readString();
		from = in.readString();
		text = in.readString();
		// in.readDoubleArray(features);
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}