package net.oschina.app.ui;

import java.io.File;
import java.io.IOException;

import net.oschina.app.AppException;
import com.hkzhe.wwtt.R;
import net.oschina.app.api.ApiClient;
import net.oschina.app.common.FileUtils;
import net.oschina.app.common.ImageUtils;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

/**
 * 鍥剧墖缂╂斁瀵硅瘽妗�
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ImageZoomDialog extends Activity implements OnTouchListener, OnClickListener {

	// These matrices will be used to move and zoom image
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	PointF start = new PointF();
	PointF mid = new PointF();
	DisplayMetrics dm;
	float oldDist = 1f;
	private ImageView imgView;
	private Button zoomIn, zoomOut;
	private ViewSwitcher mViewSwitcher;
	
	// button zoom
	private float scaleWidth = 1;
	private float scaleHeight = 1;
	private Bitmap bitmap, zoomedBMP;
	private int zoom_level = 0;
	private static final double ZOOM_IN_SCALE = 1.25;// 鏀惧ぇ绯绘暟
	private static final double ZOOM_OUT_SCALE = 0.8;// 缂╁皬绯绘暟
	
    float minScaleR;// 鏈�皬缂╂斁姣斾緥
    static final float MAX_SCALE = 4f;// 鏈�ぇ缂╂斁姣斾緥
	
	// We can be in one of these 3 states
	static final int NONE = 0;// 鍒濆鐘舵�
	static final int DRAG = 1;// 鎷栧姩
	static final int ZOOM = 2;// 缂╂斁
	int mode = NONE;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_zoom_dialog);
		
		this.initView();        
        this.initData();
	}

	private void initView()
    {
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);// 鑾峰彇鍒嗚鲸鐜�
		
		//zoomIn = (Button) findViewById(R.id.zoom_in);// 鏀惧ぇ鎸夐挳
		//zoomOut = (Button) findViewById(R.id.zoom_out);// 缂╁皬鎸夐挳
		//zoomIn.setOnClickListener(this);
		//zoomOut.setOnClickListener(this);
		
		imgView = (ImageView) findViewById(R.id.imagezoomdialog_image);
		imgView.setOnTouchListener(this);// 璁剧疆瑙﹀睆鐩戝惉
		
		mViewSwitcher = (ViewSwitcher)findViewById(R.id.imagezoomdialog_view_switcher); 
    } 
	
    private void initData() 
    {
		final String imgURL = getIntent().getStringExtra("img_url");		
		final String ErrMsg = getString(R.string.msg_load_image_fail);
		final Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				if(msg.what==1 && msg.obj != null){
					bitmap = (Bitmap)msg.obj;
					imgView.setImageBitmap(bitmap);
					minZoom();// 璁＄畻鏈�皬缂╂斁姣�
					CheckView();// 璁剧疆鍥惧儚灞呬腑
					imgView.setImageMatrix(matrix);
					mViewSwitcher.showNext();
				}else{
					UIHelper.ToastMessage(ImageZoomDialog.this, ErrMsg);
					finish();
				}
			}
		};
		new Thread(){
			public void run() {
				Message msg = new Message();
				Bitmap bmp = null;
		    	String filename = FileUtils.getFileName(imgURL);
				try {
					//璇诲彇鏈湴鍥剧墖
					if(imgURL.endsWith("portrait.gif") || StringUtils.isEmpty(imgURL)){
						bmp = BitmapFactory.decodeResource(imgView.getResources(), R.drawable.widget_dface);
					}
					if(bmp == null){
						//鏄惁鏈夌紦瀛樺浘鐗�
				    	//Environment.getExternalStorageDirectory();杩斿洖/sdcard
				    	String filepath = getFilesDir() + File.separator + filename;
						File file = new File(filepath);
						if(file.exists()){
							bmp = ImageUtils.getBitmap(imgView.getContext(), filename);
				    	}
					}
					if(bmp == null){
						bmp = ApiClient.getNetBitmap(imgURL);
						if(bmp != null){
							try {
		                    	//鍐欏浘鐗囩紦瀛�
								ImageUtils.saveImage(imgView.getContext(), filename, bmp);
							} catch (IOException e) {
								e.printStackTrace();
							}
							//缂╂斁鍥剧墖
							bmp = ImageUtils.reDrawBitMap(ImageZoomDialog.this, bmp);
						}
					}
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
	
	public boolean onTouch(View v, MotionEvent event) {
		// Handle touch events here...
		ImageView imgView = (ImageView) v;

		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		// 璁剧疆鎷栨媺妯″紡(涓荤偣)
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			//Log.d(TAG, "mode=DRAG");
			mode = DRAG;
			break;
		// 璁剧疆澶氱偣瑙︽懜妯″紡(鍓偣)
		case MotionEvent.ACTION_POINTER_DOWN:
				oldDist = spacing(event);
				//Log.d(TAG, "oldDist=" + oldDist);
				if (oldDist > 10f) {
					savedMatrix.set(matrix);
					midPoint(mid, event);
					mode = ZOOM;
					//Log.d(TAG, "mode=ZOOM");
				}
				break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			//Log.d(TAG, "mode=NONE");
			break;
		// 鑻ヤ负DRAG妯″紡锛屽垯鐐瑰嚮绉诲姩鍥剧墖
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				matrix.set(savedMatrix);
				// 璁剧疆浣嶇Щ
				matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
			}
			// 鑻ヤ负ZOOM妯″紡锛屽垯澶氱偣瑙︽懜缂╂斁
			else if (mode == ZOOM) {
				float newDist = spacing(event);
				//Log.d(TAG, "newDist=" + newDist);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					// 璁剧疆缂╂斁姣斾緥鍜屽浘鐗囦腑鐐逛綅缃�
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
			}
			break;
		}

		// Perform the transformation
		imgView.setImageMatrix(matrix);
		CheckView();
		return true; // indicate event was handled
	}

    /**
     * 闄愬埗鏈�ぇ鏈�皬缂╂斁姣斾緥锛岃嚜鍔ㄥ眳涓�
     */
    private void CheckView() {
        float p[] = new float[9];
        matrix.getValues(p);
        if (mode == ZOOM) {
            if (p[0] < minScaleR) {
                matrix.setScale(minScaleR, minScaleR);
            }
            if (p[0] > MAX_SCALE) {
                matrix.set(savedMatrix);
            }
        }
        center();
    }

    /**
     * 鏈�皬缂╂斁姣斾緥锛屾渶澶т负100%
     */
    private void minZoom() {
//        minScaleR = Math.min(
//                (float) dm.widthPixels / (float) bitmap.getWidth(),
//                (float) dm.heightPixels / (float) bitmap.getHeight());
        if(bitmap.getWidth() >= dm.widthPixels)
        	minScaleR = ((float) dm.widthPixels) / bitmap.getWidth();
    	else
    		minScaleR = 1.0f;
        
        if (minScaleR < 1.0) {
            matrix.postScale(minScaleR, minScaleR);
        }
    }

    private void center() {
        center(true, true);
    }

    /**
     * 妯悜銆佺旱鍚戝眳涓�
     */
    protected void center(boolean horizontal, boolean vertical) {
        Matrix m = new Matrix();
        m.set(matrix);
        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        m.mapRect(rect);

        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;

        if (vertical) {
            // 鍥剧墖灏忎簬灞忓箷澶у皬锛屽垯灞呬腑鏄剧ず銆傚ぇ浜庡睆骞曪紝涓婃柟鐣欑┖鍒欏線涓婄Щ锛屼笅鏂圭暀绌哄垯寰�笅绉�
            int screenHeight = dm.heightPixels;
            if (height < screenHeight) {
                deltaY = (screenHeight - height) / 2 - rect.top;
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < screenHeight) {
                deltaY = imgView.getHeight() - rect.bottom;
            }
        }

        if (horizontal) {
            int screenWidth = dm.widthPixels;
            if (width < screenWidth) {
                deltaX = (screenWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < screenWidth) {
                deltaX = screenWidth - rect.right;
            }
        }
        matrix.postTranslate(deltaX, deltaY);
    }
	
	// 璁＄畻绉诲姩璺濈
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	// 璁＄畻涓偣浣嶇疆
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	// 鏀惧ぇ锛岀缉灏忔寜閽偣鍑讳簨浠�
	//@Override
	public void onClick(View v) {
		if (v == zoomIn) {
			enlarge();
		} else if (v == zoomOut) {
			small();
		}
	}

	// 鎸夐挳鐐瑰嚮缂╁皬鍑芥暟
	private void small() {
		int bmpWidth = bitmap.getWidth();
		int bmpHeight = bitmap.getHeight();

		scaleWidth = (float) (scaleWidth * ZOOM_OUT_SCALE);
		scaleHeight = (float) (scaleHeight * ZOOM_OUT_SCALE);

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		zoomedBMP = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix,
				true);
		imgView.setImageBitmap(zoomedBMP);
	}

	// 鎸夐挳鐐瑰嚮鏀惧ぇ鍑芥暟
	private void enlarge() {
		try {
			int bmpWidth = bitmap.getWidth();
			int bmpHeight = bitmap.getHeight();

			scaleWidth = (float) (scaleWidth * ZOOM_IN_SCALE);
			scaleHeight = (float) (scaleHeight * ZOOM_IN_SCALE);

			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			zoomedBMP = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight,
					matrix, true);
			imgView.setImageBitmap(zoomedBMP);
		} catch (Exception e) {
			// can't zoom because of memory issue, just ignore, no big deal
		}
	}
}