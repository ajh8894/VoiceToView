package Server;

public class Driver {
	public static void main(String[] args) {
		Server server = new Server();
		QueueAdapter queueAdapter = new QueueAdapter();
		queueAdapter.start();
		server.startWaiting();
	}
}
