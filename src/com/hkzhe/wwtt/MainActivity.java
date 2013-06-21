package com.hkzhe.wwtt;

import greendroid.widget.QuickActionWidget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import net.oschina.app.AppManager;
import net.oschina.app.adapter.ListViewActiveAdapter;
import net.oschina.app.adapter.ListViewBlogAdapter;
import net.oschina.app.adapter.ListViewMessageAdapter;
import net.oschina.app.adapter.ListViewNewsAdapter;
import net.oschina.app.adapter.ListViewQuestionAdapter;
import net.oschina.app.adapter.ListViewTweetAdapter;
import net.oschina.app.bean.Active;
import net.oschina.app.bean.ActiveList;
import net.oschina.app.bean.Blog;
import net.oschina.app.bean.BlogList;
import net.oschina.app.bean.MessageList;
import net.oschina.app.bean.Messages;
import net.oschina.app.bean.News;
import net.oschina.app.bean.NewsList;
import net.oschina.app.bean.Notice;
import net.oschina.app.bean.Post;
import net.oschina.app.bean.PostList;
import net.oschina.app.bean.Result;
import net.oschina.app.bean.Tweet;
import net.oschina.app.bean.TweetList;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import net.oschina.app.ui.Main;
import net.oschina.app.widget.PullToRefreshListView;
import net.oschina.app.widget.ScrollLayout;

import com.hkzhe.wwtt.R;
import com.hkzhe.wwtt.views.Global;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.app.TabActivity;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;

public class MainActivity extends Activity  {
	private ScrollLayout mScrollLayout;	
	private RadioButton[] mButtons;
	private String[] mHeadTitles;
	private int mViewCount;
	private int mCurSel;
	
	private int curNewsCatalog = NewsList.CATALOG_ALL;
	private int curQuestionCatalog = PostList.CATALOG_ASK;
	private int curTweetCatalog = TweetList.CATALOG_LASTEST;
	private int curActiveCatalog = ActiveList.CATALOG_LASTEST;
	
	private QuickActionWidget mGrid;//������ؼ�
	
	private RadioButton fbNews;
	private RadioButton fbQuestion;
	private RadioButton fbTweet;
	private RadioButton fbactive;
	private ImageView fbSetting;
	
	private PullToRefreshListView lvNews;
	private PullToRefreshListView lvBlog;
	private PullToRefreshListView lvQuestion;
	private PullToRefreshListView lvTweet;
	private PullToRefreshListView lvActive;
	private PullToRefreshListView lvMsg;
	
	private AppContext appContext;
	
	private ImageView mHeadLogo;
	private TextView mHeadTitle;
	private ProgressBar mHeadProgress;
	private ImageButton mHead_search;
	private ImageButton mHeadPub_post;
	private ImageButton mHeadPub_tweet;
	
	private int lvNewsSumData;
	private int lvBlogSumData;
	private int lvQuestionSumData;
	private int lvTweetSumData;
	private int lvActiveSumData;
	private int lvMsgSumData;
	
	private List<News> lvNewsData = new ArrayList<News>();
	private List<Blog> lvBlogData = new ArrayList<Blog>();
	private List<Post> lvQuestionData = new ArrayList<Post>();
	private List<Tweet> lvTweetData = new ArrayList<Tweet>();
	private List<Active> lvActiveData = new ArrayList<Active>();
	private List<Messages> lvMsgData = new ArrayList<Messages>();
	
	private ListViewNewsAdapter lvNewsAdapter;
	private ListViewBlogAdapter lvBlogAdapter;
	private ListViewQuestionAdapter lvQuestionAdapter;
	private ListViewTweetAdapter lvTweetAdapter;
	private ListViewActiveAdapter lvActiveAdapter;
	private ListViewMessageAdapter lvMsgAdapter;
	
	private Handler lvNewsHandler;
	private Handler lvBlogHandler;
	private Handler lvQuestionHandler;
	private Handler lvTweetHandler;
	private Handler lvActiveHandler;
	private Handler lvMsgHandler;
	
	private View lvNews_footer;
	private View lvBlog_footer;
	private View lvQuestion_footer;
	private View lvTweet_footer;
	private View lvActive_footer;
	private View lvMsg_footer;
	
