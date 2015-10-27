package com.kercer.kerkee.bridge;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zihong on 15/10/23.
 */
public abstract class KCJSObject
{	
    abstract public String getJSObjectName();    
    
    public final Map<String, KCMethod> getMethods()
    {
        Map<String, KCMethod> methods = new HashMap<String, KCMethod>();
        Method[] targetMethods = getClass().getDeclaredMethods();
        for (int i = 0; i < targetMethods.length; i++)
        {
            Method targetMethod = targetMethods[i];
            if (targetMethod.getAnnotation(KerkeeMethod.class) != null)
            {
                String methodName = targetMethod.getName();
                if (methods.containsKey(methodName))
                {
                    // We do not support method overloading since js sees a function as an object regardless
                    // of number of params.
                    throw new IllegalArgumentException("Java Class " + getJSObjectName() + " method name already registered: " + methodName);
                }
                methods.put(methodName, new KCMethod(targetMethod));
            }
        }
        return methods;
    }



}
