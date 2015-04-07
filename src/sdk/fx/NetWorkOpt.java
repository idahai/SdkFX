package sdk.fx;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

@SuppressWarnings("unused")
public class NetWorkOpt {
	public static NetWorkOpt mNetWorkOptInstance;

	public NetWorkOpt() {
	}

	public static NetWorkOpt GetNetWorkOptInstance() {
		if (mNetWorkOptInstance == null) {
			mNetWorkOptInstance = new NetWorkOpt();
		}
		return mNetWorkOptInstance;
	}

	public synchronized boolean IsNetWorkActivity(Context context) {
		boolean activity = false;
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = cm.getActiveNetworkInfo();
			if (mNetworkInfo.isConnected() == false) {
				return false;
			}
			activity = mNetworkInfo.isAvailable();
		} catch (Exception e) {
			activity = false;
			e.printStackTrace();
		}
		return activity;
	}

	public boolean post(String address, String params) {
		boolean bPostOk = false;
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			URL realUrl = new URL(address);
			URLConnection conn = realUrl.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			out = new PrintWriter(conn.getOutputStream());
			out.print(params);
			out.flush();
			bPostOk = out.checkError();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return (bPostOk == true) ? false : true;
	}

	public synchronized String GetCommander(String url) {
		String strResult = null;
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		HttpResponse httpResponse;
		try {
			httpResponse = client.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				strResult = EntityUtils.toString(httpResponse.getEntity());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return strResult;
	}

	public synchronized String sendPostMessage(String address,Map<String, String> params,String encode) {
		StringBuilder stringBuilder = new StringBuilder();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				try {
					stringBuilder.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), encode)).append("&");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
			try {
				URL url = new URL(address);
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setConnectTimeout(3000);
				urlConnection.setRequestMethod("POST");
				urlConnection.setDoInput(true);
				urlConnection.setDoOutput(true);
				byte[] myData = stringBuilder.toString().getBytes();
				urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				urlConnection.setRequestProperty("Content-Length",String.valueOf(myData.length));
				OutputStream outputStream = urlConnection.getOutputStream();
				outputStream.write(myData, 0, myData.length);
				outputStream.close();
				int responseCode = urlConnection.getResponseCode();
				if (responseCode == 200) {
					return changeInputStream(urlConnection.getInputStream(),encode);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}
	
	public String changeInputStream(InputStream inputStream, String encode) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		String result = null;
		if (inputStream != null) {
			try {
				while ((len = inputStream.read(data)) != -1) {
					byteArrayOutputStream.write(data, 0, len);
				}
				result = new String(byteArrayOutputStream.toByteArray(), encode);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public String GetMainConfigFileContent(String address){
		InputStream inputstream = null;
		HttpURLConnection connection = null;
		String out = "";
		try {
			URL url = new URL(address);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5 * 1000);
			inputstream = connection.getInputStream();
			if(inputstream == null){
				return null;
			}
			out = InputStream2String(inputstream);
			inputstream.close();
			connection.disconnect();
		} catch (Exception e) {
		}
		return out;
	}
	
	public String InputStream2String(InputStream ins){
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
}
