package com.softserveinc.edu.lms.webserver;

import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestCommunicator {
	Communicator comm;
	BufferedReader br;
	BufferedWriter bw;
	String str = "Hello";
	Socket s;

	@Before
	public void setUp() throws Exception {

		comm = new Communicator();
		s = new Socket("127.0.0.1", 8091);
		br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		Thread thr = new Thread(comm);
		thr.setDaemon(true);
		thr.start();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		try {
			bw.write(str);
			bw.write("\n\r");
			bw.flush();
			String actual = br.readLine();
			comm.shutdownServer();
			System.out.print(str);
			System.out.print(actual);
			boolean act = false;
			if(str.equals(actual)) {
				act = true;
			}
			assertEquals(true, act);
			//s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
