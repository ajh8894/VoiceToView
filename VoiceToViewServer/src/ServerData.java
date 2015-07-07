import java.net.ServerSocket;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.swmem.voicetoview.data.Chunk;


public class ServerData {
	public static ServerSocket serverSocket;
	public static HashMap<String, Client> clients = new HashMap<String, Client>();
	public static BlockingQueue<Chunk> receiverQueue = new ArrayBlockingQueue<>(1024);
	public static BlockingQueue<Chunk> senderQueue = new ArrayBlockingQueue<>(1024);
}