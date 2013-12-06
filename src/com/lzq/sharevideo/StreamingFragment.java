package com.lzq.sharevideo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lzq.configuration.GlobalParameters;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.VideoView;



/**
 * For general streaming.
 * @author LZQ
 *
 */
public class StreamingFragment extends Fragment {

	final String TAG = "StreamingFragment";

	Button start = null;
	EditText address = null;

	String videoPath = null;
	String videoName = null;
	String videoHost= null;
	String videoPort=null;

	String LocalPath=null;
	File Config=null;
	BufferedWriter bw=null;
	HashMap<String,String> CacheMap=null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View v = inflater
				.inflate(R.layout.streaming_fragment, container, false);
		// VideoView vv = (VideoView) (v
		// .findViewById(R.id.StreamingMediaVideoView));
		start = (Button) (v.findViewById(R.id.StartStreamingButton));
		address = (EditText) (v.findViewById(R.id.MediaPathEditText));
		
		LocalPath=getActivity().getCacheDir().getAbsolutePath();
		Config =new File(LocalPath+"/CacheInfo");
		Log.e(TAG,Config.getAbsolutePath());
		if(!Config.exists())
		{
			try {
				Config.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		GlobalParameters.ConfigPath=Config.getAbsolutePath();
		

		
		start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				String[] url=address.getText().toString().split("//|/");	
				//Log.v(TAG, address.getText().toString());
				videoPath=address.getText().toString();
				videoName=videoPath.substring(videoPath.lastIndexOf("/")+1, videoPath.length());
				videoHost=url[1];
				String[] host_and_port=videoHost.split(":");
				if(host_and_port.length==1)
				{
					//default Http 80 port
					videoPort="80";
					videoHost=host_and_port[0];
				}
				else
				{
					videoHost=host_and_port[0];
					videoPort=host_and_port[1];
				}
				Log.v(TAG,"Get host and port. Host= "+videoHost+"  Port= "+videoPort);
				
				//update the MediaServer IP and Port
				GlobalParameters.MediaServerIP=videoHost;
				GlobalParameters.MediaServerPort=Integer.parseInt(videoPort);
				
				GlobalParameters.CurrentMeidaPath=LocalPath+"/"+videoName;
				
//				try {
//				    bw=new BufferedWriter(new FileWriter(Config));
//				    bw.append(videoName+","+LocalPath+"/"+videoName+"\n");
//				    Log.v(TAG,videoName+","+LocalPath+"/"+videoName+"\n");
//				    bw.flush();
//				    bw.close();
//				    
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				 
				//Load local Config File
				HashMap<String,String> ConfigInfo=GlobalParameters.getConfigInfo();
				Log.e(TAG,"load ConfigInfo: "+ConfigInfo.toString());
				if(!ConfigInfo.containsKey(videoName))
				{
					ConfigInfo.put(videoName, LocalPath+"/"+videoName);
					Log.e(TAG,"add to ConfigInfo: "+videoName+"  "+LocalPath+"/"+videoName);
				}
				GlobalParameters.refreshConfigInfo(ConfigInfo);
//				Log.e(TAG,"refresh ConfigInfo at "+GlobalParameters.ConfigPath);
//				Log.e(TAG,ConfigInfo.toString());
				Intent intent=new Intent();
				intent.putExtra("videoPath","http://"+GlobalParameters.MediaProxyIP +":"+GlobalParameters.MediaProxyPort+"/"+videoName);
				intent.setClass(getActivity(), MediaActivity.class);
				startActivity(intent);
			}
			
			
		});

		return v;

	}

}
