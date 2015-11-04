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
	
	public void callbackToJS(KCWebView aWebView, String aStr)
	{
		KCJSExecutor.callbackJS(aWebView, mCallbackId, aStr);
	}
	
	
	public String toString()
	{
		return mCallbackId;
	}
}
