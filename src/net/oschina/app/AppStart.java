package net.oschina.app;

import com.hkzhe.wwtt.R;

import net.oschina.app.common.StringUtils;
import net.oschina.app.ui.Main;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/**
 * 鎼存梻鏁ょ粙瀣碍閸氼垰濮╃猾浼欑窗閺勫墽銇氬▎銏ｇ箣閻ｅ矂娼伴獮鎯扮儲鏉烆剙鍩屾稉鑽ゆ櫕闂堬拷
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class AppStart extends Activity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = View.inflate(this, R.layout.start, null);
		setContentView(view);
        
		//濞撴劕褰夌仦鏇犮仛閸氼垰濮╃仦锟�		
		AlphaAnimation aa = new AlphaAnimation(0.3f,1.0f);
		aa.setDuration(2000);
		view.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationEnd(Animation arg0) {
				redirectTo();
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationStart(Animation animation) {}
			
		});
		
		//閸忕厧顔愭担搴ｅ閺堢惂ookie閿涳拷.5閻楀牊婀版禒銉ょ瑓閿涘苯瀵橀幏锟�5.0,1.5.1閿涳拷
		AppContext appContext = (AppContext)getApplication();
		String cookie = appContext.getProperty("cookie");
		if(StringUtils.isEmpty(cookie)) {
			String cookie_name = appContext.getProperty("cookie_name");
			String cookie_value = appContext.getProperty("cookie_value");
			if(!StringUtils.isEmpty(cookie_name) && !StringUtils.isEmpty(cookie_value)) {
				cookie = cookie_name + "=" + cookie_value;
				appContext.setProperty("cookie", cookie);
				appContext.removeProperty("cookie_domain","cookie_name","cookie_value","cookie_version","cookie_path");
			}
		}
    }
    
    /**
     * 鐠哄疇娴嗛崚锟�.
     */
    private void redirectTo(){        
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
        finish();
    }
}