package com.androidhive.musicplayer;


import com.androidhive.musicplayer.views.Global;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class PlayingTabActivity extends Activity {
	private TextView mAudioTitleLable;
	private ImageView mAudioThumb;
	private ImageButton mBtnPlay;
	private SeekBar mAudioProgressBar;
	private TextView mAudioTotalDurationLabel;
	private TextView mAudioCurrentDurationLabel;
	private Handler mHandler = new Handler();
	PlayingApplication mPlayingApp;
	MediaPlayer m_player;
	final String TAG = "PlayingTab";
	private Runnable mUpdateTimeTask = new Runnable() {
		   public void run() {
			   long totalDuration = m_player.getDuration();
			   long currentDuration = m_player.getCurrentPosition();
			  
			   // Displaying Total Duration time
			   mAudioTotalDurationLabel.setText(""+Utilities.milliSecondsToTimer(totalDuration));
			   // Displaying time completed playing
			   mAudioCurrentDurationLabel.setText(""+Utilities.milliSecondsToTimer(currentDuration));
			   
			   // Updating progress bar
			   int progress = (int)(Utilities.getProgressPercentage(currentDuration, totalDuration));
			   mAudioProgressBar.setProgress(progress);
			   
			   // Running this thread after 100 milliseconds
		       mHandler.postDelayed(this, 100);
		   }
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mPlayingApp = (PlayingApplication)getApplicationContext(); 
		Log.d(TAG , "get playing stat = " + mPlayingApp.getPlaying() );
		if ( mPlayingApp.getPlaying()  ) {
			this.setContentView( R.layout.playing_tab );
		}else {
			this.setContentView( R.layout.play_stop_tab ) ;
			return ;
		}
		
		mBtnPlay = (ImageButton) findViewById(R.id.btnPlay);
		mAudioProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
		mAudioCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		mAudioTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
		m_player = mPlayingApp.getPlayer();
		
		
		mBtnPlay.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// check for already playing
				if( m_player.isPlaying()){
					if( m_player != null ){
						m_player.pause();
						// Changing button image to play button
						mBtnPlay.setImageResource(R.drawable.btn_play);
						mPlayingApp.setPlaying( false );
						Global.tabView.setImageResource( R.drawable.navigation_playing_num_sel );
					}
				}else{
					// Resume song
					if( m_player !=null){
						m_player.start();
						// Changing button image to pause button
						mBtnPlay.setImageResource(R.drawable.btn_pause);
						mPlayingApp.setPlaying( true );
					}
				}
				
			}
		});
		
		if ( mPlayingApp.getPlaying() ) {
			mAudioTitleLable = (TextView) findViewById( R.id.songTitle );
			mAudioTitleLable.setText( mPlayingApp.getAudioTitle() );
		
			mAudioThumb = (ImageView) findViewById( R.id.songThumb );
			mAudioThumb.setImageResource( R.drawable.songpic );
			
			mBtnPlay.setImageResource(R.drawable.btn_pause);
			mAudioProgressBar.setMax(100);
			mHandler.postDelayed(mUpdateTimeTask, 100);
		}
	}

}
