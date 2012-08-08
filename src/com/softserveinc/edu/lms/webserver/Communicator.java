package com.softserveinc.edu.lms.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Communicator implements Runnable {

	/**
	 * configurator
	 */
	Configurator configurator;
	/**
	 * serverSoket
	 */
	private ServerSocket serverSoket;

	/**
	 * serverThread
	 */
	private Thread serverThread;
	/**
	 * taskQueue
	 */
	private TaskQueue taskQueue;
	private Thread queueThread;

	/**
	 * 
	 * @param port
	 *            - port of web server
	 * @throws IOException
	 */
	public Communicator() throws IOException {
		Configurator configurator = new Configurator();
		serverSoket = new ServerSocket(configurator.getPort());
		taskQueue = new TaskQueue();
		queueThread = new Thread(taskQueue);
		queueThread.setDaemon(true);
		queueThread.start();
		System.out.println("Task queue started!");
	}
	/**
	 * listen to new clients
	 */
	public void run() {
		serverThread = Thread.currentThread();
		Socket socket;
		while (true) {
			try {
				socket = serverSoket.accept();
				if (serverThread.isInterrupted()) {
					break;
				} else if (socket != null) {
					try {
						final SocketProcessor processor = new SocketProcessor(
								socket);
						final Thread thread = new Thread(processor);
						thread.setDaemon(true);
						thread.start();
						taskQueue.queue.add(processor);
						System.out.println(taskQueue.queue.size());
					} catch (IOException ignored) {
					}
				}
			} catch (IOException e) {
				shutdownServer();
			}

		}
	}

	/**
	 * stop web server work
	 */
	public synchronized void shutdownServer() {
		for (SocketProcessor socket : taskQueue.queue) {
			socket.close();
		}
		if (!serverSoket.isClosed()) {
			try {
				serverSoket.close();
			} catch (IOException ignored) {
			}
		}
	}

	/**
	 * 
	 * @author Oleh Halushchak
	 * 
	 */
	public class SocketProcessor implements Runnable {
		/**
		 * true if all text of request is loader to server
		 */
		boolean isRequestTextLoader = false;
		/**
		 * socket -socket connection object
		 */
		Socket socket;
		/**
		 * inputStream
		 */
		InputStream inputStream;
		/**
		 * outputStream
		 */
		OutputStream outputStream;
		/**
		 * requestText
		 */
		String requestText = "";

		/**
		 * 
		 * @param socketParam
		 *            - client socket
		 * @throws IOException
		 */
		SocketProcessor(Socket socketParam) throws IOException {
			socket = socketParam;
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
		}

		/**
		 * listening to the new client's requests
		 */
		public void run() {
			String temp = "";
			while (!socket.isClosed()) {
				String line = "";
				BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream));
				try {
					while (true) {
						temp = bufferReader.readLine();
						if (temp == null || temp.trim().length() == 0) {
							break;
						}
						line += temp + "\n";
					}
					isRequestTextLoader = true;
					if (line != "") {
						setRequestText(line);
						System.out.print(line + "\n" + "Request resived!\n");
						line = "";
					}
					sendTestResponse("<html><body><h1>Hello World!!!</h1></body></html>");
				} catch (IOException e) {
					close();
				}
			}
		}

		/**
		 * 
		 * @param responseText
		 *            - response text
		 */
		public synchronized void sendTestResponse(String responseText) {
			/*String response = "HTTP/1.1 200 OK\r\n"
					+ "Server: YarServer/2012-07-08\r\n"
					+ "Content-Type: text/html\r\n" + "Content-Length: "
					+ responseText.length() + "\r\n"
					+ "Connection: close\r\n\r\n";
			String resault = response + responseText;
			try {
				outputStream.write(resault.getBytes());
				outputStream.flush();
			} catch (IOException e) {
				close();
			}*/
			try {
				String str = "Hello";
				outputStream.write(str.getBytes());
				outputStream.flush();
			} catch(IOException e)
			{
				close();
			}
		}
		/**
		 * 
		 * @param response - object with response info
		 */
		public synchronized void sendResponse(Response response)
		{
			try {
				outputStream.write(response.getBytes());
				outputStream.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		/**
		 * 
		 * @param text
		 *            - request text
		 */
		public void setRequestText(String text) {
			requestText = text;
		}

		/**
		 * close current client connection
		 */
		public synchronized void close() {
			taskQueue.queue.remove(this);
			if (!socket.isClosed()) {
				try {
					socket.close();
				} catch (IOException ignored) {
				}
			}
		}
	}
}
