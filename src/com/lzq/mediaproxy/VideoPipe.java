package com.lzq.mediaproxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Set;

import com.lzq.configuration.GlobalParameters;

import android.util.Log;

public class VideoPipe implements Runnable{

	
	final String TAG="VideoPipe";
	String videoPath="/sdcard/129.mp4";
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

		try {

			os=socket.getOutputStream();
			
			//read http connection
			socket.getInputStream().read(buff);
			String httpHead=new String(buff);
			Log.v(TAG,httpHead);
			System.out.println(new String(buff));
	
			Set<String> keyName=GlobalParameters.getConfigInfo().keySet();
			for(String s:keyName)
			{
				if(httpHead.contains(s)&&s.length()>0)
				{
					videoPath=GlobalParameters.getConfigInfo().get(s);
				}
			}
			
			
			
			video=new File(videoPath);
			fis=new FileInputStream(video);
			
			
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
