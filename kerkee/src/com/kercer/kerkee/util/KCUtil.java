package com.kercer.kerkee.util;

import com.kercer.kercore.debug.KCLog;

import java.util.regex.Pattern;

/**
 *
 * @author zihong
 *
 */
public class KCUtil
{
    private final static ThreadLocal<StringBuilder> threadSafeStrBuilder = new ThreadLocal<StringBuilder>();
    private final static ThreadLocal<byte[]> threadSafeByteBuf = new ThreadLocal<byte[]>();

    private final static Pattern PATTERN_PLUS = Pattern.compile("\\+");

    public static StringBuilder getThreadSafeStringBuilder()
    {
        StringBuilder sb = threadSafeStrBuilder.get();
        if (sb == null)
        {
            sb = new StringBuilder();
            threadSafeStrBuilder.set(sb);
        }
        sb.delete(0, sb.length());
        return sb;
    }

    public static byte[] getThreadSafeByteBuffer()
    {
        byte[] buf = threadSafeByteBuf.get();
        if (buf == null)
        {
            buf = new byte[1024 * 4]; // 4kb
            threadSafeByteBuf.set(buf);
        }
        return buf;
    }


    public static String getMD5String(String str)
    {
        try
        {
            return KCNativeUtil.getMd5(str);
        }
        catch (Exception e)
        {
            KCLog.e(e);
        }
        return null;
    }


    public static String replacePlusWithPercent20(String url)
    {
        if (url.contains("+"))
        {
            url = PATTERN_PLUS.matcher(url).replaceAll("%20");
        }
        return url;
    }




}
