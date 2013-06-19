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
 * 鎼存梻鏁ょ粙瀣碍閺囧瓨鏌婂銉ュ徔閸栵拷
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
	//闁氨鐓＄�纭呯樈濡楋拷
	private Dialog noticeDialog;
	//娑撳娴囩�纭呯樈濡楋拷
	private Dialog downloadDialog;
    //鏉╂稑瀹抽弶锟�  
	private ProgressBar mProgress;
    //閺屻儴顕楅崝銊ф暰
    private ProgressDialog mProDialog;
    
    private int progress;
    //娑撳娴囩痪璺ㄢ柤
    private Thread downLoadThread;
    //缂佸牊顒涢弽鍥唶
    private boolean interceptFlag = false;
	//閹绘劗銇氱拠锟�
    private String updateMsg = "";
	//鏉╂柨娲栭惃鍕暔鐟佸懎瀵榰rl
	private String apkUrl = "";
	//娑撳娴囬崠鍛箽鐎涙鐭惧锟� 
	private String savePath = "";
	//apk娣囨繂鐡ㄧ�灞炬殻鐠侯垰绶�
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
				Toast.makeText(mContext, "閺冪姵纭舵稉瀣祰鐎瑰顥栭弬鍥︽閿涘矁顕Λ锟界叀SD閸椻剝妲搁崥锔藉瘯鏉烇拷", 3000).show();
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
	 * 濡拷鐓pp閺囧瓨鏌�
	 * @param context
	 * @param isShowMsg 閺勵垰鎯侀弰鍓с仛閹绘劗銇氬☉鍫熶紖
	 */
	public void checkAppUpdate(Context context, final boolean isShowMsg){
		this.mContext = context;
		getCurrentVersion();
		if(isShowMsg)
			mProDialog = ProgressDialog.show(mContext, null, "濮濓絽婀Λ锟界ゴ閿涘矁顕粙宥呮倵...", true, true);
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
							builder.setTitle("缁崵绮洪幓鎰仛");
							builder.setMessage("閹劌缍嬮崜宥呭嚒缂佸繑妲搁張锟芥煀閻楀牊婀�");
							builder.setPositiveButton("绾喖鐣�", null);
							builder.create().show();
						}
					}
				}else if(isShowMsg){
					AlertDialog.Builder builder = new Builder(mContext);
					builder.setTitle("缁崵绮洪幓鎰仛");
					builder.setMessage("閺冪姵纭堕懢宄板絿閻楀牊婀伴弴瀛樻煀娣団剝浼�");
					builder.setPositiveButton("绾喖鐣�", null);
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
	 * 閼惧嘲褰囪ぐ鎾冲鐎广垺鍩涚粩顖滃閺堫兛淇婇幁锟�	 */
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
	 * 閺勫墽銇氶悧鍫熸拱閺囧瓨鏌婇柅姘辩叀鐎电鐦藉锟�	 */
	private void showNoticeDialog(){
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("鏉烆垯娆㈤悧鍫熸拱閺囧瓨鏌�");
		builder.setMessage(updateMsg);
		builder.setPositiveButton("缁斿宓嗛弴瀛樻煀", new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showDownloadDialog();			
			}
		});
		builder.setNegativeButton("娴犮儱鎮楅崘宥堫嚛", new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();				
			}
		});
		noticeDialog = builder.create();
		noticeDialog.show();
	}
	
	/**
	 * 閺勫墽銇氭稉瀣祰鐎电鐦藉锟�	 */
	private void showDownloadDialog(){
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("濮濓絽婀稉瀣祰閺傛壆澧楅張锟�");
		
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.update_progress, null);
		mProgress = (ProgressBar)v.findViewById(R.id.update_progress);
		
		builder.setView(v);
		builder.setNegativeButton("閸欐牗绉�", new OnClickListener() {	
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
				//閸掋倖鏌囬弰顖氭儊閹稿倽娴囨禍鍝燚閸楋拷
				String storageState = Environment.getExternalStorageState();		
				if(storageState.equals(Environment.MEDIA_MOUNTED)){
					savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OSChina/Update/";
					File file = new File(savePath);
					if(!file.exists()){
						file.mkdirs();
					}
					apkFilePath = savePath + apkName;
				}
				
				//濞屸剝婀侀幐鍌濇祰SD閸椻槄绱濋弮鐘崇《娑撳娴囬弬鍥︽
				if(apkFilePath == null || apkFilePath == ""){
					mHandler.sendEmptyMessage(DOWN_NOSDCARD);
					return;
				}
				
				File ApkFile = new File(apkFilePath);
				
				//閺勵垰鎯佸韫瑓鏉炶姤娲块弬鐗堟瀮娴狅拷
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
		    	    //閺囧瓨鏌婃潻娑樺
		    	    mHandler.sendEmptyMessage(DOWN_UPDATE);
		    		if(numread <= 0){	
		    			//娑撳娴囩�灞惧灇闁氨鐓＄�澶庮棖
		    			mHandler.sendEmptyMessage(DOWN_OVER);
		    			break;
		    		}
		    		fos.write(buf,0,numread);
		    	}while(!interceptFlag);//閻愮懓鍤崣鏍ㄧХ鐏忓崬浠犲顤嶇瑓鏉烇拷
				
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
	* 娑撳娴嘺pk
	* @param url
	*/	
	private void downloadApk(){
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}
	
	/**
    * 鐎瑰顥朼pk
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
