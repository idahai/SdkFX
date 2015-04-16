package sdk.fx;

import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

@SuppressLint("HandlerLeak")
public class ShowView {
	public static boolean removed = false;
	public static boolean isShowing = false;
	public static ShowView mInstance;
	private static Context mContext;
	public static Handler sendAutoMsgHandler;
	public static Runnable mViewPagerRunable;
	private int mScreenX;
	private int mScreenY;
	public static WindowManager  mWndMgr;
	public  WindowManager.LayoutParams mImgBackGroudLayout;
	public  WindowManager.LayoutParams mCloseBtnLayout;
	private static int mCurrentPicIndex = 0;
	public static ImageView mPictureView;
	public static ImageView mCloseiconView;
	private DisplayMetrics outMetrics;
	public static Handler mReceiveHeartBeatMsgHandler;
	private static long lastClickTime = 0L;
	private long currentClickTime = 0L;
	///////////////////////////////////////////////////////////////////////////
	
	
	public static ShowView getShowViewInstance(){
		if(mInstance == null)
			mInstance = new ShowView();
		return mInstance;
	}
	public void inintMsgHandler(){
		sendAutoMsgHandler = new Handler();
		mReceiveHeartBeatMsgHandler = new Handler(){
			public void handleMessage(Message msg){
				if(msg.what == GlobalDatas.MSG_START_SHOW){
					Log.d(GlobalDatas.__TAG__,"recevied show picture message.");
					mContext = (Context) msg.obj;
					boolean bshow =detectWhetherNeedShow(mContext);
					Log.d(GlobalDatas.__TAG__,"bshow:"+bshow);
					if(bshow==true && isShowing == false){
						StartingAutoScollImage();
					}
				}
				
				if(msg.what == GlobalDatas.MSG_NEW_APP_START){
					Log.d(GlobalDatas.__TAG__,"recevied new application start message.");
					mContext = (Context) msg.obj;
					boolean bshow =detectWhetherNeedShow(mContext);
					if(bshow==true && isShowing == false){
						StartingAutoScollImage();
					}
				}
			}
		};
	}

	public void StartingAutoScollImage() {
		initLayoutParams();
		setViews();
		mViewPagerRunable = new Runnable() {
			public void run() {
				if (mCurrentPicIndex + 1 >= 3) {
					mCurrentPicIndex = 0;
				} else {
					mCurrentPicIndex = mCurrentPicIndex + 1;
				}
				mPictureView.setScaleType(ScaleType.FIT_XY);
				mPictureView.setImageBitmap(GlobalDatas.gBitmapList.get(mCurrentPicIndex));
				sendAutoMsgHandler.postDelayed(mViewPagerRunable, 3000);
			}
		};
		sendAutoMsgHandler.postDelayed(mViewPagerRunable, 3000);
		isShowing = true;
	}


	private int getCurrentScreenStatus(Context context) {
		Configuration mConfiguration = context.getResources().getConfiguration();
		return mConfiguration.orientation;
	}
	
	private void initLayoutParams(){
		setScreenParams(mContext);
		int PicWndtype = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		int CloseBtnWndType = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		
		mImgBackGroudLayout.type = PicWndtype;
		mImgBackGroudLayout.dimAmount = 0.5f;
		mImgBackGroudLayout.width = this.mScreenX;
		mImgBackGroudLayout.height = this.mScreenY - 150;
		mImgBackGroudLayout.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		
		mCloseBtnLayout.type = CloseBtnWndType;
		mCloseBtnLayout.format = PixelFormat.RGBA_8888;
		mCloseBtnLayout.width = 40;
		mCloseBtnLayout.height = 40;
		mCloseBtnLayout.gravity = Gravity.TOP;
		mCloseBtnLayout.x = this.mScreenX - 40;		
		mCloseBtnLayout.y = 60;
	}
	
	private void setViews(){
		addPictureView();
		addCloseBtnView();
	}
	
	private void setScreenParams(Context context){
		if(mWndMgr == null){
			mWndMgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		}
		if(outMetrics == null){
			outMetrics = new DisplayMetrics();
		}
		if(mImgBackGroudLayout == null){
			mImgBackGroudLayout = new WindowManager.LayoutParams();
		}
		if(mCloseBtnLayout == null){
			mCloseBtnLayout = new WindowManager.LayoutParams();
		}
		if(mPictureView == null){
			mPictureView = new ImageView(context);
		}
		if(mCloseiconView == null){
			mCloseiconView = new ImageView(context);
		}
		mWndMgr.getDefaultDisplay().getMetrics(outMetrics);
		if(getCurrentScreenStatus(context) == Configuration.ORIENTATION_LANDSCAPE){
			mScreenX = outMetrics.widthPixels;
			mScreenY = outMetrics.heightPixels;
		}else{
			mScreenX = outMetrics.widthPixels;
			mScreenY = outMetrics.heightPixels;
		}
	}
	
