package com.kercer.kerkee.bridge;

import org.json.JSONObject;

import com.kercer.kerkee.log.KCLog;
import com.kercer.kerkee.webview.KCWebView;

import android.widget.Toast;

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
