package com.kercer.kerkee.webview;

import com.kercer.kercore.debug.KCLog;
import com.kercer.kercore.io.KCUtilIO;
import com.kercer.kerkee.util.KCNativeUtil;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author zihong
 *
 */
public class KCUrlMapper
{
    // this map stores the mappings, a more succinct URL maps to a verbose URL
    private Map<String, String> mUrlMappingMap = new HashMap<String, String>();
    private String mResRootPath;
    // the regex pattern to match requested URL and extract the path of the URL(excluding the mRootPath)
    private Pattern mRequestUrlPattern;
    private List<AliasPatternAndMappingTarget> mUrlAliasPatternList = new ArrayList<AliasPatternAndMappingTarget>();

    public KCUrlMapper(final String aResRootPath, final String aCfgPath)
    {
        mResRootPath = aResRootPath;
        // '\\??' to match the character '?', the question mark
        mRequestUrlPattern = Pattern.compile(mResRootPath + "([^?]*)\\??(.+)*");

        //use native,it can load util lib???
        if (KCNativeUtil.fileExists(aCfgPath))
        {
            initMappingConfigurations(aCfgPath);
        }
    }

    private void initMappingConfigurations(String aCfgPath)
    {
        BufferedReader br = null;
        try
        {
            final Pattern mappingEntryPattern = Pattern.compile("(?: |\t)+");

            final Pattern replaceDotPattern = Pattern.compile("\\.");
            final String dotReplacement = "\\\\.";
            final Pattern replaceAsteriskPattern = Pattern.compile("\\*");
            final String asteriskReplacement = "[^/]+";

            br = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(aCfgPath)), "utf-8"));
            String line;
            while ((line = br.readLine()) != null)
            {
                line = line.trim();
                if (!line.startsWith("#"))
                {
                    String[] arr = mappingEntryPattern.split(line);
                    if (arr.length >= 2)
                    {
                        // if the alias url contains an '*', the url should be treated as a regex pattern
                        // For example: '/hello/*.html'  ->  '/hello/allpages.html'
                        // for this rule, '/hello/world.html', '/hello/android.html' will map to '/hello/allpages.html'
                        if (arr[0].indexOf('*') != -1)
                        {
                            arr[0] = replaceDotPattern.matcher(arr[0]).replaceAll(dotReplacement);
                            arr[0] = replaceAsteriskPattern.matcher(arr[0]).replaceAll(asteriskReplacement);
                            // precompile all the wildcard patterns
                            mUrlAliasPatternList.add(new AliasPatternAndMappingTarget(Pattern.compile(arr[0]), arr[1]));
                        }
                        else
                        {
                            mUrlMappingMap.put(arr[0], arr[1]);
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            KCLog.e(e);
        }
        finally
        {
            KCUtilIO.closeSilently(br);
        }
    }

    public String lookup(String aUrl)
    {
        Matcher mapMatcher = mRequestUrlPattern.matcher(aUrl);
        if (mapMatcher.find())
        {
            // get the path, excluding the mRootPath
            String originalPath = mapMatcher.group(1);
            String mappedPath = mUrlMappingMap.get(originalPath);

            // if mappedPath is null, we should try wildcards
            if (mappedPath == null && mUrlAliasPatternList.size() > 0)
            {
                // test against the regex list for matching
                for (int i = 0; i < mUrlAliasPatternList.size(); ++i)
                {
                    AliasPatternAndMappingTarget aliasPatternAndMappingTarget = mUrlAliasPatternList.get(i);
                    if (aliasPatternAndMappingTarget.pattern.matcher(originalPath).matches())
                    {
                        mappedPath = aliasPatternAndMappingTarget.target;
                        break;
                    }
                }
            }

            if (mappedPath != null)
            {
                String params = mapMatcher.group(2);
                boolean isMappedToHttp = mappedPath.startsWith("http");

                // if the old url contains parameters
                if (params != null)
                {
                    // if the replacement contains parameters
                    if (mappedPath.lastIndexOf('?') != -1)
                    {
                        if (isMappedToHttp)
                            aUrl = mapMatcher.replaceFirst(mappedPath + "&" + params);
                        else
                            aUrl = mapMatcher.replaceFirst(mResRootPath + mappedPath + "&" + params);
                    }
                    else
                    {
                        if (isMappedToHttp)
                            aUrl = mapMatcher.replaceFirst(mappedPath + "?" + params);
                        else
                            aUrl = mapMatcher.replaceFirst(mResRootPath + mappedPath + "?" + params);
                    }
                }
                else
                {
                    if (!isMappedToHttp)
                        aUrl = mapMatcher.replaceFirst(mResRootPath + mappedPath);
                    else
                        aUrl = mappedPath;
                }
            }

        }

        return aUrl;
    }

    class AliasPatternAndMappingTarget
    {
        AliasPatternAndMappingTarget(Pattern pattern, String target)
        {
            this.pattern = pattern;
            this.target = target;
        }

        public Pattern pattern;
        public String target;
    }
}
