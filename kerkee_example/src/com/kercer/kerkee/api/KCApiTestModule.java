package com.kercer.kerkee.api;

import android.widget.Toast;

import com.kercer.kerkee.bridge.KCArgList;
import com.kercer.kerkee.webview.KCWebView;

public class KCApiTestModule
{
	public static void testInfo(final KCWebView aWebView, KCArgList aArgList)
	{
		String str = aArgList.getString("testInfo");
		Toast.makeText(aWebView.getContext(), str, Toast.LENGTH_SHORT).show();
	}
}
