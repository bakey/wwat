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
 * 閻劍鍩涢崝銊︼拷Adapter缁拷
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewActiveAdapter extends BaseAdapter {
	private Context 					context;//鏉╂劘顢戞稉濠佺瑓閺傦拷
	private List<Active> 				listItems;//閺佺増宓侀梿鍡楁値
	private LayoutInflater 				listContainer;//鐟欏棗娴樼�鐟版珤
	private int 						itemViewResource;//閼奉亜鐣炬稊澶愩�鐟欏棗娴樺┃锟�
	private BitmapManager 				bmpManager;
	static class ListItemView{				//閼奉亜鐣炬稊澶嬪付娴犲爼娉﹂崥锟�
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
	 * 鐎圭偘绶ラ崠鏈卍apter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewActiveAdapter(Context context, List<Active> data,int resource) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//閸掓稑缂撶憴鍡楁禈鐎圭懓娅掗獮鎯邦啎缂冾喕绗傛稉瀣瀮
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
	 * ListView Item鐠佸墽鐤�
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		//Log.d("method", "getView");
		
		//閼奉亜鐣炬稊澶庮瀰閸ワ拷
		ListItemView  listItemView = null;
		
		if (convertView == null) {
			//閼惧嘲褰噇ist_item鐢啫鐪弬鍥︽閻ㄥ嫯顬呴崶锟�			
			convertView = listContainer.inflate(this.itemViewResource, null);
			
			listItemView = new ListItemView();
			//閼惧嘲褰囬幒褌娆㈢�纭呰杽
			listItemView.userface = (ImageView)convertView.findViewById(R.id.active_listitem_userface);
			listItemView.username = (TextView)convertView.findViewById(R.id.active_listitem_username);
			listItemView.content = (TextView)convertView.findViewById(R.id.active_listitem_content);
			listItemView.date = (TextView)convertView.findViewById(R.id.active_listitem_date);
			listItemView.commentCount = (TextView)convertView.findViewById(R.id.active_listitem_commentCount);
			listItemView.client= (TextView)convertView.findViewById(R.id.active_listitem_client);
			listItemView.reply = (TextView)convertView.findViewById(R.id.active_listitem_reply);
			listItemView.redirect = (ImageView)convertView.findViewById(R.id.active_listitem_redirect);
			listItemView.image= (ImageView)convertView.findViewById(R.id.active_listitem_image);
			
			//鐠佸墽鐤嗛幒褌娆㈤梿鍡楀煂convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}
		
		//鐠佸墽鐤嗛弬鍥х摟閸滃苯娴橀悧锟�		
		Active active = listItems.get(position);
		listItemView.username.setText(UIHelper.parseActiveAction(active.getAuthor(),active.getObjectType(),active.getObjectCatalog(),active.getObjectTitle()));
		listItemView.username.setTag(active);//鐠佸墽鐤嗛梾鎰閸欏倹鏆�鐎圭偘缍嬬猾锟�
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
				listItemView.client.setText("来自:手机");
				break;
			case 3:
				listItemView.client.setText("来自:Android");
				break;
			case 4:
				listItemView.client.setText("来自:iPhone");
				break;
			case 5:
				listItemView.client.setText("来自:Windows Phone");
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