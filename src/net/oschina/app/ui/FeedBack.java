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
 * 鐢ㄦ埛鍙嶉
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
	
	//鍒濆鍖栬鍥炬帶浠�
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
				UIHelper.ToastMessage(v.getContext(), "鍙嶉淇℃伅涓嶈兘涓虹┖");
				return;
			}
			
			Intent i = new Intent(Intent.ACTION_SEND);  
			//i.setType("text/plain"); //妯℃嫙鍣�
			i.setType("message/rfc822") ; //鐪熸満
			i.putExtra(Intent.EXTRA_EMAIL, new String[]{"ld@oschina.net"});  
			i.putExtra(Intent.EXTRA_SUBJECT,"鐢ㄦ埛鍙嶉-Android瀹㈡埛绔�");  
			i.putExtra(Intent.EXTRA_TEXT,content);  
			startActivity(Intent.createChooser(i, "Sending mail..."));
			finish();
		}
	};
}
