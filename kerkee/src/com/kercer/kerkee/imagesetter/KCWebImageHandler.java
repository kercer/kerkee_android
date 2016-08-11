package com.kercer.kerkee.imagesetter;

import java.util.HashMap;

/**
 * Created by liwei on 16/8/11.
 */
public class KCWebImageHandler implements KCWebImageListener {
    private HashMap<String,String> urls = new HashMap<>();
    private KCWebImageListener mWebImageListener;

    public KCWebImageHandler(KCWebImageListener mWebImageListener) {
        this.mWebImageListener = mWebImageListener;
    }

    public KCWebImageHandler add(String url){
        if (!urls.containsKey(url))
            urls.put(url,url);
        return this;
    }

    @Override
    public void onAllImageFinish(){
        if (mWebImageListener!=null)
            mWebImageListener.onAllImageFinish();
    }

    @Override
    public void onImageFinish(String url)
    {
        if (urls.containsKey(url))
            urls.remove(url);
        if (mWebImageListener!=null)
            mWebImageListener.onImageFinish(url);
        if (urls.size()<=0)
            onAllImageFinish();
    }
}
