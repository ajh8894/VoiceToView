package Server;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.swmem.voicetoview.data.Model;

import Core.MessageProcessor;


public class Server {
	private ServerSocket server;
	public static BlockingQueue<Model> queue;
	public static ObjectOutputStream oosServer;
	public static String CONNECTION_SERVER_IP = "/211.189.127.145";

	//PreProcessor Option Value
	public static boolean FILE_RECORD = false;//파일저장하고싶을경우
	public static boolean PLAY_SIGNAL = false;//스피커로 재생할경우
	
	//Test value
	public static boolean test=false;
	public static ObjectOutputStream oos;
	public static int count=0;
	public static int queueCount=0;
	public static String emotion,text;
	
	
	public Server() {
		// TODO Auto-generated constructor stub
		queue = new ArrayBlockingQueue<>(1000);
	}
	
	void synchronizedObjectOutputTake(){
		new Thread(new Runnable() {
			public void run() {
				while(true){
					try {
						Model responseModel = queue.take();
						oos.writeObject(responseModel);
						oos.flush();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
			}
		}).start();
	}
	
	void startWaiting(){
		try {
			server = new ServerSocket(3577);
			System.out.println("Server On");
			while(true){ 
				Socket socket = server.accept();
				if(socket.getInetAddress().toString().equals(CONNECTION_SERVER_IP)){
					System.out.println("컨넥션서버가 접속하였습니다.");
					oos = new ObjectOutputStream(socket.getOutputStream());
				}else{
					System.out.println(socket.getInetAddress()+":"+socket.getPort()+" 에서 데이터 받음 ////////////////////////////////////////////////");
					try {
						new MessageProcessor(socket,Server.count++).start();
						if(Server.test){
							Server.oos = new ObjectOutputStream(socket.getOutputStream());
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param model
	 * @param option 1.STT 2.감정 3.Loading
	 */
	public static void  sendMessageToServer(Model model,int option){
		Model responseModel = new Model();
		responseModel.setInitValues(model);
		System.out.print("===================================================\n");
		if(option==1){
			responseModel.setTextResult(model.getTextResult());
			System.out.println("[한글] 보낸메시지 : "+responseModel.getTextResult()+",메시지번호 : "+responseModel.getMessageNum());
		}else if(option==2){
			responseModel.setEmotionType(model.getEmotionType());
			System.out.println("[감정] 보낸메시지 : "+responseModel.getEmotionType()+",메시지번호 : "+responseModel.getMessageNum());
		}else{
			System.out.println("[작업시작] 보낸메시지 메시지번호 : "+responseModel.getMessageNum());
		}
		System.out.print("===================================================\n");
		
		try {
			queue.add(responseModel);
//			Socket connectServerSocket = new Socket(CONNECTION_SERVER_IP, 7070);
//			ObjectOutputStream oos = new ObjectOutputStream(connectServerSocket.getOutputStream());
//			oos.writeObject(responseModel);
//			oos.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void sendTargetQueue(String str, boolean isemotion) {
		// TODO Auto-generated method stub
		queueCount++;
		if(isemotion){
			emotion=str;
			System.out.println(emotion);
		}else{
			text=str;
			System.out.println(text);
		}
		
		if(queueCount==2){
			try {
				System.out.println("둘다도착");
				System.out.println("\""+text+"\" , // 목소리감정 : "+emotion);
//				Socket socket  = new Socket(CONNECTION_SERVER_IP1, 7070);
//				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//				oos.writeObject("\""+text+"\" , // 목소리감정 : "+emotion);
//				oos.flush();
				Server.oos.writeObject("\""+text+"\" , // 목소리감정 : "+emotion);
				oos.flush();
				queueCount=0;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}