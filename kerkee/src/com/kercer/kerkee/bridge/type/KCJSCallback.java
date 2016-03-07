package com.kercer.kerkee.bridge.type;

import com.kercer.kerkee.bridge.KCJSExecutor;
import com.kercer.kerkee.webview.KCWebView;

public class KCJSCallback implements KCJSType
{
	private String mCallbackId;
	
	public KCJSCallback(String aCallbackId)
	{
		mCallbackId = aCallbackId;
	}
	
	public String getCallbackId()
	{
		return mCallbackId;
	}


	public void callbackToJS(final KCWebView aWebView)
	{
		KCJSExecutor.callbackJS(aWebView, mCallbackId);
	}

	public void callbackToJS(final KCWebView aWebView, Object... aArgs)
	{
		KCJSExecutor.callbackJS(aWebView, mCallbackId, aArgs);
	}

//	public void callbackToJS(KCWebView aWebView, String aStr)
//	{
//		KCJSExecutor.callbackJS(aWebView, mCallbackId, aStr);
//	}
//
//	public void callbackToJS(KCWebView aWebView, JSONObject aJSONObject)
//	{
//		KCJSExecutor.callbackJS(aWebView, mCallbackId, aJSONObject);
//	}
//
//	public void callbackToJS(KCWebView aWebView, JSONArray aJSONArray)
//	{
//		KCJSExecutor.callbackJS(aWebView, mCallbackId, aJSONArray);
//	}
	
	public String toString()
	{
		return mCallbackId;
	}
}
