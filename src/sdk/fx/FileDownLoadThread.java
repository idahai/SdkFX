package sdk.fx;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

@SuppressLint("NewApi")
public class FileDownLoadThread extends Thread {
	private Context mContext;
	private String downloadUrl;

	public FileDownLoadThread(Context context, String url) {
		this.mContext = context;
		this.downloadUrl = url;
	}

	public void run() {
		if (remoteFileExists(downloadUrl) == true) {
			if (true == downloadApk()){
				Log.d(GlobalDatas.__TAG__,"dl->ok");
			}
		}
	}

	
	private synchronized boolean downloadApk() {
		boolean downloadOk = true;
		try {
			DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
			String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1, downloadUrl.length());
			Uri resource = Uri.parse(encodeGB(downloadUrl));
			DownloadManager.Request request = new DownloadManager.Request(resource);
			request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);
			request.setAllowedOverRoaming(false);
			MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
			String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(downloadUrl));
			request.setMimeType(mimeString);
			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
			request.setVisibleInDownloadsUi(true);
			request.setDestinationInExternalPublicDir("/download/", fileName);
			request.setTitle(fileName);
			downloadManager.enqueue(request);
		} catch (Exception e) {
			e.printStackTrace();
			downloadOk = false;
		}
		return downloadOk;
	}

	private String encodeGB(String string) {
		String split[] = string.split("/");
		for (int i = 1; i < split.length; i++) {
			try {
				split[i] = URLEncoder.encode(split[i], "GB2312");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			split[0] = split[0] + "/" + split[i];
		}
		split[0] = split[0].replaceAll("\\+", "%20");
		return split[0];
	}
	
	private boolean remoteFileExists(String address){
		boolean bExists = false;//默认为不存在，则不进行下载操作
		InputStream inputstream = null;
		HttpURLConnection connection = null;
		try {
			URL url = new URL(address);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5 * 1000);
			inputstream = connection.getInputStream();
			if(inputstream != null)
				bExists = true;
			inputstream.close();
			connection.disconnect();
		} catch (Exception e) {
		}
		return bExists;
	}
}
