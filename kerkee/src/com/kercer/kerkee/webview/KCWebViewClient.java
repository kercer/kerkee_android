package com.kercer.kerkee.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kercer.kercore.debug.KCLog;
import com.kercer.kerkee.bridge.KCApiBridge;
import com.kercer.kerkee.downloader.KCDownloader.KCScheme;
import com.kercer.kerkee.imagesetter.KCDefaultImageStream;
import com.kercer.kerkee.imagesetter.KCWebImage;
import com.kercer.kerkee.imagesetter.KCWebImageDownloader;
import com.kercer.kerkee.imagesetter.KCWebImageHandler;
import com.kercer.kerkee.imagesetter.KCWebImageListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author zihong
 */
@SuppressLint("DefaultLocale")
public class KCWebViewClient extends WebViewClient
{
    private static KCWebViewClient mInstance;

    private KCWebImageDownloader mImageDownloader;

    private static KCDefaultImageStream mDefaultImageStream;

    /**
     * 图片完成通知
     */
    private KCWebImageHandler mWebImageHandler;
    private KCWebImageListener mWebImageListener;

    protected KCWebViewClient()
    {
        super();
    }

    public static KCWebViewClient getInstance()
    {
        if (mInstance == null) mInstance = new KCWebViewClient();
        return mInstance;
    }

    void setWebImageListener(KCWebImageListener mWebImageListener) {
        this.mWebImageListener = mWebImageListener;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView aWebView, String aUrl)
    {
        ((KCWebView) aWebView).loadUrlExt(aUrl);
        return true;
    }

    @Override
    public void onPageStarted(WebView aWebView, String aUrl, Bitmap aFavicon)
    {
        super.onPageStarted(aWebView, aUrl, aFavicon);
        KCWebView webView = (KCWebView) aWebView;
        webView.documentReady(false);
        webView.mUrl = aUrl;
        webView.mWebPath.mBridgeScheme = KCScheme.ofUri(aUrl);
    }

    @Override
    public void onPageFinished(WebView aWebView, String aUrl)
    {
        KCWebView webView = (KCWebView) aWebView;
        KCApiBridge.initJSBridgeEnvironment(webView, KCScheme.ofUri(aUrl));
        super.onPageFinished(aWebView, aUrl);
    }

    @Override
    public void onLoadResource(final WebView aWebView, final String aUrl)
    {
        super.onLoadResource(aWebView, aUrl);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView aWebView, WebResourceRequest request)
    {

        String strMimeType = getFileMimeType(request.getUrl().toString());
        if (strMimeType != null)
        {
            String lowerCaseUrl = strMimeType.toLowerCase();
            if (lowerCaseUrl.contains("png") || lowerCaseUrl.contains("jpg") || lowerCaseUrl.contains("jpeg"))
            {
                return handleImageRequest(aWebView, request, strMimeType);
            }
        }

        return super.shouldInterceptRequest(aWebView, request);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(final WebView aWebView, final String aUrl)
    {
        String strMimeType = getFileMimeType(aUrl);
        if (strMimeType != null)
        {
            String lowerCaseUrl = strMimeType.toLowerCase();
            if (lowerCaseUrl.contains("png") || lowerCaseUrl.contains("jpg") || lowerCaseUrl.contains("jpeg"))
            {
                return handleImageRequest(aWebView, aUrl, strMimeType);
            }
        }
        return super.shouldInterceptRequest(aWebView, aUrl);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private WebResourceResponse handleImageRequest(final WebView aWebView, final WebResourceRequest request, String strMimeType)
    {
        KCWebView webView = (KCWebView) aWebView;
        if (mImageDownloader == null)
            mImageDownloader = new KCWebImageDownloader(webView.getContext(), webView.getWebPath());
        if (mWebImageHandler==null)
            mWebImageHandler = new KCWebImageHandler(mWebImageListener);

        KCWebImage webImage = mImageDownloader.downloadImageFile(request.getUrl().toString(), mWebImageHandler.add(request.getUrl().toString()));
        InputStream stream = webImage.getInputStream();
        if (stream == null)
        {
            Log.e("image", "current stream is null,download image from net");
            return null;
        }
        return new WebResourceResponse(strMimeType, "utf-8", stream);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private WebResourceResponse handleImageRequest(final WebView aWebView, final String aUrl, String strMimeType)
    {
        KCWebView webView = (KCWebView) aWebView;
        if (mImageDownloader == null)
            mImageDownloader = new KCWebImageDownloader(aWebView.getContext(), webView.getWebPath());
        if (mWebImageHandler==null)
            mWebImageHandler = new KCWebImageHandler(mWebImageListener);

        KCWebImage webImage = mImageDownloader.downloadImageFile(aUrl, mWebImageHandler.add(aUrl));
        InputStream stream = webImage.getInputStream();
        if (stream == null)
        {
            Log.e("image", "current stream is null,download image from net");
            return null;
        }
        return new WebResourceResponse(strMimeType, "utf-8", stream);
    }

    public String getMimeType(String aUrl)
    {
        String strMimeType = getFileMimeType(aUrl);
        if (strMimeType == null)
        {
            try
            {
                strMimeType = getURLMimeType(aUrl);
                KCLog.i(strMimeType);
            }
            catch (MalformedURLException e)
            {
                KCLog.e(e);
            }
            catch (IOException e)
            {
                KCLog.e(e);
            }
        }

        return strMimeType;
    }

    /**
     * Warning: this method is very slow mine type define in [jre_home]\lib\content-types.properties
     *
     * @param aUrl
     * @return
     * @throws java.io.IOException
     * @throws MalformedURLException
     */
    public String getURLMimeType(String aUrl) throws java.io.IOException, MalformedURLException
    {
        String type;
        URL u = new URL(aUrl);
        URLConnection uc;
        uc = u.openConnection();
        type = uc.getContentType();
        return type;
    }

    public String getFileMimeType(String aUrl)
    {
        try
        {
            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            String type = fileNameMap.getContentTypeFor(aUrl);
            return type;
        }
        catch (Exception e)
        {
            KCLog.e(e);
        }
        return null;
    }

    public KCDefaultImageStream getSavedStream(Context aContext)
    {
        if (mDefaultImageStream == null)
        {
            mDefaultImageStream = new KCDefaultImageStream(aContext);
        }
        return mDefaultImageStream;
    }
}
