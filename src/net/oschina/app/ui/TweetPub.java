package net.oschina.app.ui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import com.hkzhe.wwtt.R;
import net.oschina.app.adapter.GridViewFaceAdapter;
import net.oschina.app.bean.Result;
import net.oschina.app.bean.Tweet;
import net.oschina.app.common.FileUtils;
import net.oschina.app.common.ImageUtils;
import net.oschina.app.common.MediaUtils;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 閸欐垼銆冮崝銊ヨ剨
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class TweetPub extends Activity{

	private FrameLayout mForm;
	private ImageView mBack;
	private EditText mContent;
	private Button mPublish;
	private ImageView mFace;
	private ImageView mPick;
	private ImageView mAtme;
	private ImageView mSoftware;
	private ImageView mImage;
	private LinearLayout mClearwords;
	private TextView mNumberwords;

	private GridView mGridView;
	private GridViewFaceAdapter mGVFaceAdapter;
	
	private Tweet tweet;
	private File imgFile;
	private String theLarge;
	private String theThumbnail;
	private InputMethodManager imm;
	
	private String tempTweetKey = AppConfig.TEMP_TWEET;
	private String tempTweetImageKey = AppConfig.TEMP_TWEET_IMAGE;
	
	public static LinearLayout mMessage;
	public static Context mContext;
	
	private static final int MAX_TEXT_LENGTH = 160;//閺堬拷銇囨潏鎾冲弳鐎涙鏆�
	private static final String TEXT_ATME = "@鐠囩柉绶崗銉ф暏閹村嘲鎮�";
	private static final String TEXT_SOFTWARE = "#鐠囩柉绶崗銉ㄨ拫娴犺泛鎮�";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tweet_pub);
		
		mContext = this;
		//鏉烆垶鏁惄妯碱吀閻炲棛琚�
		imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		
		//閸掓繂顬婇崠鏍х唨閺堫剝顬呴崶锟�		this.initView();
		//閸掓繂顬婇崠鏍�閹懓顬呴崶锟�		this.initGridView();
	}
	
    @Override
	protected void onDestroy() {
    	mContext = null;
    	super.onDestroy();
	}
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if(mGridView.getVisibility() == View.VISIBLE){
    		//闂呮劘妫岀悰銊﹀剰
    		hideFace();
    	}
    }
    
    @Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(keyCode == KeyEvent.KEYCODE_BACK) {
    		if(mGridView.getVisibility() == View.VISIBLE) {
    			//闂呮劘妫岀悰銊﹀剰
    			hideFace();
    		}else{
    			return super.onKeyDown(keyCode, event);
    		}
    	}
    	return true;
    }
    
	//閸掓繂顬婇崠鏍瀰閸ョ偓甯舵禒锟�  
    private void initView()
    {    	
    	mForm = (FrameLayout)findViewById(R.id.tweet_pub_form);
    	mBack = (ImageView)findViewById(R.id.tweet_pub_back);
    	mMessage = (LinearLayout)findViewById(R.id.tweet_pub_message);
    	mImage = (ImageView)findViewById(R.id.tweet_pub_image);
    	mPublish = (Button)findViewById(R.id.tweet_pub_publish);
    	mContent = (EditText)findViewById(R.id.tweet_pub_content);
    	mFace = (ImageView)findViewById(R.id.tweet_pub_footbar_face);
    	mPick = (ImageView)findViewById(R.id.tweet_pub_footbar_photo);
    	mAtme = (ImageView)findViewById(R.id.tweet_pub_footbar_atme);
    	mSoftware = (ImageView)findViewById(R.id.tweet_pub_footbar_software);
    	mClearwords = (LinearLayout)findViewById(R.id.tweet_pub_clearwords);
    	mNumberwords = (TextView)findViewById(R.id.tweet_pub_numberwords);
    	
    	mBack.setOnClickListener(UIHelper.finish(this));
    	mPublish.setOnClickListener(publishClickListener);
    	mImage.setOnLongClickListener(imageLongClickListener);
    	mFace.setOnClickListener(faceClickListener);
    	mPick.setOnClickListener(pickClickListener);
    	mAtme.setOnClickListener(atmeClickListener);
    	mSoftware.setOnClickListener(softwareClickListener);
    	mClearwords.setOnClickListener(clearwordsClickListener);
    	
    	//@閺屾劒姹�
    	String atme = getIntent().getStringExtra("at_me");
    	int atuid = getIntent().getIntExtra("at_uid",0);
    	if(atuid > 0){
    		tempTweetKey = AppConfig.TEMP_TWEET + "_" + atuid;
    		tempTweetImageKey = AppConfig.TEMP_TWEET_IMAGE + "_" + atuid;
    	}
    	
    	//缂傛牞绶崳銊﹀潑閸旂姵鏋冮張顒傛磧閸氾拷
    	mContent.addTextChangedListener(new TextWatcher() {		
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//娣囨繂鐡ㄨぐ鎾冲EditText濮濓絽婀紓鏍帆閻ㄥ嫬鍞寸�锟�			
				((AppContext)getApplication()).setProperty(tempTweetKey, s.toString());
				//閺勫墽銇氶崜鈺�稇閸欘垵绶崗銉ф畱鐎涙鏆�
				mNumberwords.setText((MAX_TEXT_LENGTH - s.length()) + "");
			}		
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}		
			public void afterTextChanged(Editable s) {}
		});
    	//缂傛牞绶崳銊у仯閸戣绨ㄦ禒锟�    	
    	mContent.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//閺勫墽銇氭潪顖炴暛閻╋拷
				showIMM();
			}
		});
    	//鐠佸墽鐤嗛張锟姐亣鏉堟挸鍙嗙�妤佹殶
    	InputFilter[] filters = new InputFilter[1];  
    	filters[0] = new InputFilter.LengthFilter(MAX_TEXT_LENGTH);
    	mContent.setFilters(filters);
    	
    	//閺勫墽銇氭稉瀛樻缂傛牞绶崘鍛啇
		UIHelper.showTempEditContent(this, mContent, tempTweetKey);
		//閺勫墽銇氭稉瀛樻娣囨繂鐡ㄩ崶鍓у
		String tempImage = ((AppContext)getApplication()).getProperty(tempTweetImageKey);
		if(!StringUtils.isEmpty(tempImage)) {
    		Bitmap bitmap = ImageUtils.loadImgThumbnail(tempImage, 100, 100);
    		if(bitmap != null) {
    			imgFile = new File(tempImage);
				mImage.setImageBitmap(bitmap);
				mImage.setVisibility(View.VISIBLE);
			}
		}
		
		if(atuid > 0 && mContent.getText().length() == 0){
			mContent.setText(atme);
    		mContent.setSelection(atme.length());//鐠佸墽鐤嗛崗澶嬬垼娴ｅ秶鐤�
		}
    }
    
    //閸掓繂顬婇崠鏍�閹懏甯舵禒锟�  
    private void initGridView() {
    	mGVFaceAdapter = new GridViewFaceAdapter(this);
    	mGridView = (GridView)findViewById(R.id.tweet_pub_faces);
    	mGridView.setAdapter(mGVFaceAdapter);
    	mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//閹绘帒鍙嗛惃鍕�閹拷
				SpannableString ss = new SpannableString(view.getTag().toString());
				Drawable d = getResources().getDrawable((int)mGVFaceAdapter.getItemId(position));
				d.setBounds(0, 0, 35, 35);//鐠佸墽鐤嗙悰銊﹀剰閸ュ墽澧栭惃鍕▔缁�搫銇囩亸锟�			
				ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
				ss.setSpan(span, 0, view.getTag().toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);				 
				//閸︺劌鍘滈弽鍥ㄥ閸︺劌顦甸幓鎺戝弳鐞涖劍鍎�
				mContent.getText().insert(mContent.getSelectionStart(), ss);				
			}    		
    	});
    }
    
    private void showIMM() {
    	mFace.setTag(1);
    	showOrHideIMM();
    }
    private void showFace() {
		mFace.setImageResource(R.drawable.widget_bar_keyboard);
		mFace.setTag(1);
		mGridView.setVisibility(View.VISIBLE);
    }
    private void hideFace() {
    	mFace.setImageResource(R.drawable.widget_bar_face);
		mFace.setTag(null);
		mGridView.setVisibility(View.GONE);
    }
    private void showOrHideIMM() {
    	if(mFace.getTag() == null){
			//闂呮劘妫屾潪顖炴暛閻╋拷
			imm.hideSoftInputFromWindow(mFace.getWindowToken(), 0);
			//閺勫墽銇氱悰銊﹀剰
			showFace();				
		}else{
			//閺勫墽銇氭潪顖炴暛閻╋拷
			imm.showSoftInput(mContent, 0);
			//闂呮劘妫岀悰銊﹀剰
			hideFace();
		}
    }
    
    private View.OnClickListener faceClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			showOrHideIMM();
		}
	};
    
	private View.OnClickListener pickClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			//闂呮劘妫屾潪顖炴暛閻╋拷
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
			//闂呮劘妫岀悰銊﹀剰
			hideFace();		
			
			CharSequence[] items = {
					TweetPub.this.getString(R.string.img_from_album),
					TweetPub.this.getString(R.string.img_from_camera)
			};
			imageChooseItem(items);
		}
	};
	
	private View.OnClickListener atmeClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			//閺勫墽銇氭潪顖炴暛閻╋拷
			showIMM();			
				
    		//閸︺劌鍘滈弽鍥ㄥ閸︺劌顦甸幓鎺戝弳閳ユ穽閻劍鍩涢崥宥侊拷
			int curTextLength = mContent.getText().length();
			if(curTextLength < MAX_TEXT_LENGTH) {
				String atme = TEXT_ATME;
				int start,end;
				if((MAX_TEXT_LENGTH - curTextLength) >= atme.length()) {
					start = mContent.getSelectionStart() + 1;
					end = start + atme.length() - 2;
				} else {
					int num = MAX_TEXT_LENGTH - curTextLength;
					if(num < atme.length()) {
						atme = atme.substring(0, num);
					}
					start = mContent.getSelectionStart() + 1;
					end = start + atme.length() - 1;
				}
				if(start > MAX_TEXT_LENGTH || end > MAX_TEXT_LENGTH) {
					start = MAX_TEXT_LENGTH;
					end = MAX_TEXT_LENGTH;
				}
				mContent.getText().insert(mContent.getSelectionStart(), atme);
				mContent.setSelection(start, end);//鐠佸墽鐤嗛柅澶夎厬閺傚洤鐡�
			}
		}
	};
	
	private View.OnClickListener softwareClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			//閺勫墽銇氭潪顖炴暛閻╋拷
			showIMM();
			
			//閸︺劌鍘滈弽鍥ㄥ閸︺劌顦甸幓鎺戝弳閳ワ拷鏉烆垯娆㈤崥锟介垾锟�			
			int curTextLength = mContent.getText().length();
			if(curTextLength < MAX_TEXT_LENGTH) {
				String software = TEXT_SOFTWARE;
				int start,end;
				if((MAX_TEXT_LENGTH - curTextLength) >= software.length()) {
					start = mContent.getSelectionStart() + 1;
					end = start + software.length() - 2;
				} else {
					int num = MAX_TEXT_LENGTH - curTextLength;
					if(num < software.length()) {
						software = software.substring(0, num);
					}					
					start = mContent.getSelectionStart() + 1;
					end = start + software.length() - 1;
				}
				if(start > MAX_TEXT_LENGTH || end > MAX_TEXT_LENGTH) {
					start = MAX_TEXT_LENGTH;
					end = MAX_TEXT_LENGTH;
				}
				mContent.getText().insert(mContent.getSelectionStart(), software);
	    		mContent.setSelection(start, end);//鐠佸墽鐤嗛柅澶夎厬閺傚洤鐡�
			}
		}
	};
	
	private View.OnClickListener clearwordsClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			String content = mContent.getText().toString();
			if(!StringUtils.isEmpty(content)){
				UIHelper.showClearWordsDialog(v.getContext(), mContent, mNumberwords);
			}
		}
	};
	
	private View.OnLongClickListener imageLongClickListener = new View.OnLongClickListener() {
		public boolean onLongClick(View v) {
			//闂呮劘妫屾潪顖炴暛閻╋拷
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
			
			new AlertDialog.Builder(v.getContext())
			.setIcon(android.R.drawable.ic_dialog_info)
			.setTitle(getString(R.string.delete_image))
			.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//濞撳懘娅庢稊瀣娣囨繂鐡ㄩ惃鍕椽鏉堟垵娴橀悧锟�					((AppContext)getApplication()).removeProperty(tempTweetImageKey);
					
					imgFile = null;
					mImage.setVisibility(View.GONE);
					dialog.dismiss();
				}
			})
			.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.create().show();
			return true;
		}
		
	};
	
	/**
	 * 閹垮秳缍旈柅澶嬪
	 * @param items
	 */
	public void imageChooseItem(CharSequence[] items )
	{
		AlertDialog imageDialog = new AlertDialog.Builder(this).setTitle(R.string.ui_insert_image).setIcon(android.R.drawable.btn_star).setItems(items,
			new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int item)
				{
					//閹靛婧�柅澶婃禈
					if( item == 0 )
					{
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
						intent.addCategory(Intent.CATEGORY_OPENABLE); 
						intent.setType("image/*"); 
						startActivityForResult(Intent.createChooser(intent, "闁瀚ㄩ崶鍓у"),ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD); 
					}
					//閹峰秶鍙�
					else if( item == 1 )
					{	
						String savePath = "";
						//閸掋倖鏌囬弰顖氭儊閹稿倽娴囨禍鍝燚閸楋拷
						String storageState = Environment.getExternalStorageState();		
						if(storageState.equals(Environment.MEDIA_MOUNTED)){
							savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OSChina/Camera/";//鐎涙ɑ鏂侀悡褏澧栭惃鍕瀮娴犺泛銇�
							File savedir = new File(savePath);
							if (!savedir.exists()) {
								savedir.mkdirs();
							}
						}
						
						//濞屸剝婀侀幐鍌濇祰SD閸椻槄绱濋弮鐘崇《娣囨繂鐡ㄩ弬鍥︽
						if(StringUtils.isEmpty(savePath)){
							UIHelper.ToastMessage(TweetPub.this, "閺冪姵纭舵穱婵嗙摠閻撗呭閿涘矁顕Λ锟界叀SD閸椻剝妲搁崥锔藉瘯鏉烇拷");
							return;
						}

						String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
						String fileName = "osc_" + timeStamp + ".jpg";//閻撗呭閸涜棄鎮�
						File out = new File(savePath, fileName);
						Uri uri = Uri.fromFile(out);
						
						theLarge = savePath + fileName;//鐠囥儳鍙庨悧鍥╂畱缂佹繂顕捄顖氱窞
						
						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
						startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
					}   
				}}).create();
		
		 imageDialog.show();
	}
	
	@Override 
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
	{ 
    	if(resultCode != RESULT_OK) return;
		
		final Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				if(msg.what == 1 && msg.obj != null){
					//閺勫墽銇氶崶鍓у
					mImage.setImageBitmap((Bitmap)msg.obj);
					mImage.setVisibility(View.VISIBLE);
				}
			}
		};
		
		new Thread(){
			public void run() 
			{
				Bitmap bitmap = null;
				
		        if(requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD) 
		        {         	
		        	if(data == null)  return;
		        	
		        	Uri thisUri = data.getData();
		        	String thePath = ImageUtils.getAbsolutePathFromNoStandardUri(thisUri);
		        	
		        	//婵″倹鐏夐弰顖涚垼閸戝摙ri
		        	if(StringUtils.isEmpty(thePath))
		        	{
		        		theLarge = ImageUtils.getAbsoluteImagePath(TweetPub.this,thisUri);
		        	}
		        	else
		        	{
		        		theLarge = thePath;
		        	}
		        	
		        	String attFormat = FileUtils.getFileFormat(theLarge);
		        	if(!"photo".equals(MediaUtils.getContentType(attFormat)))
		        	{
		        		Toast.makeText(TweetPub.this, getString(R.string.choose_image), Toast.LENGTH_SHORT).show();
		        		return;
		        	}
		        	
		        	//閼惧嘲褰囬崶鍓у缂傗晝鏆愰崶锟介崣顏呮箒Android2.1娴犮儰绗傞悧鍫熸拱閺�垱瀵�
		    		if(AppContext.isMethodsCompat(android.os.Build.VERSION_CODES.ECLAIR_MR1)){
		    			String imgName = FileUtils.getFileName(theLarge);
		    			bitmap = ImageUtils.loadImgThumbnail(TweetPub.this, imgName, MediaStore.Images.Thumbnails.MICRO_KIND);
		    		}
		        	
		        	if(bitmap == null && !StringUtils.isEmpty(theLarge))
		        	{
		        		bitmap = ImageUtils.loadImgThumbnail(theLarge, 100, 100);
		        	}
		        }
		        //閹峰秵鎲氶崶鍓у
		        else if(requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA)
		        {	
		        	if(bitmap == null && !StringUtils.isEmpty(theLarge))
		        	{
		        		bitmap = ImageUtils.loadImgThumbnail(theLarge, 100, 100);
		        	}
		        }
		        
				if(bitmap!=null)
				{
					//鐎涙ɑ鏂侀悡褏澧栭惃鍕瀮娴犺泛銇�
					String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OSChina/Camera/";
					File savedir = new File(savePath);
					if (!savedir.exists()) {
						savedir.mkdirs();
					}
					
					String largeFileName = FileUtils.getFileName(theLarge);
					String largeFilePath = savePath + largeFileName;
					//閸掋倖鏌囬弰顖氭儊瀹告彃鐡ㄩ崷銊х級閻ｃ儱娴�
					if(largeFileName.startsWith("thumb_") && new File(largeFilePath).exists()) 
					{
						theThumbnail = largeFilePath;
						imgFile = new File(theThumbnail);
					} 
					else 
					{
						//閻㈢喐鍨氭稉濠佺炊閻拷00鐎硅棄瀹抽崶鍓у
						String thumbFileName = "thumb_" + largeFileName;
						theThumbnail = savePath + thumbFileName;
						if(new File(theThumbnail).exists())
						{
							imgFile = new File(theThumbnail);
						}
						else
						{
							try {
								//閸樺缂夋稉濠佺炊閻ㄥ嫬娴橀悧锟�					
								ImageUtils.createImageThumbnail(TweetPub.this, theLarge, theThumbnail, 800, 80);
								imgFile = new File(theThumbnail);
							} catch (IOException e) {
								e.printStackTrace();
							}	
						}						
					}					
					//娣囨繂鐡ㄩ崝銊ヨ剨娑撳瓨妞傞崶鍓у
					((AppContext)getApplication()).setProperty(tempTweetImageKey, theThumbnail);
					
					Message msg = new Message();
					msg.what = 1;
					msg.obj = bitmap;
					handler.sendMessage(msg);
				}				
			};
		}.start();
    }
	
	private View.OnClickListener publishClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			//闂呮劘妫屾潪顖炴暛閻╋拷
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
			
			String content = mContent.getText().toString();
			if(StringUtils.isEmpty(content)){
				UIHelper.ToastMessage(v.getContext(), "鐠囩柉绶崗銉ュЗ瀵懓鍞寸�锟�");
				return;
			}
			
			final AppContext ac = (AppContext)getApplication();
			if(!ac.isLogin()){
				UIHelper.showLoginDialog(TweetPub.this);
				return;
			}	
			
			mMessage.setVisibility(View.VISIBLE);
			mForm.setVisibility(View.GONE);
			
			tweet = new Tweet();
			tweet.setAuthorId(ac.getLoginUid());
			tweet.setBody(content);
			tweet.setImageFile(imgFile);
			
			final Handler handler = new Handler(){
				public void handleMessage(Message msg) {
					mMessage.setVisibility(View.GONE);
					if(msg.what == 1){
						//濞撳懘娅庢稊瀣娣囨繂鐡ㄩ惃鍕椽鏉堟垵鍞寸�锟�						ac.removeProperty(tempTweetKey,tempTweetImageKey);						
						finish();
					}else{
						mMessage.setVisibility(View.GONE);
						mForm.setVisibility(View.VISIBLE);
					}
				}				
			};
			
			new Thread(){
				public void run() {
					Message msg =new Message();
					Result res = null;
					int what = 0;
					try {
						res = ac.pubTweet(tweet);
						what = 1;
						msg.what = 1;
						msg.obj = res;
		            } catch (AppException e) {
		            	e.printStackTrace();
						msg.what = -1;
						msg.obj = e;
		            }
					handler.sendMessage(msg);
					UIHelper.sendBroadCastTweet(TweetPub.this, what, res, tweet);
				}
			}.start();
		}
	};
}
