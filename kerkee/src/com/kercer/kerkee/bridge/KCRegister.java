package com.kercer.kerkee.bridge;

import com.kercer.kercore.task.KCTaskExecutor;
import com.kercer.kerkee.bridge.event.KCEvent;
import com.kercer.kerkee.bridge.xhr.KCXMLHttpRequestManager;

import java.util.HashMap;

/**
 *
 * @author zihong
 *
 */
public class KCRegister
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

    public KCClass registObject(final KCJSObject aObject)
    {
        if (aObject == null) return null;
        String jsObjectName = aObject.getJSObjectName();
        KCClass clz = null;
        if (jsObjectName != null)
        {
            mJSObjectMap.put(jsObjectName, aObject);
            clz = registClass(jsObjectName, aObject.getClass());
        }

        return clz;
    }
    public KCClass removeObject(KCJSObject aObject)
    {
    	if (aObject == null) return null;
    	String jsObjectName = aObject.getJSObjectName();
    	KCClass clz = null;
    	if(jsObjectName != null)
    	{
    		mJSObjectMap.remove(jsObjectName);
    		clz = removeClass(jsObjectName);
    	}
    	return clz;
    }


    public KCClass registClass(KCClass aClass)
    {
        return registClass(aClass.getJSClz(), aClass.getNavClass()) ;
    }

    public KCClass registClass(String aJSObjectName, Class<?> aClass)
    {
        if (aJSObjectName == null || aClass == null) return null;
        KCClass clz = KCClass.newClass(aJSObjectName, aClass);
        mClassMap.put(aJSObjectName, clz);
//            String js = String.format("if(%s && global.%s) global.%s=%s", aJSObjectName, aJSObjectName, aJSObjectName, aJSObjectName);
//            callJS(aWebView, js, true);

        loadMethodsAsyn(clz);

            return clz;
    }

    private void loadMethodsAsyn(final KCClass aClass)
    {
        KCTaskExecutor.executeTask(new Runnable() {
            @Override
            public void run() {
                aClass.loadMethods();
            }
        });
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
