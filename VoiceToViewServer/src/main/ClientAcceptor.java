package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import client.Client;
import data.Constants;

public class ClientAcceptor extends Thread {
	private ServerSocket serverSocket;
	
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(Constants.SERVER_PORT);
			
			while (true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("client connection accepted");
				Client client = new Client(clientSocket);
				new Thread(client).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
