package com.hkzhe.wwtt;

import java.io.IOException;


import net.oschina.app.bean.News;
import net.oschina.app.common.UIHelper;
import net.oschina.app.ui.Main;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class PlayManager implements OnCompletionListener, SeekBar.OnSeekBarChangeListener {
	private ImageButton m_play_btnPlay;
	private ImageButton m_play_btnPrevious;
	private ImageButton m_play_btnNext;
	private TextView m_songTitle;
	private TextView m_songCurrentDurationLabel;
	private TextView m_songTotalDurationLabel;
	private SeekBar m_songProgressBar;
	private Activity m_context;
	private MediaPlayer m_player;
	private ImageView   m_AudioThumb;
	private ImageView   m_playingSignal;
	private boolean    m_isPause;
	private Handler mHandler = new Handler();
	private Runnable mUpdateTimeTask = new Runnable() {
		   public void run() {
			   long totalDuration = m_player.getDuration();
			   long currentDuration = m_player.getCurrentPosition();
			  
			   // Displaying Total Duration time
			   m_songTotalDurationLabel.setText(""+Utilities.milliSecondsToTimer(totalDuration));
			   // Displaying time completed playing
			   m_songCurrentDurationLabel.setText(""+Utilities.milliSecondsToTimer(currentDuration));
			   
			   // Updating progress bar
			   int progress = (int)(Utilities.getProgressPercentage(currentDuration, totalDuration));
			   //Log.d("Progress", ""+progress);
			   m_songProgressBar.setProgress(progress);
			   
			   // Running this thread after 100 milliseconds
		       mHandler.postDelayed(this, 100);
		   }
	};
	
	private void bindButtons() 
	{
		m_play_btnPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// check for already playing
				if(m_player.isPlaying()){
					if( m_player != null ){
						m_player.pause();
						// Changing button image to play button
						m_play_btnPlay.setImageResource(R.drawable.listening_play_img);						
						AnimationDrawable animation = (AnimationDrawable) m_playingSignal.getDrawable();
						if ( animation != null ) {
							Log.d("bakey" , "stop the animation ");
							animation.stop();	
							m_playingSignal.setImageResource( R.drawable.navigation_playing_num_sel );
						}else {
							Log.d("bakey" , "get animation failed ");							
						}
						m_isPause = true;
					}
				}else{
					// Resume song
					if( m_player != null ){
						if ( m_isPause ) {
							m_player.start();
							m_play_btnPlay.setImageResource(R.drawable.listening_pause_img);
							m_isPause = false;
							m_playingSignal.setImageResource( R.drawable.playing );
							AnimationDrawable animation = (AnimationDrawable) m_playingSignal.getDrawable();
							if ( animation != null ) {
								animation.start();
							}							
						}
					}
				}
				
			}
		});
	}
	public void initPlayingElements( Activity context )
    {
		m_player = new MediaPlayer();
		m_isPause = false;
		m_context = context;
    	m_play_btnPlay     				= (ImageButton) m_context.findViewById(R.id.btnPlay);
    	m_play_btnNext     				= ( ImageButton ) m_context.findViewById( R.id.btnNext );
    	m_play_btnPrevious 				= (ImageButton) m_context.findViewById( R.id.btnPrevious );
    	m_songTitle        				=  (TextView) m_context.findViewById(R.id.songTitle);
    	m_songCurrentDurationLabel 		= (TextView) m_context.findViewById(R.id.songCurrentDurationLabel);
		m_songTotalDurationLabel 		= (TextView) m_context.findViewById(R.id.songTotalDurationLabel);
		m_songProgressBar               = (SeekBar) m_context.findViewById(R.id.songProgressBar);
		m_AudioThumb                    = (ImageView) m_context.findViewById( R.id.songThumb );
		m_playingSignal                 = (ImageView)m_context.findViewById( R.id.main_footbar_setting );
		bindButtons();
    }
	public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);        
    }	
	public void startPlay( News news ) {
		if ( m_context == null ) {
			return ;
		}
		m_player.reset();
		m_playingSignal.setImageResource( R.drawable.playing );
		AnimationDrawable animation = (AnimationDrawable) m_playingSignal.getDrawable();
		if ( animation != null ) {
			animation.start();
		}
		String songPath = news.getUrl();
		try {
			m_player.setDataSource( songPath );
			m_player.prepare();
    		m_player.start();
    		
    		m_AudioThumb.setImageResource( R.drawable.songpic2 );
    		m_songTitle.setText( news.getTitle() );
    		m_play_btnPlay.setImageResource( R.drawable.listening_pause_img );
    		m_songProgressBar.setProgress(0);
    		m_songProgressBar.setMax(100);
    		updateProgressBar();
		}catch (IOException e) {
			//UIHelper.ToastMessage( Main.this ,  "get io exection of play" );				
		}catch( IllegalStateException e ) {
			e.printStackTrace();			
		}
	}
	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		
	}

}
