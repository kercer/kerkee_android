package com.kercer.kerkee.bridge;

/**
 * Created by zihong on 15/10/19.
 */
public class KCJSError
{
    protected String mName;
    protected String mMessage;
    protected int mErrorCode;

    public KCJSError(final String aMessage)
    {
        this(null, 0, aMessage);
    }
    public KCJSError(final String aName, final int aErrorCode, final String aMessage)
    {
        mName = aName;
        mMessage = aMessage;
        mErrorCode = aErrorCode;
    }

    public String toString()
    {
        return mMessage == null ? "" : mMessage;
    }
}
