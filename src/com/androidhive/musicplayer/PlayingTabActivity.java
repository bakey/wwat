package com.androidhive.musicplayer;


import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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
	private Runnable mUpdateTimeTask = new Runnable() {
		   public void run() {
			   MediaPlayer mp = mPlayingApp.getPlayer();
			   long totalDuration = mp.getDuration();
			   long currentDuration = mp.getCurrentPosition();
			  
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
		this.setContentView( R.layout.playing_tab );
		mPlayingApp = ((PlayingApplication)getApplicationContext()); 
		mBtnPlay = (ImageButton) findViewById(R.id.btnPlay);
		mAudioProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
		mAudioCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		mAudioTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
		
		if ( mPlayingApp.getPlaying() ) {
			mAudioTitleLable = (TextView) findViewById( R.id.songTitle );
			mAudioTitleLable.setText( mPlayingApp.getAudioTitle() );
		
			mAudioThumb = (ImageView) findViewById( R.id.songThumb );
			mAudioThumb.setImageResource( R.drawable.songpic );
			//mAudioThumb.setImageDrawable( getResources().getDrawable( R.drawable.songpic ) );
			
			mBtnPlay.setImageResource(R.drawable.btn_pause);
			mAudioProgressBar.setMax(100);
			mHandler.postDelayed(mUpdateTimeTask, 100);
		}
	}

}
