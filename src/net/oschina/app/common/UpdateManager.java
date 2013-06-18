package net.oschina.app.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import com.hkzhe.wwtt.R;
import net.oschina.app.api.ApiClient;
import net.oschina.app.bean.Update;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * 搴旂敤绋嬪簭鏇存柊宸ュ叿鍖�
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-6-29
 */
public class UpdateManager {

	private static final int DOWN_NOSDCARD = 0;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
	
	private static UpdateManager updateManager;
	
	private Context mContext;
	//閫氱煡瀵硅瘽妗�
	private Dialog noticeDialog;
	//涓嬭浇瀵硅瘽妗�
	private Dialog downloadDialog;
    //杩涘害鏉�
    private ProgressBar mProgress;
    //鏌ヨ鍔ㄧ敾
    private ProgressDialog mProDialog;
    
    private int progress;
    //涓嬭浇绾跨▼
    private Thread downLoadThread;
    //缁堟鏍囪
    private boolean interceptFlag = false;
	//鎻愮ず璇�
	private String updateMsg = "";
	//杩斿洖鐨勫畨瑁呭寘url
	private String apkUrl = "";
	//涓嬭浇鍖呬繚瀛樿矾寰�
    private String savePath = "";
	//apk淇濆瓨瀹屾暣璺緞
	private String apkFilePath = "";
	
	private String curVersionName = "";
	private int curVersionCode;
	private Update mUpdate;
    
    private Handler mHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				break;
			case DOWN_OVER:
				downloadDialog.dismiss();
				installApk();
				break;
			case DOWN_NOSDCARD:
				downloadDialog.dismiss();
				Toast.makeText(mContext, "鏃犳硶涓嬭浇瀹夎鏂囦欢锛岃妫�煡SD鍗℃槸鍚︽寕杞�", 3000).show();
				break;
			}
    	};
    };
    
	public static UpdateManager getUpdateManager() {
		if(updateManager == null){
			updateManager = new UpdateManager();
		}
		return updateManager;
	}
	
	/**
	 * 妫�煡App鏇存柊
	 * @param context
	 * @param isShowMsg 鏄惁鏄剧ず鎻愮ず娑堟伅
	 */
	public void checkAppUpdate(Context context, final boolean isShowMsg){
		this.mContext = context;
		getCurrentVersion();
		if(isShowMsg)
			mProDialog = ProgressDialog.show(mContext, null, "姝ｅ湪妫�祴锛岃绋嶅悗...", true, true);
		final Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				if(isShowMsg && mProDialog != null)
					mProDialog.dismiss();
				if(msg.what == 1){
					mUpdate = (Update)msg.obj;
					if(mUpdate != null){
						if(curVersionCode < mUpdate.getVersionCode()){
							apkUrl = mUpdate.getDownloadUrl();
							updateMsg = mUpdate.getUpdateLog();
							showNoticeDialog();
						}else if(isShowMsg){
							AlertDialog.Builder builder = new Builder(mContext);
							builder.setTitle("绯荤粺鎻愮ず");
							builder.setMessage("鎮ㄥ綋鍓嶅凡缁忔槸鏈�柊鐗堟湰");
							builder.setPositiveButton("纭畾", null);
							builder.create().show();
						}
					}
				}else if(isShowMsg){
					AlertDialog.Builder builder = new Builder(mContext);
					builder.setTitle("绯荤粺鎻愮ず");
					builder.setMessage("鏃犳硶鑾峰彇鐗堟湰鏇存柊淇℃伅");
					builder.setPositiveButton("纭畾", null);
					builder.create().show();
				}
			}			
		};
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {					
					Update update = ApiClient.checkVersion((AppContext)mContext.getApplicationContext());
					msg.what = 1;
					msg.obj = update;
				} catch (AppException e) {
					e.printStackTrace();
				}
				handler.sendMessage(msg);
			}			
		}.start();		
	}	
	
	/**
	 * 鑾峰彇褰撳墠瀹㈡埛绔増鏈俊鎭�
	 */
	private void getCurrentVersion(){
        try { 
        	PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        	curVersionName = info.versionName;
        	curVersionCode = info.versionCode;
        } catch (NameNotFoundException e) {    
			e.printStackTrace(System.err);
		} 
	}
	
	/**
	 * 鏄剧ず鐗堟湰鏇存柊閫氱煡瀵硅瘽妗�
	 */
	private void showNoticeDialog(){
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("杞欢鐗堟湰鏇存柊");
		builder.setMessage(updateMsg);
		builder.setPositiveButton("绔嬪嵆鏇存柊", new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showDownloadDialog();			
			}
		});
		builder.setNegativeButton("浠ュ悗鍐嶈", new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();				
			}
		});
		noticeDialog = builder.create();
		noticeDialog.show();
	}
	
	/**
	 * 鏄剧ず涓嬭浇瀵硅瘽妗�
	 */
	private void showDownloadDialog(){
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("姝ｅ湪涓嬭浇鏂扮増鏈�");
		
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.update_progress, null);
		mProgress = (ProgressBar)v.findViewById(R.id.update_progress);
		
		builder.setView(v);
		builder.setNegativeButton("鍙栨秷", new OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		downloadDialog = builder.create();
		downloadDialog.show();
		
		downloadApk();
	}
	
	private Runnable mdownApkRunnable = new Runnable() {	
		@Override
		public void run() {
			try {
				String apkName = "OSChinaApp_"+mUpdate.getVersionName()+".apk";
				//鍒ゆ柇鏄惁鎸傝浇浜哠D鍗�
				String storageState = Environment.getExternalStorageState();		
				if(storageState.equals(Environment.MEDIA_MOUNTED)){
					savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OSChina/Update/";
					File file = new File(savePath);
					if(!file.exists()){
						file.mkdirs();
					}
					apkFilePath = savePath + apkName;
				}
				
				//娌℃湁鎸傝浇SD鍗★紝鏃犳硶涓嬭浇鏂囦欢
				if(apkFilePath == null || apkFilePath == ""){
					mHandler.sendEmptyMessage(DOWN_NOSDCARD);
					return;
				}
				
				File ApkFile = new File(apkFilePath);
				
				//鏄惁宸蹭笅杞芥洿鏂版枃浠�
				if(ApkFile.exists()){
					downloadDialog.dismiss();
					installApk();
					return;
				}
				
				FileOutputStream fos = new FileOutputStream(ApkFile);
				
				URL url = new URL(apkUrl);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();
				
				int count = 0;
				byte buf[] = new byte[1024];
				
				do{   		   		
		    		int numread = is.read(buf);
		    		count += numread;
		    	    progress =(int)(((float)count / length) * 100);
		    	    //鏇存柊杩涘害
		    	    mHandler.sendEmptyMessage(DOWN_UPDATE);
		    		if(numread <= 0){	
		    			//涓嬭浇瀹屾垚閫氱煡瀹夎
		    			mHandler.sendEmptyMessage(DOWN_OVER);
		    			break;
		    		}
		    		fos.write(buf,0,numread);
		    	}while(!interceptFlag);//鐐瑰嚮鍙栨秷灏卞仠姝笅杞�
				
				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch(IOException e){
				e.printStackTrace();
			}
			
		}
	};
	
	/**
	* 涓嬭浇apk
	* @param url
	*/	
	private void downloadApk(){
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}
	
	/**
    * 瀹夎apk
    * @param url
    */
	private void installApk(){
		File apkfile = new File(apkFilePath);
        if (!apkfile.exists()) {
            return;
        }    
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive"); 
        mContext.startActivity(i);
	}
}
