package com.swmem.voicetoview.data;

public class Constants {
	// header
	public static final String SERVICE_EXTRA_HEADER = "header";
	public static final String SERVICE_EXTRA_KIND_CALL = "call";
	public static final String KIND_RECEIVE = "receiver";
	public static final String KIND_SEND = "sender";
	public static final String KIND_END = "disconnect";
	
	// server	
	public static final String SERVER_IP = "211.189.127.200";
	public static final int SERVER_PORT = 8080;
	
	// option
	public static final int VIEW_OFF = 0;
	public static final int VIEW_ON = 1;
	public static final int HIDE_VIEW = 0;
	public static final int ASSISTANT_VIEW = 1;
	public static final int MALE = 0;
	public static final int FEMALE = 1;
	public static final int MAX_RECORD_TIME = 15;
    public static final int TASK_DELAY_STOP = 1000;
    

	public static final int CONNECT_INIT = 100;
    public static final int CONNECT = 200;
    public static final int RECONNECT = 201;
    public static final int DISCONNECT = 500;
    public static final int REFRESH = 202;
    public static final int SWAP = 203;
    
	// Key obtained through Google Developer group
    public static final String V1_API_KEY = "AIzaSyBgnC5fljMTmCFeilkgLsOKBvvnx6CBS0M";
	// URL for Google API
    public static final String ROOT = "https://www.google.com/speech-api/full-duplex/v1/";
    public static final String UP_P1 = "up?lang=ko_kr&lm=dictation&client=chromium&pair=";
    public static final String UP_P2 = "&key=";
	// Variables used to establish return code
    public static final long MIN = 10000000;
    public static final long MAX = 900000009999999L;
}
