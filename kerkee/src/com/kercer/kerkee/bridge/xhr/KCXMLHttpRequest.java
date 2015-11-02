package com.kercer.kerkee.bridge.xhr;

import com.kercer.kerkee.bridge.KCJSExecutor;
import com.kercer.kerkee.log.KCLog;
import com.kercer.kerkee.util.KCUtil;
import com.kercer.kerkee.webview.KCWebView;
import com.kercer.kernet.http.KCHttpListener;
import com.kercer.kernet.http.KCHttpRequest;
import com.kercer.kernet.http.KCHttpResponse;
import com.kercer.kernet.http.KCHttpResult;
import com.kercer.kernet.http.KerNet;
import com.kercer.kernet.http.base.KCHeader;
import com.kercer.kernet.http.base.KCHeaderGroup;
import com.kercer.kernet.http.base.KCHttpDefine;
import com.kercer.kernet.http.base.KCStatusLine;
import com.kercer.kernet.http.error.KCAuthFailureError;
import com.kercer.kernet.http.error.KCNetError;
import com.kercer.kernet.http.request.KCStringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 *
 * @author zihong
 *
 */
public class KCXMLHttpRequest
{
    private final static int UNSET = 0;
    private final static int OPENED = 1;
    private final static int HEADERS_RECEIVED = 2;
    private final static int LOADING = 3;
    private final static int DONE = 4;

    private final static String GET = "GET";
    private final static String POST = "POST";
    private final static String HEAD = "HEAD";

    private KCStringRequest mHttpRequest;

    private int mState = UNSET;
    private int mId;
    private String mUrlHash;
    private boolean mAsync;

    private final static String DEFAULT_RESPONSE_CHARSET = "UTF-8";
    private String mResponseCharset; // for example: gbk, gb2312, etc.

    private boolean mAborted;

    private String mBody;

    public KCXMLHttpRequest(int id, String urlHash)
    {
        this.mId = id;
        this.mUrlHash = urlHash;
    }

    public void setId(int id)
    {
        this.mId = id;
    }

    public int getId()
    {
        return this.mId;
    }

    /**
     * The reason that userAgent and referer are not set in the constructor is
     * that XMLHttpRequest.constructor in the JS layer may be called only once,
     * while XMLHttpRequest.open may be called multiple times. In this case, we
     * still have to create a brand new XMLHttpRequest object internally in the
     * Java layer, in which case we have no way of acquiring userAgent of the
     * browser and referer of the current request, but JS#XMLHttpRequest.open
     * can send them to us here.
     *
     * @param method    - GET/POST/HEAD
     * @param url       - the url to request
     * @param userAgent - User-Agent of the browser(currently WebView)
     * @param referer   - referer of the current request
     */
    public void open(final KCWebView webView, final String method, final String url, final boolean async, final String userAgent, final String referer, final String cookie)
    {
        mAsync = async;
        createHttpRequest(webView, method, url);
        if (mHttpRequest != null)
        {
            if (userAgent != null)
                mHttpRequest.addHeader(KCHeader.header("User-Agent", userAgent));
            if (referer != null)
                mHttpRequest.addHeader(KCHeader.header("Referer", referer));
            if (cookie != null)
                mHttpRequest.addHeader(KCHeader.header("Cookie", cookie));
        }
    }

