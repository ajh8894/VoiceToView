package main;

import task.ClientAcceptor;
import task.IndividualSorter;
import task.SVMserverConnector;


public class ServerOperator {
	public static void main(String[] args) {
		SVMserverConnector svmServerConnector = new SVMserverConnector();
		svmServerConnector.start();
		
		ClientAcceptor clientAcceptor = new ClientAcceptor();
		clientAcceptor.start();
		
		IndividualSorter individualSorter = new IndividualSorter();
		individualSorter.start();
	}
}
