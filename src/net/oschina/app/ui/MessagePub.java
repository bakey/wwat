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
 * 閸欐垼銆冮悾娆掆枅
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
	
    //閸掓繂顬婇崠鏍瀰閸ョ偓甯舵禒锟�  
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
    	//缂傛牞绶崳銊﹀潑閸旂姵鏋冮張顒傛磧閸氾拷
    	mContent.addTextChangedListener(UIHelper.getTextWatcher(this, tempMessageKey));
    	
    	//閺勫墽銇氭稉瀛樻缂傛牞绶崘鍛啇
    	UIHelper.showTempEditContent(this, mContent, tempMessageKey);
    	
    	mReceiver.setText("閸欐垿锟介悾娆掆枅缂侊拷 "+_friendname);
    }    
	
	private View.OnClickListener publishClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			//闂呮劘妫屾潪顖炴暛閻╋拷
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
			
			_content = mContent.getText().toString();
			if(StringUtils.isEmpty(_content)){
				UIHelper.ToastMessage(v.getContext(), "鐠囩柉绶崗銉ф殌鐟凤拷鍞寸�锟�");
				return;
			}
			
			final AppContext ac = (AppContext)getApplication();
			if(!ac.isLogin()){
				UIHelper.showLoginDialog(MessagePub.this);
				return;
			}
			
			mProgress = ProgressDialog.show(v.getContext(), null, "閸欐垿锟芥稉顒讳豢浠匡拷",true,true); 
			
			final Handler handler = new Handler(){
				public void handleMessage(Message msg) {
					
					if(mProgress!=null)mProgress.dismiss();
					
					if(msg.what == 1){
						Result res = (Result)msg.obj;
						UIHelper.ToastMessage(MessagePub.this, res.getErrorMessage());
						if(res.OK()){
							//閸欐垿锟介柅姘辩叀楠炴寧鎸�
							if(res.getNotice() != null){
								UIHelper.sendBroadCast(MessagePub.this, res.getNotice());
							}
							//濞撳懘娅庢稊瀣娣囨繂鐡ㄩ惃鍕椽鏉堟垵鍞寸�锟�							ac.removeProperty(tempMessageKey);
							//鏉╂柨娲栭崚姘灠閸欐垼銆冮惃鍕槑鐠侊拷
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
