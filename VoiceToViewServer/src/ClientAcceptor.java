import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientAcceptor extends Thread {
	@Override
	public void run() {
		super.run();

		try {
			ServerData.serverSocket = new ServerSocket(Constants.SERVER_PORT);
			
			while(true) {
				Socket clientSocket = ServerData.serverSocket.accept();
				System.out.println("Connection accepted");

				Client client = new Client(clientSocket);
				new Thread(client).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
