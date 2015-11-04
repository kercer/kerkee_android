package com.kercer.kerkee.bridge.type;

import org.json.JSONObject;

/**
 * Created by zihong on 15/10/28.
 */
public class KCJSNull implements KCJSType
{
    public static final KCJSNull NULL = new KCJSNull();

    public static boolean isNull(Object value)
    {
        return value == null || value == NULL || value == JSONObject.NULL;
    }

    @Override public boolean equals(Object o)
    {
        return o == this || o == null; // API specifies this broken equals implementation
    }
    @Override public String toString()
    {
        return null;
    }


}
