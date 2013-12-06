package com.lzq.mediaproxy;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.HashMap;

import com.lzq.configuration.GlobalParameters;


/**
 * To reveive the video list info(which video has been buffered and where it is)
 * @author LZQ
 *
 */
public class MenuProxy extends CacheProxy {

	public MenuProxy(int servPort) {
		super(servPort);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		try {
			servSocket = new ServerSocket(servPort);
			while (running) {
				sock = servSocket.accept();
				ObjectInputStream ois = new ObjectInputStream(
						sock.getInputStream());
				try {
					HashMap<String,String> videoList=(HashMap<String,String>)ois.readObject();
					GlobalParameters.remoteConfig=videoList;
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
