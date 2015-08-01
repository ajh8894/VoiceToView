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
	 * @param perDistance is 샘플을 몇띵할것인가? ex)2를 넣으면 2000샘플이라면 1000샘플이됨
	 * 대신 최대주파수가 줄어들음(줄일때 PerDistance로 줄여버리면 FFT값에 영향을받음)
	 * @return
	 */
	public double[] getSignalData(byte[] buffers,int perDistance){
		short[] audioShorts = null;
		double [] audioDoubles = null;
		ArrayList<Double> tempDoubleList = new ArrayList<>();

		//ByteBuffer(buffers)를 Double Signal로 Converting 
		ShortBuffer sbuf = ByteBuffer.wrap(buffers).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
		audioShorts = new short[sbuf.capacity()];
		sbuf.get(audioShorts);
		System.out.println("원형Signal size : "+audioShorts.length);
		//앞 묵음제거 (얇은부분을 지우고 시작)
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
			System.out.println("[ 묵 음 ] %%%%%%%%%%%전부 묵음들어옴 = STT요청하지않고 'X'만 보냄%%%%%%%%%%"); //i가 인덱스를 초과할경우 
			return null;
		}
		//		System.out.println("================================i값: " +i + ", widSum값: "+widthSum);
		//뒷부분 8000은 1초이므로 묵음으로 간주하고짤라버리자 
		int doubleLength =(audioShorts.length/perDistance)-5000/perDistance; 
		for  (; i < doubleLength; i++)  { 
			tempDoubleList.add(( (double) audioShorts [i*perDistance ])/ 0x8000 );
			//2500을 초과하면 너무 길기때문에 앞에만가지고 감정처리하자.
			if(tempDoubleList.size()>=30000)break; 		    
		}
		//signal을 파일로 저장
		if(Server.FILE_RECORD) fileRecording(tempDoubleList);

		System.out.println("harf rate & 묵음제거 Filter후 size : "+ tempDoubleList.size());
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
		int length = signals.length/2;//0~4000주파수까지만 사용하겠음
		PrintStream out = null;
		try {
			if(Server.FILE_RECORD)
				out = new PrintStream(new FileOutputStream("feature.txt", true));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//8등분하면서 모든값더함.
		//저주파성분줄임 
		//고주파성분 강조
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

		//기계음이입력되면 저주파성분을 나눈 1개만큼 증가시켜서 저주파를 증폭시킴
		if(Server.PLAY_SIGNAL){
			double gap =totalPower/(divideNum-1); 
			features[0]+=gap;
			features[1]+=gap;
			totalPower+=gap*2;
		}

		//Scalling & 상대값으로바꿈
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
		 * 1.  speech signal 추출 및 샘플링
		 */
		//		System.out.println("1.  speech signal 추출 및 샘플링 시작"+new Date());

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
		 * 2. complexFFT 주파수대역으로 변환
		 * ComplexDFT Success!! 
		 * 
		 * **/
		//		System.out.println("2. complexFFT 주파수대역으로 변환 시작"+new Date());
		ComplexDFT FFT = new ComplexDFT(signals.length);
		FFT.transform(TransformDirection.Forward, signals, 0, imageSignal, 0, 1);
		//		for(int i=0;i<100;i++){
		//			System.out.print("["+signals[i]+" + "+imageSignal[i]+"]");
		//		}

		//복소수의 실수 변경 positive signal converting
		//		System.out.println("3. 복소수의 실수 변경 후 positive signal converting 시작"+new Date());
		for(int i=0;i<sampleNum;i++){
			signals[i]= Math.sqrt((signals[i] * signals[i]) + (imageSignal[i] * imageSignal[i]));
		}

		/**
		 * 3. 트레이닝 셋 생성 (여기서 무작정 다더하면 안될것같음 - 일단 해보자)
		 * 여기서 한가지 방법을 생각했다. 패턴으로 찾는다는것.
		 * 방법-전체를 비율화시켰다. 결과- 길이에무관
		 */
		//		System.out.println("4. 트레이닝 셋 생성 시작"+new Date());
		return extraSet(signals,6);

	}
}