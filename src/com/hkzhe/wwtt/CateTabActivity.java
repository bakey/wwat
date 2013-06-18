package com.hkzhe.wwtt;


import java.util.ArrayList;
import java.util.HashMap;

import com.hkzhe.wwtt.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

public class CateTabActivity extends Activity {
	GridView  mGridView;
	static final String[] mAlbums = new String[] { 
		"ÁùÒ»×¨¼­", "¶ù¸è","ÒôÀÖ", "¹ÊÊÂ" , "ÏµÁÐ¹ÊÊÂ" , "Ó¢Óï" , "ËÐ¶Á" , "sanwushuoshi" , "heiheihei" , "ggggg" , };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.cate_tab );
		
		mGridView = (GridView) findViewById( R.id.cateGridView );
		
		mGridView.setAdapter(new ImageAdapter(this, mAlbums));
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Toast.makeText(
				   getApplicationContext(),
				   ((TextView) v.findViewById(R.id.ItemText)).getText(),
				   Toast.LENGTH_SHORT).show();
 
			}
		});
		/*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, numbers);
		
		mGridView.setAdapter( adapter );
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
				   Toast.makeText(getApplicationContext(),
							((TextView) view).getText(), Toast.LENGTH_SHORT).show();
				
			}
			/*public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				   Toast.makeText(getApplicationContext(),
					((TextView) v).getText(), Toast.LENGTH_SHORT).show();
				}
			});*/
	}

}
