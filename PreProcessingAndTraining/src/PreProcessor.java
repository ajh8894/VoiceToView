import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.text.DecimalFormat;
import java.util.logging.FileHandler;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;


import SVM.svm_toy;
import SVM.LibSVM.svm_model;

import fft.ComplexDFT;
import fft.TransformDirection;


public class PreProcessor {

	int bytesPerFrame;
	AudioInputStream audioInputStream;
	int totalFramesRead = 0;
	public static String DIRECTORY = "sampleWav/";
//	public static String DIRECTORY = "testWav/";
//	public static String DIRECTORY = "eachWav/";

	public PreProcessor() {
		// TODO Auto-generated constructor stub

	}
	/**
	 * @param fileName is filePath
	 * @param perDistance is 샘플을 몇띵할것인가? ex)2를 넣으면 2000샘플이라면 1000샘플이됨
	 * 줄일때 PerDistance로 줄여버리면 FFT값에 영향을받음
	 * @return
	 */
	public double[] getSignalData(String fileName,int perDistance){
		File fileIn = new File(fileName);
		byte buffers[] = null ;
		short[] audioShorts = null;
		double [] audioDoubles = null;

		try {
			audioInputStream = AudioSystem.getAudioInputStream(fileIn);
			bytesPerFrame = audioInputStream.getFormat().getFrameSize();
			int length = audioInputStream.available();
//			System.out.println("length="+length);
//			System.out.println("FrameRate : "+audioInputStream.getFormat().getFrameRate());
			buffers = new byte[length];
			audioInputStream.read(buffers);

			//ByteBuffer(buffers)를 Double Signal로 Converting 
			ShortBuffer sbuf = ByteBuffer.wrap(buffers).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
			audioShorts = new short[sbuf.capacity()];
			sbuf.get(audioShorts);
			int doubleLength = (audioShorts.length)/perDistance;
			audioDoubles =  new  double [ doubleLength];
			for  (int i =  0 ; i < doubleLength  ; i ++)  { 
				audioDoubles [ i ]  =  ( (double) audioShorts [ i*perDistance ])/ 0x8000 ; 
			}
			audioInputStream.close();
		} catch (UnsupportedAudioFileException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return audioDoubles;
	}
	public double[] extraSet(double[] signals,int divideNum) {
		// TODO Auto-generated method stub
		double[] features=new double[divideNum];
		int totalPower=0;
		
		//Cut off 절반!
		int length = signals.length/2;//0~4000주파수까지만 사용하겠음
		
		int term = (int) Math.ceil(length/(double)divideNum);
		int index=-1;
		for(int i=0;i<length;i++){
			if(i%term==0) {
				index++;
			}
			features[index]+=signals[i];
		}
		int lowEmphase = 1;
		for(int i=0;i<lowEmphase;i++){
			features[i]*=0.9;
		}
	
		//Total power
		for(int i=0;i<divideNum;i++){
			totalPower+= (int) features[i];
		}
		//Scalling & 상대값으로바꿈 &저주파성분줄임
		for(int i=0;i<divideNum;i++){
			features[i] = features[i]/totalPower;
		}
//		System.out.println("fft signal's length = "+length);
		DecimalFormat format = new DecimalFormat("0.#######");
//		System.out.print("Extra training Set : \n");
		for(int i=0; i<divideNum;i++){
			System.out.print((i+1)+":"+format.format(features[i])+" ");
		}
		System.out.println();
		return features;
	}
	
	void printSampleFilesFeatures(String fileName){
		
		//Label 자동분류
		char labelChar = fileName.split("\\.")[0].charAt(5);
		switch(labelChar){
		case 'T':
			System.out.print("1 ");
			break;
		case 'N':
			System.out.print("2 ");
			break;
		case 'W':
			System.out.print("3 ");
			break;
		case 'F':
			System.out.print("4 ");
			break;
		default:
			return;
			
		}
		
		/**
		 * 1.  speech signal 추출 및 샘플링
		 */
		
		double[] signals = getSignalData(DIRECTORY+fileName,3);
		int sampleNum = signals.length;
//		System.out.println("sampleNum = " +sampleNum);
//		for(int i=0;i<100;i++){
//			System.out.print(signals[i]+", ");
//		}
//		System.out.println("\n");
//		System.out.println("sampleSignal's length="+signals.length);
		
		
		/**
		 * 
		 * 2. complexFFT 주파수대역으로 변환
		 * ComplexDFT Success!! 
		 * 
		 * **/
		ComplexDFT FFT = new ComplexDFT(signals.length);
		double imageSignal[] = new double[signals.length];
		FFT.transform(TransformDirection.Forward, signals, 0, imageSignal, 0, 1);
//		for(int i=0;i<100;i++){
//			System.out.print("["+signals[i]+" + "+imageSignal[i]+"]");
//		}
		/**
		 * 3. 복소수의 실수 변경 후 positive signal converting
		 */
		for(int i=0;i<sampleNum;i++){
			signals[i]= Math.sqrt((signals[i] * signals[i]) + (imageSignal[i] * imageSignal[i]));
		}
//		for(int i=0;i<100;i++){
//			System.out.print(signals[i]+", ");
//		}
//		System.out.println("\nFFT변환 직후 SIZE = "+signals.length);
		
		/**
		 * 4. 트레이닝 셋 생성 (여기서 무작정 다더하면 안될것같음 - 일단 해보자)
		 * 여기서 한가지 방법을 생각했다. 패턴으로 찾는다는것.
		 * 방법-전체를 비율화시켰다. 결과- 길이에무관
		 */
		extraSet(signals,6);
	}
	public static void main(String[] args) {
		File[] files = new File(DIRECTORY).listFiles();
		PreProcessor preProcessor = new PreProcessor();
		for(File file : files){
			preProcessor.printSampleFilesFeatures(file.getName());
		}
		
		/**
		 * SVM테스트
		 */
//		svm_toy toy = new svm_toy();
//		toy.init();
//		System.out.println("실행됨");
	}
}