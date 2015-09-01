package com.kercer.kerkee.net.uri;

import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 * 
 * @author zihong
 *
 */
public class KCUtilURI
{

    final static String ALPHA = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    final static String DIGIT = "0123456789";
    final static String HEXDIGIT = "abcdefABCDEF" + DIGIT;
    final static String GENDELIMS = ":/?#[]@";
    final static String SUBDELIMS = "!$&'()*+;=";

    final static String UNRESERVED = ALPHA + DIGIT + "-._~";
    final static String RESERVED = GENDELIMS + SUBDELIMS;
    final static String PERCENT = "%";
    final static String PCHAR = UNRESERVED + SUBDELIMS + PERCENT;

    final static String QUERY = PCHAR + "/" + "?";
    final static String REGNAME = UNRESERVED + PERCENT + SUBDELIMS;

    final static Pattern sPercentEncodingPattern = Pattern.compile("(?:%([0-9a-fA-F]{2}))");

    public static String normalize(String aQuery, String aCharList)
    {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < aQuery.length(); i++)
        {
            result.append(getPercentEncodedChar(aQuery.charAt(i), aCharList));
        }
        return result.toString();
    }

    public static String normalizeString(String aText, boolean aIgnoreCase) throws URISyntaxException
    {
        StringBuffer result = new StringBuffer(aIgnoreCase ? aText : aText.toLowerCase());
        try
        {
            int index = 0;
            int found = 0;
            while ((found = result.indexOf("%", index)) != -1)
            {
                if (found < result.length() - 2)
                {
                    String hexString = result.substring(found + 1, found + 3);
                    String replaced = KCUtilURI.getPercentEncodedChar(hexString, aIgnoreCase);
                    result.replace(found, found + 3, replaced);
                }
                index++;
            }
        }
        catch (Exception e)
        {
            throw new URISyntaxException(aText, "Failed to normalize string");
        }
        return result.toString();
    }

    private static String getPercentEncodedChar(String aHexValue, boolean aIgnoreCase)
    {
        int value = Integer.parseInt(aHexValue, 16);
        int index = 0;
        if ((index = UNRESERVED.indexOf(value)) != -1)
        {
            String c = "" + UNRESERVED.charAt(index);
            return (aIgnoreCase) ? c : c.toLowerCase();
        }
        return "%" + aHexValue.toUpperCase();
    }

    public static String getPercentEncodedChar(char aChar, final String aCharList)
    {
        int index = aCharList.indexOf(aChar);
        if (index == -1)
        {
            int value = (int) aChar;
            return ("%" + (value < 16 ? "0" : "") + Integer.toHexString(value)).toUpperCase();
        }
        return String.valueOf(aChar);
    }

    /**
     * TODO refactor this algorithm, it's a bit chunky and not so readable
     * 
     * @param aPath
     * @return
     */
    public static String removeDotSegments(String aPath)
    {
        Stack<String> output = new Stack<String>();
        String input = new String(aPath);

        while (!input.isEmpty())
        {
            if (input.startsWith("../") || input.startsWith("./"))
            {
                input = input.substring(input.indexOf("/") + 1);
            }
            else if (input.startsWith("/.."))
            {
                input = input.substring(3);
                if (!output.isEmpty())
                {
                    output.pop();
                }
            }
            else if (input.equals("/."))
            {
                input = "/";
            }
            else if (input.startsWith("/."))
            {
                input = input.substring(2);
            }
            else if (input.equals(".") || input.equals(".."))
            {
                input = "";
            }
            else
            {
                int segmentIndex = input.indexOf("/", 1);
                if (segmentIndex == -1)
                {
                    segmentIndex = input.length();
                }

                String pathSegment = input.substring(0, segmentIndex);
                input = input.substring(segmentIndex);
                if (!pathSegment.isEmpty())
                {
                    output.push(pathSegment);
                }
            }
        }

        return join(output);
    }

    public static String join(final Iterable<String> aContainer)
    {
        return join(aContainer, "");
    }

    /**
     * Joins a collection of strings to a single string, all parts are merged with a delimiter string.
     * 
     * @param aContainer
     * @param aDelimiter
     * @return
     */
    public static String join(final Iterable<String> aContainer, final String aDelimiter)
    {
        StringBuilder builder = new StringBuilder();
        Iterator<String> it = aContainer.iterator();
        while (it.hasNext())
        {
            builder.append(it.next());
            if (it.hasNext())
                builder.append(aDelimiter);
        }
        return builder.toString();
    }
}