	private TextView lvNews_foot_more;
	private TextView lvBlog_foot_more;
	private TextView lvQuestion_foot_more;
	private TextView lvTweet_foot_more;
	private TextView lvActive_foot_more;
	private TextView lvMsg_foot_more;
	
	private ProgressBar lvNews_foot_progress;
	private ProgressBar lvBlog_foot_progress;
	private ProgressBar lvQuestion_foot_progress;
	private ProgressBar lvTweet_foot_progress;
	private ProgressBar lvActive_foot_progress;
	private ProgressBar lvMsg_foot_progress;
	
	private boolean isClearNotice = false;
	private int curClearNoticeType = 0;
	static String TAG = "wwtt";
	
	//private TabHost    mTabHost;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Log.d( TAG , "start create activity");
		AppManager.getAppManager().addActivity(this);		
		
		this.initHeadView();
		
		//GuideHelper guideHelper = new GuideHelper(this);
       // guideHelper.openGuide();
        
       /* setContentView( R.layout.main );
        mTabHost = this.getTabHost();   
        
        TabSpec homeTabSpec = mTabHost.newTabSpec( "Home" );
        homeTabSpec.setIndicator("", getResources().getDrawable(R.drawable.navigation_index_sel));
        Intent homeTabIntent = new Intent(this, HomeTabActivity.class);
        homeTabSpec.setContent(homeTabIntent);
        
        TabSpec CateSpec = mTabHost.newTabSpec( "Cate" );
        CateSpec.setIndicator("", getResources().getDrawable(R.drawable.navigation_cate_sel));
        Intent CateIntent = new Intent(this, CateTabActivity.class);
        CateSpec.setContent( CateIntent );
        
        TabSpec PocketSpec = mTabHost.newTabSpec( "Pocket" );
        PocketSpec.setIndicator("" , getResources().getDrawable(R.drawable.navigation_pocket_sel));
        Intent PocketIntent = new Intent( this , PocketTabActivity.class );
        PocketSpec.setContent( PocketIntent );
        
        TabSpec PlayingSpec = mTabHost.newTabSpec( "Playing" );
        ImageView playingView = new ImageView( this );
        Global.tabView = playingView;
        playingView.setImageResource( R.drawable.navigation_playing_num_sel );
        //playingView.setBackground( getResources().getDrawable(R.drawable.navigation_playing_num_sel) );
        playingView.setId( R.id.playing_tab_animation );
        PlayingSpec.setIndicator( playingView );
        //PlayingSpec.setIndicator("" , getResources().getDrawable(R.drawable.navigation_playing_num_sel));
        Intent PlayingIntent = new Intent( this , PlayingTabActivity.class );
        PlayingSpec.setContent( PlayingIntent );
        
        
        //���ѡ�  
        if ( mTabHost != null ) {
        	mTabHost.addTab( homeTabSpec );  
        	mTabHost.addTab( CateSpec );  
        	mTabHost.addTab( PocketSpec );  
        	mTabHost.addTab( PlayingSpec );
        }		
        mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
        	@Override
        	public void onTabChanged(String tabId) {
        		int i = mTabHost.getCurrentTab();
        	  }
        });*/
   
	}	
	/**
	 * ֪ͨ��Ϣ����
	 * @param type 1:@�ҵ���Ϣ 2:δ����Ϣ 3:���۸��� 4:�·�˿����
	 */
	private void ClearNotice(final int type)
	{
		final int uid = appContext.getLoginUid();
		final Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				if(msg.what==1 && msg.obj != null){
					Result res = (Result)msg.obj;
					if(res.OK() && res.getNotice()!=null){
						UIHelper.sendBroadCast(MainActivity.this, res.getNotice());
					}
				}else{
					((AppException)msg.obj).makeToast(MainActivity.this);
				}
			}
		};
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {
					Result res = appContext.noticeClear(uid, type);
					msg.what = 1;
					msg.obj = res;
				} catch (AppException e) {
					e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	 /**
     * ��ʼ��ͷ����ͼ
     */
    private void initHeadView()
    {
    	mHeadLogo = (ImageView)findViewById(R.id.main_head_logo);
    	mHeadTitle = (TextView)findViewById(R.id.main_head_title);
    	mHeadProgress = (ProgressBar)findViewById(R.id.main_head_progress);
    	mHead_search = (ImageButton)findViewById(R.id.main_head_search);
    	mHeadPub_post = (ImageButton)findViewById(R.id.main_head_pub_post);
    	mHeadPub_tweet = (ImageButton)findViewById(R.id.main_head_pub_tweet);
    	
    	mHead_search.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				UIHelper.showSearch(v.getContext());
			}
		});
    	mHeadPub_post.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				UIHelper.showQuestionPub(v.getContext());
			}
		});
    	mHeadPub_tweet.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				UIHelper.showTweetPub(MainActivity.this);
			}
		});
    }
    /**
     * ��ʼ���ײ���
     */
    private void initFootBar()
    {
    	fbNews = (RadioButton)findViewById(R.id.main_footbar_news);
    	fbQuestion = (RadioButton)findViewById(R.id.main_footbar_question);
    	fbTweet = (RadioButton)findViewById(R.id.main_footbar_tweet);
    	//fbactive = (RadioButton)findViewById(R.id.main_footbar_active);
    	
    	fbSetting = (ImageView)findViewById(R.id.main_footbar_setting);
    	fbSetting.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {    			
    			//չʾ�����&�ж��Ƿ��¼&�Ƿ��������ͼƬ
    			UIHelper.showSettingLoginOrLogout(MainActivity.this, mGrid.getQuickAction(0));
    			mGrid.show(v);
    		}
    	});    	
    }
    /**
     * listview���ݴ���
     * @param what ����
     * @param obj ����
     * @param objtype ��������
     * @param actiontype ��������
     * @return notice ֪ͨ��Ϣ
     */
    private Notice handleLvData(int what,Object obj,int objtype,int actiontype){
    	Notice notice = null;
		switch (actiontype) {
			case UIHelper.LISTVIEW_ACTION_INIT:
			case UIHelper.LISTVIEW_ACTION_REFRESH:
			case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
				switch (objtype) {
					case UIHelper.LISTVIEW_DATATYPE_NEWS:
						NewsList nlist = (NewsList)obj;
						notice = nlist.getNotice();
						lvNewsSumData = what;
						lvNewsData.clear();//�����ԭ������
						lvNewsData.addAll(nlist.getNewslist());
						break;
					case UIHelper.LISTVIEW_DATATYPE_BLOG:
						BlogList blist = (BlogList)obj;
						notice = blist.getNotice();
						lvBlogSumData = what;
						lvBlogData.clear();//�����ԭ������
						lvBlogData.addAll(blist.getBloglist());
						break;
					case UIHelper.LISTVIEW_DATATYPE_POST:
						PostList plist = (PostList)obj;
						notice = plist.getNotice();
						lvQuestionSumData = what;
						lvQuestionData.clear();//�����ԭ������
						lvQuestionData.addAll(plist.getPostlist());
						break;
					case UIHelper.LISTVIEW_DATATYPE_TWEET:
						TweetList tlist = (TweetList)obj;
						notice = tlist.getNotice();
						lvTweetSumData = what;
						lvTweetData.clear();//�����ԭ������
						lvTweetData.addAll(tlist.getTweetlist());
						break;
					case UIHelper.LISTVIEW_DATATYPE_ACTIVE:
						ActiveList alist = (ActiveList)obj;
						notice = alist.getNotice();
						lvActiveSumData = what;
						lvActiveData.clear();//�����ԭ������
						lvActiveData.addAll(alist.getActivelist());
						break;
					case UIHelper.LISTVIEW_DATATYPE_MESSAGE:
						MessageList mlist = (MessageList)obj;
						notice = mlist.getNotice();
						lvMsgSumData = what;
						lvMsgData.clear();//�����ԭ������
						lvMsgData.addAll(mlist.getMessagelist());
						break;
				}
				break;
			case UIHelper.LISTVIEW_ACTION_SCROLL:
				switch (objtype) {
					case UIHelper.LISTVIEW_DATATYPE_NEWS:
						NewsList list = (NewsList)obj;
						notice = list.getNotice();
						lvNewsSumData += what;
						if(lvNewsData.size() > 0){
							for(News news1 : list.getNewslist()){
								boolean b = false;
								for(News news2 : lvNewsData){
									if(news1.getId() == news2.getId()){
										b = true;
										break;
									}
								}
								if(!b) lvNewsData.add(news1);
							}
						}else{
							lvNewsData.addAll(list.getNewslist());
						}
						break;
					case UIHelper.LISTVIEW_DATATYPE_BLOG:
						BlogList blist = (BlogList)obj;
						notice = blist.getNotice();
						lvBlogSumData += what;
						if(lvBlogData.size() > 0){
							for(Blog blog1 : blist.getBloglist()){
								boolean b = false;
								for(Blog blog2 : lvBlogData){
									if(blog1.getId() == blog2.getId()){
										b = true;
										break;
									}
								}
								if(!b) lvBlogData.add(blog1);
							}
						}else{
							lvBlogData.addAll(blist.getBloglist());
						}
						break;
					case UIHelper.LISTVIEW_DATATYPE_POST:
						PostList plist = (PostList)obj;
						notice = plist.getNotice();
						lvQuestionSumData += what;
						if(lvQuestionData.size() > 0){
							for(Post post1 : plist.getPostlist()){
								boolean b = false;
								for(Post post2 : lvQuestionData){
									if(post1.getId() == post2.getId()){
										b = true;
										break;
									}
								}
								if(!b) lvQuestionData.add(post1);
							}
						}else{
							lvQuestionData.addAll(plist.getPostlist());
						}
						break;
					case UIHelper.LISTVIEW_DATATYPE_TWEET:
						TweetList tlist = (TweetList)obj;
						notice = tlist.getNotice();
						lvTweetSumData += what;
						if(lvTweetData.size() > 0){
							for(Tweet tweet1 : tlist.getTweetlist()){
								boolean b = false;
								for(Tweet tweet2 : lvTweetData){
									if(tweet1.getId() == tweet2.getId()){
										b = true;
										break;
									}
								}
								if(!b) lvTweetData.add(tweet1);
							}
						}else{
							lvTweetData.addAll(tlist.getTweetlist());
						}
						break;
					case UIHelper.LISTVIEW_DATATYPE_ACTIVE:
						ActiveList alist = (ActiveList)obj;
						notice = alist.getNotice();
						lvActiveSumData += what;
						if(lvActiveData.size() > 0){
							for(Active active1 : alist.getActivelist()){
								boolean b = false;
								for(Active active2 : lvActiveData){
									if(active1.getId() == active2.getId()){
										b = true;
										break;
									}
								}
								if(!b) lvActiveData.add(active1);
							}
						}else{
							lvActiveData.addAll(alist.getActivelist());
						}
						break;
					case UIHelper.LISTVIEW_DATATYPE_MESSAGE:
						MessageList mlist = (MessageList)obj;
						notice = mlist.getNotice();
						lvMsgSumData += what;
						if(lvMsgData.size() > 0){
							for(Messages msg1 : mlist.getMessagelist()){
								boolean b = false;
								for(Messages msg2 : lvMsgData){
									if(msg1.getId() == msg2.getId()){
										b = true;
										break;
									}
								}
								if(!b) lvMsgData.add(msg1);
							}
						}else{
							lvMsgData.addAll(mlist.getMessagelist());
						}
						break;
				}
				break;
		}
		return notice;
    }
    /**
     * ��ȡlistview�ĳ�ʼ��Handler
     * @param lv
     * @param adapter
     * @return
     */
    private Handler getLvHandler(final PullToRefreshListView lv,final BaseAdapter adapter,final TextView more,final ProgressBar progress,final int pageSize){
    	return new Handler(){
			public void handleMessage(Message msg) {
				if(msg.what >= 0){
					//listview���ݴ���
					Notice notice = handleLvData(msg.what, msg.obj, msg.arg2, msg.arg1);
					
					if(msg.what < pageSize){
						lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_full);
					}else if(msg.what == pageSize){
						lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_more);
						
						//���⴦��-���Ŷ������ܷ�ҳ
						if(lv == lvTweet) {
							TweetList tlist = (TweetList)msg.obj;
							if(lvTweetData.size() == tlist.getTweetCount()){
								lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
								more.setText(R.string.load_full);
							}
						}
					}
					//����֪ͨ�㲥
					if(notice != null){
						UIHelper.sendBroadCast(lv.getContext(), notice);
					}
					//�Ƿ����֪ͨ��Ϣ
					if(isClearNotice){
						ClearNotice(curClearNoticeType);
						isClearNotice = false;//����
						curClearNoticeType = 0;
					}
				}
				else if(msg.what == -1){
					//���쳣--��ʾ���س��� & ����������Ϣ
					lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
					more.setText(R.string.load_error);
					((AppException)msg.obj).makeToast(MainActivity.this);
				}
				if(adapter.getCount()==0){
					lv.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					more.setText(R.string.load_empty);
				}
				progress.setVisibility(ProgressBar.GONE);
				mHeadProgress.setVisibility(ProgressBar.GONE);
				if(msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH){
					lv.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
					lv.setSelection(0);
				}else if(msg.arg1 == UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG){
					lv.onRefreshComplete();
					lv.setSelection(0);
				}
			}
		};
    }
    /**
     * �̼߳�����������
     * @param catalog ����
     * @param pageIndex ��ǰҳ��
     * @param handler ������
     * @param action ������ʶ
     */
	private void loadLvNewsData(final int catalog,final int pageIndex,final Handler handler,final int action){ 
		mHeadProgress.setVisibility(ProgressBar.VISIBLE);		
		new Thread(){
			public void run() {				
				Message msg = new Message();
				boolean isRefresh = false;
				if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {					
					NewsList list = appContext.getNewsList(catalog, pageIndex, isRefresh);				
					msg.what = list.getPageSize();
					msg.obj = list;
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_DATATYPE_NEWS;
                if(curNewsCatalog == catalog)
                	handler.sendMessage(msg);
			}
		}.start();
	} 
    /**
     * ��ʼ������ListView����
     */
    private void initFrameListViewData()
    {
        //��ʼ��Handler
        lvNewsHandler = this.getLvHandler(lvNews, lvNewsAdapter, lvNews_foot_more, lvNews_foot_progress, AppContext.PAGE_SIZE);
        lvBlogHandler = this.getLvHandler(lvBlog, lvBlogAdapter, lvBlog_foot_more, lvBlog_foot_progress, AppContext.PAGE_SIZE);
        lvQuestionHandler = this.getLvHandler(lvQuestion, lvQuestionAdapter, lvQuestion_foot_more, lvQuestion_foot_progress, AppContext.PAGE_SIZE);  
        lvTweetHandler = this.getLvHandler(lvTweet, lvTweetAdapter, lvTweet_foot_more, lvTweet_foot_progress, AppContext.PAGE_SIZE);  
        lvActiveHandler = this.getLvHandler(lvActive, lvActiveAdapter, lvActive_foot_more, lvActive_foot_progress, AppContext.PAGE_SIZE); 
        lvMsgHandler = this.getLvHandler(lvMsg, lvMsgAdapter, lvMsg_foot_more, lvMsg_foot_progress, AppContext.PAGE_SIZE);      	
    	
        //��������				
		if(lvNewsData.size() == 0) {
			loadLvNewsData(curNewsCatalog, 0, lvNewsHandler, UIHelper.LISTVIEW_ACTION_INIT);
		}
		/*if(lvQuestionData.size() == 0) {
			loadLvQuestionData(curQuestionCatalog, 0, lvQuestionHandler, UIHelper.LISTVIEW_ACTION_INIT);
		}     
		if(lvTweetData.size() == 0) {
			loadLvTweetData(curTweetCatalog, 0, lvTweetHandler, UIHelper.LISTVIEW_ACTION_INIT);
		}  
		if(lvActiveData.size() == 0) {
			loadLvActiveData(curActiveCatalog, 0, lvActiveHandler, UIHelper.LISTVIEW_ACTION_INIT);
		}*/
    }
    private void initFrameListView()
    {
    	//��ʼ��listview�ؼ�
		this.initNewsListView();
		this.initBlogListView();
		/*this.initQuestionListView();
		this.initTweetListView();
		this.initActiveListView();
		this.initMsgListView();*/
		//����listview����
		this.initFrameListViewData();
    }
    /**
     * ��ʼ�������б�
     */
	private void initBlogListView()
    {
        /*lvBlogAdapter = new ListViewBlogAdapter(this, BlogList.CATALOG_LATEST, lvBlogData, R.layout.blog_listitem);        
        lvBlog_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
        lvBlog_foot_more = (TextView)lvBlog_footer.findViewById(R.id.listview_foot_more);
        lvBlog_foot_progress = (ProgressBar)lvBlog_footer.findViewById(R.id.listview_foot_progress);
        lvBlog = (PullToRefreshListView)findViewById(R.id.frame_listview_blog);
        lvBlog.addFooterView(lvBlog_footer);//��ӵײ���ͼ  ������setAdapterǰ
        lvBlog.setAdapter(lvBlogAdapter); 
        lvBlog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		//���ͷ�����ײ�����Ч
        		if(position == 0 || view == lvBlog_footer) return;
        		
        		Blog blog = null;        		
        		//�ж��Ƿ���TextView
        		if(view instanceof TextView){
        			blog = (Blog)view.getTag();
        		}else{
        			TextView tv = (TextView)view.findViewById(R.id.blog_listitem_title);
        			blog = (Blog)tv.getTag();
        		}
        		if(blog == null) return;
        		
        		//��ת����������
        		UIHelper.showUrlRedirect(view.getContext(), blog.getUrl());
        	}        	
		});
        lvBlog.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				lvBlog.onScrollStateChanged(view, scrollState);
				
				//����Ϊ��--���ü������������
				if(lvBlogData.size() == 0) return;
				
				//�ж��Ƿ�������ײ�
				boolean scrollEnd = false;
				try {
					if(view.getPositionForView(lvBlog_footer) == view.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				
				int lvDataState = StringUtils.toInt(lvBlog.getTag());
				if(scrollEnd && lvDataState==UIHelper.LISTVIEW_DATA_MORE)
				{
					lvBlog_foot_more.setText(R.string.load_ing);
					lvBlog_foot_progress.setVisibility(View.VISIBLE);
					//��ǰpageIndex
					int pageIndex = lvBlogSumData/AppContext.PAGE_SIZE;
					loadLvBlogData(curNewsCatalog, pageIndex, lvBlogHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
				lvBlog.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
        lvBlog.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            public void onRefresh() {
            	loadLvBlogData(curNewsCatalog, 0, lvBlogHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });	*/				
    }
    private void initNewsListView()
    {
        lvNewsAdapter = new ListViewNewsAdapter(this, lvNewsData, R.layout.news_listitem);        
        lvNews_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
        lvNews_foot_more = (TextView)lvNews_footer.findViewById(R.id.listview_foot_more);
        lvNews_foot_progress = (ProgressBar)lvNews_footer.findViewById(R.id.listview_foot_progress);
        lvNews = (PullToRefreshListView)findViewById(R.id.frame_listview_news);
        lvNews.addFooterView(lvNews_footer);//��ӵײ���ͼ  ������setAdapterǰ
        lvNews.setAdapter(lvNewsAdapter); 
        lvNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		//���ͷ�����ײ�����Ч
        		if(position == 0 || view == lvNews_footer) return;
        		
        		News news = null;        		
        		//�ж��Ƿ���TextView
        		if(view instanceof TextView){
        			news = (News)view.getTag();
        		}else{
        			TextView tv = (TextView)view.findViewById(R.id.news_listitem_title);
        			news = (News)tv.getTag();
        		}
        		if(news == null) return;
        		
        		//��ת����������
        		UIHelper.showNewsRedirect(view.getContext(), news);
        	}        	
		});
    }
	
	@Override
    protected void onActivityResult(int requestCode,
                                     int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); 
    }
	
	@Override
	 public void onDestroy(){
		super.onDestroy();
	    //mp.release();
	 }
	
}