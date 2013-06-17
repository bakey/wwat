package com.androidhive.musicplayer;

import android.app.Application;
import android.media.MediaPlayer;

public class PlayingApplication extends Application {
	private String mAudioTitle;
	private boolean mPlaying;
	private  MediaPlayer m_player;
	public PlayingApplication() {
		mPlaying = false;
	}
	public void setAudioTitle( String title ) {
		mAudioTitle = title;
	}
	public void setPlaying( boolean playing ) {
		mPlaying = playing;
	}
	public void setPlayer( MediaPlayer player ) {
		m_player = player;
	}
	public String getAudioTitle() {
		return mAudioTitle;
	}
	public boolean getPlaying() {
		return mPlaying;
	}
	public MediaPlayer getPlayer() {
		return m_player;
	}

}
