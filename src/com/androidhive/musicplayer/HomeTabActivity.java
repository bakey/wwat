package com.androidhive.musicplayer;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TabActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.Toast;

public class HomeTabActivity extends Activity {
	final String TAG = "HomeTab";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.home_tab );
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
