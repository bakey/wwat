package net.oschina.app.adapter;

import java.util.List;

import com.hkzhe.wwtt.R;
import net.oschina.app.bean.News;
import net.oschina.app.common.StringUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 鏂伴椈璧勮Adapter绫�
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewNewsAdapter extends BaseAdapter {
	private Context 					context;//杩愯涓婁笅鏂�
	private List<News> 					listItems;//鏁版嵁闆嗗悎
	private LayoutInflater 				listContainer;//瑙嗗浘瀹瑰櫒
	private int 						itemViewResource;//鑷畾涔夐」瑙嗗浘婧�
	static class ListItemView{				//鑷畾涔夋帶浠堕泦鍚� 
	        public TextView title;  
		    public TextView author;
		    public TextView date;  
		    public TextView count;
		    public ImageView flag;
	 }  

	/**
	 * 瀹炰緥鍖朅dapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewNewsAdapter(Context context, List<News> data,int resource) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//鍒涘缓瑙嗗浘瀹瑰櫒骞惰缃笂涓嬫枃
		this.itemViewResource = resource;
		this.listItems = data;
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
			listItemView.title = (TextView)convertView.findViewById(R.id.news_listitem_title);
			listItemView.author = (TextView)convertView.findViewById(R.id.news_listitem_author);
			listItemView.count= (TextView)convertView.findViewById(R.id.news_listitem_commentCount);
			listItemView.date= (TextView)convertView.findViewById(R.id.news_listitem_date);
			listItemView.flag= (ImageView)convertView.findViewById(R.id.news_listitem_flag);
			
			//璁剧疆鎺т欢闆嗗埌convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		
		//璁剧疆鏂囧瓧鍜屽浘鐗�
		News news = listItems.get(position);
		
		listItemView.title.setText(news.getTitle());
		listItemView.title.setTag(news);//璁剧疆闅愯棌鍙傛暟(瀹炰綋绫�
		listItemView.author.setText(news.getAuthor());
		listItemView.date.setText(StringUtils.friendly_time(news.getPubDate()));
		listItemView.count.setText(news.getCommentCount()+"");
		if(StringUtils.isToday(news.getPubDate()))
			listItemView.flag.setVisibility(View.VISIBLE);
		else
			listItemView.flag.setVisibility(View.GONE);
		
		return convertView;
	}
}