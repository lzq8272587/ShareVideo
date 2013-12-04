package com.lzq.sharevideo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

public class MediaActivity extends Activity {
final String TAG="MediaActivity";
	
	Intent startIntent=null;
	VideoView vv=null;
	String videoPath=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		startIntent=getIntent();
		vv=new VideoView(this);
		vv.setMediaController(new MediaController(this));
		setContentView(vv);
		
		videoPath=startIntent.getStringExtra("videoPath");
		
		Log.v(TAG,videoPath);
		vv.setVideoPath(videoPath);
		vv.start();
		vv.requestFocus();
		
		
		
	}

	

}
