package com.androidhive.musicplayer;


import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

public class HomeTabActivity extends TabActivity {
	final String TAG = "HomeTab";
	GridView mHometabGridView;
	ImageView mImageView;
	LinearLayout  mLoadingLayout;
	private TabHost    mTopTabHost;
	private  MediaPlayer m_player;
	protected static final int IMAGE_LOADED = 0x101;  
	View mLoading;
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
	static final String[] mAudioTitles = new String[] { 
		"小猪讲故事", "六一去郊游","天上星", "两个旋和三个选" ,"妈妈乖宝宝" , "大头爸爸和小头儿子"};
	static final String[] mAudioClassify = new String[]{
		"动物故事","轻松故事","轻松故事","寓言故事","妈妈讲故事" , "床头故事系列"
	};
	private void showLoading(boolean bShow){
		if(mLoading == null) {
			return;
		}
		mLoading.setVisibility(bShow ? View.VISIBLE : View.GONE);
	}
	private void InitList()
	{
		try {
			Thread.sleep(2000);
		}catch( InterruptedException e ) {
			Toast.makeText(  getApplicationContext(),
					   "get interrupted exection ",
					   Toast.LENGTH_SHORT).show();
		}
		 Message msg = Message.obtain();
		 msg.what = HomeTabActivity.IMAGE_LOADED;
		 mHandler.sendMessage( msg ); //发送消息
	}
	private void showMainContent() {
		//showLoading( false );
		((ViewGroup) mLoadingLayout.getParent()).removeView( mLoadingLayout );
		//mLoadingLayout.removeView( mLoading );
		m_player = new MediaPlayer();
		
		mHometabGridView.setVisibility( View.VISIBLE );
		mImageView.setVisibility( View.VISIBLE );
				
		mHometabGridView.setAdapter(new HometabImageAdapter(this, mAudioTitles , mAudioClassify));
		mHometabGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				m_player.reset();
				String songPath = "http://www.hkzhe.com/song/" + position + ".mp3";
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
	/*@Override
	public void onResume() {
		mTabHost.getTabWidget().getChildAt(0).setOnClickListener( new View.OnClickListener() {
			public void onClick(View arg0) {
				Log.d( TAG , "click home tab ");
			}
		});
	}*/

}
