package net.oschina.app.adapter;

import java.util.List;

import com.hkzhe.wwtt.R;
import net.oschina.app.bean.FriendList.Friend;
import net.oschina.app.common.BitmapManager;
import net.oschina.app.common.StringUtils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 鐢ㄦ埛绮変笣銆佸叧娉ˋdapter绫�
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-5-24
 */
public class ListViewFriendAdapter extends BaseAdapter {
	private Context 					context;//杩愯涓婁笅鏂�
	private List<Friend> 				listItems;//鏁版嵁闆嗗悎
	private LayoutInflater 				listContainer;//瑙嗗浘瀹瑰櫒
	private int 						itemViewResource;//鑷畾涔夐」瑙嗗浘婧�
	private BitmapManager 				bmpManager;
	static class ListItemView{				//鑷畾涔夋帶浠堕泦鍚� 
        public ImageView face;  
        public ImageView gender;
        public TextView name;  
        public TextView expertise;
	}  

	/**
	 * 瀹炰緥鍖朅dapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewFriendAdapter(Context context, List<Friend> data,int resource) {
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
			listItemView.name = (TextView)convertView.findViewById(R.id.friend_listitem_name);
			listItemView.expertise = (TextView)convertView.findViewById(R.id.friend_listitem_expertise);
			listItemView.face = (ImageView)convertView.findViewById(R.id.friend_listitem_userface);
			listItemView.gender = (ImageView)convertView.findViewById(R.id.friend_listitem_gender);
			
			//璁剧疆鎺т欢闆嗗埌convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		
		//璁剧疆鏂囧瓧鍜屽浘鐗�
		Friend friend = listItems.get(position);
		
		listItemView.name.setText(friend.getName());
		listItemView.name.setTag(friend);//璁剧疆闅愯棌鍙傛暟(瀹炰綋绫�
		listItemView.expertise.setText(friend.getExpertise());
		
		if(friend.getGender() == 1)
			listItemView.gender.setImageResource(R.drawable.widget_gender_man);
		else
			listItemView.gender.setImageResource(R.drawable.widget_gender_woman);
		
		String faceURL = friend.getFace();
		if(faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL)){
			listItemView.face.setImageResource(R.drawable.widget_dface);
		}else{
			bmpManager.loadBitmap(faceURL, listItemView.face);
		}
		listItemView.face.setTag(friend);
		
		return convertView;
	}
}