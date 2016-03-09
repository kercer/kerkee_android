package com.kercer.kerkee.api;

import android.widget.Toast;

import com.kercer.kerkee.bridge.KCArgList;
import com.kercer.kerkee.bridge.KCJSObject;
import com.kercer.kerkee.bridge.type.KCJSCallback;
import com.kercer.kerkee.webview.KCWebView;

/**
 * Created by zihong on 16/3/7.
 */
public class KCApiJSObjExample extends KCJSObject
{
    @Override
    public String getJSObjectName()
    {
        return KCJSObjDefine.kJS_JSObjExampleModule;
    }

    //成员方法
    public void objExampleNotStaticFunction(final KCWebView aWebView, KCArgList aArgList)
    {
        String strTestInfo = aArgList.getString("testInfo");
        Toast.makeText(aWebView.getContext(), strTestInfo, Toast.LENGTH_SHORT).show();

        KCJSCallback callback = aArgList.getCallback();
        if (callback != null)
        {
            callback.callbackToJS(aWebView, "I'm callbackData");
        }

        String strArgList = aArgList.toString();
        Toast.makeText(aWebView.getContext(), strArgList, Toast.LENGTH_SHORT).show();
    }

    //静态方法
    public static void objExampleStaticFunction(final KCWebView aWebView, KCArgList aArgList)
    {
        String strTestInfo = aArgList.getString("testInfo");
        Toast.makeText(aWebView.getContext(), strTestInfo, Toast.LENGTH_SHORT).show();

        KCJSCallback callback = aArgList.getCallback();
        if (callback != null)
        {
            callback.callbackToJS(aWebView, "I'm callbackData");
        }

        String strArgList = aArgList.toString();
        Toast.makeText(aWebView.getContext(), strArgList, Toast.LENGTH_SHORT).show();
    }

}
