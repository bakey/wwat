package net.oschina.app.widget;

import com.hkzhe.wwtt.R;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;

/**
 * 鍔犺浇瀵硅瘽妗嗘帶浠�
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class LoadingDialog extends Dialog {

	private Context mContext;
	private LayoutInflater inflater;
	private LayoutParams lp;

	public LoadingDialog(Context context) {
		super(context, R.style.Dialog);
		
		this.mContext = context;
		
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.loadingdialog, null);
		setContentView(layout);
		
		// 璁剧疆window灞炴�
		lp = getWindow().getAttributes();
		lp.gravity = Gravity.CENTER;
		lp.dimAmount = 0; // 鍘昏儗鏅伄鐩�
		lp.alpha = 1.0f;
		getWindow().setAttributes(lp);

	}
	
}