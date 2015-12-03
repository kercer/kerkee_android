package com.kercer.kerkee.bridge.type;

/**
 * Created by zihong on 15/12/3.
 */
public class KCJSString implements KCJSType
{
    private String mString = "";
    public KCJSString(String aString)
    {
        if(aString != null)
            mString = aString;
    }

    public static KCJSString string(final String aString)
    {
        return new KCJSString(aString);
    }

    public String toString()
    {
        return mString;
    }

}
