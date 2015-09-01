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
    
    public KCArg(String aName, Object aValue)
    {
        mName = aName;
        mObject = aValue;
    }
    
    public String getArgName()
    {
        return mName;
    }
    
    public Object getValue()
    {
        return mObject;
    }
    
    @Override
    public String toString()
    {
        return mName + ":" + mObject.toString();
    }
    
}
