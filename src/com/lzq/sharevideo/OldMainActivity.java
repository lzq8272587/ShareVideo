package com.lzq.sharevideo;

import java.io.IOException;

import com.lzq.mediaproxy.MediaProxy;

import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.view.Menu;
import android.widget.MediaController;
import android.widget.VideoView;

public class OldMainActivity extends Activity {
  
	
	private final IntentFilter intentFilter = new IntentFilter();
	
	Channel mChannel;
	WifiP2pManager mManager;
	private boolean isWifiP2pEnabled = false;
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);

		try {
			Process process = Runtime.getRuntime().exec("su");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		MediaProxy mp=new MediaProxy(8888);
		  
//		VideoView vv=(VideoView)findViewById(R.id.MainVideoView);
//		vv.setVideoPath("http://127.0.0.1:8888/test.mp4");
//		vv.setMediaController(new MediaController(this));
//		vv.start();
//		vv.requestFocus();
		
		
		//for Wi-Fi direct
	    //  Indicates a change in the Wi-Fi P2P status.
	    intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

	    // Indicates a change in the list of available peers.
	    intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

	    // Indicates the state of Wi-Fi P2P connectivity has changed.
	    intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

	    // Indicates this device's details have changed.
	    intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
	    
	    
	    mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
	    mChannel = mManager.initialize(this, getMainLooper(), null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	
	public boolean isWifiP2pEnabled() {
		return isWifiP2pEnabled;
	}
	        
	public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
		this.isWifiP2pEnabled = isWifiP2pEnabled;
	}
	
}
