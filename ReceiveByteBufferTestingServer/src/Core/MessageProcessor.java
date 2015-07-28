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
	Model modelBean;
	PreProcessor preProcessor;
	ObjectInputStream ois;
	svm_model svmModel;
	int tempMsgNum;
	Socket socket;

	//TestValue

	public MessageProcessor(Socket socket,int tempMsgNum) {
		// TODO Auto-generated constructor stub
		this.tempMsgNum =tempMsgNum; 
		this.socket = socket;
		preProcessor = new PreProcessor(tempMsgNum);
	}

	@Override
	public void run() {
		try{
			ois = new ObjectInputStream(socket.getInputStream());
			modelBean  = (Model) ois.readObject();

			// TODO Auto-generated method stub
			System.out.println(tempMsgNum+"��° ���� ����������");
			//1.STT run() �� ��������� null�� �ƴѰ�쿡�� �������� ������ �ű�
//			new SttAdapter(modelBean).start();

			//2. �� run()�� �۾������̹Ƿ� emotiom Processing�̾� ����
			svm_node[] x= preProcessor.printSampleFilesFeatures(modelBean);
			if(modelBean.isMan()){
				System.out.println("���ڸ�����");
				svmModel = svm.svm_load_model(preProcessor.modelNameForMan);
			}else{
				System.out.println("���ڸ�����");
				svmModel = svm.svm_load_model(preProcessor.modelNameForWomen);
			}
			int emotionType=5;
			if(x!=null){
				emotionType = (int)svm.svm_predict(svmModel,x);
			}
			String emotion=null;
			switch (emotionType) {
			case 1:
				emotion="����";
				break;
			case 2:
				emotion="����";
				break;
			case 3:
				emotion="ȭ��";
				break;
			case 4:
				emotion="���";
				break;
			case 5:
				//�̰�� STT Run�� �۵������ϹǷ� Google�� STT����������. �׷��� X�� ���⼭ ���Ƿ� �����ش�.
				emotion="��ü����";
				modelBean.setTextResult("X");
				Server.sendMessageToServer(this.modelBean,false);
				break;
			}
//			System.out.println(" (����������� �޽�����ȣ: " + tempMsgNum+")");

			this.modelBean.setEmotionType(emotionType);
			if(Server.test){
				Server.sendTargetQueue(emotion, true);
			}else{
				Server.sendMessageToServer(this.modelBean,true);
			}
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(!Server.test){
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
}