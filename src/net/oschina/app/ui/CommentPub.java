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
 * 鍙戣〃璇勮
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class CommentPub extends Activity{

	public final static int CATALOG_NEWS = 1;
	public final static int CATALOG_POST = 2;
	public final static int CATALOG_TWEET = 3;
	public final static int CATALOG_ACTIVE = 4;
	public final static int CATALOG_MESSAGE = 4;//鍔ㄦ�涓庣暀瑷�兘灞炰簬娑堟伅涓績
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
	
	//-------瀵硅瘎璁哄洖澶嶈繕闇�姞2鍙橀噺------
	private int _replyid;//琚洖澶嶇殑鍗曚釜璇勮id
	private int _authorid;//璇ヨ瘎璁虹殑鍘熷浣滆�id
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_pub);
		
		this.initView();
		
	}
	
    //鍒濆鍖栬鍥炬帶浠�
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
				UIHelper.ToastMessage(v.getContext(), "璇疯緭鍏ヨ瘎璁哄唴瀹�");
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
			
	    	mProgress = ProgressDialog.show(v.getContext(), null, "鍙戣〃涓仿仿�",true,true); 			
			
			final Handler handler = new Handler(){
				public void handleMessage(Message msg) {
					if(mProgress!=null)mProgress.dismiss();
					if(msg.what == 1){
						Result res = (Result)msg.obj;
						UIHelper.ToastMessage(CommentPub.this, res.getErrorMessage());
						if(res.OK()){
							//鍙戦�閫氱煡骞挎挱
							if(res.getNotice() != null){
								UIHelper.sendBroadCast(CommentPub.this, res.getNotice());
							}
							//杩斿洖鍒氬垰鍙戣〃鐨勮瘎璁�
							Intent intent = new Intent();
							intent.putExtra("COMMENT_SERIALIZABLE", res.getComment());
							setResult(RESULT_OK, intent);
							//璺宠浆鍒版枃绔犺鎯�
							finish();
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
						//鍙戣〃璇勮
						if(_replyid == 0){
							res = ac.pubComment(_catalog, _id, _uid, _content, _isPostToMyZone);
						}
						//瀵硅瘎璁鸿繘琛屽洖澶�
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
