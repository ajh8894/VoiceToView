package Server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.swmem.voicetoview.data.Model;

public class QueueAdapter  extends Thread{
	public static BlockingQueue<Model> queue;

	public QueueAdapter() {
		// TODO Auto-generated constructor stub
		queue = new ArrayBlockingQueue<>(1000);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			try {
				Model responseModel = queue.take();
				Server.oosServer.writeObject(responseModel);
				Server.oosServer.flush();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}


}
