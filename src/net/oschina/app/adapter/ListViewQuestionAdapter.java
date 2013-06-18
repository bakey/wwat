package net.oschina.app.adapter;

import java.util.List;

import com.hkzhe.wwtt.R;
import net.oschina.app.bean.Post;
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
 * 闂瓟Adapter绫�
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewQuestionAdapter extends BaseAdapter {
	private Context 					context;//杩愯涓婁笅鏂�
	private List<Post> 					listItems;//鏁版嵁闆嗗悎
	private LayoutInflater 				listContainer;//瑙嗗浘瀹瑰櫒
	private int 						itemViewResource;//鑷畾涔夐」瑙嗗浘婧�
	private BitmapManager 				bmpManager;
	static class ListItemView{				//鑷畾涔夋帶浠堕泦鍚� 
			public ImageView face;
	        public TextView title;  
		    public TextView author;
		    public TextView date;  
		    public TextView count;
	 }  

	/**
	 * 瀹炰緥鍖朅dapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewQuestionAdapter(Context context, List<Post> data,int resource) {
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
			listItemView.face = (ImageView)convertView.findViewById(R.id.question_listitem_userface);
			listItemView.title = (TextView)convertView.findViewById(R.id.question_listitem_title);
			listItemView.author = (TextView)convertView.findViewById(R.id.question_listitem_author);
			listItemView.count= (TextView)convertView.findViewById(R.id.question_listitem_count);
			listItemView.date= (TextView)convertView.findViewById(R.id.question_listitem_date);
			
			//璁剧疆鎺т欢闆嗗埌convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
      
		//璁剧疆鏂囧瓧鍜屽浘鐗�
		Post post = listItems.get(position);
		String faceURL = post.getFace();
		if(faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL)){
			listItemView.face.setImageResource(R.drawable.widget_dface);
		}else{
			bmpManager.loadBitmap(faceURL, listItemView.face);
		}
		listItemView.face.setOnClickListener(faceClickListener);
		listItemView.face.setTag(post);
		
		listItemView.title.setText(post.getTitle());
		listItemView.title.setTag(post);//璁剧疆闅愯棌鍙傛暟(瀹炰綋绫�
		listItemView.author.setText(post.getAuthor());
		listItemView.date.setText(StringUtils.friendly_time(post.getPubDate()));
		listItemView.count.setText(post.getAnswerCount()+"鍥瀨"+post.getViewCount()+"闃�");
		
		return convertView;
	}
	
	private View.OnClickListener faceClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			Post post = (Post)v.getTag();
			UIHelper.showUserCenter(v.getContext(), post.getAuthorId(), post.getAuthor());
		}
	};
}