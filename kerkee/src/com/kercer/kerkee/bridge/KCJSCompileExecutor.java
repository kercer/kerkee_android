package com.kercer.kerkee.bridge;

import android.annotation.SuppressLint;

import com.kercer.kercore.debug.KCLog;
import com.kercer.kerkee.bridge.type.KCCallback;
import com.kercer.kerkee.webview.KCWebView;

import java.util.HashMap;
import java.util.List;

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

	@Deprecated
	public static void compileJS(final KCWebView aWebview, String aJS, KCCallback aReturnCallback)
	{
		compileJS(aWebview, aReturnCallback, aJS);
	}

	public static void compileJS(final KCWebView aWebview, KCCallback aReturnCallback, String aJS)
	{
		mIdentity ++;
		mCallBackMap.put(mIdentity, aReturnCallback);
		aWebview.addJSCompileIdentity(mIdentity);

		String escapedJavascript = aJS.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\"", "\\\\\"");
		String finalCode =
				"ApiBridge.compile(" + mIdentity +
						", \"" + escapedJavascript + "\");";
		KCLog.v(finalCode);

		KCJSExecutor.callJSOnMainThread(aWebview, finalCode);
	}

	public static void compileFunction(final KCWebView aWebview, KCCallback aReturnCallback, String aJSFunctionName, Object... aJSArgs)
	{
		compileJS(aWebview, aReturnCallback, KCMethod.toJS(aJSFunctionName, aJSArgs));
	}

	public static void release(List<Integer> aIdentities)
	{
		for (Integer identity: aIdentities)
		{
			mCallBackMap.remove(identity);
		}
	}

	protected static void didCompile(KCWebView aWebView, Integer aIdentity, Object aReturnValue, String aError)
	{
		aWebView.removeJSCompileIdentity(aIdentity);
		KCCallback callback = mCallBackMap.remove(aIdentity);
		if (callback != null)
		{
			callback.callback(aReturnValue, new KCJSError(aError));
		}

	}
}
