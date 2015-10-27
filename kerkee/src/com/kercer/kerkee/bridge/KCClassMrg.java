package com.kercer.kerkee.bridge;

import com.kercer.kerkee.bridge.event.KCEvent;
import com.kercer.kerkee.bridge.xhr.KCXMLHttpRequestManager;

import java.util.HashMap;

/**
 *
 * @author zihong
 *
 */
public class KCClassMrg
{
    private final static HashMap<String, KCClass> mClassMap = new HashMap<String, KCClass>();
//    private final static HashMap<String, KCMethod> mMethodCache = new HashMap<String, KCMethod>();  //key is identity for KCMethod
    private final static HashMap<String, KCJSObject> mJSObjectMap = new HashMap<String, KCJSObject>();

    static
    {
        mClassMap.put(KCJSDefine.kJS_ApiBridge, KCClass.newClass(KCJSDefine.kJS_ApiBridge, KCApiBridge.class));
        mClassMap.put(KCJSDefine.kJS_jsBridgeClient, KCClass.newClass(KCJSDefine.kJS_jsBridgeClient, KCApiBridgeManager.class));
        mClassMap.put(KCJSDefine.kJS_XMLHttpRequest, KCClass.newClass(KCJSDefine.kJS_XMLHttpRequest, KCXMLHttpRequestManager.class));
        mClassMap.put(KCJSDefine.kJS_event, KCClass.newClass(KCJSDefine.kJS_event, KCEvent.class));

    }


    public KCClass registObject(KCJSObject aObject)
    {
        if (aObject == null) return null;
        String jsObjectName = aObject.getJSObjectName();
        if (jsObjectName != null)
        {
            mJSObjectMap.put(jsObjectName, aObject);
            return registClass(jsObjectName, aObject.getClass());
        }

        return null;
    }
    public KCClass removeObject(KCJSObject aObject)
    {
    	if (aObject == null) return null;
    	String jsObjectName = aObject.getJSObjectName();
    	if(jsObjectName != null)
    	{
    		mJSObjectMap.remove(jsObjectName);
    		return removeClass(jsObjectName);
    	}
    	return null;
    }
    

    public boolean registClass(KCClass aClass)
    {
        return registClass(aClass.getJSClz(), aClass.getNavClass()) != null ? true : false;
    }

    public KCClass registClass(String aJSObjectName, Class<?> aClass)
    {
        if (aJSObjectName == null || aClass == null) return null;
        KCClass clz = KCClass.newClass(aJSObjectName, aClass);
        mClassMap.put(aJSObjectName, KCClass.newClass(aJSObjectName, aClass));
//            String js = String.format("if(%s && global.%s) global.%s=%s", aJSObjectName, aJSObjectName, aJSObjectName, aJSObjectName);
//            callJS(aWebView, js, true);
            return clz;
    }

    public KCClass removeClass(String aJSObjectName)
    {
        if (aJSObjectName == null) return null;
       return mClassMap.remove(aJSObjectName);
    }

    public KCClass getClass(String aClassName)
    {
        return mClassMap.get(aClassName);
    }
    
    public KCJSObject getJSObject(String aJSObjectName)
    {
    	return mJSObjectMap.get(aJSObjectName);
    }


}
