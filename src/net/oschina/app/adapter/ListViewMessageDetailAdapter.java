package net.oschina.app.adapter;

import java.util.List;

import net.oschina.app.AppContext;
import com.hkzhe.wwtt.R;
import net.oschina.app.bean.Comment;
import net.oschina.app.common.BitmapManager;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import net.oschina.app.widget.LinkView;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 閻劍鍩涢悾娆掆枅鐠囷附鍎廇dapter缁拷
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewMessageDetailAdapter extends BaseAdapter {
	private Context 					context;//鏉╂劘顢戞稉濠佺瑓閺傦拷
	private List<Comment> 				listItems;//閺佺増宓侀梿鍡楁値
	private LayoutInflater 				listContainer;//鐟欏棗娴樼�鐟版珤
	private int 						itemViewResource;//閼奉亜鐣炬稊澶愩�鐟欏棗娴樺┃锟�
	private BitmapManager 				bmpManager;
	static class ListItemView{				//閼奉亜鐣炬稊澶嬪付娴犲爼娉﹂崥锟�
			public ImageView userface1;
			public ImageView userface2;
			public LinkView username;  
		    public TextView date;  
		    public LinearLayout contentll;
	 }  

	/**
	 * 鐎圭偘绶ラ崠鏈卍apter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewMessageDetailAdapter(Context context, List<Comment> data,int resource) {
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
			//閼惧嘲褰噇ist_item鐢啫鐪弬鍥︽閻ㄥ嫯顬呴崶锟�			convertView = listContainer.inflate(this.itemViewResource, null);
			
			listItemView = new ListItemView();
			//閼惧嘲褰囬幒褌娆㈢�纭呰杽
			listItemView.userface1 = (ImageView)convertView.findViewById(R.id.messagedetail_listitem_userface1);
			listItemView.userface2 = (ImageView)convertView.findViewById(R.id.messagedetail_listitem_userface2);
			listItemView.username = (LinkView)convertView.findViewById(R.id.messagedetail_listitem_username);
			listItemView.date = (TextView)convertView.findViewById(R.id.messagedetail_listitem_date);
			listItemView.contentll = (LinearLayout)convertView.findViewById(R.id.messagedetail_listitem_contentll);
			
			//鐠佸墽鐤嗛幒褌娆㈤梿鍡楀煂convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}
		
		//鐠佸墽鐤嗛弬鍥х摟閸滃苯娴橀悧锟�	
		Comment msg = listItems.get(position);
		listItemView.username.setLinkText("<font color='#0e5986'><b>" + msg.getAuthor() + "</b></font>閿涳拷" + msg.getContent());
		//listItemView.username.setText(UIHelper.parseMessageSpan(msg.getAuthor(), msg.getContent(), ""));
		//listItemView.username.parseLinkText();
		listItemView.username.setTag(msg);//鐠佸墽鐤嗛梾鎰閸欏倹鏆�鐎圭偘缍嬬猾锟�
		listItemView.date.setText(StringUtils.friendly_time(msg.getPubDate()));
		
		String faceURL = msg.getFace();
		AppContext ac = (AppContext)context.getApplicationContext();
		//閸欐垹鏆�懛锟斤拷閺勵垱鍨�
		if(msg.getAuthorId() == ac.getLoginUid())
		{
			if(faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL)){
				listItemView.userface2.setImageResource(R.drawable.widget_dface);
			}else{
				bmpManager.loadBitmap(faceURL, listItemView.userface2);
			}
			listItemView.userface2.setOnClickListener(faceClickListener);
			listItemView.userface2.setTag(msg);
			listItemView.userface2.setVisibility(ImageView.VISIBLE);
			listItemView.userface1.setVisibility(ImageView.GONE);
			listItemView.contentll.setBackgroundResource(R.drawable.review_bg_right);
		}else{
			if(faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL)){
				listItemView.userface1.setImageResource(R.drawable.widget_dface);
			}else{
				bmpManager.loadBitmap(faceURL, listItemView.userface1);
			}
			listItemView.userface1.setOnClickListener(faceClickListener);
			listItemView.userface1.setTag(msg);
			listItemView.userface1.setVisibility(ImageView.VISIBLE);
			listItemView.userface2.setVisibility(ImageView.GONE);
			listItemView.contentll.setBackgroundResource(R.drawable.review_bg_left);
		}
		
		return convertView;
	}
	
	private View.OnClickListener faceClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			Comment msg = (Comment)v.getTag();
			UIHelper.showUserCenter(v.getContext(), msg.getAuthorId(), msg.getAuthor());
		}
	};
    
}