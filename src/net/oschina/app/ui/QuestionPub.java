package net.oschina.app.ui;

import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import com.hkzhe.wwtt.R;
import net.oschina.app.bean.Post;
import net.oschina.app.bean.Result;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

/**
 * 鍙戣〃甯栧瓙
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class QuestionPub extends Activity{

	private ImageView mBack;
	private EditText mTitle;
	private EditText mContent;
	private Spinner mCatalog;
	private CheckBox mEmail;
	private Button mPublish;
    private ProgressDialog mProgress;
	private Post post;
	private InputMethodManager imm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_pub);
		
		this.initView();
		
	}
	
    //鍒濆鍖栬鍥炬帶浠�
    private void initView()
    {    	
    	imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
    	
    	mBack = (ImageView)findViewById(R.id.question_pub_back);
    	mPublish = (Button)findViewById(R.id.question_pub_publish);
    	mTitle = (EditText)findViewById(R.id.question_pub_title);
    	mContent = (EditText)findViewById(R.id.question_pub_content);
    	mEmail = (CheckBox)findViewById(R.id.question_pub_email);
    	mCatalog = (Spinner)findViewById(R.id.question_pub_catalog);
    	
    	mBack.setOnClickListener(UIHelper.finish(this));
    	mPublish.setOnClickListener(publishClickListener);
    	mCatalog.setOnItemSelectedListener(catalogSelectedListener);
    	//缂栬緫鍣ㄦ坊鍔犳枃鏈洃鍚�
    	mTitle.addTextChangedListener(UIHelper.getTextWatcher(this, AppConfig.TEMP_POST_TITLE));
    	mContent.addTextChangedListener(UIHelper.getTextWatcher(this, AppConfig.TEMP_POST_CONTENT));
    	
    	//鏄剧ず涓存椂缂栬緫鍐呭
    	UIHelper.showTempEditContent(this, mTitle, AppConfig.TEMP_POST_TITLE);
    	UIHelper.showTempEditContent(this, mContent, AppConfig.TEMP_POST_CONTENT);
    	//鏄剧ず涓存椂閫夋嫨鍒嗙被
    	String position = ((AppContext)getApplication()).getProperty(AppConfig.TEMP_POST_CATALOG);
    	mCatalog.setSelection(StringUtils.toInt(position, 0));
    }
	
    private AdapterView.OnItemSelectedListener catalogSelectedListener = new AdapterView.OnItemSelectedListener(){
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			//淇濆瓨涓存椂閫夋嫨鐨勫垎绫�
			((AppContext)getApplication()).setProperty(AppConfig.TEMP_POST_CATALOG, position+"");
		}
		public void onNothingSelected(AdapterView<?> parent) {}
    };
    
	private View.OnClickListener publishClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			//闅愯棌杞敭鐩�
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
			
			String title = mTitle.getText().toString();
			if(StringUtils.isEmpty(title)){
				UIHelper.ToastMessage(v.getContext(), "璇疯緭鍏ユ爣棰�");
				return;
			}
			String content = mContent.getText().toString();
			if(StringUtils.isEmpty(content)){
				UIHelper.ToastMessage(v.getContext(), "璇疯緭鍏ユ彁闂唴瀹�");
				return;
			}
			
			final AppContext ac = (AppContext)getApplication();
			if(!ac.isLogin()){
				UIHelper.showLoginDialog(QuestionPub.this);
				return;
			}
			
			mProgress = ProgressDialog.show(v.getContext(), null, "鍙戝竷涓仿仿�",true,true); 
			
			post = new Post();
			post.setAuthorId(ac.getLoginUid());
			post.setTitle(title);
			post.setBody(content);
			post.setCatalog(mCatalog.getSelectedItemPosition()+1);
			if(mEmail.isChecked())
				post.setIsNoticeMe(1);
			
			final Handler handler = new Handler(){
				public void handleMessage(Message msg) {
					if(mProgress!=null)mProgress.dismiss();
					if(msg.what == 1){
						Result res = (Result)msg.obj;
						UIHelper.ToastMessage(QuestionPub.this, res.getErrorMessage());
						if(res.OK()){
							//鍙戦�閫氱煡骞挎挱
							if(res.getNotice() != null){
								UIHelper.sendBroadCast(QuestionPub.this, res.getNotice());
							}
							//娓呴櫎涔嬪墠淇濆瓨鐨勭紪杈戝唴瀹�
							ac.removeProperty(AppConfig.TEMP_POST_TITLE,AppConfig.TEMP_POST_CATALOG,AppConfig.TEMP_POST_CONTENT);
							//璺宠浆鍒版枃绔犺鎯�
							finish();
						}
					}
					else {
						((AppException)msg.obj).makeToast(QuestionPub.this);
					}
				}
			};
			new Thread(){
				public void run() {
					Message msg = new Message();					
					try {
						Result res = ac.pubPost(post);
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
