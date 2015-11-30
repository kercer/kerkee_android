package com.kercer.kerkee.browser;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kercer.kerkee.browser.page.KCPage;
import com.kercer.kerkee.webview.KCWebChromeClient;
import com.kercer.kerkee.webview.KCWebView;
import com.kercer.kerkee.webview.KCWebViewClient;
import com.kercer.kerkee_example.R;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author zihong
 *
 */
public class KCDefaultBrowser extends KCPage implements View.OnClickListener
{
    protected KCWebView mWebView;
    private KCJSBridge mJSBridge;

    private ImageButton mBtnBrowserRefreshOrStopLoading;
    private ImageButton mBtnBrowserBack;
    private ImageButton mBtnBrowserForward;
    private TextView mTvTitle;

    private String mCurrentUrl; //store current show url
    private Map<String, String> mExtraHeaders = new HashMap<String, String>();
    private boolean mClearedHistory;
    private boolean mIsLoading = false;

    public KCDefaultBrowser(Context context)
    {
        this(context, R.layout.browser, R.id.webview);
    }

    public KCDefaultBrowser(Context context, int layoutId, int webViewId)
    {
        super(context, layoutId);
        mWebView = (KCWebView) findViewById(webViewId);
        if (mWebView == null)
            throw new IllegalStateException("R.id.webview is not defined.");
        init();
    }

    public KCDefaultBrowser(Context context, KCWebView webView)
    {
        super(context, webView);
        mWebView = webView;
        init();
    }

    private void init()
    {
        mWebView.setWebChromeClient(new KCProgressBarWebChromeClient(findViewById(R.id.loadingProgressIndicator)));
        mWebView.setWebViewClient(new KCWebPageLoadWebViewClient());
        mWebView.setDownloadListener(new KCDownloadListener());

        initHeaderBar();

        (mBtnBrowserRefreshOrStopLoading = (ImageButton) findViewById(R.id.btnBrowserRefreshOrStopLoading)).setOnClickListener(this);
        (mBtnBrowserBack = (ImageButton) findViewById(R.id.btnBrowserBack)).setOnClickListener(this);
        (mBtnBrowserForward = (ImageButton) findViewById(R.id.btnBrowserForward)).setOnClickListener(this);

        mJSBridge = new KCJSBridge(mWebView);

    }

    public void loadUrl(String aUrl)
    {
        if (null == mWebView)
            return;
        mWebView.loadUrlExt(aUrl);
    }

    public void loadTestPage()
    {
        String url ="file://" + mWebView.getWebPath().getResRootPath() +"/modules/test/test.html";
        // onSetImage must with this scheme
//        String url = "http://127.0.0.1:" + KCHttpServer.getPort() + "/jsbridge/test.html";
//    	String url = "http://www.baidu.com/";
        loadUrl(url);
    }


    public boolean registJSBridgeClient(Class<?> aClass)
    {
        return KCJSBridge.registJSBridgeClient(aClass) != null ? true : false;
    }

    public KCWebView getWebView()
    {
        return mWebView;
    }

    public String getResRootPath()
    {
        return mWebView.getWebPath().getResRootPath();
    }

    private void initHeaderBar()
    {
        mTvTitle = (TextView) findViewById(R.id.tvHeaderBarTitle);
        findViewById(R.id.btnHeaderBarBack).setOnClickListener(this);
        mTvTitle.setOnClickListener(this);
    }

    public void destroy()
    {
        mJSBridge.destroy();
    }

    public void reLoadBtnPressed()
    {
        try
        {
            setLoadBtnImage(mIsLoading);
            if (mIsLoading)
            {
                mWebView.stopLoading();
            }
            else
            {
                mWebView.reload();
            }
            mIsLoading = !mIsLoading;
        }
        catch (Exception ex)
        {

        }
    }

