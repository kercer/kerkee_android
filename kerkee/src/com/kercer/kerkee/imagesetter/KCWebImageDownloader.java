package com.kercer.kerkee.imagesetter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.kercer.kercore.debug.KCLog;
import com.kercer.kercore.task.KCTaskExecutor;
import com.kercer.kerkee.downloader.KCDefaultDownloader;
import com.kercer.kerkee.downloader.KCDownloader.KCScheme;
import com.kercer.kerkee.net.KCHttpServer;
import com.kercer.kerkee.util.KCNativeUtil;
import com.kercer.kerkee.webview.KCWebPath;
import com.kercer.kerkee.webview.KCWebView;
import com.kercer.kernet.download.KCDownloadEngine;
import com.kercer.kernet.download.KCDownloadListener;
import com.kercer.kernet.uri.KCURI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author zihong
 *
 */
public class KCWebImageDownloader
{
    KCDownloadEngine mDownloadEngine;

    //the key is url string
    private final ConcurrentHashMap<String, String> mDownloadingImageMap = new ConcurrentHashMap<String, String>();

    Context mContext;
    KCWebImageSetter mWebImageSetter;
    KCDefaultDownloader mLoader;
    KCWebImageCache mWebImageCache;

    public KCWebImageDownloader(final Context aContext, KCWebPath aWebPath)
    {
        mContext = aContext;

        mDownloadEngine = new KCDownloadEngine("kerkee", 5);

        mWebImageSetter = new KCWebImageSetter();
        mWebImageSetter.start();
        mLoader = new KCDefaultDownloader(aContext);
        mWebImageCache = new KCWebImageCache(aContext);
        KCScheme scheme = aWebPath.getBridgeScheme();
        //scheme possible null
        if(scheme != null && scheme.equals(KCScheme.HTTP))
        {
            mWebImageCache.setCacheDir(new File(aWebPath.getWebImageCachePath()));
        }

        KCTaskExecutor.executeTask(new Runnable() {
            @Override
            public void run() {
                FilenameFilter filenameFilter = new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return true;
                    }
                };
                mWebImageCache.loadCache(filenameFilter);
            }
        });

    }


    private String getCacheUri(KCWebPath aWebPath, KCURI aUri,boolean isLocalTip)
    {
        final String pathURI = aUri.getPath();

        String cacheUri = null;

        final String filePath = mWebImageCache.getCacheDir().getAbsolutePath() + pathURI;
        cacheUri = "file://"+filePath;

        if (!aWebPath.getBridgeScheme().equals(KCScheme.FILE))
        {
            if (KCHttpServer.isRunning())
            {
                String localHostUrl = KCHttpServer.getLocalHostUrl();
                if (localHostUrl != null)
                    cacheUri =localHostUrl + (isLocalTip ? File.separator : aWebPath.getWebImageCacheRelativePath()) + pathURI;
            }
        }

        return cacheUri;
    }

    private String localHostPathToFilePath(KCURI aUri)
    {
        if (aUri.getScheme().equalsIgnoreCase(KCScheme.FILE.toString()))
            return aUri.toString();
        String cacheUri = "file://" + KCHttpServer.getRootDir() + aUri.getPath();
       return cacheUri;
    }


    private void addUriTip(KCURI aUri)
    {
        aUri.addParam("UriTip", "true");
    }

    private boolean hasUriTip(final KCURI aUri)
    {
        String fromLocal = aUri.getQueryParameter("UriTip");
        return fromLocal != null ? true : false;
    }

    public KCWebImage downloadImageFile(final String aUrl, final KCWebView aWebView)
    {
    	final KCWebImage webImage = new KCWebImage();
        InputStream inputStream = null;
        try
        {
            //when onSetImage, callback native again whit file scheme, why it callback again?
            if (KCScheme.ofUri(aUrl).equals(KCScheme.FILE))
            {
                 inputStream = mLoader.getStream(aUrl, null);
                 webImage.setInputStream(inputStream);
                 return webImage;
            }

            //load from cache
            KCURI localUri = KCURI.parse(aUrl);
            boolean isFromLocal = hasUriTip(localUri);
            boolean hasCache = mWebImageCache.containsCache(localUri);
            if (hasCache || isFromLocal)
            {
                String cacheUri = getCacheUri(aWebView.getWebPath(), localUri, isFromLocal);

                if (cacheUri != null)
                {
                    //if image loaded from local server, change path to file path,and loaded use "file://"
                    if (!aWebView.getWebPath().getBridgeScheme().equals(KCScheme.FILE))
                    {
                        KCURI tmpUri = KCURI.parse(cacheUri);
                        cacheUri = localHostPathToFilePath(tmpUri);
                    }

                    inputStream = mLoader.getStream(cacheUri, null);
                    webImage.setInputStream(inputStream);
                }

                return webImage;
            }

            //download image from net
            final KCURI tmpLocalUri = localUri;
            if (!mDownloadingImageMap.containsKey(aUrl))
            {
                mDownloadingImageMap.put(aUrl, "");
                download(aWebView, tmpLocalUri);
            }
        }
        catch (Exception e)
        {
            KCLog.e(e);
        }

        return webImage;
    }

    private void download(final KCWebView aWebView, final KCURI aUri)
    {
        try
        {
            String pathURI = aUri.getPath();
            String filePath = mWebImageCache.getCacheDir().getAbsolutePath() + pathURI;
            File tmpDestFile = new File(filePath);

            if (!KCNativeUtil.fileExists(tmpDestFile.getParent()))
                // ensures the directory exists.
                tmpDestFile.getParentFile().mkdirs();

            String downloadUrl = aUri.toString();
            if (downloadUrl != null)
            {
                String tmpDestFilePath = tmpDestFile.getAbsolutePath();
                KCWebImageDownloaderListener imageDownloadProgressListener = new KCWebImageDownloaderListener(aUri, filePath, aWebView);
                mDownloadEngine.startDownload(downloadUrl, tmpDestFilePath, imageDownloadProgressListener, true, true);
            }
            else
            {
                mDownloadingImageMap.remove(aUri.toString());
            }
        }
        catch (Exception e)
        {
            KCLog.e(e);
        }
    }

    @SuppressLint("DefaultLocale")
    class KCWebImageDownloaderListener implements KCDownloadListener
    {
        private KCURI mUri;
        private String mDestFilePath;
        private KCWebView mWebView;

        public KCWebImageDownloaderListener(KCURI aUri, String aDestFilePath, KCWebView aWebView)
        {
            this.mUri = aUri;
            this.mDestFilePath = aDestFilePath;
            this.mWebView = aWebView;
//            KCLog.i("download image: " + aUri + ", " + aDestFilePath);
        }

        @Override
        public void onPrepare() {

        }

        @Override
        public void onReceiveFileLength(long downloadedBytes, long fileLength) {

        }

        @Override
        public void onProgressUpdate(long downloadedBytes, long fileLength, int speed) {

        }

        @Override
        public void onComplete(long downloadedBytes, long fileLength, int totalTimeInSeconds)
        {
            if (mDestFilePath.toLowerCase().endsWith(".gif"))
            {
                try
                {
                    Bitmap bitmap = BitmapFactory.decodeFile(mDestFilePath);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, new FileOutputStream(mDestFilePath));

                    if (bitmap != null && !bitmap.isRecycled())
                    {
                        bitmap.recycle();
                    }
                }
                catch (Exception e)
                {
                }
            }

            mWebImageCache.add(mUri);
            mDownloadingImageMap.remove(mUri.toString());
            try
            {
                String cacheUri = getCacheUri(mWebView.getWebPath(), mUri, false);
                if (cacheUri != null)
                {
                    KCURI uriLocal = KCURI.parse(cacheUri);
                    addUriTip(uriLocal);
                    mWebImageSetter.addTask(new KCWebImageSetterTask(mWebView, mUri.toString(), uriLocal));
                }
            }
            catch (URISyntaxException e)
            {
                // TODO Auto-generated catch block
                KCLog.e(e);
            }

//            KCLog.i("download image succeeded: " + mUri.toString() + ", " + mDestFilePath);
        }

        @Override
        public void onError(long downloadedBytes, Throwable e)
        {
            mWebImageCache.remove(mUri);
            mDownloadingImageMap.remove(mUri.toString());

            KCLog.i("download image failed: " + mUri.toString() + ", " + mDestFilePath);
        }
    }
}
