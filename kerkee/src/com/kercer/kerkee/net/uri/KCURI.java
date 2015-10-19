package com.kercer.kerkee.net.uri;

import java.net.IDN;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author zihong
 *
 */
public class KCURI
{

    private final static Map<String, Integer> DefaultPortMap = new HashMap<String, Integer>();

    private final static char DEFAULT_DELIMITER = '&';

    private final static String ALPHA = "a-zA-Z";
    private final static String DIGIT = "0-9";
    private final static String HEX = "a-fA-F0-9";
    private final static String UNRESERVED = ALPHA + DIGIT + "-._~";
    private final static String SUBDELIM = "!$&'()*+,;=";
    private final static String COMMON = UNRESERVED + SUBDELIM;
    private final static String PERCENT = "%[" + HEX + "]{2}";

    private final static String sRegExUserInfo = "(?:[" + COMMON + ":]|" + PERCENT + ")*";
    private final static String sRegExScheme = "^([" + ALPHA + "]+[" + ALPHA + DIGIT + "+-.]*)";

    private final static String sRegExNamedHost = "(?:[^\\[\\]:/?#])*";
    private final static String sRegExIPV6Host = "\\[[" + HEX + ":.]+\\]";
    private final static String sRegExIPFuture = "(?:\\[v[" + HEX + ".]+[" + COMMON + ":]+\\])";
    private final static String sRegExHost = "(" + sRegExNamedHost + "|" + sRegExIPV6Host + "|" + sRegExIPFuture + ")?";

    private final static String sRegExRequestURI = "(?:([^?#]*))?(?:\\?([^#]*))?";

    private final static String sRegExAuthority = "\\A^" + "(?:([^\\[\\]]*)@)?" + // user info
            sRegExHost + "?" + // host
            "(?::([^:@\\[\\]]*))?" + // port
            "$\\Z";
    private final static String sRegExURI = "\\A" + "(?:([^:/?#]+):)?" + // scheme
            "(?:\\/\\/([^\\/?#]*))?" + // authority
            "(?:([^?#]*))?" + // path
            "(?:\\?([^#]*))?" + // query string
            "(?:#(.*))?" + // fragment
            "\\Z";

    private final static Pattern sURIPattern;
    private final static Pattern sAuthorityPattern;

    private final static Pattern sUserInfoPattern;
    private final static Pattern sSchemePattern;
    private final static Pattern sRequestURIPattern;

    private final static Pattern sNamedHostPattern;
    private final static Pattern sIPV6HostPattern;
    private final static Pattern sIPFuturePattern;

    private final Vector<KCNameValuePair> mQueries = new Vector<KCNameValuePair>();
    private String mScheme = null;
    private String mUsername = null;
    private String mUserpass = null;
    private String mHost = null;
    private int mPort = -1;
    private String mPath = null;
    private String mFragment = null;

    private List<String> mPathSegments = null;

    private char mDelimiter = DEFAULT_DELIMITER;

    static
    {
        sURIPattern = Pattern.compile(sRegExURI);
        sAuthorityPattern = Pattern.compile(sRegExAuthority);
        sUserInfoPattern = Pattern.compile(sRegExUserInfo);
        sSchemePattern = Pattern.compile(sRegExScheme);
        sRequestURIPattern = Pattern.compile(sRegExRequestURI);

        sNamedHostPattern = Pattern.compile(sRegExNamedHost);
        sIPV6HostPattern = Pattern.compile(sRegExIPV6Host);
        sIPFuturePattern = Pattern.compile(sRegExIPFuture);

        DefaultPortMap.put("acap", 674);
        DefaultPortMap.put("dict", 2628);
        DefaultPortMap.put("ftp", 21);
        DefaultPortMap.put("go", 1096);
        DefaultPortMap.put("gopher", 70);
        DefaultPortMap.put("http", 80);
        DefaultPortMap.put("https", 443);
        DefaultPortMap.put("icap", 1344);
        DefaultPortMap.put("ldap", 389);
        DefaultPortMap.put("mupdate", 3905);
        DefaultPortMap.put("nntp", 119);
        DefaultPortMap.put("nntps", 563);
        DefaultPortMap.put("prospero", 1525);
        DefaultPortMap.put("rsync", 873);
        DefaultPortMap.put("rtsp", 554);
        DefaultPortMap.put("snmp", 161);
        DefaultPortMap.put("telnet", 23);
        DefaultPortMap.put("vemmi", 575);
        DefaultPortMap.put("wais", 210);
        DefaultPortMap.put("ws", 80);
        DefaultPortMap.put("wss", 443);
    }

