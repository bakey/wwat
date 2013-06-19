package net.oschina.app.ui;

import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import com.hkzhe.wwtt.R;
import net.oschina.app.bean.CommentList;
import net.oschina.app.bean.Result;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import net.oschina.app.widget.LinkView;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * 閸欐垼銆冪拠鍕啈
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class CommentPub extends Activity{

	public final static int CATALOG_NEWS = 1;
	public final static int CATALOG_POST = 2;
	public final static int CATALOG_TWEET = 3;
	public final static int CATALOG_ACTIVE = 4;
	public final static int CATALOG_MESSAGE = 4;//閸斻劍锟芥稉搴ｆ殌鐟凤拷鍏樼仦鐐扮艾濞戝牊浼呮稉顓炵妇
	public final static int CATALOG_BLOG = 5;
	
	private ImageView mBack;
	private EditText mContent;
	private CheckBox mZone;
	private Button mPublish;
	private LinkView mQuote;
    private ProgressDialog mProgress;
	
	private int _catalog;
	private int _id;
	private int _uid;
	private String _content;
	private int _isPostToMyZone;
	
	//-------鐎电鐦庣拋鍝勬礀婢跺秷绻曢棁锟藉2閸欐﹢鍣�-----
	private int _replyid;//鐞氼偄娲栨径宥囨畱閸楁洑閲滅拠鍕啈id
	private int _authorid;//鐠囥儴鐦庣拋铏规畱閸樼喎顬婃担婊嗭拷id
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_pub);
		
		this.initView();
		
	}
	
    //閸掓繂顬婇崠鏍瀰閸ョ偓甯舵禒锟�   
	private void initView()
    {
		_id = getIntent().getIntExtra("id", 0);
		_catalog = getIntent().getIntExtra("catalog", 0);
		_replyid = getIntent().getIntExtra("reply_id", 0);
		_authorid = getIntent().getIntExtra("author_id", 0);
    	
    	mBack = (ImageView)findViewById(R.id.comment_list_back);
    	mPublish = (Button)findViewById(R.id.comment_pub_publish);
    	mContent = (EditText)findViewById(R.id.comment_pub_content);
    	mZone = (CheckBox)findViewById(R.id.comment_pub_zone);
    	if(_catalog == CommentList.CATALOG_TWEET){
    		mZone.setVisibility(View.VISIBLE);
    	}
    	
    	mBack.setOnClickListener(UIHelper.finish(this));
    	mPublish.setOnClickListener(publishClickListener);    	
    	
    	mQuote = (LinkView)findViewById(R.id.comment_pub_quote);
    	mQuote.setText(UIHelper.parseQuoteSpan(getIntent().getStringExtra("author"),getIntent().getStringExtra("content")));
    	mQuote.parseLinkText();
    }
	
	private View.OnClickListener publishClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			_content = mContent.getText().toString();
			if(StringUtils.isEmpty(_content)){
				UIHelper.ToastMessage(v.getContext(), "鐠囩柉绶崗銉ㄧ槑鐠佸搫鍞寸�锟�");
				return;
			}
			
			final AppContext ac = (AppContext)getApplication();
			if(!ac.isLogin()){
				UIHelper.showLoginDialog(CommentPub.this);
				return;
			}
			
			if(mZone.isChecked())
				_isPostToMyZone = 1;
				
			_uid = ac.getLoginUid();
			
	    	mProgress = ProgressDialog.show(v.getContext(), null, "閸欐垼銆冩稉顒讳豢浠匡拷",true,true); 			
			
			final Handler handler = new Handler(){
				public void handleMessage(Message msg) {
					if(mProgress!=null)mProgress.dismiss();
					if(msg.what == 1){
						Result res = (Result)msg.obj;
						UIHelper.ToastMessage(CommentPub.this, res.getErrorMessage());
						if(res.OK()){
							//閸欐垿锟介柅姘辩叀楠炴寧鎸�
							if(res.getNotice() != null){
								UIHelper.sendBroadCast(CommentPub.this, res.getNotice());
							}
							//鏉╂柨娲栭崚姘灠閸欐垼銆冮惃鍕槑鐠侊拷
							Intent intent = new Intent();
							intent.putExtra("COMMENT_SERIALIZABLE", res.getComment());
							setResult(RESULT_OK, intent);
							//鐠哄疇娴嗛崚鐗堟瀮缁旂姾顕涢幆锟�							finish();
						}
					}
					else {
						((AppException)msg.obj).makeToast(CommentPub.this);
					}
				}
			};
			new Thread(){
				public void run() {
					Message msg = new Message();
					Result res = new Result();
					try {
						//閸欐垼銆冪拠鍕啈
						if(_replyid == 0){
							res = ac.pubComment(_catalog, _id, _uid, _content, _isPostToMyZone);
						}
						//鐎电鐦庣拋楦跨箻鐞涘苯娲栨径锟�					
						else if(_replyid > 0){
							if(_catalog == CATALOG_BLOG)
								res = ac.replyBlogComment(_id, _uid, _content, _replyid, _authorid);
							else
								res = ac.replyComment(_id, _catalog, _replyid, _authorid, _uid, _content);
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
}
