package com.softserveinc.edu.lms.webserver;

import java.util.concurrent.LinkedBlockingQueue;
import com.softserveinc.edu.lms.webserver.Communicator.SocketProcessor;

public class TaskQueue implements Runnable {
	LinkedBlockingQueue<SocketProcessor> queue;
	public TaskQueue()
	{
		/**
		 * taskQueue
		 */
		queue = new LinkedBlockingQueue<SocketProcessor>();
	}
	
	public LinkedBlockingQueue<SocketProcessor> getTaskQueue()
	{
		return queue;
	}

	public void run() {
		for(SocketProcessor socket : queue)
		{
			if(socket.isRequestTextLoader)
			{
				socket.isRequestTextLoader = false;
				Handler handler = new Handler();
				Response response = handler.handle (socket);
				socket.sendTestResponse("<html><body><h1>Hello World!!!</h1></body></html>");
				/*try {
					socket.sendResponse("<html><body><h1>Hello World!!!</h1></body></html>");
					socket.bufferWritter.flush();
					System.out.print("Response sent!");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}
		}
	}

}
