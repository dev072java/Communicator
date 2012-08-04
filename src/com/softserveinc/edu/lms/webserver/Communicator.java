package com.softserveinc.edu.lms.webserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Communicator implements Runnable {
	/**
	 * serverSoket 
	 */
	private ServerSocket serverSoket;
	/**
	 *  serverThread
	 */
	private Thread serverThread;
	/**
	 * taskQueue
	 */
	BlockingQueue<SocketProcessor> taskQueue = new LinkedBlockingQueue<SocketProcessor>();

	/**
	 * 
	 * @param port - port of web server
	 * @throws IOException
	 */
	public Communicator(int port) throws IOException {
		serverSoket = new ServerSocket(port);
	}
	/**
	 * listen to new clients
	 */
	public void run() {
		serverThread = Thread.currentThread();
		while (true) {
			Socket socket = getNewConn();
			if (serverThread.isInterrupted()) {
				break;
			} else if (socket != null) {
				try {
					final SocketProcessor processor = new SocketProcessor(socket);
					final Thread thread = new Thread(processor);
					thread.setDaemon(true);
					thread.start();
					taskQueue.offer(processor);
				} catch (IOException ignored) {
				}
			}
		}
	}
	/**
	 * 
	 * @return newSocket - socket of new client connection
	 */
	private Socket getNewConn() {
		Socket newSocket = null;
		try {
			newSocket = serverSoket.accept();
		} catch (IOException e) {
			shutdownServer();
		}
		return newSocket;
	}
	
	/**
	 * 
	 * @param client - client's socket
	 * @param text - response text
	 */
	public void sendResponse(SocketProcessor client,String text) {
		client.sendResponse(text);
	}
	
	/**
	 * stop web server work 
	 */
	public synchronized void shutdownServer() {
		for (SocketProcessor socket : taskQueue) {
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
	private class SocketProcessor implements Runnable {
		/**
		 * socket
		 */
		Socket socket;
		/**
		 * bufferReader
		 */
		BufferedReader bufferReader;
		/**
		 * bufferWriter
		 */
		BufferedWriter bufferWritter;
		/**
		 * requestText
		 */
		@SuppressWarnings("unused")
		String requestText = "";
		/**
		 * 
		 * @param socketParam - client socket
		 * @throws IOException
		 */
		SocketProcessor(Socket socketParam) throws IOException {
			socket = socketParam;
			bufferReader = new BufferedReader(new InputStreamReader(
					socket.getInputStream(), "UTF-8"));
			bufferWritter = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream(), "UTF-8"));
		}
		/**
		 * listening to the new client's requests
		 */
		public void run() {
			while (!socket.isClosed()) {
				String line = null;
				try {
					while (bufferReader.readLine() != null) {
						line += bufferReader.readLine();
					}
					setRequestText(line);
					System.out.print(line);
				} catch (IOException e) {
					close();
				}
			}
		}
		/**
		 * 
		 * @param responseText - response text
		 */
		public synchronized void sendResponse(String responseText) {
			try {
				bufferWritter.write(responseText);
				bufferWritter.flush();
			} catch (IOException e) {
				close();
			}
		}
		
		/**
		 * 
		 * @param text - request text
		 */
		public void setRequestText(String text) {
			requestText = text;
		}
		
		/**
		 * close current client connection
		 */
		public synchronized void close() {
			taskQueue.remove(this);
			if (!socket.isClosed()) {
				try {
					socket.close();
				} catch (IOException ignored) {
				}
			}
		}
	}
}

