package com.lzq.configuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

public class GlobalParameters {

	public static String MediaServerIP = "192.168.137.1";
	public static int MediaServerPort = 80;// Http

	public static String MediaProxyIP = "127.0.0.1";
	public static int MediaProxyPort = 8888;// Http

	public static String CacheProxyIP = "127.0.0.1";
	public static int CacheProxyPort = 8889;// Http

	
	public static int MenuProxyPort=8890;
	
	public static String ConfigPath = null;
	public static String CurrentMeidaPath = null;

	//the video info from other devices
	public static HashMap<String,String> remoteConfig=null;
	
	//show all above info
	public static void showInfo() {
		System.out.println("MediaServerIP=" + MediaServerIP);
		System.out.println("MediaServerPort=" + MediaServerPort);
		System.out.println("MediaProxyIP=" + MediaProxyIP);
		System.out.println("MediaProxyPort=" + MediaProxyPort);
		System.out.println("CacheProxyIP=" + CacheProxyIP);
		System.out.println("CacheProxyPort=" + CacheProxyPort);
	}

	
	/**
	 * get the local configuration file, including the video buffer and the path 
	 * @return
	 */
	public static HashMap<String, String> getConfigInfo() {
		HashMap<String, String> ConfigInfo = new HashMap<String, String>();
		File ConfigFile = new File(ConfigPath);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(ConfigFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!ConfigFile.exists()) {
			try {
				ConfigFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return ConfigInfo;
		} else {
			String line = null;
			try {
				while ((line = br.readLine()) != null) {
					String VideoName = line.split(",")[0];
					String VideoPath = line.split(",")[1];
					ConfigInfo.put(VideoName, VideoPath);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ConfigInfo;
	}

	
	/**
	 * refresh the configuration info
	 * @param ConfigInfo
	 */
	public static void refreshConfigInfo(HashMap<String, String> ConfigInfo) {
		File ConfigFile = new File(ConfigPath);
		BufferedWriter bw = null;


		try {
			if (ConfigFile.exists()) {
				ConfigFile.delete();
			}
			ConfigFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			bw = new BufferedWriter(new FileWriter(ConfigFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Set<String> keySet = ConfigInfo.keySet();
		for (Object key : keySet) {
			try {
				bw.append((String)key + "," + ConfigInfo.get(key)+"\n");
				System.out.println("append to Config: " + (String) key + ","
						+ ConfigInfo.get(key));
				bw.flush();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
