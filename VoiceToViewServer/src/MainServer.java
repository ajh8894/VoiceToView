
public class MainServer {
	public static void main(String[] args) {
		ClientAcceptor clientAcceptor = new ClientAcceptor();
		clientAcceptor.start();
		SVM svm = new SVM();
		svm.start();
		ChunkSender chunkSender = new ChunkSender();
		chunkSender.start();
	}
}
