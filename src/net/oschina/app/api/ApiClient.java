package net.oschina.app.api;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import net.oschina.app.AppContext;
import net.oschina.app.AppException;
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
import net.oschina.app.bean.URLs;
import net.oschina.app.bean.Update;
import net.oschina.app.bean.User;
import net.oschina.app.bean.UserInformation;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * API瀹㈡埛绔帴鍙ｏ細鐢ㄤ簬璁块棶缃戠粶鏁版嵁
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ApiClient {

	public static final String UTF_8 = "UTF-8";
	public static final String DESC = "descend";
	public static final String ASC = "ascend";
	
	private final static int TIMEOUT_CONNECTION = 20000;
	private final static int TIMEOUT_SOCKET = 20000;
	private final static int RETRY_TIME = 3;

	private static String appCookie;
	private static String appUserAgent;

	public static void cleanCookie() {
		appCookie = "";
	}
	
	private static String getCookie(AppContext appContext) {
		if(appCookie == null || appCookie == "") {
			appCookie = appContext.getProperty("cookie");
		}
		return appCookie;
	}
	
	private static String getUserAgent(AppContext appContext) {
		if(appUserAgent == null || appUserAgent == "") {
			StringBuilder ua = new StringBuilder("OSChina.NET");
			ua.append('/'+appContext.getPackageInfo().versionName+'_'+appContext.getPackageInfo().versionCode);//App鐗堟湰
			ua.append("/Android");//鎵嬫満绯荤粺骞冲彴
			ua.append("/"+android.os.Build.VERSION.RELEASE);//鎵嬫満绯荤粺鐗堟湰
			ua.append("/"+android.os.Build.MODEL); //鎵嬫満鍨嬪彿
			ua.append("/"+appContext.getAppId());//瀹㈡埛绔敮涓�爣璇�
			appUserAgent = ua.toString();
		}
		return appUserAgent;
	}
	
	private static HttpClient getHttpClient() {        
        HttpClient httpClient = new HttpClient();
		// 璁剧疆 HttpClient 鎺ユ敹 Cookie,鐢ㄤ笌娴忚鍣ㄤ竴鏍风殑绛栫暐
		httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        // 璁剧疆 榛樿鐨勮秴鏃堕噸璇曞鐞嗙瓥鐣�
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		// 璁剧疆 杩炴帴瓒呮椂鏃堕棿
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT_CONNECTION);
		// 璁剧疆 璇绘暟鎹秴鏃舵椂闂�
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT_SOCKET);
		// 璁剧疆 瀛楃闆�
		httpClient.getParams().setContentCharset(UTF_8);
		return httpClient;
	}	
	
	private static GetMethod getHttpGet(String url, String cookie, String userAgent) {
		GetMethod httpGet = new GetMethod(url);
		// 璁剧疆 璇锋眰瓒呮椂鏃堕棿
		httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpGet.setRequestHeader("Host", URLs.HOST);
		httpGet.setRequestHeader("Connection","Keep-Alive");
		httpGet.setRequestHeader("Cookie", cookie);
		httpGet.setRequestHeader("User-Agent", userAgent);
		return httpGet;
	}
	
	private static PostMethod getHttpPost(String url, String cookie, String userAgent) {
		PostMethod httpPost = new PostMethod(url);
		// 璁剧疆 璇锋眰瓒呮椂鏃堕棿
		httpPost.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpPost.setRequestHeader("Host", URLs.HOST);
		httpPost.setRequestHeader("Connection","Keep-Alive");
		httpPost.setRequestHeader("Cookie", cookie);
		httpPost.setRequestHeader("User-Agent", userAgent);
		return httpPost;
	}
	
	private static String _MakeURL(String p_url, Map<String, Object> params) {
		StringBuilder url = new StringBuilder(p_url);
		if(url.indexOf("?")<0)
			url.append('?');

		for(String name : params.keySet()){
			url.append('&');
			url.append(name);
			url.append('=');
			url.append(String.valueOf(params.get(name)));
			//涓嶅仛URLEncoder澶勭悊
			//url.append(URLEncoder.encode(String.valueOf(params.get(name)), UTF_8));
		}

		return url.toString().replace("?&", "?");
	}
	
	/**
	 * get璇锋眰URL
	 * @param url
	 * @throws AppException 
	 */
	private static InputStream http_get(AppContext appContext, String url) throws AppException {	
		//System.out.println("get_url==> "+url);
		Log.d("bakey" , "get url = " + url );
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);
		
		HttpClient httpClient = null;
		GetMethod httpGet = null;

		String responseBody = "";
		int time = 0;
		do{
			try 
			{
				httpClient = getHttpClient();
				httpGet = getHttpGet(url, cookie, userAgent);			
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
				responseBody = httpGet.getResponseBodyAsString();
				//System.out.println("XMLDATA=====>"+responseBody);
				break;				
			} catch (HttpException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 鍙戠敓鑷村懡鐨勫紓甯革紝鍙兘鏄崗璁笉瀵规垨鑰呰繑鍥炵殑鍐呭鏈夐棶棰�
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 鍙戠敓缃戠粶寮傚父
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 閲婃斁杩炴帴
				httpGet.releaseConnection();
				httpClient = null;
			}
		}while(time < RETRY_TIME);
		
		responseBody = responseBody.replace('', '?');
		if(responseBody.contains("result") && responseBody.contains("errorCode")){
			try {
				Result res = Result.parse(new ByteArrayInputStream(responseBody.getBytes()));	
				if(res.getErrorCode() == 0){
					appContext.Logout();
					appContext.getUnLoginHandler().sendEmptyMessage(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
		return new ByteArrayInputStream(responseBody.getBytes());
	}
	
	/**
	 * 鍏敤post鏂规硶
	 * @param url
	 * @param params
	 * @param files
	 * @throws AppException
	 */
	private static InputStream _post(AppContext appContext, String url, Map<String, Object> params, Map<String,File> files) throws AppException {
		//System.out.println("post_url==> "+url);
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);
		
		HttpClient httpClient = null;
		PostMethod httpPost = null;
		
		//post琛ㄥ崟鍙傛暟澶勭悊
		int length = (params == null ? 0 : params.size()) + (files == null ? 0 : files.size());
		Part[] parts = new Part[length];
		int i = 0;
        if(params != null)
        for(String name : params.keySet()){
        	parts[i++] = new StringPart(name, String.valueOf(params.get(name)), UTF_8);
        	//System.out.println("post_key==> "+name+"    value==>"+String.valueOf(params.get(name)));
        }
        if(files != null)
        for(String file : files.keySet()){
        	try {
				parts[i++] = new FilePart(file, files.get(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
        	//System.out.println("post_key_file==> "+file);
        }
		
		String responseBody = "";
		int time = 0;
		do{
			try 
			{
				httpClient = getHttpClient();
				httpPost = getHttpPost(url, cookie, userAgent);	        
		        httpPost.setRequestEntity(new MultipartRequestEntity(parts,httpPost.getParams()));		        
		        int statusCode = httpClient.executeMethod(httpPost);
		        if(statusCode != HttpStatus.SC_OK) 
		        {
		        	throw AppException.http(statusCode);
		        }
		        else if(statusCode == HttpStatus.SC_OK) 
		        {
		            Cookie[] cookies = httpClient.getState().getCookies();
		            String tmpcookies = "";
		            for (Cookie ck : cookies) {
		                tmpcookies += ck.toString()+";";
		            }
		            //淇濆瓨cookie   
	        		if(appContext != null && tmpcookies != ""){
	        			appContext.setProperty("cookie", tmpcookies);
	        			appCookie = tmpcookies;
	        		}
		        }
		     	responseBody = httpPost.getResponseBodyAsString();
		        //System.out.println("XMLDATA=====>"+responseBody);
		     	break;	     	
			} catch (HttpException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 鍙戠敓鑷村懡鐨勫紓甯革紝鍙兘鏄崗璁笉瀵规垨鑰呰繑鍥炵殑鍐呭鏈夐棶棰�
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 鍙戠敓缃戠粶寮傚父
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 閲婃斁杩炴帴
				httpPost.releaseConnection();
				httpClient = null;
			}
		}while(time < RETRY_TIME);
        
        responseBody = responseBody.replace('', '?');
		if(responseBody.contains("result") && responseBody.contains("errorCode")){
			try {
				Result res = Result.parse(new ByteArrayInputStream(responseBody.getBytes()));	
				if(res.getErrorCode() == 0){
					appContext.Logout();
					appContext.getUnLoginHandler().sendEmptyMessage(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
        return new ByteArrayInputStream(responseBody.getBytes());
	}
	
	/**
	 * post璇锋眰URL
	 * @param url
	 * @param params
	 * @param files
	 * @throws AppException 
	 * @throws IOException 
	 * @throws  
	 */
	private static Result http_post(AppContext appContext, String url, Map<String, Object> params, Map<String,File> files) throws AppException, IOException {
        return Result.parse(_post(appContext, url, params, files));  
	}	
	
	/**
	 * 鑾峰彇缃戠粶鍥剧墖
	 * @param url
	 * @return
	 */
	public static Bitmap getNetBitmap(String url) throws AppException {
		//System.out.println("image_url==> "+url);
		HttpClient httpClient = null;
		GetMethod httpGet = null;
		Bitmap bitmap = null;
		int time = 0;
		do{
			try 
			{
				httpClient = getHttpClient();
				httpGet = getHttpGet(url, null, null);
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
		        InputStream inStream = httpGet.getResponseBodyAsStream();
		        bitmap = BitmapFactory.decodeStream(inStream);
		        inStream.close();
		        break;
			} catch (HttpException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 鍙戠敓鑷村懡鐨勫紓甯革紝鍙兘鏄崗璁笉瀵规垨鑰呰繑鍥炵殑鍐呭鏈夐棶棰�
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 鍙戠敓缃戠粶寮傚父
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 閲婃斁杩炴帴
				httpGet.releaseConnection();
				httpClient = null;
			}
		}while(time < RETRY_TIME);
		return bitmap;
	}
	
	/**
	 * 妫�煡鐗堟湰鏇存柊
	 * @param url
	 * @return
	 */
	public static Update checkVersion(AppContext appContext) throws AppException {
		try{
			return Update.parse(http_get(appContext, URLs.UPDATE_VERSION));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鐧诲綍锛�鑷姩澶勭悊cookie
	 * @param url
	 * @param username
	 * @param pwd
	 * @return
	 * @throws AppException
	 */
	public static User login(AppContext appContext, String username, String pwd) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("username", username);
		params.put("pwd", pwd);
		params.put("keep_login", 1);
				
		String loginurl = URLs.LOGIN_VALIDATE_HTTP;
		if(appContext.isHttpsLogin()){
			loginurl = URLs.LOGIN_VALIDATE_HTTPS;
		}
		
		try{
			return User.parse(_post(appContext, loginurl, params, null));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}

	/**
	 * 鎴戠殑涓汉璧勬枡
	 * @param appContext
	 * @param uid
	 * @return
	 * @throws AppException
	 */
	public static MyInformation myInformation(AppContext appContext, int uid) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
				
		try{
			return MyInformation.parse(_post(appContext, URLs.MY_INFORMATION, params, null));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鑾峰彇鐢ㄦ埛淇℃伅涓汉涓撻〉锛堝寘鍚鐢ㄦ埛鐨勫姩鎬佷俊鎭互鍙婁釜浜轰俊鎭級
	 * @param uid 鑷繁鐨剈id
	 * @param hisuid 琚煡鐪嬬敤鎴风殑uid
	 * @param hisname 琚煡鐪嬬敤鎴风殑鐢ㄦ埛鍚�
	 * @param pageIndex 椤甸潰绱㈠紩
	 * @param pageSize 姣忛〉璇诲彇鐨勫姩鎬佷釜鏁�
	 * @return
	 * @throws AppException
	 */
	public static UserInformation information(AppContext appContext, int uid, int hisuid, String hisname, int pageIndex, int pageSize) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("hisuid", hisuid);
		params.put("hisname", hisname);
		params.put("pageIndex", pageIndex);
		params.put("pageSize", pageSize);
				
		try{
			return UserInformation.parse(_post(appContext, URLs.USER_INFORMATION, params, null));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鏇存柊鐢ㄦ埛涔嬮棿鍏崇郴锛堝姞鍏虫敞銆佸彇娑堝叧娉級
	 * @param uid 鑷繁鐨剈id
	 * @param hisuid 瀵规柟鐢ㄦ埛鐨剈id
	 * @param newrelation 0:鍙栨秷瀵逛粬鐨勫叧娉�1:鍏虫敞浠�
	 * @return
	 * @throws AppException
	 */
	public static Result updateRelation(AppContext appContext, int uid, int hisuid, int newrelation) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("hisuid", hisuid);
		params.put("newrelation", newrelation);
				
		try{
			return Result.parse(_post(appContext, URLs.USER_UPDATERELATION, params, null));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鑾峰彇鐢ㄦ埛閫氱煡淇℃伅
	 * @param uid
	 * @return
	 * @throws AppException
	 */
	public static Notice getUserNotice(AppContext appContext, int uid) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
				
		try{
			return Notice.parse(_post(appContext, URLs.USER_NOTICE, params, null));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 娓呯┖閫氱煡娑堟伅
	 * @param uid
	 * @param type 1:@鎴戠殑淇℃伅 2:鏈娑堟伅 3:璇勮涓暟 4:鏂扮矇涓濅釜鏁�
	 * @return
	 * @throws AppException
	 */
	public static Result noticeClear(AppContext appContext, int uid, int type) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("type", type);
				
		try{
			return Result.parse(_post(appContext, URLs.NOTICE_CLEAR, params, null));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鐢ㄦ埛绮変笣銆佸叧娉ㄤ汉鍒楄〃
	 * @param uid
	 * @param relation 0:鏄剧ず鑷繁鐨勭矇涓�1:鏄剧ず鑷繁鐨勫叧娉ㄨ�
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static FriendList getFriendList(AppContext appContext, final int uid, final int relation, final int pageIndex, final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.FRIENDS_LIST, new HashMap<String, Object>(){{
			put("uid", uid);
			put("relation", relation);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});
		
		try{
			return FriendList.parse(http_get(appContext, newUrl));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鑾峰彇璧勮鍒楄〃
	 * @param url
	 * @param catalog
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static NewsList getNewsList(AppContext appContext, final int catalog, final int pageIndex, final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.NEWS_LIST, new HashMap<String, Object>(){{
			put("catalog", catalog);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});
		
		try{
			return NewsList.parse(http_get(appContext, newUrl));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鑾峰彇璧勮鐨勮鎯�
	 * @param url
	 * @param news_id
	 * @return
	 * @throws AppException
	 */
	public static News getNewsDetail(AppContext appContext, final int news_id) throws AppException {
		String newUrl = _MakeURL(URLs.NEWS_DETAIL, new HashMap<String, Object>(){{
			put("id", news_id);
		}});
		
		try{
			return News.parse(http_get(appContext, newUrl));			
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鑾峰彇鏌愮敤鎴风殑鍗氬鍒楄〃
	 * @param authoruid
	 * @param uid
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static BlogList getUserBlogList(AppContext appContext, final int authoruid, final String authorname, final int uid, final int pageIndex, final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.USERBLOG_LIST, new HashMap<String, Object>(){{
			put("authoruid", authoruid);
			put("authorname", URLEncoder.encode(authorname));
			put("uid", uid);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});

		try{
			return BlogList.parse(http_get(appContext, newUrl));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鑾峰彇鍗氬鍒楄〃
	 * @param type 鎺ㄨ崘锛歳ecommend 鏈�柊锛歭atest
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static BlogList getBlogList(AppContext appContext, final String type, final int pageIndex, final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.BLOG_LIST, new HashMap<String, Object>(){{
			put("type", type);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});

		try{
			return BlogList.parse(http_get(appContext, newUrl));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鍒犻櫎鏌愮敤鎴风殑鍗氬
	 * @param uid
	 * @param authoruid
	 * @param id
	 * @return
	 * @throws AppException
	 */
	public static Result delBlog(AppContext appContext, int uid, int authoruid, int id) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("authoruid", authoruid);
		params.put("id", id);

		try{
			return http_post(appContext, URLs.USERBLOG_DELETE, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鑾峰彇鍗氬璇︽儏
	 * @param blog_id
	 * @return
	 * @throws AppException
	 */
	public static Blog getBlogDetail(AppContext appContext, final int blog_id) throws AppException {
		String newUrl = _MakeURL(URLs.BLOG_DETAIL, new HashMap<String, Object>(){{
			put("id", blog_id);
		}});
		
		try{
			return Blog.parse(http_get(appContext, newUrl));			
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鑾峰彇杞欢璇︽儏
	 * @param soft_id
	 * @return
	 * @throws AppException
	 */
	public static Software getSoftwareDetail(AppContext appContext, final String ident) throws AppException {
		String newUrl = _MakeURL(URLs.SOFTWARE_DETAIL, new HashMap<String, Object>(){{
			put("ident", ident);
		}});
		
		try{
			return Software.parse(http_get(appContext, newUrl));			
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鑾峰彇甯栧瓙鍒楄〃
	 * @param url
	 * @param catalog
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public static PostList getPostList(AppContext appContext, final int catalog, final int pageIndex, final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.POST_LIST, new HashMap<String, Object>(){{
			put("catalog", catalog);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});

		try{
			return PostList.parse(http_get(appContext, newUrl));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鑾峰彇甯栧瓙鐨勮鎯�
	 * @param url
	 * @param post_id
	 * @return
	 * @throws AppException
	 */
	public static Post getPostDetail(AppContext appContext, final int post_id) throws AppException {
		String newUrl = _MakeURL(URLs.POST_DETAIL, new HashMap<String, Object>(){{
			put("id", post_id);
		}});
		try{
			return Post.parse(http_get(appContext, newUrl));			
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鍙戝笘瀛�
	 * @param post 锛坲id銆乼itle銆乧atalog銆乧ontent銆乮sNoticeMe锛�
	 * @return
	 * @throws AppException
	 */
	public static Result pubPost(AppContext appContext, Post post) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", post.getAuthorId());
		params.put("title", post.getTitle());
		params.put("catalog", post.getCatalog());
		params.put("content", post.getBody());
		params.put("isNoticeMe", post.getIsNoticeMe());				
		
		try{
			return http_post(appContext, URLs.POST_PUB, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鑾峰彇鍔ㄥ脊鍒楄〃
	 * @param uid
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static TweetList getTweetList(AppContext appContext, final int uid, final int pageIndex, final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.TWEET_LIST, new HashMap<String, Object>(){{
			put("uid", uid);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});
		
		try{
			return TweetList.parse(http_get(appContext, newUrl));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鑾峰彇鍔ㄥ脊璇︽儏
	 * @param tweet_id
	 * @return
	 * @throws AppException
	 */
	public static Tweet getTweetDetail(AppContext appContext, final int tweet_id) throws AppException {
		String newUrl = _MakeURL(URLs.TWEET_DETAIL, new HashMap<String, Object>(){{
			put("id", tweet_id);
		}});
		try{
			return Tweet.parse(http_get(appContext, newUrl));			
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鍙戝姩寮�
	 * @param Tweet-uid & msg & image
	 * @return
	 * @throws AppException
	 */
	public static Result pubTweet(AppContext appContext, Tweet tweet) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", tweet.getAuthorId());
		params.put("msg", tweet.getBody());
				
		Map<String, File> files = new HashMap<String, File>();
		if(tweet.getImageFile() != null)
			files.put("img", tweet.getImageFile());
		
		try{
			return http_post(appContext, URLs.TWEET_PUB, params, files);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}

	/**
	 * 鍒犻櫎鍔ㄥ脊
	 * @param uid
	 * @param tweetid
	 * @return
	 * @throws AppException
	 */
	public static Result delTweet(AppContext appContext, int uid, int tweetid) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("tweetid", tweetid);

		try{
			return http_post(appContext, URLs.TWEET_DELETE, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鑾峰彇鍔ㄦ�鍒楄〃
	 * @param uid
	 * @param catalog 1鏈�柊鍔ㄦ�  2@鎴� 3璇勮  4鎴戣嚜宸�
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static ActiveList getActiveList(AppContext appContext, final int uid,final int catalog, final int pageIndex, final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.ACTIVE_LIST, new HashMap<String, Object>(){{
			put("uid", uid);
			put("catalog", catalog);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});
		
		try{
			return ActiveList.parse(http_get(appContext, newUrl));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鑾峰彇鐣欒█鍒楄〃
	 * @param uid
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public static MessageList getMessageList(AppContext appContext, final int uid, final int pageIndex, final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.MESSAGE_LIST, new HashMap<String, Object>(){{
			put("uid", uid);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});
		
		try{
			return MessageList.parse(http_get(appContext, newUrl));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鍙戦�鐣欒█
	 * @param uid 鐧诲綍鐢ㄦ埛uid
	 * @param receiver 鎺ュ彈鑰呯殑鐢ㄦ埛id
	 * @param content 娑堟伅鍐呭锛屾敞鎰忎笉鑳借秴杩�50涓瓧绗�
	 * @return
	 * @throws AppException
	 */
	public static Result pubMessage(AppContext appContext, int uid, int receiver, String content) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("receiver", receiver);
		params.put("content", content);

		try{
			return http_post(appContext, URLs.MESSAGE_PUB, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 杞彂鐣欒█
	 * @param uid 鐧诲綍鐢ㄦ埛uid
	 * @param receiver 鎺ュ彈鑰呯殑鐢ㄦ埛鍚�
	 * @param content 娑堟伅鍐呭锛屾敞鎰忎笉鑳借秴杩�50涓瓧绗�
	 * @return
	 * @throws AppException
	 */
	public static Result forwardMessage(AppContext appContext, int uid, String receiverName, String content) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("receiverName", receiverName);
		params.put("content", content);

		try{
			return http_post(appContext, URLs.MESSAGE_PUB, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鍒犻櫎鐣欒█
	 * @param uid 鐧诲綍鐢ㄦ埛uid
	 * @param friendid 鐣欒█鑰卛d
	 * @return
	 * @throws AppException
	 */
	public static Result delMessage(AppContext appContext, int uid, int friendid) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("friendid", friendid);

		try{
			return http_post(appContext, URLs.MESSAGE_DELETE, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鑾峰彇鍗氬璇勮鍒楄〃
	 * @param id 鍗氬id
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static BlogCommentList getBlogCommentList(AppContext appContext, final int id, final int pageIndex, final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.BLOGCOMMENT_LIST, new HashMap<String, Object>(){{
			put("id", id);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});
		
		try{
			return BlogCommentList.parse(http_get(appContext, newUrl));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鍙戣〃鍗氬璇勮
	 * @param blog 鍗氬id
	 * @param uid 鐧婚檰鐢ㄦ埛鐨剈id
	 * @param content 璇勮鍐呭
	 * @return
	 * @throws AppException
	 */
	public static Result pubBlogComment(AppContext appContext, int blog, int uid, String content) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("blog", blog);
		params.put("uid", uid);
		params.put("content", content);
		
		try{
			return http_post(appContext, URLs.BLOGCOMMENT_PUB, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
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
	public static Result replyBlogComment(AppContext appContext, int blog, int uid, String content, int reply_id, int objuid) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("blog", blog);
		params.put("uid", uid);
		params.put("content", content);
		params.put("reply_id", reply_id);
		params.put("objuid", objuid);
		
		try{
			return http_post(appContext, URLs.BLOGCOMMENT_PUB, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
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
	public static Result delBlogComment(AppContext appContext, int uid, int blogid, int replyid, int authorid, int owneruid) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("blogid", blogid);		
		params.put("replyid", replyid);
		params.put("authorid", authorid);
		params.put("owneruid", owneruid);

		try{
			return http_post(appContext, URLs.BLOGCOMMENT_DELETE, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鑾峰彇璇勮鍒楄〃
	 * @param catalog 1鏂伴椈  2甯栧瓙  3鍔ㄥ脊  4鍔ㄦ�
	 * @param id
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static CommentList getCommentList(AppContext appContext, final int catalog, final int id, final int pageIndex, final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.COMMENT_LIST, new HashMap<String, Object>(){{
			put("catalog", catalog);
			put("id", id);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});
		
		try{
			return CommentList.parse(http_get(appContext, newUrl));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
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
	public static Result pubComment(AppContext appContext, int catalog, int id, int uid, String content, int isPostToMyZone) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("catalog", catalog);
		params.put("id", id);
		params.put("uid", uid);
		params.put("content", content);
		params.put("isPostToMyZone", isPostToMyZone);
		
		try{
			return http_post(appContext, URLs.COMMENT_PUB, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
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
	public static Result replyComment(AppContext appContext, int id, int catalog, int replyid, int authorid, int uid, String content) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("catalog", catalog);
		params.put("id", id);
		params.put("uid", uid);
		params.put("content", content);
		params.put("replyid", replyid);
		params.put("authorid", authorid);
		
		try{
			return http_post(appContext, URLs.COMMENT_REPLY, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
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
	public static Result delComment(AppContext appContext, int id, int catalog, int replyid, int authorid) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("id", id);
		params.put("catalog", catalog);
		params.put("replyid", replyid);
		params.put("authorid", authorid);

		try{
			return http_post(appContext, URLs.COMMENT_DELETE, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鐢ㄦ埛鏀惰棌鍒楄〃
	 * @param uid 鐢ㄦ埛UID
	 * @param type 0:鍏ㄩ儴鏀惰棌 1:杞欢 2:璇濋 3:鍗氬 4:鏂伴椈 5:浠ｇ爜
	 * @param pageIndex 椤甸潰绱㈠紩 0琛ㄧず绗竴椤�
	 * @param pageSize 姣忛〉鐨勬暟閲�
	 * @return
	 * @throws AppException
	 */
	public static FavoriteList getFavoriteList(AppContext appContext, final int uid, final int type, final int pageIndex, final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.FAVORITE_LIST, new HashMap<String, Object>(){{
			put("uid", uid);
			put("type", type);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});
		
		try{
			return FavoriteList.parse(http_get(appContext, newUrl));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}	
	
	/**
	 * 鐢ㄦ埛娣诲姞鏀惰棌
	 * @param uid 鐢ㄦ埛UID
	 * @param objid 姣斿鏄柊闂籌D 鎴栬�闂瓟ID 鎴栬�鍔ㄥ脊ID
	 * @param type 1:杞欢 2:璇濋 3:鍗氬 4:鏂伴椈 5:浠ｇ爜
	 * @return
	 * @throws AppException
	 */
	public static Result addFavorite(AppContext appContext, int uid, int objid, int type) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("objid", objid);
		params.put("type", type);

		try{
			return http_post(appContext, URLs.FAVORITE_ADD, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 鐢ㄦ埛鍒犻櫎鏀惰棌
	 * @param uid 鐢ㄦ埛UID
	 * @param objid 姣斿鏄柊闂籌D 鎴栬�闂瓟ID 鎴栬�鍔ㄥ脊ID
	 * @param type 1:杞欢 2:璇濋 3:鍗氬 4:鏂伴椈 5:浠ｇ爜
	 * @return
	 * @throws AppException
	 */
	public static Result delFavorite(AppContext appContext, int uid, int objid, int type) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("objid", objid);
		params.put("type", type);

		try{
			return http_post(appContext, URLs.FAVORITE_DELETE, params, null);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
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
	public static SearchList getSearchList(AppContext appContext, String catalog, String content, int pageIndex, int pageSize) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("catalog", catalog);
		params.put("content", content);
		params.put("pageIndex", pageIndex);
		params.put("pageSize", pageSize);

		try{
			return SearchList.parse(_post(appContext, URLs.SEARCH_LIST, params, null));	
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 杞欢鍒楄〃
	 * @param searchTag 杞欢鍒嗙被  鎺ㄨ崘:recommend 鏈�柊:time 鐑棬:view 鍥戒骇:list_cn
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static SoftwareList getSoftwareList(AppContext appContext,final String searchTag,final int pageIndex,final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.SOFTWARE_LIST, new HashMap<String, Object>(){{
			put("searchTag", searchTag);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});

		try{
			return SoftwareList.parse(http_get(appContext, newUrl));	
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 杞欢鍒嗙被鐨勮蒋浠跺垪琛�
	 * @param searchTag 浠巗oftwarecatalog_list鑾峰彇鐨則ag
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static SoftwareList getSoftwareTagList(AppContext appContext,final int searchTag,final int pageIndex,final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.SOFTWARETAG_LIST, new HashMap<String, Object>(){{
			put("searchTag", searchTag);
			put("pageIndex", pageIndex);
			put("pageSize", pageSize);
		}});

		try{
			return SoftwareList.parse(http_get(appContext, newUrl));	
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 杞欢鍒嗙被鍒楄〃
	 * @param tag 绗竴绾�0  绗簩绾�tag
	 * @return
	 * @throws AppException
	 */
	public static SoftwareCatalogList getSoftwareCatalogList(AppContext appContext,final int tag) throws AppException {
		String newUrl = _MakeURL(URLs.SOFTWARECATALOG_LIST, new HashMap<String, Object>(){{
			put("tag", tag);
		}});

		try{
			return SoftwareCatalogList.parse(http_get(appContext, newUrl));	
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
}
