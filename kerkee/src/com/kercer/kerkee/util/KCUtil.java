package com.kercer.kerkee.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
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

    /**
     * 获取线程安全的StringBuilder
     * **/
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

    public static void closeCloseable(Closeable obj)
    {
        try
        {
            if (obj != null)
                obj.close();
        }
        catch (IOException e)
        {
            //            e.printStackTrace();
        }
    }


    /**
     * 获取md5值
     * @param str   需要md5的字符串
     * @return md后的字符串
     * **/
    public static String getMD5String(String str)
    {
        try
        {
            //            if (Application.versionCode > 7)
            //            {//因为之前java版md5获取有误，兼容前面版本
            return KCNativeUtil.getMd5(str);
            //            }
            //            else
            //            {
            //                return getMD5String(str.getBytes("utf-8"));
            //            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Deprecated
    /**
     * 此方法获取md5有错误
     * 推荐：KCNativeUtil.getMd5(str);
     */
    public static String getMD5String(byte[] bytes)
    {
        try
        {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(bytes);
            byte md5Data[] = md.digest();
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < md5Data.length; ++i)
                hexString.append(Integer.toHexString(0xFF & md5Data[i]));
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 替换url里的空格
     * @param url   url字符串
     * @return 替换后的结果
     * **/
    public static String replacePlusWithPercent20(String url)
    {
        if (url.contains("+"))
        {
            url = PATTERN_PLUS.matcher(url).replaceAll("%20");
        }
        return url;
    }



    public static String readInputStreamAsString(InputStream is) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes = new byte[4096];
        int lenRead;
        while ((lenRead = is.read(bytes)) != -1)
        {
            if (lenRead > 0)
                baos.write(bytes, 0, lenRead);
        }

        if (baos.size() > 0)
            return baos.toString("utf-8");
        return null;
    }
}
