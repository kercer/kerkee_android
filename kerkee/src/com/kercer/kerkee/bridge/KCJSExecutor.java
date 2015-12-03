package com.kercer.kerkee.bridge;

import com.kercer.kercore.task.KCTaskExecutor;
import com.kercer.kerkee.util.KCUtil;
import com.kercer.kerkee.webview.KCWebView;

import org.json.JSONArray;
import org.json.JSONObject;

/********************************************************/
/*
 * js opt
 */
/********************************************************/
public class KCJSExecutor
{

    public static void callJS(final KCWebView aWebview, final String aJS)
    {
        if (aWebview == null)
            return;
        aWebview.loadUrlExt("javascript:" + aJS);
    }
    public static void callJSOnMainThread(final KCWebView aWebview, final String aJS)
    {
        KCTaskExecutor.runTaskOnUiThread(new Runnable() {
            @Override
            public void run() {
                aWebview.loadUrlExt("javascript:" + aJS);
            }
        });
    }


    public static void callJSFunction(final KCWebView aWebview, String aJSFunctionName, Object... aArgs)
    {
        callJS(aWebview, KCMethod.toJS(aJSFunctionName, aArgs));
    }
    public static void callJSFunctionOnMainThread(final KCWebView aWebview, String aJSFunctionName, Object... aArgs)
    {
        callJSOnMainThread(aWebview, KCMethod.toJS(aJSFunctionName, aArgs));
    }


//    public static void callJSFunctionOnMainThread(final KCWebView aWebview, String aFunName, String aString)
//    {
//        StringBuilder sb = KCUtil.getThreadSafeStringBuilder().append(aFunName).append('(');
//        if (aString != null)
//            sb.append(aString);
//        sb.append(')');
//        callJSOnMainThread(aWebview, sb.toString());
//    }
//
//    public static void callJSFunctionOnMainThread(final KCWebView aWebview, String aFunName, String aArgs1, String aArgs2)
//    {
//        StringBuilder sb = KCUtil.getThreadSafeStringBuilder().append(aFunName).append('(');
//        if (aArgs1 != null)
//            sb.append(aArgs1);
//        if (aArgs2 != null) {
//        	sb.append(','+aArgs2);
//        }
//        sb.append(')');
//        callJSOnMainThread(aWebview, sb.toString());
//    }




    public static void callbackJS(KCWebView aWebview, String aCallbackId)
    {
        StringBuilder sb = KCUtil.getThreadSafeStringBuilder().append("ApiBridge.onCallback(").append(aCallbackId).append(')');
        callJSOnMainThread(aWebview, sb.toString());
    }

    public static void callbackJS(KCWebView aWebview, String aCallbackId, String aStr)
    {
        StringBuilder sb = KCUtil.getThreadSafeStringBuilder().append("ApiBridge.onCallback(");
        sb.append(aCallbackId).append(", '").append(aStr).append("')");
        callJSOnMainThread(aWebview, sb.toString());
    }

    public static void callbackJS(KCWebView aWebview, String aCallbackId, JSONObject aJSONObject)
    {
        StringBuilder sb = KCUtil.getThreadSafeStringBuilder().append("ApiBridge.onCallback(");
        sb.append(aCallbackId).append(',').append(aJSONObject.toString()).append(')');
        callJSOnMainThread(aWebview, sb.toString());
    }

    public static void callbackJS(KCWebView aWebview, String aCallbackId, JSONArray aJSONArray)
    {
        StringBuilder sb = KCUtil.getThreadSafeStringBuilder().append("ApiBridge.onCallback(");
        sb.append(aCallbackId).append(',').append(aJSONArray.toString()).append(')');
        callJSOnMainThread(aWebview, sb.toString());
    }


}