    private static boolean isDefined(String aInput)
    {
        return (aInput != null && !aInput.isEmpty());
    }

    /**
     * See section 5.2.3 of RFC 3986 for more details
     *
     * @param basePath
     * @param relativePath
     * @return
     */
    private static String mergePath(KCURI aBaseURI, KCURI aReferenceURI)
    {
        String basePath = (aBaseURI.mPath == null || aBaseURI.mPath.isEmpty()) ? "" : aBaseURI.mPath;

        if (!aBaseURI.authority().isEmpty() && basePath.isEmpty())
        {
            basePath = "/";
        }
        else
        {
            // remove the rightmost path segment from the base path (if available)
            int index = basePath.lastIndexOf('/');
            if (index != -1)
            {
                basePath = basePath.substring(0, index + 1);
            }
            else
            {
                basePath = "";
            }
        }

        if (aReferenceURI.mPath != null)
        {
            basePath += aReferenceURI.mPath;
        }

        return basePath;
    }

    private static String toUserInfo(String aUsername, String aUserpass)
    {
        String userinfo = "";
        userinfo += (isDefined(aUsername)) ? aUsername : "";
        userinfo += (isDefined(aUserpass)) ? ":" + aUserpass : "";
        return userinfo;
    }

    public static KCURI parse(String aUrl) throws URISyntaxException
    {
        Matcher matcher = sURIPattern.matcher(aUrl);
        if (matcher.find())
        {
            String scheme = matcher.group(1);
            String authority = matcher.group(2);
            String path = matcher.group(3);
            String query = matcher.group(4);
            String fragment = matcher.group(5);
            KCURI uri = new KCURI().withScheme(scheme).withAuthority(authority).withPath(path).withQuery(query).withFragment(fragment);
            return uri;
        }
        throw new URISyntaxException(aUrl, "Some components could not be parsed!");
    }

    public static KCURI parseURL(URL aUrl) throws URISyntaxException
    {
        KCURI uri = new KCURI().withScheme(aUrl.getProtocol()).withUserInfo(aUrl.getUserInfo()).withHost(aUrl.getHost()).withPort(aUrl.getPort()).withPath(aUrl.getPath()).withQuery(aUrl.getQuery());
        return uri;
    }

    public KCURI()
    {
    }

    public boolean equals(Object aObj)
    {
        if (aObj == null)
        {
            return false;
        }
        else if (!(aObj instanceof String || aObj instanceof KCURI))
        {
            return false;
        }

        // get URI from Object
        KCURI uri = null;
        if (aObj instanceof String)
        {
            try
            {
                uri = KCURI.parse((String) aObj);
            }
            catch (URISyntaxException e)
            {
                return false;
            }
        }
        else
        {
            uri = (KCURI) aObj;
        }

        return toString().equals(uri.toString());
    }

    public int hashCode()
    {
        return toString().hashCode();
    }

    public String toString()
    {
        try
        {
            return toASCII();
        }
        catch (URISyntaxException e)
        {
        }
        return recompose();
    }

    public KCURI withAuthority(String aAuthority) throws URISyntaxException
    {
        parseAuthority(aAuthority);
        return this;
    }

    public KCURI withHost(String aHost) throws URISyntaxException
    {
        this.mHost = null;
        parseHost(aHost);
        return this;
    }

    private KCURI withUserInfo(String aUserInfo) throws URISyntaxException
    {
        mUsername = null;
        mUserpass = null;
        parseUserInfo(aUserInfo);
        return this;
    }

    public KCURI withUserInfo(String aUserName, String aUserPass) throws URISyntaxException
    {
        return withUserInfo(toUserInfo(aUserName, aUserPass));
    }

