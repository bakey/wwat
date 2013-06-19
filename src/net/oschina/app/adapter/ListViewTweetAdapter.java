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
 * 閸斻劌鑴夾dapter缁拷
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewTweetAdapter extends BaseAdapter {
	private Context 					context;//鏉╂劘顢戞稉濠佺瑓閺傦拷
	private List<Tweet> 				listItems;//閺佺増宓侀梿鍡楁値
	private LayoutInflater 				listContainer;//鐟欏棗娴樼�鐟版珤
	private int 						itemViewResource;//閼奉亜鐣炬稊澶愩�鐟欏棗娴樺┃锟�	
	private BitmapManager 				bmpManager;
	static class ListItemView{				//閼奉亜鐣炬稊澶嬪付娴犲爼娉﹂崥锟�
			public ImageView userface;  
	        public TextView username;  
		    public TextView date;  
		    public TextView content;
		    public TextView commentCount;
		    public TextView client;
		    public ImageView image;
	 }  

	/**
	 * 鐎圭偘绶ラ崠鏈卍apter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewTweetAdapter(Context context, List<Tweet> data,int resource) {
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
			listItemView.userface = (ImageView)convertView.findViewById(R.id.tweet_listitem_userface);
			listItemView.username = (TextView)convertView.findViewById(R.id.tweet_listitem_username);
			listItemView.content = (TextView)convertView.findViewById(R.id.tweet_listitem_content);
			listItemView.image= (ImageView)convertView.findViewById(R.id.tweet_listitem_image);
			listItemView.date= (TextView)convertView.findViewById(R.id.tweet_listitem_date);
			listItemView.commentCount= (TextView)convertView.findViewById(R.id.tweet_listitem_commentCount);
			listItemView.client= (TextView)convertView.findViewById(R.id.tweet_listitem_client);
			
			//鐠佸墽鐤嗛幒褌娆㈤梿鍡楀煂convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}
				
		//鐠佸墽鐤嗛弬鍥х摟閸滃苯娴橀悧锟�	
		Tweet tweet = listItems.get(position);
		listItemView.username.setText(tweet.getAuthor());
		listItemView.username.setTag(tweet);//鐠佸墽鐤嗛梾鎰閸欏倹鏆�鐎圭偘缍嬬猾锟�
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
				listItemView.client.setText("閺夈儴鍤�閹靛婧�");
				break;
			case 3:
				listItemView.client.setText("閺夈儴鍤�Android");
				break;
			case 4:
				listItemView.client.setText("閺夈儴鍤�iPhone");
				break;
			case 5:
				listItemView.client.setText("閺夈儴鍤�Windows Phone");
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