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
 * 閻劍鍩涚划澶夌閵嗕礁鍙у▔藡dapter缁拷
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-5-24
 */
public class ListViewFriendAdapter extends BaseAdapter {
	private Context 					context;//鏉╂劘顢戞稉濠佺瑓閺傦拷
	private List<Friend> 				listItems;//閺佺増宓侀梿鍡楁値
	private LayoutInflater 				listContainer;//鐟欏棗娴樼�鐟版珤
	private int 						itemViewResource;//閼奉亜鐣炬稊澶愩�鐟欏棗娴樺┃锟�
	private BitmapManager 				bmpManager;
	static class ListItemView{				//閼奉亜鐣炬稊澶嬪付娴犲爼娉﹂崥锟�
        public ImageView face;  
        public ImageView gender;
        public TextView name;  
        public TextView expertise;
	}  

	/**
	 * 鐎圭偘绶ラ崠鏈卍apter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewFriendAdapter(Context context, List<Friend> data,int resource) {
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
			listItemView.name = (TextView)convertView.findViewById(R.id.friend_listitem_name);
			listItemView.expertise = (TextView)convertView.findViewById(R.id.friend_listitem_expertise);
			listItemView.face = (ImageView)convertView.findViewById(R.id.friend_listitem_userface);
			listItemView.gender = (ImageView)convertView.findViewById(R.id.friend_listitem_gender);
			
			//鐠佸墽鐤嗛幒褌娆㈤梿鍡楀煂convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		
		//鐠佸墽鐤嗛弬鍥х摟閸滃苯娴橀悧锟�		
		Friend friend = listItems.get(position);
		
		listItemView.name.setText(friend.getName());
		listItemView.name.setTag(friend);//鐠佸墽鐤嗛梾鎰閸欏倹鏆�鐎圭偘缍嬬猾锟�
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