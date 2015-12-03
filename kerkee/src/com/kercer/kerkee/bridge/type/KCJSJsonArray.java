package com.kercer.kerkee.bridge.type;

import org.json.JSONArray;

/**
 * Created by zihong on 15/12/3.
 */
public class KCJSJsonArray implements KCJSType
{
    JSONArray mJsonArray;

    public KCJSJsonArray(JSONArray aJsonArray)
    {
        mJsonArray = aJsonArray;
    }

    public String toString()
    {
        return mJsonArray != null ? mJsonArray.toString() : null;
    }
}
