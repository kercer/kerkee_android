package com.kercer.kerkee.webview;

import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.kercer.kerkee.bridge.KCApiBridge;
import com.kercer.kerkee.downloader.KCDownloader.KCScheme;
import com.kercer.kerkee.imagesetter.KCWebImage;
import com.kercer.kerkee.imagesetter.KCWebImageDownloader;
import com.kercer.kerkee.imagesetter.KCDefaultImageStream;
import com.kercer.kerkee.log.KCLog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 
 * @author zihong
 *
 */
@SuppressLint("DefaultLocale")
public class KCWebViewClient extends WebViewClient
{
	private static KCWebViewClient mInstance;

	private KCWebImageDownloader mImageDownloader;
	
	private static KCDefaultImageStream mDefaultImageStream;


	protected KCWebViewClient()
	{
		super();
	}

	public static KCWebViewClient getInstance()
	{
		if (mInstance == null)
			mInstance = new KCWebViewClient();
		return mInstance;
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
		// webView.mUrl = aUrl;
		KCApiBridge.initJSBridgeEnvironment(webView, KCScheme.ofUri(aUrl));
		super.onPageFinished(aWebView, aUrl);
	}

	@Override
	public void onLoadResource(final WebView aWebView, final String aUrl)
	{
		super.onLoadResource(aWebView, aUrl);
	}

	// shouldInterceptRequest() CallbackProxy.java <- shouldInterceptRequest() BrowserFrame.java <- shouldInterceptRequest() WebCoreFrameBridge.cpp
	@Override
	public WebResourceResponse shouldInterceptRequest(final WebView aWebView, final String aUrl)
	{
		// //临时解决有时图片不能通过JS setImage设置成功的问题
		// return super.shouldInterceptRequest(aWebView, aUrl);

		KCWebView webView = (KCWebView) aWebView;

		String strMimeType = getFileMimeType(aUrl);
		if (strMimeType == null)
			return null;
		String lowerCaseUrl = strMimeType.toLowerCase();
		if (lowerCaseUrl.contains("png") || lowerCaseUrl.contains("jpg") || lowerCaseUrl.contains("jpeg"))
		{
			if (mImageDownloader == null)
				mImageDownloader = new KCWebImageDownloader(webView.getContext(), webView.getWebPath());

			KCWebImage webImage = mImageDownloader.downloadImageFile(aUrl, webView);

			InputStream stream = webImage.getInputStream();
			if (stream == null)
			{
				stream = getSavedStream(webView.getContext()).getInputStream();
			}
			WebResourceResponse res = new WebResourceResponse(strMimeType, "utf-8", stream);
			return res;
		}
		return super.shouldInterceptRequest(aWebView, aUrl);
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
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
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
		String type = null;
		URL u = new URL(aUrl);
		URLConnection uc = null;
		uc = u.openConnection();
		type = uc.getContentType();
		return type;
	}

	public String getFileMimeType(String aUrl)
	{
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String type = fileNameMap.getContentTypeFor(aUrl);
		return type;
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
