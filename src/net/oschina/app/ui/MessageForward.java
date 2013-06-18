package net.oschina.app.ui;

import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import com.hkzhe.wwtt.R;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * 杞彂鐣欒█
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class MessageForward extends Activity{
	
	private ImageView mBack;
	private EditText mReceiver;
	private EditText mContent;
	private Button mPublish;
    private ProgressDialog mProgress;
    private InputMethodManager imm;
	
	private int _uid;
	private String _content;
	private String _receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_forward);
		
		this.initView();
	}
	
    //鍒濆鍖栬鍥炬帶浠�
    private void initView()
    {
    	imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
    	
    	String friend_name = "@" + getIntent().getStringExtra("friend_name") + " ";
		_uid = getIntent().getIntExtra("user_id", 0);
		_content = friend_name + getIntent().getStringExtra("message_content");
    	
    	mBack = (ImageView)findViewById(R.id.message_forward_back);
    	mPublish = (Button)findViewById(R.id.message_forward_publish);
    	mContent = (EditText)findViewById(R.id.message_forward_content);
    	mReceiver = (EditText)findViewById(R.id.message_forward_receiver);
    	
    	mBack.setOnClickListener(UIHelper.finish(this));
    	mPublish.setOnClickListener(publishClickListener);
    	
    	mContent.setText(_content);
    }    
	
	private View.OnClickListener publishClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			//闅愯棌杞敭鐩�
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
			
			_content = mContent.getText().toString();
			_receiver = mReceiver.getText().toString();
			if(StringUtils.isEmpty(_content)){
				UIHelper.ToastMessage(v.getContext(), "璇疯緭鍏ョ暀瑷�唴瀹�");
				return;
			}
			if(StringUtils.isEmpty(_receiver)){
				UIHelper.ToastMessage(v.getContext(), "璇疯緭鍏ュ鏂圭殑鏄电О");
				return;
			}
			
			final AppContext ac = (AppContext)getApplication();
			if(!ac.isLogin()){
				UIHelper.showLoginDialog(MessageForward.this);
				return;
			}
			
			mProgress = ProgressDialog.show(v.getContext(), null, "鍙戦�涓仿仿�",true,true); 
			
			final Handler handler = new Handler(){
				public void handleMessage(Message msg) {
					
					if(mProgress!=null)mProgress.dismiss();
					
					if(msg.what == 1){
						Result res = (Result)msg.obj;
						UIHelper.ToastMessage(MessageForward.this, res.getErrorMessage());
						if(res.OK()){
							//鍙戦�閫氱煡骞挎挱
							if(res.getNotice() != null){
								UIHelper.sendBroadCast(MessageForward.this, res.getNotice());
							}
							finish();
						}
					}
					else {
						((AppException)msg.obj).makeToast(MessageForward.this);
					}
				}
			};
			new Thread(){
				public void run() {
					Message msg =new Message();
					try {
						Result res = ac.forwardMessage(_uid, _receiver, _content);
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
