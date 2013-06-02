package com.androidhive.musicplayer;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TabActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class HomeTabActivity extends Activity {
	final String TAG = "HomeTab";
	GridView mHometabGridView;
	static final String[] mAudioTitles = new String[] { 
		"小猪讲故事", "六一去郊游","天上星", "两个旋和三个选" , "22" , "33" , "44" };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.home_tab );
		
		mHometabGridView = (GridView) findViewById( R.id.homtetabGridView );
		
		mHometabGridView.setAdapter(new HometabImageAdapter(this, mAudioTitles));
		mHometabGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Toast.makeText(
				   getApplicationContext(),
				   ((TextView) v.findViewById(R.id.home_grid_item1)).getText(),
				   Toast.LENGTH_SHORT).show();
 
			}
		});
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
