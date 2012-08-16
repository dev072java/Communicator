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
                    RequestBuilder rb = new RequestBuilder(socket.requestText);
                    Request request = rb.getRequest();
                    Response response = handler.handle(request);
                    socket.sendResponse(response);
					
					 //socket.sendTestResponse("<HTML><HEAD><META HTTP-EQUIV=\"REFRESH\" CONTENT=\"1; URL=http://www.softtime.ru\"></HEAD><BODY></BODY></HTML>");
					 socket.close();
					 System.out.print("Response sent!");
					 
				}
			}
		}
	}

}
