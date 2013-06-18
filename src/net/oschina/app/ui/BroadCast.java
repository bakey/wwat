package net.oschina.app.ui;

import com.hkzhe.wwtt.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * 閫氱煡淇℃伅骞挎挱鎺ユ敹鍣�
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-4-16
 */
public class BroadCast extends BroadcastReceiver {

	private final static int NOTIFICATION_ID = R.layout.main;
	
	private static int lastNoticeCount;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String ACTION_NAME = intent.getAction();
		if("net.oschina.app.action.APPWIDGET_UPDATE".equals(ACTION_NAME))
		{	
			int atmeCount = intent.getIntExtra("atmeCount", 0);//@鎴�
			int msgCount = intent.getIntExtra("msgCount", 0);//鐣欒█
			int reviewCount = intent.getIntExtra("reviewCount", 0);//璇勮
			int newFansCount = intent.getIntExtra("newFansCount", 0);//鏂扮矇涓�
			int activeCount = atmeCount + reviewCount + msgCount + newFansCount;//淇℃伅鎬绘暟
			
			//鍔ㄦ�-鎬绘暟
			if(Main.bv_active != null){
				if(activeCount > 0){
					Main.bv_active.setText(activeCount+"");
					Main.bv_active.show();
				}else{
					Main.bv_active.setText("");
					Main.bv_active.hide();
				}
			}
			//@鎴�
			if(Main.bv_atme != null){
				if(atmeCount > 0){
					Main.bv_atme.setText(atmeCount+"");
					Main.bv_atme.show();
				}else{
					Main.bv_atme.setText("");
					Main.bv_atme.hide();
				}
			}
			//璇勮
			if(Main.bv_review != null){
				if(reviewCount > 0){
					Main.bv_review.setText(reviewCount+"");
					Main.bv_review.show();
				}else{
					Main.bv_review.setText("");
					Main.bv_review.hide();
				}
			}
			//鐣欒█
			if(Main.bv_message != null){
				if(msgCount > 0){
					Main.bv_message.setText(msgCount+"");
					Main.bv_message.show();
				}else{
					Main.bv_message.setText("");
					Main.bv_message.hide();
				}
			}
			
			//閫氱煡鏍忔樉绀�
			this.notification(context, activeCount);
		}
	}

	private void notification(Context context, int noticeCount){		
		//鍒涘缓 NotificationManager
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		String contentTitle = "寮�簮涓浗";
		String contentText = "鎮ㄦ湁 " + noticeCount + " 鏉℃渶鏂颁俊鎭�";
		int _lastNoticeCount;
		
		//鍒ゆ柇鏄惁鍙戝嚭閫氱煡淇℃伅
		if(noticeCount == 0)
		{
			notificationManager.cancelAll();
			lastNoticeCount = 0;
			return;
		}
		else if(noticeCount == lastNoticeCount)
		{
			return; 
		}
		else
		{
			_lastNoticeCount = lastNoticeCount;
			lastNoticeCount = noticeCount;
		}
		
		//鍒涘缓閫氱煡 Notification
		Notification notification = null;
		
		if(noticeCount > _lastNoticeCount) 
		{
			String noticeTitle = "鎮ㄦ湁 " + (noticeCount-_lastNoticeCount) + " 鏉℃渶鏂颁俊鎭�";
			notification = new Notification(R.drawable.icon, noticeTitle, System.currentTimeMillis());
		}
		else
		{
			notification = new Notification();
		}
		
		//璁剧疆鐐瑰嚮閫氱煡璺宠浆
		Intent intent = new Intent(context, Main.class);
		intent.putExtra("NOTICE", true);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK); 
		
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		//璁剧疆鏈�柊淇℃伅
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		//璁剧疆鐐瑰嚮娓呴櫎閫氱煡
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		
		if(noticeCount > _lastNoticeCount) 
		{
			//璁剧疆閫氱煡鏂瑰紡
			notification.defaults |= Notification.DEFAULT_LIGHTS;
			
			//璁剧疆閫氱煡闊�
			notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notificationsound);
			
			//璁剧疆鎸姩 <闇�鍔犱笂鐢ㄦ埛鏉冮檺android.permission.VIBRATE>
			//notification.vibrate = new long[]{100, 250, 100, 500};
		}
		
		//鍙戝嚭閫氱煡
		notificationManager.notify(NOTIFICATION_ID, notification);		
	}
	
}
