package com.lzq.mediaproxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

public class PipeThread extends Thread {

	final String TAG = "PipeThread";
	InputStream is = null;
	OutputStream os = null;
	String direction = null;

	int buffSize = 1024;

	public PipeThread(InputStream is, OutputStream os) {
		super();
		this.is = is;
		this.os = os;
	}

	public void run() {
		int count = 0;
		byte[] buff = new byte[buffSize];
		try {
			while ((count = is.read(buff)) != -1) {
				os.write(buff, 0, count);
				os.flush();
				if (direction.equals("FromAppToProxy"))
					Log.i(TAG + direction, new String(buff));
				else
					;//Log.e(TAG + direction, new String(buff));
			}
			is.close();
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setDirection(String direct) {
		direction = direct;
	}
} 
