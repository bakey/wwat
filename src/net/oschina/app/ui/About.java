package net.oschina.app.ui;

import com.hkzhe.wwtt.R;
import net.oschina.app.common.UpdateManager;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 閸忓厖绨幋鎴滄粦
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class About extends Activity{
	
	private TextView mVersion;
	private Button mUpdate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		//閼惧嘲褰囩�銏″煕缁旑垳澧楅張顑夸繆閹拷
        try { 
        	PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
        	mVersion = (TextView)findViewById(R.id.about_version);
    		mVersion.setText("閻楀牊婀伴敍锟�"+info.versionName);
        } catch (NameNotFoundException e) {    
			e.printStackTrace(System.err);
		} 
        
        mUpdate = (Button)findViewById(R.id.about_update);
        mUpdate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				UpdateManager.getUpdateManager().checkAppUpdate(About.this, true);
			}
		});
	}
}
