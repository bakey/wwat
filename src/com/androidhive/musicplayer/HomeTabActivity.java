package com.androidhive.musicplayer;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

import com.androidhive.musicplayer.common.*;
import com.androidhive.musicplayer.views.Global;
public class HomeTabActivity extends TabActivity {
	final String TAG = "HomeTab";
	final String API_AUDIO_LIST_PATH="/wawatingting/get_list.php";
	GridView mHometabGridView;
	ImageView mImageView;
	LinearLayout  mLoadingLayout;
	private TabHost    mTopTabHost;
	private  MediaPlayer m_player;
	protected static final int IMAGE_LOADED = 0x101;  
	View mLoading;
	String mAudioTitles[];
	String mAudioClassify[];
	String mAudioUrl[];
	
    Handler mHandler = new Handler() {  
         public void handleMessage(Message msg) {   
              switch (msg.what) {   
                   case HomeTabActivity.IMAGE_LOADED:  
                	    showMainContent();
                        //myBounceView.invalidate();  
                        break; 
                    default:
                    	break;
              }   
              super.handleMessage(msg);   
         }   
    }; 
	private void showLoading(boolean bShow){
		if(mLoading == null) {
			return;
		}
		mLoading.setVisibility(bShow ? View.VISIBLE : View.GONE);
	}
	private void getAudioList() {
		String Url = "http://" + getString(R.string.remote_host) + API_AUDIO_LIST_PATH ;
		try {
			URL url = new URL( Url );
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			String msg = Utils.readStream(con.getInputStream());
			if ( msg != "" ) {
				Log.d( TAG , "get msg = " + msg );
			}
			JSONObject json_obj = new JSONObject( msg );
			JSONArray alist;
			try {
				alist = json_obj.getJSONArray("AudioList");
			}catch( JSONException e ) {
				Log.e(TAG , "get [AudioList] failed");
				return ;
			}
			mAudioTitles = new String[ alist.length() ];
			mAudioClassify = new String[ alist.length() ];
			mAudioUrl = new String[ alist.length() ];
			for( int i = 0 ; i < alist.length() ; i ++ ) {
				JSONObject obj = alist.getJSONObject( i );
				mAudioTitles[ i ] = obj.getString("title");
				mAudioClassify[ i ] = obj.getString("classify");
				mAudioUrl[ i ] = obj.getString("Url");
			}
		} catch (Exception e) {
			  e.printStackTrace();
		}
	}
	private void InitList()
	{
		boolean isNetworkAvailable = Utils.isNetworkAvailable( this );
		if ( !isNetworkAvailable ) {
			Toast.makeText( this , 
						"网络不连通，请检查你的网络设置", Toast.LENGTH_LONG ).show();
			return ;
		}
		getAudioList();
		Message msg = Message.obtain();
		msg.what = HomeTabActivity.IMAGE_LOADED;
		mHandler.sendMessage( msg ); //发送消息
	}
	private void showMainContent() {
		((ViewGroup) mLoadingLayout.getParent()).removeView( mLoadingLayout );
		m_player = new MediaPlayer();
		final ImageView playingView = Global.tabView; //null;
	
		if ( playingView != null ) {
			Log.d( TAG , "get animation imageview success");
		}else {
			Log.d( TAG , "get animation imageview failed");
		}
		
		mHometabGridView.setVisibility( View.VISIBLE );
		mImageView.setVisibility( View.VISIBLE );
				
		mHometabGridView.setAdapter(new HometabImageAdapter(this, mAudioTitles , mAudioClassify));
		mHometabGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				m_player.reset();
				playingView.setImageResource( R.drawable.playing );
				AnimationDrawable animation = (AnimationDrawable) playingView.getDrawable();
				if ( animation != null ) {
					animation.start();
					Log.d( TAG , "get animation success");
				}else {
					Log.d( TAG , "get animation failed ");
				}
				String songPath = mAudioUrl[ position ];
				Log.d( TAG , "get url = " + songPath );
				//playingView.setBackground( getResources().getDrawable(R.drawable.btn_next) );
				try {
					m_player.setDataSource( songPath );
					m_player.prepare();
	        		m_player.start();
				}catch (IOException e) {
					Toast.makeText(  getApplicationContext(),
							   "get io exection ",
							   Toast.LENGTH_SHORT).show();
				}
				/*Toast.makeText(
				   getApplicationContext(),
				   ((TextView) v.findViewById(R.id.home_grid_item1)).getText(),
				   Toast.LENGTH_SHORT).show();*/
 
			}
		});
	}
	private void createTopTabs() {
		 TabSpec recommendTabSpec = mTopTabHost.newTabSpec( "recommend" );
		 recommendTabSpec.setIndicator( "推荐" );
	     recommendTabSpec.setContent( new Intent(this, RecommendTabActivity.class) );
	     
	     TabSpec newestTabSpec = mTopTabHost.newTabSpec( "newest" );
	     newestTabSpec.setIndicator( "最新" );
	     newestTabSpec.setContent( new Intent(this, NewestTabActivity.class) );
	     
	     TabSpec rankingTabSpec = mTopTabHost.newTabSpec( "ranking" );
	     rankingTabSpec.setIndicator( "排行" );
	     rankingTabSpec.setContent( new Intent(this, RankingTabActivity.class) );
	     
	     mTopTabHost.addTab( recommendTabSpec );
	     mTopTabHost.addTab( newestTabSpec );
	     mTopTabHost.addTab( rankingTabSpec );
	        
	     /*TabSpec CateSpec = mTabHost.newTabSpec( "Cate" );
	     CateSpec.setIndicator("", getResources().getDrawable(R.drawable.navigation_cate_sel));
	     Intent CateIntent = new Intent(this, CateTabActivity.class);
	     CateSpec.setContent( CateIntent );*/
	 }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.home_tab );
		mTopTabHost = this.getTabHost();
		createTopTabs();
		mLoading = findViewById( R.id.video_tip_layout );
		mLoadingLayout = (LinearLayout)findViewById( R.id.loading );
		mHometabGridView = (GridView) findViewById( R.id.homtetabGridView );
		mImageView = (ImageView) findViewById( R.id.home_ad_image );
		mHometabGridView.setVisibility( View.GONE );
		mImageView.setVisibility( View.GONE );
		showLoading( true );
		new Thread(new Runnable() {
			@Override
			public void run() {
				InitList();
			}
		}).start();
	}
}
