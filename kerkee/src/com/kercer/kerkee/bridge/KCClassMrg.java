package com.kercer.kerkee.bridge;

import java.util.HashMap;

import com.kercer.kerkee.bridge.event.KCEvent;
import com.kercer.kerkee.bridge.xhr.KCXMLHttpRequestManager;

/**
 * 
 * @author zihong
 *
 */
public class KCClassMrg
{
    private final static HashMap<String, KCClass> mClassMap = new HashMap<String, KCClass>();
    private final static HashMap<String, KCMethod> mMethodCache = new HashMap<String, KCMethod>();  //key is identity for KCMethod
    
    static
    {
        mClassMap.put(KCJSDefine.kJS_ApiBridge, KCClass.newClass(KCJSDefine.kJS_ApiBridge, KCApiBridge.class));
        mClassMap.put(KCJSDefine.kJS_jsBridgeClient, KCClass.newClass(KCJSDefine.kJS_jsBridgeClient, KCApiBridgeManager.class));
        mClassMap.put(KCJSDefine.kJS_XMLHttpRequest, KCClass.newClass(KCJSDefine.kJS_XMLHttpRequest, KCXMLHttpRequestManager.class));
        mClassMap.put(KCJSDefine.kJS_event, KCClass.newClass(KCJSDefine.kJS_event, KCEvent.class));
        
    }
    
    public boolean registClass(KCClass aClass)
    {
        return registClass(aClass.getJSClz(), aClass.getNavClass());
    }
    
    public boolean registClass(String aJSObjectName, Class<?> aClass)
    {
        if (!mClassMap.containsKey(aJSObjectName))
        {
            mClassMap.put(aJSObjectName, KCClass.newClass(aJSObjectName, aClass));
//            String js = String.format("if(%s && global.%s) global.%s=%s", aJSObjectName, aJSObjectName, aJSObjectName, aJSObjectName);
//            callJS(aWebView, js, true);
            return true;
        }
        
        return false;
    }
     
    public void removeClass(String aJSObjectName)
    {
        if (aJSObjectName == null) return;
        if (mClassMap.containsKey(aJSObjectName))
        {
            mClassMap.remove(aJSObjectName);
        }
    }
    
    public KCClass getClass(String aClassName)
    {
        return mClassMap.get(aClassName);
    }
    
    
}
