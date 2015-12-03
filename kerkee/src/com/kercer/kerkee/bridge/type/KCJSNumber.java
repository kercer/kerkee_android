package com.kercer.kerkee.bridge.type;

/**
 * Created by zihong on 15/12/3.
 */
public class KCJSNumber implements KCJSType
{
    private Number mNumber;

    public KCJSNumber(int aValue)
    {
        mNumber = new Integer(aValue);
    }

    public KCJSNumber(double aValue)
    {
        mNumber = new Double(aValue);
    }

    public KCJSNumber(float aValue)
    {
        mNumber = new Float(aValue);
    }

    public String toString()
    {
        return mNumber.toString();
    }

}
