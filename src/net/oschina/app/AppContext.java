package net.oschina.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Properties;
import java.util.UUID;

import com.hkzhe.wwtt.R;

import net.oschina.app.api.ApiClient;
import net.oschina.app.bean.ActiveList;
import net.oschina.app.bean.Blog;
import net.oschina.app.bean.BlogCommentList;
import net.oschina.app.bean.BlogList;
import net.oschina.app.bean.CommentList;
import net.oschina.app.bean.FavoriteList;
import net.oschina.app.bean.FriendList;
import net.oschina.app.bean.MessageList;
import net.oschina.app.bean.MyInformation;
import net.oschina.app.bean.News;
import net.oschina.app.bean.NewsList;
import net.oschina.app.bean.Notice;
import net.oschina.app.bean.Post;
import net.oschina.app.bean.PostList;
import net.oschina.app.bean.Result;
import net.oschina.app.bean.SearchList;
import net.oschina.app.bean.Software;
import net.oschina.app.bean.SoftwareCatalogList;
import net.oschina.app.bean.SoftwareList;
import net.oschina.app.bean.Tweet;
import net.oschina.app.bean.TweetList;
import net.oschina.app.bean.User;
import net.oschina.app.bean.UserInformation;
import net.oschina.app.common.CyptoUtils;
import net.oschina.app.common.FileUtils;
import net.oschina.app.common.ImageUtils;
import net.oschina.app.common.MethodsCompat;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
//import android.webkit.CacheManager;
import android.util.Log;

