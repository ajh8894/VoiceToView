package data;
public class Constants {
	// server
	public static final int SERVER_PORT = 8080;
	public static final int RECEIVER_TIMEOUT = 60000;
	public static final int SENDER_TIMEOUT = 60000;
	
	// svm server info
/*	public static final String SVM_SERVER_IP = "211.189.127.218";
	public static final int SVM_SERVER_PORT = 3577;*/
	
	public static final String SVM_SERVER_IP = "211.189.127.145";
	public static final int SVM_SERVER_PORT = 8090;
	
	// header
	public static final String SERVICE_EXTRA_MODE = "mode";
	public static final String SERVICE_EXTRA_HEADER = "header";
	public static final String KIND_RECEIVE = "receiver";
	public static final String KIND_SEND = "sender";
	public static final String KIND_END = "disconnect";
	
	//emotion
	public static final int NATURAL = 0;
	public static final int HAPPY = 1;
	public static final int ANGRY = 2;
	public static final int SAD = 3;
}
