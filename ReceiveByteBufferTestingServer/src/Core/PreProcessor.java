package Core;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;

import SVM.LibSVM.svm_node;
import Server.Server;

import com.swmem.voicetoview.data.Model;

import fft.ComplexDFT;
import fft.TransformDirection;


public class PreProcessor {


	int bytesPerFrame;
	int totalFramesRead = 0;
	int tempMsgNum;
	//	public static String DIRECTORY = "sampleWav/";
	public String DIRECTORY = "eachWav/";
	public String modelNameForMan = "train.model";
	public String modelNameForWomen = "train.model_w";
	public PreProcessor(int tempMsgNum) {
		this.tempMsgNum = tempMsgNum;
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param fileName is filePath
	 * @param perDistance is ������ ����Ұ��ΰ�? ex)2�� ������ 2000�����̶�� 1000�����̵�
	 * ��� �ִ����ļ��� �پ����(���϶� PerDistance�� �ٿ������� FFT���� ����������)
	 * @return
	 */
	public double[] getSignalData(byte[] buffers,int perDistance){
		short[] audioShorts = null;
		double [] audioDoubles = null;
		ArrayList<Double> tempDoubleList = new ArrayList<>();

		//ByteBuffer(buffers)�� Double Signal�� Converting 
		ShortBuffer sbuf = ByteBuffer.wrap(buffers).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
		audioShorts = new short[sbuf.capacity()];
		sbuf.get(audioShorts);
		System.out.println("����Signal size : "+audioShorts.length);
		//�� �������� (�����κ��� ����� ����)
		int widthSum = 0,i=1;
		try {
			while(widthSum<3500){
				if((Math.abs(( (double) audioShorts [ i*perDistance ])/ 0x8000)) < 0.08){
					i++;
				}else{
					widthSum++;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("[ �� �� ] %%%%%%%%%%%���� �������� = STT��û�����ʰ� 'X'�� ����%%%%%%%%%%"); //i�� �ε����� �ʰ��Ұ�� 
			return null;
		}
		//		System.out.println("================================i��: " +i + ", widSum��: "+widthSum);
		//�޺κ� 8000�� 1���̹Ƿ� �������� �����ϰ�©������� 
		int doubleLength =(audioShorts.length/perDistance)-5000/perDistance; 
		for  (; i < doubleLength; i++)  { 
			tempDoubleList.add(( (double) audioShorts [i*perDistance ])/ 0x8000 );
			//2500�� �ʰ��ϸ� �ʹ� ��⶧���� �տ��������� ����ó������.
			if(tempDoubleList.size()>=30000)break; 		    
		}
		//signal�� ���Ϸ� ����
		if(Server.FILE_RECORD) fileRecording(tempDoubleList);

		System.out.println("harf rate & �������� Filter�� size : "+ tempDoubleList.size());
		StdAudio.play(tempDoubleList);
		audioDoubles = new double[tempDoubleList.size()];
		for(int j=0; j<tempDoubleList.size();j++){
			audioDoubles[j] = tempDoubleList.get(j);
		}
		return audioDoubles;
	}

	private void fileRecording(ArrayList<Double> tempDoubleList) {
		// TODO Auto-generated method stub
		PrintStream out = null;
		try {

			out = new PrintStream(new FileOutputStream("signal"+tempMsgNum+".txt"));
			for(double temp : tempDoubleList){
				out.append(temp+", ");
			}
			out.append("\n");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			out.close();
		}
	}
	public svm_node[] extraSet(double[] signals,int divideNum) {
		// TODO Auto-generated method stub
		double[] features=new double[divideNum];
		svm_node[] x = new svm_node[divideNum];
		int totalPower=0;
		int length = signals.length/2;//0~4000���ļ������� ����ϰ���
		PrintStream out = null;
		try {
			if(Server.FILE_RECORD)
				out = new PrintStream(new FileOutputStream("feature.txt", true));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//8����ϸ鼭 ��簪����.
		//�����ļ������� 
		//�����ļ��� ����
		int term = (int) Math.ceil(length/(double)divideNum);
		int index=-1;
		//		int highEmphase = 5;
		for(int i=0;i<length;i++){
			if(i%term==0) {
				index++;
				//				if(index==highEmphase) features[index-1]*=1.2;
			}
			features[index]+=signals[i];
		}
		int lowEmphase = 2;
		for(int i=0;i<lowEmphase;i++){
			if(i==0)features[i]*=1.15;
			else features[i]*=1.12;
		}

		//Total power
		for(int i=0;i<divideNum;i++){
			totalPower+= (int) features[i];
		}

		//��������ԷµǸ� �����ļ����� ���� 1����ŭ �������Ѽ� �����ĸ� ������Ŵ
		if(Server.PLAY_SIGNAL){
			double gap =totalPower/(divideNum-1); 
			features[0]+=gap;
			features[1]+=gap;
			totalPower+=gap*2;
		}

		//Scalling & ��밪���ιٲ�
		for(int i=0;i<divideNum;i++){
			features[i] = features[i]/totalPower;
		}
		//		System.out.println("fft signal's length = "+length);
		DecimalFormat format = new DecimalFormat("0.#######");
		//		System.out.print("Extra training Set : \n");
		for(int i=0; i<divideNum;i++){
			//			System.out.print((i+1)+":"+format.format(features[i])+" ");
			if(Server.FILE_RECORD) out.append((i+1)+":"+format.format(features[i])+" ");
			svm_node node = new svm_node();
			node.index=i+1;
			node.value=features[i];
			x[i]=node;
		}
		System.out.println();
		if(Server.FILE_RECORD){
			out.append("\n");
			out.close();
		}
		return x;
	}

	public svm_node[] printSampleFilesFeatures(Model modelBean){

		/**
		 * 1.  speech signal ���� �� ���ø�
		 */
		//		System.out.println("1.  speech signal ���� �� ���ø� ����"+new Date());

		double[] signals = getSignalData(modelBean.getBuffers(),3);
		if(signals==null || signals.length==0)return null;

		new SttAdapter(modelBean).start();

		int sampleNum = signals.length;
		//		for(int i=0;i<100;i++){
		//			System.out.print(signals[i]+", ");
		//		}
		//		System.out.println();
		double imageSignal[] = new double[signals.length];
		//		System.out.println("\n");
		//		System.out.println("sampleSignal's length="+signals.length);

		/**
		 * 
		 * 2. complexFFT ���ļ��뿪���� ��ȯ
		 * ComplexDFT Success!! 
		 * 
		 * **/
		//		System.out.println("2. complexFFT ���ļ��뿪���� ��ȯ ����"+new Date());
		ComplexDFT FFT = new ComplexDFT(signals.length);
		FFT.transform(TransformDirection.Forward, signals, 0, imageSignal, 0, 1);
		//		for(int i=0;i<100;i++){
		//			System.out.print("["+signals[i]+" + "+imageSignal[i]+"]");
		//		}

		//���Ҽ��� �Ǽ� ���� positive signal converting
		//		System.out.println("3. ���Ҽ��� �Ǽ� ���� �� positive signal converting ����"+new Date());
		for(int i=0;i<sampleNum;i++){
			signals[i]= Math.sqrt((signals[i] * signals[i]) + (imageSignal[i] * imageSignal[i]));
		}

		/**
		 * 3. Ʈ���̴� �� ���� (���⼭ ������ �ٴ��ϸ� �ȵɰͰ��� - �ϴ� �غ���)
		 * ���⼭ �Ѱ��� ����� �����ߴ�. �������� ã�´ٴ°�.
		 * ���-��ü�� ����ȭ���״�. ���- ���̿�����
		 */
		//		System.out.println("4. Ʈ���̴� �� ���� ����"+new Date());
		return extraSet(signals,6);

	}
}