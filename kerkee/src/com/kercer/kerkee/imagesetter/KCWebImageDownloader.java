package com.kercer.kerkee.imagesetter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import com.kercer.kercore.debug.KCLog;
import com.kercer.kerkee.downloader.KCDefaultDownloader;
import com.kercer.kerkee.downloader.KCDownloader.KCScheme;
import com.kercer.kerkee.webview.KCWebPath;
import com.kercer.kernet.uri.KCURI;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zihong
 */
public class KCWebImageDownloader
{
    private static ExecutorService executorService = Executors.newCachedThreadPool();
    //the key is url string
    private final ConcurrentHashMap<String, String> mDownloadingImageMap = new ConcurrentHashMap<String, String>();
    Context mContext;
    KCDefaultDownloader mLoader;
    KCWebImageCache mWebImageCache;
    /**
     * create dirs lock object
     */
    private final Object object = new Object();

    public KCWebImageDownloader(final Context aContext, KCWebPath aWebPath)
    {
        mContext = aContext;
        mLoader = new KCDefaultDownloader(aContext);
        mWebImageCache = new KCWebImageCache(aContext);
        KCScheme scheme = aWebPath.getBridgeScheme();
        //scheme possible null
        if (scheme != null && scheme.equals(KCScheme.HTTP))
        {
            mWebImageCache.setCacheDir(new File(aWebPath.getWebImageCachePath()));
        }
        FilenameFilter filenameFilter = new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                return true;
            }
        };
        mWebImageCache.loadCache(filenameFilter);
    }

    private String getCacheUri(KCURI aUri)
    {
        final String pathURI = aUri.getPath();
        String cacheUri;
        final String filePath = mWebImageCache.getCacheDir().getAbsolutePath() + pathURI;
        cacheUri = filePath;
        return cacheUri;
    }

    public KCWebImage downloadImageFile(final String aUrl,final KCWebImageListener aOnImageFinish)
    {
        final PipedOutputStream out = new PipedOutputStream();
        final PipedInputStream inputStream;
        KCWebImage webImage = new KCWebImage();
        try
        {
            inputStream = new PipedInputStream(out);
            webImage.setInputStream(inputStream);
            setImageToPipStream(out, aUrl,aOnImageFinish);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return webImage;
    }

    private void setImageToPipStream(final OutputStream outputStream, final String aUrl,final KCWebImageListener aOnImageFinish)
    {
        executorService.execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    //load from cache
                    KCURI localUri = KCURI.parse(aUrl);
                    final String cacheUri = getCacheUri(localUri);
                    boolean hasCache = mWebImageCache.containsCache(localUri);
                    InputStream inputStream = null;
                    if (KCScheme.ofUri(aUrl).equals(KCScheme.FILE))
                    {
                        Log.i("KCWebImageDownloader", "read file:" + aUrl);
                        inputStream = mLoader.getStream(aUrl, null);
                    }
                    else if (hasCache)
                    {
                        Log.i("KCWebImageDownloader", "read cache:" + cacheUri);
                        if (cacheUri != null)
                        {
                            inputStream = mLoader.getStream("file://" + cacheUri, null);
                        }
                    }
                    else
                    {
                        //download image from net
                        if (!mDownloadingImageMap.containsKey(aUrl))
                        {
                            Log.i("KCWebImageDownloader", "read net:" + aUrl);
                            mDownloadingImageMap.put(aUrl, "");
                            writeBitmapToFile(cacheUri, mLoader.getStream(aUrl, null));
                            mDownloadingImageMap.remove(aUrl);
                            //read from file
                            File file = new File(cacheUri);
                            if (file.exists())
                                inputStream = new FileInputStream(new File(cacheUri));
                            mWebImageCache.add(localUri);
                        }
                    }
                    if (inputStream == null || inputStream.available() <= 0)
                    {
                        Log.i("KCWebImageDownloader", "error happen, read default image for:" + aUrl);
                        inputStream = new KCDefaultImageStream(mContext).getInputStream();
                    }
                    writeToOutStream(outputStream, inputStream);
                    if (aOnImageFinish!=null)
                        aOnImageFinish.onImageFinish(aUrl);
                }
                catch (Exception e)
                {
                    KCLog.e(e);
                }
            }
        });
    }

    private void writeToOutStream(OutputStream outputStream, InputStream inputStream)
    {
        try
        {
            if (outputStream != null && inputStream != null && inputStream.available() > 0)
            {
                byte[] buffer = new byte[4096];
                
                while (inputStream.read(buffer) != -1)
                {
                    outputStream.write(buffer);
                }
                outputStream.flush();
            }
            if (outputStream != null) outputStream.close();
        }
        catch (IOException e)
        {
            KCLog.e(e);
        }
    }

    private void writeBitmapToFile(String targetPath, InputStream inputStream) throws IOException
    {
        File file = new File(targetPath);
        synchronized (object)
        {
            if (!file.getParentFile().exists())
            {
                file.getParentFile().mkdirs();
            }
        }
        String name = file.getName().toLowerCase();
        Bitmap.CompressFormat format;
        if (name.contains("jpg") || name.contains("jpeg"))
        {
            format = Bitmap.CompressFormat.JPEG;
        }
        else if (name.contains("png"))
        {
            format = Bitmap.CompressFormat.PNG;
        }
        else if (name.contains("webp"))
        {
            if (Build.VERSION.SDK_INT >= 14) format = Bitmap.CompressFormat.WEBP;
            else format = Bitmap.CompressFormat.JPEG;
        }
        else
        {
            format = Bitmap.CompressFormat.JPEG;
        }
        //write to file
        try
        {
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStream fileOut = new BufferedOutputStream(fileOutputStream);
            bitmap.compress(format, 100, fileOut);
            bitmap.recycle();
            fileOut.flush();
            fileOut.close();
            fileOutputStream.close();
        }
        catch (Exception e)
        {
            if (KCLog.DEBUG)
            {
                KCLog.e(e);
            }
        }
        catch (OutOfMemoryError error)
        {
            if (KCLog.DEBUG)
            {
                KCLog.e(error);
            }
        }
    }
}
