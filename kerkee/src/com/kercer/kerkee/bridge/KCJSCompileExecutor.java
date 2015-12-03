package com.kercer.kerkee.bridge;

import android.annotation.SuppressLint;

import com.kercer.kercore.debug.KCLog;
import com.kercer.kerkee.bridge.type.KCCallback;
import com.kercer.kerkee.util.KCUtil;
import com.kercer.kerkee.webview.KCWebView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
		KCLog.v(finalCode);

		KCJSExecutor.callJSOnMainThread(aWebview, finalCode);

	}

	public static void compileFunction(final KCWebView aWebview, String aJSFunctionName, Object[] aObjects, KCCallback aCallback)
	{
		StringBuilder js = KCUtil.getThreadSafeStringBuilder().append(aJSFunctionName).append('(');

		int lenth = aObjects.length;
		for (int i = 0; i < lenth; ++i)
		{
			Object obj = aObjects[i];
			if(obj != null)
			{
				if(obj instanceof String)
				{
					js.append("'");
					js.append(obj.toString());
					js.append("'");
				}
				else if (obj instanceof Collection)
				{
					JSONArray jsonarray = new JSONArray((Collection)obj);
					js.append(jsonarray.toString());
				}
				else if (obj instanceof Map)
				{
					JSONObject jsonObject = new JSONObject((Map)obj);
					js.append(jsonObject);
				}
				else
				{
					js.append(obj.toString());
				}
			}

			if (i < lenth-1)
			{
				js.append(',');
			}
		}
		js.append(')');
		compileJS(aWebview, js.toString(), aCallback);
	}


	protected static void didCompile(Integer aIdentity, Object aReturnValue, String aError)
	{
		KCCallback callback = mCallBackMap.remove(aIdentity);
		if (callback != null)
		{
			callback.callback(aReturnValue, new KCJSError(aError));
		}

	}
}
