package com.lzq.sharevideo;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.lzq.configuration.GlobalParameters;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.AdapterView.OnItemClickListener;



/**
 * Discover devices nearby
 * and
 * Share video to other devices.
 * @author LZQ
 *
 */
public class FetchingFragment extends Fragment implements PeerListListener,
		ConnectionInfoListener {

	final String TAG = "FetchingFragment";

	private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

	private VideoView vv = null;
	private Button fetch = null;
	private Button discover = null;

	public String peerIP = null;

	private boolean shouldShowAlertDialog = false;
	// for WiFi-Direct
	private boolean isWifiP2pEnabled = false;
	private final IntentFilter intentFilter = new IntentFilter();
	private Channel mChannel;
	private WifiP2pManager manager;
	private WiFiDirectBroadcastReceiver receiver;

	private ProgressDialog progressDialog = null;
	private AlertDialog alertdialog = null;

	private ListView peerListView = null;
	private ListView videoListView=null;

	
	private Handler handler=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// Indicates a change in the Wi-Fi P2P status.
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

		// Indicates a change in the list of available peers.
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

		// Indicates the state of Wi-Fi P2P connectivity has changed.
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

		// Indicates this device's details have changed.
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

		manager = (WifiP2pManager) (getActivity()
				.getSystemService(Context.WIFI_P2P_SERVICE));
		mChannel = manager.initialize(getActivity(), getActivity()
				.getMainLooper(), null);
		Log.i(TAG, "regist receiver");
		receiver = new WiFiDirectBroadcastReceiver(manager, mChannel, this);
		getActivity().registerReceiver(receiver, intentFilter);

		
		handler=new Handler();
		/**
		 * This thread is used for refreash the video list infomation from other devices
		 */
		new Thread()
		{
			public void run()
			{
				try {
					while(true)
					{
					sleep(1000);
					if(GlobalParameters.remoteConfig!=null)
					{
						handler.post(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								ArrayList<HashMap<String, String>> videoListArray = new ArrayList();
								
								Set<String> keyName=GlobalParameters.remoteConfig.keySet();
								for (Object name : keyName) {
									HashMap<String, String> map = new HashMap<String, String>();
									map.put("peerName", (String)name);
									map.put("peerInfo",GlobalParameters.remoteConfig.get(name));
									videoListArray.add(map);
								}

								SimpleAdapter adapter = new SimpleAdapter(getActivity(), videoListArray,
										R.layout.peerlist_item,
										new String[] { "peerName", "peerInfo" }, new int[] {
												R.id.PeerNameTextView, R.id.PeerInfoTextView });
								videoListView.setBackgroundColor(Color.BLACK);

								videoListView.setAdapter(adapter);
								videoListView.setBackgroundColor(Color.WHITE);
							}
							
						});
					}
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View v = inflater.inflate(R.layout.fetching_fragment, container, false);
		// vv = (VideoView) (v.findViewById(R.id.FetchingMediaVideoView));
		fetch = (Button) (v.findViewById(R.id.FetchButton));
		discover = (Button) (v.findViewById(R.id.DiscoverButton));
		// fetch.setBackgroundColor(Color.BLACK);
		// discover.setBackgroundColor(Color.BLACK);

		peerListView = (ListView) (v.findViewById(R.id.PeerListView));

		videoListView=(ListView)(v.findViewById(R.id.VideoListView));
		
		fetch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String videopath = "http://" + peerIP + ":"+GlobalParameters.CacheProxyPort+"/test.mp4";
				Log.v(TAG, videopath);
				Intent intent = new Intent();
				intent.putExtra("videoPath", videopath);
				intent.setClass(getActivity(), MediaActivity.class);
				startActivity(intent);
				// vv.setVideoPath(videopath);
				// vv.setMediaController(new MediaController(getActivity()));
				// vv.start();
				// vv.requestFocus();
			}
		});

		discover.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}

				progressDialog = ProgressDialog.show(getActivity(),
						"Discovering Other Peers",
						"Please wait for discovering completed.");
				progressDialog.setCancelable(true);
				discoverPeers();
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

		// After getting the peer list, dismiss the progress dialog and show the
		// peer list
		shouldShowAlertDialog = true;
		// Log.e(TAG, "onPeersAvailable:" + peerList.toString());
		// Out with the old, in with the new.
		peers.clear();
		peers.addAll(peerList.getDeviceList());

		// Get and show the peer list
		CharSequence[] deviceName = new String[peers.size()];
		for (int i = 0; i < peers.size(); i++) {
			deviceName[i] = peers.get(i).deviceName + " "
					+ peers.get(i).deviceAddress + " "
					+ peers.get(i).primaryDeviceType;
		}

		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		if (alertdialog != null && alertdialog.isShowing()) {
			alertdialog.dismiss();
		}

		ArrayList<HashMap<String, String>> peerListArray = new ArrayList();
		for (WifiP2pDevice wd_info : peers) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("peerName", wd_info.deviceName);
			map.put("peerInfo", wd_info.deviceAddress);
			peerListArray.add(map);
		}

		SimpleAdapter adapter = new SimpleAdapter(getActivity(), peerListArray,
				R.layout.peerlist_item,
				new String[] { "peerName", "peerInfo" }, new int[] {
						R.id.PeerNameTextView, R.id.PeerInfoTextView });
		peerListView.setBackgroundColor(Color.BLACK);

		peerListView.setAdapter(adapter);
		peerListView.setBackgroundColor(Color.WHITE);

		peerListView.setOnItemClickListener(new GroupItemOnClickListener());

		// alertdialog = new AlertDialog.Builder(getActivity())
		// .setTitle("Discover Results")
		// .setPositiveButton("OK", null)
		// .setSingleChoiceItems(deviceName, deviceName.length,
		// new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface arg0,
		// int pos) {
		// // TODO Auto-generated method stub
		//

		//
		// }
		// }).show();

		// If an AdapterView is backed by this data, notify it
		// of the change. For instance, if you have a ListView of available
		// peers, trigger an update.
		// ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
		if (peers.size() == 0) {
			Log.d(TAG, "No devices found");
			return;
		}
	}

	
	/**
	 * Called after connection established
	 */
	@Override
	public void onConnectionInfoAvailable(final WifiP2pInfo info) {
		// TODO Auto-generated method stub
		// InetAddress from WifiP2pInfo struct.

		System.out.println("onConnectionInfoAvailable: "
				+ info.groupOwnerAddress.getHostAddress());
		InetAddress groupOwnerAddress = info.groupOwnerAddress;
		peerIP = groupOwnerAddress.getHostAddress();
		try {
			new Thread()
			{
				public void run()
				{
					Socket sock;
					try {
						sock = new Socket(peerIP,GlobalParameters.MenuProxyPort);
						ObjectOutputStream oos=new ObjectOutputStream(sock.getOutputStream());
						oos.writeObject(GlobalParameters.getConfigInfo());
						oos.flush();
						oos.close();
						sock.close();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}.start();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e(TAG, "call onConnectionInfoAvailable, groupOwnerAddress= "
				+ groupOwnerAddress);
		Log.e(TAG, "Now you can fecth the video from peers!");
		// After the group negotiation, we can determine the group owner.
		Toast.makeText(getActivity(),
				"Connect successed. Now you can fecth the video from peers!.",
				Toast.LENGTH_SHORT).show();
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

	/** register the BroadcastReceiver with the intent values to be matched */
	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, "regist receiver");
		receiver = new WiFiDirectBroadcastReceiver(manager, mChannel, this);
		getActivity().registerReceiver(receiver, intentFilter);
	}

	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(receiver);
	}

	public boolean isWifiP2pEnabled() {
		return isWifiP2pEnabled;
	}

	public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
		this.isWifiP2pEnabled = isWifiP2pEnabled;
	}

	private void discoverPeers() {
		/**
		 * discover other devices
		 */
		Log.i(TAG, "discoverPeers");
		manager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

			@Override
			public void onSuccess() {
				Log.i(TAG, "discoverPeers  success");
				Toast.makeText(getActivity(), "Discovery Initiated",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int reasonCode) {
				Log.i(TAG, "discoverPeers  fail");
				Toast.makeText(getActivity(),
						"Discovery Failed : " + reasonCode, Toast.LENGTH_SHORT)
						.show();
				// if (progressDialog != null && progressDialog.isShowing()) {
				// progressDialog.dismiss();
				// }
			}
		});
	}

	
	
	/**
	 * connect to other devices
	 * @param config
	 */
	private void connect(WifiP2pConfig config) {
		manager.connect(mChannel, config, new ActionListener() {

			@Override
			public void onSuccess() {
				// WiFiDirectBroadcastReceiver
				// will notify us. Ignore
				// for now.
				Log.i(TAG, "connect successfully");
				// if connect to other peers
				// successfully, no need to
				// show this dialog again
				shouldShowAlertDialog = false;
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				Toast.makeText(getActivity(),
						"Connect successed. Fetch video.", Toast.LENGTH_SHORT)
						.show();
				
				//tell peers your video list
				
			}

			@Override
			public void onFailure(int reason) {
				Log.i(TAG, "connect failed");
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				Toast.makeText(getActivity(), "Connect failed. Retry.",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	
	/**
	 * Called when try to connect a certain device after discovering
	 * @author LZQ
	 *
	 */
	class GroupItemOnClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg) {
			// TODO Auto-generated method stub
			Log.v(TAG, pos + " " + arg);

			WifiP2pDevice device = peers.get(pos);
			final WifiP2pConfig config = new WifiP2pConfig();
			config.deviceAddress = device.deviceAddress;
			config.wps.setup = WpsInfo.PBC;
			Log.i(TAG, "try to connect to " + device.toString());

			progressDialog = ProgressDialog.show(getActivity(),
					"Connecting Other Peers",
					"Please wait for connecting completed.");

			alertdialog = new AlertDialog.Builder(getActivity())
					.setTitle("Discover Results").setMessage("Peer: "+device.deviceName)
					.setPositiveButton("Connect",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									connect(config);
								}
							}).setNegativeButton("Cancel", null).show();

		}

	}
}
