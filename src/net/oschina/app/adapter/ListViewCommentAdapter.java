package net.oschina.app.adapter;

import java.util.List;

import com.hkzhe.wwtt.R;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.Comment.Refer;
import net.oschina.app.bean.Comment.Reply;
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
 * 閻劍鍩涚拠鍕啈Adapter缁拷
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewCommentAdapter extends BaseAdapter {
	private Context 					context;//鏉╂劘顢戞稉濠佺瑓閺傦拷
	private List<Comment> 				listItems;//閺佺増宓侀梿鍡楁値
	private LayoutInflater 				listContainer;//鐟欏棗娴樼�鐟版珤
	private int 						itemViewResource;//閼奉亜鐣炬稊澶愩�鐟欏棗娴樺┃锟�
	private BitmapManager 				bmpManager;
	static class ListItemView{				//閼奉亜鐣炬稊澶嬪付娴犲爼娉﹂崥锟�		
			public ImageView face;
	        public TextView name;  
		    public TextView date;  
		    public LinkView content;
		    public TextView client;
		    public LinearLayout relies;
		    public LinearLayout refers;
	 }  

	/**
	 * 鐎圭偘绶ラ崠鏈卍apter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewCommentAdapter(Context context, List<Comment> data,int resource) {
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
			listItemView.face = (ImageView)convertView.findViewById(R.id.comment_listitem_userface);
			listItemView.name = (TextView)convertView.findViewById(R.id.comment_listitem_username);
			listItemView.date = (TextView)convertView.findViewById(R.id.comment_listitem_date);
			listItemView.content = (LinkView)convertView.findViewById(R.id.comment_listitem_content);
			listItemView.client= (TextView)convertView.findViewById(R.id.comment_listitem_client);
			listItemView.relies = (LinearLayout)convertView.findViewById(R.id.comment_listitem_relies);
			listItemView.refers = (LinearLayout)convertView.findViewById(R.id.comment_listitem_refers);
			
			//鐠佸墽鐤嗛幒褌娆㈤梿鍡楀煂convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		
		//鐠佸墽鐤嗛弬鍥х摟閸滃苯娴橀悧锟�	
		Comment comment = listItems.get(position);
		String faceURL = comment.getFace();
		if(faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL)){
			listItemView.face.setImageResource(R.drawable.widget_dface);
		}else{
			bmpManager.loadBitmap(faceURL, listItemView.face);
		}
		listItemView.face.setTag(comment);//鐠佸墽鐤嗛梾鎰閸欏倹鏆�鐎圭偘缍嬬猾锟�
		listItemView.face.setOnClickListener(faceClickListener);
		listItemView.name.setText(comment.getAuthor());
		listItemView.date.setText(StringUtils.friendly_time(comment.getPubDate()));
		listItemView.content.setText(comment.getContent());
		listItemView.content.parseLinkText();
		listItemView.content.setTag(comment);//鐠佸墽鐤嗛梾鎰閸欏倹鏆�鐎圭偘缍嬬猾锟�
		
		switch(comment.getAppClient())
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
		
		listItemView.relies.setVisibility(View.GONE);
		listItemView.relies.removeAllViews();//閸忓牊绔荤粚锟�		
		if(comment.getReplies().size() > 0){
			//鐠囧嫯顔戦弫鎵窗
			View view = listContainer.inflate(R.layout.comment_reply, null);
			TextView tv = (TextView)view.findViewById(R.id.comment_reply_content);
			tv.setText(context.getString(R.string.comment_reply_title, comment.getReplies().size()));
			listItemView.relies.addView(view);
			//鐠囧嫯顔戦崘鍛啇
			for(Reply reply : comment.getReplies()){
				View view2 = listContainer.inflate(R.layout.comment_reply, null);
				TextView tv2 = (TextView)view2.findViewById(R.id.comment_reply_content);
				tv2.setText(reply.rauthor+"("+StringUtils.friendly_time(reply.rpubDate)+")閿涳拷"+reply.rcontent);
				listItemView.relies.addView(view2);
			}
			listItemView.relies.setVisibility(View.VISIBLE);
		}
		
		listItemView.refers.setVisibility(View.GONE);
		listItemView.refers.removeAllViews();//閸忓牊绔荤粚锟�		
		if(comment.getRefers().size() > 0){
			//瀵洜鏁ら崘鍛啇
			for(Refer refer : comment.getRefers()){
				View view = listContainer.inflate(R.layout.comment_refer, null);
				TextView title = (TextView)view.findViewById(R.id.comment_refer_title);
				TextView body = (TextView)view.findViewById(R.id.comment_refer_body);
				title.setText(refer.refertitle);
				body.setText(refer.referbody);
				listItemView.refers.addView(view);
			}
			listItemView.refers.setVisibility(View.VISIBLE);
		}
		
		return convertView;
	}
	
	private View.OnClickListener faceClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			Comment comment = (Comment)v.getTag();
			UIHelper.showUserCenter(v.getContext(), comment.getAuthorId(), comment.getAuthor());
		}
	};
}