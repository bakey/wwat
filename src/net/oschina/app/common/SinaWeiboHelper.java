package net.oschina.app.common;

import com.weibo.net.AccessToken;
import com.weibo.net.DialogError;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboDialogListener;
import com.weibo.net.WeiboException;

import net.oschina.app.AppConfig;
import com.hkzhe.wwtt.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
/**
 * 鏂版氮寰崥甯姪绫�
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-22
 */
public class SinaWeiboHelper {

	private static final String CONSUMER_KEY = "3616966952";
	private static final String CONSUMER_SECRET = "fd81f6d31427b467f49226e48a741e28";
	private static final String REDIRECT_URL = "http://www.oschina.net";
	
	public static final int OAUTH_ERROR = 0;
	public static final int OAUTH_RequestToken_ACCESS = 1;
	public static final int OAUTH_RequestToken_ERROR = 2;
	public static final int OAUTH_AccessToken_ACCESS = 3;
	public static final int OAUTH_AccessToken_ERROR = 4;
	public static final int OAUTH_AccessToken_SXPIRED = 5;
	public static final int Weibo_Message_CHECKED = 6;
	public static final int Weibo_Message_NULL = 7;
	public static final int Weibo_Message_LONG = 8;
	public static final int Weibo_Share_Success = 9;
	public static final int Weibo_Share_Error = 10;
	public static final int Weibo_Share_Repeat = 11;
	
	private static Weibo 			weibo = null;
	private static AccessToken		accessToken = null;
	private static String 			shareImage = null;
	private static String			shareMessage = null;
	private static Activity			context = null;
	public static ProgressDialog	progressDialog = null;
	
	
	public static void setAccessToken(String accessKey,String accessSecret,long expiresIn){
		accessToken = new AccessToken(accessKey, accessSecret);
		accessToken.setExpiresIn(expiresIn);
	}
	
