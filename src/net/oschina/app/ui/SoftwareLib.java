package net.oschina.app.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import com.hkzhe.wwtt.R;
import net.oschina.app.adapter.ListViewSoftwareAdapter;
import net.oschina.app.adapter.ListViewSoftwareCatalogAdapter;
import net.oschina.app.bean.Notice;
import net.oschina.app.bean.SoftwareCatalogList;
import net.oschina.app.bean.SoftwareCatalogList.SoftwareType;
import net.oschina.app.bean.SoftwareList;
import net.oschina.app.bean.SoftwareList.Software;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import net.oschina.app.widget.PullToRefreshListView;
import net.oschina.app.widget.ScrollLayout;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 鏉烆垯娆㈡惔锟� * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class SoftwareLib extends Activity{
	
	private ImageView mBack;
	private TextView mTitle;
	private ProgressBar mProgressbar;
	private ScrollLayout mScrollLayout;	
	
	private Button software_catalog;
	private Button software_recommend;
	private Button software_lastest;
	private Button software_hot;
	private Button software_china;
	
	private PullToRefreshListView mlvSoftware;
	private ListViewSoftwareAdapter lvSoftwareAdapter;
	private List<Software> lvSoftwareData = new ArrayList<Software>();
	private View lvSoftware_footer;
	private TextView lvSoftware_foot_more;
	private ProgressBar lvSoftware_foot_progress;
    private Handler mSoftwareHandler;
    private int lvSumData;
	
	private ListView mlvSoftwareCatalog;
	private ListViewSoftwareCatalogAdapter lvSoftwareCatalogAdapter;
	private List<SoftwareType> lvSoftwareCatalogData = new ArrayList<SoftwareType>();
    private Handler mSoftwareCatalogHandler;
    
	private ListView mlvSoftwareTag;
	private ListViewSoftwareCatalogAdapter lvSoftwareTagAdapter;
	private List<SoftwareType> lvSoftwareTagData = new ArrayList<SoftwareType>();
    private Handler mSoftwareTagHandler;
    
	private int curHeadTag = HEAD_TAG_CATALOG;//姒涙顓婚崚婵嗩瀶婢舵挳鍎撮弽鍥╊劮
	private int curScreen = SCREEN_CATALOG;//姒涙顓昏ぐ鎾冲鐏炲繐绠�
	private int curSearchTag;//瑜版挸澧犳禍宀�獓閸掑棛琚惃鍑綼g
	private int curLvSoftwareDataState;
	private String curTitleLV1;//瑜版挸澧犳稉锟介獓閸掑棛琚弽鍥暯
    
	private final static int HEAD_TAG_CATALOG = 0x001;
	private final static int HEAD_TAG_RECOMMEND = 0x002;
	private final static int HEAD_TAG_LASTEST = 0x003;
	private final static int HEAD_TAG_HOT = 0x004;
	private final static int HEAD_TAG_CHINA = 0x005;
	
	private final static int DATA_LOAD_ING = 0x001;
	private final static int DATA_LOAD_COMPLETE = 0x002;
	
	private final static int SCREEN_CATALOG = 0;
	private final static int SCREEN_TAG = 1;
	private final static int SCREEN_SOFTWARE = 2;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_software);
        
        this.initView();
        
        this.initData();
	}
	
	//閸掓繂顬婇崠鏍瀰閸ョ偓甯舵禒锟�  
	private void initView()
    {
    	mBack = (ImageView)findViewById(R.id.frame_software_head_back);
    	mTitle = (TextView)findViewById(R.id.frame_software_head_title);
    	mProgressbar = (ProgressBar)findViewById(R.id.frame_software_head_progress);
    	mScrollLayout = (ScrollLayout) findViewById(R.id.frame_software_scrolllayout);
    	
    	mBack.setOnClickListener(backClickListener);
    	
    	//缁備胶鏁ゅ鎴濆З
        mScrollLayout.setIsScroll(false);
    	
    	software_catalog = (Button)findViewById(R.id.frame_btn_software_catalog);
    	software_recommend = (Button)findViewById(R.id.frame_btn_software_recommend);
    	software_lastest = (Button)findViewById(R.id.frame_btn_software_lastest);
    	software_hot = (Button)findViewById(R.id.frame_btn_software_hot);
    	software_china = (Button)findViewById(R.id.frame_btn_software_china);
    	
    	software_catalog.setOnClickListener(this.softwareBtnClick(software_catalog,HEAD_TAG_CATALOG,"瀵拷绨潪顖欐鎼达拷"));
    	software_recommend.setOnClickListener(this.softwareBtnClick(software_recommend,HEAD_TAG_RECOMMEND,"濮ｅ繐鎳嗛幒銊ㄥ礃鏉烆垯娆�"));
    	software_lastest.setOnClickListener(this.softwareBtnClick(software_lastest,HEAD_TAG_LASTEST,"閺堬拷鏌婃潪顖欐閸掓銆�"));
    	software_hot.setOnClickListener(this.softwareBtnClick(software_hot,HEAD_TAG_HOT,"閻戭參妫潪顖欐閸掓銆�"));
    	software_china.setOnClickListener(this.softwareBtnClick(software_china,HEAD_TAG_CHINA,"閸ユ垝楠囨潪顖欐閸掓銆�"));
    	
    	software_catalog.setEnabled(false);
    	
    	this.initSoftwareCatalogListView();
    	this.initSoftwareTagListView();
    	this.initSoftwareListView();
    }
    
    //閸掓繂顬婇崠鏍у瀻缁卑istview
    private void initSoftwareCatalogListView()
    {
    	lvSoftwareCatalogAdapter = new ListViewSoftwareCatalogAdapter(this, lvSoftwareCatalogData, R.layout.softwarecatalog_listitem); 
    	mlvSoftwareCatalog = (ListView)findViewById(R.id.frame_software_listview_catalog);
    	mlvSoftwareCatalog.setAdapter(lvSoftwareCatalogAdapter); 
    	mlvSoftwareCatalog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {       		
    			TextView name = (TextView)view.findViewById(R.id.softwarecatalog_listitem_name);
        		SoftwareType type = (SoftwareType)name.getTag();
        		
        		if(type == null) return;
        		
        		if(type.tag > 0){
        			curTitleLV1 = type.name;
        			mTitle.setText(curTitleLV1);
        			//閸旂姾娴囨禍宀�獓閸掑棛琚�
        			curScreen = SCREEN_TAG;
        			mScrollLayout.scrollToScreen(curScreen);
        			loadLvSoftwareCatalogData(type.tag, mSoftwareTagHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
        		}
        	}
		});
    	
    	mSoftwareCatalogHandler = new Handler()
		{
			public void handleMessage(Message msg) {
				
				headButtonSwitch(DATA_LOAD_COMPLETE);

				if(msg.what >= 0){						
					SoftwareCatalogList list = (SoftwareCatalogList)msg.obj;
					Notice notice = list.getNotice();
					//婢跺嫮鎮妉istview閺佺増宓�
					switch (msg.arg1) {
					case UIHelper.LISTVIEW_ACTION_INIT:
					case UIHelper.LISTVIEW_ACTION_REFRESH:
					case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
						lvSoftwareCatalogData.clear();//閸忓牊绔婚梽銈呭斧閺堝鏆熼幑锟�						lvSoftwareCatalogData.addAll(list.getSoftwareTypelist());
						break;
					case UIHelper.LISTVIEW_ACTION_SCROLL:
						break;
					}	
					
					lvSoftwareCatalogAdapter.notifyDataSetChanged();

					//閸欐垿锟介柅姘辩叀楠炴寧鎸�
					if(notice != null){
						UIHelper.sendBroadCast(SoftwareLib.this, notice);
					}
				}
				else if(msg.what == -1){
					//閺堝绱撶敮锟�閺勫墽銇氶崝鐘烘祰閸戞椽鏁�& 瀵懓鍤柨娆掝嚖濞戝牊浼�
					((AppException)msg.obj).makeToast(SoftwareLib.this);
				}
			}
		};
    }
    
    //閸掓繂顬婇崠鏍︾癌缁狙冨瀻缁卑istview
    private void initSoftwareTagListView()
    {
    	lvSoftwareTagAdapter = new ListViewSoftwareCatalogAdapter(this, lvSoftwareTagData, R.layout.softwarecatalog_listitem); 
    	mlvSoftwareTag = (ListView)findViewById(R.id.frame_software_listview_tag);
    	mlvSoftwareTag.setAdapter(lvSoftwareTagAdapter); 
    	mlvSoftwareTag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {       		
        		TextView name = (TextView)view.findViewById(R.id.softwarecatalog_listitem_name);
        		SoftwareType type = (SoftwareType)name.getTag();
        		
        		if(type == null) return;
        		
        		if(type.tag > 0){
        			mTitle.setText(type.name);
        			//閸旂姾娴囨潪顖欐閸掓銆�
        			curScreen = SCREEN_SOFTWARE;
        			mScrollLayout.scrollToScreen(curScreen);
        			curSearchTag = type.tag;
        			loadLvSoftwareTagData(curSearchTag, 0, mSoftwareHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
        		}
        	}
		});
    	
    	mSoftwareTagHandler = new Handler()
		{
			public void handleMessage(Message msg) {
				
				headButtonSwitch(DATA_LOAD_COMPLETE);

				if(msg.what >= 0){						
					SoftwareCatalogList list = (SoftwareCatalogList)msg.obj;
					Notice notice = list.getNotice();
					//婢跺嫮鎮妉istview閺佺増宓�
					switch (msg.arg1) {
					case UIHelper.LISTVIEW_ACTION_INIT:
					case UIHelper.LISTVIEW_ACTION_REFRESH:
					case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
						lvSoftwareTagData.clear();//閸忓牊绔婚梽銈呭斧閺堝鏆熼幑锟�						lvSoftwareTagData.addAll(list.getSoftwareTypelist());
						break;
					case UIHelper.LISTVIEW_ACTION_SCROLL:
						break;
					}	
					
					lvSoftwareTagAdapter.notifyDataSetChanged();

					//閸欐垿锟介柅姘辩叀楠炴寧鎸�
					if(notice != null){
						UIHelper.sendBroadCast(SoftwareLib.this, notice);
					}
				}
				else if(msg.what == -1){
					//閺堝绱撶敮锟�閺勫墽銇氶崝鐘烘祰閸戞椽鏁�& 瀵懓鍤柨娆掝嚖濞戝牊浼�
					((AppException)msg.obj).makeToast(SoftwareLib.this);
				}
			}
		};
    }
    
    //閸掓繂顬婇崠鏍拫娴犵istview
    private void initSoftwareListView()
    {
    	lvSoftware_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
    	lvSoftware_foot_more = (TextView)lvSoftware_footer.findViewById(R.id.listview_foot_more);
    	lvSoftware_foot_progress = (ProgressBar)lvSoftware_footer.findViewById(R.id.listview_foot_progress);

    	lvSoftwareAdapter = new ListViewSoftwareAdapter(this, lvSoftwareData, R.layout.software_listitem); 
    	mlvSoftware = (PullToRefreshListView)findViewById(R.id.frame_software_listview);
    	
    	mlvSoftware.addFooterView(lvSoftware_footer);//濞ｈ濮炴惔鏇㈠劥鐟欏棗娴� 韫囧懘銆忛崷鈺痚tAdapter閸擄拷
    	mlvSoftware.setAdapter(lvSoftwareAdapter); 
    	mlvSoftware.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		//閻愮懓鍤径鎾劥閵嗕礁绨抽柈銊︾埉閺冪姵鏅�
        		if(position == 0 || view == lvSoftware_footer) return;
        		        		
    			TextView name = (TextView)view.findViewById(R.id.software_listitem_name);
    			Software sw = (Software)name.getTag();

        		if(sw == null) return;
        		
        		//鐠哄疇娴�
        		UIHelper.showUrlRedirect(view.getContext(), sw.url);
        	}
		});
    	mlvSoftware.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				mlvSoftware.onScrollStateChanged(view, scrollState);
				
				//閺佺増宓佹稉铏光敄--娑撳秶鏁ょ紒褏鐢绘稉瀣桨娴狅絿鐖滄禍锟�				if(lvSoftwareData.size() == 0) return;
				
				//閸掋倖鏌囬弰顖氭儊濠婃艾濮╅崚鏉跨俺闁拷
				boolean scrollEnd = false;
				try {
					if(view.getPositionForView(lvSoftware_footer) == view.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				
				if(scrollEnd && curLvSoftwareDataState==UIHelper.LISTVIEW_DATA_MORE)
				{
					lvSoftware_foot_more.setText(R.string.load_ing);
					lvSoftware_foot_progress.setVisibility(View.VISIBLE);
					//瑜版挸澧爌ageIndex
					int pageIndex = lvSumData/20;
					if(curHeadTag == HEAD_TAG_CATALOG)
						loadLvSoftwareTagData(curSearchTag, pageIndex, mSoftwareHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
					else
						loadLvSoftwareData(curHeadTag, pageIndex, mSoftwareHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
				mlvSoftware.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
    	mlvSoftware.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			public void onRefresh() {
				if(curHeadTag == HEAD_TAG_CATALOG)
					loadLvSoftwareTagData(curSearchTag, 0, mSoftwareHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
				else
					loadLvSoftwareData(curHeadTag, 0, mSoftwareHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });
    	
      	mSoftwareHandler = new Handler()
		{
			public void handleMessage(Message msg) {
				
				headButtonSwitch(DATA_LOAD_COMPLETE);

				if(msg.what >= 0){						
					SoftwareList list = (SoftwareList)msg.obj;
					Notice notice = list.getNotice();
					//婢跺嫮鎮妉istview閺佺増宓�
					switch (msg.arg1) {
					case UIHelper.LISTVIEW_ACTION_INIT:
					case UIHelper.LISTVIEW_ACTION_REFRESH:
					case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
						lvSumData = msg.what;
						lvSoftwareData.clear();//閸忓牊绔婚梽銈呭斧閺堝鏆熼幑锟�						lvSoftwareData.addAll(list.getSoftwarelist());
						break;
					case UIHelper.LISTVIEW_ACTION_SCROLL:
						lvSumData += msg.what;
						if(lvSoftwareData.size() > 0){
							for(Software sw1 : list.getSoftwarelist()){
								boolean b = false;
								for(Software sw2 : lvSoftwareData){
									if(sw1.name.equals(sw2.name)){
										b = true;
										break;
									}
								}
								if(!b) lvSoftwareData.add(sw1);
							}
						}else{
							lvSoftwareData.addAll(list.getSoftwarelist());
						}
						break;
					}	
					
					if(msg.what < 20){
						curLvSoftwareDataState = UIHelper.LISTVIEW_DATA_FULL;
						lvSoftwareAdapter.notifyDataSetChanged();
						lvSoftware_foot_more.setText(R.string.load_full);
					}else if(msg.what == 20){					
						curLvSoftwareDataState = UIHelper.LISTVIEW_DATA_MORE;
						lvSoftwareAdapter.notifyDataSetChanged();
						lvSoftware_foot_more.setText(R.string.load_more);
					}
					//閸欐垿锟介柅姘辩叀楠炴寧鎸�
					if(notice != null){
						UIHelper.sendBroadCast(SoftwareLib.this, notice);
					}
				}
				else if(msg.what == -1){
					//閺堝绱撶敮锟�閺勫墽銇氶崝鐘烘祰閸戞椽鏁�& 瀵懓鍤柨娆掝嚖濞戝牊浼�
					curLvSoftwareDataState = UIHelper.LISTVIEW_DATA_MORE;
					lvSoftware_foot_more.setText(R.string.load_error);
					((AppException)msg.obj).makeToast(SoftwareLib.this);
				}
				if(lvSoftwareData.size()==0){
					curLvSoftwareDataState = UIHelper.LISTVIEW_DATA_EMPTY;
					lvSoftware_foot_more.setText(R.string.load_empty);
				}
				lvSoftware_foot_progress.setVisibility(View.GONE);
				if(msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH){
					mlvSoftware.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
					mlvSoftware.setSelection(0);
				}else if(msg.arg1 == UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG){
					mlvSoftware.onRefreshComplete();
					mlvSoftware.setSelection(0);
				}
			}
		};
    }
    
    //閸掓繂顬婇崠鏍ㄥ付娴犺埖鏆熼幑锟�
 	private void initData()
  	{
  		this.loadLvSoftwareCatalogData(0, mSoftwareCatalogHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
  	}
  	
  	/**
     * 婢舵挳鍎撮幐澶愭尦鐏炴洜銇�
     * @param type
     */
    private void headButtonSwitch(int type) {
    	switch (type) {
    	case DATA_LOAD_ING:
			mProgressbar.setVisibility(View.VISIBLE);
			break;
		case DATA_LOAD_COMPLETE:
			mProgressbar.setVisibility(View.GONE);
			break;
		}
    }
  	
  	private View.OnClickListener softwareBtnClick(final Button btn,final int tag,final String title){
    	return new View.OnClickListener() {
			public void onClick(View v) {
		    	if(btn == software_catalog)
		    		software_catalog.setEnabled(false);
		    	else
		    		software_catalog.setEnabled(true);
		    	if(btn == software_recommend)
		    		software_recommend.setEnabled(false);
		    	else
		    		software_recommend.setEnabled(true);
		    	if(btn == software_lastest)
		    		software_lastest.setEnabled(false);
		    	else
		    		software_lastest.setEnabled(true);	
		    	if(btn == software_hot)
		    		software_hot.setEnabled(false);
		    	else
		    		software_hot.setEnabled(true);
		    	if(btn == software_china)
		    		software_china.setEnabled(false);
		    	else
		    		software_china.setEnabled(true);	    	
		    	
				curHeadTag = tag;	
		    	
		    	if(btn == software_catalog)
		    	{			    		
		    		curScreen = SCREEN_CATALOG;
		    		if(lvSoftwareCatalogData.size() == 0)
		    			loadLvSoftwareCatalogData(0, mSoftwareCatalogHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);		
		    	}
		    	else
		    	{		    		
		    		curScreen = SCREEN_SOFTWARE;
		    		loadLvSoftwareData(tag, 0, mSoftwareHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);	
		    	}			
	    	
		    	mTitle.setText(title);
		    	mScrollLayout.setToScreen(curScreen);
			}
		};
    }
	
  	/**
     * 缁捐法鈻奸崝鐘烘祰鏉烆垯娆㈤崚鍡欒閸掓銆冮弫鐗堝祦
     * @param tag 缁楊兛绔寸痪锟� 缁楊兛绨╃痪锟絫ag
     * @param handler 婢跺嫮鎮婇崳锟�     * @param action 閸斻劋缍旈弽鍥槕
     */
	private void loadLvSoftwareCatalogData(final int tag,final Handler handler,final int action){  
		headButtonSwitch(DATA_LOAD_ING);
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {
					SoftwareCatalogList softwareCatalogList = ((AppContext)getApplication()).getSoftwareCatalogList(tag);
					msg.what = softwareCatalogList.getSoftwareTypelist().size();
					msg.obj = softwareCatalogList;
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
				msg.arg1 = action;//閸涘﹦鐓andler瑜版挸澧燼ction
                handler.sendMessage(msg);
			}
		}.start();
	}
	
  	/**
     * 缁捐法鈻奸崝鐘烘祰鏉烆垯娆㈤崚鍡欒娴滃瞼楠囬崚妤勩�閺佺増宓�
     * @param tag 缁楊兛绔寸痪锟� 缁楊兛绨╃痪锟絫ag
     * @param handler 婢跺嫮鎮婇崳锟�     * @param action 閸斻劋缍旈弽鍥槕
     */
	private void loadLvSoftwareTagData(final int searchTag,final int pageIndex,final Handler handler,final int action){  
		headButtonSwitch(DATA_LOAD_ING);
		new Thread(){
			public void run() {
				Message msg = new Message();
				boolean isRefresh = false;
				if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {
					SoftwareList softwareList = ((AppContext)getApplication()).getSoftwareTagList(searchTag, pageIndex, isRefresh);
					msg.what = softwareList.getSoftwarelist().size();
					msg.obj = softwareList;
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
				msg.arg1 = action;//閸涘﹦鐓andler瑜版挸澧燼ction
                handler.sendMessage(msg);
			}
		}.start();
	}
	
  	/**
     * 缁捐法鈻奸崝鐘烘祰鏉烆垯娆㈤崚妤勩�閺佺増宓�
     * @param searchTag 鏉烆垯娆㈤崚鍡欒 閹恒劏宕�recommend 閺堬拷鏌�time 閻戭參妫�view 閸ユ垝楠�list_cn
     * @param pageIndex 瑜版挸澧犳い鍨殶
     * @param handler 婢跺嫮鎮婇崳锟�     * @param action 閸斻劋缍旈弽鍥槕
     */
	private void loadLvSoftwareData(final int tag,final int pageIndex,final Handler handler,final int action){  
		
		String _searchTag = "";
		
		switch (tag) {
		case HEAD_TAG_RECOMMEND: 
			_searchTag = SoftwareList.TAG_RECOMMEND;
			break;
		case HEAD_TAG_LASTEST: 
			_searchTag = SoftwareList.TAG_LASTEST;
			break;
		case HEAD_TAG_HOT: 
			_searchTag = SoftwareList.TAG_HOT;
			break;
		case HEAD_TAG_CHINA: 
			_searchTag = SoftwareList.TAG_CHINA;
			break;
		}
		
		if(StringUtils.isEmpty(_searchTag)) return;		
		
		final String searchTag = _searchTag;
		
		headButtonSwitch(DATA_LOAD_ING);
		
		new Thread(){
			public void run() {
				Message msg = new Message();
				boolean isRefresh = false;
				if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {
					SoftwareList softwareList = ((AppContext)getApplication()).getSoftwareList(searchTag, pageIndex, isRefresh);
					msg.what = softwareList.getPageSize();
					msg.obj = softwareList;
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
				msg.arg1 = action;//閸涘﹦鐓andler瑜版挸澧燼ction
				if(curHeadTag == tag)
					handler.sendMessage(msg);
			}
		}.start();
	} 
	
	/**
	 * 鏉╂柨娲栨禍瀣╂
	 */
	private void back() {
		if(curHeadTag == HEAD_TAG_CATALOG) {
			switch (curScreen) {
			case SCREEN_SOFTWARE:
    			mTitle.setText(curTitleLV1);
				curScreen = SCREEN_TAG;
				mScrollLayout.scrollToScreen(SCREEN_TAG);
				break;
			case SCREEN_TAG:
				mTitle.setText("瀵拷绨潪顖欐鎼达拷");
				curScreen = SCREEN_CATALOG;
				mScrollLayout.scrollToScreen(SCREEN_CATALOG);
				break;
			case SCREEN_CATALOG:
				finish();
				break;
			}
			
		}else{
			finish();
		}
	}

	private View.OnClickListener backClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			back();
		}		
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			back();
			return true;
		}
		return false;
	}
	
	
}
