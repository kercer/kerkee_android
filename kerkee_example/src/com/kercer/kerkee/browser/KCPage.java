package com.kercer.kerkee.browser;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 *
 * @author zihong
 *
 */
public class KCPage
{
    protected Context mContext;
    private View mView;

    public KCPage(Context context, int layoutId)
    {
        mContext = context;
        mView = LayoutInflater.from(context).inflate(layoutId, null);
        init();
    }

    public KCPage(Context context, View view)
    {
        mContext = context;
        mView = view;
        init();
    }

    private void init()
    {
    }

    public View getView()
    {
        return mView;
    }

    public View findViewById(int id)
    {
        return mView.findViewById(id);
    }
}

