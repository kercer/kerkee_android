package com.kercer.kerkee.bridge;

import com.kercer.kerkee.bridge.type.KCJSCallback;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zihong
 *
 */
public class KCArgList
{
    private List<KCArg> mArgs = new ArrayList<KCArg>(2);

    public int size()
    {
        return mArgs.size();
    }

    public boolean addArg(KCArg aArg)
    {
        if (aArg == null) return false;
        mArgs.add(aArg);
        return true;
    }
    
    public boolean has(String aKey)
    {
    	return getObject(aKey) != null ? true : false;
    }

    public KCArg get(int aIndex)
    {
        return mArgs.get(aIndex);
    }

    @Deprecated
    public Object getArgValue(String aKey)
    {
        return getObject(aKey);
    }
    @Deprecated
    public Object getArgValue(int aIndex)
    {
        return get(aIndex);
    }
    @Deprecated
    public String getArgValueString(String aKey)
    {
        return getString(aKey);
    }
    @Deprecated
    public String getArgValueString(int aIndex)
    {
        return getString(aIndex);
    }


    public Object getObject(String aKey)
    {
        Object obj = null;
        if (aKey != null)
        {
            int count = mArgs.size();
            for (int i = 0; i < count; ++i)
            {
                KCArg arg = mArgs.get(i);
                if (arg != null && arg.getArgName().equals(aKey))
                {
                    obj = arg.getValue();
                    break;
                }
            }
        }

        return obj;
    }

    public Object getObject(int aIndex)
    {
        KCArg arg = get(aIndex);
        return arg != null ? arg.getValue() : null;
    }

    public String getString(String aKey)
    {
    	Object value = getObject(aKey);
        return value == null ? null : value.toString();
    }

    public String getString(int aIndex)
    {
        Object value = getObject(aIndex);
        return value == null ? null : value.toString();
    }

    public KCJSCallback getCallback()
    {
        KCJSCallback callback = (KCJSCallback)getObject(KCJSDefine.kJS_callbackId);
        return callback;
    }

//    boolean isNull(int index)
//    {
//
//    }

    public boolean getBoolean(int aIndex)
    {
        String obj = getString(aIndex);
        return Boolean.parseBoolean(obj);
    }
    public boolean getBoolean(String aKey)
    {
    	String obj = getString(aKey);
    	return Boolean.parseBoolean(obj);
    }

    double getDouble(int aIndex)
    {
        String obj = getString(aIndex);
        return Double.parseDouble(obj);
    }
    
    public int getInt(int aIndex)
    {
        String obj = getString(aIndex);
        return Integer.parseInt(obj);
    }
    public int getInt(String aKey)
    {
    	String obj = getString(aKey);
    	return Integer.parseInt(obj);
    }
    

//    Array getArray(int index);
//    Map getMap(int index);
//     KCType getType(int index);

    Class<?> getType(int aIndex)
    {
        KCArg arg = get(aIndex);
        if (arg != null)
            return arg.getType();
        return null;
    }

    Class<?>[] getTypes()
    {
        int lengh = size();
        Class<?>[] types = new Class[lengh];

        for (int i = 0; i < lengh; ++i)
        {
            KCArg arg = get(i);
            if (arg != null)
                types[i] = arg.getType();
        }
        return  types;
    }

    @Override
    public String toString()
    {
        return mArgs.toString();
    }
}
