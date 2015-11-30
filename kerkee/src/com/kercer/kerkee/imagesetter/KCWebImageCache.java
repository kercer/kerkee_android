package com.kercer.kerkee.imagesetter;

import android.content.Context;
import android.os.FileObserver;

import com.kercer.kercore.debug.KCLog;
import com.kercer.kerkee.util.KCUtilFile;
import com.kercer.kernet.uri.KCURI;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author zihong
 *
 */
public class KCWebImageCache
{
    Context mContext;

    private final ConcurrentHashMap<String, String> mImageFileMap = new ConcurrentHashMap<String, String>();
    private final static String DUMMY_STRING = "";
    // A reference to the DirWatcher MUST be kept so it will receive events.
    // see: http://stackoverflow.com/a/13521540/668963
    private KCDirWatcher mDirWatcher;

    File mWebImageDir;

    public KCWebImageCache(Context aContext)
    {
        super();
        mContext = aContext;
    }

    public void loadCache(FilenameFilter aFilenameFilter)
    {

        File webImageDir = null;
        try
        {
            webImageDir = getCacheDir();
            List<String> fileList = KCUtilFile.getFiles(webImageDir.getAbsolutePath(), true, true, aFilenameFilter);
            for (int i = 0; i < fileList.size(); ++i)
            {
                mImageFileMap.put(fileList.get(i), DUMMY_STRING);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (webImageDir != null)
        {
            try
            {
                mDirWatcher = new KCDirWatcher(webImageDir.getAbsolutePath(), FileObserver.DELETE | FileObserver.DELETE_SELF);
                mDirWatcher.startWatching();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    class KCDirWatcher extends FileObserver
    {
        public KCDirWatcher(String dir, int mask) throws IOException
        {
            super(dir, mask);
        }

        @Override
        public void onEvent(int event, String path)
        {
            if (event == DELETE)
            {
//                mImageFileMap.remove(mParentDir + "/" + path);
                mImageFileMap.remove("/" + path);
            }
            else if (event == DELETE_SELF)
            {
                mImageFileMap.clear();
                stopWatching();
                getCacheDir(); //check dir
                startWatching();
            }
        }
    }

    private void checkTerminalPath(final File aPath)
    {
        aPath.mkdirs(); //If the terminal directory already exists, answer false
    }

    public File getCacheDir()
    {
        if (mWebImageDir == null)
        {
            File baseDir = mContext.getExternalCacheDir();
            if (baseDir == null)
            {
                baseDir = new File(mContext.getFilesDir(), "cache");
            }
            baseDir.mkdirs();
            mWebImageDir = new File(baseDir, "webimages");
        }
        checkTerminalPath(mWebImageDir);
        return mWebImageDir;
    }

    public void setCacheDir(File aDir)
    {
        if (aDir != null)
        {
            mWebImageDir = aDir;
            checkTerminalPath(aDir);
        }
    }

    public static File getInternalCacheDirectory(Context context)
    {
        File internalCacheDirectory = new File(context.getCacheDir(), "temp-images");
        if (!internalCacheDirectory.exists())
        {
            if (!internalCacheDirectory.mkdir())
            {
                KCLog.i("Failed creating temporary storage directory, this is probably not good");
            }
        }
        return internalCacheDirectory;
    }


    public void add(KCURI aUri)
    {
        mImageFileMap.put(aUri.getPath(), DUMMY_STRING);
    }

    public void remove(KCURI aUri)
    {
        mImageFileMap.remove(aUri.getPath());
    }

    public boolean containsCache(KCURI aUri)
    {
        return mImageFileMap.containsKey(aUri.getPath());
    }


}
