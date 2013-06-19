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
 * 闁氨鐓℃穱鈩冧紖楠炴寧鎸遍幒銉︽暪閸ｏ拷
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
			int atmeCount = intent.getIntExtra("atmeCount", 0);//@閹达拷
			int msgCount = intent.getIntExtra("msgCount", 0);//閻ｆ瑨鈻�
			int reviewCount = intent.getIntExtra("reviewCount", 0);//鐠囧嫯顔�
			int newFansCount = intent.getIntExtra("newFansCount", 0);//閺傛壆鐭囨稉锟�	
			int activeCount = atmeCount + reviewCount + msgCount + newFansCount;//娣団剝浼呴幀缁樻殶
			
			//閸斻劍锟�閹粯鏆�
			if(Main.bv_active != null){
				if(activeCount > 0){
					Main.bv_active.setText(activeCount+"");
					Main.bv_active.show();
				}else{
					Main.bv_active.setText("");
					Main.bv_active.hide();
				}
			}
			//@閹达拷
			if(Main.bv_atme != null){
				if(atmeCount > 0){
					Main.bv_atme.setText(atmeCount+"");
					Main.bv_atme.show();
				}else{
					Main.bv_atme.setText("");
					Main.bv_atme.hide();
				}
			}
			//鐠囧嫯顔�
			if(Main.bv_review != null){
				if(reviewCount > 0){
					Main.bv_review.setText(reviewCount+"");
					Main.bv_review.show();
				}else{
					Main.bv_review.setText("");
					Main.bv_review.hide();
				}
			}
			//閻ｆ瑨鈻�
			if(Main.bv_message != null){
				if(msgCount > 0){
					Main.bv_message.setText(msgCount+"");
					Main.bv_message.show();
				}else{
					Main.bv_message.setText("");
					Main.bv_message.hide();
				}
			}
			
			//闁氨鐓￠弽蹇旀▔缁�拷
			this.notification(context, activeCount);
		}
	}

	private void notification(Context context, int noticeCount){		
		//閸掓稑缂�NotificationManager
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		String contentTitle = "瀵拷绨稉顓炴禇";
		String contentText = "閹劍婀�" + noticeCount + " 閺夆剝娓堕弬棰佷繆閹拷";
		int _lastNoticeCount;
		
		//閸掋倖鏌囬弰顖氭儊閸欐垵鍤柅姘辩叀娣団剝浼�
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
		
		//閸掓稑缂撻柅姘辩叀 Notification
		Notification notification = null;
		
		if(noticeCount > _lastNoticeCount) 
		{
			String noticeTitle = "閹劍婀�" + (noticeCount-_lastNoticeCount) + " 閺夆剝娓堕弬棰佷繆閹拷";
			notification = new Notification(R.drawable.icon, noticeTitle, System.currentTimeMillis());
		}
		else
		{
			notification = new Notification();
		}
		
		//鐠佸墽鐤嗛悙鐟板毊闁氨鐓＄捄瀹犳祮
		Intent intent = new Intent(context, Main.class);
		intent.putExtra("NOTICE", true);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK); 
		
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		//鐠佸墽鐤嗛張锟芥煀娣団剝浼�
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		//鐠佸墽鐤嗛悙鐟板毊濞撳懘娅庨柅姘辩叀
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		
		if(noticeCount > _lastNoticeCount) 
		{
			//鐠佸墽鐤嗛柅姘辩叀閺傜懓绱�
			notification.defaults |= Notification.DEFAULT_LIGHTS;
			
			//鐠佸墽鐤嗛柅姘辩叀闂婏拷
			notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notificationsound);
			
			//鐠佸墽鐤嗛幐顖氬З <闂囷拷顪呴崝鐘辩瑐閻劍鍩涢弶鍐android.permission.VIBRATE>
			//notification.vibrate = new long[]{100, 250, 100, 500};
		}
		
		//閸欐垵鍤柅姘辩叀
		notificationManager.notify(NOTIFICATION_ID, notification);		
	}
	
}
