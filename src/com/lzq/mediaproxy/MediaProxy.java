package com.lzq.mediaproxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;


/**
 * This MediaProxy is for redirect the original streaming flow and save the video in local buffer
 * @author LZQ
 *
 */
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
		System.out.println("Media Proxy Listening on Port : "+servPort);

	}

	@Override
	public void run() {   
		// TODO Auto-generated method stub
		try {
			servSock = new ServerSocket(servPort);
			while (running) {
				sock=servSock.accept();
				Log.v(TAG, "Media Proxy: new connection in "+sock.toString());
				new DataPipe(sock).startPipe();
				System.out.println("Media Proxy : new connection in");
			} 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stopServ() {

	}
}
