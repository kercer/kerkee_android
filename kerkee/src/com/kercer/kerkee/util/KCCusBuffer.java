package com.kercer.kerkee.util;

/**
 * 
 * @author zihong
 *
 */
public class KCCusBuffer
{
    private byte[] buffer;
    private int nBufferLen;

    public KCCusBuffer()
    {
    }

    public byte[] getBuffer()
    {
        return buffer;
    }

    public void setBuffer(byte[] buffer)
    {
        this.buffer = buffer;
    }

    public int getnBufferLen()
    {
        return nBufferLen;
    }

    public void setnBufferLen(int nBufferLen)
    {
        this.nBufferLen = nBufferLen;
    }
}
