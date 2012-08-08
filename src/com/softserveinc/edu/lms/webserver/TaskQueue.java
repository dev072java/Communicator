package com.softserveinc.edu.lms.webserver;

import java.util.concurrent.LinkedBlockingQueue;
import com.softserveinc.edu.lms.webserver.Communicator.SocketProcessor;

public class TaskQueue implements Runnable {
	LinkedBlockingQueue<SocketProcessor> queue;

	public TaskQueue() {
		/**
		 * taskQueue
		 */
		queue = new LinkedBlockingQueue<SocketProcessor>();
	}

	public void run() {
		while (true) {
			for (SocketProcessor socket : queue) {
				if (socket.isRequestTextLoader) {
					System.out.println(queue.size());
					socket.isRequestTextLoader = false;
					Handler handler = new Handler();
					@SuppressWarnings("unused")
					Response response = handler.handle(socket);
					socket.sendTestResponse(socket.requestText);
					/*
					 * try { socket.sendResponse(
					 * "<html><body><h1>Hello World!!!</h1></body></html>");
					 * socket.bufferWritter.flush();
					 * System.out.print("Response sent!"); } catch (IOException
					 * e) { // TODO Auto-generated catch block
					 * e.printStackTrace(); }
					 */
				}
			}
		}
	}

}
