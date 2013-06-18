package com.hkzhe.wwtt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import net.oschina.app.common.UIHelper;
import net.oschina.app.ui.Main;
import net.oschina.app.widget.ScrollLayout;

import com.hkzhe.wwtt.R;
import com.hkzhe.wwtt.views.Global;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.app.TabActivity;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;

public class MainActivity extends TabActivity  {
	private ScrollLayout mScrollLayout;	
	private RadioButton[] mButtons;
	private String[] mHeadTitles;
	private int mViewCount;
	private int mCurSel;
	
	private ImageView mHeadLogo;
	private TextView mHeadTitle;
	private ProgressBar mHeadProgress;
	private ImageButton mHead_search;
	private ImageButton mHeadPub_post;
	private ImageButton mHeadPub_tweet;
	
	private TabHost    mTabHost;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		this.initHeadView();
		
		//GuideHelper guideHelper = new GuideHelper(this);
       // guideHelper.openGuide();
        
       /* setContentView( R.layout.main );
        mTabHost = this.getTabHost();   
        
        TabSpec homeTabSpec = mTabHost.newTabSpec( "Home" );
        homeTabSpec.setIndicator("", getResources().getDrawable(R.drawable.navigation_index_sel));
        Intent homeTabIntent = new Intent(this, HomeTabActivity.class);
        homeTabSpec.setContent(homeTabIntent);
        
        TabSpec CateSpec = mTabHost.newTabSpec( "Cate" );
        CateSpec.setIndicator("", getResources().getDrawable(R.drawable.navigation_cate_sel));
        Intent CateIntent = new Intent(this, CateTabActivity.class);
        CateSpec.setContent( CateIntent );
        
        TabSpec PocketSpec = mTabHost.newTabSpec( "Pocket" );
        PocketSpec.setIndicator("" , getResources().getDrawable(R.drawable.navigation_pocket_sel));
        Intent PocketIntent = new Intent( this , PocketTabActivity.class );
        PocketSpec.setContent( PocketIntent );
        
        TabSpec PlayingSpec = mTabHost.newTabSpec( "Playing" );
        ImageView playingView = new ImageView( this );
        Global.tabView = playingView;
        playingView.setImageResource( R.drawable.navigation_playing_num_sel );
        //playingView.setBackground( getResources().getDrawable(R.drawable.navigation_playing_num_sel) );
        playingView.setId( R.id.playing_tab_animation );
        PlayingSpec.setIndicator( playingView );
        //PlayingSpec.setIndicator("" , getResources().getDrawable(R.drawable.navigation_playing_num_sel));
        Intent PlayingIntent = new Intent( this , PlayingTabActivity.class );
        PlayingSpec.setContent( PlayingIntent );
        
        
        //添加选项卡  
        if ( mTabHost != null ) {
        	mTabHost.addTab( homeTabSpec );  
        	mTabHost.addTab( CateSpec );  
        	mTabHost.addTab( PocketSpec );  
        	mTabHost.addTab( PlayingSpec );
        }		
        mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
        	@Override
        	public void onTabChanged(String tabId) {
        		int i = mTabHost.getCurrentTab();
        	  }
        });*/
   
	}	
	 /**
     * 初始化头部视图
     */
    private void initHeadView()
    {
    	mHeadLogo = (ImageView)findViewById(R.id.main_head_logo);
    	mHeadTitle = (TextView)findViewById(R.id.main_head_title);
    	mHeadProgress = (ProgressBar)findViewById(R.id.main_head_progress);
    	mHead_search = (ImageButton)findViewById(R.id.main_head_search);
    	mHeadPub_post = (ImageButton)findViewById(R.id.main_head_pub_post);
    	mHeadPub_tweet = (ImageButton)findViewById(R.id.main_head_pub_tweet);
    	
    	mHead_search.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				UIHelper.showSearch(v.getContext());
			}
		});
    	mHeadPub_post.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				UIHelper.showQuestionPub(v.getContext());
			}
		});
    	mHeadPub_tweet.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				UIHelper.showTweetPub(MainActivity.this);
			}
		});
    }
	
	@Override
    protected void onActivityResult(int requestCode,
                                     int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); 
    }
	
	@Override
	 public void onDestroy(){
		super.onDestroy();
	    //mp.release();
	 }
	
}