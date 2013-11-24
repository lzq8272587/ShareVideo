package com.lzq.mediaproxy;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.lzq.configuration.GlobalParameters;

public class DataPipe {

	Socket AppToProxy=null;
	Socket ProxyToServ=null;
	PipeThread pipeThdUp=null;
	PipeThread pipeThdDown=null;
	
	public DataPipe(Socket socket)
	{
		AppToProxy=socket;
	}
	
	public void startPipe()
	{
		try {
			ProxyToServ=new Socket(GlobalParameters.MediaServerIP,GlobalParameters.MediaServerPort);
			pipeThdUp=new PipeThread(AppToProxy.getInputStream(), ProxyToServ.getOutputStream());
			pipeThdDown=new PipeThread(ProxyToServ.getInputStream(), AppToProxy.getOutputStream());
			pipeThdUp.setDirection("FromAppToProxy");
			pipeThdDown.setDirection("FromProxyToApp");
			pipeThdUp.start();
			pipeThdDown.start();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
