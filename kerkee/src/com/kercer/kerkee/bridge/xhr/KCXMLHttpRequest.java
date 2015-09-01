package com.kercer.kerkee.bridge.xhr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.kercer.kerkee.bridge.KCApiBridge;
import com.kercer.kerkee.log.KCLog;
import com.kercer.kerkee.net.KCHttpClient;
import com.kercer.kerkee.util.KCUtil;
import com.kercer.kerkee.webview.KCWebView;

import android.os.SystemClock;

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

    //    private final static String KC_CACHE_TIME_HEADER = "kc-cache-time";

    private HttpRequestBase mHttpRequest;

    private int mState = UNSET;
    private int mId;
    private String mUrlHash;
    private boolean mAsync;

    private final static ExecutorService mWorkerExecutor = Executors.newFixedThreadPool(3);

    private final static String DEFAULT_RESPONSE_CHARSET = "UTF-8";
    private String mResponseCharset; // for example: gbk, gb2312, etc.

    private final static int RETRY_MAX_COUNT = 1;
    private final static int RETRY_WAIT_TIME = 3000;

    private boolean mAborted;

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
                mHttpRequest.setHeader("User-Agent", userAgent);
            if (referer != null)
                mHttpRequest.setHeader("Referer", referer);
            if (cookie != null)
                mHttpRequest.setHeader("Cookie", cookie);
        }
    }

    /**
     * @param method - currently only supports GET, POST, HEAD
     * @param url
     */
    private void createHttpRequest(final KCWebView webView, final String method, final String url)
    {
        URI uri = URI.create(KCUtil.replacePlusWithPercent20(url));
        if (GET.equalsIgnoreCase(method))
        {
            mHttpRequest = new HttpGet(uri);
        }
        else if (POST.equalsIgnoreCase(method))
        {
            mHttpRequest = new HttpPost(uri);
        }
        else if (HEAD.equalsIgnoreCase(method))
        {
            mHttpRequest = new HttpHead(uri);
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

        mWorkerExecutor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    int curRetryCount = 0;
                    while (++curRetryCount <= RETRY_MAX_COUNT)
                    {
                        HttpEntity entity = null;
                        InputStream is = null;

                        if (mAborted)
                            return;

                        try
                        {
                            KCLog.i(">>>> XHR send: " + mHttpRequest.getURI());
                            // send the request
                            HttpResponse response = KCHttpClient.getHttpClient().execute(mHttpRequest);
                            StatusLine sl = response.getStatusLine();
                            if (KCLog.DEBUG)
                            {
                                KCLog.i(">>>> XHR response status: " + sl.getStatusCode());
                                KCLog.d(">>>> XHR content length: " + response.getFirstHeader("Content-Length"));
                            }

                            if (sl.getStatusCode() == HttpStatus.SC_OK)
                            {
                                // send the received response headers to the JS layer
                                setCookieToWebView(response);
                                handleHeaders(webView, response, sl);

                                entity = response.getEntity();
                                is = entity.getContent();
                                String responseText = readResponseBody(is);

                                KCLog.d(">>>> XHR READ content length: " + responseText.length());

                                returnResult(webView, sl.getStatusCode(), sl.getReasonPhrase(), responseText, false);
                                break;
                            }
                            else if (curRetryCount == RETRY_MAX_COUNT)
                            {
                                KCLog.i(">>>> XHR reach retry max count, status: " + sl.getStatusCode());
                                returnError(webView, sl.getStatusCode(), sl.getReasonPhrase());
                                break;
                            }
                            KCLog.i(">>>> XHR unknown status: " + sl.getStatusCode());
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();

                            KCLog.i(">>>> XHR exception: " + e.getMessage());
                            if (!mHttpRequest.isAborted())
                            {
                                if (curRetryCount < RETRY_MAX_COUNT)
                                {
                                    SystemClock.sleep(RETRY_WAIT_TIME);
                                }
                                else
                                {
                                    returnError(webView, 500, e.getMessage());
                                    KCLog.i(">>>> XHR exception: " + e.getMessage());
                                    break;
                                }
                            }
                        }
                        finally
                        {
                            KCUtil.closeCloseable(is);
                            closeHttpEntity(entity);
                        }
                    }
                }
                finally
                {
                    KCXMLHttpRequestManager.freeXMLHttpRequestObject(webView, mId);
                }
            }
        });
    }

    /**
     * for POST only start data transmission(sending the request HEADER & BODY
     * and reading the response)
     */
    public void send(final KCWebView webView, final String data)
    {
        if (mHttpRequest == null || !POST.equals(mHttpRequest.getMethod().toUpperCase()))
            return;

        mWorkerExecutor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                // Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                HttpEntity entity = null;
                InputStream is = null;

                try
                {
                    StringEntity formParametersEntity = new StringEntity(data);
                    formParametersEntity.setContentType("application/x-www-form-urlencoded");
                    ((HttpPost) mHttpRequest).setEntity(formParametersEntity);
                    HttpResponse response = KCHttpClient.getHttpClient().execute(mHttpRequest);
                    StatusLine sl = response.getStatusLine();

                    if (sl.getStatusCode() == HttpStatus.SC_OK)
                    {
                        //setCookieToWebView(response);
                        handleHeaders(webView, response, sl);

                        entity = response.getEntity();
                        is = entity.getContent();
                        String responseText = readResponseBody(is);

                        returnResult(webView, sl.getStatusCode(), sl.getReasonPhrase(), responseText, false);
                    }
                    else
                    {
                        returnError(webView, sl.getStatusCode(), sl.getReasonPhrase());
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();

                    if (!mHttpRequest.isAborted())
                    {
                        returnError(webView, 500, e.getMessage());
                    }
                }
                finally
                {
                    KCUtil.closeCloseable(is);
                    closeHttpEntity(entity);
                }
            }
        });
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
    private void handleHeaders(KCWebView webView, HttpResponse response, StatusLine sl) throws JSONException
    {
        Header[] headers = response.getAllHeaders();
        JSONObject jsonHeaders = new JSONObject();
        for (Header h : headers)
        {
            String name = h.getName();
            String value = h.getValue();
            if (name.equals("Content-Type"))
            {
                readCharset(value);
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
        if ("host".equalsIgnoreCase(headerName))
        { // this header must not be set this way
            return;
        }
        mHttpRequest.setHeader(headerName, headerValue);
    }

    // "text/html;charset=gbk", "gbk" will be extracted, others will be ignored.
    private void readCharset(String mimeType)
    {
        StringTokenizer st = new StringTokenizer(mimeType, ";=");
        while (st.hasMoreTokens())
        {
            if (st.nextToken().trim().equalsIgnoreCase("charset"))
            {
                if (st.hasMoreTokens())
                    mResponseCharset = st.nextToken().trim();
            }
        }
    }

    public void overrideMimeType(String mimeType)
    {
        readCharset(mimeType);
    }

    private void setCookieToWebView(HttpResponse response)
    {
        //      Header[] cookies = response.getHeaders("Set-Cookie");
        //      for (int i = 0; i < cookies.length; ++i) {
        //          ApiBridge.callJs("javascript:document.cookie='"
        //                  + cookies[i].getValue() + "'");
        //      }
    }

    private void callJSSetProperties(KCWebView webView, String jsonStr)
    {
        KCApiBridge.callJSFunctionOnMainThread(webView, "XMLHttpRequest.setProperties", jsonStr);
    }

    private void closeHttpEntity(HttpEntity en)
    {
        if (en != null)
        {
            try
            {
                en.consumeContent();
            }
            catch (IOException e)
            {
                KCLog.e(e.toString());
            }
        }
    }

    public synchronized boolean isOpened()
    {
        return mState == OPENED;
    }

    public synchronized void abort()
    {
        mAborted = true;
        if (mState != DONE && !mHttpRequest.isAborted())
        {
            mHttpRequest.abort();
        }
    }
}