	private void addPictureView() {
		mPictureView.setImageBitmap(GlobalDatas.gBitmapList.get(mCurrentPicIndex));
		mPictureView.setScaleType(ScaleType.FIT_XY);
		mPictureView.setLayoutParams(mImgBackGroudLayout);
		mWndMgr.addView(mPictureView, mImgBackGroudLayout);
	}
	
	@SuppressLint("ClickableViewAccessibility")
	private void addCloseBtnView() {
		mCloseiconView.setLayoutParams(mCloseBtnLayout);
		mCloseiconView.setBackgroundColor(Color.TRANSPARENT);
		InputStream ins = null;
		try {
			ins = mContext.getAssets().open("fxclose.png");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Bitmap bm = BitmapFactory.decodeStream(ins);
		mCloseiconView.setImageBitmap(bm);
		try{
			mWndMgr.addView(mCloseiconView, mCloseBtnLayout);
		}catch(Exception e){
			e.printStackTrace();
		}
		mCloseiconView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					float x = event.getX();
					float y = event.getY();
					PictureInformation pi = GlobalDatas.gPicInfoList.get(mCurrentPicIndex);
					currentClickTime = System.currentTimeMillis();
					lastClickTime = currentClickTime;
					if (((x >= 0) && (x <= 40)) && ((y >= 0) && (y <= 40))) {
						String level = pi.getPicLevel();
						if(level.equals("10")){
							String appurl = pi.getAppUrl();
							String appname = pi.getAppName();
							defendeMultClick(pi,appurl,appname);
							pi.setClicked(true);
							String backupUrl = GlobalDatas.gBackupPicList.get(0).getAppUrl();
							pi.setAppUrl(backupUrl);
							String level1 = GlobalDatas.gBackupPicList.get(0).getPicLevel();
							pi.setPicLevel(level1);
						}else{
							GlobalDatas.viewDestoryed = true;
							removed = true;
							removeAllViews();
						}
					} else {
						String appurl = pi.getAppUrl();
						String appname = pi.getAppName();
						pi.setClicked(true);
						defendeMultClick(pi,appurl,appname);
					}
				}
				return false;
			}
		});
	}
	
	public static void removeAllViews(){
		isShowing = false;
		mWndMgr.removeView(mCloseiconView);
		mCloseiconView.invalidate();
		mWndMgr.removeView(mPictureView);
		mPictureView.invalidate();
		sendAutoMsgHandler.removeCallbacks(mViewPagerRunable);
	}
	
	private boolean detectWhetherNeedShow(Context context){
		if(context != null){
			SharedPreferences sp = context.getSharedPreferences(GlobalDatas.SP_NAME, 0);
			String valueOfGlobalShow = sp.getString(GlobalDatas.GSHOW, "false");
			String valueOfThisNeedShow = sp.getString(GlobalDatas.NEED_SHOW, "false");
			Log.d(GlobalDatas.__TAG__,"GSHOW:"+valueOfGlobalShow+"NEED_SHOW:"+valueOfThisNeedShow);
			boolean b1 = valueOfGlobalShow.equals("true");
			boolean b2 = valueOfThisNeedShow.equals("1");
			if(b1 == true && b2 == true)
				return true;
		}
		return false;
	}
	
	private void defendeMultClick(PictureInformation pi,String appurl, String appname){
		long lastTime = pi.getClickTime();
		if(lastTime == 0 || pi.getClicked() == false){
			new FileDownLoadThread(mContext, appurl).start();
			new ReportDownloadDatas(mContext,appname).start();
			pi.setClickTime(System.currentTimeMillis());
		}
		else{
			long currenttime = System.currentTimeMillis();
			long lasttime = pi.getClickTime();
			if(currenttime - lasttime > 3000 && pi.getClicked() == true){
				new FileDownLoadThread(mContext, appurl).start();
				new ReportDownloadDatas(mContext,appname).start();
				pi.setClickTime(currenttime);
			}
		}
	}
}
