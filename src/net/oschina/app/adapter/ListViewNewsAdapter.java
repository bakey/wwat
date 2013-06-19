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
 * 閺備即妞堢挧鍕唵Adapter缁拷
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ListViewNewsAdapter extends BaseAdapter {
	private Context 					context;//鏉╂劘顢戞稉濠佺瑓閺傦拷
	private List<News> 					listItems;//閺佺増宓侀梿鍡楁値
	private LayoutInflater 				listContainer;//鐟欏棗娴樼�鐟版珤
	private int 						itemViewResource;//閼奉亜鐣炬稊澶愩�鐟欏棗娴樺┃锟�
	static class ListItemView{				//閼奉亜鐣炬稊澶嬪付娴犲爼娉﹂崥锟�
	        public TextView title;  
		    public TextView author;
		    public TextView date;  
		    public TextView count;
		    public ImageView flag;
	 }  

	/**
	 * 鐎圭偘绶ラ崠鏈卍apter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewNewsAdapter(Context context, List<News> data,int resource) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//閸掓稑缂撶憴鍡楁禈鐎圭懓娅掗獮鎯邦啎缂冾喕绗傛稉瀣瀮
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
			listItemView.title = (TextView)convertView.findViewById(R.id.news_listitem_title);
			listItemView.author = (TextView)convertView.findViewById(R.id.news_listitem_author);
			listItemView.count= (TextView)convertView.findViewById(R.id.news_listitem_commentCount);
			listItemView.date= (TextView)convertView.findViewById(R.id.news_listitem_date);
			listItemView.flag= (ImageView)convertView.findViewById(R.id.news_listitem_flag);
			
			//鐠佸墽鐤嗛幒褌娆㈤梿鍡楀煂convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		
		//鐠佸墽鐤嗛弬鍥х摟閸滃苯娴橀悧锟�		
		News news = listItems.get(position);
		
		listItemView.title.setText(news.getTitle());
		listItemView.title.setTag(news);//鐠佸墽鐤嗛梾鎰閸欏倹鏆�鐎圭偘缍嬬猾锟�
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