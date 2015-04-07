package sdk.fx;

import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class TimerService extends Service {
	private Context mContext;
	private ActivityManager manager;
	private long time = 1000 * 60 * 60 * 4;
	public static TimerThread mInstance;
	private Message mMsg;
	public static Handler mMsghandler;
	public static Runnable mRunnable;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		initHandler();
		startingRunnable(this.getApplicationContext());
		new TimerThread(this.getApplicationContext()).start();
		return super.onStartCommand(intent, flags, startId);
	}

	private void initHandler(){
		mMsghandler = new Handler();
	}
	
	private void startingRunnable(final Context context){
		mRunnable = new Runnable(){
			public void run() {
				new TimerThread(context).start();
				mMsghandler.postDelayed(mRunnable, time);
			}
		};
		mMsghandler.postDelayed(mRunnable, time);
	}
	public class TimerThread extends Thread {
		public TimerThread(Context context) {
			mContext = context;
			manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		}

		public void run() {
			try {
				Thread.sleep(time);
				mMsghandler.sendEmptyMessage(1000);
				Log.d(GlobalDatas.__TAG__, "times up");
				String topName = getCurrentRunningActivityPackgeName(manager);
				if (UnexpectName(topName) == false&& ShowView.isShowing == false) {
					Log.d(GlobalDatas.__TAG__,"not expected name and not showing now.");
					mMsg = Message.obtain();
					mMsg.what = GlobalDatas.MSG_NEW_APP_START;
					mMsg.obj = mContext;
					ShowView.mReceiveHeartBeatMsgHandler.sendMessage(mMsg);
				}
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}

		private String getCurrentRunningActivityPackgeName(
				ActivityManager manager) {
			String packageName = "";
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
				List<ActivityManager.RunningAppProcessInfo> tasks = manager
						.getRunningAppProcesses();
				packageName = tasks.get(0).processName;
			} else {
				List<ActivityManager.RunningTaskInfo> runningTasks = manager
						.getRunningTasks(1);
				ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks
						.get(0);
				ComponentName topActivity = runningTaskInfo.topActivity;
				packageName = topActivity.getPackageName();
			}
			return packageName;
		}

		private boolean UnexpectName(String name) {
			return GlobalDatas.gWhiteListSet.contains(name);
		}
	}

}
