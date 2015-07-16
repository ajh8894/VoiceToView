package main;


public class MainServer {
	public static void main(String[] args) {
		SVMServerConnector svmServerConnector = new SVMServerConnector();
		svmServerConnector.start();
		
		ClientAcceptor clientAcceptor = new ClientAcceptor();
		clientAcceptor.start();
		
		IndividualSorter individualSorter = new IndividualSorter();
		individualSorter.start();
	}
}