	public static Handler  handler	= new Handler(){ 
		public void handleMessage(Message msg) { 
			if(progressDialog != null)
				progressDialog.dismiss();
		    switch (msg.what) 
		    { 
		    	case OAUTH_ERROR: 
		    		Toast.makeText(context, context.getString(R.string.OAUTH_ERROR), Toast.LENGTH_SHORT).show();
		    		break; 
		    	case OAUTH_RequestToken_ACCESS:
		    		Toast.makeText(context, context.getString(R.string.OAUTH_RequestToken_ACCESS), Toast.LENGTH_SHORT).show();
		    		break;
		    	case OAUTH_RequestToken_ERROR: 
		    		Toast.makeText(context, context.getString(R.string.OAUTH_RequestToken_ERROR), Toast.LENGTH_SHORT).show();
		    		break; 
		    	case OAUTH_AccessToken_ACCESS:
		    		Toast.makeText(context, context.getString(R.string.OAUTH_AccessToken_ACCESS), Toast.LENGTH_SHORT).show();
		    		break;
		    	case OAUTH_AccessToken_ERROR: 
		    		Toast.makeText(context, context.getString(R.string.OAUTH_AccessToken_ERROR), Toast.LENGTH_SHORT).show();
		    		authorize(context, shareMessage, shareImage);//璺宠浆鍒版巿鏉冮〉闈�
		    		break;
		    	case OAUTH_AccessToken_SXPIRED: 
		    		Toast.makeText(context, context.getString(R.string.OAUTH_AccessToken_SXPIRED), Toast.LENGTH_SHORT).show();
		    		authorize(context, shareMessage, shareImage);//璺宠浆鍒版巿鏉冮〉闈�
		    		break;
		    	case Weibo_Message_NULL:
		    		Toast.makeText(context, context.getString(R.string.Weibo_Message_NULL), Toast.LENGTH_SHORT).show();
		    		break;
		    	case Weibo_Message_LONG: 
					Toast.makeText(context, context.getString(R.string.Weibo_Message_LONG), Toast.LENGTH_SHORT ).show();
		    		break;
		    	case Weibo_Share_Success:
		    		Toast.makeText(context, context.getString(R.string.Weibo_Share_Success), Toast.LENGTH_SHORT).show();
		    		break;
		    	case Weibo_Share_Error:
					Toast.makeText(context, context.getString(R.string.Weibo_Share_Error), Toast.LENGTH_SHORT).show();
					break;
		    	case Weibo_Share_Repeat:
		    		Toast.makeText(context, context.getString(R.string.Weibo_Share_Repeat), Toast.LENGTH_SHORT).show();
		    		break;
		    }
		};
	};
	/**
	 * 鍒ゆ柇weibo鏄惁涓簄ull
	 * @return
	 */
	public static boolean isWeiboNull()
	{
		if(weibo == null)
			return true;
		else 
			return false;
	}
	/**
	 * 鍒濆鍖杦eibo
	 */
	public static void initWeibo()
	{
    	weibo = Weibo.getInstance();
    	weibo.setupConsumerConfig(CONSUMER_KEY, CONSUMER_SECRET);
		weibo.setRedirectUrl(REDIRECT_URL);
	}
	/**
	 * 寰崥鎺堟潈 骞�鍒嗕韩(鏂囨湰)
	 */
	public static void authorize(Activity cont, String shareMsg)
	{
		authorize(cont, shareMsg, null);
	}
	/**
	 * 寰崥鎺堟潈 骞�鍒嗕韩(鏂囨湰銆佸浘鐗�
	 */
	public static void authorize(final Activity cont,final String shareMsg,final String shareImg)
	{		
		context = cont;
		
		if(isWeiboNull())
		{
			initWeibo();
		}
		weibo.authorize(cont, new WeiboDialogListener() {
	    	@Override
			public void onComplete(Bundle values) {
	    		try 
	    		{
					String token = values.getString(Weibo.TOKEN);
					String expires_in = values.getString(Weibo.EXPIRES);
					accessToken = new AccessToken(token, CONSUMER_SECRET);
					accessToken.setExpiresIn(expires_in);	
					//淇濆瓨AccessToken
					AppConfig.getAppConfig(cont).setAccessInfo(accessToken.getToken(), accessToken.getSecret(), accessToken.getExpiresIn());
					//寰崥鍒嗕韩
					shareMessage(cont, shareMsg, null);
	    		} 
	    		catch (Exception e) 
	    		{
	    			e.printStackTrace();
	    		}
			}
			@Override
			public void onError(DialogError e) {
				Toast.makeText(context,"鎺堟潈澶辫触 : " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
			@Override
			public void onCancel() {
				//Toast.makeText(context, "鍙栨秷鎺堟潈", Toast.LENGTH_LONG).show();
			}
			@Override
			public void onWeiboException(WeiboException e) {
				Toast.makeText(context,"鎺堟潈寮傚父 : " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
		});		
	}
	/**
	 * 鏁版嵁鍚堟硶鎬у垽鏂�
	 * @return
	 */
	public static int messageChecked(String shareMsg)
	{
		int ret = Weibo_Message_CHECKED;
		if( StringUtils.isEmpty(shareMsg) )
		{
			ret = Weibo_Message_NULL;
		}
		else if( shareMsg.length() > 140 )
		{
			ret = Weibo_Message_LONG;
		}
		return ret;
	}
	/**
	 * 寰崥鍒嗕韩
	 * 鍒嗕韩鍐呭shareMessage & 鍒嗕韩鍥剧墖shareImage & 褰撳墠Activity
     * Toast浼氭彁绀哄垎浜垚鍔熸垨澶辫触
	 */
    public static void shareMessage(Activity cont, String shareMsg, String shareImg)
    {	
    	context = cont;    
    	shareMessage = shareMsg;
    	shareImage = shareImg;
    	
    	if(isWeiboNull())
		{
			initWeibo();
		}
		
    	Message msg = new Message();
    	msg.what = Weibo_Share_Error;	
    	
    	//鍒ゆ柇鏄惁鎺堟潈
    	if(accessToken == null)
    	{
    		msg.what = OAUTH_AccessToken_ERROR;
    		handler.sendMessage(msg);
    		return;
    	}
    	
    	//鍒ゆ柇token鏄惁杩囨湡
    	if(accessToken.getExpiresIn() < System.currentTimeMillis())
    	{
    		msg.what = OAUTH_AccessToken_SXPIRED;
    		handler.sendMessage(msg);
    		return;
    	}
    	
    	//妫�煡鏂囨湰鏄惁瓒呭嚭闄愬埗
    	int checkCode = messageChecked(shareMsg);
    	if(checkCode != Weibo_Message_CHECKED)
    	{
    		msg.what = checkCode;
    		handler.sendMessage(msg);
    		return;
    	}    	
    	
        try 
        {   
        	if( StringUtils.isEmpty(shareImage) )
        	{
        		weibo.shareToweibo(cont, accessToken.getToken(), accessToken.getSecret(), shareMessage);
        	}
        	else
        	{
        		//鍒嗕韩甯﹀浘鐗囩殑寰崥
        	}
			
        	msg.what = Weibo_Share_Success;

		} 
        catch(WeiboException e)
        {
        	int statusCode = e.getStatusCode();
        	if(statusCode == 20019 || e.getMessage().contains("repeat"))
        		msg.what = Weibo_Share_Repeat;
        	e.printStackTrace();
        }
        catch (Exception e) 
        {
        	e.printStackTrace();
		}
        
        handler.sendMessage(msg);
	}
    /**
     * 寰崥鍒嗕韩   
     * 鍒嗕韩鍐呭shareMessage & 褰撳墠Activity
     * Toast浼氭彁绀哄垎浜垚鍔熸垨澶辫触
     */
    public static void shareMessage(Activity cont, String shareMsg)
    {
    	shareMessage(cont, shareMsg, null);
    }
}
