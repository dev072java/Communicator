package com.softserveinc.edu.lms.webserver;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestCommunicator {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		try {
			InputStream is;
			OutputStream os;
			BufferedReader br;
			BufferedWriter bw;
			String str = "Hello" + "\r\n" + "Hello" + "\r\n" + "Hello";
			Communicator comm = new Communicator();
			Thread thr = new Thread(comm);
			thr.setDaemon(true);
			thr.start();
			Socket s = new Socket("127.0.0.1",1080);
			is = s.getInputStream();
			os = s.getOutputStream();
			br = new BufferedReader(new InputStreamReader(is));
			bw = new BufferedWriter(new OutputStreamWriter(os));
			bw.flush();
			bw.write(str);
			bw.flush();
			String actual = br.readLine();
			s.close();
			comm.shutdownServer();
			assertEquals(str, actual);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
