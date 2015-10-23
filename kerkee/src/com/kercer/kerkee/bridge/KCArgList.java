package com.kercer.kerkee.bridge;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zihong
 *
 */
public class KCArgList
{
    private List<KCArg> mArgs = new ArrayList<KCArg>();
//    private HashMap<String, KCArg> mArgs = new HashMap<String, KCArg>();


    public int size()
    {
        return mArgs.size();
    }

    public boolean addArg(KCArg aArg)
    {
        if (aArg == null) return false;
//        String key = aArg.getArgName();
//        mArgs.put(key, aArg);
        mArgs.add(aArg);
        return true;
    }

    public KCArg get(int aIndex)
    {
        return mArgs.get(aIndex);
    }

    public Object getArgValue(int aIndex)
    {
        KCArg arg = get(aIndex);
        return arg != null ? arg.getValue() : null;
    }

    public Object getArgValue(String aKey)
    {
        Object obj = null;
        if (aKey != null)
        {
//            KCArg arg = mArgs.get(aKey);
//            if (arg != null)
//                obj = arg.getValue();
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

    public String getArgValueString(String aKey)
    {
    	Object value = getArgValue(aKey);
        return value == null ? null : value.toString();
    }

    public String getArgValueString(int aIndex)
    {
        Object value = getArgValue(aIndex);
        return value == null ? null : value.toString();
    }


//    boolean isNull(int index)
//    {
//
//    }

    boolean getBoolean(int aIndex)
    {
        String obj = getArgValueString(aIndex);
        return Boolean.parseBoolean(obj);
    }

    double getDouble(int aIndex)
    {
        String obj = getArgValueString(aIndex);
        return Double.parseDouble(obj);
    }
    int getInt(int aIndex)
    {
        String obj = getArgValueString(aIndex);
        return Integer.parseInt(obj);
    }
    String getString(int aIndex)
    {
        return getArgValueString(aIndex);
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

    @Override
    public String toString()
    {
        return mArgs.toString();
    }
}
