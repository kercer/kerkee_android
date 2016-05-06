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
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zihong
 */
public class KCWebImageDownloader {
    //the key is url string
    private final ConcurrentHashMap<String, String> mDownloadingImageMap = new ConcurrentHashMap<String, String>();
    Context mContext;
    KCDefaultDownloader mLoader;
    KCWebImageCache mWebImageCache;

    public KCWebImageDownloader(final Context aContext, KCWebPath aWebPath) {
        mContext = aContext;
        mLoader = new KCDefaultDownloader(aContext);
        mWebImageCache = new KCWebImageCache(aContext);
        KCScheme scheme = aWebPath.getBridgeScheme();
        //scheme possible null
        if (scheme != null && scheme.equals(KCScheme.HTTP)) {
            mWebImageCache.setCacheDir(new File(aWebPath.getWebImageCachePath()));
        }
        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return true;
            }
        };
        mWebImageCache.loadCache(filenameFilter);
    }

    private String getCacheUri(KCURI aUri) {
        final String pathURI = aUri.getPath();
        String cacheUri;
        final String filePath = mWebImageCache.getCacheDir().getAbsolutePath() + pathURI;
        cacheUri = filePath;
        return cacheUri;
    }

    public KCWebImage downloadImageFile(final String aUrl) {
        final KCWebImage webImage = new KCWebImage();
        InputStream inputStream;
        try {
            //load from cache
            KCURI localUri = KCURI.parse(aUrl);
            String cacheUri = getCacheUri(localUri);
            KCURI kcuri = KCURI.parse(cacheUri);
            boolean hasCache = mWebImageCache.containsCache(localUri);
            if (KCScheme.ofUri(aUrl).equals(KCScheme.FILE)) {
                Log.i("KCWebImageDownloader", "read file:" + aUrl);
                inputStream = mLoader.getStream(aUrl, null);
                webImage.setInputStream(inputStream);
            } else if (hasCache) {
                Log.i("KCWebImageDownloader", "read cache:" + cacheUri);
                if (cacheUri != null) {
                    inputStream = mLoader.getStream("file://" + cacheUri, null);
                    webImage.setInputStream(inputStream);
                }
            } else {
                //download image from net
                if (!mDownloadingImageMap.containsKey(aUrl)) {
                    Log.i("KCWebImageDownloader", "read net:" + aUrl);
                    mDownloadingImageMap.put(aUrl, "");
                    inputStream = mLoader.getStream(aUrl, null);
                    mDownloadingImageMap.remove(aUrl);
                    writeBitmapToFile(cacheUri, inputStream);
                    inputStream.close();
                    //read from file
                    webImage.setInputStream(new FileInputStream(new File(cacheUri)));
                    mWebImageCache.add(kcuri);
                }
            }
        } catch (Exception e) {
            KCLog.e(e);
        }

        return webImage;
    }

    private void writeBitmapToFile(String targetPath, InputStream inputStream) throws IOException {
        File file = new File(targetPath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        String name = file.getName().toLowerCase();
        Bitmap.CompressFormat format;
        if (name.contains("jpg") || name.contains("jpeg")) {
            format = Bitmap.CompressFormat.JPEG;
        } else if (name.contains("png")) {
            format = Bitmap.CompressFormat.PNG;
        } else if (name.contains("webp")) {
            if (Build.VERSION.SDK_INT >= 14)
                format = Bitmap.CompressFormat.WEBP;
            else
                format = Bitmap.CompressFormat.JPEG;
        } else {
            format = Bitmap.CompressFormat.JPEG;
        }
        //write to file
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        OutputStream fileOut = new BufferedOutputStream(fileOutputStream);
        bitmap.compress(format, 100, fileOut);
        fileOut.flush();
        fileOut.close();
        fileOutputStream.close();
    }
}
