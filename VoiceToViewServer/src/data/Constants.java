package data;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import client.Client;

import com.swmem.voicetoview.data.Model;

public class Constants {
	// server
	public static final int SERVER_PORT = 8894;
	public static HashMap<String, Client> clients = new HashMap<String, Client>();
	public static BlockingQueue<Model> receiverQueue = new ArrayBlockingQueue<Model>(2024);
	
	// svm_server
<<<<<<< HEAD
	public static final String SVM_SERVER_IP = "211.189.127.145";
	//public static final String SVM_SERVER_IP = "211.189.127.217";
=======
	//public static final String SVM_SERVER_IP = "211.189.127.145";
	public static final String SVM_SERVER_IP = "localhost";
>>>>>>> a9d61a3829a9be48b59223697e432877e4456e6d
	public static final int SVM_SERVER_PORT = 3577;
	
	// header
	public static final String SERVICE_EXTRA_MODE = "mode";
	public static final String SERVICE_EXTRA_HEADER = "header";
	public static final String KIND_RECEIVE = "receiver";
	public static final String KIND_SEND = "sender";
	public static final String KIND_END = "disconnect";

	// option
	public static final int SENDER_TIMEOUT = 1000 * 60 * 2;
	public static final long RESTORE_TIMEOUT = 1000 * 5;
	
    //speech
	public static final String SPEECH_FAIL = "X";
	
	//emotion
	public static final int EMOTION_NOT_COMPLETE = 0;
	public static final int SAD = 1;
	public static final int NATURAL = 2;
	public static final int ANGRY = 3;
	public static final int HAPPY = 4;
	public static final int SILENCE = 5;
}
