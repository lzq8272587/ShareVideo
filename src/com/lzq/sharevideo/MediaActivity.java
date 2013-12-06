package com.lzq.sharevideo;

import com.lzq.configuration.GlobalParameters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class MediaActivity extends Activity {
	final String TAG = "MediaActivity";

	Intent startIntent = null;
	ShareVideoView vv = null;
	String videoPath = null;
	String videoName = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		startIntent = getIntent();

		// get ride of head info(useless!)
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.media_activity);
		vv = (ShareVideoView) findViewById(R.id.StreamingVideoView);
		vv.setMediaController(new MediaController(this));

		videoPath = startIntent.getStringExtra("videoPath");

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) vv
				.getLayoutParams();
		params.width = metrics.widthPixels;
		params.height = metrics.heightPixels;
		params.leftMargin = 0;
		vv.setLayoutParams(params);

		vv.setVideoPath(videoPath);
		// vv.setVideoPath("http://192.168.2.104/test.mp4");
		vv.setHorizontalScrollBarEnabled(true);
		vv.setVerticalScrollBarEnabled(true);
		GlobalParameters.showInfo();
		Log.v(TAG + " start video", videoPath);
		vv.start();
		vv.requestFocus();

	}

}