    public void setLoadBtnImage(boolean isLoading)
    {
        if (isLoading)
        {
            mBtnBrowserRefreshOrStopLoading.setImageResource(R.drawable.btn_browser_control_stop_selector);
        }
        else
        {
            mBtnBrowserRefreshOrStopLoading.setImageResource(R.drawable.btn_browser_control_refresh_selector);
        }
        mIsLoading = isLoading;
    }

    @Override
    public void onClick(View v)
    {
        try
        {
            int id = v.getId();
            if (id == R.id.btnBrowserBack)
            {
                mWebView.goBack();
            }
            else if (id == R.id.btnBrowserForward)
            {
                mWebView.goForward();
            }
            else if (id == R.id.btnBrowserRefreshOrStopLoading)
            {
                reLoadBtnPressed();
            }
            else if (id == R.id.btnHeaderBarBack)
            {
            }
            else if (id == R.id.tvHeaderBarTitle)
            {
            }
        }
        catch (Exception ex)
        {

        }

    }

    /*********************************/
    /*new class*/
    /*********************************/

    class KCProgressBarWebChromeClient extends KCWebChromeClient
    {
        private View mLoadingProgressIndicator;
        private final Pattern MATCH_WEBVIEW_TITLE_PATTERN = Pattern.compile("^([^ _-]*)");

        public KCProgressBarWebChromeClient(View loadingProgressIndicator)
        {
            mLoadingProgressIndicator = loadingProgressIndicator;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress)
        {
            if (mLoadingProgressIndicator != null && mLoadingProgressIndicator.getVisibility() == View.VISIBLE)
            {
                int curWidth = view.getWidth() * newProgress / 100;
                mLoadingProgressIndicator.getLayoutParams().width = curWidth;
                mLoadingProgressIndicator.requestLayout();
                if (curWidth == view.getWidth())
                {
                    view.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mLoadingProgressIndicator.getLayoutParams().width = 0;
                            mLoadingProgressIndicator.requestLayout();
                        }
                    });
                }
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title)
        {
            super.onReceivedTitle(view, title);

            if (title != null)
            {
                Matcher m = MATCH_WEBVIEW_TITLE_PATTERN.matcher(title);

                if (m.find())
                    mTvTitle.setText(m.group(1));
                else
                    mTvTitle.setText(title);
            }
        }
    }

    class KCWebPageLoadWebViewClient extends KCWebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
//        	HitTestResult hit = view.getHitTestResult();
//            int hitType = hit.getType();
//            if (hitType == HitTestResult.SRC_ANCHOR_TYPE)

            //use this judgement to avoid redirect circle problem when user try to goback to a redirect page
            if (mCurrentUrl != null && url != null && url.equals(mCurrentUrl))
            {
                mWebView.goBack();
                return true;
            }

            mWebView.loadUrlExt(url, mExtraHeaders);
            mCurrentUrl = url;

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            super.onPageFinished(view, url);
            if (!mClearedHistory)
            {
                mClearedHistory = true;
                view.clearHistory();
            }

            if (mWebView.canGoBack())
            {
                mBtnBrowserBack.setEnabled(true);
            }
            else
            {
                mBtnBrowserBack.setEnabled(false);
            }
            if (mWebView.canGoForward())
            {
                mBtnBrowserForward.setEnabled(true);
            }
            else
            {
                mBtnBrowserForward.setEnabled(false);
            }
            setLoadBtnImage(false);

            mExtraHeaders.clear();
            String origUrl = view.getOriginalUrl();
            if (origUrl != null)
                mExtraHeaders.put("Referer", origUrl);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            //开始
            super.onPageStarted(view, url, favicon);
            setLoadBtnImage(true);
        }
    }

    /**
     * 实现提供下载功能
     * 跳转到第三方应用进行下载
     * WebView.setDownloadListener(new ForumDownloadListener());
     */
    class KCDownloadListener implements DownloadListener
    {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength)
        {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //fixed this: android.util.AndroidRuntimeException: Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

}
