package net.oschina.app.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.oschina.app.AppException;
import net.oschina.app.common.StringUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * 新闻实体类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class News extends Entity{
	
	public final static String NODE_ID = "id";
	public final static String NODE_TITLE = "title";
	public final static String NODE_URL = "url";
	public final static String NODE_BODY = "body";
	public final static String NODE_AUTHORID = "authorid";
	public final static String NODE_AUTHOR = "author";
	public final static String NODE_PUBDATE = "pubDate";
	public final static String NODE_COMMENTCOUNT = "commentCount";
	public final static String NODE_FAVORITE = "favorite";
	public final static String NODE_START = "news";
	
	public final static String NODE_SOFTWARELINK = "softwarelink";
	public final static String NODE_SOFTWARENAME = "softwarename";
	
	public final static String NODE_NEWSTYPE = "newstype";
	public final static String NODE_TYPE = "type";
	public final static String NODE_ATTACHMENT = "attachment";
	public final static String NODE_AUTHORUID2 = "authoruid2";
	
	public final static int NEWSTYPE_NEWS = 0x00;//0 新闻
	public final static int NEWSTYPE_SOFTWARE = 0x01;//1 软件
	public final static int NEWSTYPE_POST = 0x02;//2 帖子
	public final static int NEWSTYPE_BLOG = 0x03;//3 博客

	private String title;
	private String url;
	private String body;
	private String author;
	private int authorId;
	private int commentCount;
	private String pubDate;
	private String softwareLink;
	private String softwareName;
	private int favorite;
	private NewsType newType;
	private List<Relative> relatives;

	public News(){
		this.newType = new NewsType();
		this.relatives = new ArrayList<Relative>();
	}	
	
	public class NewsType implements Serializable{
		public int type;
		public String attachment;
		public int authoruid2;
	} 
	
	public static class Relative implements Serializable{
		public String title;
		public String url;
	} 
	
	public List<Relative> getRelatives() {
		return relatives;
	}
	public void setRelatives(List<Relative> relatives) {
		this.relatives = relatives;
	}
	public NewsType getNewType() {
		return newType;
	}
	public void setNewType(NewsType newType) {
		this.newType = newType;
	}	
	public int getFavorite() {
		return favorite;
	}
	public void setFavorite(int favorite) {
		this.favorite = favorite;
	}
	public String getSoftwareLink() {
		return softwareLink;
	}
	public void setSoftwareLink(String softwareLink) {
		this.softwareLink = softwareLink;
	}
	public String getSoftwareName() {
		return softwareName;
	}
	public void setSoftwareName(String softwareName) {
		this.softwareName = softwareName;
	}
	public String getPubDate() {
		return this.pubDate;
	}
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public int getAuthorId() {
		return authorId;
	}
	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}
	public int getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	
	public static News parse(InputStream inputStream) throws IOException, AppException {
		News news = null;
		Relative relative = null;
        //鑾峰緱XmlPullParser瑙ｆ瀽鍣�
        XmlPullParser xmlParser = Xml.newPullParser();
        try {        	
            xmlParser.setInput(inputStream, UTF8);
            //鑾峰緱瑙ｆ瀽鍒扮殑浜嬩欢绫诲埆锛岃繖閲屾湁寮�鏂囨。锛岀粨鏉熸枃妗ｏ紝寮�鏍囩锛岀粨鏉熸爣绛撅紝鏂囨湰绛夌瓑浜嬩欢銆�
            int evtType=xmlParser.getEventType();
			//涓�洿寰幆锛岀洿鍒版枃妗ｇ粨鏉�   
			while(evtType!=XmlPullParser.END_DOCUMENT){ 
	    		String tag = xmlParser.getName(); 
			    switch(evtType){ 
			    	case XmlPullParser.START_TAG:
			    		if(tag.equalsIgnoreCase(NODE_START))
			    		{
			    			news = new News();
			    		}
			    		else if(news != null)
			    		{	
				            if(tag.equalsIgnoreCase(NODE_ID))
				            {			      
				            	news.id = StringUtils.toInt(xmlParser.nextText(),0);
				            }
				            else if(tag.equalsIgnoreCase(NODE_TITLE))
				            {			            	
				            	news.setTitle(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(NODE_URL))
				            {			            	
				            	news.setUrl(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(NODE_BODY))
				            {			            	
				            	news.setBody(xmlParser.nextText());
				            }
				            else if(tag.equalsIgnoreCase(NODE_AUTHOR))
				            {			            	
				            	news.setAuthor(xmlParser.nextText());		            	
				            }
				            else if(tag.equalsIgnoreCase(NODE_AUTHORID))
				            {			            	
				            	news.setAuthorId(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
				            else if(tag.equalsIgnoreCase(NODE_COMMENTCOUNT))
				            {			            	
				            	news.setCommentCount(StringUtils.toInt(xmlParser.nextText(),0));			            	
				            }
				            else if(tag.equalsIgnoreCase(NODE_PUBDATE))
				            {			            	
				            	news.setPubDate(xmlParser.nextText());      	
				            }	
				            else if(tag.equalsIgnoreCase(NODE_SOFTWARELINK))
				            {			            	
				            	news.setSoftwareLink(xmlParser.nextText());			            	
				            }	
				            else if(tag.equalsIgnoreCase(NODE_SOFTWARENAME))
				            {			            	
				            	news.setSoftwareName(xmlParser.nextText());			            	
				            }	
				            else if(tag.equalsIgnoreCase(NODE_FAVORITE))
				            {			            	
				            	news.setFavorite(StringUtils.toInt(xmlParser.nextText(),0));		            	
				            }
				            else if(tag.equalsIgnoreCase(NODE_TYPE))
				            {	
				            	news.getNewType().type = StringUtils.toInt(xmlParser.nextText(),0); 
				            }
				            else if(tag.equalsIgnoreCase(NODE_ATTACHMENT))
				            {			            	
				            	news.getNewType().attachment = xmlParser.nextText(); 	
				            }
				            else if(tag.equalsIgnoreCase(NODE_AUTHORUID2))
				            {			            	
				            	news.getNewType().authoruid2 = StringUtils.toInt(xmlParser.nextText(),0); 
				            }
				            else if(tag.equalsIgnoreCase("relative"))
				            {			            	
				            	relative = new Relative();
				            }
				            else if(relative != null)
				            {			            	
				            	if(tag.equalsIgnoreCase("rtitle"))
				            	{
				            		relative.title = xmlParser.nextText(); 	
				            	}
				            	else if(tag.equalsIgnoreCase("rurl"))
				            	{
				            		relative.url = xmlParser.nextText(); 	
				            	}
				            }
				            //閫氱煡淇℃伅
				            else if(tag.equalsIgnoreCase("notice"))
				    		{
				            	news.setNotice(new Notice());
				    		}
				            else if(news.getNotice() != null)
				    		{
				    			if(tag.equalsIgnoreCase("atmeCount"))
					            {			      
				    				news.getNotice().setAtmeCount(StringUtils.toInt(xmlParser.nextText(),0));
					            }
					            else if(tag.equalsIgnoreCase("msgCount"))
					            {			            	
					            	news.getNotice().setMsgCount(StringUtils.toInt(xmlParser.nextText(),0));
					            }
					            else if(tag.equalsIgnoreCase("reviewCount"))
					            {			            	
					            	news.getNotice().setReviewCount(StringUtils.toInt(xmlParser.nextText(),0));
					            }
					            else if(tag.equalsIgnoreCase("newFansCount"))
					            {			            	
					            	news.getNotice().setNewFansCount(StringUtils.toInt(xmlParser.nextText(),0));
					            }
				    		}
			    		}
			    		break;
			    	case XmlPullParser.END_TAG:		
			    		//濡傛灉閬囧埌鏍囩缁撴潫锛屽垯鎶婂璞℃坊鍔犺繘闆嗗悎涓�
				       	if (tag.equalsIgnoreCase("relative") && news!=null && relative!=null) { 
				       		news.getRelatives().add(relative);
				       		relative = null; 
				       	}
				       	break; 
			    }
			    //濡傛灉xml娌℃湁缁撴潫锛屽垯瀵艰埅鍒颁笅涓�釜鑺傜偣
			    evtType=xmlParser.next();
			}		
        } catch (XmlPullParserException e) {
			throw AppException.xml(e);
        } finally {
        	inputStream.close();	
        }      
        return news;       
	}
}
