package net.oschina.app.ui;

import com.hkzhe.wwtt.R;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * 閻劍鍩涢崣宥夘湆
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class FeedBack extends Activity{
	
	private ImageButton mClose;
	private EditText mEditer;
	private Button mPublish;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback);
		
		this.initView();
	}
	
	//閸掓繂顬婇崠鏍瀰閸ョ偓甯舵禒锟� 
	private void initView()
    {
    	mClose = (ImageButton)findViewById(R.id.feedback_close_button);
    	mEditer = (EditText)findViewById(R.id.feedback_content);
    	mPublish = (Button)findViewById(R.id.feedback_publish);
    	
    	mClose.setOnClickListener(UIHelper.finish(this));
    	mPublish.setOnClickListener(publishClickListener);
    }
    
    private View.OnClickListener publishClickListener = new View.OnClickListener() {		
		public void onClick(View v) {
			String content = mEditer.getText().toString();
			
			if(StringUtils.isEmpty(content)) {
				UIHelper.ToastMessage(v.getContext(), "閸欏秹顪屾穱鈩冧紖娑撳秷鍏樻稉铏光敄");
				return;
			}
			
			Intent i = new Intent(Intent.ACTION_SEND);  
			//i.setType("text/plain"); //濡剝瀚欓崳锟�			i.setType("message/rfc822") ; //閻喐婧�
			i.putExtra(Intent.EXTRA_EMAIL, new String[]{"ld@oschina.net"});  
			i.putExtra(Intent.EXTRA_SUBJECT,"閻劍鍩涢崣宥夘湆-Android鐎广垺鍩涚粩锟�");  
			i.putExtra(Intent.EXTRA_TEXT,content);  
			startActivity(Intent.createChooser(i, "Sending mail..."));
			finish();
		}
	};
}
