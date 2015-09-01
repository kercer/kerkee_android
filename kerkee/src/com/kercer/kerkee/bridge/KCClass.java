package com.kercer.kerkee.bridge;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author zihong
 *
 */
public class KCClass
{
    private String mJSClzName;
    private Class<?> mClz;
    //the key is method name
    private HashMap<String, List<KCMethod>> mMethods = new HashMap<String, List<KCMethod>>();
    
    
    public KCClass(String aJSClzName,  Class<?> aClass)
    {
        mJSClzName = aJSClzName;
        mClz = aClass;
    }
    
    public static KCClass newClass(String aJSClzName,  Class<?> aClass)
    {
        return new KCClass(aJSClzName, aClass);
    }
    
    public Class<?> getNavClass()
    {
        return mClz;
    }
    
    public String getJSClz()
    {
        return mJSClzName;
    }
    
    public void addMethod(String aMethodName, KCArgList aArgList)
    {
      //TODO
    }
    
    public List<KCMethod> getMethods(String aName)
    {
       return mMethods.get(aName);
    }
    
    
    public KCMethod getMethod(String aMethodName, Class<?>... aParameterTypes) throws NoSuchMethodException
    {
        List<KCMethod> listMethods = getMethods(aMethodName);
        if (listMethods == null)
        {
            listMethods = new ArrayList<KCMethod>();
        }
        
        KCMethod method = null;
        for(int i = 0; i < listMethods.size(); ++i)
        {
            KCMethod tmpMethod = listMethods.get(i);
            if (tmpMethod == null) continue;
            if (aParameterTypes.length == tmpMethod.getArgsCount())
                method = tmpMethod;
        }
        
        if (method == null)
        {            
            Method tmp = mClz.getMethod(aMethodName, aParameterTypes);
            method = new KCMethod(this, tmp);
            listMethods.add(method);
            mMethods.put(aMethodName, listMethods);
        }
        return method;
    }
    
    
    
    
}
