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
 * 閻劍鍩涢崡姘吂Adapter缁拷
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewBlogAdapter extends BaseAdapter {
	private Context 					context;//鏉╂劘顢戞稉濠佺瑓閺傦拷
	private List<Blog> 					listItems;//閺佺増宓侀梿鍡楁値
	private LayoutInflater 				listContainer;//鐟欏棗娴樼�鐟版珤
	private int 						itemViewResource;//閼奉亜鐣炬稊澶愩�鐟欏棗娴樺┃锟�
	private int							blogtype;
	static class ListItemView{				//閼奉亜鐣炬稊澶嬪付娴犲爼娉﹂崥锟�
	        public TextView title;
	        public TextView author;
		    public TextView date;  
		    public TextView count;
		    public ImageView type;
	 }  

	/**
	 * 鐎圭偘绶ラ崠鏈卍apter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewBlogAdapter(Context context, int blogtype, List<Blog> data, int resource) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//閸掓稑缂撶憴鍡楁禈鐎圭懓娅掗獮鎯邦啎缂冾喕绗傛稉瀣瀮
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
			listItemView.title = (TextView)convertView.findViewById(R.id.blog_listitem_title);
			listItemView.author = (TextView)convertView.findViewById(R.id.blog_listitem_author);
			listItemView.count = (TextView)convertView.findViewById(R.id.blog_listitem_commentCount);
			listItemView.date = (TextView)convertView.findViewById(R.id.blog_listitem_date);
			listItemView.type = (ImageView)convertView.findViewById(R.id.blog_listitem_documentType);
			
			//鐠佸墽鐤嗛幒褌娆㈤梿鍡楀煂convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		
		//鐠佸墽鐤嗛弬鍥х摟閸滃苯娴橀悧锟�		
		Blog blog = listItems.get(position);
		
		listItemView.title.setText(blog.getTitle());
		listItemView.title.setTag(blog);//鐠佸墽鐤嗛梾鎰閸欏倹鏆�鐎圭偘缍嬬猾锟�
		listItemView.date.setText(StringUtils.friendly_time(blog.getPubDate()));
		listItemView.count.setText(blog.getCommentCount()+"");
		if(blog.getDocumentType() == Blog.DOC_TYPE_ORIGINAL)
			listItemView.type.setImageResource(R.drawable.widget_original_icon);
		else
			listItemView.type.setImageResource(R.drawable.widget_repaste_icon);
		
		if(blogtype == BlogList.CATALOG_USER){
			listItemView.author.setVisibility(View.GONE);
		}else{
			listItemView.author.setText(blog.getAuthor()+"   閸欐垼銆冩禍锟�");
		}
		
		return convertView;
	}
}