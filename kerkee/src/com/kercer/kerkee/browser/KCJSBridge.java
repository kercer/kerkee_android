package com.kercer.kerkee.browser;

import android.view.ViewGroup;

import com.kercer.kerkee.bridge.KCApiBridge;
import com.kercer.kerkee.bridge.KCClass;
import com.kercer.kerkee.bridge.KCJSDefine;
import com.kercer.kerkee.bridge.KCJSExecutor;
import com.kercer.kerkee.bridge.KCJSObject;
import com.kercer.kerkee.webview.KCWebView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author zihong
 *
 */
public class KCJSBridge
{

    public KCJSBridge()
    {
//        File wwwRoot = new File(getResRootPath() + "/");
//        if(!KCHttpServer.isRunning())
//        	KCHttpServer.startServer(KCHttpServer.getPort(), wwwRoot);
    }



    /********************************************************/
    /*
     * js opt
     */
    /********************************************************/

    public static KCClass registJSBridgeClient(Class<?> aClass)
    {
        return registClass(KCJSDefine.kJS_jsBridgeClient, aClass);
    }

    public static KCClass registClass(KCClass aClass)
    {
        return KCApiBridge.getRegister().registClass(aClass);
    }

    public static KCClass registClass(String aJSObjectName, Class<?> aClass)
    {
        return KCApiBridge.getRegister().registClass(aJSObjectName, aClass);
    }

    public static KCClass registObject(final KCJSObject aObject)
    {
    	return KCApiBridge.getRegister().registObject(aObject);
    }
    public KCClass removeObject(KCJSObject aObject)
    {
    	return KCApiBridge.getRegister().removeObject(aObject);
    }


    public static void removeClass(String aJSObjectName)
    {
        KCApiBridge.getRegister().removeClass(aJSObjectName);
    }


    /********************************************************/
    /*
     * js call, you call us KCJSExecutor
     */
    /********************************************************/
    public static void callJSOnMainThread(final KCWebView aWebview, final String aJS)
    {
    	KCJSExecutor.callJSOnMainThread(aWebview, aJS);
    }

    public static void callJS(final KCWebView aWebview, final String aJS)
    {
    	KCJSExecutor.callJS(aWebview, aJS);
    }

    public static void callJSFunctionOnMainThread(final KCWebView aWebview, String aFunName, String aArgs)
    {
    	KCJSExecutor.callJSFunctionOnMainThread(aWebview, aFunName, aArgs);
    }

    public static void callbackJS(KCWebView aWebview, String aCallbackId)
    {
    	KCJSExecutor.callbackJS(aWebview, aCallbackId);
    }

    public static void callbackJS(KCWebView aWebview, String aCallbackId, String aStr)
    {
    	KCJSExecutor.callbackJS(aWebview, aCallbackId, aStr);
    }

    public static void callbackJS(KCWebView aWebview, String aCallbackId, JSONObject aJSONObject)
    {
    	KCJSExecutor.callbackJS(aWebview, aCallbackId, aJSONObject);
    }

    public static void callbackJS(KCWebView aWebview, String aCallbackId, JSONArray aJSONArray)
    {
    	KCJSExecutor.callbackJS(aWebview, aCallbackId, aJSONArray);
    }


    /********************************************************/
    /*
     * config
     */
    /********************************************************/
    public static void openGlobalJSLog(boolean aIsOpenJSLog)
    {
        KCApiBridge.openGlobalJSLog(aIsOpenJSLog);
    }

    public static void setIsOpenJSLog(KCWebView aWebview, boolean aIsOpenJSLog)
    {
        KCApiBridge.setIsOpenJSLog(aWebview, aIsOpenJSLog);
    }


    public static void destroyWebview(KCWebView aWebview)
    {
        if(aWebview != null)
        {
            aWebview.loadUrl("about:blank");

            ViewGroup vg = (ViewGroup) aWebview.getParent();
            if (vg != null)
                vg.removeView(aWebview);
            aWebview.clearCache(true);
            aWebview.destroy();
            aWebview = null;
        }

    }
}
