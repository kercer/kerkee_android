package com.kercer.kerkee.bridge;

/**
 *
 * @author zihong
 *
 */
public class KCArg
{
    private String mName;
    private Object mObject;
    private Class<?> mType;

    public KCArg(String aName, Object aValue, Class<?> aType)
    {
        mName = aName;
        mObject = aValue;
        mType = aType;
    }

    public KCArg(String aName, Object aValue)
    {
        this(aName, aValue, aValue.getClass());
    }

    public String getArgName()
    {
        return mName;
    }

    public Object getValue()
    {
        return mObject;
    }

    public Class<?> getType()
    {
        return mType;
    }

    @Override
    public String toString()
    {
        return mName + ":" + mObject.toString();
    }

}
