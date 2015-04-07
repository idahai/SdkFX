package sdk.fx;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;

@SuppressWarnings("unused")
public class BasicInforGetter {
	public static BasicInforGetter mBasicInforGetterInstance;
	private String mStrDeviceID;
	private String mStrChannelId;
	private String mStrInstallTime;
	private String mStrPkgName;
	private String mAppName;
	public BasicInforGetter(){}
	public static BasicInforGetter getBasicInforGetterInstance(){
		if(mBasicInforGetterInstance == null){
			mBasicInforGetterInstance = new BasicInforGetter();
		}
		return mBasicInforGetterInstance;
	}
	
	public void SetDeviceID(Context context){
		TelephonyManager tm =  ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
		this.mStrDeviceID = tm.getDeviceId();
		this.mAppName = getApplicationName(context);
	}
	
	public void SetPkgName(Context context){
		String strPkgName = context.getPackageName();
		this.mStrPkgName = strPkgName;
		SharedPreferences sp = context.getSharedPreferences(GlobalDatas.SP_NAME, 0);
		String Value = sp.getString(GlobalDatas.KEY_CHANNELID, "");
		this.mStrChannelId = Value;
	}
	
	@SuppressLint("SimpleDateFormat")
	public void SetSoftInstallTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		String now = sdf.format(new Date());
		this.mStrInstallTime = now;
	}
	
	public String GetDeviceID(){
		return this.mStrDeviceID;
	}
	
	public String GetChinalID(){
		return this.mStrChannelId;
	}
	
	public String GetPkgName(){
		return this.mStrPkgName;
	}
	
	public String GetAppName(){
		return this.mAppName;
	}
	public String GetSoftInstallTime(){
		return this.mStrInstallTime;
	}
	
	public void setBasicInfor(Context context){
		SetDeviceID(context);
		SetPkgName(context);
		SetSoftInstallTime();
	}

	public String getApplicationName(Context ctx) {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = ctx.getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			applicationInfo = null;
		}
		String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
		return applicationName;
	}

	public void setChannelID(Context ctx,String id){
		SharedPreferences sp = ctx.getSharedPreferences(GlobalDatas.SP_NAME, 0);
		sp.edit().putString(GlobalDatas.KEY_CHANNELID, id).commit();
	}
}