    public KCURI withScheme(String aScheme) throws URISyntaxException
    {
        this.mScheme = null;
        parseScheme(aScheme);
        return this;
    }

    public KCURI withPort(int aPort) throws URISyntaxException
    {
        this.mPort = -1;
        parsePort(aPort);
        return this;
    }

    private KCURI withPort(String aPort) throws URISyntaxException
    {
        this.mPort = -1;
        if (!isDefined(aPort))
            return this;

        try
        {
            int portNumber = Integer.parseInt(aPort);
            return withPort(portNumber);
        }
        catch (NumberFormatException e)
        {
            throw new URISyntaxException(aPort, "Invalid port specified");
        }
    }

    public KCURI withPath(String aPath) throws URISyntaxException
    {
        this.mPath = null;
        parsePath(aPath);
        return this;
    }

    public KCURI withFragment(String aFragment)
    {
        this.mFragment = null;
        parseFragment(aFragment);
        return this;
    }

    public KCURI withQuery(String aQuery)
    {
        parseQuery(aQuery);
        return this;
    }

    /**
     * Sets the request URI component consisting of path and query parameters, e.g.
     * 'path/to/resource?q=all&search=foo'
     *
     * @param aRequest
     * @return
     * @throws URISyntaxException
     */
    public KCURI withRequestURI(String aRequest) throws URISyntaxException
    {
        mPath = null;
        mQueries.clear();
        parseRequestURI(aRequest);
        return this;
    }

    public KCURI addParam(String aKey, String aValue)
    {
        parseQuery(aKey, aValue);
        return this;
    }

    /**
     * remove the first aKey in mQueries
     * @param aKey
     * @return
     */
    public KCURI removeParam(String aKey)
    {
        aKey = (aKey != null && !aKey.isEmpty()) ? aKey : null;
        if(aKey != null)
        {
            int count = mQueries.size();
            for (int i = 0; i < count; ++i)
            {
                KCNameValuePair pair =  mQueries.get(i);
                if (aKey.equals(pair.mKey))
                {
                    mQueries.remove(i);
                    break;
                }
            }
        }
        return this;
    }

    public KCURI removeParamAll(String aKey)
    {
        aKey = (aKey != null && !aKey.isEmpty()) ? aKey : null;
        if(aKey != null)
        {
            for (int i = 0; i < mQueries.size(); ++i)
            {
                KCNameValuePair pair =  mQueries.get(i);
                if (aKey.equals(pair.mKey))
                {
                    mQueries.remove(i);
                    i--;
                }
            }
        }
        return this;
    }

    public void queryDelimiter(char aDelimiter)
    {
        if (this.mDelimiter != aDelimiter)
        {
            this.mDelimiter = aDelimiter;
            String query = getQuery();
            parseQuery(query);
        }
    }

    public KCURI sortQuery()
    {
        Collections.sort(this.mQueries);
        return this;
    }

    public String getScheme()
    {
        return mScheme;
    }

    public String getUserName()
    {
        return mUsername;
    }

    public String getUserPass()
    {
        return mUserpass;
    }

    public String getUserInfo()
    {
        return toUserInfo(mUsername, mUserpass);
    }

    public String getHost()
    {
        return mHost;
    }

    public int getPort()
    {
        return mPort;
    }

    public String getPath()
    {
        return (mHost != null && mPath != null && !mPath.startsWith("/")) ? "/" + mPath : mPath;
    }

    /**
     * Gets the decoded path segments.
     *
     * @return decoded path segments, each without a leading or trailing '/'
     */
    public List<String> getPathSegments()
    {
        if(mPathSegments == null)
            mPathSegments = createPathSegments();
        return mPathSegments;
    }

    /**
     * Gets the decoded last segment in the path.
     *
     * @return the decoded last segment or null if the path is empty
     */
    public String getLastPathSegment()
    {
        String segment = null;
        List<String> list = getPathSegments();
        if(list.size() > 0)
        {
            segment = list.get(list.size()-1);
        }
        return segment;
    }

