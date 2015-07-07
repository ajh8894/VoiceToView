import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientAcceptor extends Thread {
	private ServerSocket serverSocket;
	
	public ClientAcceptor(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	@Override
	public void run() {
		super.run();
		try {
			serverSocket = new ServerSocket(Constants.SERVER_PORT);

			while(true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("Connection accepted");
				
				Client client = new Client(clientSocket);
				new Thread(client).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
