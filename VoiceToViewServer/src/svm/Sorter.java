package svm;

import com.swmem.voicetoview.data.Chunk;
import com.swmem.voicetoview.data.Signal;

import data.ServerData;

public class Sorter extends Thread {
	@Override
	public void run() {
		super.run();
		while (true) {
			try {
				Object obj = ServerData.receiverQueue.take();
				if(obj instanceof Signal) {
					Signal signal = (Signal) obj;
					PreProcessor preProcessor = new PreProcessor();
					preProcessor.printSampleFilesFeatures(signal.getPcm());
				} else {
					ServerData.senderQueue.put((Chunk) obj);
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
