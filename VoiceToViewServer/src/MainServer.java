
public class MainServer {
	public static void main(String[] args) {
		ClientAcceptor clientAcceptor = new ClientAcceptor();
		clientAcceptor.start();
		
		ChunkSender chunkSender = new ChunkSender();
		chunkSender.start();
		
		SVM svm = new SVM();
		svm.start();
		
	}
}
