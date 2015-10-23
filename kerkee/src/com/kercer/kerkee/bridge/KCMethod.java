package com.kercer.kerkee.bridge;

import com.kercer.kerkee.log.KCLog;

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

    public KCMethod(Method aMethod)
    {
        this(null, aMethod);
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


    public Object invoke(KCJSObject aObject, KCArgList aArgList)
    {
        KCLog.i("call begin  KCMethod invoke");
        Object returnObj = null;
        try
        {
            Class[] types = mMethod.getParameterTypes();
            if (types.length != aArgList.size())
            {
                throw new RuntimeException(
                        "JavaModule" + "." + mMethod.getName() + " got " + aArgList.size() +
                                " arguments, expected " + types.length);
            }
            Object[] arguments = new Object[types.length];

            int i = 0;
            try
            {
                for (; i < types.length; i++)
                {
                    Class argumentClass = types[i];
                    if (argumentClass == Boolean.class || argumentClass == boolean.class)
                    {
                        arguments[i] = Boolean.valueOf(aArgList.getBoolean(i));
                    }
                    else if (argumentClass == Integer.class || argumentClass == int.class)
                    {
                        arguments[i] = Integer.valueOf((int) aArgList.getInt(i));
                    }
                    else if (argumentClass == Double.class || argumentClass == double.class)
                    {
                        arguments[i] = Double.valueOf(aArgList.getDouble(i));
                    }
                    else if (argumentClass == Float.class || argumentClass == float.class)
                    {
                        arguments[i] = Float.valueOf((float) aArgList.getDouble(i));
                    }
                    else if (argumentClass == String.class)
                    {
                        arguments[i] = aArgList.getString(i);
                    }
//                    else if (argumentClass == Callback.class)
//                    {
//                        if (aArgList.isNull(i))
//                        {
//                            arguments[i] = null;
//                        }
//                        else
//                        {
//                            int id = (int) aArgList.getDouble(i);
//                            arguments[i] = new CallbackImpl(catalystInstance, id);
//                        }
//                    }
//                    else if (argumentClass == Map.class)
//                    {
//                        arguments[i] = parameters.getMap(i);
//                    }
//                    else if (argumentClass == Array.class)
//                    {
//                        arguments[i] = parameters.getArray(i);
//                    }
                    else
                    {
                        throw new RuntimeException("Got unknown argument class: " + argumentClass.getSimpleName());
                    }
                }

            }
            catch (Exception e)
            {
                throw new RuntimeException( e.getMessage() + " (constructing arguments for " + aObject.getObjectName() +  "." + mMethod.getName() + " at argument index " + i + ")",  e);
            }

            try
            {
                returnObj = mMethod.invoke(aObject, arguments);
            }
            catch (IllegalArgumentException ie)
            {
                throw new RuntimeException(
                        "Could not invoke " + aObject.getObjectName() + "." + mMethod.getName(), ie);
            }
            catch (IllegalAccessException iae)
            {
                throw new RuntimeException(
                        "Could not invoke " + aObject.getObjectName() + "." + mMethod.getName(), iae);
            }
            catch (InvocationTargetException ite)
            {
                // Exceptions thrown from native module calls end up wrapped in InvocationTargetException
                // which just make traces harder to read and bump out useful information
                if (ite.getCause() instanceof RuntimeException)
                {
                    throw (RuntimeException) ite.getCause();
                }
                throw new RuntimeException( "Could not invoke " + aObject.getObjectName() + "." + mMethod.getName(), ite);
            }
        }
        finally
        {
            KCLog.i("call end  KCMethod invoke");
        }

        return returnObj;
    }


}
