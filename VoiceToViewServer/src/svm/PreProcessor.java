package svm;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import fft.ComplexDFT;
import fft.TransformDirection;


public class PreProcessor {

   int bytesPerFrame;
   int totalFramesRead = 0;

   public PreProcessor() {
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
      ArrayList<Double> tempDoubleList = new ArrayList<Double>();
      //ByteBuffer(buffers)를 Double Signal로 Converting 
      ShortBuffer sbuf = ByteBuffer.wrap(buffers).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
      audioShorts = new short[sbuf.capacity()];
      sbuf.get(audioShorts);
      System.out.println("원형Signal size : "+audioShorts.length);
      int doubleLength = ((audioShorts.length+1)/perDistance)-8000;//뒷묵음 1초 8000을빼주자
      //앞 묵음제거
      int i=0;
      while((Math.abs(( (double) audioShorts [ i*perDistance ])/ 0x8000))<0.025){
         i++;
      }
      for  (; i < doubleLength  ; i ++)  { 
         tempDoubleList.add(( (double) audioShorts [ i*perDistance ])/ 0x8000 );
      }
      System.out.println("harf rate & 묵음제거 Filter후 size : "+ tempDoubleList.size());
      audioDoubles = new double[tempDoubleList.size()];
      for(int j=0; j<tempDoubleList.size();j++){
         audioDoubles[j] = tempDoubleList.get(j);
      }
      return audioDoubles;
   }
   public double[] extraSet(double[] signals,int divideNum) {
      // TODO Auto-generated method stub
      double[] features=new double[divideNum];
      int totalPower=0;
      int length = signals.length/2;//0~4000주파수까지만 사용하겠음
      
      //8등분하면서 모든값더함.
      int term = (int) Math.ceil(length/(double)divideNum);
      int index=-1;
      for(int i=0;i<length;i++){
         if(i%term==0) {
            index++;
         }
         if(index==0){
            features[index]+=signals[i]*0.8;
         }else{
            features[index]+=signals[i];
         }
      }
      //Total power
      for(int i=0;i<divideNum;i++){
         totalPower+= (int) features[i];
      }
      //Scalling & 상대값으로바꿈 = 정규화
      for(int i=0;i<divideNum;i++){
         features[i] = features[i]/totalPower;
      }
//      System.out.println("fft signal's length = "+length);
      DecimalFormat format = new DecimalFormat("0.#######");
//      System.out.print("Extra training Set : \n");
      for(int i=0; i<divideNum;i++){
         System.out.print((i+1)+":"+format.format(features[i])+" ");
      }
      System.out.println();
      return features;
   }
   
   @SuppressWarnings("deprecation")
   double[] printSampleFilesFeatures(byte[] byteBuffers){
      
      /**
       * 1.  speech signal 추출 및 샘플링
       */
      System.out.println("1.  speech signal 추출 및 샘플링 시작"+new Date());
      double[] signals = getSignalData(byteBuffers,2);
      int sampleNum = signals.length;
      for(int i=0;i<100;i++){
         System.out.print(signals[i]+", ");
      }
      System.out.println();
      double imageSignal[] = new double[signals.length];
//      System.out.println("\n");
//      System.out.println("sampleSignal's length="+signals.length);
      
      
      /**
       * 
       * 2. complexFFT 주파수대역으로 변환
       * ComplexDFT Success!! 
       * 
       * **/
      System.out.println("2. complexFFT 주파수대역으로 변환 시작"+new Date());
      ComplexDFT FFT = new ComplexDFT(signals.length);
      FFT.transform(TransformDirection.Forward, signals, 0, imageSignal, 0, 1);
//      for(int i=0;i<100;i++){
//         System.out.print("["+signals[i]+" + "+imageSignal[i]+"]");
//      }
   
      /**
       * 3. 복소수의 실수 변경 후 positive signal converting
       */
      System.out.println("3. 복소수의 실수 변경 후 positive signal converting 시작"+new Date());
      for(int i=0;i<sampleNum;i++){
         signals[i]= Math.sqrt((signals[i] * signals[i]) + (imageSignal[i] * imageSignal[i]));
      }
      for(int i=0;i<100;i++){
         System.out.print(signals[i]+", ");
      }
      System.out.println();
      
      
      /**
       * 4. 트레이닝 셋 생성 (여기서 무작정 다더하면 안될것같음 - 일단 해보자)
       * 여기서 한가지 방법을 생각했다. 패턴으로 찾는다는것.
       * 방법-전체를 비율화시켰다. 결과- 길이에무관
       */
      System.out.println("4. 트레이닝 셋 생성 시작");
      return extraSet(signals,8);
   }
}