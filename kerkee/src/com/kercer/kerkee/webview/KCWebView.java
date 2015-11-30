package com.kercer.kerkee.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kercer.kercore.debug.KCLog;
import com.kercer.kerkee.bridge.KCApiBridge;
import com.kercer.kerkee.downloader.KCDownloader.KCScheme;

import java.util.Map;

/**
 *
 * @author zihong
 *
 */
@SuppressLint("SetJavaScriptEnabled")
public class KCWebView extends WebView
{
    private boolean mIsDestroyed = false;
    private boolean mIsDocumentReady = false;
    protected String mUrl;
    private KCUrlMapper mUrlMapper = null;
    protected KCWebPath mWebPath = null;
    private Object mAttach = null;




    public Object getmAttach() {
		return mAttach;
	}

	public void setmAttach(Object aAttach) {
		this.mAttach = aAttach;
	}

	private final static View.OnLongClickListener M_WEB_VIEW_LONG_CLICK_LISTENER = new View.OnLongClickListener()
    {
        @Override
        public boolean onLongClick(View view)
        {
            if (Build.VERSION.SDK_INT >= 14)
                return false;
            // the following code crashes the app if applying to a webview contained in a PopupWindow
            return emulateShiftHeldForWebView(view);
        }

        private boolean emulateShiftHeldForWebView(View view)
        {
            try
            {
                KeyEvent shiftPressEvent = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT, 0, 0);
                shiftPressEvent.dispatch(view);
                return true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return false;
        }
    };

    public KCWebView(Context context, WebViewClient webViewClient, WebChromeClient webChromeClient)
    {
        super(context);
        init(webViewClient, webChromeClient);
    }

    public KCWebView(Context context, boolean init)
    {
        super(context);
        if (init)
        {
            init(KCWebViewClient.getInstance(), KCWebChromeClient.getInstance());
        }
    }

    public KCWebView(Context context)
    {
        this(context, true);
    }

    public KCWebView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(KCWebViewClient.getInstance(), KCWebChromeClient.getInstance());
    }

    public KCWebView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(KCWebViewClient.getInstance(), KCWebChromeClient.getInstance());
    }

    public void init(WebViewClient webViewClient, WebChromeClient webChromeClient)
    {
        mWebPath = new KCWebPath(getContext());

        //creat url map
        mUrlMapper = new KCUrlMapper(mWebPath.getResRootPath(), mWebPath.getCfgPath());


//        setBackgroundColor(getContext().getResources().getColor(R.color.darker_gray));
        setWebViewClient(webViewClient);
        setWebChromeClient(webChromeClient);
        KCWebView.setupWebViewAttributes(this);
        setSoundEffectsEnabled(false);
        setLongClickable(true);
        setOnLongClickListener(M_WEB_VIEW_LONG_CLICK_LISTENER);

    }

    public void documentReady(boolean aIsReady)
    {
        mIsDocumentReady = aIsReady;
    }

    public boolean isDocumentReady()
    {
        return mIsDocumentReady;
    }

    public KCWebPath getWebPath()
    {
        return mWebPath;
    }

    public void destroy()
    {
        if (mIsDestroyed)
        {
            return;
        }
        mIsDestroyed = true;

        if (Build.VERSION.SDK_INT > 10)
        {
            loadUrl("javascript:ApiBridge.callNative('ApiBridge', 'hackDestroyWebView')");
        }
        else
        {
            super.stopLoading();
            super.destroy();
        }
    }

    public void doDestroy()
    {
        super.stopLoading();
        super.destroy();
        if (KCLog.DEBUG)
            KCLog.d(">>>>> KCWebView.doDestroy() called");
    }

    public void loadUrlExt(String url, Map<String, String> additionalHttpHeaders)
    {
        if (mIsDestroyed || url == null)
            return;

        if (mUrlMapper != null && url.startsWith("file"))
        {
            url = mUrlMapper.lookup(url);
        }

        try
        {
//            if (KCLog.DEBUG)
//                KCLog.i(">>>> loadUrl1: " + url + ", " + additionalHttpHeaders);
            super.loadUrl(url, additionalHttpHeaders);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void loadUrlExt(String url)
    {
        if (mIsDestroyed || url == null)
            return;

        if (mUrlMapper != null && url.startsWith("file"))
        {
            url = mUrlMapper.lookup(url);
        }

        try
        {
//            if (KCLog.DEBUG)
//                KCLog.i(">>>> loadUrl2: " + url);
            super.loadUrl(url);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getUrl()
    {
        return mUrl;
    }

    public KCScheme getScheme()
    {
        return mWebPath.mBridgeScheme;
    }

    // use this to avoid a bug
    private boolean mIgnoreScroll;
    private float mCurContentHeight;
    private int mThreshold;

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {
        try
        {
            super.onScrollChanged(l, t, oldl, oldt);
            float contentHeight = getContentHeight() * getScale();

            if (mCurContentHeight != contentHeight)
            {
                if (contentHeight <= getScrollY() + getHeight() + mThreshold)
                {
                    if (!mIgnoreScroll)
                    {
                        KCApiBridge.callbackJSOnHitPageBottom(this);
                        mCurContentHeight = contentHeight;
                    }
                    else
                    {
                        mIgnoreScroll = false;
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void reload()
    {
        mCurContentHeight = 0;
        // use this to avoid a bug(reload() causes contentHeight to be equal to (getScrollY() + getHeight())))
        mIgnoreScroll = true;
        try
        {
            super.reload();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setHitPageBottomThreshold(int threshold)
    {
        mCurContentHeight = 0;
        mThreshold = threshold;
    }

    public boolean isDestroyed()
    {
        return mIsDestroyed;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        try
        {
            return super.onTouchEvent(event);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    //webview setting

    /**
     * @param webSettings
     * **/
    private static void setCustomizedUA(WebSettings webSettings)
    {

        StringBuilder customizedUA = new StringBuilder(webSettings.getUserAgentString()).append("; ");

        webSettings.setUserAgentString(customizedUA.toString());
    }

    /**
     * @param aWebView
     * **/
    public static void setupWebViewAttributes(KCWebView aWebView)
    {
        WebSettings webSettings = aWebView.getSettings();
        setCustomizedUA(webSettings);

        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setAppCachePath(aWebView.getWebPath().getRootPath() + "/webcache");
        webSettings.setAppCacheEnabled(true);
    //        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setLightTouchEnabled(false);
        webSettings.setDomStorageEnabled(true); // supports local storage
        webSettings.setDatabaseEnabled(true); // supports local storage
        webSettings.setDatabasePath(aWebView.getWebPath().getRootPath() + "/localstorage");

        // we are using ApplicationContext when creaing KCWebView, without disabling the "Save Password" dialog
        // there will be an exception that would cause crash: "Unable to add window -- token null is not for an application"
        webSettings.setSavePassword(false);

        aWebView.setHorizontalScrollBarEnabled(false);
        //        mWebView.setVerticalScrollBarEnabled(false);
        aWebView.setScrollbarFadingEnabled(true);
        aWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
    }

}
