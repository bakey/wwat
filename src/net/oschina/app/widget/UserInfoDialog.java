package net.oschina.app.widget;

import com.hkzhe.wwtt.R;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager.LayoutParams;

/**
 * 鐢ㄦ埛淇℃伅瀵硅瘽妗嗘帶浠�
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-7-2
 */
public class UserInfoDialog extends Dialog {
	
	private LayoutParams lp;

	public UserInfoDialog(Context context) {
		super(context, R.style.Dialog);		
		setContentView(R.layout.user_center_content);
		
		// 璁剧疆鐐瑰嚮瀵硅瘽妗嗕箣澶栬兘娑堝け
		setCanceledOnTouchOutside(true);
		// 璁剧疆window灞炴�
		lp = getWindow().getAttributes();
		lp.gravity = Gravity.TOP;
		lp.dimAmount = 0; // 鍘昏儗鏅伄鐩�
		lp.alpha = 1.0f;
		lp.y = 55;
		getWindow().setAttributes(lp);

	}
}
