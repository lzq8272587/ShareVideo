package com.lzq.mediaproxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.lzq.configuration.GlobalParameters;

import android.util.Log;

public class PipeThread extends Thread {

	final String TAG = "PipeThread";
	InputStream is = null;
	OutputStream os = null;
	String direction = null;

	int buffSize = 1024;

	
	File MediaFile=null;
	FileOutputStream fos=null;
	
	public PipeThread(InputStream is, OutputStream os) {
		super();
		this.is = is;
		this.os = os;
		MediaFile=new File(GlobalParameters.CurrentMeidaPath);
		if(MediaFile.exists())
		{
			MediaFile.delete();
		}
		try {
			MediaFile.createNewFile();
			fos=new FileOutputStream(MediaFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		int count = 0;
		byte[] buff = new byte[buffSize];
		try {
			while ((count = is.read(buff)) != -1) {
				os.write(buff, 0, count);
				os.flush();
				if (direction.equals("FromProxyToApp"))
				{
//					Log.i(TAG + direction, new String(buff));
					fos.write(buff, 0, count);
					fos.flush();
				}
				
				else
					;//Log.e(TAG + direction, new String(buff));
			}
			is.close();
			os.close();
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setDirection(String direct) {
		direction = direct;
	}
} 