/**
 * 鍏ㄥ眬搴旂敤绋嬪簭绫伙細鐢ㄤ簬淇濆瓨鍜岃皟鐢ㄥ叏灞�簲鐢ㄩ厤缃強璁块棶缃戠粶鏁版嵁
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class AppContext extends Application {
	
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;
	
	public static final int PAGE_SIZE = 20;//榛樿鍒嗛〉澶у皬
	private static final int CACHE_TIME = 10*60000;//缂撳瓨澶辨晥鏃堕棿
	
	private boolean login = false;	//鐧诲綍鐘舵�
	private int loginUid = 0;	//鐧诲綍鐢ㄦ埛鐨刬d
	private Hashtable<String, Object> memCacheRegion = new Hashtable<String, Object>();
	
	private Handler unLoginHandler = new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				UIHelper.ToastMessage(AppContext.this, getString(R.string.msg_login_error));
				UIHelper.showLoginDialog(AppContext.this);
			}
		}		
	};

	/**
	 * 妫�祴缃戠粶鏄惁鍙敤
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 鑾峰彇褰撳墠缃戠粶绫诲瀷
	 * @return 0锛氭病鏈夌綉缁�  1锛歐IFI缃戠粶   2锛歐AP缃戠粶    3锛歂ET缃戠粶
	 */
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}		
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if(!StringUtils.isEmpty(extraInfo)){
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}
	
	/**
	 * 鍒ゆ柇褰撳墠鐗堟湰鏄惁鍏煎鐩爣鐗堟湰鐨勬柟娉�
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}
	
	/**
	 * 鑾峰彇App瀹夎鍖呬俊鎭�
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try { 
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {    
			e.printStackTrace(System.err);
		} 
		if(info == null) info = new PackageInfo();
		return info;
	}
	
	/**
	 * 鑾峰彇App鍞竴鏍囪瘑
	 * @return
	 */
	public String getAppId() {
		String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
		if(StringUtils.isEmpty(uniqueID)){
			uniqueID = UUID.randomUUID().toString();
			setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
		}
		return uniqueID;
	}
	
	/**
	 * 鐢ㄦ埛鏄惁鐧诲綍
	 * @return
	 */
	public boolean isLogin() {
		return login;
	}
	
	/**
	 * 鑾峰彇鐧诲綍鐢ㄦ埛id
	 * @return
	 */
	public int getLoginUid() {
		return this.loginUid;
	}
	
	/**
	 * 鐢ㄦ埛娉ㄩ攢
	 */
	public void Logout() {
		ApiClient.cleanCookie();
		this.cleanCookie();
		this.login = false;
		this.loginUid = 0;
	}
	
	/**
	 * 鏈櫥褰曟垨淇敼瀵嗙爜鍚庣殑澶勭悊
	 */
	public Handler getUnLoginHandler() {
		return this.unLoginHandler;
	}
	
	/**
	 * 鍒濆鍖栫敤鎴风櫥褰曚俊鎭�
	 */
	public void initLoginInfo() {
		User loginUser = getLoginInfo();
		if(loginUser!=null && loginUser.getUid()>0 && loginUser.isRememberMe()){
			this.loginUid = loginUser.getUid();
			this.login = true;
		}else{
			this.Logout();
		}
	}
	
	/**
	 * 鐢ㄦ埛鐧诲綍楠岃瘉
	 * @param account
	 * @param pwd
	 * @return
	 * @throws AppException
	 */
	public User loginVerify(String account, String pwd) throws AppException {
		return ApiClient.login(this, account, pwd);
	}
	
	/**
	 * 鎴戠殑涓汉璧勬枡
	 * @param isRefresh 鏄惁涓诲姩鍒锋柊
	 * @return
	 * @throws AppException
	 */
	public MyInformation getMyInformation(boolean isRefresh) throws AppException {
		MyInformation myinfo = null;
		String key = "myinfo_"+loginUid;
		if(isNetworkConnected() && (isCacheDataFailure(key) || isRefresh)) {
			try{
				myinfo = ApiClient.myInformation(this, loginUid);
				if(myinfo != null && myinfo.getName().length() > 0){
					Notice notice = myinfo.getNotice();
					myinfo.setNotice(null);
					saveObject(myinfo, key);
					myinfo.setNotice(notice);
				}
			}catch(AppException e){
				myinfo = (MyInformation)readObject(key);
				if(myinfo == null)
					throw e;
			}
		} else {
			myinfo = (MyInformation)readObject(key);
			if(myinfo == null)
				myinfo = new MyInformation();
		}
		return myinfo;
	}
	
	/**
	 * 鑾峰彇鐢ㄦ埛淇℃伅涓汉涓撻〉锛堝寘鍚鐢ㄦ埛鐨勫姩鎬佷俊鎭互鍙婁釜浜轰俊鎭級
	 * @param uid 鑷繁鐨剈id
	 * @param hisuid 琚煡鐪嬬敤鎴风殑uid
	 * @param hisname 琚煡鐪嬬敤鎴风殑鐢ㄦ埛鍚�
	 * @param pageIndex 椤甸潰绱㈠紩
	 * @return
	 * @throws AppException
	 */
	public UserInformation getInformation(int uid, int hisuid, String hisname, int pageIndex, boolean isRefresh) throws AppException {
		String _hisname = ""; 
		if(!StringUtils.isEmpty(hisname)){
			_hisname = hisname;
		}
		UserInformation userinfo = null;
		String key = "userinfo_"+uid+"_"+hisuid+"_"+hisname+"_"+pageIndex+"_"+PAGE_SIZE; 
		if(isNetworkConnected() && (isCacheDataFailure(key) || isRefresh)) {			
			try{
				userinfo = ApiClient.information(this, uid, hisuid, _hisname, pageIndex, PAGE_SIZE);
				if(userinfo != null && pageIndex == 0){
					Notice notice = userinfo.getNotice();
					userinfo.setNotice(null);
					saveObject(userinfo, key);
					userinfo.setNotice(notice);
				}
			}catch(AppException e){
				userinfo = (UserInformation)readObject(key);
				if(userinfo == null)
					throw e;
			}
		} else {
			userinfo = (UserInformation)readObject(key);
			if(userinfo == null)
				userinfo = new UserInformation();
		}
		return userinfo;
	}
	
	/**
	 * 鏇存柊鐢ㄦ埛涔嬮棿鍏崇郴锛堝姞鍏虫敞銆佸彇娑堝叧娉級
	 * @param uid 鑷繁鐨剈id
	 * @param hisuid 瀵规柟鐢ㄦ埛鐨剈id
	 * @param newrelation 0:鍙栨秷瀵逛粬鐨勫叧娉�1:鍏虫敞浠�
	 * @return
	 * @throws AppException
	 */
	public Result updateRelation(int uid, int hisuid, int newrelation) throws AppException {
		return ApiClient.updateRelation(this, uid, hisuid, newrelation);
	}
	
	/**
	 * 娓呯┖閫氱煡娑堟伅
	 * @param uid
	 * @param type 1:@鎴戠殑淇℃伅 2:鏈娑堟伅 3:璇勮涓暟 4:鏂扮矇涓濅釜鏁�
	 * @return
	 * @throws AppException
	 */
	public Result noticeClear(int uid, int type) throws AppException {
		return ApiClient.noticeClear(this, uid, type);
	}
	
	/**
	 * 鑾峰彇鐢ㄦ埛閫氱煡淇℃伅
	 * @param uid
	 * @return
	 * @throws AppException
	 */
	public Notice getUserNotice(int uid) throws AppException {
		return ApiClient.getUserNotice(this, uid);
	}
	
	/**
	 * 鐢ㄦ埛鏀惰棌鍒楄〃
	 * @param type 0:鍏ㄩ儴鏀惰棌 1:杞欢 2:璇濋 3:鍗氬 4:鏂伴椈 5:浠ｇ爜
	 * @param pageIndex 椤甸潰绱㈠紩 0琛ㄧず绗竴椤�
	 * @return
	 * @throws AppException
	 */
	public FavoriteList getFavoriteList(int type, int pageIndex, boolean isRefresh) throws AppException {
		FavoriteList list = null;
		String key = "favoritelist_"+loginUid+"_"+type+"_"+pageIndex+"_"+PAGE_SIZE; 
		if(isNetworkConnected() && (isCacheDataFailure(key) || isRefresh)) {
			try{
				list = ApiClient.getFavoriteList(this, loginUid, type, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (FavoriteList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (FavoriteList)readObject(key);
			if(list == null)
				list = new FavoriteList();
		}
		return list;
	}
	
	/**
	 * 鐢ㄦ埛绮変笣銆佸叧娉ㄤ汉鍒楄〃
	 * @param relation 0:鏄剧ず鑷繁鐨勭矇涓�1:鏄剧ず鑷繁鐨勫叧娉ㄨ�
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public FriendList getFriendList(int relation, int pageIndex, boolean isRefresh) throws AppException {
		FriendList list = null;
		String key = "friendlist_"+loginUid+"_"+relation+"_"+pageIndex+"_"+PAGE_SIZE; 
		if(isNetworkConnected() && (isCacheDataFailure(key) || isRefresh)) {
			try{
				list = ApiClient.getFriendList(this, loginUid, relation, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (FriendList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (FriendList)readObject(key);
			if(list == null)
				list = new FriendList();
		}
		return list;
	}
	
	/**
	 * 鏂伴椈鍒楄〃
	 * @param catalog
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws ApiException
	 */
	public NewsList getNewsList(int catalog, int pageIndex, boolean isRefresh) throws AppException {
		NewsList list = null;
		String key = "newslist_"+catalog+"_"+pageIndex+"_"+PAGE_SIZE;
		Log.d("bakey" , "news list key = " + key + ", " + isCacheDataFailure(key) );
		if(isNetworkConnected() && (isCacheDataFailure(key) || isRefresh)) {
			try{
				Log.d("bakey" , "try get news list ");
				list = ApiClient.getNewsList(this, catalog, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Log.d("bakey","get list count = " + list.getNewsCount() );
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (NewsList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (NewsList)readObject(key);
			if(list == null)
				list = new NewsList();
		}
		return list;
	}
	
	/**
	 * 鏂伴椈璇︽儏
	 * @param news_id
	 * @return
	 * @throws ApiException
	 */
	public News getNews(int news_id, boolean isRefresh) throws AppException {		
		News news = null;
		String key = "news_"+news_id;
		if(isNetworkConnected() && (isCacheDataFailure(key) || isRefresh)) {
			try{
				news = ApiClient.getNewsDetail(this, news_id);
				if(news != null){
					Notice notice = news.getNotice();
					news.setNotice(null);
					saveObject(news, key);
					news.setNotice(notice);
				}
			}catch(AppException e){
				news = (News)readObject(key);
				if(news == null)
					throw e;
			}
		} else {
			news = (News)readObject(key);
			if(news == null)
				news = new News();
		}
		return news;		
	}
	
	/**
	 * 鐢ㄦ埛鍗氬鍒楄〃
	 * @param authoruid
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public BlogList getUserBlogList(int authoruid, String authorname, int pageIndex, boolean isRefresh) throws AppException {
		BlogList list = null;
		String key = "userbloglist_"+authoruid+"_"+loginUid+"_"+pageIndex+"_"+PAGE_SIZE;
		if(isNetworkConnected() && (isCacheDataFailure(key) || isRefresh)) {
			try{
				list = ApiClient.getUserBlogList(this, authoruid, authorname, loginUid, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (BlogList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (BlogList)readObject(key);
			if(list == null)
				list = new BlogList();
		}
		return list;
	}
	
	/**
	 * 鍗氬鍒楄〃
	 * @param type 鎺ㄨ崘锛歳ecommend 鏈�柊锛歭atest
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public BlogList getBlogList(String type, int pageIndex, boolean isRefresh) throws AppException {
		BlogList list = null;
		String key = "bloglist_"+type+"_"+pageIndex+"_"+PAGE_SIZE;
		if(isNetworkConnected() && (isCacheDataFailure(key) || isRefresh)) {
			try{
				list = ApiClient.getBlogList(this, type, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (BlogList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (BlogList)readObject(key);
			if(list == null)
				list = new BlogList();
		}
		return list;
	}
	
	/**
	 * 鍗氬璇︽儏
	 * @param blog_id
	 * @return
	 * @throws AppException
	 */
	public Blog getBlog(int blog_id, boolean isRefresh) throws AppException {
		Blog blog = null;
		String key = "blog_"+blog_id;
		if(isNetworkConnected() && (isCacheDataFailure(key) || isRefresh)) {
			try{
				blog = ApiClient.getBlogDetail(this, blog_id);
				if(blog != null){
					Notice notice = blog.getNotice();
					blog.setNotice(null);
					saveObject(blog, key);
					blog.setNotice(notice);
				}
			}catch(AppException e){
				blog = (Blog)readObject(key);
				if(blog == null)
					throw e;
			}
		} else {
			blog = (Blog)readObject(key);
			if(blog == null)
				blog = new Blog();
		}
		return blog;
	}
	
	/**
	 * 杞欢鍒楄〃
	 * @param searchTag 杞欢鍒嗙被  鎺ㄨ崘:recommend 鏈�柊:time 鐑棬:view 鍥戒骇:list_cn
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public SoftwareList getSoftwareList(String searchTag, int pageIndex, boolean isRefresh) throws AppException {
		SoftwareList list = null;
		String key = "softwarelist_"+searchTag+"_"+pageIndex+"_"+PAGE_SIZE;
		if(isNetworkConnected() && (isCacheDataFailure(key) || isRefresh)) {
			try{
				list = ApiClient.getSoftwareList(this, searchTag, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (SoftwareList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (SoftwareList)readObject(key);
			if(list == null)
				list = new SoftwareList();
		}
		return list;
	}
	
	/**
	 * 杞欢鍒嗙被鐨勮蒋浠跺垪琛�
	 * @param searchTag 浠巗oftwarecatalog_list鑾峰彇鐨則ag
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public SoftwareList getSoftwareTagList(int searchTag, int pageIndex, boolean isRefresh) throws AppException {
		SoftwareList list = null;
		String key = "softwaretaglist_"+searchTag+"_"+pageIndex+"_"+PAGE_SIZE;
		if(isNetworkConnected() && (isCacheDataFailure(key) || isRefresh)) {
			try{
				list = ApiClient.getSoftwareTagList(this, searchTag, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (SoftwareList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (SoftwareList)readObject(key);
			if(list == null)
				list = new SoftwareList();
		}
		return list;
	}
	
	/**
	 * 杞欢鍒嗙被鍒楄〃
	 * @param tag 绗竴绾�0  绗簩绾�tag
	 * @return
	 * @throws AppException
	 */
	public SoftwareCatalogList getSoftwareCatalogList(int tag) throws AppException {
		SoftwareCatalogList list = null;
		String key = "softwarecataloglist_"+tag;
		if(isNetworkConnected() && isCacheDataFailure(key)) {
			try{
				list = ApiClient.getSoftwareCatalogList(this, tag);
				if(list != null){
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (SoftwareCatalogList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (SoftwareCatalogList)readObject(key);
			if(list == null)
				list = new SoftwareCatalogList();
		}
		return list;
	}
	
	/**
	 * 杞欢璇︽儏
	 * @param soft_id
	 * @return
	 * @throws AppException
	 */
	public Software getSoftware(String ident, boolean isRefresh) throws AppException {
		Software soft = null;
		String key = "software_"+ident;
		if(isNetworkConnected() && (isCacheDataFailure(key) || isRefresh)) {
			try{
				soft = ApiClient.getSoftwareDetail(this, ident);
				if(soft != null){
					Notice notice = soft.getNotice();
					soft.setNotice(null);
					saveObject(soft, key);
					soft.setNotice(notice);
				}
			}catch(AppException e){
				soft = (Software)readObject(key);
				if(soft == null)
					throw e;
			}
		} else {
			soft = (Software)readObject(key);
			if(soft == null)
				soft = new Software();
		}
		return soft;
	}
	
	/**
	 * 甯栧瓙鍒楄〃
	 * @param catalog
	 * @param pageIndex
	 * @return
	 * @throws ApiException
	 */
	public PostList getPostList(int catalog, int pageIndex, boolean isRefresh) throws AppException {
		PostList list = null;
		String key = "postlist_"+catalog+"_"+pageIndex+"_"+PAGE_SIZE;
		if(isNetworkConnected() && (isCacheDataFailure(key) || isRefresh)) {		
			try{
				list = ApiClient.getPostList(this, catalog, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (PostList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (PostList)readObject(key);
			if(list == null)
				list = new PostList();
		}
		return list;
	}
	
	/**
	 * 璇诲彇甯栧瓙璇︽儏
	 * @param post_id
	 * @return
	 * @throws ApiException
	 */
	public Post getPost(int post_id, boolean isRefresh) throws AppException {		
		Post post = null;
		String key = "post_"+post_id;
		if(isNetworkConnected() && (isCacheDataFailure(key) || isRefresh)) {	
			try{
				post = ApiClient.getPostDetail(this, post_id);
				if(post != null){
					Notice notice = post.getNotice();
					post.setNotice(null);
					saveObject(post, key);
					post.setNotice(notice);
				}
			}catch(AppException e){
				post = (Post)readObject(key);
				if(post == null)
					throw e;
			}
		} else {
			post = (Post)readObject(key);
			if(post == null)
				post = new Post();
		}
		return post;		
	}
	
	/**
	 * 鍔ㄥ脊鍒楄〃
	 * @param catalog -1 鐑棬锛� 鏈�柊锛屽ぇ浜� 鏌愮敤鎴风殑鍔ㄥ脊(uid)
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public TweetList getTweetList(int catalog, int pageIndex, boolean isRefresh) throws AppException {
		TweetList list = null;
		String key = "tweetlist_"+catalog+"_"+pageIndex+"_"+PAGE_SIZE;		
		if(isNetworkConnected() && (isCacheDataFailure(key) || isRefresh)) {
			try{
				list = ApiClient.getTweetList(this, catalog, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (TweetList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (TweetList)readObject(key);
			if(list == null)
				list = new TweetList();
		}
		return list;
	}
	
	/**
	 * 鑾峰彇鍔ㄥ脊璇︽儏
	 * @param tweet_id
	 * @return
	 * @throws AppException
	 */
	public Tweet getTweet(int tweet_id, boolean isRefresh) throws AppException {
		Tweet tweet = null;
		String key = "tweet_"+tweet_id;
		if(isNetworkConnected() && (isCacheDataFailure(key) || isRefresh)) {
			try{
				tweet = ApiClient.getTweetDetail(this, tweet_id);
				if(tweet != null){
					Notice notice = tweet.getNotice();
					tweet.setNotice(null);
					saveObject(tweet, key);
					tweet.setNotice(notice);
				}
			}catch(AppException e){
				tweet = (Tweet)readObject(key);
				if(tweet == null)
					throw e;
			}
		} else {
			tweet = (Tweet)readObject(key);
			if(tweet == null)
				tweet = new Tweet();
		}
		return tweet;
	}
	
	/**
	 * 璇勮鍒楄〃
	 * @param catalog 1鏈�柊鍔ㄦ� 2@鎴�3璇勮 4鎴戣嚜宸�
	 * @param id
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public ActiveList getActiveList(int catalog, int pageIndex, boolean isRefresh) throws AppException {
		ActiveList list = null;
		String key = "activelist_"+loginUid+"_"+catalog+"_"+pageIndex+"_"+PAGE_SIZE;
		if(isNetworkConnected() && (isCacheDataFailure(key) || isRefresh)) {
			try{
				list = ApiClient.getActiveList(this, loginUid, catalog, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (ActiveList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (ActiveList)readObject(key);
			if(list == null)
				list = new ActiveList();
		}
		return list;
	}
	
	/**
	 * 鐣欒█鍒楄〃
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public MessageList getMessageList(int pageIndex, boolean isRefresh) throws AppException {
		MessageList list = null;
		String key = "messagelist_"+loginUid+"_"+pageIndex+"_"+PAGE_SIZE;
		if(isNetworkConnected() && (isCacheDataFailure(key) || isRefresh)) {
			try{
				list = ApiClient.getMessageList(this, loginUid, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (MessageList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (MessageList)readObject(key);
			if(list == null)
				list = new MessageList();
		}
		return list;
	}
	
	/**
	 * 鍗氬璇勮鍒楄〃
	 * @param id 鍗氬Id
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public BlogCommentList getBlogCommentList(int id, int pageIndex, boolean isRefresh) throws AppException {
		BlogCommentList list = null;
		String key = "blogcommentlist_"+id+"_"+pageIndex+"_"+PAGE_SIZE;		
		if(isNetworkConnected() && (isCacheDataFailure(key) || isRefresh)) {
			try{
				list = ApiClient.getBlogCommentList(this, id, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (BlogCommentList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (BlogCommentList)readObject(key);
			if(list == null)
				list = new BlogCommentList();
		}
		return list;
	}
	
	/**
	 * 璇勮鍒楄〃
	 * @param catalog 1鏂伴椈 2甯栧瓙 3鍔ㄥ脊 4鍔ㄦ�
	 * @param id 鏌愭潯鏂伴椈锛屽笘瀛愶紝鍔ㄥ脊鐨刬d 鎴栬�鏌愭潯鐣欒█鐨刦riendid
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public CommentList getCommentList(int catalog, int id, int pageIndex, boolean isRefresh) throws AppException {
		CommentList list = null;
		String key = "commentlist_"+catalog+"_"+id+"_"+pageIndex+"_"+PAGE_SIZE;		
		if(isNetworkConnected() && (isCacheDataFailure(key) || isRefresh)) {
			try{
				list = ApiClient.getCommentList(this, catalog, id, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					Notice notice = list.getNotice();
					list.setNotice(null);
					saveObject(list, key);
					list.setNotice(notice);
				}
			}catch(AppException e){
				list = (CommentList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (CommentList)readObject(key);
			if(list == null)
				list = new CommentList();
		}
		return list;
	}
	
	/**
	 * 鑾峰彇鎼滅储鍒楄〃
	 * @param catalog 鍏ㄩ儴:all 鏂伴椈:news  闂瓟:post 杞欢:software 鍗氬:blog 浠ｇ爜:code
	 * @param content 鎼滅储鐨勫唴瀹�
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public SearchList getSearchList(String catalog, String content, int pageIndex, int pageSize) throws AppException {
		return ApiClient.getSearchList(this, catalog, content, pageIndex, pageSize);
	}
	
	/**
	 * 鍙戝笘瀛�
	 * @param post 锛坲id銆乼itle銆乧atalog銆乧ontent銆乮sNoticeMe锛�
	 * @return
	 * @throws AppException
	 */
	public Result pubPost(Post post) throws AppException {
		return ApiClient.pubPost(this, post);
	}
	
	/**
	 * 鍙戝姩寮�
	 * @param Tweet-uid & msg & image
	 * @return
	 * @throws AppException
	 */
	public Result pubTweet(Tweet tweet) throws AppException {
		return ApiClient.pubTweet(this, tweet);
	}
	
	/**
	 * 鍒犻櫎鍔ㄥ脊
	 * @param uid
	 * @param tweetid
	 * @return
	 * @throws AppException
	 */
	public Result delTweet(int uid, int tweetid) throws AppException {
		return ApiClient.delTweet(this, uid, tweetid);
	}
	
	/**
	 * 鍙戦�鐣欒█
	 * @param uid 鐧诲綍鐢ㄦ埛uid
	 * @param receiver 鎺ュ彈鑰呯殑鐢ㄦ埛id
	 * @param content 娑堟伅鍐呭锛屾敞鎰忎笉鑳借秴杩�50涓瓧绗�
	 * @return
	 * @throws AppException
	 */
	public Result pubMessage(int uid, int receiver, String content) throws AppException {
		return ApiClient.pubMessage(this, uid, receiver, content);
	}
	
	/**
	 * 杞彂鐣欒█
	 * @param uid 鐧诲綍鐢ㄦ埛uid
	 * @param receiver 鎺ュ彈鑰呯殑鐢ㄦ埛鍚�
	 * @param content 娑堟伅鍐呭锛屾敞鎰忎笉鑳借秴杩�50涓瓧绗�
	 * @return
	 * @throws AppException
	 */
	public Result forwardMessage(int uid, String receiver, String content) throws AppException {
		return ApiClient.forwardMessage(this, uid, receiver, content);
	}
	
	/**
	 * 鍒犻櫎鐣欒█
	 * @param uid 鐧诲綍鐢ㄦ埛uid
	 * @param friendid 鐣欒█鑰卛d
	 * @return
	 * @throws AppException
	 */
	public Result delMessage(int uid, int friendid) throws AppException {
		return ApiClient.delMessage(this, uid, friendid);
	}
	
	/**
	 * 鍙戣〃璇勮
	 * @param catalog 1鏂伴椈  2甯栧瓙  3鍔ㄥ脊  4鍔ㄦ�
	 * @param id 鏌愭潯鏂伴椈锛屽笘瀛愶紝鍔ㄥ脊鐨刬d
	 * @param uid 鐢ㄦ埛uid
	 * @param content 鍙戣〃璇勮鐨勫唴瀹�
	 * @param isPostToMyZone 鏄惁杞彂鍒版垜鐨勭┖闂� 0涓嶈浆鍙� 1杞彂
	 * @return
	 * @throws AppException
	 */
	public Result pubComment(int catalog, int id, int uid, String content, int isPostToMyZone) throws AppException {
		return ApiClient.pubComment(this, catalog, id, uid, content, isPostToMyZone);
	}
	
	/**
	 * 
	 * @param id 琛ㄧず琚瘎璁虹殑鏌愭潯鏂伴椈锛屽笘瀛愶紝鍔ㄥ脊鐨刬d 鎴栬�鏌愭潯娑堟伅鐨�friendid 
	 * @param catalog 琛ㄧず璇ヨ瘎璁烘墍灞炰粈涔堢被鍨嬶細1鏂伴椈  2甯栧瓙  3鍔ㄥ脊  4鍔ㄦ�
	 * @param replyid 琛ㄧず琚洖澶嶇殑鍗曚釜璇勮id
	 * @param authorid 琛ㄧず璇ヨ瘎璁虹殑鍘熷浣滆�id
	 * @param uid 鐢ㄦ埛uid 涓�埇閮芥槸褰撳墠鐧诲綍鐢ㄦ埛uid
	 * @param content 鍙戣〃璇勮鐨勫唴瀹�
	 * @return
	 * @throws AppException
	 */
	public Result replyComment(int id, int catalog, int replyid, int authorid, int uid, String content) throws AppException {
		return ApiClient.replyComment(this, id, catalog, replyid, authorid, uid, content);
	}
	
	/**
	 * 鍒犻櫎璇勮
	 * @param id 琛ㄧず琚瘎璁哄搴旂殑鏌愭潯鏂伴椈,甯栧瓙,鍔ㄥ脊鐨刬d 鎴栬�鏌愭潯娑堟伅鐨�friendid
	 * @param catalog 琛ㄧず璇ヨ瘎璁烘墍灞炰粈涔堢被鍨嬶細1鏂伴椈  2甯栧瓙  3鍔ㄥ脊  4鍔ㄦ�&鐣欒█
	 * @param replyid 琛ㄧず琚洖澶嶇殑鍗曚釜璇勮id
	 * @param authorid 琛ㄧず璇ヨ瘎璁虹殑鍘熷浣滆�id
	 * @return
	 * @throws AppException
	 */
	public Result delComment(int id, int catalog, int replyid, int authorid) throws AppException {
		return ApiClient.delComment(this, id, catalog, replyid, authorid);
	}
	
	/**
	 * 鍙戣〃鍗氬璇勮
	 * @param blog 鍗氬id
	 * @param uid 鐧婚檰鐢ㄦ埛鐨剈id
	 * @param content 璇勮鍐呭
	 * @return
	 * @throws AppException
	 */
	public Result pubBlogComment(int blog, int uid, String content) throws AppException {
		return ApiClient.pubBlogComment(this, blog, uid, content);
	}
	
	/**
	 * 鍙戣〃鍗氬璇勮
	 * @param blog 鍗氬id
	 * @param uid 鐧婚檰鐢ㄦ埛鐨剈id
	 * @param content 璇勮鍐呭
	 * @param reply_id 璇勮id
	 * @param objuid 琚瘎璁虹殑璇勮鍙戣〃鑰呯殑uid
	 * @return
	 * @throws AppException
	 */
	public Result replyBlogComment(int blog, int uid, String content, int reply_id, int objuid) throws AppException {
		return ApiClient.replyBlogComment(this, blog, uid, content, reply_id, objuid);
	}
	
	/**
	 * 鍒犻櫎鍗氬璇勮
	 * @param uid 鐧诲綍鐢ㄦ埛鐨剈id
	 * @param blogid 鍗氬id
	 * @param replyid 璇勮id
	 * @param authorid 璇勮鍙戣〃鑰呯殑uid
	 * @param owneruid 鍗氬浣滆�uid
	 * @return
	 * @throws AppException
	 */
	public Result delBlogComment(int uid, int blogid, int replyid, int authorid, int owneruid) throws AppException {
		return ApiClient.delBlogComment(this, uid, blogid, replyid, authorid, owneruid);
	}
	
	/**
	 * 鍒犻櫎鍗氬
	 * @param uid 鐧诲綍鐢ㄦ埛鐨剈id
	 * @param authoruid 鍗氬浣滆�uid
	 * @param id 鍗氬id
	 * @return
	 * @throws AppException
	 */
	public Result delBlog(int uid, int authoruid, int id) throws AppException { 	
		return ApiClient.delBlog(this, uid, authoruid, id);
	}
	
	/**
	 * 鐢ㄦ埛娣诲姞鏀惰棌
	 * @param uid 鐢ㄦ埛UID
	 * @param objid 姣斿鏄柊闂籌D 鎴栬�闂瓟ID 鎴栬�鍔ㄥ脊ID
	 * @param type 1:杞欢 2:璇濋 3:鍗氬 4:鏂伴椈 5:浠ｇ爜
	 * @return
	 * @throws AppException
	 */
	public Result addFavorite(int uid, int objid, int type) throws AppException {
		return ApiClient.addFavorite(this, uid, objid, type);
	}
	
	/**
	 * 鐢ㄦ埛鍒犻櫎鏀惰棌
	 * @param uid 鐢ㄦ埛UID
	 * @param objid 姣斿鏄柊闂籌D 鎴栬�闂瓟ID 鎴栬�鍔ㄥ脊ID
	 * @param type 1:杞欢 2:璇濋 3:鍗氬 4:鏂伴椈 5:浠ｇ爜
	 * @return
	 * @throws AppException
	 */
	public Result delFavorite(int uid, int objid, int type) throws AppException { 	
		return ApiClient.delFavorite(this, uid, objid, type);
	}
	
	/**
	 * 淇濆瓨鐧诲綍淇℃伅
	 * @param username
	 * @param pwd
	 */
	public void saveLoginInfo(final User user) {
		this.loginUid = user.getUid();
		this.login = true;
		setProperties(new Properties(){{
			setProperty("user.uid", String.valueOf(user.getUid()));
			setProperty("user.name", user.getName());
			setProperty("user.face", FileUtils.getFileName(user.getFace()));//鐢ㄦ埛澶村儚-鏂囦欢鍚�
			setProperty("user.account", user.getAccount());
			setProperty("user.pwd", CyptoUtils.encode("oschinaApp",user.getPwd()));
			setProperty("user.location", user.getLocation());
			setProperty("user.followers", String.valueOf(user.getFollowers()));
			setProperty("user.fans", String.valueOf(user.getFans()));
			setProperty("user.score", String.valueOf(user.getScore()));
			setProperty("user.isRememberMe", String.valueOf(user.isRememberMe()));//鏄惁璁颁綇鎴戠殑淇℃伅
		}});		
	}
	
	/**
	 * 娓呴櫎鐧诲綍淇℃伅
	 */
	public void cleanLoginInfo() {
		this.loginUid = 0;
		this.login = false;
		removeProperty("user.uid","user.name","user.face","user.account","user.pwd",
				"user.location","user.followers","user.fans","user.score","user.isRememberMe");
	}
	
	/**
	 * 鑾峰彇鐧诲綍淇℃伅
	 * @return
	 */
	public User getLoginInfo() {		
		User lu = new User();		
		lu.setUid(StringUtils.toInt(getProperty("user.uid"), 0));
		lu.setName(getProperty("user.name"));
		lu.setFace(getProperty("user.face"));
		lu.setAccount(getProperty("user.account"));
		lu.setPwd(CyptoUtils.decode("oschinaApp",getProperty("user.pwd")));
		lu.setLocation(getProperty("user.location"));
		lu.setFollowers(StringUtils.toInt(getProperty("user.followers"), 0));
		lu.setFans(StringUtils.toInt(getProperty("user.fans"), 0));
		lu.setScore(StringUtils.toInt(getProperty("user.score"), 0));
		lu.setRememberMe(StringUtils.toBool(getProperty("user.isRememberMe")));
		return lu;
	}
	
	/**
	 * 淇濆瓨鐢ㄦ埛澶村儚
	 * @param fileName
	 * @param bitmap
	 */
	public void saveUserFace(String fileName,Bitmap bitmap) {
		try {
			ImageUtils.saveImage(this, fileName, bitmap);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 鑾峰彇鐢ㄦ埛澶村儚
	 * @param key
	 * @return
	 * @throws AppException
	 */
	public Bitmap getUserFace(String key) throws AppException {
		FileInputStream fis = null;
		try{
			fis = openFileInput(key);
			return BitmapFactory.decodeStream(fis);
		}catch(Exception e){
			throw AppException.run(e);
		}finally{
			try {
				fis.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 鏄惁鍔犺浇鏄剧ず鏂囩珷鍥剧墖
	 * @return
	 */
	public boolean isLoadImage()
	{
		String perf_loadimage = getProperty(AppConfig.CONF_LOAD_IMAGE);
		//榛樿鏄姞杞界殑
		if(StringUtils.isEmpty(perf_loadimage))
			return true;
		else
			return StringUtils.toBool(perf_loadimage);
	}
	
	/**
	 * 璁剧疆鏄惁鍔犺浇鏂囩珷鍥剧墖
	 * @param b
	 */
	public void setConfigLoadimage(boolean b)
	{
		setProperty(AppConfig.CONF_LOAD_IMAGE, String.valueOf(b));
	}
	
	/**
	 * 鏄惁宸﹀彸婊戝姩
	 * @return
	 */
	public boolean isScroll()
	{
		String perf_scroll = getProperty(AppConfig.CONF_SCROLL);
		//榛樿鏄叧闂乏鍙虫粦鍔�
		if(StringUtils.isEmpty(perf_scroll))
			return false;
		else
			return StringUtils.toBool(perf_scroll);
	}
	
	/**
	 * 璁剧疆鏄惁宸﹀彸婊戝姩
	 * @param b
	 */
	public void setConfigScroll(boolean b)
	{
		setProperty(AppConfig.CONF_SCROLL, String.valueOf(b));
	}
	
	/**
	 * 鏄惁Https鐧诲綍
	 * @return
	 */
	public boolean isHttpsLogin()
	{
		String perf_httpslogin = getProperty(AppConfig.CONF_HTTPS_LOGIN);
		//榛樿鏄痟ttp
		if(StringUtils.isEmpty(perf_httpslogin))
			return false;
		else
			return StringUtils.toBool(perf_httpslogin);
	}
	
	/**
	 * 璁剧疆鏄槸鍚ttps鐧诲綍
	 * @param b
	 */
	public void setConfigHttpsLogin(boolean b)
	{
		setProperty(AppConfig.CONF_HTTPS_LOGIN, String.valueOf(b));
	}
	
	/**
	 * 娓呴櫎淇濆瓨鐨勭紦瀛�
	 */
	public void cleanCookie()
	{
		removeProperty(AppConfig.CONF_COOKIE);
	}
	

	public boolean isCacheDataFailure(String cachefile)
	{
		/*boolean failure = false;
		File data = getFileStreamPath(cachefile);
		if(data.exists() && (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
			failure = true;
		else if(!data.exists())
			failure = true;
		return failure;*/
		return true;
	}
	
	/**
	 * 娓呴櫎app缂撳瓨
	 */
	public void clearAppCache()
	{
		//娓呴櫎webview缂撳瓨
		/*File file = CacheManager.getCacheFileBaseDir();  
		if (file != null && file.exists() && file.isDirectory()) {  
		    for (File item : file.listFiles()) {  
		    	item.delete();  
		    }  
		    file.delete();  
		} */ 		  
		deleteDatabase("webview.db");  
		deleteDatabase("webview.db-shm");  
		deleteDatabase("webview.db-wal");  
		deleteDatabase("webviewCache.db");  
		deleteDatabase("webviewCache.db-shm");  
		deleteDatabase("webviewCache.db-wal");  
		//娓呴櫎鏁版嵁缂撳瓨
		clearCacheFolder(getFilesDir(),System.currentTimeMillis());
		clearCacheFolder(getCacheDir(),System.currentTimeMillis());
		//2.2鐗堟湰鎵嶆湁灏嗗簲鐢ㄧ紦瀛樿浆绉诲埌sd鍗＄殑鍔熻兘
		if(isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)){
			clearCacheFolder(MethodsCompat.getExternalCacheDir(this),System.currentTimeMillis());
		}
		//娓呴櫎缂栬緫鍣ㄤ繚瀛樼殑涓存椂鍐呭
		Properties props = getProperties();
		for(Object key : props.keySet()) {
			String _key = key.toString();
			if(_key.startsWith("temp"))
				removeProperty(_key);
		}
	}	
	
	// clear the cache before time numDays     
	private int clearCacheFolder(File dir, long numDays) {          
	    int deletedFiles = 0;         
	    if (dir!= null && dir.isDirectory()) {             
	        try {                
	            for (File child:dir.listFiles()) {    
	                if (child.isDirectory()) {              
	                    deletedFiles += clearCacheFolder(child, numDays);          
	                }  
	                if (child.lastModified() < numDays) {     
	                    if (child.delete()) {                   
	                        deletedFiles++;           
	                    }    
	                }    
	            }             
	        } catch(Exception e) {       
	            e.printStackTrace();    
	        }     
	    }       
	    return deletedFiles;     
	}
	
	/**
	 * 灏嗗璞′繚瀛樺埌鍐呭瓨缂撳瓨涓�
	 * @param key
	 * @param value
	 */
	public void setMemCache(String key, Object value) {
		memCacheRegion.put(key, value);
	}
	
	/**
	 * 浠庡唴瀛樼紦瀛樹腑鑾峰彇瀵硅薄
	 * @param key
	 * @return
	 */
	public Object getMemCache(String key){
		return memCacheRegion.get(key);
	}
	
	/**
	 * 淇濆瓨纾佺洏缂撳瓨
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public void setDiskCache(String key, String value) throws IOException {
		FileOutputStream fos = null;
		try{
			fos = openFileOutput("cache_"+key+".data", Context.MODE_PRIVATE);
			fos.write(value.getBytes());
			fos.flush();
		}finally{
			try {
				fos.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 鑾峰彇纾佺洏缂撳瓨鏁版嵁
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public String getDiskCache(String key) throws IOException {
		FileInputStream fis = null;
		try{
			fis = openFileInput("cache_"+key+".data");
			byte[] datas = new byte[fis.available()];
			fis.read(datas);
			return new String(datas);
		}finally{
			try {
				fis.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 淇濆瓨瀵硅薄
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public boolean saveObject(Serializable ser, String file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try{
			fos = openFileOutput(file, MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}finally{
			try {
				oos.close();
			} catch (Exception e) {}
			try {
				fos.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 璇诲彇瀵硅薄
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file){
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try{
			fis = openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable)ois.readObject();
		}catch(FileNotFoundException e){
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				ois.close();
			} catch (Exception e) {}
			try {
				fis.close();
			} catch (Exception e) {}
		}
		return null;
	}

	public void setProperties(Properties ps){
		AppConfig.getAppConfig(this).set(ps);
	}

	public Properties getProperties(){
		return AppConfig.getAppConfig(this).get();
	}
	
	public void setProperty(String key,String value){
		AppConfig.getAppConfig(this).set(key, value);
	}
	
	public String getProperty(String key){
		return AppConfig.getAppConfig(this).get(key);
	}
	public void removeProperty(String...key){
		AppConfig.getAppConfig(this).remove(key);
	}	
}
