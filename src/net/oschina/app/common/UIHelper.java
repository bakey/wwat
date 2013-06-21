package net.oschina.app.common;

import java.io.File;
import java.io.IOException;

import greendroid.widget.MyQuickAction;
import greendroid.widget.QuickAction;
import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import net.oschina.app.AppManager;
import com.hkzhe.wwtt.R;
import net.oschina.app.api.ApiClient;
import net.oschina.app.bean.AccessInfo;
import net.oschina.app.bean.Active;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.CommentList;
import net.oschina.app.bean.Messages;
import net.oschina.app.bean.News;
import net.oschina.app.bean.Notice;
import net.oschina.app.bean.Result;
import net.oschina.app.bean.Tweet;
import net.oschina.app.bean.URLs;
import net.oschina.app.ui.About;
import net.oschina.app.ui.BlogDetail;
import net.oschina.app.ui.CommentPub;
import net.oschina.app.ui.FeedBack;
import net.oschina.app.ui.ImageDialog;
import net.oschina.app.ui.ImageZoomDialog;
import net.oschina.app.ui.LoginDialog;
import net.oschina.app.ui.Main;
import net.oschina.app.ui.MessageDetail;
import net.oschina.app.ui.MessageForward;
import net.oschina.app.ui.MessagePub;
import net.oschina.app.ui.NewsDetail;
import net.oschina.app.ui.QuestionDetail;
import net.oschina.app.ui.QuestionPub;
import net.oschina.app.ui.Search;
import net.oschina.app.ui.Setting;
import net.oschina.app.ui.SoftwareLib;
import net.oschina.app.ui.SoftwareDetail;
import net.oschina.app.ui.TweetDetail;
import net.oschina.app.ui.TweetPub;
import net.oschina.app.ui.UserCenter;
import net.oschina.app.ui.UserFavorite;
import net.oschina.app.ui.UserFriend;
import net.oschina.app.ui.UserInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 鎼存梻鏁ょ粙瀣碍UI瀹搞儱鍙块崠鍜冪窗鐏忎浇顥朥I閻╃鍙ч惃鍕娴滄稒鎼锋担锟� * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class UIHelper {

	public final static int LISTVIEW_ACTION_INIT = 0x01;
	public final static int LISTVIEW_ACTION_REFRESH = 0x02;
	public final static int LISTVIEW_ACTION_SCROLL = 0x03;
	public final static int LISTVIEW_ACTION_CHANGE_CATALOG = 0x04;
	
	public final static int LISTVIEW_DATA_MORE = 0x01;
	public final static int LISTVIEW_DATA_LOADING = 0x02;
	public final static int LISTVIEW_DATA_FULL = 0x03;
	public final static int LISTVIEW_DATA_EMPTY = 0x04;
	
	public final static int LISTVIEW_DATATYPE_NEWS = 0x01;
	public final static int LISTVIEW_DATATYPE_BLOG = 0x02;
	public final static int LISTVIEW_DATATYPE_POST = 0x03;
	public final static int LISTVIEW_DATATYPE_TWEET = 0x04;
	public final static int LISTVIEW_DATATYPE_ACTIVE = 0x05;
	public final static int LISTVIEW_DATATYPE_MESSAGE = 0x06;
	public final static int LISTVIEW_DATATYPE_COMMENT = 0x07;
	
	public final static int REQUEST_CODE_FOR_RESULT = 0x01;
	public final static int REQUEST_CODE_FOR_REPLY = 0x02;
	
	/** 閸忋劌鐪瑆eb閺嶅嘲绱�*/
	public final static String WEB_STYLE = "<style>* {font-size:16px;line-height:20px;} p {color:#333;} a {color:#3E62A6;} img {max-width:310px;} " +
			"img.alignleft {float:left;max-width:120px;margin:0 10px 5px 0;border:1px solid #ccc;background:#fff;padding:2px;} " +
			"pre {font-size:9pt;line-height:12pt;font-family:Courier New,Arial;border:1px solid #ddd;border-left:5px solid #6CE26C;background:#f6f6f6;padding:5px;}</style>";
	/**
	 * 閺勫墽銇氭＃鏍�
	 * @param activity
	 */
	public static void showHome(Activity activity)
	{
		Intent intent = new Intent(activity,Main.class);
		activity.startActivity(intent);
		activity.finish();
	}
	
	/**
	 * 閺勫墽銇氶惂璇茬秿妞ょ敻娼�
	 * @param activity
	 */
	public static void showLoginDialog(Context context)
	{
		Intent intent = new Intent(context,LoginDialog.class);
		if(context instanceof Main)
			intent.putExtra("LOGINTYPE", LoginDialog.LOGIN_MAIN);
		else if(context instanceof Setting)
			intent.putExtra("LOGINTYPE", LoginDialog.LOGIN_SETTING);
		else
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	/**
	 * 显示新闻详情
	 * @param context
	 * @param newsId
	 */
	public static void showNewsDetail(Context context, int newsId)
	{
		Intent intent = new Intent(context, NewsDetail.class);
		intent.putExtra("news_id", newsId);
		context.startActivity(intent);
	}
	
	/**
	 * 显示帖子详情
	 * @param context
	 * @param postId
	 */
	public static void showQuestionDetail(Context context, int postId)
	{
		Intent intent = new Intent(context, QuestionDetail.class);
		intent.putExtra("post_id", postId);
		context.startActivity(intent);
	}
	
	/**
	 * 显示我要提问页面
	 * @param context
	 */
	public static void showQuestionPub(Context context)
	{
		Intent intent = new Intent(context, QuestionPub.class);
		context.startActivity(intent);
	}
	
	/**
	 * 显示动弹详情及评论
	 * @param context
	 * @param tweetId
	 */
	public static void showTweetDetail(Context context, int tweetId)
	{
		Intent intent = new Intent(context, TweetDetail.class);
		intent.putExtra("tweet_id", tweetId);
		context.startActivity(intent);
	}
	
	/**
	 * 显示动弹一下页面
	 * @param context
	 */
	public static void showTweetPub(Activity context)
	{
		Intent intent = new Intent(context, TweetPub.class);
		context.startActivityForResult(intent, REQUEST_CODE_FOR_RESULT);
	}
	public static void showTweetPub(Activity context,String atme,int atuid)
	{
		Intent intent = new Intent(context, TweetPub.class);
		intent.putExtra("at_me", atme);
		intent.putExtra("at_uid", atuid);
		context.startActivityForResult(intent, REQUEST_CODE_FOR_RESULT);
	}
	
	/**
	 * 显示博客详情
	 * @param context
	 * @param blogId
	 */
	public static void showBlogDetail(Context context, int blogId)
	{
		Intent intent = new Intent(context, BlogDetail.class);
		intent.putExtra("blog_id", blogId);
		context.startActivity(intent);
	}
	
	/**
	 * 显示软件详情
	 * @param context
	 * @param ident
	 */
	public static void showSoftwareDetail(Context context, String ident)
	{
		Intent intent = new Intent(context, SoftwareDetail.class);
		intent.putExtra("ident", ident);
		context.startActivity(intent);
	}
	
	/**
	 * 新闻超链接点击跳转
	 * @param context
	 * @param newsId
	 * @param newsType
	 * @param objId
	 */
	public static void showNewsRedirect(Context context, News news)
	{
		String url = news.getUrl();
		//url为空-旧方法
		if(StringUtils.isEmpty(url)) {
			int newsId = news.getId();
			int newsType = news.getNewType().type;
			String objId = news.getNewType().attachment;
			switch (newsType) {
				case News.NEWSTYPE_NEWS:
					showNewsDetail(context, newsId);
					break;
				case News.NEWSTYPE_SOFTWARE:
					showSoftwareDetail(context, objId);
					break;
				case News.NEWSTYPE_POST:
					showQuestionDetail(context, StringUtils.toInt(objId));
					break;
				case News.NEWSTYPE_BLOG:
					showBlogDetail(context, StringUtils.toInt(objId));
					break;
			}
		} else {
			showUrlRedirect(context, url);
		}
	}
	
	/**
	 * 閸斻劍锟介悙鐟板毊鐠哄疇娴嗛崚鎵祲閸忚櫕鏌婇梻姹囷拷鐢牕鐡欑粵锟�	 * @param context
	 * @param id
	 * @param catalog 0閸忔湹绮� 1閺備即妞� 2鐢牕鐡� 3閸斻劌鑴� 4閸楁艾顓� 
	 */
	public static void showActiveRedirect(Context context, Active active)
	{
		String url = active.getUrl();
		//url娑撹櫣鈹�閺冄勬煙濞夛拷
		if(StringUtils.isEmpty(url)) {
			int id = active.getObjectId();
			int catalog = active.getActiveType();
			switch (catalog) {
				case Active.CATALOG_OTHER:
					//閸忔湹绮�閺冪姾鐑︽潪锟�					break;
				case Active.CATALOG_NEWS:
					showNewsDetail(context, id);
					break;
				case Active.CATALOG_POST:
					showQuestionDetail(context, id);
					break;
				case Active.CATALOG_TWEET:
					showTweetDetail(context, id);
					break;
				case Active.CATALOG_BLOG:
					showBlogDetail(context, id);
					break;
			}
		} else {
			showUrlRedirect(context, url);
		}
	}

	/**
	 * 閺勫墽銇氱拠鍕啈閸欐垼銆冩い鐢告桨
	 * @param context
	 * @param id 閺備即妞坾鐢牕鐡檤閸斻劌鑴婇惃鍒琩
	 * @param catalog 1閺備即妞�2鐢牕鐡�3閸斻劌鑴�4閸斻劍锟�
	 */
	public static void showCommentPub(Activity context, int id, int catalog)
	{
		Intent intent = new Intent(context, CommentPub.class);
		intent.putExtra("id", id);
		intent.putExtra("catalog", catalog);
		context.startActivityForResult(intent, REQUEST_CODE_FOR_RESULT);
	}
	
	/**
	 * 閺勫墽銇氱拠鍕啈閸ョ偛顦炬い鐢告桨
	 * @param context
	 * @param id
	 * @param catalog
	 * @param replyid
	 * @param authorid
	 */
	public static void showCommentReply(Activity context, int id, int catalog, int replyid, int authorid, String author, String content)
	{
		Intent intent = new Intent(context, CommentPub.class);
		intent.putExtra("id", id);
		intent.putExtra("catalog", catalog);
		intent.putExtra("reply_id", replyid);
		intent.putExtra("author_id", authorid);
		intent.putExtra("author", author);
		intent.putExtra("content", content);
		if(catalog == CommentList.CATALOG_POST)
			context.startActivityForResult(intent, REQUEST_CODE_FOR_REPLY);
		else
			context.startActivityForResult(intent, REQUEST_CODE_FOR_RESULT);
	}
	
	/**
	 * 閺勫墽銇氶悾娆掆枅鐎电鐦芥い鐢告桨
	 * @param context
	 * @param catalog
	 * @param friendid
	 */
	public static void showMessageDetail(Context context, int friendid, String friendname)
	{
		Intent intent = new Intent(context, MessageDetail.class);
		intent.putExtra("friend_name", friendname);
		intent.putExtra("friend_id", friendid);
		context.startActivity(intent);
	}
	
	/**
	 * 閺勫墽銇氶悾娆掆枅閸ョ偛顦鹃悾宀勬桨
	 * @param context
	 * @param friendId 鐎佃鏌焛d
	 * @param friendName 鐎佃鏌熼崥宥囆�
	 */
	public static void showMessagePub(Activity context, int friendId, String friendName)
	{
    	Intent intent = new Intent();
		intent.putExtra("user_id", ((AppContext)context.getApplication()).getLoginUid());
		intent.putExtra("friend_id", friendId);
		intent.putExtra("friend_name", friendName);
		intent.setClass(context, MessagePub.class);
		context.startActivityForResult(intent, REQUEST_CODE_FOR_RESULT);
	}
	
	/**
	 * 閺勫墽銇氭潪顒�絺閻ｆ瑨鈻堥悾宀勬桨
	 * @param context
	 * @param friendName 鐎佃鏌熼崥宥囆�
	 * @param messageContent 閻ｆ瑨鈻堥崘鍛啇
	 */
	public static void showMessageForward(Activity context, String friendName, String messageContent)
	{
    	Intent intent = new Intent();
		intent.putExtra("user_id", ((AppContext)context.getApplication()).getLoginUid());
		intent.putExtra("friend_name", friendName);
		intent.putExtra("message_content", messageContent);
		intent.setClass(context, MessageForward.class);
		context.startActivity(intent);
	}
	
	/**
	 * 閸掑棔闊╅崚锟介弬鐗堟爱瀵邦喖宕�閹达拷閼垫崘顔嗗顔煎触'閻ㄥ嫬顕拠婵囶攱
	 * @param context 瑜版挸澧燗ctivity
	 * @param title	閸掑棔闊╅惃鍕垼妫帮拷
	 * @param url 閸掑棔闊╅惃鍕懠閹猴拷
	 */
	public static void showShareDialog(final Activity context,final String title,final String url)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.btn_star);
		builder.setTitle(context.getString(R.string.share));
		builder.setItems(R.array.app_share_items,new DialogInterface.OnClickListener(){
			AppConfig cfgHelper = AppConfig.getAppConfig(context);
			AccessInfo access = cfgHelper.getAccessInfo();
			public void onClick(DialogInterface arg0, int arg1) {
				switch (arg1) {
					case 0:
						//閸掑棔闊╅惃鍕敶鐎癸拷
						final String shareMessage = title + " " +url;
						//閸掓繂顬婇崠鏍т簳閸楋拷
						if(SinaWeiboHelper.isWeiboNull())
			    		{
			    			SinaWeiboHelper.initWeibo();
			    		}
						//閸掋倖鏌囨稊瀣閺勵垰鎯侀惂濠氭鏉╋拷
				        if(access != null)
				        {   
				        	SinaWeiboHelper.progressDialog = new ProgressDialog(context); 
				        	SinaWeiboHelper.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
				        	SinaWeiboHelper.progressDialog.setMessage(context.getString(R.string.sharing));
				        	SinaWeiboHelper.progressDialog.setCancelable(true);
				        	SinaWeiboHelper.progressDialog.show();
				        	new Thread()
				        	{
								public void run() 
								{						        	
									SinaWeiboHelper.setAccessToken(access.getAccessToken(), access.getAccessSecret(), access.getExpiresIn());
									SinaWeiboHelper.shareMessage(context, shareMessage);
				        		}
				        	}.start();
				        }
				        else
				        {
				        	SinaWeiboHelper.authorize(context, shareMessage);
				        }
						break;
					case 1:
						QQWeiboHelper.shareToQQ(context, title, url);
						break;
				}				
			}
		});
		builder.create().show();
	}
	
	/**
	 * 閺�儼妫岄幙宥勭稊闁瀚ㄥ锟�	 * @param context
	 * @param thread
	 */
	public static void showFavoriteOptionDialog(final Activity context,final Thread thread)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.ic_dialog_menu);
		builder.setTitle(context.getString(R.string.select));
		builder.setItems(R.array.favorite_options,new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface arg0, int arg1) {
				switch (arg1) {
					case 0://閸掔娀娅�
						thread.start();
						break;
				}				
			}
		});
		builder.create().show();
	}
	
	/**
	 * 濞戝牊浼呴崚妤勩�閹垮秳缍旈柅澶嬪濡楋拷
	 * @param context
	 * @param msg
	 * @param thread
	 */
	public static void showMessageListOptionDialog(final Activity context,final Messages msg,final Thread thread)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.ic_dialog_menu);
		builder.setTitle(context.getString(R.string.select));
		builder.setItems(R.array.message_list_options,new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface arg0, int arg1) {
				switch (arg1) {
					case 0://閸ョ偛顦�
						showMessagePub(context,msg.getFriendId(),msg.getFriendName());
						break;
					case 1://鏉烆剙褰�
						showMessageForward(context,msg.getFriendName(),msg.getContent());
						break;
					case 2://閸掔娀娅�
						thread.start();
						break;
				}				
			}
		});
		builder.create().show();
	}
	
	/**
	 * 濞戝牊浼呯拠锔藉剰閹垮秳缍旈柅澶嬪濡楋拷
	 * @param context
	 * @param msg
	 * @param thread
	 */
	public static void showMessageDetailOptionDialog(final Activity context,final Comment msg,final Thread thread)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.ic_dialog_menu);
		builder.setTitle(context.getString(R.string.select));
		builder.setItems(R.array.message_detail_options,new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface arg0, int arg1) {
				switch (arg1) {
					case 0://鏉烆剙褰�
						showMessageForward(context,msg.getAuthor(),msg.getContent());
						break;
					case 1://閸掔娀娅�
						thread.start();
						break;
				}				
			}
		});
		builder.create().show();
	}
	
	/**
	 * 鐠囧嫯顔戦幙宥勭稊闁瀚ㄥ锟�	 * @param context
	 * @param id 閺屾劖娼弬浼存閿涘苯绗樼�鎰剁礉閸斻劌鑴婇惃鍒琩 閹存牞锟介弻鎰蒋濞戝牊浼呴惃锟絝riendid
	 * @param catalog 鐠囥儴鐦庣拋鐑樺鐏炵偟琚崹瀣剁窗1閺備即妞� 2鐢牕鐡� 3閸斻劌鑴� 4閸斻劍锟�
	 * @param comment 閺堫剚娼拠鍕啈鐎电钖勯敍宀�暏娴滃氦骞忛崣鏍槑鐠佺d&鐠囧嫯顔戦懓鍗唘thorid
	 * @param thread 婢跺嫮鎮婇崚鐘绘珟鐠囧嫯顔戦惃鍕殠缁嬪绱濋懟銉︽￥閸掔娀娅庨幙宥勭稊娴肩垔ull
	 */
	public static void showCommentOptionDialog(final Activity context,final int id,final int catalog,final Comment comment,final Thread thread)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.ic_dialog_menu);
		builder.setTitle(context.getString(R.string.select));
		if(thread != null)
		{
			builder.setItems(R.array.comment_options_2,new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface arg0, int arg1) {
					switch (arg1) {
						case 0://閸ョ偛顦�
							showCommentReply(context,id,catalog,comment.getId(),comment.getAuthorId(),comment.getAuthor(),comment.getContent());
							break;
						case 1://閸掔娀娅�
							thread.start();
							break;
					}				
				}
			});
		}
		else
		{
			builder.setItems(R.array.comment_options_1,new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface arg0, int arg1) {
					switch (arg1) {
						case 0://閸ョ偛顦�
							showCommentReply(context,id,catalog,comment.getId(),comment.getAuthorId(),comment.getAuthor(),comment.getContent());
							break;
					}				
				}
			});
		}
		builder.create().show();
	}
	
	/**
	 * 閸楁艾顓归崚妤勩�閹垮秳缍�
	 * @param context
	 * @param thread
	 */
	public static void showBlogOptionDialog(final Context context,final Thread thread)
	{
		new AlertDialog.Builder(context)
		.setIcon(android.R.drawable.ic_dialog_info)
		.setTitle(context.getString(R.string.delete_blog))
		.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if(thread != null)
					thread.start();
				else
					ToastMessage(context, R.string.msg_noaccess_delete);
				dialog.dismiss();
			}
		})
		.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		})
		.create().show();
	}
	
	/**
	 * 閸斻劌鑴婇幙宥勭稊闁瀚ㄥ锟�	 * @param context
	 * @param thread
	 */
	public static void showTweetOptionDialog(final Context context,final Thread thread)
	{
		new AlertDialog.Builder(context)
		.setIcon(android.R.drawable.ic_dialog_info)
		.setTitle(context.getString(R.string.delete_tweet))
		.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if(thread != null)
					thread.start();
				else
					ToastMessage(context, R.string.msg_noaccess_delete);
				dialog.dismiss();
			}
		})
		.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		})
		.create().show();
	}
	
	/**
	 * 閺勵垰鎯侀柌宥嗘煀閸欐垵绔烽崝銊ヨ剨閹垮秴顕拠婵囶攱
	 * @param context
	 * @param thread
	 */
	public static void showResendTweetDialog(final Context context,final Thread thread)
	{
		new AlertDialog.Builder(context)
		.setIcon(android.R.drawable.ic_dialog_info)
		.setTitle(context.getString(R.string.republish_tweet))
		.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				thread.start();
				dialog.dismiss();
				if(context == TweetPub.mContext && TweetPub.mMessage != null)
					TweetPub.mMessage.setVisibility(View.VISIBLE);
			}
		})
		.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		})
		.create().show();
	}
	
	/**
	 * 閺勫墽銇氶崶鍓у鐎电鐦藉锟�	 * @param context
	 * @param imgUrl
	 */
	public static void showImageDialog(Context context, String imgUrl)
	{
		Intent intent = new Intent(context, ImageDialog.class);
		intent.putExtra("img_url", imgUrl);
		context.startActivity(intent);
	}
	public static void showImageZoomDialog(Context context, String imgUrl)
	{
		Intent intent = new Intent(context, ImageZoomDialog.class);
		intent.putExtra("img_url", imgUrl);
		context.startActivity(intent);
	}
	
	/**
	 * 閺勫墽銇氱化鑽ょ埠鐠佸墽鐤嗛悾宀勬桨
	 * @param context
	 */
	public static void showSetting(Context context)
	{
		Intent intent = new Intent(context, Setting.class);
		context.startActivity(intent);
	}	
	
	/**
	 * 閺勫墽銇氶幖婊呭偍閻ｅ矂娼�
	 * @param context
	 */
	public static void showSearch(Context context)
	{
		Intent intent = new Intent(context, Search.class);
		context.startActivity(intent);
	}
	
	/**
	 * 閺勫墽銇氭潪顖欐閻ｅ矂娼�
	 * @param context
	 */
	public static void showSoftware(Context context)
	{
		Intent intent = new Intent(context, SoftwareLib.class);
		context.startActivity(intent);
	}
	
	/**
	 * 閺勫墽銇氶幋鎴犳畱鐠у嫭鏋�
	 * @param context
	 */
	public static void showUserInfo(Activity context)
	{
		AppContext ac = (AppContext)context.getApplicationContext();
		if(!ac.isLogin()){
			showLoginDialog(context);
		}else{
			Intent intent = new Intent(context, UserInfo.class);
			context.startActivity(intent);
		}
	}
	
	/**
	 * 閺勫墽銇氶悽銊﹀煕閸斻劍锟�
	 * @param context
	 * @param uid
	 * @param hisuid
	 * @param hisname
	 */
	public static void showUserCenter(Context context, int hisuid, String hisname)
	{
    	Intent intent = new Intent(context, UserCenter.class);
		intent.putExtra("his_id", hisuid);
		intent.putExtra("his_name", hisname);
		context.startActivity(intent);
	}
	
	/**
	 * 閺勫墽銇氶悽銊﹀煕閺�儼妫屾径锟�	 * @param context
	 */
	public static void showUserFavorite(Context context)
	{
		Intent intent = new Intent(context, UserFavorite.class);
		context.startActivity(intent);
	}
	
	/**
	 * 閺勫墽銇氶悽銊﹀煕婵傝棄寮�
	 * @param context
	 */
	public static void showUserFriend(Context context, int friendType, int followers, int fans)
	{
		Intent intent = new Intent(context, UserFriend.class);
		intent.putExtra("friend_type", friendType);
		intent.putExtra("friend_followers", followers);
		intent.putExtra("friend_fans", fans);
		context.startActivity(intent);
	}
	
	/**
	 * 閸旂姾娴囬弰鍓с仛閻劍鍩涙径鏉戝剼
	 * @param imgFace
	 * @param faceURL
	 */
	public static void showUserFace(final ImageView imgFace,final String faceURL)
	{
		showLoadImage(imgFace,faceURL,imgFace.getContext().getString(R.string.msg_load_userface_fail));
	}
	
	/**
	 * 閸旂姾娴囬弰鍓с仛閸ュ墽澧�
	 * @param imgFace
	 * @param faceURL
	 * @param errMsg
	 */
	public static void showLoadImage(final ImageView imgView,final String imgURL,final String errMsg)
	{
		//鐠囪褰囬張顒�勾閸ュ墽澧�
		if(StringUtils.isEmpty(imgURL) || imgURL.endsWith("portrait.gif")){
			Bitmap bmp = BitmapFactory.decodeResource(imgView.getResources(), R.drawable.widget_dface);
			imgView.setImageBitmap(bmp);
			return;
		}
		
		//閺勵垰鎯侀張澶岀处鐎涙ê娴橀悧锟�    
		final String filename = FileUtils.getFileName(imgURL);
    	//Environment.getExternalStorageDirectory();鏉╂柨娲�sdcard
    	String filepath = imgView.getContext().getFilesDir() + File.separator + filename;
		File file = new File(filepath);
		if(file.exists()){
			Bitmap bmp = ImageUtils.getBitmap(imgView.getContext(), filename);
			imgView.setImageBitmap(bmp);
			return;
    	}
		
		//娴犲海缍夌紒婊嗗箯閸欙拷閸愭瑥鍙嗛崶鍓у缂傛挸鐡�
		String _errMsg = imgView.getContext().getString(R.string.msg_load_image_fail);
		if(!StringUtils.isEmpty(errMsg))
			_errMsg = errMsg;
		final String ErrMsg = _errMsg;
		final Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				if(msg.what==1 && msg.obj != null){
					imgView.setImageBitmap((Bitmap)msg.obj);
					try {
                    	//閸愭瑥娴橀悧鍥╃处鐎涳拷
						ImageUtils.saveImage(imgView.getContext(), filename, (Bitmap)msg.obj);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					ToastMessage(imgView.getContext(), ErrMsg);
				}
			}
		};
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {
					Bitmap bmp = ApiClient.getNetBitmap(imgURL);
					msg.what = 1;
					msg.obj = bmp;
				} catch (AppException e) {
					e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	/**
	 * url鐠哄疇娴�
	 * @param context
	 * @param url
	 */
	public static void showUrlRedirect(Context context, String url){
		URLs urls = URLs.parseURL(url);
		if(urls != null){
			showLinkRedirect(context, urls.getObjType(), urls.getObjId(), urls.getObjKey());
		}else{
			openBrowser(context, url);
		}
	}
	
	public static void showLinkRedirect(Context context, int objType, int objId, String objKey){
		switch (objType) {
			case URLs.URL_OBJ_TYPE_NEWS:
				showNewsDetail(context, objId);
				break;
			case URLs.URL_OBJ_TYPE_QUESTION:
				showQuestionDetail(context, objId);
				break;
			case URLs.URL_OBJ_TYPE_SOFTWARE:
				showSoftwareDetail(context, objKey);
				break;
			case URLs.URL_OBJ_TYPE_ZONE:
				showUserCenter(context, objId, objKey);
				break;
			case URLs.URL_OBJ_TYPE_TWEET:
				showTweetDetail(context, objId);
				break;
			case URLs.URL_OBJ_TYPE_BLOG:
				showBlogDetail(context, objId);
				break;
			case URLs.URL_OBJ_TYPE_OTHER:
				openBrowser(context, objKey);
				break;
		}
	}
	
	/**
	 * 閹垫挸绱戝ù蹇氼瀲閸ｏ拷
	 * @param context
	 * @param url
	 */
	public static void openBrowser(Context context, String url){
		try {
			Uri uri = Uri.parse(url);  
			Intent it = new Intent(Intent.ACTION_VIEW, uri);  
			context.startActivity(it);
		} catch (Exception e) {
			e.printStackTrace();
			ToastMessage(context, "閺冪姵纭跺ù蹇氼瀲濮濄倗缍夋い锟�", 500);
		} 
	}
		
	/**
	 * 閼惧嘲褰噖ebviewClient鐎电钖�
	 * @return
	 */
	public static WebViewClient getWebViewClient(){
		return new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view,String url) {
				showUrlRedirect(view.getContext(), url);
				return true;
			}
		};
	}
	
	/**
	 * 閼惧嘲褰嘥extWatcher鐎电钖�
	 * @param context
	 * @param tmlKey
	 * @return
	 */
	public static TextWatcher getTextWatcher(final Activity context, final String temlKey) {
		return new TextWatcher() {		
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//娣囨繂鐡ㄨぐ鎾冲EditText濮濓絽婀紓鏍帆閻ㄥ嫬鍞寸�锟�				((AppContext)context.getApplication()).setProperty(temlKey, s.toString());
			}		
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}		
			public void afterTextChanged(Editable s) {}
		};
	}
	
	/**
	 * 缂傛牞绶崳銊︽▔缁�桨绻氱�妯兼畱閼藉顭�
	 * @param context
	 * @param editer
	 * @param temlKey
	 */
	public static void showTempEditContent(Activity context, EditText editer, String temlKey) {
		String tempContent = ((AppContext)context.getApplication()).getProperty(temlKey);
		if(!StringUtils.isEmpty(tempContent)) {
			editer.setText(tempContent);
			editer.setSelection(tempContent.length());//鐠佸墽鐤嗛崗澶嬬垼娴ｅ秶鐤�
		}
	}
	
	/**
	 * 濞撳懘娅庨弬鍥х摟
	 * @param cont
	 * @param editer
	 */
	public static void showClearWordsDialog(final Context cont, final EditText editer, final TextView numwords)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setTitle(R.string.clearwords);
		builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//濞撳懘娅庨弬鍥х摟
				editer.setText("");
				numwords.setText("160");
			}
		});
		builder.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}
	
	/**
	 * 閸欐垿锟介柅姘辩叀楠炴寧鎸�
	 * @param context
	 * @param notice
	 */
	public static void sendBroadCast(Context context, Notice notice){
		if(!((AppContext)context.getApplicationContext()).isLogin() || notice==null) return;
		Intent intent = new Intent("net.oschina.app.action.APPWIDGET_UPDATE"); 
		intent.putExtra("atmeCount", notice.getAtmeCount());
		intent.putExtra("msgCount", notice.getMsgCount());
		intent.putExtra("reviewCount", notice.getReviewCount());
		intent.putExtra("newFansCount", notice.getNewFansCount());
		context.sendBroadcast(intent);
	}
	
	/**
	 * 閸欐垿锟介獮鎸庢尡-閸欐垵绔烽崝銊ヨ剨
	 * @param context
	 * @param notice
	 */
	public static void sendBroadCastTweet(Context context, int what, Result res, Tweet tweet){
		if(res==null && tweet==null) return;
		Intent intent = new Intent("net.oschina.app.action.APP_TWEETPUB"); 
		intent.putExtra("MSG_WHAT", what);
		if(what == 1)
			intent.putExtra("RESULT", res);
		else
			intent.putExtra("TWEET", tweet);
		context.sendBroadcast(intent);
	}
	
	/**
	 * 缂佸嫬鎮庨崝銊︼拷閻ㄥ嫬濮╂担婊勬瀮閺堬拷
	 * @param objecttype
	 * @param objectcatalog
	 * @param objecttitle
	 * @return
	 */
	public static SpannableString parseActiveAction(String author,int objecttype,int objectcatalog,String objecttitle){
		String title = "";
		int start = 0;
		int end = 0;
		if(objecttype==32 && objectcatalog==0){
			title = "閸旂姴鍙嗘禍鍡楃磻濠ф劒鑵戦崶锟�";
		}
		else if(objecttype==1 && objectcatalog==0){
			title = "濞ｈ濮炴禍鍡楃磻濠ф劙銆嶉惄锟�"+objecttitle;
		}
		else if(objecttype==2 && objectcatalog==1){
			title = "閸︺劏顓跨拋鍝勫隘閹绘劙妫堕敍锟�"+objecttitle;
		}
		else if(objecttype==2 && objectcatalog==2){
			title = "閸欐垼銆冩禍鍡樻煀鐠囨繈顣介敍锟�"+objecttitle;
		}
		else if(objecttype==3 && objectcatalog==0){
			title = "閸欐垼銆冩禍鍡楀触鐎癸拷"+objecttitle;
		}
		else if(objecttype==4 && objectcatalog==0){
			title = "閸欐垼銆冩稉锟界槖閺備即妞�"+objecttitle;
		}
		else if(objecttype==5 && objectcatalog==0){
			title = "閸掑棔闊╂禍鍡曠濞堝吀鍞惍锟�"+objecttitle;
		}
		else if(objecttype==6 && objectcatalog==0){
			title = "閸欐垵绔锋禍鍡曠娑擃亣浜存担宥忕窗"+objecttitle;
		}
		else if(objecttype==16 && objectcatalog==0){
			title = "閸︺劍鏌婇梻锟�"+objecttitle+" 閸欐垼銆冪拠鍕啈";
		}
		else if(objecttype==17 && objectcatalog==1){
			title = "閸ョ偟鐡熸禍鍡涙６妫版﹫绱�"+objecttitle;
		}
		else if(objecttype==17 && objectcatalog==2){
			title = "閸ョ偛顦炬禍鍡氱樈妫版﹫绱�"+objecttitle;
		}
		else if(objecttype==17 && objectcatalog==3){
			title = "閸︼拷"+objecttitle+" 鐎电懓娲栫敮鏍у絺鐞涖劏鐦庣拋锟�";
		}
		else if(objecttype==18 && objectcatalog==0){
			title = "閸︺劌宕ョ�锟�"+objecttitle+" 閸欐垼銆冪拠鍕啈";
		}
		else if(objecttype==19 && objectcatalog==0){
			title = "閸︺劋鍞惍锟�"+objecttitle+" 閸欐垼銆冪拠鍕啈";
		}
		else if(objecttype==20 && objectcatalog==0){
			title = "閸︺劏浜存担锟�"+objecttitle+" 閸欐垼銆冪拠鍕啈";
		}
		else if(objecttype==101 && objectcatalog==0){
			title = "閸ョ偛顦炬禍鍡楀З閹緤绱�"+objecttitle;
		}
		else if(objecttype==100){
			title = "閺囧瓨鏌婃禍鍡楀З閹拷";
		}
		title = author + " " + title;
		SpannableString sp = new SpannableString(title);
		//鐠佸墽鐤嗛悽銊﹀煕閸氬秴鐡ф担鎾炽亣鐏忓繈锟介崝鐘电煐閵嗕線鐝禍锟�
		sp.setSpan(new AbsoluteSizeSpan(14,true), 0, author.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, author.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(Color.parseColor("#0e5986")), 0, author.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //鐠佸墽鐤嗛弽鍥暯鐎涙ぞ缍嬫径褍鐨妴渚�彯娴滐拷
        if(!StringUtils.isEmpty(objecttitle)){
        	start = title.indexOf(objecttitle);
			if(objecttitle.length()>0 && start>0){
				end = start + objecttitle.length();  
				sp.setSpan(new AbsoluteSizeSpan(14,true), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		        sp.setSpan(new ForegroundColorSpan(Color.parseColor("#0e5986")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
        }
        return sp;
	}
	
	/**
	 * 缂佸嫬鎮庨崝銊︼拷閻ㄥ嫬娲栨径宥嗘瀮閺堬拷
	 * @param name
	 * @param body
	 * @return
	 */
	public static SpannableString parseActiveReply(String name,String body){
		SpannableString sp = new SpannableString(name+"閿涳拷"+body);
		//鐠佸墽鐤嗛悽銊﹀煕閸氬秴鐡ф担鎾冲缁ぜ锟芥妯瑰瘨 
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(Color.parseColor("#0e5986")), 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
	}
	
	/**
	 * 缂佸嫬鎮庡☉鍫熶紖閺傚洦婀�
	 * @param name
	 * @param body
	 * @return
	 */
	public static SpannableString parseMessageSpan(String name,String body,String action){
		SpannableString sp = null;
		int start = 0;
		int end = 0;
		if(StringUtils.isEmpty(action)){
			sp = new SpannableString(name + "閿涳拷" + body);
			end = name.length();
		}else{
			sp = new SpannableString(action + name + "閿涳拷" + body);
			start = action.length();
			end = start + name.length();
		}
		//鐠佸墽鐤嗛悽銊﹀煕閸氬秴鐡ф担鎾冲缁ぜ锟芥妯瑰瘨 
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new ForegroundColorSpan(Color.parseColor("#0e5986")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
	}
	
	/**
	 * 缂佸嫬鎮庨崶鐐差樉瀵洜鏁ら弬鍥ㄦ拱
	 * @param name
	 * @param body
	 * @return
	 */
	public static SpannableString parseQuoteSpan(String name,String body){
		SpannableString sp = new SpannableString("閸ョ偛顦鹃敍锟�"+name+"\n"+body);
		//鐠佸墽鐤嗛悽銊﹀煕閸氬秴鐡ф担鎾冲缁ぜ锟芥妯瑰瘨 
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 3, 3+name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(Color.parseColor("#0e5986")), 3, 3+name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
	}
	
	/**
	 * 瀵懓鍤璗oast濞戝牊浼�
	 * @param msg
	 */
	public static void ToastMessage(Context cont,String msg)
	{
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}
	public static void ToastMessage(Context cont,int msg)
	{
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}
	public static void ToastMessage(Context cont,String msg,int time)
	{
		Toast.makeText(cont, msg, time).show();
	}
	
	/**
	 * 閻愮懓鍤潻鏂挎礀閻╂垵鎯夋禍瀣╂
	 * @param activity
	 * @return
	 */
	public static View.OnClickListener finish(final Activity activity)
	{
		return new View.OnClickListener() {
			public void onClick(View v) {
				activity.finish();
			}
		};
	}	
	
	/**
	 * 閺勫墽銇氶崗鍏呯艾閹存垳婊�
	 * @param context
	 */
	public static void showAbout(Context context)
	{
		Intent intent = new Intent(context, About.class);
		context.startActivity(intent);
	}
	
	/**
	 * 閺勫墽銇氶悽銊﹀煕閸欏秹顪�
	 * @param context
	 */
	public static void showFeedBack(Context context)
	{
		Intent intent = new Intent(context, FeedBack.class);
		context.startActivity(intent);
	}
	
	/**
	 * 閼挎粌宕熼弰鍓с仛閻ц缍嶉幋鏍閸戯拷
	 * @param activity
	 * @param menu
	 */
	public static void showMenuLoginOrLogout(Activity activity,Menu menu)
	{
		if(((AppContext)activity.getApplication()).isLogin()){
			menu.findItem(R.id.main_menu_user).setTitle(R.string.main_menu_logout);
			menu.findItem(R.id.main_menu_user).setIcon(R.drawable.ic_menu_logout);
		}else{
			menu.findItem(R.id.main_menu_user).setTitle(R.string.main_menu_login);
			menu.findItem(R.id.main_menu_user).setIcon(R.drawable.ic_menu_login);
		}
	}
	
	/**
	 * 韫囶偅宓庨弽蹇旀▔缁�櫣娅ヨぐ鏇氱瑢閻ц鍤�
	 * @param activity
	 * @param qa
	 */
	public static void showSettingLoginOrLogout(Activity activity,QuickAction qa)
	{
		if(((AppContext)activity.getApplication()).isLogin()){
			qa.setIcon(MyQuickAction.buildDrawable(activity, R.drawable.ic_menu_logout));
			qa.setTitle(activity.getString(R.string.main_menu_logout));
		}else{
			qa.setIcon(MyQuickAction.buildDrawable(activity, R.drawable.ic_menu_login));
			qa.setTitle(activity.getString(R.string.main_menu_login));
		}
	}
	
	/**
	 * 韫囶偅宓庨弽蹇旀Ц閸氾附妯夌粈鐑樻瀮缁旂姴娴橀悧锟�	 * @param activity
	 * @param qa
	 */
	public static void showSettingIsLoadImage(Activity activity,QuickAction qa)
	{
		if(((AppContext)activity.getApplication()).isLoadImage()){
			qa.setIcon(MyQuickAction.buildDrawable(activity, R.drawable.ic_menu_picnoshow));
			qa.setTitle(activity.getString(R.string.main_menu_picnoshow));
		}else{
			qa.setIcon(MyQuickAction.buildDrawable(activity, R.drawable.ic_menu_picshow));
			qa.setTitle(activity.getString(R.string.main_menu_picshow));
		}
	}
	
	/**
	 * 閻劍鍩涢惂璇茬秿閹存牗鏁為柨锟�	 * @param activity
	 */
	public static void loginOrLogout(Activity activity)
	{
		AppContext ac = (AppContext)activity.getApplication();
		if(ac.isLogin()){
			ac.Logout();
			ToastMessage(activity, "瀹告煡锟介崙铏规瑜帮拷");
		}else{
			showLoginDialog(activity);
		}
	}
	
	/**
	 * 閺傚洨鐝烽弰顖氭儊閸旂姾娴囬崶鍓у閺勫墽銇�
	 * @param activity
	 */
	public static void changeSettingIsLoadImage(Activity activity)
	{
		AppContext ac = (AppContext)activity.getApplication();
		if(ac.isLoadImage()){
			ac.setConfigLoadimage(false);
			ToastMessage(activity, "瀹歌尪顔曠純顔芥瀮缁旂姳绗夐崝鐘烘祰閸ュ墽澧�");
		}else{
			ac.setConfigLoadimage(true);
			ToastMessage(activity, "瀹歌尪顔曠純顔芥瀮缁旂姴濮炴潪钘夋禈閻楋拷");
		}
	}
	public static void changeSettingIsLoadImage(Activity activity,boolean b)
	{
		AppContext ac = (AppContext)activity.getApplication();
		ac.setConfigLoadimage(b);
	}
	
	/**
	 * 濞撳懘娅巃pp缂傛挸鐡�
	 * @param activity
	 */
	public static void clearAppCache(Activity activity)
	{
		final AppContext ac = (AppContext)activity.getApplication();
		final Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				if(msg.what==1){
					ToastMessage(ac, "缂傛挸鐡ㄥ〒鍛存珟閹存劕濮�");
				}else{
					ToastMessage(ac, "缂傛挸鐡ㄥ〒鍛存珟婢惰精瑙�");
				}
			}
		};
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {				
					ac.clearAppCache();
					msg.what = 1;
				} catch (Exception e) {
					e.printStackTrace();
	            	msg.what = -1;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	/**
	 * 闁拷鍤粙瀣碍
	 * @param cont
	 */
	public static void Exit(final Context cont)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_menu_surelogout);
		builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//闁拷鍤�
				AppManager.getAppManager().AppExit(cont);
			}
		});
		builder.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}
}
