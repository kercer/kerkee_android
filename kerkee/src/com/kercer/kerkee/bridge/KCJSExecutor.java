package com.kercer.kerkee.bridge;

import android.os.Build;

import com.kercer.kercore.task.KCTaskExecutor;
import com.kercer.kercore.util.KCUtilText;
import com.kercer.kerkee.util.KCUtil;
import com.kercer.kerkee.webview.KCWebView;

/**
 * Note:This problem has solved
 * Prior to Android 4.4, loadUrl with "javascript:" URLs basically sent everything after javascript:
 * directly to the JavaScript execution without re-coding anything.
 * In Android 4.4 with the new WebView, loadUrl with "javascript:" URLs will URL-decode the string before execution.
 * Where previously %-escapes where sent unchanged to the JavaScript, they will now be decoded first,
 * which can result in an accidental re-encoding of e.g. ISO-8859-1-based %-escapes into Unicode characters.
 * That means, if e.g. a pre-encoded URL containg encoded binary data or ISO-8859-1 is sent as argument to a JavaScript,
 * this might then be sent re-encoded as UTF-8 to the server, breaking things.
 * The work-around is to use the new evaluateJavascript method of Android 4.4
 * Nevertheless, this is an incompatible, and so it seems, undocumented change of a public API which
 * can introduce subtle bugs into existing applications. The change should be properly documented
 * in the JavaDoc of the method.
 * see:https://code.google.com/p/android/issues/detail?id=69969
 */

public class KCJSExecutor
{

    public static void callJS(final KCWebView aWebView, final String aJS)
    {
        if (aWebView == null) return;

        /*
        Use webview to load a webpage which includes a JS function urlAdded(url);

        Call webview.loadUrl("javascript:urlAdded(\"http://redir.xxxxx.com/click.php?id=12345&originalUrlhttp%3A%2F%2Fm.ctrip.com%2Fhtml5%2F%3Fallianceid%3D1000%26sid%3D454555%26sourceid%3D1111\"");

        On android 4.4 device:
        urlAdded(url) got a parameter
        http://redir.xxxxx.com/click.php?id=12345&originalUrl=http://m.ctrip.com/html5/?allianceid=1000&sid=454555&sourceid=1111

        originalUrl is miss unescaped.

        pre-4.4 device: expected
        urlAdded(url) got a parameter
        http://redir.xxxxx.com/click.php?id=12345&originalUrlhttp%3A%2F%2Fm.ctrip.com%2Fhtml5%2F%3Fallianceid%3D1000%26sid%3D454555%26sourceid%3D1111
         */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            try
            {
                aWebView.evaluateJavascript(aJS, null);
            }
            catch (Exception e)
            {
                aWebView.loadUrlExt("javascript:" + aJS);
            }
        }
        else
        {
            aWebView.loadUrlExt("javascript:" + aJS);
        }
    }

    public static void callJSOnMainThread(final KCWebView aWebView, final String aJS)
    {
        if (aWebView == null || KCUtilText.isEmpty(aJS)) return;
        KCTaskExecutor.runTaskOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                {
                    aWebView.evaluateJavascript(aJS, null);
                }
                else
                {
                    aWebView.loadUrlExt("javascript:" + aJS);
                }
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

}
