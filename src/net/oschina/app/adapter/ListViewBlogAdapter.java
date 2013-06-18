package net.oschina.app.adapter;

import java.util.List;

import com.hkzhe.wwtt.R;
import net.oschina.app.bean.Blog;
import net.oschina.app.bean.BlogList;
import net.oschina.app.common.StringUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 鐢ㄦ埛鍗氬Adapter绫�
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewBlogAdapter extends BaseAdapter {
	private Context 					context;//杩愯涓婁笅鏂�
	private List<Blog> 					listItems;//鏁版嵁闆嗗悎
	private LayoutInflater 				listContainer;//瑙嗗浘瀹瑰櫒
	private int 						itemViewResource;//鑷畾涔夐」瑙嗗浘婧�
	private int							blogtype;
	static class ListItemView{				//鑷畾涔夋帶浠堕泦鍚� 
	        public TextView title;
	        public TextView author;
		    public TextView date;  
		    public TextView count;
		    public ImageView type;
	 }  

	/**
	 * 瀹炰緥鍖朅dapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewBlogAdapter(Context context, int blogtype, List<Blog> data, int resource) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//鍒涘缓瑙嗗浘瀹瑰櫒骞惰缃笂涓嬫枃
		this.itemViewResource = resource;
		this.listItems = data;
		this.blogtype = blogtype;
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
			listItemView.title = (TextView)convertView.findViewById(R.id.blog_listitem_title);
			listItemView.author = (TextView)convertView.findViewById(R.id.blog_listitem_author);
			listItemView.count = (TextView)convertView.findViewById(R.id.blog_listitem_commentCount);
			listItemView.date = (TextView)convertView.findViewById(R.id.blog_listitem_date);
			listItemView.type = (ImageView)convertView.findViewById(R.id.blog_listitem_documentType);
			
			//璁剧疆鎺т欢闆嗗埌convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		
		//璁剧疆鏂囧瓧鍜屽浘鐗�
		Blog blog = listItems.get(position);
		
		listItemView.title.setText(blog.getTitle());
		listItemView.title.setTag(blog);//璁剧疆闅愯棌鍙傛暟(瀹炰綋绫�
		listItemView.date.setText(StringUtils.friendly_time(blog.getPubDate()));
		listItemView.count.setText(blog.getCommentCount()+"");
		if(blog.getDocumentType() == Blog.DOC_TYPE_ORIGINAL)
			listItemView.type.setImageResource(R.drawable.widget_original_icon);
		else
			listItemView.type.setImageResource(R.drawable.widget_repaste_icon);
		
		if(blogtype == BlogList.CATALOG_USER){
			listItemView.author.setVisibility(View.GONE);
		}else{
			listItemView.author.setText(blog.getAuthor()+"   鍙戣〃浜�");
		}
		
		return convertView;
	}
}