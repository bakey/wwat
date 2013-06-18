package net.oschina.app.adapter;

import java.util.List;

import com.hkzhe.wwtt.R;
import net.oschina.app.bean.Active;
import net.oschina.app.bean.Active.ObjectReply;
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
 * 鐢ㄦ埛鍔ㄦ�Adapter绫�
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewActiveAdapter extends BaseAdapter {
	private Context 					context;//杩愯涓婁笅鏂�
	private List<Active> 				listItems;//鏁版嵁闆嗗悎
	private LayoutInflater 				listContainer;//瑙嗗浘瀹瑰櫒
	private int 						itemViewResource;//鑷畾涔夐」瑙嗗浘婧�
	private BitmapManager 				bmpManager;
	static class ListItemView{				//鑷畾涔夋帶浠堕泦鍚� 
			public ImageView userface;  
	        public TextView username;  
		    public TextView date;  
		    public TextView content;
		    public TextView reply;
		    public TextView commentCount;
		    public TextView client;
		    public ImageView redirect;  
		    public ImageView image;
	 }  

	/**
	 * 瀹炰緥鍖朅dapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewActiveAdapter(Context context, List<Active> data,int resource) {
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
			listItemView.userface = (ImageView)convertView.findViewById(R.id.active_listitem_userface);
			listItemView.username = (TextView)convertView.findViewById(R.id.active_listitem_username);
			listItemView.content = (TextView)convertView.findViewById(R.id.active_listitem_content);
			listItemView.date = (TextView)convertView.findViewById(R.id.active_listitem_date);
			listItemView.commentCount = (TextView)convertView.findViewById(R.id.active_listitem_commentCount);
			listItemView.client= (TextView)convertView.findViewById(R.id.active_listitem_client);
			listItemView.reply = (TextView)convertView.findViewById(R.id.active_listitem_reply);
			listItemView.redirect = (ImageView)convertView.findViewById(R.id.active_listitem_redirect);
			listItemView.image= (ImageView)convertView.findViewById(R.id.active_listitem_image);
			
			//璁剧疆鎺т欢闆嗗埌convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}
		
		//璁剧疆鏂囧瓧鍜屽浘鐗�
		Active active = listItems.get(position);
		listItemView.username.setText(UIHelper.parseActiveAction(active.getAuthor(),active.getObjectType(),active.getObjectCatalog(),active.getObjectTitle()));
		listItemView.username.setTag(active);//璁剧疆闅愯棌鍙傛暟(瀹炰綋绫�
		listItemView.content.setText(active.getMessage());
		listItemView.date.setText(StringUtils.friendly_time(active.getPubDate()));
		listItemView.commentCount.setText(active.getCommentCount()+"");
		
		switch(active.getAppClient())
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
		
		ObjectReply reply= active.getObjectReply();
		if(reply != null){
			listItemView.reply.setText(UIHelper.parseActiveReply(reply.objectName, reply.objectBody));
			listItemView.reply.setVisibility(TextView.VISIBLE);
		}else{
			listItemView.reply.setText("");
			listItemView.reply.setVisibility(TextView.GONE);
		}
		
		if(active.getActiveType() == Active.CATALOG_OTHER)
			listItemView.redirect.setVisibility(ImageView.GONE);
		else
			listItemView.redirect.setVisibility(ImageView.VISIBLE);
		
		String faceURL = active.getFace();
		if(faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL)){
			listItemView.userface.setImageResource(R.drawable.widget_dface);
		}else{
			bmpManager.loadBitmap(faceURL, listItemView.userface);
		}
		listItemView.userface.setOnClickListener(faceClickListener);
		listItemView.userface.setTag(active);
		
		String imgSmall = active.getTweetimage();
		if(!StringUtils.isEmpty(imgSmall)) {
			bmpManager.loadBitmap(imgSmall, listItemView.image, BitmapFactory.decodeResource(context.getResources(), R.drawable.image_loading));
			listItemView.image.setVisibility(ImageView.VISIBLE);
		}else{
			listItemView.image.setVisibility(ImageView.GONE);
		}
		
		return convertView;
	}
	
	
	private View.OnClickListener faceClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			Active active = (Active)v.getTag();
			UIHelper.showUserCenter(v.getContext(), active.getAuthorId(), active.getAuthor());
		}
	};

}