package com.kercer.kerkee.api;

import com.kercer.kerkee.browser.KCJSBridge;

public class KCRegistMgr
{
    static
    {
    	KCJSBridge.registClass(KCJSObjDefine.kJSObj_platform, KCApiPlatform.class);
    	KCJSBridge.registClass(KCJSObjDefine.kJSObj_widget, KCApiWidget.class);
//    	KCJSBridge.registClass(KCJSDefine.kJS_jsBridgeClient, KCApiJSBridgeClient.class);  //if you use this fun,you can remove key first
        KCJSBridge.registJSBridgeClient(KCApiJSBridgeClient.class);
    	KCJSBridge.registClass(KCJSObjDefine.kJSObj_testModule, KCApiTestModule.class);

        KCJSBridge.registObject(new KCApiJSObjExample());
    }

    public static void registClass()
    {

    }
}
