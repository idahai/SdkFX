package sdk.fx;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class ReportDownloadDatas extends Thread{
	private Context context;
	private String name;
	private Map<String,String> data;
	
	public ReportDownloadDatas(Context ctx ,String appname){
		context = ctx;
		data = new HashMap<String,String>();
		name = appname;
	}
	@SuppressLint("SimpleDateFormat")
	public void run(){
		SharedPreferences sp = context.getSharedPreferences(GlobalDatas.SP_NAME, 0);
		String address = sp.getString(GlobalDatas.APP_DOWN_REPORT_URL, "");
		String channelid = sp.getString(GlobalDatas.KEY_CHANNELID, "");
		data.put(GlobalDatas.KEY_CHANNELID, channelid);
		
		String deviceid = sp.getString(GlobalDatas.KEY_IMEI, "");
		data.put(GlobalDatas.KEY_IMEI, deviceid);
		data.put(GlobalDatas.KEY_APP_NAME, name);
		
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddHHmm");
		Date curDate = new Date(System.currentTimeMillis());
		String _time = formatter.format(curDate);
		data.put(GlobalDatas.KEY_APP_DOWN_TIME, _time);
		NetWorkOpt nwo = NetWorkOpt.GetNetWorkOptInstance();
		nwo.sendPostMessage(address, data, "utf-8");
	}
}
