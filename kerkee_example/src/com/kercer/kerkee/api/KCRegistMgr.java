package com.kercer.kerkee.api;

import com.kercer.kerkee.browser.KCJSBridge;

public class KCRegistMgr
{
    static
    {
    	KCJSBridge.registClass("jsBridgeClient", KCApiJSBridgeClient.class);
    }
    
    public static void registClass()
    {
    	
    }
}
