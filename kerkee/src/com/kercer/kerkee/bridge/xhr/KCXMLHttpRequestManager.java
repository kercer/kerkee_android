package com.kercer.kerkee.bridge.xhr;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;

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
    
    public static void create(KCWebView webView, JSONObject args)
    {
        
    }

    public static void open(KCWebView webView, JSONObject args)
    {
        try
        {

            KCLog.i("XHR open");

            int id = args.getInt("id");
            String url = args.getString("url");
            String method = getJSONString(args, "method", "GET").toUpperCase();
            String urlHash = KCUtil.getMD5String(url);

            KCXMLHttpRequest xhr = mRequestMap.get(keyFromWebViewAndId(webView, id));
            if (xhr != null)
                xhr.abort();

            xhr = createXMLHttpRequest(webView, id, urlHash);
            String ua = webView.getSettings().getUserAgentString();
            String scheme = getJSONString(args, "scheme", null);
            String host = getJSONString(args, "host", null);
            String port = getJSONString(args, "port", "");
            String referer = getJSONString(args, "referer", null);
            String cookie = getJSONString(args, "cookie", null);
            boolean async = getJSONBoolean(args, "async", true);
            String href = getJSONString(args, "href", null);

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
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        catch (URISyntaxException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    // there's currently no error checking
    public static void send(KCWebView webView, JSONObject args)
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
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    // there's currently no error checking
    public static void abort(KCWebView webView, JSONObject args)
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
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    public static void setRequestHeader(KCWebView webView, JSONObject args)
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
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    public static void overrideMimeType(KCWebView webView, JSONObject args)
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
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void freeXMLHttpRequestObject(KCWebView webView, int id)
    {
        mRequestMap.remove(keyFromWebViewAndId(webView, id));
    }

    private static String getJSONString(JSONObject jsonObj, String name, String defaultValue)
    {
        try
        {
            String value = jsonObj.getString(name);
            return (value != null) ? value : defaultValue ;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return defaultValue;
    }

    private static boolean getJSONBoolean(JSONObject jsonObj, String name, boolean defaultValue)
    {
        try
        {
            return jsonObj.getBoolean(name);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return defaultValue;
    }
}
