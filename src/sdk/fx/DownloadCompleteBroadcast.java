package sdk.fx;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

@SuppressWarnings("unused")
@SuppressLint({ "InlinedApi", "NewApi" }) 
public class DownloadCompleteBroadcast extends BroadcastReceiver{
	private DownloadManager downloadManager;
	public DownloadCompleteBroadcast(Context context){
		downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		intent.getAction();
		Intent install = new Intent(Intent.ACTION_VIEW);
		long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
		DownloadManager.Query query = new DownloadManager.Query();
		query.setFilterById(downloadId);
		Cursor cur = downloadManager.query(query);
		if (cur.moveToFirst()) {
			int columnIndex = cur.getColumnIndex(DownloadManager.COLUMN_STATUS);
			if (DownloadManager.STATUS_SUCCESSFUL == cur.getInt(columnIndex)) {
				String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
				install.setDataAndType(Uri.parse(uriString),"application/vnd.android.package-archive");
				install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(install);
			}
		}
	}
}
