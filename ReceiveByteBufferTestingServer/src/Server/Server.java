package Server;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import Core.MessageProcessor;

import com.swmem.voicetoview.data.Model;


public class Server {
	private ServerSocket server;
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
	}
	
	void startWaiting(){
		try {
			server = new ServerSocket(3577);
			System.out.println("Server On");
			while(true){ 
				Socket socket = server.accept();
				if(socket.getInetAddress().toString().equals(CONNECTION_SERVER_IP)){
					System.out.println("컨넥션 서버가 접속했습니다.");
					oosServer = new ObjectOutputStream(socket.getOutputStream());
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
	
	public static void  sendMessageToServer(Model model,boolean emotion){
		Model responseModel = new Model();
		responseModel.setInitValues(model);
		System.out.print("===================================================\n");
		if(emotion){
			responseModel.setEmotionType(model.getEmotionType());
			System.out.println("[감정] 보낸메시지 : "+responseModel.getEmotionType()+",메시지번호 : "+responseModel.getMessageNum());
		}else{
			responseModel.setTextResult(model.getTextResult());
			System.out.println("[한글] 보낸메시지 : "+responseModel.getTextResult()+",메시지번호 : "+responseModel.getMessageNum());
		}
		System.out.print("===================================================\n");
		try {
			Server.oosServer.writeObject(responseModel);
			Server.oosServer.flush();
		} catch (IOException e) {
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