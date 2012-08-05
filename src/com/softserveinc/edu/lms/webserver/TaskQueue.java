package com.softserveinc.edu.lms.webserver;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import com.softserveinc.edu.lms.webserver.Communicator.SocketProcessor;

public class TaskQueue implements Runnable {
	List<SocketProcessor> queue;
	public TaskQueue()
	{
		/**
		 * taskQueue
		 */
		queue = new LinkedList<SocketProcessor>();
	}
	
	public List<SocketProcessor> getTaskQueue()
	{
		return queue;
	}

	public void run() {
		for(int i = 0; i < queue.size(); i++)
		{
			SocketProcessor socket = queue.get(i);
			System.out.println(socket.hashCode() + "  " + socket.isRequestTextLoader);
			if(socket.isRequestTextLoader)
			{
				try {
					socket.sendResponse("<html><body><h1>Hello World!!!</h1></body></html>");
					socket.bufferWritter.flush();
					System.out.print("Response sent!");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
