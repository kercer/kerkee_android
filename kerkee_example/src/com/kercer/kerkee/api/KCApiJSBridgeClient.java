package com.kercer.kerkee.api;

import org.json.JSONException;
import org.json.JSONObject;

import com.kercer.kerkee.browser.KCJSBridge;
import com.kercer.kerkee.bridge.KCArgList;
import com.kercer.kerkee.bridge.KCJSDefine;
import com.kercer.kerkee.bridge.KCJSCompileExecutor;
import com.kercer.kerkee.bridge.KCReturnCallback;
import com.kercer.kerkee.log.KCLog;
import com.kercer.kerkee.webview.KCWebView;

import android.widget.Toast;


/**
 * This is a JS object API, the JS object is jsBridgeClient
 * @author zihong
 *
 */
public class KCApiJSBridgeClient
{
    public static void testJSBrige(final KCWebView aWebView, KCArgList aArgList)
    {
    	KCJSCompileExecutor.compileJS(aWebView, "document.title", new KCReturnCallback()
		{			
			@Override
			public void returnCallback(Object aObject)
			{
				String a = (String) aObject;
				KCLog.i(a);
			}
		});
    	
    	
        String str = aArgList.toString();
        Toast.makeText(aWebView.getContext(), str,
                Toast.LENGTH_SHORT).show();
        if (KCLog.DEBUG)
            KCLog.d(">>>>>> NewApiTest testJSBrige called: " + aArgList.toString());
    }
    
    public static void commonApi(final KCWebView aWebView, KCArgList aArgList)
    {
        if (KCLog.DEBUG)
            KCLog.d(">>>>>> NewApiTest commonApi called: " + aArgList.toString());
        
        String callbackId = (String)aArgList.getArgValue(KCJSDefine.kJS_callbackId);
        JSONObject jsonObject = null;
		try
		{
			jsonObject = new JSONObject("{'key'='value'}");
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
        KCJSBridge.callbackJS(aWebView, callbackId, jsonObject);
    }
    
    public static void onSetImage(final KCWebView aWebView, KCArgList aArgList)
    {
        if (KCLog.DEBUG)
            KCLog.d(">>>>>> NewApiTest onSetImage called: " + aArgList.toString());
    }
}