    /**
     * remove last path segment,if last segment is not null or the path is not empty
     * then parse path
     */
    public void removeLastPathSegment()
    {
        List<String> list = getPathSegments();
        int count = list.size();
        if(count > 0)
        {
            list.remove(count-1);
            pathSegmentsToPath();
        }
    }


    public String getQuery()
    {
        Vector<String> result = new Vector<String>();
        for (KCNameValuePair pair : mQueries)
        {
            result.add(pair.toString());
        }
        return mQueries.isEmpty() ? "" : KCUtilURI.join(result, Character.toString(mDelimiter));
    }

    public List<KCNameValuePair> getQueries()
    {
        return mQueries;
    }

    /**
     * Searches the query string for the first value with the given key.
     *
     * @param aKey which will be encoded
     * @throws UnsupportedOperationException if this isn't a hierarchical URI
     * @throws NullPointerException if key is null
     * @return the decoded value or null if no parameter is found
     */
    public String getQueryParameter(String aKey)
    {
        String value = null;
        for (KCNameValuePair pair : mQueries)
        {
            if (aKey.equals(pair.mKey))
            {
                value = pair.mValue;
                break;
            }
        }
        return value;
    }

    public String getFragment()
    {
        return mFragment;
    }

    /**
     * Returns the HTTP request URI, consisting of path and the query components of the URI.
     *
     * @return
     */
    public String requestURI()
    {
        String query = getQuery();
        String path = getPath();
        StringBuilder builder = new StringBuilder();
        builder.append(path != null ? path : "");
        builder.append(!query.isEmpty() ? "?" + query : "");
        return builder.toString();
    }

    /**
     * Returns the HTTP authority part. The authority consists of user info (if available) the
     * host (named, ipv4, ipv6 or ipfuture) and the port (if available and different from default).
     *
     * @return The authority part of the URI.
     */
    public String authority()
    {
        StringBuilder result = new StringBuilder();
        String userinfo = getUserInfo();

        result.append(userinfo != null && !userinfo.isEmpty() ? userinfo + "@" : "");
        result.append(mHost != null ? mHost : "");

        if (mPort != -1 && mPort != inferredPort())
        {
            result.append(":" + mPort);
        }
        return result.toString();
    }

    /**
     * Returns the composite site component, consisting of scheme and authority, e.g.
     * The site component of the URI 'http://user:pass@www.example.com:1234/path?query=true#fragment'
     * is 'http://user:pass@www.example.com:1234'
     *
     * @return
     */
    public String site()
    {
        String scheme = getScheme();
        String authority = authority();

        StringBuilder builder = new StringBuilder();
        builder.append(scheme != null ? scheme + ":" : "");
        if (isDefined(scheme) /*&& isDefined(authority)*/)
        {
            builder.append("//");
        }
        builder.append(authority);

        return (builder.length() > 0) ? builder.toString() : null;
    }

    public int inferredPort()
    {
        if (mScheme != null && DefaultPortMap.containsKey(mScheme))
        {
            return DefaultPortMap.get(mScheme).intValue();
        }
        return -1;
    }

    public boolean isAbsolute()
    {
        return !isRelative();
    }

    public boolean isRelative()
    {
        return (mScheme == null);
    }

    /**
     * For more details see {@link #join(URI)}
     *
     * @param aUri
     * @return
     * @throws URISyntaxException
     */
    public KCURI join(String aUri) throws URISyntaxException
    {
        return join(KCURI.parse(aUri));
    }

