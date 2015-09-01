package com.example.kerkee_test;

import org.json.JSONException;
import org.json.JSONObject;

import com.kercer.kerkee.browser.KCJSBridge;
import com.kercer.kerkee.bridge.KCArgList;
import com.kercer.kerkee.bridge.KCJSDefine;
import com.kercer.kerkee.log.KCLog;
import com.kercer.kerkee.webview.KCWebView;

import android.widget.Toast;



public class NewApiTest
{
    public static void testJSBrige(final KCWebView aWebView, KCArgList aArgList)
    {
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
