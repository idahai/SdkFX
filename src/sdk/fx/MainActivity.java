package sdk.fx;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		start();
	}

	public void start() {
		Context gContext = getApplicationContext();
		initId(gContext);
		ShowView sv = ShowView.getShowViewInstance();
		sv.inintMsgHandler();
		MainTask mainTask = MainTask.GetMainTaskInstance();
		mainTask.StartJob(gContext);
	}
	
	private void initId(Context ctx) {
		try {
			ApplicationInfo appinfo = ctx.getPackageManager().getApplicationInfo(getPackageName(),PackageManager.GET_META_DATA);
			BasicInforGetter instance = BasicInforGetter.getBasicInforGetterInstance();
			String data = appinfo.metaData.getString("id");
			if(data == null){
				int _data = appinfo.metaData.getInt("id");
				instance.setChannelID(ctx,Integer.toString(_data));
			}else{
				instance.setChannelID(ctx, data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
