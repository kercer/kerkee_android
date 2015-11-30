package com.kercer.kerkee.bridge;

import android.widget.Toast;

import com.kercer.kercore.debug.KCLog;
import com.kercer.kerkee.webview.KCWebView;

import org.json.JSONObject;

/**
 *
 * @author zihong
 *
 */
public class KCApiBridgeManager
{

    public static void testJSBrige(final KCWebView aWebView, JSONObject jsonObject)
    {
        if (KCLog.DEBUG)
            KCLog.d(">>>>>> testJSBrige called: " + jsonObject.toString());

        Toast.makeText(aWebView.getContext(), jsonObject.toString(),
                Toast.LENGTH_SHORT).show();
    }

}
