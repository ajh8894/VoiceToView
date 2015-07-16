package data;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import client.Client;

import com.swmem.voicetoview.data.Model;


public class ServerData {
	public static HashMap<String, Client> clients = new HashMap<String, Client>();
	public static BlockingQueue<Model> receiverQueue = new ArrayBlockingQueue<Model>(2024);
	public static BlockingQueue<Model> senderQueue = new PriorityBlockingQueue<Model>(1024);
}