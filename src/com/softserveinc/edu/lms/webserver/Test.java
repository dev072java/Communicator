package com.softserveinc.edu.lms.webserver;

import java.io.IOException;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			System.out.println("Communicator started!");
			Communicator comm = new Communicator();
			comm.run();
			//Thread communicatorThread = new Thread(comm);
			//communicatorThread.setDaemon(true);
			//communicatorThread.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.print("done!");
	}

}
