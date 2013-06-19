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
 * 閸欐垼銆冪敮鏍х摍
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
	
    //閸掓繂顬婇崠鏍瀰閸ョ偓甯舵禒锟�   
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
    	//缂傛牞绶崳銊﹀潑閸旂姵鏋冮張顒傛磧閸氾拷
    	mTitle.addTextChangedListener(UIHelper.getTextWatcher(this, AppConfig.TEMP_POST_TITLE));
    	mContent.addTextChangedListener(UIHelper.getTextWatcher(this, AppConfig.TEMP_POST_CONTENT));
    	
    	//閺勫墽銇氭稉瀛樻缂傛牞绶崘鍛啇
    	UIHelper.showTempEditContent(this, mTitle, AppConfig.TEMP_POST_TITLE);
    	UIHelper.showTempEditContent(this, mContent, AppConfig.TEMP_POST_CONTENT);
    	//閺勫墽銇氭稉瀛樻闁瀚ㄩ崚鍡欒
    	String position = ((AppContext)getApplication()).getProperty(AppConfig.TEMP_POST_CATALOG);
    	mCatalog.setSelection(StringUtils.toInt(position, 0));
    }
	
    private AdapterView.OnItemSelectedListener catalogSelectedListener = new AdapterView.OnItemSelectedListener(){
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			//娣囨繂鐡ㄦ稉瀛樻闁瀚ㄩ惃鍕瀻缁拷
			((AppContext)getApplication()).setProperty(AppConfig.TEMP_POST_CATALOG, position+"");
		}
		public void onNothingSelected(AdapterView<?> parent) {}
    };
    
	private View.OnClickListener publishClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			//闂呮劘妫屾潪顖炴暛閻╋拷
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
			
			String title = mTitle.getText().toString();
			if(StringUtils.isEmpty(title)){
				UIHelper.ToastMessage(v.getContext(), "鐠囩柉绶崗銉︾垼妫帮拷");
				return;
			}
			String content = mContent.getText().toString();
			if(StringUtils.isEmpty(content)){
				UIHelper.ToastMessage(v.getContext(), "鐠囩柉绶崗銉﹀絹闂傤喖鍞寸�锟�");
				return;
			}
			
			final AppContext ac = (AppContext)getApplication();
			if(!ac.isLogin()){
				UIHelper.showLoginDialog(QuestionPub.this);
				return;
			}
			
			mProgress = ProgressDialog.show(v.getContext(), null, "閸欐垵绔锋稉顒讳豢浠匡拷",true,true); 
			
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
							//閸欐垿锟介柅姘辩叀楠炴寧鎸�
							if(res.getNotice() != null){
								UIHelper.sendBroadCast(QuestionPub.this, res.getNotice());
							}
							//濞撳懘娅庢稊瀣娣囨繂鐡ㄩ惃鍕椽鏉堟垵鍞寸�锟�							ac.removeProperty(AppConfig.TEMP_POST_TITLE,AppConfig.TEMP_POST_CATALOG,AppConfig.TEMP_POST_CONTENT);
							//鐠哄疇娴嗛崚鐗堟瀮缁旂姾顕涢幆锟�							finish();
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
