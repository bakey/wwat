package com.androidhive.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HometabImageAdapter extends BaseAdapter {
	private Context context;
	private final String[] mAlbums;
	private final String[] mClassify;
 
	public HometabImageAdapter(Context context, String[] albums , String []classify) {
		this.context = context;
		this.mAlbums = albums;
		this.mClassify = classify;
	}
 
	public View getView(int position, View convertView, ViewGroup parent) {
 
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View gridView;
 
		if (convertView == null) {
 
			gridView = new View(context);
 
			// get layout from home_grid.xml
			gridView = inflater.inflate(R.layout.home_grid, null);
 
			// set value into textview
			TextView tv1 = (TextView) gridView.findViewById(R.id.home_grid_item1);
			tv1.setText(mAlbums[position]);
			
			TextView tv2 = (TextView)gridView.findViewById( R.id.home_grid_item2 );
			tv2.setText( mClassify[position] );
 
			// set image based on selected text
			ImageView imageView = (ImageView) gridView.findViewById(R.id.hometab_grid_item_image);
 
			String mobile = mAlbums[position];
			
			imageView.setImageResource( R.drawable.songpic );
			
			//ViewGroup attrParent = (ViewGroup)gridView.findViewById( R.id.starLayout );
			LinearLayout starLayout = (LinearLayout) gridView.findViewById( R.id.starLayout );;
			
			
			for( int i = 0 ; i < 5 ; i ++ ) {	
				ImageView starView = new ImageView( context );
				starView.setBackgroundResource( R.drawable.xin_01 );
				starView.setLayoutParams( new LayoutParams(20,20) );
				starLayout.addView( starView );
			}

 
		} else {
			gridView = (View) convertView;
		}
 
		return gridView;
	}
 
	@Override
	public int getCount() {
		return mAlbums.length;
	}
 
	@Override
	public Object getItem(int position) {
		return null;
	}
 
	@Override
	public long getItemId(int position) {
		return 0;
	}

}