    /**
     * Joins the URI with another one, useful for Reference resolution, e.g. specifying a relative URI
     * to a different one, acting as Base URI.
     *
     * A relative path can only be joined when a base URI is known. A base URI must conform to the
     * absolute URI syntax (see section 4.3 of RFC 3986). A base URI is an absolute URI with scheme,
     * authority. How a base URI can be obtained of a reference is described in section 5 of RFC 3986.
     *
     * The algorithm used is taken from section 5.2.2
     *
     * @param relativePath
     * @return
     * @throws URISyntaxException
     */
    public KCURI join(KCURI aUri) throws URISyntaxException
    {
        String targetScheme = null;
        String targetAuthority = null;
        String targetPath = null;
        String targetQuery = null;
        String targetFragment = null;

        if (isDefined(aUri.mScheme))
        {
            targetScheme = aUri.mScheme;
            targetAuthority = aUri.authority();
            targetPath = aUri.mPath;
            targetQuery = aUri.getQuery();
        }
        else
        {
            if (isDefined(aUri.authority()))
            {
                targetAuthority = aUri.authority();
                targetPath = aUri.mPath;
                targetQuery = aUri.getQuery();
            }
            else
            {
                if (!isDefined(aUri.mPath))
                {
                    targetPath = this.mPath;
                    targetQuery = isDefined(aUri.getQuery()) ? aUri.getQuery() : this.getQuery();
                }
                else
                {
                    targetPath = aUri.mPath.startsWith("/") ? aUri.mPath : mergePath(this, aUri);
                    targetQuery = aUri.getQuery();
                }
                targetAuthority = this.authority();
            }
            targetScheme = this.mScheme;
        }
        targetFragment = aUri.mFragment;

        KCURI targetURI = new KCURI().withScheme(targetScheme).withAuthority(targetAuthority).withPath(targetPath).withQuery(targetQuery).withFragment(targetFragment);
        return targetURI;
    }

    /**
     * Returns an ASCII compatible representation of the string (only using characters from with values 0x0 - 0x7f)
     * This is the most common way to retrieve a representation of the URI, useful in situations where it
     * is mandatory only to use ASCII characters. This method might not be useful when displaying the string
     * in a User Interface.
     *
     * @return A representation of the URI that only uses ASCII characters (in the range of 0 - 127)
     * @throws URISyntaxException
     */
    public String toASCII() throws URISyntaxException
    {
        String path = getPath();

        if (mUsername == null && mUserpass != null)
        {
            throw new URISyntaxException(getUserInfo(), "Userpass given but no username");
        }

        if (path != null)
        {
            if (path.compareTo("/") == 0)
            {
                path = "";
            }
            else if (path.startsWith("//"))
            {
                throw new URISyntaxException(path, "Path component must not start with '//'");
            }
        }

        String authority = authority();
        if (!isDefined(authority) && !isDefined(path))
        {
            throw new URISyntaxException("", "URI is missing authority or path!");
        }
        if (isDefined(authority) && !isDefined(mScheme))
        {
            throw new URISyntaxException("", "Authority given but no scheme found!");
        }

        return recompose();
    }

    private String recompose()
    {
        StringBuffer builder = new StringBuffer();

        String site = site();
        String path = getPath();
        String query = getQuery();
        String fragment = getFragment();

        if (isDefined(path) && path.compareTo("/") == 0)
        {
            path = "";
        }

        builder.append(isDefined(site) ? site : "");
        builder.append(isDefined(path) ? path : "");
        builder.append(isDefined(query) ? "?" + query : "");
        builder.append(isDefined(fragment) ? "#" + fragment : "");
        return KCSimpleIDN.toASCII(builder.toString());
    }

    private void parseAuthority(String aAuthority) throws URISyntaxException
    {
        if (aAuthority != null)
        {
            Matcher matcher = sAuthorityPattern.matcher(aAuthority);
            if (!matcher.matches())
            {
                throw new URISyntaxException(aAuthority, "No valid authority given");
            }
            String userinfo = matcher.group(1);
            String host = matcher.group(2);
            String port = matcher.group(3);
            withUserInfo(userinfo);
            withHost(host);
            withPort(port);
        }
    }

    private void parseScheme(String aScheme) throws URISyntaxException
    {
        if (aScheme != null)
        {
            Matcher matcher = sSchemePattern.matcher(aScheme);
            if (!matcher.matches())
            {
                throw new URISyntaxException(aScheme, "No valid scheme");
            }
            this.mScheme = aScheme.toLowerCase();
        }
    }

    private void parseUserInfo(String aUserInfo) throws URISyntaxException
    {
        if (aUserInfo != null)
        {
            String[] parts = aUserInfo.split(":", -1);
            if (parts.length > 2 || !isUserInfoValid(aUserInfo))
            {
                throw new URISyntaxException(aUserInfo, "User info is not valid");
            }
            mUsername = (!parts[0].isEmpty()) ? parts[0] : null;
            mUserpass = (parts.length > 1 && !parts[1].isEmpty()) ? parts[1] : null;
        }
    }

