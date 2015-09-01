package com.kercer.kerkee.net.uri;

import java.net.IDN;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author zihong
 *
 */
public class KCSimpleIDN
{

    public static final String ACE_PREFIX = "xn--";

    private static final String UTF8_REGEX = "\\A(?:" + "[\\x09\\x0A\\x0D\\x20-\\x7E]]" + // ASCII characters
            "|[\\xC2-\\xDF][\\x80-\\xBF]" + // non-overlong 2-byte
            "|\\xE0[\\xA0-\\xBF][\\x80-\\xBF]" + // excluding overlongs
            "|[\\xE1-\\xEC\\xEE\\xEF][\\x80-\\xBF]{2}" + // straight 3-byte
            "|\\xED[\\x80-\\x9F][\\x80-\\xBF]" + // excluding surrogates
            "|\\xF0[\\x90-\\xBF][\\x80-\\xBF]{2}" + // planes 1-3
            "|[\\xF1-\\xF3][\\x80-\\xBF]{3}" + // planes 4-5
            "|\\xF4[\\x80-\\x8F][\\x80-\\xBF]{2}" + // plane 16
            ")*\\Z/mnx";

    private static final String UTF8_REGEX_MULTIBYTE = "\\A(?:" + "[\\xC2-\\xDF][\\x80-\\xBF]" + // non-overlong 2-byte
            "|\\xE0[\\xA0-\\xBF][\\x80-\\xBF]" + // excluding overlongs
            "|[\\xE1-\\xEC\\xEE\\xEF][\\x80-\\xBF]{2}" + // straight 3-byte
            "|\\xED[\\x80-\\x9F][\\x80-\\xBF]" + // excluding surrogates
            "|\\xF0[\\x90-\\xBF][\\x80-\\xBF]{2}" + // planes 1-3
            "|[\\xF1-\\xF3][\\x80-\\xBF]{3}" + // planes 4-5
            "|\\xF4[\\x80-\\x8F][\\x80-\\xBF]{2}" + // plane 16
            ")\\Z/mnx";

    // reg ex used to find all dot characters
    private static final String DOTS_REGEX = "(?:[\\x2E]|[\\x3002]|[\\xFF0E]|[\\xFF61])";

    private static final Pattern UTF8_PATTERN = Pattern.compile(UTF8_REGEX);
    private static final Pattern UTF8_MULTI_PATTERN = Pattern.compile(UTF8_REGEX_MULTIBYTE);

    /**
     * A basic implementation of the toASCII function as described in section 4.1 of the RFC 3490
     * (see http://www.ietf.org/rfc/rfc3490.txt for more details)
     * 
     * The implementation currently uses the IDN class of Java SE
     * 
     * @param aLabel
     * @return
     */
    public static String toASCII(final String aLabel)
    {
        if (isUTF8Label(aLabel))
        {
            String[] parts = splitParts(downcase(aLabel));
            Vector<String> result = new Vector<String>();
            for (String part : parts)
            {
                if (isUTF8Label(part))
                {
                    result.add(IDN.toASCII(part));
                }
                else
                {
                    result.add(part);
                }
            }
            return KCUtilURI.join(result, ".");
        }
        return aLabel;
    }

    /**
     * A basic implementation of the toUnicode function as described in section 4.2 of the RFC 3490
     * (see http://www.ietf.org/rfc/rfc3490.txt for more details)
     * 
     * The implementation currently uses the IDN class of Java SE
     * 
     * @param aLabel
     * @return
     */
    public static String toUnicode(final String aLabel)
    {
        try
        {
            String[] parts = splitParts(aLabel);
            Vector<String> result = new Vector<String>();
            for (String part : parts)
            {
                if (part.startsWith(ACE_PREFIX))
                {
                    result.add(IDN.toUnicode(part));
                }
                else
                {
                    result.add(part);
                }
            }
            return KCUtilURI.join(result, ".");
        }
        catch (Exception e)
        {
            return aLabel;
        }
    }

    public static String[] splitParts(String aInput)
    {
        return aInput.split(DOTS_REGEX);
    }

    /**
     * Checks if the given input label is an UTF-8 string.
     * 
     * @param aLabel
     * @return
     */
    private static boolean isUTF8Label(String aLabel)
    {
        Matcher utf8Matcher = UTF8_PATTERN.matcher(aLabel);
        Matcher utf8MultiMatcher = UTF8_MULTI_PATTERN.matcher(aLabel);
        return (utf8Matcher.find() && utf8MultiMatcher.find());
    }

    private static String downcase(String aInput)
    {
        return aInput.toLowerCase();
    }
}
