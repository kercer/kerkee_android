package com.kercer.kerkee.imagesetter;

import com.kercer.kerkee.bridge.KCApiBridge;
import com.kercer.kerkee.bridge.KCJSExecutor;
import com.kercer.kerkee.net.uri.KCURI;
import com.kercer.kerkee.webview.KCWebView;

/**
 * 
 * @author zihong
 *
 */
public class KCWebImageSetterTask
{
    public KCWebView mWebView;
    public KCURI mUri;  //[scheme:][//authority][path][?query][#fragment]
    public String mUrl;
    
    public KCWebImageSetterTask(KCWebView aWebView, String aUrl, KCURI aUri)
    {
        mWebView = aWebView;
        mUrl = aUrl;
        mUri = aUri;
    }
    
    public static KCWebImageSetterTask create(KCWebView aWebView, String aUrl, KCURI aUri)
    {
        KCWebImageSetterTask task = new KCWebImageSetterTask(aWebView, aUrl, aUri);
        return task;
    }
    
    public boolean canExecute()
    {
        return mWebView.isDocumentReady();
    }
    
    public void executeTask()
    {
        final String srcSuffix = mUrl.substring(mUrl.lastIndexOf('/') + 1);
        KCJSExecutor.callJSOnMainThread(mWebView, "jsBridgeClient.onSetImage('" + srcSuffix + "','" + mUri.toString() + "')");
    }
    
}
