package com.swmem.voicetoview.data;

public class Constants {
	// header
	public static final String SERVICE_EXTRA_HEADER = "header";
	public static final String SERVICE_EXTRA_KIND_CALL = "call";
	public static final String KIND_RECEIVE = "receiver";
	public static final String KIND_SEND = "sender";
	public static final String KIND_END = "disconnect";
	
	// server	
	public static final String CONNECT_SERVER_IP = "211.189.127.145";
	public static final int CONNECT_SERVER_PORT = 8080;
/*	public static final String SVM_SERVER_IP = "211.189.127.218";
	public static final int SVM_SERVER_PORT = 3577;*/
	public static final String SVM_SERVER_IP = "211.189.127.145";
	public static final int SVM_SERVER_PORT = 8090;
	
	// sqlite option
	public static final int VIEW_OFF = 0;
	public static final int VIEW_ON = 1;
	public static final int MALE = 0;
	public static final int FEMALE = 1;
	
	// task option
	public static final int HIDE_VIEW = 0;
	public static final int ASSISTANT_VIEW = 1;
	public static final int MAX_RECORD_TIME = 15;
    public static final int TASK_DELAY_STOP = 1000;
    public static final int TASK_DELAY_RECONNECT = 2000;

	public static final int CONNECT_INIT = 100;
    public static final int CONNECT = 200;
    public static final int RECONNECT = 201;
    public static final int DISCONNECT = 500;
    public static final int REFRESH = 600;
    public static final int SWAP = 601;
}
