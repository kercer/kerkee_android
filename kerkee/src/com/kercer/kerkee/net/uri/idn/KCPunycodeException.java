package com.kercer.kerkee.net.uri.idn;


/**
 * 
 * @author zihong
 *
 */
@SuppressWarnings("serial")
public class KCPunycodeException extends Exception
{
    public static String OVERFLOW = "Overflow";
    public static String BAD_INPUT = "Bad Input";

    public KCPunycodeException(String aMessage)
    {
        super(aMessage);
    }
}
