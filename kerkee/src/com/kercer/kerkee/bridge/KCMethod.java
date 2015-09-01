package com.kercer.kerkee.bridge;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 
 * @author zihong
 *
 */
public class KCMethod
{
    private String mJSMethodName;
    private Method mMethod;
    private String mIdentity = "";
    
    public KCMethod(KCClass aClass, Method aMethod)
    {
        mMethod = aMethod;
        mJSMethodName =  mMethod.getName();
    }
    
    public static String createIdentity(String aClzName, String aMethodName, List<String> aArgsKeys)
    {
        return aClzName + "_" + aMethodName + "_" + aArgsKeys.toString();
    }
    
    public String getIdentity()
    {
        return mIdentity;
    }
    
    public Method getNavMethod()
    {
        return mMethod;
    }
    
    public int getArgsCount()
    {
       return mMethod.getParameterTypes().length;
    }
    
    public Object invoke(Object aReceiver, Object... aArgs) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        return mMethod.invoke(aReceiver, aArgs);
    }
}
