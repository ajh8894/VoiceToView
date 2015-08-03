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
			if(x==null){
				modelBean.setTextResult("X");
				Server.sendMessageToServer(this.modelBean,1);
			}else if(x!=null){
				emotionType = (int)svm.svm_predict(svmModel,x);
			}

			this.modelBean.setEmotionType(emotionType);
			Server.sendMessageToServer(this.modelBean,2);
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				ois.close();
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}