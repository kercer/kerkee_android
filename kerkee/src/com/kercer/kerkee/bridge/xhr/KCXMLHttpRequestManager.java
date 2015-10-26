package com.kercer.kerkee.bridge.xhr;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;

import com.kercer.kerkee.bridge.KCArgList;
import com.kercer.kerkee.log.KCLog;
import com.kercer.kerkee.net.uri.KCURI;
import com.kercer.kerkee.util.KCUtil;
import com.kercer.kerkee.webview.KCWebView;

/**
 * 
 * @author zihong
 *
 */
public class KCXMLHttpRequestManager
{
    private final static HashMap<String, KCXMLHttpRequest> mRequestMap = new HashMap<String, KCXMLHttpRequest>();

    public static String keyFromWebViewAndId(KCWebView webview, int id)
    {
        return String.valueOf(webview.hashCode()) + id;
    }

    private static KCXMLHttpRequest createXMLHttpRequest(KCWebView webview, int id, String urlHash)
    {
        KCXMLHttpRequest xhr = new KCXMLHttpRequest(id, urlHash);
        synchronized (mRequestMap)
        {
            mRequestMap.put(keyFromWebViewAndId(webview, id), xhr);
        }
        return xhr;
    }
    
    public static void create(KCWebView webView, KCArgList args)
    {
        
    }

    public static void open(KCWebView webView, KCArgList args)
    {
        try
        {

            KCLog.i("XHR open");

            int id = args.getInt("id");
            String url = args.getString("url");
            String method = getArgString(args, "method", "GET").toUpperCase();
            String urlHash = KCUtil.getMD5String(url);

            KCXMLHttpRequest xhr = mRequestMap.get(keyFromWebViewAndId(webView, id));
            if (xhr != null)
                xhr.abort();

            xhr = createXMLHttpRequest(webView, id, urlHash);
            String ua = webView.getSettings().getUserAgentString();
            String scheme = getArgString(args, "scheme", null);
            String host = getArgString(args, "host", null);
            String port = getArgString(args, "port", "");
            String referer = getArgString(args, "referer", null);
            String cookie = getArgString(args, "cookie", null);
            boolean async = getArgBoolean(args, "async", true);
            String href = getArgString(args, "href", null);

            KCURI uriUrl = KCURI.parse(url);
            boolean isAbsolute = uriUrl.isAbsolute();
            if(!isAbsolute)
            {
                List<String> list= uriUrl.getPathSegments();
                int nSegmentCount = list.size();

                if(nSegmentCount == 1 && !url.startsWith("/"))
                {
                    KCURI uriHref = KCURI.parse(href); 
                    uriHref.removeLastPathSegment();
                    url = uriHref.site()+uriHref.getPath()+"/" + url;
                }
                else if(nSegmentCount > 0)
                {
                    String tmpPath = url.startsWith("/") ? url : ("/" + url);
                    String tmpPort = (port.length()>0 ? ":" :"") + port;
                    url = scheme + "//" + host + tmpPort + tmpPath;
                }
                else
                {
                    url = href;
                }
                
            }
            
            xhr.open(webView, method, url, async, ua, referer, cookie);
        }
        catch (URISyntaxException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    // there's currently no error checking
    public static void send(KCWebView webView, KCArgList args)
    {
        KCXMLHttpRequest xhr;
        try
        {
            xhr = mRequestMap.get(keyFromWebViewAndId(webView, args.getInt("id")));
            KCLog.i("XHRDisp send: " + xhr);
            if (xhr != null)
            {
                // more than 1 property(the 'id' property)
                if (args.has("data"))
                {
                    xhr.send(webView, args.getString("data"));
                }
                else
                {
                    xhr.send(webView);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    // there's currently no error checking
    public static void abort(KCWebView webView, KCArgList args)
    {
        KCXMLHttpRequest xhr;
        try
        {
            xhr = mRequestMap.get(keyFromWebViewAndId(webView, args.getInt("id")));
            KCLog.i("XHRDisp abort: " + xhr);
            if (xhr != null)
            {
                xhr.abort();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static void setRequestHeader(KCWebView webView, KCArgList args)
    {
        try
        {
            String headerName = args.getString("headerName");
            String headerValue = args.getString("headerValue");
            KCXMLHttpRequest xhr = mRequestMap.get(keyFromWebViewAndId(webView, args.getInt("id")));
            if (xhr != null)
            {
                xhr.setRequestHeader(headerName, headerValue);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static void overrideMimeType(KCWebView webView, KCArgList args)
    {

        KCXMLHttpRequest xhr;
        try
        {
            xhr = mRequestMap.get(keyFromWebViewAndId(webView, args.getInt("id")));
            String mimetype = args.getString("mimetype");
            if (xhr != null)
            {
                xhr.overrideMimeType(mimetype);
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void freeXMLHttpRequestObject(KCWebView webView, int id)
    {
        mRequestMap.remove(keyFromWebViewAndId(webView, id));
    }

    private static String getArgString(KCArgList aArgList, String name, String defaultValue)
    {
        try
        {
            String value = aArgList.getString(name);
            return (value != null) ? value : defaultValue ;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return defaultValue;
    }

    private static boolean getArgBoolean(KCArgList aArgList, String name, boolean defaultValue)
    {
        try
        {
            return aArgList.getBoolean(name);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return defaultValue;
    }
}
