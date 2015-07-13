package temp;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import android.util.Log;

import com.swmem.voicetoview.data.Constants;

public class Test extends Thread {
	public Socket socket;
	public ObjectOutputStream oos;
	private BlockingQueue<Model> queue;

	public Test(BlockingQueue<Model> queue) {
		this.queue = queue;

	}

	@Override
	public void run() {
		try {
			socket = new Socket(Constants.SERVER_IP, Constants.SERVER_PORT);
			oos = new ObjectOutputStream(socket.getOutputStream());
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("hwanjong", "fail");
			e.printStackTrace();
		}
		while (true) {
			Model m = null;
			try {
				m = queue.take();
				Log.d("TEST", m.getBuffers().length + "SUCCESS");
				oos.writeObject(m);
				oos.flush();
				Log.d("HwanJong", "메시지 한번 전송");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	/*
	 * @Override public void run() {
	 * 
	 * 
	 * 
	 * }
	 */

}
