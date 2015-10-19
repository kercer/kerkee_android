package com.kercer.kerkee.bridge;

import android.annotation.SuppressLint;

import com.kercer.kerkee.log.KCLog;
import com.kercer.kerkee.webview.KCWebView;

import java.util.HashMap;

/**
 *
 * @author zihong
 *
 */

@SuppressLint("UseSparseArrays")
public class KCJSCompileExecutor
{
	private static HashMap<Integer, KCCallback> mCallBackMap = new HashMap<Integer, KCCallback>();
	private static Integer mIdentity = 0;

	public static void compileJS(final KCWebView aWebview, String aJS, KCCallback aCallback)
	{
		mIdentity ++;
		mCallBackMap.put(mIdentity, aCallback);

		String escapedJavascript = aJS.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\"", "\\\\\"");
		String finalCode =
					"ApiBridge.compile(" + mIdentity +
					", \"" + escapedJavascript + "\");";
		KCLog.v( finalCode);

		KCJSExecutor.callJSOnMainThread(aWebview, finalCode);

	}


	protected static void didCompile(Integer aIdentity, String aReturnValue, String aError)
	{
		KCCallback callback = mCallBackMap.remove(aIdentity);
		if (callback != null)
		{
			callback.callback(aReturnValue, new KCJSError(aError));
		}

	}
}
