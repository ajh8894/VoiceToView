package Core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import SVM.LibSVM.svm;
import SVM.LibSVM.svm_model;
import SVM.LibSVM.svm_node;
import Server.Server;

import com.swmem.voicetoview.data.Model;


public class MessageProcessor extends Thread {
	private Socket socket;
	private ObjectInputStream ois;
	private Model modelBean;
	private PreProcessor preProcessor;
	private svm_model svmModel;
	private int tempMsgNum;

	public MessageProcessor(Socket socket,int tempMsgNum) {
		this.tempMsgNum = tempMsgNum; 
		this.socket = socket;
		preProcessor = new PreProcessor(tempMsgNum);
	}

	@Override
	public void run() {
		try{
			ois = new ObjectInputStream(socket.getInputStream());
			modelBean  = (Model) ois.readObject();


			// TODO Auto-generated method stub
			System.out.println(tempMsgNum+"번째 받은 음성데이터");
			//1.STT run() 을 샘플추출시 null이 아닌경우에만 보내도록 안으로 옮김
			//			new SttAdapter(modelBean).start();

			//2. 이 run()은 작업종료이므로 emotiom Processing이어 진행
			svm_node[] x= preProcessor.printSampleFilesFeatures(modelBean);
			if(modelBean.isMan()){
				System.out.println("남자모델적용");
				svmModel = svm.svm_load_model(preProcessor.modelNameForMan);
			}else{
				System.out.println("여자모델적용");
				svmModel = svm.svm_load_model(preProcessor.modelNameForWomen);
			}
			int emotionType=5;
			if(x!=null){
				emotionType = (int)svm.svm_predict(svmModel,x);
			}
			String emotion=null;
			switch (emotionType) {
			case 1:
				emotion="슬픔";
				break;
			case 2:
				emotion="보통";
				break;
			case 3:
				emotion="화남";
				break;
			case 4:
				emotion="기쁨";
				break;
			case 5:
				//이경우 STT Run이 작동도안하므로 Google에 STT보내지않음. 그래서 X를 여기서 임의로 보내준다.
				emotion="전체묵음";
				modelBean.setTextResult("X");
				Server.sendMessageToServer(this.modelBean,1);
				break;
			}
			//			System.out.println(" (서버에저장될 메시지번호: " + tempMsgNum+")");

			this.modelBean.setEmotionType(emotionType);
			Server.sendMessageToServer(this.modelBean,2);
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				socket.close();
				ois.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}