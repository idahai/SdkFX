package sdk.fx;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.os.Environment;

public class GeneralOpt {
	public static boolean IsthereSamePluginIn(Context context) {
		String FileName = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/tmp/fx.dat";
		File file = new File(FileName);
		if (file.exists() == false) {
			String cmd = String.format("touch %s", FileName);
			try {
				Runtime.getRuntime().exec(cmd);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		return true;
	}
	
	public static boolean thisChinalNeedDoShow(JSONObject obj,String id){
		boolean needShow = false;
		String objValue = "";
		try{
			objValue = obj.getString(id).replace("\n", "");
		}catch(JSONException je){
			needShow = false;
		}
		if(objValue.equals("1".toString())){
			needShow = true;
		}
		return needShow;
	}
	
	public static JSONObject AnalyticalCommander(String data) {
		if (data.contains("error")) {
			return null;
		}
		JSONObject arrayRet = new JSONObject();
		String[] elements = data.split("\\n");
		for (String element : elements) {
			String[] eachData = element.split(":");
			try {
				arrayRet.put(eachData[0], eachData[1]);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return arrayRet;
	}
	
	public static synchronized String getStringMD5(String input) {

		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			byte[] inputByteArray = input.getBytes();
			messageDigest.update(inputByteArray);
			byte[] resultByteArray = messageDigest.digest();
			return byteArrayToHex(resultByteArray);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	public static synchronized String byteArrayToHex(byte[] byteArray) {
		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9','A', 'B', 'C', 'D', 'E', 'F' };
		char[] resultCharArray = new char[byteArray.length * 2];
		int index = 0;
		for (byte b : byteArray) {
			resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
			resultCharArray[index++] = hexDigits[b & 0xf];
		}
		return new String(resultCharArray);
	}
}
