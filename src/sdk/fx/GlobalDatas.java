package sdk.fx;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.graphics.Bitmap;

public class GlobalDatas {
	public static int nCountHeartBeat                  = 0;
	public static int nCountAppLauMon                  = 0;
	public static final String SP_NAME                 = "TRUE";
	public static boolean IS_RUNNING                   = false;
	public static boolean ACT_SOFT_INSTALL             = false;
	public static final int OPT_TYPE_SOFT_INSTALL      = 1;
	public static final int OPT_TYPE_SOFT_START        = 2;
	public static final int OPT_TYPE_SOFT_UNINSTALL    = 3;
	public static final int OPT_TYPE_AD_CLCICK         = 4;
	public static final int MSG_PIC_DLCMP              = 0x1001;
	public static final int MSG_NEW_APP_START          = 0x1002;
	public static final int MSG_START_SHOW             = 0x1004;
	public static final int MSG_READY_OK               = 0x1005;
	public static final String __TAG__                 = "FX";
	public static boolean viewDestoryed                = false;
	public final static String GSHOW                   = "GS";
	public final static String REPORT_URL              = "RU";
	public final static String CHANNEL_URL             = "CU";
	public final static String PICTURE_URL             = "PU";
	public final static String BMD_URL                 = "BU";
	public final static String APP_DOWN_REPORT_URL     = "DU";
	public final static String REPLACE_URL             = "EU";
	public final static String PIC_MD5                 = "PM";
	public final static String QD_MD5                  = "QM";
	public final static String KEY_CHANNELID           = "cid";
	public final static String KEY_APP_NAME            = "name";
	public final static String KEY_IMEI                = "did";
	public final static String KEY_INSTALL_TIME        = "itt";
	public final static String KEY_PKGNAME             = "pgn";
	public final static String KEY_HAS_INSTALLED       = "hi";
	public final static String KEY_APP_DOWN_TIME       = "adt";
	public final static String NAME_OPT                = "opt";
	public final static String NAME_RIRSTRUN           = "ftr";
	public static String FIRST_RUN                     = "0";
	public static String NEED_SHOW                     = "NS";
	public static List<PictureInformation> gPicInfoList;
	public static List<Bitmap>             gBitmapList;
	public static Set<String>              gWhiteListSet;
	public static List<PictureInformation> gBackupPicList;
	
	static{
		gPicInfoList   = new ArrayList<PictureInformation>();
		gBackupPicList = new ArrayList<PictureInformation>();
		gBitmapList    = new ArrayList<Bitmap>();
		gWhiteListSet  = new HashSet<String>();
	}
}
