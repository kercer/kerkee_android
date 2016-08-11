package com.kercer.kerkee.imagesetter;

/**
 * 当前webview 加载图片完成回调接口
 * 内部使用
 * Created by liwei on 16/8/11.
 */
public interface KCWebImageListener {
    void onImageFinish(String url);
    void onAllImageFinish();
}
