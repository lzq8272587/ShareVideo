package com.lzq.mediaproxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import android.util.Log;

public class VideoPipe implements Runnable{

	
	final String TAG="VideoPipe";
	String videoPath="/sdcard/test.mp4";
	Socket socket=null;
	
	File video=null;
	FileInputStream fis=null;
	OutputStream os=null;
	public VideoPipe(Socket s)
	
	{
		socket=s;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		byte[] buff=new byte[1024];
		int count=0;
		video=new File(videoPath);
		try {
			fis=new FileInputStream(video);
			os=socket.getOutputStream();
			
			//read http connection
			socket.getInputStream().read(buff);
			Log.v(TAG,new String(buff));
			System.out.println(new String(buff));
			while((count=fis.read(buff))!=-1)
			{
				os.write(buff, 0, count);
				os.flush();
			}
			fis.close();
			os.close();
			socket.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
