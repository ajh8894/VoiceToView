package com.swmem.voicetoview.data;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class Server {
	
	static final int PORT = 8080;
	
	public static void main(String[] args) {
		BlockingQueue<Chunk> senderQueue;
		BlockingQueue<Chunk> receiverQueue;
		
        ServerSocket serverSocket = null;
        Socket socket = null;
        
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();

        }
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new threa for a client
        }
	}
}
