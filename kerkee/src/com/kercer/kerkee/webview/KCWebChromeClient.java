package com.kercer.kerkee.webview;

import com.kercer.kerkee.bridge.KCApiBridge;

import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * 
 * @author zihong
 *
 */
public class KCWebChromeClient extends WebChromeClient
{
    private static KCWebChromeClient mInstance;
    private boolean mEnableConsoleLog;

    public KCWebChromeClient()
    {
        super();
    }

    public static KCWebChromeClient getInstance()
    {
        if (mInstance == null)
            mInstance = new KCWebChromeClient();
        return mInstance;
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage)
    {

        // we MUST return true we are done with debugging
        return !mEnableConsoleLog; // return false to enable console.log
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result)
    {
    	if(view instanceof KCWebView) {
    		result.confirm(KCApiBridge.callNative((KCWebView) view, message));
    	}
        return true;

    }

//    @Override
//    public boolean onJsAlert(WebView view, String url, String message, JsResult result)
//    {
//        result.confirm();
//        return true;
//    }
    
}
