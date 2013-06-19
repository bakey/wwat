package net.oschina.app.ui;

import java.io.File;

import net.oschina.app.AppContext;
import com.hkzhe.wwtt.R;
import net.oschina.app.common.FileUtils;
import net.oschina.app.common.MethodsCompat;
import net.oschina.app.common.UIHelper;
import net.oschina.app.common.UpdateManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class Setting extends PreferenceActivity{
	
	SharedPreferences mPreferences;
	Preference account;
	Preference myinfo;
	Preference cache;
	Preference feedback;
	Preference update;
	Preference about;
	CheckBoxPreference httpslogin;
	CheckBoxPreference loadimage;
	CheckBoxPreference scroll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//鐠佸墽鐤嗛弰鍓с仛Preferences
		addPreferencesFromResource(R.xml.preferences);
		//閼惧嘲绶盨haredPreferences
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);		
		
		ListView localListView = getListView();
		localListView.setBackgroundColor(0);
		localListView.setCacheColorHint(0);
		((ViewGroup)localListView.getParent()).removeView(localListView);
		ViewGroup localViewGroup = (ViewGroup)getLayoutInflater().inflate(R.layout.setting, null);
		((ViewGroup)localViewGroup.findViewById(R.id.setting_content)).addView(localListView, -1, -1);
		setContentView(localViewGroup);
	      
	    
		final AppContext ac = (AppContext)getApplication();
		
		//閻ц缍嶉妴浣规暈闁匡拷
		account = (Preference)findPreference("account");
		if(ac.isLogin()){
			account.setTitle(R.string.main_menu_logout);
		}else{
			account.setTitle(R.string.main_menu_login);
		}
		account.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				UIHelper.loginOrLogout(Setting.this);
				account.setTitle(R.string.main_menu_login);
				return true;
			}
		});
		
		//閹存垹娈戠挧鍕灐
		myinfo = (Preference)findPreference("myinfo");
		myinfo.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				UIHelper.showUserInfo(Setting.this);
				return true;
			}
		});
		
		//https閻ц缍�
		httpslogin = (CheckBoxPreference)findPreference("httpslogin");
		httpslogin.setChecked(ac.isHttpsLogin());
		if(ac.isHttpsLogin()){
			httpslogin.setSummary("瑜版挸澧犳禒锟紿TTPS 閻ц缍�");
		}else{
			httpslogin.setSummary("瑜版挸澧犳禒锟紿TTP 閻ц缍�");
		}
		httpslogin.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				ac.setConfigHttpsLogin(httpslogin.isChecked());
				if(httpslogin.isChecked()){
					httpslogin.setSummary("瑜版挸澧犳禒锟紿TTPS 閻ц缍�");
				}else{
					httpslogin.setSummary("瑜版挸澧犳禒锟紿TTP 閻ц缍�");
				}
				return true;
			}
		});
		
		//閸旂姾娴囬崶鍓уloadimage
		loadimage = (CheckBoxPreference)findPreference("loadimage");
		loadimage.setChecked(ac.isLoadImage());
		if(ac.isLoadImage()){
			loadimage.setSummary("妞ょ敻娼伴崝鐘烘祰閸ュ墽澧�(姒涙顓婚崷鈺揑FI缂冩垹绮舵稉瀣鏉炶棄娴橀悧锟�");
		}else{
			loadimage.setSummary("妞ょ敻娼版稉宥呭鏉炶棄娴橀悧锟�姒涙顓婚崷鈺揑FI缂冩垹绮舵稉瀣鏉炶棄娴橀悧锟�");
		}
		loadimage.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				UIHelper.changeSettingIsLoadImage(Setting.this,loadimage.isChecked());
				if(loadimage.isChecked()){
					loadimage.setSummary("妞ょ敻娼伴崝鐘烘祰閸ュ墽澧�(姒涙顓婚崷鈺揑FI缂冩垹绮舵稉瀣鏉炶棄娴橀悧锟�");
				}else{
					loadimage.setSummary("妞ょ敻娼版稉宥呭鏉炶棄娴橀悧锟�姒涙顓婚崷鈺揑FI缂冩垹绮舵稉瀣鏉炶棄娴橀悧锟�");
				}
				return true;
			}
		});
		
		//瀹革箑褰稿鎴濆З
		scroll = (CheckBoxPreference)findPreference("scroll");
		scroll.setChecked(ac.isScroll());
		scroll.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				ac.setConfigScroll(scroll.isChecked());
				if(scroll.isChecked()){
					scroll.setSummary("瀹告彃鎯庨悽銊ヤ箯閸欒櫕绮﹂崝锟�");
				}else{
					scroll.setSummary("瀹告彃鍙ч梻顓炰箯閸欒櫕绮﹂崝锟�");
				}
				return true;
			}
		});
		
		//鐠侊紕鐣荤紓鎾崇摠婢堆冪毈		
		long fileSize = 0;
		String cacheSize = "0KB";		
		File filesDir = getFilesDir();
		File cacheDir = getCacheDir();
		
		fileSize += FileUtils.getDirSize(filesDir);
		fileSize += FileUtils.getDirSize(cacheDir);		
		//2.2閻楀牊婀伴幍宥嗘箒鐏忓棗绨查悽銊х处鐎涙娴嗙粔璇插煂sd閸楋紕娈戦崝鐔诲厴
		if(AppContext.isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)){
			File externalCacheDir = MethodsCompat.getExternalCacheDir(this);
			fileSize += FileUtils.getDirSize(externalCacheDir);
		}		
		if(fileSize > 0)
			cacheSize = FileUtils.formatFileSize(fileSize);
		
		//濞撳懘娅庣紓鎾崇摠
		cache = (Preference)findPreference("cache");
		cache.setSummary(cacheSize);
		cache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				UIHelper.clearAppCache(Setting.this);
				cache.setSummary("0KB");
				return true;
			}
		});
		
		//閹板繗顬�崣宥夘湆
		feedback = (Preference)findPreference("feedback");
		feedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				UIHelper.showFeedBack(Setting.this);
				return true;
			}
		});
		
		//閻楀牊婀伴弴瀛樻煀
		update = (Preference)findPreference("update");
		update.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				UpdateManager.getUpdateManager().checkAppUpdate(Setting.this, true);
				return true;
			}
		});
		
		//閸忓厖绨幋鎴滄粦
		about = (Preference)findPreference("about");
		about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				UIHelper.showAbout(Setting.this);
				return true;
			}
		});
		
	}
	public void back(View paramView)
	{
		finish();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		if(intent.getBooleanExtra("LOGIN", false)){
			account.setTitle(R.string.main_menu_logout);
		}				
	}
}
