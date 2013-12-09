package com.lzq.mediaproxy;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This CacheProxy is used for share buffered video stream for other devices
 * @author LZQ
 */
public class CacheProxy implements Runnable {

	
	final String TAG="CacheProxy";
	
	Thread CacheThread=null;
	
	ServerSocket servSocket=null;
	
	int servPort=8080;
	
	
	boolean running = true;
	Socket sock = null;
	
	public int getServPort() {
		return servPort;
	}
	
	
	public CacheProxy(int servPort) {
		this.servPort = servPort;

		
		File video=new File("/sdcard/129.mp4");
		System.out.println(video.getName()+" "+video.exists());
		CacheThread = new Thread(this);
		CacheThread.start();

		System.out.println("Cache Proxy Listening on Port : "+servPort);
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub

		try {
			servSocket=new ServerSocket(servPort);
			while(running)
			{
				sock=servSocket.accept();
				new Thread(new VideoPipe(sock)).start();
				System.out.println("Cache Proxy : new connection in");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void stopServ() {

	}
}
