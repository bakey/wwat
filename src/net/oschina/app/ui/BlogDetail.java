package net.oschina.app.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import net.oschina.app.AppManager;
import com.hkzhe.wwtt.R;
import net.oschina.app.adapter.ListViewCommentAdapter;
import net.oschina.app.bean.Blog;
import net.oschina.app.bean.BlogCommentList;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.FavoriteList;
import net.oschina.app.bean.Notice;
import net.oschina.app.bean.Result;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import net.oschina.app.widget.BadgeView;
import net.oschina.app.widget.PullToRefreshListView;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * 閸楁艾顓圭拠锔藉剰
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class BlogDetail extends Activity {
	
	private FrameLayout mHeader;
	private LinearLayout mFooter;
	private ImageView mBack;
	private ImageView mFavorite;
	private ImageView mRefresh;
	private TextView mHeadTitle;
	private ProgressBar mProgressbar;
	private ScrollView mScrollView;
    private ViewSwitcher mViewSwitcher;
	
	private BadgeView bv_comment;
	private ImageView mDetail;
	private ImageView mCommentList;
	private ImageView mShare;
    
	private ImageView mDocTYpe;
	private TextView mTitle;
	private TextView mAuthor;
	private TextView mPubDate;
	private TextView mCommentCount;
	
	private WebView mWebView;
    private Handler mHandler;
    private Blog blogDetail;
    private int blogId;
    
	private final static int VIEWSWITCH_TYPE_DETAIL = 0x001;
	private final static int VIEWSWITCH_TYPE_COMMENTS = 0x002;
	
	private final static int DATA_LOAD_ING = 0x001;
	private final static int DATA_LOAD_COMPLETE = 0x002;
	private final static int DATA_LOAD_FAIL = 0x003;
	
	private PullToRefreshListView mLvComment;
	private ListViewCommentAdapter lvCommentAdapter;
	private List<Comment> lvCommentData = new ArrayList<Comment>();
	private View lvComment_footer;
	private TextView lvComment_foot_more;
	private ProgressBar lvComment_foot_progress;
    private Handler mCommentHandler;
    private int lvSumData;
    
    private int curId;
	private int curCatalog;	//閸楁艾顓圭拠鍕啈閸掑棛琚�
	private int curLvDataState;
	private int curLvPosition;//瑜版挸澧爈istview闁鑵戦惃鍒瑃em娴ｅ秶鐤�
	
	private ViewSwitcher mFootViewSwitcher;
	private ImageView mFootEditebox;
	private EditText mFootEditer;
	private Button mFootPubcomment;	
	private ProgressDialog mProgress;
	private InputMethodManager imm;
	private String tempCommentKey = AppConfig.TEMP_COMMENT;
	
	private int _id;
	private int _uid;
	private String _content;
	
	private GestureDetector gd;
	private boolean isFullScreen;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_detail);
        
        AppManager.getAppManager().addActivity(this);
        
        this.initView();        
        this.initData();
        
    	//閸旂姾娴囩拠鍕啈鐟欏棗娴�閺佺増宓�
    	this.initCommentView();
    	this.initCommentData();
        
    	//濞夈劌鍞介崣灞藉毊閸忋劌鐫嗘禍瀣╂
    	this.regOnDoubleEvent();
    }
    
    //閸掓繂顬婇崠鏍瀰閸ョ偓甯舵禒锟�  
    private void initView()
    {
    	blogId = getIntent().getIntExtra("blog_id", 0);
    	
    	if(blogId > 0) tempCommentKey = AppConfig.TEMP_COMMENT + "_" + CommentPub.CATALOG_BLOG + "_" + blogId;
    	
    	mHeader = (FrameLayout)findViewById(R.id.blog_detail_header);
    	mFooter = (LinearLayout)findViewById(R.id.blog_detail_footer);
    	mBack = (ImageView)findViewById(R.id.blog_detail_back);
    	mRefresh = (ImageView)findViewById(R.id.blog_detail_refresh);
    	mProgressbar = (ProgressBar)findViewById(R.id.blog_detail_head_progress);
    	mHeadTitle = (TextView)findViewById(R.id.blog_detail_head_title);
    	mViewSwitcher = (ViewSwitcher)findViewById(R.id.blog_detail_viewswitcher);
    	mScrollView = (ScrollView)findViewById(R.id.blog_detail_scrollview);
    	
    	mDetail = (ImageView)findViewById(R.id.blog_detail_footbar_detail);
    	mCommentList = (ImageView)findViewById(R.id.blog_detail_footbar_commentlist);
    	mShare = (ImageView)findViewById(R.id.blog_detail_footbar_share);
    	mFavorite = (ImageView)findViewById(R.id.blog_detail_footbar_favorite);
    	
    	mDocTYpe = (ImageView)findViewById(R.id.blog_detail_documentType);
    	mTitle = (TextView)findViewById(R.id.blog_detail_title);
    	mAuthor = (TextView)findViewById(R.id.blog_detail_author);
    	mPubDate = (TextView)findViewById(R.id.blog_detail_date);
    	mCommentCount = (TextView)findViewById(R.id.blog_detail_commentcount);
    	
    	mDetail.setEnabled(false);
    	
    	mWebView = (WebView)findViewById(R.id.blog_detail_webview);
    	mWebView.getSettings().setJavaScriptEnabled(false);
    	mWebView.getSettings().setSupportZoom(true);
    	mWebView.getSettings().setBuiltInZoomControls(true);
    	mWebView.getSettings().setDefaultFontSize(15);
    	
    	mBack.setOnClickListener(UIHelper.finish(this));
    	mFavorite.setOnClickListener(favoriteClickListener);
    	mRefresh.setOnClickListener(refreshClickListener);
    	mAuthor.setOnClickListener(authorClickListener);
    	mShare.setOnClickListener(shareClickListener);
    	mDetail.setOnClickListener(detailClickListener);
    	mCommentList.setOnClickListener(commentlistClickListener);
    	
    	bv_comment = new BadgeView(this, mCommentList);
    	bv_comment.setBackgroundResource(R.drawable.widget_count_bg2);
    	bv_comment.setIncludeFontPadding(false);
    	bv_comment.setGravity(Gravity.CENTER);
    	bv_comment.setTextSize(8f);
    	bv_comment.setTextColor(Color.WHITE);
    	
    	imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
    	
    	mFootViewSwitcher = (ViewSwitcher)findViewById(R.id.blog_detail_foot_viewswitcher);
    	mFootPubcomment = (Button)findViewById(R.id.blog_detail_foot_pubcomment);
    	mFootPubcomment.setOnClickListener(commentpubClickListener);
    	mFootEditebox = (ImageView)findViewById(R.id.blog_detail_footbar_editebox);
    	mFootEditebox.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mFootViewSwitcher.showNext();
				mFootEditer.setVisibility(View.VISIBLE);
				mFootEditer.requestFocus();
				mFootEditer.requestFocusFromTouch();
			}
		});
    	mFootEditer = (EditText)findViewById(R.id.blog_detail_foot_editer);
    	mFootEditer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){  
					imm.showSoftInput(v, 0);  
		        }  
		        else{  
		            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
		        }  
			}
		}); 
    	mFootEditer.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if(mFootViewSwitcher.getDisplayedChild()==1){
						mFootViewSwitcher.setDisplayedChild(0);
						mFootEditer.clearFocus();
						mFootEditer.setVisibility(View.GONE);
					}
					return true;
				}
				return false;
			}
		});
    	//缂傛牞绶崳銊﹀潑閸旂姵鏋冮張顒傛磧閸氾拷
    	mFootEditer.addTextChangedListener(UIHelper.getTextWatcher(this, tempCommentKey));
    	
    	//閺勫墽銇氭稉瀛樻缂傛牞绶崘鍛啇
    	UIHelper.showTempEditContent(this, mFootEditer, tempCommentKey);
    }
    
    //閸掓繂顬婇崠鏍ㄥ付娴犺埖鏆熼幑锟�	
    private void initData()
	{	
		mHandler = new Handler()
		{
			public void handleMessage(Message msg) 
			{				
				if(msg.what == 1)
				{					
					headButtonSwitch(DATA_LOAD_COMPLETE);
					
					int docType = blogDetail.getDocumentType(); 
					if(docType == Blog.DOC_TYPE_ORIGINAL){
						mDocTYpe.setImageResource(R.drawable.widget_original_icon);
					}else if(docType == Blog.DOC_TYPE_REPASTE){
						mDocTYpe.setImageResource(R.drawable.widget_repaste_icon);
					}
					
					mTitle.setText(blogDetail.getTitle());
					mAuthor.setText(blogDetail.getAuthor());
					mPubDate.setText(StringUtils.friendly_time(blogDetail.getPubDate()));
					mCommentCount.setText(String.valueOf(blogDetail.getCommentCount()));
					
					//閺勵垰鎯侀弨鎯版
					if(blogDetail.getFavorite() == 1)
						mFavorite.setImageResource(R.drawable.widget_bar_favorite2);
					else
						mFavorite.setImageResource(R.drawable.widget_bar_favorite);
					
					//閺勫墽銇氱拠鍕啈閺侊拷
					if(blogDetail.getCommentCount() > 0){
						bv_comment.setText(blogDetail.getCommentCount()+"");
						bv_comment.show();
					}else{
						bv_comment.setText("");
						bv_comment.hide();
					}
					
					String body = blogDetail.getBody() + "<div style=\"margin-bottom: 80px\" />";
					//鐠囪褰囬悽銊﹀煕鐠佸墽鐤嗛敍姘Ц閸氾箑濮炴潪鑺ユ瀮缁旂姴娴橀悧锟�姒涙顓婚張澧俰fi娑撳顬婄紒鍫濆鏉炶棄娴橀悧锟�		
					boolean isLoadImage;
					AppContext ac = (AppContext)getApplication();
					if(AppContext.NETTYPE_WIFI == ac.getNetworkType()){
						isLoadImage = true;
					}else{
						isLoadImage = ac.isLoadImage();
					}
					if(isLoadImage){
						body = body.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+","$1");
						body = body.replaceAll("(<img[^>]*?)\\s+height\\s*=\\s*\\S+","$1");
					}else{
						body = body.replaceAll("<\\s*img\\s+([^>]*)\\s*>","");
					}
					if(!body.trim().startsWith("<style>")){
						String html = UIHelper.WEB_STYLE;
						body = html + body;
					}

					mWebView.loadDataWithBaseURL(null, body, "text/html", "utf-8",null);
					mWebView.setWebViewClient(UIHelper.getWebViewClient());
					
					//閸欐垿锟介柅姘辩叀楠炴寧鎸�
					if(msg.obj != null){
						UIHelper.sendBroadCast(BlogDetail.this, (Notice)msg.obj);
					}
				}
				else if(msg.what == 0)
				{
					headButtonSwitch(DATA_LOAD_FAIL);
					
					UIHelper.ToastMessage(BlogDetail.this, R.string.msg_load_is_null);
				}
				else if(msg.what == -1 && msg.obj != null)
				{
					headButtonSwitch(DATA_LOAD_FAIL);
					
					((AppException)msg.obj).makeToast(BlogDetail.this);
				}
			}
		};
		
		initData(blogId, false);
	}
	
    private void initData(final int blog_id, final boolean isRefresh)
    {	
    	headButtonSwitch(DATA_LOAD_ING);
    	
		new Thread(){
			public void run() {
                Message msg = new Message();
				try {
					blogDetail = ((AppContext)getApplication()).getBlog(blog_id, isRefresh);
	                msg.what = (blogDetail!=null && blogDetail.getId()>0) ? 1 : 0;
	                msg.obj = (blogDetail!=null) ? blogDetail.getNotice() : null;
	            } catch (AppException e) {
	                e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
                mHandler.sendMessage(msg);
			}
		}.start();
    }
    
    /**
     * 鎼存洟鍎撮弽蹇撳瀼閹癸拷
     * @param type
     */
    private void viewSwitch(int type) {
    	switch (type) {
		case VIEWSWITCH_TYPE_DETAIL:
			mDetail.setEnabled(false);
			mCommentList.setEnabled(true);
			mHeadTitle.setText(R.string.blog_detail_head_title);
			mViewSwitcher.setDisplayedChild(0);			
			break;
		case VIEWSWITCH_TYPE_COMMENTS:
			mDetail.setEnabled(true);
			mCommentList.setEnabled(false);
			mHeadTitle.setText(R.string.comment_list_head_title);
			mViewSwitcher.setDisplayedChild(1);
			break;
    	}
    }
    
    /**
     * 婢舵挳鍎撮幐澶愭尦鐏炴洜銇�
     * @param type
     */
    private void headButtonSwitch(int type) {
    	switch (type) {
		case DATA_LOAD_ING:
			mScrollView.setVisibility(View.GONE);
			mProgressbar.setVisibility(View.VISIBLE);
			mRefresh.setVisibility(View.GONE);
			break;
		case DATA_LOAD_COMPLETE:
			mScrollView.setVisibility(View.VISIBLE);
			mProgressbar.setVisibility(View.GONE);
			mRefresh.setVisibility(View.VISIBLE);
			break;
		case DATA_LOAD_FAIL:
			mScrollView.setVisibility(View.GONE);
			mProgressbar.setVisibility(View.GONE);
			mRefresh.setVisibility(View.VISIBLE);
			break;
		}
    }
	
    private View.OnClickListener refreshClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			initData(blogId, true);
			loadLvCommentData(curId,0,mCommentHandler,UIHelper.LISTVIEW_ACTION_REFRESH);
		}
	};
    
	private View.OnClickListener authorClickListener = new View.OnClickListener() {
		public void onClick(View v) {				
			UIHelper.showUserCenter(v.getContext(), blogDetail.getAuthorId(), blogDetail.getAuthor());
		}
	};
	
	private View.OnClickListener shareClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			if(blogDetail == null){
				UIHelper.ToastMessage(v.getContext(), R.string.msg_read_detail_fail);
				return;
			}
			//閸掑棔闊╅崚锟�			UIHelper.showShareDialog(BlogDetail.this, blogDetail.getTitle(), blogDetail.getUrl());
		}
	};
	
	private View.OnClickListener detailClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			if(blogId == 0){
				return;
			}
			//閸掑洦宕查崚鎷岊嚊閹拷
			viewSwitch(VIEWSWITCH_TYPE_DETAIL);
		}
	};	
	
	private View.OnClickListener commentlistClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			if(blogId == 0){
				return;
			}
			//閸掑洦宕查崚鎷岀槑鐠侊拷
			viewSwitch(VIEWSWITCH_TYPE_COMMENTS);
		}
	};
	
	private View.OnClickListener favoriteClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			if(blogId == 0 || blogDetail == null){
				return;
			}
			
			final AppContext ac = (AppContext)getApplication();
			if(!ac.isLogin()){
				UIHelper.showLoginDialog(BlogDetail.this);
				return;
			}
			final int uid = ac.getLoginUid();
						
			final Handler handler = new Handler(){
				public void handleMessage(Message msg) {
					if(msg.what == 1){
						Result res = (Result)msg.obj;
						if(res.OK()){
							if(blogDetail.getFavorite() == 1){
								blogDetail.setFavorite(0);
								mFavorite.setImageResource(R.drawable.widget_bar_favorite);
							}else{
								blogDetail.setFavorite(1);
								mFavorite.setImageResource(R.drawable.widget_bar_favorite2);
							}	
						}
						UIHelper.ToastMessage(BlogDetail.this, res.getErrorMessage());
					}else{
						((AppException)msg.obj).makeToast(BlogDetail.this);
					}
				}        			
    		};
    		new Thread(){
				public void run() {
					Message msg = new Message();
					Result res = null;
					try {
						if(blogDetail.getFavorite() == 1){
							res = ac.delFavorite(uid, blogId, FavoriteList.TYPE_BLOG);
						}else{
							res = ac.addFavorite(uid, blogId, FavoriteList.TYPE_BLOG);
						}
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
	};
	
    //閸掓繂顬婇崠鏍瀰閸ョ偓甯舵禒锟�  
	private void initCommentView()
    {    	
    	lvComment_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
    	lvComment_foot_more = (TextView)lvComment_footer.findViewById(R.id.listview_foot_more);
        lvComment_foot_progress = (ProgressBar)lvComment_footer.findViewById(R.id.listview_foot_progress);

    	lvCommentAdapter = new ListViewCommentAdapter(this, lvCommentData, R.layout.comment_listitem); 
    	mLvComment = (PullToRefreshListView)findViewById(R.id.comment_list_listview);
    	
        mLvComment.addFooterView(lvComment_footer);//濞ｈ濮炴惔鏇㈠劥鐟欏棗娴� 韫囧懘銆忛崷鈺痚tAdapter閸擄拷
        mLvComment.setAdapter(lvCommentAdapter); 
        mLvComment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		//閻愮懓鍤径鎾劥閵嗕礁绨抽柈銊︾埉閺冪姵鏅�
        		if(position == 0 || view == lvComment_footer) return;
        		
        		Comment com = null;
        		//閸掋倖鏌囬弰顖氭儊閺勭柖extView
        		if(view instanceof TextView){
        			com = (Comment)view.getTag();
        		}else{
            		ImageView img = (ImageView)view.findViewById(R.id.comment_listitem_userface);
            		com = (Comment)img.getTag();
        		} 
        		if(com == null) return;
        		
        		//鐠哄疇娴�-閸ョ偛顦剧拠鍕啈閻ｅ矂娼�
        		UIHelper.showCommentReply(BlogDetail.this,curId, curCatalog, com.getId(), com.getAuthorId(), com.getAuthor(), com.getContent());
        	}
		});
        mLvComment.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				mLvComment.onScrollStateChanged(view, scrollState);
				
				//閺佺増宓佹稉铏光敄--娑撳秶鏁ょ紒褏鐢绘稉瀣桨娴狅絿鐖滄禍锟�				if(lvCommentData.size() == 0) return;
				
				//閸掋倖鏌囬弰顖氭儊濠婃艾濮╅崚鏉跨俺闁拷
				boolean scrollEnd = false;
				try {
					if(view.getPositionForView(lvComment_footer) == view.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				
				if(scrollEnd && curLvDataState==UIHelper.LISTVIEW_DATA_MORE)
				{
					lvComment_foot_more.setText(R.string.load_ing);
					lvComment_foot_progress.setVisibility(View.VISIBLE);
					//瑜版挸澧爌ageIndex
					int pageIndex = lvSumData/20;
					loadLvCommentData(curId, pageIndex, mCommentHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
				mLvComment.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
        mLvComment.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				//閻愮懓鍤径鎾劥閵嗕礁绨抽柈銊︾埉閺冪姵鏅�
        		if(position == 0 || view == lvComment_footer) return false;				
				
        		Comment _com = null;
        		//閸掋倖鏌囬弰顖氭儊閺勭柖extView
        		if(view instanceof TextView){
        			_com = (Comment)view.getTag();
        		}else{
            		ImageView img = (ImageView)view.findViewById(R.id.comment_listitem_userface);
            		_com = (Comment)img.getTag();
        		} 
        		if(_com == null) return false;
        		
        		final Comment com = _com;
        		
        		curLvPosition = lvCommentData.indexOf(com);
        		
        		final AppContext ac = (AppContext)getApplication();
				//閹垮秳缍�-閸ョ偛顦�& 閸掔娀娅�       		
        		final int uid = ac.getLoginUid();
        		//閸掋倖鏌囪ぐ鎾冲閻ц缍嶉悽銊﹀煕閺勵垰鎯侀弰顖氬触娑擄拷閹存牞锟�鐠囥儴鐦庣拋鐑樻Ц閸氾附妲歌ぐ鎾冲閻ц缍嶉悽銊﹀煕閸欐垼銆冮惃鍕剁窗true--閺堝鍨归梽銈嗘惙娴ｏ拷 false--濞屸剝婀侀崚鐘绘珟閹垮秳缍�
        		if(uid == com.getAuthorId() || (blogDetail != null && uid == blogDetail.getAuthorId()))
        		{
	        		final Handler handler = new Handler(){
						public void handleMessage(Message msg) {
							if(msg.what == 1){
								Result res = (Result)msg.obj;
								if(res.OK()){
									lvSumData--;
									bv_comment.setText(lvSumData+"");
						    		bv_comment.show();
									lvCommentData.remove(com);
									lvCommentAdapter.notifyDataSetChanged();
								}
								UIHelper.ToastMessage(BlogDetail.this, res.getErrorMessage());
							}else{
								((AppException)msg.obj).makeToast(BlogDetail.this);
							}
						}        			
	        		};
	        		final Thread thread = new Thread(){
						public void run() {
							Message msg = new Message();
							try {
								Result res = ac.delBlogComment(uid, blogId, com.getId(), com.getAuthorId(), blogDetail.getAuthorId());
								msg.what = 1;
								msg.obj = res;
				            } catch (AppException e) {
				            	e.printStackTrace();
				            	msg.what = -1;
				            	msg.obj = e;
				            }
			                handler.sendMessage(msg);
						}        			
	        		};
	        		UIHelper.showCommentOptionDialog(BlogDetail.this, curId, curCatalog, com, thread);
        		}
        		else
        		{
        			UIHelper.showCommentOptionDialog(BlogDetail.this, curId, curCatalog, com, null);
        		}
				return true;
			}        	
		});
        mLvComment.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			public void onRefresh() {
				loadLvCommentData(curId, 0, mCommentHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });
    }
    
    //閸掓繂顬婇崠鏍槑鐠佺儤鏆熼幑锟�
	private void initCommentData()
	{
		curId = blogId;
		curCatalog = CommentPub.CATALOG_BLOG;
		
    	mCommentHandler = new Handler()
		{
			public void handleMessage(Message msg) 
			{
				if(msg.what >= 0){						
					BlogCommentList list = (BlogCommentList)msg.obj;
					Notice notice = list.getNotice();
					//婢跺嫮鎮妉istview閺佺増宓�
					switch (msg.arg1) {
					case UIHelper.LISTVIEW_ACTION_INIT:
					case UIHelper.LISTVIEW_ACTION_REFRESH:
						lvSumData = msg.what;
						lvCommentData.clear();//閸忓牊绔婚梽銈呭斧閺堝鏆熼幑锟�						lvCommentData.addAll(list.getCommentlist());
						break;
					case UIHelper.LISTVIEW_ACTION_SCROLL:
						lvSumData += msg.what;
						if(lvCommentData.size() > 0){
							for(Comment com1 : list.getCommentlist()){
								boolean b = false;
								for(Comment com2 : lvCommentData){
									if(com1.getId() == com2.getId() && com1.getAuthorId() == com2.getAuthorId()){
										b = true;
										break;
									}
								}
								if(!b) lvCommentData.add(com1);
							}
						}else{
							lvCommentData.addAll(list.getCommentlist());
						}
						break;
					}	
					
					//鐠囧嫯顔戦弫鐗堟纯閺傦拷
					if(blogDetail != null && lvCommentData.size() > blogDetail.getCommentCount()){
						blogDetail.setCommentCount(lvCommentData.size());
						bv_comment.setText(lvCommentData.size()+"");
						bv_comment.show();
					}
					
					if(msg.what < 20){
						curLvDataState = UIHelper.LISTVIEW_DATA_FULL;
						lvCommentAdapter.notifyDataSetChanged();
						lvComment_foot_more.setText(R.string.load_full);
					}else if(msg.what == 20){					
						curLvDataState = UIHelper.LISTVIEW_DATA_MORE;
						lvCommentAdapter.notifyDataSetChanged();
						lvComment_foot_more.setText(R.string.load_more);
					}
					//閸欐垿锟介柅姘辩叀楠炴寧鎸�
					if(notice != null){
						UIHelper.sendBroadCast(BlogDetail.this, notice);
					}
				}
				else if(msg.what == -1){
					//閺堝绱撶敮锟�閺勫墽銇氶崝鐘烘祰閸戞椽鏁�& 瀵懓鍤柨娆掝嚖濞戝牊浼�
					curLvDataState = UIHelper.LISTVIEW_DATA_MORE;
					lvComment_foot_more.setText(R.string.load_error);
					((AppException)msg.obj).makeToast(BlogDetail.this);
				}
				if(lvCommentData.size()==0){
					curLvDataState = UIHelper.LISTVIEW_DATA_EMPTY;
					lvComment_foot_more.setText(R.string.load_empty);
				}
				lvComment_foot_progress.setVisibility(View.GONE);
				if(msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH){
					mLvComment.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
					mLvComment.setSelection(0);
				}
			}
		};
		this.loadLvCommentData(curId,0,mCommentHandler,UIHelper.LISTVIEW_ACTION_INIT);
    }
    /**
     * 缁捐法鈻奸崝鐘烘祰鐠囧嫯顔戦弫鐗堝祦
     * @param id 瑜版挸澧犻弬鍥╃彿id
     * @param pageIndex 瑜版挸澧犳い鍨殶
     * @param handler 婢跺嫮鎮婇崳锟�     * @param action 閸斻劋缍旈弽鍥槕
     */
	private void loadLvCommentData(final int id,final int pageIndex,final Handler handler,final int action){  
		new Thread(){
			public void run() {
				Message msg = new Message();
				boolean isRefresh = false;
				if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {
					BlogCommentList commentlist = ((AppContext)getApplication()).getBlogCommentList(id, pageIndex, isRefresh);
					msg.what = commentlist.getPageSize();
					msg.obj = commentlist;
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
	
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{ 	
		if (resultCode != RESULT_OK) return;   
    	if (data == null) return;
    	
    	viewSwitch(VIEWSWITCH_TYPE_COMMENTS);//鐠哄啿鍩岀拠鍕啈閸掓銆�
    	
        if (requestCode == UIHelper.REQUEST_CODE_FOR_RESULT) 
        { 
        	Comment comm = (Comment)data.getSerializableExtra("COMMENT_SERIALIZABLE");
        	lvCommentData.add(0,comm);
        	lvCommentAdapter.notifyDataSetChanged();
        	mLvComment.setSelection(0);        	
    		//閺勫墽銇氱拠鍕啈閺侊拷
            int count = blogDetail.getCommentCount() + 1;
            blogDetail.setCommentCount(count);
    		bv_comment.setText(count+"");
    		bv_comment.show();
        }
        else if (requestCode == UIHelper.REQUEST_CODE_FOR_REPLY)
        {
        	Comment comm = (Comment)data.getSerializableExtra("COMMENT_SERIALIZABLE");
        	lvCommentData.set(curLvPosition, comm);
        	lvCommentAdapter.notifyDataSetChanged();
        }
	}
	
	private View.OnClickListener commentpubClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			_id = curId;
			
			if(curId == 0){
				return;
			}
			
			_content = mFootEditer.getText().toString();
			if(StringUtils.isEmpty(_content)){
				UIHelper.ToastMessage(v.getContext(), "鐠囩柉绶崗銉ㄧ槑鐠佸搫鍞寸�锟�");
				return;
			}
			
			final AppContext ac = (AppContext)getApplication();
			if(!ac.isLogin()){
				UIHelper.showLoginDialog(BlogDetail.this);
				return;
			}
				
			_uid = ac.getLoginUid();
			
			mProgress = ProgressDialog.show(v.getContext(), null, "閸欐垼銆冩稉顒讳豢浠匡拷",true,true); 			
			
			final Handler handler = new Handler(){
				public void handleMessage(Message msg) {
					
					if(mProgress!=null)mProgress.dismiss();
					
					if(msg.what == 1){
						Result res = (Result)msg.obj;
						UIHelper.ToastMessage(BlogDetail.this, res.getErrorMessage());
						if(res.OK()){
							//閸欐垿锟介柅姘辩叀楠炴寧鎸�
							if(res.getNotice() != null){
								UIHelper.sendBroadCast(BlogDetail.this, res.getNotice());
							}
							//閹垹顦鹃崚婵嗩瀶鎼存洟鍎撮弽锟�							mFootViewSwitcher.setDisplayedChild(0);
							mFootEditer.clearFocus();
							mFootEditer.setText("");
							mFootEditer.setVisibility(View.GONE);
							//鐠哄啿鍩岀拠鍕啈閸掓銆�
					    	viewSwitch(VIEWSWITCH_TYPE_COMMENTS);
					    	//閺囧瓨鏌婄拠鍕啈閸掓銆�
					    	lvCommentData.add(0,res.getComment());
					    	lvCommentAdapter.notifyDataSetChanged();
					    	mLvComment.setSelection(0);        	
							//閺勫墽銇氱拠鍕啈閺侊拷
					        int count = blogDetail.getCommentCount() + 1;
					        blogDetail.setCommentCount(count);
							bv_comment.setText(count+"");
							bv_comment.show();
							//濞撳懘娅庢稊瀣娣囨繂鐡ㄩ惃鍕椽鏉堟垵鍞寸�锟�							ac.removeProperty(tempCommentKey);
						}
					}
					else {
						((AppException)msg.obj).makeToast(BlogDetail.this);
					}
				}
			};
			new Thread(){
				public void run() {
					Message msg = new Message();
					Result res = new Result();
					try {
						//閸欐垼銆冪拠鍕啈
						res = ac.pubBlogComment(_id, _uid, _content);
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
	};
	
	/**
	 * 濞夈劌鍞介崣灞藉毊閸忋劌鐫嗘禍瀣╂
	 */
	private void regOnDoubleEvent(){
		gd = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
			@Override
			public boolean onDoubleTap(MotionEvent e) {
				isFullScreen = !isFullScreen;
				if (!isFullScreen) {   
                    WindowManager.LayoutParams params = getWindow().getAttributes();   
                    params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);   
                    getWindow().setAttributes(params);   
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);  
                    mHeader.setVisibility(View.VISIBLE);
                    mFooter.setVisibility(View.VISIBLE);
                } else {    
                    WindowManager.LayoutParams params = getWindow().getAttributes();   
                    params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;   
                    getWindow().setAttributes(params);   
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);   
                    mHeader.setVisibility(View.GONE);
                    mFooter.setVisibility(View.GONE);
                }
				return true;
			}
		});
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		gd.onTouchEvent(event);
		return super.dispatchTouchEvent(event);
	}
}
