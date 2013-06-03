package com.androidhive.musicplayer;


import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TabActivity;
import android.media.MediaPlayer;
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
	private  MediaPlayer m_player;
	static final String[] mAudioTitles = new String[] { 
		"С������", "��һȥ����","������", "������������ѡ" ,"����Ա���" , "��ͷ�ְֺ�Сͷ����"};
	static final String[] mAudioClassify = new String[]{
		"�������","���ɹ���","���ɹ���","Ԣ�Թ���","���轲����" , "��ͷ����ϵ��"
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.home_tab );
		
		m_player = new MediaPlayer();
		
		mHometabGridView = (GridView) findViewById( R.id.homtetabGridView );
		
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
	/*@Override
	public void onResume() {
		mTabHost.getTabWidget().getChildAt(0).setOnClickListener( new View.OnClickListener() {
			public void onClick(View arg0) {
				Log.d( TAG , "click home tab ");
			}
		});
	}*/

}
