package main;
import svm.ChunkSender;
import svm.Sorter;


public class MainServer {
	public static void main(String[] args) {
		ClientAcceptor clientAcceptor = new ClientAcceptor();
		clientAcceptor.start();
		
		ChunkSender chunkSender = new ChunkSender();
		chunkSender.start();
		
		Sorter svm = new Sorter();
		svm.start();
	}
}
