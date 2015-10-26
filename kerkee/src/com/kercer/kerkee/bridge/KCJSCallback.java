package com.kercer.kerkee.bridge;

import com.kercer.kerkee.webview.KCWebView;

public class KCJSCallback
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
