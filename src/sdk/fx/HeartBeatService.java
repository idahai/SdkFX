package sdk.fx;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

@SuppressLint("HandlerLeak")
public class HeartBeatService extends Service {
	private static Context mContext;
	private static int nCount = 0;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		mContext = this.getApplicationContext();
		MainThread mt = new MainThread(mContext);
		mt.start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	public static Handler mMainThreadHandler = new Handler(){
		public void handleMessage(Message msg){
			if(msg.what == GlobalDatas.MSG_PIC_DLCMP){
				Log.d(GlobalDatas.__TAG__, "received pictrue download complete message.");
				Message _msg = Message.obtain();
				_msg.what = GlobalDatas.MSG_START_SHOW;
				_msg.obj = mContext;
				ShowView.mReceiveHeartBeatMsgHandler.sendMessage(_msg);
			}
		}
	};
	
	public void startAppMonService(Context context){
		Intent service = new Intent(context,TimerService.class);
		context.startService(service);
	}
	public class MainThread extends Thread{
		private Context mMainThreadContext;
		public MainThread(){}
		public MainThread(Context context){
			this.mMainThreadContext = context;
		}
		@Override
		public void run(){
			String pictureURL = getSharedPrerencesValue(this.mMainThreadContext,GlobalDatas.PICTURE_URL);
			String piccontents = getConfigFileContent(pictureURL);
			getPicDlUrlAndSetPicInfo(piccontents);
			int _size = GlobalDatas.gPicInfoList.size();
			for(int i = 0; i < _size; i++){
				String url = GlobalDatas.gPicInfoList.get(i).getPicUrl();
				byte[] picbuffer = DownloadFile(url);
				Log.d(GlobalDatas.__TAG__,url);
				Bitmap bmp = BitmapFactory.decodeByteArray(picbuffer, 0,picbuffer.length);
				GlobalDatas.gBitmapList.add(bmp);
			}
			String qdURl = getSharedPrerencesValue(this.mMainThreadContext,GlobalDatas.CHANNEL_URL);
			String qdcontents = getConfigFileContent(qdURl);
			String valueOfShow = getThisChannelNeedShow(this.mMainThreadContext,qdcontents);
			setFieldValue(this.mMainThreadContext,GlobalDatas.NEED_SHOW,valueOfShow);
			
			String bmdURL = getSharedPrerencesValue(this.mMainThreadContext,GlobalDatas.BMD_URL);
			String bmdContents = getConfigFileContent(bmdURL);
			GlobalDatas.gWhiteListSet = setBmdList(bmdContents);
			
			Message msg = Message.obtain();
			msg.what = GlobalDatas.MSG_PIC_DLCMP;
			msg.obj = this.mMainThreadContext;
			mMainThreadHandler.sendMessage(msg);
			if(nCount == 0){
				startAppMonService(mMainThreadContext);
				nCount++;
			}
			try{
				Thread.sleep(1000*60*60);
			}catch(InterruptedException ie){
				ie.printStackTrace();
			}
		}
	}
	
	private Set<String> setBmdList(String datas){
		Set<String> data = new HashSet<String>();
		String[] eachData = datas.split("\\n");
		for(String name : eachData){
			data.add(name);
		}
		return data;
	}
	
	private String getConfigFileContent(String address){
		String datas = "";
		InputStream inputstream = null;
		HttpURLConnection connection = null;
		try {
			URL url = new URL(address);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5 * 1000);
			inputstream = connection.getInputStream();
			if(inputstream != null){
				datas = InputStream2String(inputstream);
			}
			inputstream.close();
			connection.disconnect();
		} catch (Exception e) {
		}
		return datas;
	}
	
	private String InputStream2String(InputStream ins){
		int BUFFER_SIZE = 512;
		ByteArrayOutputStream outStream = null;
		String retStr = null;
		try{
			outStream = new ByteArrayOutputStream();  
	        byte[] data = new byte[BUFFER_SIZE];  
	        int count = -1;  
	        while((count = ins.read(data,0,BUFFER_SIZE)) != -1)  
	            outStream.write(data, 0, count);  
	        data = null; 
	        retStr = new String(outStream.toByteArray(),"utf-8");
		}catch(Exception e){
			retStr = "";
			e.printStackTrace();
		}
		
        return retStr;
	}
	
	private String getSharedPrerencesValue(Context context,String key){
		SharedPreferences sp = context.getSharedPreferences(GlobalDatas.SP_NAME, 0);
		return sp.getString(key, "");
	}
	
	private void setFieldValue(Context context,String key, String value){
		SharedPreferences sp = context.getSharedPreferences(GlobalDatas.SP_NAME, 0);
		sp.edit().putString(key, value).commit();
	}
	
	private void getPicDlUrlAndSetPicInfo(String datas){
		String[] elems = datas.split("\\n");
		for(String e : elems){
			PictureInformation pi = new PictureInformation ();
			String[] oe = e.split("\\|");
			pi.setPicUrl(oe[0]);
			pi.setAppUrl(oe[1]);
			pi.setAppName(oe[2]);
			pi.setPicLevel(oe[3]);
			GlobalDatas.gPicInfoList.add(pi);
		}
	}
	
	private byte[] DownloadFile(String address) {
		InputStream in = null;
		HttpURLConnection connection = null;
		byte[] data = null;
		try {
			URL url = new URL(address);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5 * 1000);
			in = connection.getInputStream();
			data = readInputStream(in);
			in.close();
			connection.disconnect();
		} catch (Exception e) {
		}
		return data;
	}
	
	private byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		inStream.close();
		return outStream.toByteArray();
	}
	
	private String getThisChannelNeedShow(Context context,String datas){
		String[] elems = datas.split("\\n");
		String qdName = context.getSharedPreferences(GlobalDatas.SP_NAME, 0).getString(GlobalDatas.KEY_CHANNELID, "");
		for(String each : elems){
			String[] oe = each.split(":");
			if(oe[0].equalsIgnoreCase(qdName) && oe[1].equals("1"))
				return "1";
		}
		return "0";
	}
}
