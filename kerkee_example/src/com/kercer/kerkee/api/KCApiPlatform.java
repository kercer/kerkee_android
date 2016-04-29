package com.kercer.kerkee.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.kercer.kercore.debug.KCLog;
import com.kercer.kerkee.bridge.KCArgList;
import com.kercer.kerkee.bridge.KCJSDefine;
import com.kercer.kerkee.browser.KCJSBridge;
import com.kercer.kerkee.webview.KCWebView;

import org.json.JSONException;
import org.json.JSONObject;

public class KCApiPlatform
{
	/**
	 * 没有网络
	 */
	public static final String NETWORKTYPE_INVALID = "invalid";
	/**
	 * wap网络
	 */
	public static final String NETWORKTYPE_WAP = "wap";
	/**
	 * 2G网络
	 */
	public static final String NETWORKTYPE_2G = "2g";
	/**
	 * 3G和3G以上网络，或统称为快速网络
	 */
	public static final String NETWORKTYPE_3G = "3g";
	/**
	 * wifi网络
	 */
	public static final String NETWORKTYPE_WIFI = "wifi";

	public static void getDevice(final KCWebView aWebView, KCArgList aArgList)
	{
		if (KCLog.DEBUG)
			KCLog.d(">>>>>> NewApiTest getDevice called: " + aArgList.toString());

		String callbackId = aArgList.getString(KCJSDefine.kJS_callbackId);
		DisplayMetrics dm = new DisplayMetrics();
		dm = aWebView.getContext().getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		JSONObject jsonObject = null;
		try
		{
			jsonObject = new JSONObject();
			jsonObject.put("model", Build.MODEL);
			jsonObject.put("brand", Build.BRAND);
			jsonObject.put("device", Build.DEVICE);
			jsonObject.put("display", screenWidth + "X" + screenHeight);
			jsonObject.put("product", Build.PRODUCT);
			jsonObject.put("hardware", Build.HARDWARE);
		}
		catch (JSONException e)
		{
			KCLog.e(e);
		}
		KCJSBridge.callbackJS(aWebView, callbackId, jsonObject);
	}

	public static void getNetworkType(final KCWebView aWebView, KCArgList aArgList)
	{

		if (KCLog.DEBUG)
			KCLog.d(">>>>>> NewApiTest getNetworkType called: " + aArgList.toString());

		String callbackId = aArgList.getString(KCJSDefine.kJS_callbackId);

		ConnectivityManager manager = (ConnectivityManager) aWebView.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();

		String netTypeStr = NETWORKTYPE_INVALID;
		if (networkInfo != null && networkInfo.isConnected())
		{
			String type = networkInfo.getTypeName();

			if (type.equalsIgnoreCase("WIFI"))
			{
				netTypeStr = NETWORKTYPE_WIFI;
			}
			else if (type.equalsIgnoreCase("MOBILE"))
			{
				String proxyHost = android.net.Proxy.getDefaultHost();

				netTypeStr = TextUtils.isEmpty(proxyHost) ? (isFastMobileNetwork(aWebView.getContext()) ? NETWORKTYPE_3G : NETWORKTYPE_2G) : NETWORKTYPE_WAP;
			}
		}
		else
		{
			netTypeStr = NETWORKTYPE_INVALID;
		}

		JSONObject jsonObject = null;
		try
		{
			jsonObject = new JSONObject();
			jsonObject.put("network", netTypeStr);
		}
		catch (JSONException e)
		{
			KCLog.e(e);
		}
		KCJSBridge.callbackJS(aWebView, callbackId, jsonObject);

	}

	public static boolean isFastMobileNetwork(Context context)
	{
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		switch (telephonyManager.getNetworkType())
		{
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			return false; // ~ 50-100 kbps
		case TelephonyManager.NETWORK_TYPE_CDMA:
			return false; // ~ 14-64 kbps
		case TelephonyManager.NETWORK_TYPE_EDGE:
			return false; // ~ 50-100 kbps
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			return true; // ~ 400-1000 kbps
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			return true; // ~ 600-1400 kbps
		case TelephonyManager.NETWORK_TYPE_GPRS:
			return false; // ~ 100 kbps
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			return true; // ~ 2-14 Mbps
		case TelephonyManager.NETWORK_TYPE_HSPA:
			return true; // ~ 700-1700 kbps
		case TelephonyManager.NETWORK_TYPE_HSUPA:
			return true; // ~ 1-23 Mbps
		case TelephonyManager.NETWORK_TYPE_UMTS:
			return true; // ~ 400-7000 kbps
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			return true; // ~ 1-2 Mbps
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
			return true; // ~ 5 Mbps
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			return true; // ~ 10-20 Mbps
		case TelephonyManager.NETWORK_TYPE_IDEN:
			return false; // ~25 kbps
		case TelephonyManager.NETWORK_TYPE_LTE:
			return true; // ~ 10+ Mbps
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			return false;
		default:
			return false;
		}
	}
}
