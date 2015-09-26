package com.kercer.kerkee.api;

import com.kercer.kerkee.bridge.KCJSDefine;
import com.kercer.kerkee.browser.KCJSBridge;

public class KCRegistMgr
{
    static
    {
    	KCJSBridge.registClass(KCJSObjDefine.kJSObj_platform, KCApiPlatform.class);
    	KCJSBridge.registClass(KCJSObjDefine.kJSObj_widget, KCApiWidget.class);
    	KCJSBridge.registClass(KCJSDefine.kJS_jsBridgeClient, KCApiJSBridgeClient.class);
    	KCJSBridge.registClass(KCJSObjDefine.kJSObj_testModule, KCApiTestModule.class);
    }
    
    public static void registClass()
    {
    	
    }
}
