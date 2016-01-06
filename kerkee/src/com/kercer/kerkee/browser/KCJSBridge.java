package com.kercer.kerkee.browser;

import android.view.ViewGroup;

import com.kercer.kerkee.bridge.KCApiBridge;
import com.kercer.kerkee.bridge.KCClass;
import com.kercer.kerkee.bridge.KCJSDefine;
import com.kercer.kerkee.bridge.KCJSExecutor;
import com.kercer.kerkee.bridge.KCJSObject;
import com.kercer.kercore.io.KCAssetTool;
import com.kercer.kerkee.webview.KCWebView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author zihong
 *
 */
public class KCJSBridge
{
    protected KCWebView mWebView;
    private final String VERSION_NAME = "1.0.0";

    public KCJSBridge(KCWebView aWebView)
    {
        this.mWebView = aWebView;

        if (!isExitAsset())
            copyAssetHtmlDir();

//        File wwwRoot = new File(getResRootPath() + "/");
//        if(!KCHttpServer.isRunning())
//        	KCHttpServer.startServer(KCHttpServer.getPort(), wwwRoot);
    }

    public String getVersion()
    {
    	return VERSION_NAME;
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
     * js call
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
     *
     */
    /********************************************************/

    private boolean isExitAsset()
    {
        String cfgPath = mWebView.getWebPath().getCfgPath();
        File file = new File(cfgPath);
        if (file.exists())
            return true;
        return false;
    }

    private void copyAssetHtmlDir()
    {
        KCAssetTool assetTool = new KCAssetTool(mWebView.getContext());
        try
        {
            assetTool.copyDir("html", mWebView.getWebPath().getResRootPath());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    public KCWebView getWebView()
    {
        return mWebView;
    }

    public String getResRootPath()
    {
        return mWebView == null ? null : mWebView.getWebPath().getResRootPath();
    }

    public void destroy()
    {
        ViewGroup vg = (ViewGroup) mWebView.getParent();
        if (vg != null)
            vg.removeView(mWebView);
        mWebView.clearCache(true);
        mWebView.destroy();
        mWebView = null;
    }
}
