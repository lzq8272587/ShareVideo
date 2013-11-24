package com.lzq.mediaproxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;

public class MediaProxy implements Runnable {

	final String TAG="MediaProxy";
	Thread LocalServer = null;
	ServerSocket servSock = null;
	int servPort = 80;

	boolean running = true;
	Socket sock = null;

	public int getServPort() {
		return servPort;
	}

	public MediaProxy(int servPort) {
		this.servPort = servPort;

		LocalServer = new Thread(this);
		LocalServer.start();

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			servSock = new ServerSocket(servPort);
			while (running) {
				sock=servSock.accept();
				new DataPipe(sock).startPipe();
				Log.v(TAG, "new connection in");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stopServ() {

	}

}
