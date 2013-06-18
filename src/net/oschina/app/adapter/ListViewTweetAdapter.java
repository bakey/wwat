package net.oschina.app.adapter;

import java.util.List;

import com.hkzhe.wwtt.R;
import net.oschina.app.bean.Tweet;
import net.oschina.app.common.BitmapManager;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 鍔ㄥ脊Adapter绫�
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewTweetAdapter extends BaseAdapter {
	private Context 					context;//杩愯涓婁笅鏂�
	private List<Tweet> 				listItems;//鏁版嵁闆嗗悎
	private LayoutInflater 				listContainer;//瑙嗗浘瀹瑰櫒
	private int 						itemViewResource;//鑷畾涔夐」瑙嗗浘婧�
	private BitmapManager 				bmpManager;
	static class ListItemView{				//鑷畾涔夋帶浠堕泦鍚� 
			public ImageView userface;  
	        public TextView username;  
		    public TextView date;  
		    public TextView content;
		    public TextView commentCount;
		    public TextView client;
		    public ImageView image;
	 }  

	/**
	 * 瀹炰緥鍖朅dapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewTweetAdapter(Context context, List<Tweet> data,int resource) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//鍒涘缓瑙嗗浘瀹瑰櫒骞惰缃笂涓嬫枃
		this.itemViewResource = resource;
		this.listItems = data;
		this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.widget_dface_loading));
	}
	
	public int getCount() {
		return listItems.size();
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}
	   
	/**
	 * ListView Item璁剧疆
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		//Log.d("method", "getView");
		
		//鑷畾涔夎鍥�
		ListItemView  listItemView = null;
		
		if (convertView == null) {
			//鑾峰彇list_item甯冨眬鏂囦欢鐨勮鍥�
			convertView = listContainer.inflate(this.itemViewResource, null);
			
			listItemView = new ListItemView();
			//鑾峰彇鎺т欢瀵硅薄
			listItemView.userface = (ImageView)convertView.findViewById(R.id.tweet_listitem_userface);
			listItemView.username = (TextView)convertView.findViewById(R.id.tweet_listitem_username);
			listItemView.content = (TextView)convertView.findViewById(R.id.tweet_listitem_content);
			listItemView.image= (ImageView)convertView.findViewById(R.id.tweet_listitem_image);
			listItemView.date= (TextView)convertView.findViewById(R.id.tweet_listitem_date);
			listItemView.commentCount= (TextView)convertView.findViewById(R.id.tweet_listitem_commentCount);
			listItemView.client= (TextView)convertView.findViewById(R.id.tweet_listitem_client);
			
			//璁剧疆鎺т欢闆嗗埌convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}
				
		//璁剧疆鏂囧瓧鍜屽浘鐗�
		Tweet tweet = listItems.get(position);
		listItemView.username.setText(tweet.getAuthor());
		listItemView.username.setTag(tweet);//璁剧疆闅愯棌鍙傛暟(瀹炰綋绫�
		listItemView.content.setText(tweet.getBody());
		listItemView.date.setText(StringUtils.friendly_time(tweet.getPubDate()));
		listItemView.commentCount.setText(tweet.getCommentCount()+"");

		switch(tweet.getAppClient())
		{	
			case 0:
			case 1:
				listItemView.client.setText("");
				break;
			case 2:
				listItemView.client.setText("鏉ヨ嚜:鎵嬫満");
				break;
			case 3:
				listItemView.client.setText("鏉ヨ嚜:Android");
				break;
			case 4:
				listItemView.client.setText("鏉ヨ嚜:iPhone");
				break;
			case 5:
				listItemView.client.setText("鏉ヨ嚜:Windows Phone");
				break;
		}
		if(StringUtils.isEmpty(listItemView.client.getText().toString()))
			listItemView.client.setVisibility(View.GONE);
		else
			listItemView.client.setVisibility(View.VISIBLE);
		
		String faceURL = tweet.getFace();
		if(faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL)){
			listItemView.userface.setImageResource(R.drawable.widget_dface);
		}else{
			bmpManager.loadBitmap(faceURL, listItemView.userface);
		}
		listItemView.userface.setOnClickListener(faceClickListener);
		listItemView.userface.setTag(tweet);
		
		String imgSmall = tweet.getImgSmall();
		if(!StringUtils.isEmpty(imgSmall)) {
			bmpManager.loadBitmap(imgSmall, listItemView.image, BitmapFactory.decodeResource(context.getResources(), R.drawable.image_loading));
			listItemView.image.setOnClickListener(imageClickListener);
			listItemView.image.setTag(tweet.getImgBig());
			listItemView.image.setVisibility(ImageView.VISIBLE);
		}else{
			listItemView.image.setVisibility(ImageView.GONE);
		}
		
		return convertView;
	}
	
	private View.OnClickListener faceClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			Tweet tweet = (Tweet)v.getTag();
			UIHelper.showUserCenter(v.getContext(), tweet.getAuthorId(), tweet.getAuthor());
		}
	};
	
	private View.OnClickListener imageClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			UIHelper.showImageDialog(v.getContext(), (String)v.getTag());
		}
	};
}