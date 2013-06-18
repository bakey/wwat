package net.oschina.app.adapter;

import java.util.List;

import net.oschina.app.AppContext;
import com.hkzhe.wwtt.R;
import net.oschina.app.bean.Messages;
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
 * 鐢ㄦ埛鐣欒█Adapter绫�
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewMessageAdapter extends BaseAdapter {
	private Context 					context;//杩愯涓婁笅鏂�
	private List<Messages> 				listItems;//鏁版嵁闆嗗悎
	private LayoutInflater 				listContainer;//瑙嗗浘瀹瑰櫒
	private int 						itemViewResource;//鑷畾涔夐」瑙嗗浘婧�
	private BitmapManager 				bmpManager;
	static class ListItemView{				//鑷畾涔夋帶浠堕泦鍚� 
			public ImageView userface;
			public TextView username;
		    public TextView date;  
		    public TextView messageCount;
	 }  

	/**
	 * 瀹炰緥鍖朅dapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewMessageAdapter(Context context, List<Messages> data,int resource) {
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
			listItemView.userface = (ImageView)convertView.findViewById(R.id.message_listitem_userface);
			listItemView.username = (TextView)convertView.findViewById(R.id.message_listitem_username);
			listItemView.date = (TextView)convertView.findViewById(R.id.message_listitem_date);
			listItemView.messageCount = (TextView)convertView.findViewById(R.id.message_listitem_messageCount);
			
			//璁剧疆鎺т欢闆嗗埌convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}
		
		//璁剧疆鏂囧瓧鍜屽浘鐗�
		Messages msg = listItems.get(position);
		AppContext ac = (AppContext)context.getApplicationContext();
		if(msg.getSenderId() == ac.getLoginUid()){
			listItemView.username.setText(UIHelper.parseMessageSpan(msg.getFriendName(), msg.getContent(), "鍙戠粰 "));
		}else{
			listItemView.username.setText(UIHelper.parseMessageSpan(msg.getSender(), msg.getContent(), ""));
		}
		listItemView.username.setTag(msg);//璁剧疆闅愯棌鍙傛暟(瀹炰綋绫�
		listItemView.date.setText(StringUtils.friendly_time(msg.getPubDate()));
		listItemView.messageCount.setText("鍏辨湁 "+msg.getMessageCount()+" 鏉＄暀瑷�");
		
		String faceURL = msg.getFace();
		if(faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL)){
			listItemView.userface.setImageResource(R.drawable.widget_dface);
		}else{
			bmpManager.loadBitmap(faceURL, listItemView.userface);
		}
		listItemView.userface.setOnClickListener(faceClickListener);
		listItemView.userface.setTag(msg);
		
		return convertView;
	}
	
	private View.OnClickListener faceClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			Messages msg = (Messages)v.getTag();
			UIHelper.showUserCenter(v.getContext(), msg.getFriendId(), msg.getFriendName());
		}
	};
}