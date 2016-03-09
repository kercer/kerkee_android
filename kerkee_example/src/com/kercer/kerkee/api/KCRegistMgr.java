package com.kercer.kerkee.api;

import com.kercer.kerkee.browser.KCJSBridge;

public class KCRegistMgr
{
    static
    {
    	KCJSBridge.registClass(KCJSObjDefine.kJSObj_platform, KCApiPlatform.class);
    	KCJSBridge.registClass(KCJSObjDefine.kJSObj_widget, KCApiWidget.class);

        // 注册类，所注册的native code中的类的函数必是静态函数，js都能调到
        // 第一个参数为js的类名，第二个参数为native的类名
        KCJSBridge.registClass(KCJSObjDefine.kJSObj_testModule, KCApiTestModule.class);

        // 上层可以使用自己的类取代kerkee中的jsBridgeClient，对应的js对象类为jsBridgeClient
        // KCJSBridge.registClass(KCJSDefine.kJS_jsBridgeClient, KCApiJSBridgeClient.class);//与下一行效果一致
        KCJSBridge.registJSBridgeClient(KCApiJSBridgeClient.class);

        // 注册对象方式
        // KCApiJSObjExample必须继承KCJSObject，并实现getJSObjectName()方法，返回js类名
        // KCApiJSObjExample中的函数可以是静态函数也可以是成员函数，HybridRuntime会自动处理
        KCJSBridge.registObject(new KCApiJSObjExample());
    }

    public static void registClass()
    {
        // call this func, java can call static block
    }
}
