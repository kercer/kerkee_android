package com.kercer.kerkee.net.uri;

/**
 * 
 * @author zihong
 *
 */
public class KCNameValuePair implements Comparable<KCNameValuePair>
{
    public final String mKey;
    public final String mValue;

    public KCNameValuePair(String aKey, String aValue)
    {
        this.mKey = aKey;
        this.mValue = aValue;
    }

    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append((mKey != null) ? mKey : "");
        result.append((mKey != null && mValue != null) ? '=' : "");
        result.append((mValue != null) ? mValue : "");
        return KCUtilURI.normalize(result.toString(), KCUtilURI.QUERY);
    }

    @Override
    public int compareTo(KCNameValuePair aRhs)
    {
        int compare = mKey.compareTo(aRhs.mKey);
        if (compare != 0)
        {
            return compare;
        }
        return mValue.compareTo(aRhs.mValue);
    }
}