    /**
     * @param method - currently only supports GET, POST, HEAD
     * @param url
     */
    private void createHttpRequest(final KCWebView webView, final String method, final String url)
    {
        URI uri = URI.create(KCUtil.replacePlusWithPercent20(url));
        int nMethod = -1;
        if (GET.equalsIgnoreCase(method))
        {
            nMethod = KCHttpRequest.Method.GET;
        }
        else if (POST.equalsIgnoreCase(method))
        {
            nMethod = KCHttpRequest.Method.POST;
        }
        else if (HEAD.equalsIgnoreCase(method))
        {
            nMethod = KCHttpRequest.Method.HEAD;
        }


        if (nMethod >= 0)
        {
            final KCHttpResponse[] httpResponse = new KCHttpResponse[1];

            mHttpRequest = new KCStringRequest(nMethod, uri.toString(), new KCHttpResult.KCHttpResultListener<String>() {

                @Override
                public void onHttpResult(String response)
                {
                    final KCStatusLine sl = httpResponse[0].getStatusLine();
                    try
                    {
                        returnResult(webView, sl.getStatusCode(), sl.getReasonPhrase(), response, false);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();

                        if (!mHttpRequest.isCanceled())
                        {
                            returnError(webView, 500, e.getMessage());
                        }

                    }
                    finally
                    {
                        KCXMLHttpRequestManager.freeXMLHttpRequestObject(webView, mId);
                    }

                }
            }, new KCHttpListener() {

                @Override
                public void onResponseHeaders(KCStatusLine aStatusLine,  KCHeaderGroup aHeaderGroup)
                {
                    try
                    {
                        // send the received response headers to the JS layer
                        setCookieToWebView(webView, aHeaderGroup);
                        handleHeaders(webView, aHeaderGroup, aStatusLine);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onHttpError(KCNetError error)
                {
                    final KCStatusLine sl = error.networkResponse.getStatusLine();
                    returnError(webView, sl.getStatusCode(), sl.getReasonPhrase());
                }

                @Override
                public void onHttpComplete(KCHttpRequest<?> request, KCHttpResponse response)
                {
                    httpResponse[0] = response;
                }
            })
            {
                @Override
                public byte[] getBody() throws KCAuthFailureError
                {
                    try
                    {
                        //can set charset
                        return mBody.getBytes(getParamsEncoding());
                    }
                    catch (Exception e)
                    {
                    }

                    return super.getBody();
                }
            };
        }
        else
        {
            returnError(webView, 405, "Method Not Allowed");
        }


    }

    private void returnError(KCWebView webView, int statusCode, String statusText)
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put("id", mId);
            jsonObject.put("readyState", DONE);
            jsonObject.put("status", statusCode);
            jsonObject.put("statusText", statusText);

            callJSSetProperties(webView, jsonObject.toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        finally
        {
            KCXMLHttpRequestManager.freeXMLHttpRequestObject(webView, mId);
        }

        mState = DONE;
    }

    /**
     * for GET and HEAD start data transmission(sending the request and reading
     * the response)
     */
    public void send(final KCWebView webView)
    {
        // only when send() is called can the state be marked as OPENDED
        mState = OPENED;
        KCLog.i(">>>> XHR send start");

        // open() must be called before calling send()
        if (mHttpRequest == null)
        {
            KCXMLHttpRequestManager.freeXMLHttpRequestObject(webView, mId);
            return;
        }

        KCLog.i(">>>> XHR send, request not null");

        KCLog.i(">>>> XHR send, no cache");

        KCLog.i(">>>> XHR send, not local file");

        if (mAborted)
        {
            KCXMLHttpRequestManager.freeXMLHttpRequestObject(webView, mId);
            return;
        }

        KerNet.newRequestRunner(webView.getContext()).startAsyn(mHttpRequest);
    }

    /**
     * for POST only start data transmission(sending the request HEADER and BODY
     * and reading the response)
     */

    public void send(final KCWebView webView, final String data)
    {

        if (mHttpRequest == null || mHttpRequest.getMethod() != KCHttpRequest.Method.POST)
        {
            KCXMLHttpRequestManager.freeXMLHttpRequestObject(webView, mId);
            return;
        }

        mBody = data;

        send(webView);

    }



    //if statusCode is 200, reasonPhrase is "OK"
    private void returnResult(KCWebView webView, int statusCode, String reasonPhrase, String responseText, boolean alreadyInUIThread) throws JSONException
    {
        if (mAborted)
            return;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", mId);
        jsonObject.put("readyState", DONE);
        jsonObject.put("status", statusCode);
        jsonObject.put("statusText", reasonPhrase);
        jsonObject.put("responseText", responseText);
        callJSSetProperties(webView, jsonObject.toString());

        mState = DONE;
    }

    private String readResponseBody(InputStream is) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = KCUtil.getThreadSafeByteBuffer();
        int lenRead;
        while ((lenRead = is.read(buffer)) > 0)
        {
            baos.write(buffer, 0, lenRead);
        }

        return new String(baos.toByteArray(), mResponseCharset != null ? mResponseCharset : DEFAULT_RESPONSE_CHARSET);
    }

    // send headers to the JS layer
    private void handleHeaders(KCWebView webView, KCHeaderGroup headerGroup, KCStatusLine sl) throws JSONException
    {
        KCHeader[] headers = headerGroup.getAllHeaders();
        JSONObject jsonHeaders = new JSONObject();
        for (KCHeader h : headers)
        {
            String name = h.getName();
            String value = h.getValue();
            if (name.equalsIgnoreCase(KCHttpDefine.HEADER_CONTENT_TYPE))
            {
                mResponseCharset = readCharset(value, DEFAULT_RESPONSE_CHARSET);
            }
            jsonHeaders.put(name, value);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", mId);
        jsonObject.put("readyState", HEADERS_RECEIVED);
        jsonObject.put("status", sl.getStatusCode());
        jsonObject.put("statusText", sl.getReasonPhrase());
        jsonObject.put("headers", jsonHeaders);

        callJSSetProperties(webView, jsonObject.toString());
    }

    public void setRequestHeader(String headerName, String headerValue)
    {
        if (headerName == null || headerValue == null) return;

        if ("host".equalsIgnoreCase(headerName))
        { // this header must not be set this way
            return;
        }
        else if(KCHttpDefine.HEADER_CONTENT_TYPE.equalsIgnoreCase(headerName))
        {
            String charset = readCharset(headerValue, null);
            if (charset == null)
            {
                String valueTrim = headerValue;
                valueTrim = valueTrim.trim();

                if (valueTrim.charAt(valueTrim.length()-1) == ';')
                {
                    valueTrim = valueTrim.substring(0, valueTrim.length()-1);
                }
                headerValue = valueTrim + KCHttpDefine.CHARSET_PARAM + DEFAULT_RESPONSE_CHARSET;
            }
        }
        mHttpRequest.addHeader(KCHeader.header(headerName, headerValue));
    }

    // "text/html;charset=gbk", "gbk" will be extracted, others will be ignored.
    private String readCharset(String mimeType, String defaultCharset)
    {
        if (mimeType != null)
        {
            String[] params = mimeType.split(";");
            for (int i = 1; i < params.length; i++)
            {
                String[] pair = params[i].trim().split("=");
                if (pair.length == 2)
                {
                    if (pair[0].equalsIgnoreCase("charset"))
                    {
                        return pair[1];
                    }
                }
            }
        }

        return defaultCharset;
    }

    public void overrideMimeType(String mimeType)
    {
        readCharset(mimeType, DEFAULT_RESPONSE_CHARSET);
    }

    //httpClient setCookie
    private void setCookieToWebView(KCWebView webView, KCHeaderGroup headerGroup)
    {
//        Header[] cookies = response.getHeaders("Set-Cookie");
//        for (int i = 0; i < cookies.length; ++i)
//        {
//            KCJSExecutor.callJS(webView, "javascript:document.cookie='" + cookies[i].getValue() + "'");
//        }
    }

    private void callJSSetProperties(KCWebView webView, String jsonStr)
    {
        KCJSExecutor.callJSFunctionOnMainThread(webView, "XMLHttpRequest.setProperties", jsonStr);
    }

    public synchronized boolean isOpened()
    {
        return mState == OPENED;
    }

    public synchronized void abort()
    {
        mAborted = true;
        if (mState != DONE && !mHttpRequest.isCanceled())
        {
            mHttpRequest.cancel();
        }
    }
}
