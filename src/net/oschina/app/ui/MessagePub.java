package net.oschina.app.ui;

import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import com.hkzhe.wwtt.R;
import net.oschina.app.bean.Result;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 鍙戣〃鐣欒█
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class MessagePub extends Activity{
	
	private ImageView mBack;
	private TextView mReceiver;
	private EditText mContent;
	private Button mPublish;
    private ProgressDialog mProgress;
    private InputMethodManager imm;
    private String tempMessageKey = AppConfig.TEMP_MESSAGE;
	
	private int _uid;
	private int _friendid;
	private String _friendname;
	private String _content;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_pub);
		
		this.initView();
	}
	
    //鍒濆鍖栬鍥炬帶浠�
    private void initView()
    {
    	imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
    	
		_uid = getIntent().getIntExtra("user_id", 0);
		_friendid = getIntent().getIntExtra("friend_id", 0);
		_friendname = getIntent().getStringExtra("friend_name");
    	
		if(_friendid > 0) tempMessageKey = AppConfig.TEMP_MESSAGE + "_" + _friendid;
		
    	mBack = (ImageView)findViewById(R.id.message_pub_back);
    	mPublish = (Button)findViewById(R.id.message_pub_publish);
    	mContent = (EditText)findViewById(R.id.message_pub_content);
    	mReceiver = (TextView)findViewById(R.id.message_pub_receiver);
    	
    	mBack.setOnClickListener(UIHelper.finish(this));
    	mPublish.setOnClickListener(publishClickListener);
    	//缂栬緫鍣ㄦ坊鍔犳枃鏈洃鍚�
    	mContent.addTextChangedListener(UIHelper.getTextWatcher(this, tempMessageKey));
    	
    	//鏄剧ず涓存椂缂栬緫鍐呭
    	UIHelper.showTempEditContent(this, mContent, tempMessageKey);
    	
    	mReceiver.setText("鍙戦�鐣欒█缁� "+_friendname);
    }    
	
	private View.OnClickListener publishClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			//闅愯棌杞敭鐩�
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
			
			_content = mContent.getText().toString();
			if(StringUtils.isEmpty(_content)){
				UIHelper.ToastMessage(v.getContext(), "璇疯緭鍏ョ暀瑷�唴瀹�");
				return;
			}
			
			final AppContext ac = (AppContext)getApplication();
			if(!ac.isLogin()){
				UIHelper.showLoginDialog(MessagePub.this);
				return;
			}
			
			mProgress = ProgressDialog.show(v.getContext(), null, "鍙戦�涓仿仿�",true,true); 
			
			final Handler handler = new Handler(){
				public void handleMessage(Message msg) {
					
					if(mProgress!=null)mProgress.dismiss();
					
					if(msg.what == 1){
						Result res = (Result)msg.obj;
						UIHelper.ToastMessage(MessagePub.this, res.getErrorMessage());
						if(res.OK()){
							//鍙戦�閫氱煡骞挎挱
							if(res.getNotice() != null){
								UIHelper.sendBroadCast(MessagePub.this, res.getNotice());
							}
							//娓呴櫎涔嬪墠淇濆瓨鐨勭紪杈戝唴瀹�
							ac.removeProperty(tempMessageKey);
							//杩斿洖鍒氬垰鍙戣〃鐨勮瘎璁�
							Intent intent = new Intent();
							intent.putExtra("COMMENT_SERIALIZABLE", res.getComment());
							setResult(RESULT_OK, intent);
							finish();
						}
					}
					else {
						((AppException)msg.obj).makeToast(MessagePub.this);
					}
				}
			};
			new Thread(){
				public void run() {
					Message msg =new Message();
					try {
						Result res = ac.pubMessage(_uid, _friendid, _content);
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
