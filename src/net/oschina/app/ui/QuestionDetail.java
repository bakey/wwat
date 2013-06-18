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
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.CommentList;
import net.oschina.app.bean.FavoriteList;
import net.oschina.app.bean.Notice;
import net.oschina.app.bean.Post;
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
 * 闂瓟璇︽儏
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class QuestionDetail extends Activity {

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
	
	private TextView mTitle;
	private TextView mAuthor;
	private TextView mPubDate;
	private TextView mCommentCount;
	
	private WebView mWebView;
    private Handler mHandler;
    private Post postDetail;
    private int postId;
    
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
	private int curCatalog;	
	private int curLvDataState;
	private int curLvPosition;//褰撳墠listview閫変腑鐨刬tem浣嶇疆
	
	private ViewSwitcher mFootViewSwitcher;
	private ImageView mFootEditebox;
	private EditText mFootEditer;
	private Button mFootPubcomment;	
	private ProgressDialog mProgress;
	private InputMethodManager imm;
	private String tempCommentKey = AppConfig.TEMP_COMMENT;
	
	private int _catalog;
	private int _id;
	private int _uid;
	private String _content;
	private int _isPostToMyZone;
	
	private GestureDetector gd;
	private boolean isFullScreen;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_detail);
        
        AppManager.getAppManager().addActivity(this);
        
        this.initView();        
        this.initData();
        
    	//鍔犺浇璇勮瑙嗗浘&鏁版嵁
    	this.initCommentView();
    	this.initCommentData();
    	
    	//娉ㄥ唽鍙屽嚮鍏ㄥ睆浜嬩欢
    	this.regOnDoubleEvent();
    }
    
    //鍒濆鍖栬鍥炬帶浠�
    private void initView()
    {
		postId = getIntent().getIntExtra("post_id", 0);
		
    	if(postId > 0) tempCommentKey = AppConfig.TEMP_COMMENT + "_" + CommentList.CATALOG_POST + "_" + postId;
    	
    	mHeader = (FrameLayout)findViewById(R.id.question_detail_header);
    	mFooter = (LinearLayout)findViewById(R.id.question_detail_footer);
    	mBack = (ImageView)findViewById(R.id.question_detail_back);
    	mRefresh = (ImageView)findViewById(R.id.question_detail_refresh);
    	mHeadTitle = (TextView)findViewById(R.id.question_detail_head_title);
    	mProgressbar = (ProgressBar)findViewById(R.id.question_detail_head_progress);
    	mViewSwitcher = (ViewSwitcher)findViewById(R.id.question_detail_viewswitcher);
    	mScrollView = (ScrollView)findViewById(R.id.question_detail_scrollview);
    	
    	mDetail = (ImageView)findViewById(R.id.question_detail_footbar_detail);
    	mCommentList = (ImageView)findViewById(R.id.question_detail_footbar_commentlist);
    	mShare = (ImageView)findViewById(R.id.question_detail_footbar_share);
    	mFavorite = (ImageView)findViewById(R.id.question_detail_footbar_favorite);
    	
    	mTitle = (TextView)findViewById(R.id.question_detail_title);
    	mAuthor = (TextView)findViewById(R.id.question_detail_author);
    	mPubDate = (TextView)findViewById(R.id.question_detail_date);
    	mCommentCount = (TextView)findViewById(R.id.question_detail_commentcount);
    	
    	mDetail.setEnabled(false);
    	
    	mWebView = (WebView)findViewById(R.id.question_detail_webview);
    	mWebView.getSettings().setJavaScriptEnabled(false);
    	mWebView.getSettings().setSupportZoom(true);
    	mWebView.getSettings().setBuiltInZoomControls(true);
    	mWebView.getSettings().setDefaultFontSize(15);    	
    	
    	mBack.setOnClickListener(UIHelper.finish(this));
    	mFavorite.setOnClickListener(favoriteClickListener);
    	mRefresh.setOnClickListener(refreshClickListener);
    	mShare.setOnClickListener(shareClickListener);
    	mDetail.setOnClickListener(detailClickListener);
    	mCommentList.setOnClickListener(commentlistClickListener);
    	mAuthor.setOnClickListener(authorClickListener);
    	
    	bv_comment = new BadgeView(this, mCommentList);
    	bv_comment.setBackgroundResource(R.drawable.widget_count_bg2);
    	bv_comment.setIncludeFontPadding(false);
    	bv_comment.setGravity(Gravity.CENTER);
    	bv_comment.setTextSize(8f);
    	bv_comment.setTextColor(Color.WHITE);
    	
    	imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
    	
    	mFootViewSwitcher = (ViewSwitcher)findViewById(R.id.question_detail_foot_viewswitcher);
    	mFootPubcomment = (Button)findViewById(R.id.question_detail_foot_pubcomment);
    	mFootPubcomment.setOnClickListener(commentpubClickListener);
    	mFootEditebox = (ImageView)findViewById(R.id.question_detail_footbar_editebox);
    	mFootEditebox.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mFootViewSwitcher.showNext();
				mFootEditer.setVisibility(View.VISIBLE);
				mFootEditer.requestFocus();
				mFootEditer.requestFocusFromTouch();
			}
		});
    	mFootEditer = (EditText)findViewById(R.id.question_detail_foot_editer);
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
    	//缂栬緫鍣ㄦ坊鍔犳枃鏈洃鍚�
    	mFootEditer.addTextChangedListener(UIHelper.getTextWatcher(this, tempCommentKey));
    	
    	//鏄剧ず涓存椂缂栬緫鍐呭
    	UIHelper.showTempEditContent(this, mFootEditer, tempCommentKey);
    }
    
    //鍒濆鍖栨帶浠舵暟鎹�
	private void initData()
	{		
		mHandler = new Handler()
		{
			public void handleMessage(Message msg) {

				headButtonSwitch(DATA_LOAD_COMPLETE);
				
				if(msg.what == 1)
				{					
					mTitle.setText(postDetail.getTitle());
					mAuthor.setText(postDetail.getAuthor());
					mPubDate.setText(StringUtils.friendly_time(postDetail.getPubDate()));
					mCommentCount.setText(postDetail.getAnswerCount()+"鍥�"+postDetail.getViewCount()+"闃�");
					
					//鏄惁鏀惰棌
					if(postDetail.getFavorite() == 1)
						mFavorite.setImageResource(R.drawable.widget_bar_favorite2);
					else
						mFavorite.setImageResource(R.drawable.widget_bar_favorite);
					
					//鏄剧ず璇勮鏁�
					if(postDetail.getAnswerCount() > 0){
						bv_comment.setText(postDetail.getAnswerCount()+"");
						bv_comment.show();
					}else{
						bv_comment.setText("");
						bv_comment.hide();
					}
					
					String body = postDetail.getBody() + "<div style=\"margin-bottom: 80px\" />";
					//璇诲彇鐢ㄦ埛璁剧疆锛氭槸鍚﹀姞杞芥枃绔犲浘鐗�-榛樿鏈墂ifi涓嬪缁堝姞杞藉浘鐗�
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
					
					//鍙戦�閫氱煡骞挎挱
					if(msg.obj != null){
						UIHelper.sendBroadCast(QuestionDetail.this, (Notice)msg.obj);
					}
				}
				else if(msg.what == 0)
				{
					headButtonSwitch(DATA_LOAD_FAIL);
					
					UIHelper.ToastMessage(QuestionDetail.this, R.string.msg_load_is_null);
				}
				else if(msg.what == -1 && msg.obj != null)
				{
					headButtonSwitch(DATA_LOAD_FAIL);
					
					((AppException)msg.obj).makeToast(QuestionDetail.this);
				}
			}
		};
		
		initData(postId, false);
	}
	
    private void initData(final int post_id, final boolean isRefresh)
    {
    	headButtonSwitch(DATA_LOAD_ING);
		
		new Thread(){
			public void run() {
                Message msg = new Message();
				try {
					postDetail = ((AppContext)getApplication()).getPost(post_id, isRefresh);
	                msg.what = (postDetail!=null && postDetail.getId()>0) ? 1 : 0;
	                msg.obj = (postDetail!=null) ? postDetail.getNotice() : null;
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
     * 搴曢儴鏍忓垏鎹�
     * @param type
     */
    private void viewSwitch(int type) {
    	switch (type) {
		case VIEWSWITCH_TYPE_DETAIL:
			mDetail.setEnabled(false);
			mCommentList.setEnabled(true);
			mHeadTitle.setText(R.string.question_detail_head_title);
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
     * 澶撮儴鎸夐挳灞曠ず
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
			initData(postId, true);
			loadLvCommentData(curId,curCatalog,0,mCommentHandler,UIHelper.LISTVIEW_ACTION_REFRESH);
		}
	};
    
	private View.OnClickListener authorClickListener = new View.OnClickListener() {
		public void onClick(View v) {				
			UIHelper.showUserCenter(v.getContext(), postDetail.getAuthorId(), postDetail.getAuthor());
		}
	};
	
	private View.OnClickListener shareClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			if(postDetail == null){
				UIHelper.ToastMessage(v.getContext(), R.string.msg_read_detail_fail);
				return;
			}
			//鍒嗕韩鍒�
			UIHelper.showShareDialog(QuestionDetail.this, postDetail.getTitle(), postDetail.getUrl());
		}
	};
	
	private View.OnClickListener detailClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			if(postId == 0){
				return;
			}
			viewSwitch(VIEWSWITCH_TYPE_DETAIL);
		}
	};
	
	private View.OnClickListener commentlistClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			if(postId == 0){
				return;
			}
			//璺冲埌璇勮鍒楄〃
			//UIHelper.showCommentList(v.getContext(), CommentList.CATALOG_POST, postId);
			viewSwitch(VIEWSWITCH_TYPE_COMMENTS);
		}
	};
	
	private View.OnClickListener favoriteClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			if(postId == 0 || postDetail == null){
				return;
			}
			
			final AppContext ac = (AppContext)getApplication();
			if(!ac.isLogin()){
				UIHelper.showLoginDialog(QuestionDetail.this);
				return;
			}
			final int uid = ac.getLoginUid();
						
			final Handler handler = new Handler(){
				public void handleMessage(Message msg) {
					if(msg.what == 1){
						Result res = (Result)msg.obj;
						if(res.OK()){
							if(postDetail.getFavorite() == 1){
								postDetail.setFavorite(0);
								mFavorite.setImageResource(R.drawable.widget_bar_favorite);
							}else{
								postDetail.setFavorite(1);
								mFavorite.setImageResource(R.drawable.widget_bar_favorite2);
							}	
						}
						UIHelper.ToastMessage(QuestionDetail.this, res.getErrorMessage());
					}else{
						((AppException)msg.obj).makeToast(QuestionDetail.this);
					}
				}        			
    		};
    		new Thread(){
				public void run() {
					Message msg = new Message();
					Result res = null;
					try {
						if(postDetail.getFavorite() == 1){
							res = ac.delFavorite(uid, postId, FavoriteList.TYPE_POST);
						}else{
							res = ac.addFavorite(uid, postId, FavoriteList.TYPE_POST);
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
	
	//鍒濆鍖栬鍥炬帶浠�
    private void initCommentView()
    {    	
    	lvComment_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
    	lvComment_foot_more = (TextView)lvComment_footer.findViewById(R.id.listview_foot_more);
        lvComment_foot_progress = (ProgressBar)lvComment_footer.findViewById(R.id.listview_foot_progress);

    	lvCommentAdapter = new ListViewCommentAdapter(this, lvCommentData, R.layout.comment_listitem); 
    	mLvComment = (PullToRefreshListView)findViewById(R.id.comment_list_listview);
    	
        mLvComment.addFooterView(lvComment_footer);//娣诲姞搴曢儴瑙嗗浘  蹇呴』鍦╯etAdapter鍓�
        mLvComment.setAdapter(lvCommentAdapter); 
        mLvComment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		//鐐瑰嚮澶撮儴銆佸簳閮ㄦ爮鏃犳晥
        		if(position == 0 || view == lvComment_footer) return;
        		
        		Comment com = null;
        		//鍒ゆ柇鏄惁鏄疶extView
        		if(view instanceof TextView){
        			com = (Comment)view.getTag();
        		}else{
            		ImageView img = (ImageView)view.findViewById(R.id.comment_listitem_userface);
            		com = (Comment)img.getTag();
        		} 
        		if(com == null) return;

        		//璺宠浆--鍥炲璇勮鐣岄潰
        		UIHelper.showCommentReply(QuestionDetail.this,curId, curCatalog, com.getId(), com.getAuthorId(), com.getAuthor(), com.getContent());
        	}
		});
        mLvComment.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				mLvComment.onScrollStateChanged(view, scrollState);
				
				//鏁版嵁涓虹┖--涓嶇敤缁х画涓嬮潰浠ｇ爜浜�
				if(lvCommentData.size() == 0) return;
				
				//鍒ゆ柇鏄惁婊氬姩鍒板簳閮�
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
					//褰撳墠pageIndex
					int pageIndex = lvSumData/20;
					loadLvCommentData(curId, curCatalog, pageIndex, mCommentHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
				mLvComment.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
        mLvComment.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				//鐐瑰嚮澶撮儴銆佸簳閮ㄦ爮鏃犳晥
        		if(position == 0 || view == lvComment_footer) return false;				
				
        		Comment _com = null;
        		//鍒ゆ柇鏄惁鏄疶extView
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
				//鎿嶄綔--鍥炲 & 鍒犻櫎
        		int uid = ac.getLoginUid();
        		//鍒ゆ柇璇ヨ瘎璁烘槸鍚︽槸褰撳墠鐧诲綍鐢ㄦ埛鍙戣〃鐨勶細true--鏈夊垹闄ゆ搷浣� false--娌℃湁鍒犻櫎鎿嶄綔
        		if(uid == com.getAuthorId())
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
								UIHelper.ToastMessage(QuestionDetail.this, res.getErrorMessage());
							}else{
								((AppException)msg.obj).makeToast(QuestionDetail.this);
							}
						}        			
	        		};
	        		final Thread thread = new Thread(){
						public void run() {
							Message msg = new Message();
							try {
								Result res = ac.delComment(curId, curCatalog, com.getId(), com.getAuthorId());
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
	        		UIHelper.showCommentOptionDialog(QuestionDetail.this, curId, curCatalog, com, thread);
        		}
        		else
        		{
        			UIHelper.showCommentOptionDialog(QuestionDetail.this, curId, curCatalog, com, null);
        		}
				return true;
			}        	
		});
        mLvComment.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			public void onRefresh() {
				loadLvCommentData(curId, curCatalog, 0, mCommentHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });
    }
    
    //鍒濆鍖栬瘎璁烘暟鎹�
	private void initCommentData()
	{
		curId = postId;
		curCatalog = CommentList.CATALOG_POST;
		
    	mCommentHandler = new Handler()
		{
			public void handleMessage(Message msg) 
			{				
				if(msg.what >= 0){						
					CommentList list = (CommentList)msg.obj;
					Notice notice = list.getNotice();
					//澶勭悊listview鏁版嵁
					switch (msg.arg1) {
					case UIHelper.LISTVIEW_ACTION_INIT:
					case UIHelper.LISTVIEW_ACTION_REFRESH:
						lvSumData = msg.what;
						lvCommentData.clear();//鍏堟竻闄ゅ師鏈夋暟鎹�
						lvCommentData.addAll(list.getCommentlist());
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
					
					//璇勮鏁版洿鏂�
					if(postDetail != null && lvCommentData.size() > postDetail.getAnswerCount()){
						postDetail.setAnswerCount(lvCommentData.size());
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
					//鍙戦�閫氱煡骞挎挱
					if(notice != null){
						UIHelper.sendBroadCast(QuestionDetail.this, notice);
					}
				}
				else if(msg.what == -1){
					//鏈夊紓甯�-鏄剧ず鍔犺浇鍑洪敊 & 寮瑰嚭閿欒娑堟伅
					curLvDataState = UIHelper.LISTVIEW_DATA_MORE;
					lvComment_foot_more.setText(R.string.load_error);
					((AppException)msg.obj).makeToast(QuestionDetail.this);
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
		this.loadLvCommentData(curId,curCatalog,0,mCommentHandler,UIHelper.LISTVIEW_ACTION_INIT);
    }
    /**
     * 绾跨▼鍔犺浇璇勮鏁版嵁
     * @param id 褰撳墠鏂囩珷id
     * @param catalog 鍒嗙被
     * @param pageIndex 褰撳墠椤垫暟
     * @param handler 澶勭悊鍣�
     * @param action 鍔ㄤ綔鏍囪瘑
     */
	private void loadLvCommentData(final int id,final int catalog,final int pageIndex,final Handler handler,final int action){  
		new Thread(){
			public void run() {
				Message msg = new Message();
				boolean isRefresh = false;
				if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {
					CommentList commentlist = ((AppContext)getApplication()).getCommentList(catalog, id, pageIndex, isRefresh);				
					msg.what = commentlist.getPageSize();
					msg.obj = commentlist;
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
				msg.arg1 = action;//鍛婄煡handler褰撳墠action
                handler.sendMessage(msg);
			}
		}.start();
	} 
	
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{ 	
		if (resultCode != RESULT_OK) return;   
    	if (data == null) return;
    	
    	viewSwitch(VIEWSWITCH_TYPE_COMMENTS);//璺冲埌璇勮鍒楄〃
    	
        if (requestCode == UIHelper.REQUEST_CODE_FOR_RESULT) 
        { 
        	Comment comm = (Comment)data.getSerializableExtra("COMMENT_SERIALIZABLE");
        	lvCommentData.add(0,comm);
        	lvCommentAdapter.notifyDataSetChanged();
        	mLvComment.setSelection(0);
    		//鏄剧ず璇勮鏁�
            int count = postDetail.getAnswerCount() + 1;
            postDetail.setAnswerCount(count);
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
			
			_catalog = curCatalog;
			
			_content = mFootEditer.getText().toString();
			if(StringUtils.isEmpty(_content)){
				UIHelper.ToastMessage(v.getContext(), "璇疯緭鍏ュ洖甯栧唴瀹�");
				return;
			}
			
			final AppContext ac = (AppContext)getApplication();
			if(!ac.isLogin()){
				UIHelper.showLoginDialog(QuestionDetail.this);
				return;
			}
			
//			if(mZone.isChecked())
//				_isPostToMyZone = 1;
				
			_uid = ac.getLoginUid();
			
			mProgress = ProgressDialog.show(v.getContext(), null, "鍙戣〃涓仿仿�",true,true); 	
			
			final Handler handler = new Handler(){
				public void handleMessage(Message msg) {
					
					if(mProgress!=null)mProgress.dismiss();
					
					if(msg.what == 1){
						Result res = (Result)msg.obj;
						UIHelper.ToastMessage(QuestionDetail.this, res.getErrorMessage());
						if(res.OK()){
							//鍙戦�閫氱煡骞挎挱
							if(res.getNotice() != null){
								UIHelper.sendBroadCast(QuestionDetail.this, res.getNotice());
							}
							//鎭㈠鍒濆搴曢儴鏍�
							mFootViewSwitcher.setDisplayedChild(0);
							mFootEditer.clearFocus();
							mFootEditer.setText("");
							mFootEditer.setVisibility(View.GONE);
							//璺冲埌璇勮鍒楄〃
					    	viewSwitch(VIEWSWITCH_TYPE_COMMENTS);
					    	//鏇存柊璇勮鍒楄〃
					    	lvCommentData.add(0,res.getComment());
					    	lvCommentAdapter.notifyDataSetChanged();
					    	mLvComment.setSelection(0);        	
							//鏄剧ず璇勮鏁�
					        int count = postDetail.getAnswerCount() + 1;
					        postDetail.setAnswerCount(count);
							bv_comment.setText(count+"");
							bv_comment.show();
							//娓呴櫎涔嬪墠淇濆瓨鐨勭紪杈戝唴瀹�
							ac.removeProperty(tempCommentKey);
						}
					}
					else {
						((AppException)msg.obj).makeToast(QuestionDetail.this);
					}
				}
			};
			new Thread(){
				public void run() {
					Message msg = new Message();
					Result res = new Result();
					try {
						//鍙戣〃璇勮
						res = ac.pubComment(_catalog, _id, _uid, _content, _isPostToMyZone);
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
	 * 娉ㄥ唽鍙屽嚮鍏ㄥ睆浜嬩欢
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
