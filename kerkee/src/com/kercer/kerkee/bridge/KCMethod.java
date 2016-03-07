package com.kercer.kerkee.bridge;

import com.kercer.kercore.debug.KCLog;
import com.kercer.kerkee.util.KCUtil;
import com.kercer.kerkee.webview.KCWebView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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

    public String getJSMethodName()
    {
    	return mJSMethodName;
    }

    public int getArgsCount()
    {
       return mMethod.getParameterTypes().length;
    }

    public Class<?>[] getArgTypes()
    {
        return mMethod.getParameterTypes();
    }

    public boolean isSameMethod(final Method aMethod)
    {
    	return mMethod.equals(aMethod);
    }

    public boolean isSameArgList(final Class<?>[] aArgTypes)
    {
        if (aArgTypes == null || aArgTypes.length != getArgsCount()) return false;

        int length = aArgTypes.length;
        Class<?>[] arglist = getArgTypes();
        for (int i = 0; i < length; ++i)
        {
            if (aArgTypes[i] != arglist[i])
            {
                return false;
            }
        }
        return true;
    }

    private int getModifiers()
    {
        return mMethod.getModifiers();
    }

    public boolean isStatic()
    {
        return Modifier.isStatic(getModifiers());
    }



    public Object invoke(KCJSObject aReceiver, Object... aArgs) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        return mMethod.invoke(aReceiver, aArgs);
    }


    public Object invoke(KCJSObject aObject, KCWebView aWebView, KCArgList aArgList)
    {
        KCLog.i("call begin  KCMethod invoke");
        Object returnObj = null;
        try
        {
            Class<?>[] types = mMethod.getParameterTypes();
            if (types.length != aArgList.size())
            {
                throw new RuntimeException(
                        "JavaModule" + "." + mMethod.getName() + " got " + aArgList.size() +
                                " arguments, expected " + types.length);
            }
            Object[] argValues = new Object[types.length];

            int i = 0;
            try
            {
                for (; i < types.length; i++)
                {
                    Class<?> nativeArgType = types[i];

                    if (nativeArgType == Boolean.class || nativeArgType == boolean.class)
                    {
                        argValues[i] = Boolean.valueOf(aArgList.getBoolean(i));
                    }
                    else if (nativeArgType == Integer.class || nativeArgType == int.class)
                    {
                        argValues[i] = Integer.valueOf((int) aArgList.getInt(i));
                    }
                    else if (nativeArgType == Double.class || nativeArgType == double.class)
                    {
                        argValues[i] = Double.valueOf(aArgList.getDouble(i));
                    }
                    else if (nativeArgType == Float.class || nativeArgType == float.class)
                    {
                        argValues[i] = Float.valueOf((float) aArgList.getDouble(i));
                    }
                    else if (nativeArgType == String.class)
                    {
                        argValues[i] = aArgList.getString(i);
                    }
//                    else if (argumentClass == Callback.class)
//                    {
//                        if (aArgList.isNull(i))
//                        {
//                            argValues[i] = null;
//                        }
//                        else
//                        {
//                            int id = (int) aArgList.getDouble(i);
//                            argValues[i] = new CallbackImpl(catalystInstance, id);
//                        }
//                    }
//                    else if (argumentClass == Map.class)
//                    {
//                        argValues[i] = parameters.getMap(i);
//                    }
//                    else if (argumentClass == Array.class)
//                    {
//                        argValues[i] = parameters.getArray(i);
//                    }
                    else
                    {
                        throw new RuntimeException("Got unknown argument class: " + nativeArgType.getSimpleName());
                    }
                }

            }
            catch (Exception e)
            {
                throw new RuntimeException( e.getMessage() + " (constructing arguments for " + aObject.getJSObjectName() +  "." + mMethod.getName() + " at argument index " + i + ")",  e);
            }

            try
            {
                returnObj = invoke(aObject, argValues);
            }
            catch (IllegalArgumentException ie)
            {
                throw new RuntimeException(
                        "Could not invoke " + aObject.getJSObjectName() + "." + mMethod.getName(), ie);
            }
            catch (IllegalAccessException iae)
            {
                throw new RuntimeException(
                        "Could not invoke " + aObject.getJSObjectName() + "." + mMethod.getName(), iae);
            }
            catch (InvocationTargetException ite)
            {
                // Exceptions thrown from native module calls end up wrapped in InvocationTargetException
                // which just make traces harder to read and bump out useful information
                if (ite.getCause() instanceof RuntimeException)
                {
                    throw (RuntimeException) ite.getCause();
                }
                throw new RuntimeException( "Could not invoke " + aObject.getJSObjectName() + "." + mMethod.getName(), ite);
            }
        }
        finally
        {
            KCLog.i("call end  KCMethod invoke");
        }

        return returnObj;
    }


    /**
     * args list to args string
     * @param aJSArgs
     *      if aJSArgs is null, the argslist array is null, return empty string;
     *      if aJsArgs first arg not null, the argslist array is not null, the array element which
     *      is null to null string.
     * @return
     */
    public static String toJsArgsList(Object... aJSArgs)
    {
        StringBuilder js = KCUtil.getThreadSafeStringBuilder();
        if (aJSArgs != null)
        {
            int length = aJSArgs.length;
            for (int i = 0; i < length; ++i)
            {
                Object obj = aJSArgs[i];
                if(obj != null)
                {
                    if(obj instanceof String)
                    {
                        js.append("'");
                        js.append(obj.toString());
                        js.append("'");
                    }
                    else if (obj instanceof Collection)
                    {
                        JSONArray jsonarray = new JSONArray((Collection)obj);
                        js.append(jsonarray.toString());
                    }
                    else if (obj instanceof Map)
                    {
                        JSONObject jsonObject = new JSONObject((Map)obj);
                        js.append(jsonObject);
                    }
                    else
                    {
                        js.append(obj.toString());
                    }
                }
                else
                {
                    js.append("null");
                }

                if (i < length-1)
                {
                    js.append(',');
                }
            }
        }
        return js.toString();
    }

    public static String toJS(String aJSFunctionName, Object... aJSArgs)
    {
        String argsList = toJsArgsList(aJSArgs);
        StringBuilder js = KCUtil.getThreadSafeStringBuilder().append(aJSFunctionName).append('(');
        js.append(argsList);
        js.append(')');
        return js.toString();
    }

}
