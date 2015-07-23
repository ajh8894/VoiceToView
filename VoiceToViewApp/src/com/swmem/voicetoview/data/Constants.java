package com.swmem.voicetoview.data;

public class Constants {
	// header
	public static final String SERVICE_EXTRA_HEADER = "header";
	public static final String SERVICE_EXTRA_GENDER = "gender";
	public static final String KIND_RECEIVE = "receiver";
	public static final String KIND_SEND = "sender";
	public static final String KIND_END = "disconnect";
	
	// emotion
	public static final int SAD = 1;
	public static final int NATURAL = 2;
	public static final int ANGRY = 3;
	public static final int HAPPY = 4;
	
	// server	
	public static final String CONNECT_SERVER_IP = "211.189.127.145";
	public static final int CONNECT_SERVER_PORT = 8894;
	
/*	public static final String SVM_SERVER_IP = "211.189.127.217";
	public static final int SVM_SERVER_PORT = 3577;*/

	public static final String SVM_SERVER_IP = "211.189.127.145";
	public static final int SVM_SERVER_PORT = 3577;
	
	// sqlite option
	public static final int VIEW_OFF = 0;
	public static final int VIEW_ON = 1;
	public static final int FEMALE = 0;
	public static final int MALE = 1;
	
	// task option
	public static final int HIDE_VIEW = 0;
	public static final int ASSISTANT_VIEW = 1;
	public static final int MAX_RECORD_TIME = 15;
    public static final int TASK_DELAY_STOP = 1000;
    public static final int TASK_DELAY_RECONNECT = 3000;

	public static final int CONNECT_INIT = 100;
    public static final int CONNECT = 200;
    public static final int RECONNECT = 201;
    public static final int DISCONNECT = 500;
    public static final int REFRESH = 600;
    public static final int SWAP = 601;
}