    private boolean isUserInfoValid(String aUserInfo)
    {
        Matcher matcher = sUserInfoPattern.matcher(aUserInfo);
        return matcher.matches();
    }

    private void parseHost(String aHost) throws URISyntaxException
    {
        if (aHost == null)
        {
            return;
        }
        if (sNamedHostPattern.matcher(aHost).matches())
        {
            String ascii = IDN.toASCII(aHost);
            this.mHost = KCUtilURI.normalize(KCUtilURI.normalizeString(ascii, false), KCUtilURI.REGNAME);
        }
        else if (sIPV6HostPattern.matcher(aHost).matches())
        {
            this.mHost = aHost;
        }
        else if (sIPFuturePattern.matcher(aHost).matches())
        {
            this.mHost = aHost;
        }
        else
        {
            throw new URISyntaxException(aHost, "Host is not valid");
        }
    }

    private void parsePort(int aPort) throws URISyntaxException
    {
        if (aPort == -1)
            return;

        if (aPort < 1 || aPort > 65535)
        {
            throw new URISyntaxException(String.valueOf(aPort), "Invalid port number");
        }
        this.mPort = aPort;
    }

    private void parsePath(String aPath) throws URISyntaxException
    {
        if (isDefined(aPath))
        {
            this.mPath = aPath;
            this.mPath = KCUtilURI.normalizeString(this.mPath, true);
            this.mPath = KCUtilURI.removeDotSegments(this.mPath);
            this.mPathSegments = createPathSegments();
        }
    }

    private void pathSegmentsToPath()
    {
        this.mPath = pathSegmentsToString();
    }

    private List<String> createPathSegments()
    {
        StringTokenizer tokenizer = new StringTokenizer(getPath(), "/");
        List<String> list = new ArrayList<String>();
        while (tokenizer.hasMoreTokens())
        {
            String element = tokenizer.nextToken();
            list.add(element);
        }
        return list;
    }

    private String pathSegmentsToString()
    {
        List<String> list = getPathSegments();
        StringBuffer stringBuffer = new StringBuffer();

        for (String segment : list)
        {
            stringBuffer.append("/");
            stringBuffer.append(segment);
        }

        if(stringBuffer.length() == 0)
            stringBuffer.append("/");

        return stringBuffer.toString();
    }




    private void parseQuery(String aKey, String aValue)
    {
        aKey = (aKey != null && !aKey.isEmpty()) ? aKey : null;
        aValue = (aValue != null && !aValue.isEmpty()) ? aValue : null;
        if (aKey != null || aValue != null)
        {
            mQueries.add(new KCNameValuePair(aKey, aValue));
        }
    }

    private void parseQuery(String aQuery)
    {
        mQueries.clear();
        if (aQuery != null)
        {
            String[] parts = aQuery.split(Character.toString(mDelimiter));
            for (String part : parts)
            {
                int index = part.indexOf('=');
                String key = part;
                String value = null;
                if (index != -1)
                {
                    key = part.substring(0, index);
                    value = part.substring(index + 1, part.length());
                }
                parseQuery(key, value);
            }
        }
    }

    private void parseFragment(String aFragment)
    {
        if (isDefined(aFragment))
        {
            this.mFragment = aFragment;
        }
    }

    private void parseRequestURI(String aRequest) throws URISyntaxException
    {
        boolean found = (mScheme != null) ? mScheme.matches("^https?$") : false;
        if (isAbsolute() && !found)
        {
            throw new URISyntaxException(aRequest, "Cannot set an HTTP request URI for non-HTTP URI.");
        }

        Matcher matcher = sRequestURIPattern.matcher(aRequest);
        if (matcher.matches())
        {
            String path = matcher.group(1);
            String query = matcher.group(2);
            parsePath(path);
            parseQuery(query);
        }
        else
        {
            throw new URISyntaxException(aRequest, "Request is not valid");
        }
    }

}
