package com.kercer.kerkee.bridge;

import com.kercer.kercore.task.KCTaskExecutor;
import com.kercer.kerkee.util.KCUtil;
import com.kercer.kerkee.webview.KCWebView;

/********************************************************/
/*
 * js opt
 */
/********************************************************/
public class KCJSExecutor
{

    public static void callJS(final KCWebView aWebView, final String aJS)
    {
        if (aWebView == null)
            return;
        aWebView.loadUrlExt("javascript:" + aJS);
    }
    public static void callJSOnMainThread(final KCWebView aWebView, final String aJS)
    {
        KCTaskExecutor.runTaskOnUiThread(new Runnable() {
            @Override
            public void run() {
                aWebView.loadUrlExt("javascript:" + aJS);
            }
        });
    }


    public static void callJSFunction(final KCWebView aWebView, String aJSFunctionName, Object... aArgs)
    {
        callJS(aWebView, KCMethod.toJS(aJSFunctionName, aArgs));
    }
    public static void callJSFunctionOnMainThread(final KCWebView aWebView, String aJSFunctionName, Object... aArgs)
    {
        callJSOnMainThread(aWebView, KCMethod.toJS(aJSFunctionName, aArgs));
    }

    public static void callbackJS(final KCWebView aWebView, String aCallbackId, Object... aArgs)
    {
        String argsString = KCMethod.toJsArgsList(aArgs);
        StringBuilder sb = KCUtil.getThreadSafeStringBuilder().append("ApiBridge.onCallback(");
        sb.append(aCallbackId);
        if (argsString.length() > 0)
        {
            sb.append(",");
            sb.append(argsString);
        }
        sb.append(')');

        callJSOnMainThread(aWebView, sb.toString());
    }

    public static void callbackJS(KCWebView aWebview, String aCallbackId)
    {
        StringBuilder sb = KCUtil.getThreadSafeStringBuilder().append("ApiBridge.onCallback(").append(aCallbackId).append(')');
        callJSOnMainThread(aWebview, sb.toString());
    }

//    public static void callbackJS(KCWebView aWebview, String aCallbackId, String aStr)
//    {
//        StringBuilder sb = KCUtil.getThreadSafeStringBuilder().append("ApiBridge.onCallback(");
//        if (aStr != null)
//            sb.append(aCallbackId).append(", '").append(aStr).append("')");
//        else
//            sb.append(aCallbackId).append(",null)");
//        callJSOnMainThread(aWebview, sb.toString());
//    }

//    public static void callbackJS(KCWebView aWebview, String aCallbackId, JSONObject aJSONObject)
//    {
//        String strData = (aJSONObject != null) ? aJSONObject.toString() : "null";
//        StringBuilder sb = KCUtil.getThreadSafeStringBuilder().append("ApiBridge.onCallback(");
//        sb.append(aCallbackId).append(',').append(strData).append(')');
//        callJSOnMainThread(aWebview, sb.toString());
//    }
//
//    public static void callbackJS(KCWebView aWebview, String aCallbackId, JSONArray aJSONArray)
//    {
//        String strData = (aJSONArray != null) ? aJSONArray.toString() : "null";
//        StringBuilder sb = KCUtil.getThreadSafeStringBuilder().append("ApiBridge.onCallback(");
//        sb.append(aCallbackId).append(',').append(strData).append(')');
//        callJSOnMainThread(aWebview, sb.toString());
//    }


}
