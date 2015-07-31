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
	public static boolean FILE_RECORD = false;//���������ϰ�������
	public static boolean PLAY_SIGNAL = false;//����Ŀ�� ����Ұ��
	
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
					System.out.println("���ؼ� ������ �����߽��ϴ�.");
					oosServer = new ObjectOutputStream(socket.getOutputStream());
				}else{
					System.out.println(socket.getInetAddress()+":"+socket.getPort()+" ���� ������ ���� ////////////////////////////////////////////////");
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
			System.out.println("[����] �����޽��� : "+responseModel.getEmotionType()+",�޽�����ȣ : "+responseModel.getMessageNum());
		}else{
			responseModel.setTextResult(model.getTextResult());
			System.out.println("[�ѱ�] �����޽��� : "+responseModel.getTextResult()+",�޽�����ȣ : "+responseModel.getMessageNum());
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
				System.out.println("�Ѵٵ���");
				System.out.println("\""+text+"\" , // ��Ҹ����� : "+emotion);
				Server.oos.writeObject("\""+text+"\" , // ��Ҹ����� : "+emotion);
				oos.flush();
				queueCount=0;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}