package com.kercer.kerkee.webview;

import android.content.Context;

import com.kercer.kerkee.downloader.KCDownloader.KCScheme;

/**
 * 
 * @author zihong
 *
 */
public class KCWebPath
{
    protected KCScheme mBridgeScheme;  //the scheme is the same as KCApiBridge
    private String mRootPath = null;
    Context mContext;
    
    public KCWebPath(Context aContext)
    {
        mContext = aContext;
    }
    
    /**
     * 
     * @param aPath webview root path,if path if null, default root path is context'files dir
     *
     */
    public void setRootPath(String aPath)
    {
        mRootPath = aPath;
    }
    
    public String getRootPath()
    {
        if (mRootPath == null)
        {
        	if(mContext.getFilesDir() != null) {
        		mRootPath = mContext.getFilesDir().getAbsolutePath();
        	} else {
        		mRootPath = mContext.getExternalFilesDir(null).getAbsolutePath();
        	}
        }
        return mRootPath;
    }
    
    public String getResRootPath()
    {
        return getRootPath() + "/html";
    }
    
    public String getCfgPath()
    {
        return getResRootPath() + "/conf/urlmapping.conf";
    }
    
    public String getJSBridgePath()
    {
        return getResRootPath() + getJSBridgeRelativePath();
    }
    
    public String getJSBridgeRelativePath()
    {
        return "/bridgeLib.js";
    }
    
    public String getWebImageCacheRelativePath()
    {
        return "/cache/webimages";
    }
    
    public String getWebImageCachePath()
    {
        return getResRootPath() + getWebImageCacheRelativePath();
    }
    
    public KCScheme getBridgeScheme()
    {
        return mBridgeScheme;
    }
    
}
