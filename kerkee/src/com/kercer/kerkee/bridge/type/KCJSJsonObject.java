package com.kercer.kerkee.bridge.type;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by zihong on 15/12/3.
 */
public class KCJSJsonObject implements KCJSType
{
    JSONObject mJsonObject;

    public KCJSJsonObject()
    {
        mJsonObject = new JSONObject();
    }
    public KCJSJsonObject(JSONObject aJsonObject)
    {
        mJsonObject = aJsonObject;
    }

    public KCJSJsonObject(Map aCopyFrom)
    {
        mJsonObject = new JSONObject(aCopyFrom);
    }

    public String toString()
    {
        return mJsonObject != null ? mJsonObject.toString() : null;
    }
}
