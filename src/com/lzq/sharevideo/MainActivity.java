package com.lzq.sharevideo;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Context;
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
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lzq.configuration.GlobalParameters;
import com.lzq.mediaproxy.CacheProxy;
import com.lzq.mediaproxy.MediaProxy;
import com.lzq.mediaproxy.MenuProxy;
import com.lzq.sharevideo.R;

public class MainActivity extends Activity  {

	final static String TAG = "MainActivity";
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;


	private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();;

	
	CacheProxy cacheproxy;
	MediaProxy mediaproxy;
	MenuProxy menuproxy;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 
		GlobalParameters.remoteConfig=null;
		setTitleColor(Color.WHITE);
		//start MediaProxy and CacheProxy 
		cacheproxy=new CacheProxy(GlobalParameters.CacheProxyPort);
		mediaproxy=new MediaProxy(GlobalParameters.MediaProxyPort);
		menuproxy=new MenuProxy(GlobalParameters.MenuProxyPort);
		
		
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.pager);
		setContentView(mViewPager);

		final ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
		bar.setDisplayShowTitleEnabled(true);

		// this.setTheme(resid);
		mTabsAdapter = new TabsAdapter(this, mViewPager);
		mTabsAdapter.addTab(bar.newTab().setText("Streaming"),
				StreamingFragment.class, null);
		mTabsAdapter.addTab(bar.newTab().setText("Group"),
				FetchingFragment.class, null);
//		mTabsAdapter.addTab(bar.newTab().setText("Configuration"),
//				StreamingFragment.class, null);

		// mTabsAdapter.addTab(bar.newTab().setText("Simple"),
		// CountingFragment.class, null);
		// mTabsAdapter.addTab(bar.newTab().setText("List"),
		// FragmentPagerSupport.ArrayListFragment.class, null);
		// mTabsAdapter.addTab(bar.newTab().setText("Cursor"),
		// CursorFragment.class, null);

		if (savedInstanceState != null) {
			bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
		}

		
		//System.out.println("Begin my wifi p2p test.");
//		// Indicates a change in the Wi-Fi P2P status.
//		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
//
//		// Indicates a change in the list of available peers.
//		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
//
//		// Indicates the state of Wi-Fi P2P connectivity has changed.
//		intentFilter
//				.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
//
//		// Indicates this device's details have changed.
//		intentFilter
//				.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
//
//		manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
//		mChannel = manager.initialize(this, getMainLooper(), null);
//		Log.i(TAG,"regist receiver");
//		receiver = new WiFiDirectBroadcastReceiver(manager, mChannel, this);
//		registerReceiver(receiver, intentFilter);
		


		/**
		 * connect to another device
		 * 
		 * from the official documents:
		 * 
		 * The WifiP2pManager.ActionListener implemented in this snippet only
		 * notifies you when the initiation succeeds or fails. To listen for
		 * changes in connection state, implement the
		 * WifiP2pManager.ConnectionInfoListener interface. Its
		 * onConnectionInfoAvailable() callback will notify you when the state
		 * of the connection changes. In cases where multiple devices are going
		 * to be connected to a single device (like a game with 3 or more
		 * players, or a chat app), one device will be designated the
		 * "group owner".
		 * 
		 */
		//Log.e(TAG,"try to find other peers... waiting... ");


	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
	}

	/**
	 * This is a helper class that implements the management of tabs and all
	 * details of connecting a ViewPager with associated TabHost. It relies on a
	 * trick. Normally a tab host has a simple API for supplying a View or
	 * Intent that each tab will show. This is not sufficient for switching
	 * between pages. So instead we make the content part of the tab host 0dp
	 * high (it is not shown) and the TabsAdapter supplies its own dummy view to
	 * show as the tab content. It listens to changes in tabs, and takes care of
	 * switch to the correct paged in the ViewPager whenever the selected tab
	 * changes.
	 */
	public static class TabsAdapter extends FragmentPagerAdapter implements
			ActionBar.TabListener, ViewPager.OnPageChangeListener {
		private final Context mContext;
		private final ActionBar mActionBar;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		static final class TabInfo {
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(Class<?> _class, Bundle _args) {
				clss = _class;
				args = _args;
			}
		}

		public TabsAdapter(Activity activity, ViewPager pager) {
			super(activity.getFragmentManager());
			mContext = activity;
			mActionBar = activity.getActionBar();
			mViewPager = pager;
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
			TabInfo info = new TabInfo(clss, args);
			tab.setTag(info);
			tab.setTabListener(this);
			mTabs.add(info);
			mActionBar.addTab(tab);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.clss.getName(),
					info.args);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			mActionBar.setSelectedNavigationItem(position);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			Object tag = tab.getTag();
			for (int i = 0; i < mTabs.size(); i++) {
				if (mTabs.get(i) == tag) {
					mViewPager.setCurrentItem(i);
				}
			}
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
	}



//	@Override
//	public void onPause() {
//		super.onPause();
//		unregisterReceiver(receiver);
//	}



	/**
	 * For interface PeerListListener Fetch the List of Peers
	 */
//	@Override
//	public void onPeersAvailable(WifiP2pDeviceList peerList) {
//		// TODO Auto-generated method stub
//
//		Log.e(TAG, "onPeersAvailable:" + peerList.toString());
//		// Out with the old, in with the new.
//		peers.clear();
//		peers.addAll(peerList.getDeviceList());
//
//		
//		WifiP2pDevice device = peers.get(0);
//
//		WifiP2pConfig config = new WifiP2pConfig();
//		config.deviceAddress = device.deviceAddress;
//		config.wps.setup = WpsInfo.PBC;
//		Log.i(TAG, "try to connect to " + device.toString());
//		manager.connect(mChannel, config, new ActionListener() {
//
//			@Override
//			public void onSuccess() {
//				// WiFiDirectBroadcastReceiver will notify us. Ignore for now.
//				Log.i(TAG, "connect successfully");
//			}
//
//			@Override
//			public void onFailure(int reason) {
//				Log.i(TAG, "connect failed");
//				Toast.makeText(MainActivity.this, "Connect failed. Retry.",
//						Toast.LENGTH_SHORT).show();
//			}
//		});
//		
//		
//		// If an AdapterView is backed by this data, notify it
//		// of the change. For instance, if you have a ListView of available
//		// peers, trigger an update.
//		// ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
//		if (peers.size() == 0) {
//			Log.d(TAG, "No devices found");
//			return;
//		}
//	}

//	@Override
//	public void onConnectionInfoAvailable(final WifiP2pInfo info) {
//		// TODO Auto-generated method stub
//		// InetAddress from WifiP2pInfo struct.
//		InetAddress groupOwnerAddress = info.groupOwnerAddress;
//		FetchingFragment.peerIP=groupOwnerAddress.getHostAddress();
//		Log.d(TAG, "call onConnectionInfoAvailable, groupOwnerAddress= "
//				+ groupOwnerAddress);
//		System.out.println("successfully connect to peers");
//		// After the group negotiation, we can determine the group owner.
//		if (info.groupFormed && info.isGroupOwner) {
//			// Do whatever tasks are specific to the group owner.
//			// One common case is creating a server thread and accepting
//			// incoming connections.
//		} else if (info.groupFormed) {
//			// The other device acts as the client. In this case,
//			// you'll want to create a client thread that connects to the group
//			// owner.
//		}
//	}

}
