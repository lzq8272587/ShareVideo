package com.lzq.sharevideo;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class FetchingFragment extends Fragment implements PeerListListener,
		ConnectionInfoListener {

	
	final String TAG="FetchingFragment";
	
	Channel mChannel;
	private WifiP2pManager manager;
	WiFiDirectBroadcastReceiver receiver;

	private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();;
	
	VideoView vv=null;
	
	public static String peerIP=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View v = inflater.inflate(R.layout.fetching_fragment, container, false);
	    vv = (VideoView) (v.findViewById(R.id.FetchingMediaVideoView));
		Button start = (Button) (v.findViewById(R.id.FetchButton));

		start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String videopath="http://"+peerIP+":8080/test.mp4";
				Log.v(TAG,videopath);
				vv.setVideoPath(videopath);
				vv.setMediaController(new MediaController(getActivity()));
				vv.start();
				vv.requestFocus();
			}
		});
		return v;

	}

	/**
	 * For interface PeerListListener Fetch the List of Peers
	 */
	@Override
	public void onPeersAvailable(WifiP2pDeviceList peerList) {
		// TODO Auto-generated method stub

		Log.e(TAG, "onPeersAvailable:" + peerList.toString());
		// Out with the old, in with the new.
		peers.clear();
		peers.addAll(peerList.getDeviceList());

		
		WifiP2pDevice device = peers.get(0);

		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = device.deviceAddress;
		config.wps.setup = WpsInfo.PBC;
		Log.i(TAG, "try to connect to " + device.toString());
		manager.connect(mChannel, config, new ActionListener() {

			@Override
			public void onSuccess() {
				// WiFiDirectBroadcastReceiver will notify us. Ignore for now.
				Log.i(TAG, "connect successfully");
			}

			@Override
			public void onFailure(int reason) {
				Log.i(TAG, "connect failed");
				Toast.makeText(getActivity(), "Connect failed. Retry.",
						Toast.LENGTH_SHORT).show();
			}
		});
		
		
		// If an AdapterView is backed by this data, notify it
		// of the change. For instance, if you have a ListView of available
		// peers, trigger an update.
		// ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
		if (peers.size() == 0) {
			Log.d(TAG, "No devices found");
			return;
		}
	}

	@Override
	public void onConnectionInfoAvailable(final WifiP2pInfo info) {
		// TODO Auto-generated method stub
		// InetAddress from WifiP2pInfo struct.
		InetAddress groupOwnerAddress = info.groupOwnerAddress;
		peerIP=groupOwnerAddress.getHostAddress();
		Log.e(TAG, "call onConnectionInfoAvailable, groupOwnerAddress= "
				+ groupOwnerAddress);
		Log.e(TAG, "Now you can fecth the video from peers!");
		// After the group negotiation, we can determine the group owner.
		if (info.groupFormed && info.isGroupOwner) {
			// Do whatever tasks are specific to the group owner.
			// One common case is creating a server thread and accepting
			// incoming connections.
		} else if (info.groupFormed) {
			// The other device acts as the client. In this case,
			// you'll want to create a client thread that connects to the group
			// owner.
		}
	}


}
