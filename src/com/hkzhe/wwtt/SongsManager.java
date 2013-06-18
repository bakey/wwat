package com.hkzhe.wwtt;

import java.io.File;
import org.apache.commons.*;

import java.io.FilenameFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import android.util.Log;
import android.app.Activity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.*;
import org.apache.http.impl.client.*;
import org.apache.http.impl.client.BasicResponseHandler;

public class SongsManager {
	// SDCard Path
	final String MEDIA_PATH = new String("/sdcard/");
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	
	// Constructor
	public SongsManager(){
		
	}
	
	/**
	 * Function to read all mp3 files from sdcard
	 * and store the details in ArrayList
	 * */
	public ArrayList<HashMap<String, String>> getPlayList( Activity context ){
		/*String url = "http://" + context.getString(R.string.remote_host ) + "/list.json";
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpRequest = new HttpGet( url ); 
			String content = httpclient.execute( httpRequest , new BasicResponseHandler() );
		}catch (IOException e) {
	　　       e.printStackTrace();
	　　  }*/
		HashMap<String, String> song = new HashMap<String, String>();
		song.put("songTitle", "两个旋和三个旋");
		song.put("songPath", "http://211.147.15.167/song/23gs.mp3");
		songsList.add( song );
		return songsList;
		/*File home = new File(MEDIA_PATH);

		if (home.listFiles(new FileExtensionFilter()).length > 0) {
			for (File file : home.listFiles(new FileExtensionFilter())) {
				HashMap<String, String> song = new HashMap<String, String>();
				song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
				song.put("songPath", file.getPath());
				
				// Adding each song to SongList
				songsList.add(song);
			}
		}*/
		// return songs list array

	}
	
	/**
	 * Class to filter files which are having .mp3 extension
	 * */
	class FileExtensionFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".mp3") || name.endsWith(".MP3"));
		}
	}
}
