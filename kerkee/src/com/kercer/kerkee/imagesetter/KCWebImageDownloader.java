package com.kercer.kerkee.imagesetter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.kercer.kerkee.downloader.KCDefaultDownloader;
import com.kercer.kerkee.downloader.KCDownloader.KCScheme;
import com.kercer.kerkee.downloader.KCUtilDownload;
import com.kercer.kerkee.log.KCLog;
import com.kercer.kerkee.net.KCHttpServer;
import com.kercer.kerkee.net.uri.KCURI;
import com.kercer.kerkee.util.KCNativeUtil;
import com.kercer.kerkee.util.KCUtilFile;
import com.kercer.kerkee.webview.KCWebPath;
import com.kercer.kerkee.webview.KCWebView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author zihong
 *
 */
public class KCWebImageDownloader
{
    private ExecutorService mThreadService;

    //the key is url string
    private final ConcurrentHashMap<String, String> mDownloadingImageMap = new ConcurrentHashMap<String, String>();

    private final static String TMP = ".tmp";

    Context mContext;
    KCWebImageSetter mWebImageSetter;
    KCDefaultDownloader mDownloader;
    KCWebImageCache mWebImageCache;

    public KCWebImageDownloader(final Context aContext, KCWebPath aWebPath)
    {
        mContext = aContext;
        mThreadService = Executors.newFixedThreadPool(8);
        mWebImageSetter = new KCWebImageSetter();
        mWebImageSetter.start();
        mDownloader = new KCDefaultDownloader(aContext);
        mWebImageCache = new KCWebImageCache(aContext);
        KCScheme scheme = aWebPath.getBridgeScheme();
        //scheme possible null
        if(scheme != null && scheme.equals(KCScheme.HTTP))
        {
            mWebImageCache.setCacheDir(new File(aWebPath.getWebImageCachePath()));
        }

        FilenameFilter filenameFilter = new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                return !name.endsWith(TMP);
            }
        };
        mWebImageCache.loadCache(filenameFilter);
    }


    private String getCacheUri(KCWebPath aWebPath, KCURI aUri,boolean isLocalTip)
    {
        final String pathURI = aUri.getPath();

        String cacheUri = null;
        if (aWebPath.getBridgeScheme().equals(KCScheme.FILE))
        {
            final String filePath = mWebImageCache.getCacheDir().getAbsolutePath() + pathURI;
            cacheUri = "file://"+filePath;
        }
        else
        {
            if (KCHttpServer.isRunning())
            {
                String localHostUrl = KCHttpServer.getLocalHostUrl();
                if (localHostUrl != null)
                    cacheUri =localHostUrl + (isLocalTip ? File.separator : aWebPath.getWebImageCacheRelativePath()) + pathURI;
            }
            else
            {
                final String filePath = mWebImageCache.getCacheDir().getAbsolutePath() + pathURI;
                cacheUri = "file://"+filePath;
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
                 inputStream = mDownloader.getStream(aUrl, null);
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

                    inputStream = mDownloader.getStream(cacheUri, null);
                    webImage.setInputStream(inputStream);
                }

                return webImage;
            }

            //download image from net
            final KCURI tmpLocalUri = localUri;
            if (!mDownloadingImageMap.containsKey(aUrl))
            {
                mDownloadingImageMap.put(aUrl, "");
                mThreadService.execute(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        download(aWebView, tmpLocalUri);
                    }
                });
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return webImage;
    }

    private void download(final KCWebView aWebView, final KCURI aUri)
    {
        try
        {
            String pathURI = aUri.getPath();
            String filePath = mWebImageCache.getCacheDir().getAbsolutePath() + pathURI;
            File tmpDestFile = new File(filePath + TMP);

            if (!KCNativeUtil.fileExists(tmpDestFile.getParent()))
                // ensures the directory exists.
                tmpDestFile.getParentFile().mkdirs();

            String downloadUrl = aUri.toString();
            if (downloadUrl != null)
            {
                String tmpDestFilePath = tmpDestFile.getAbsolutePath();
                FileOutputStream fileOutputStream = new FileOutputStream(tmpDestFilePath);
                KCWebImageDownloaderListener imageDownloadProgressListener = new KCWebImageDownloaderListener(aUri, tmpDestFile, filePath, aWebView);
                KCUtilDownload.downloadFile(downloadUrl, fileOutputStream, 3, imageDownloadProgressListener);
            }
            else
            {
                mDownloadingImageMap.remove(aUri.toString());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @SuppressLint("DefaultLocale")
    class KCWebImageDownloaderListener implements KCUtilDownload.KCUtilDownloadListener
    {
        private KCURI mUri;
        private File mTmpDestFile;
        private String mDestFilePath;
        private KCWebView mWebView;

        public KCWebImageDownloaderListener(KCURI aUri, File aTmpDestFile, String aDestFilePath, KCWebView aWebView)
        {
            this.mUri = aUri;
            this.mTmpDestFile = aTmpDestFile;
            this.mDestFilePath = aDestFilePath;
            this.mWebView = aWebView;
//            KCLog.i("download image: " + aUri + ", " + aDestFilePath);
        }

        public void onProgressUpdate(long downloadedBytes, long fileLength)
        {
        }

        public void onComplete()
        {
            if (mDestFilePath.toLowerCase().endsWith(".gif"))
            {
                try
                {
                    Bitmap bitmap = BitmapFactory.decodeFile(mTmpDestFile.getAbsolutePath());
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, new FileOutputStream(mDestFilePath));

                    if (bitmap != null && !bitmap.isRecycled())
                    {
                        bitmap.recycle();
                    }

                    new File(mTmpDestFile.getAbsolutePath()).delete();
                }
                catch (Exception e)
                {
                    KCUtilFile.rename(mTmpDestFile.getAbsolutePath(), mDestFilePath);
                }
            }
            else
            {
                KCUtilFile.rename(mTmpDestFile.getAbsolutePath(), mDestFilePath);
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
                e.printStackTrace();
            }

//            KCLog.i("download image succeeded: " + mUri.toString() + ", " + mDestFilePath);
        }

        public void onError(Exception e)
        {
            mTmpDestFile.delete();
            mWebImageCache.remove(mUri);
            mDownloadingImageMap.remove(mUri.toString());

            KCLog.i("download image failed: " + mUri.toString() + ", " + mDestFilePath);
        }
    }
}
