package com.kercer.kerkee.bridge;

import android.view.ViewConfiguration;

import com.kercer.kercore.debug.KCLog;
import com.kercer.kercore.task.KCTaskExecutor;
import com.kercer.kerkee.downloader.KCDownloader.KCScheme;
import com.kercer.kerkee.net.KCHttpServer;
import com.kercer.kerkee.util.KCUtil;
import com.kercer.kerkee.webview.KCWebPath;
import com.kercer.kerkee.webview.KCWebView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author zihong
 *
 */
public class KCApiBridge
{

	private final static KCRegister mRegister = new KCRegister();
	private static String mJS;
	private static boolean mIsOpenJSLog = true;

	public static void initJSBridgeEnvironment(KCWebView aWebview, KCScheme aScheme)
	{
		if (!aScheme.equals(KCScheme.FILE) && !KCHttpServer.isRunning())
			return;

		if (mJS == null)
		{
			KCWebPath webPath = aWebview.getWebPath();
			mJS = "var scriptBlock = document.createElement('script');";
			mJS += "scriptBlock.src='';";
			String jsFilePath = "file://" + webPath.getJSBridgePath();
			String jsHttpPath = KCHttpServer.getLocalHostUrl() + webPath.getJSBridgeRelativePath();
			String jsUri = aScheme.equals(KCScheme.FILE) ? jsFilePath : jsHttpPath;
			mJS += "scriptBlock.src='" + jsUri + "';";
			mJS += "scriptBlock.type = 'text/javascript';";
			mJS += "scriptBlock.language = 'javascript';";
			// mJS += "scriptBlock.onload=function(){ApiBridge.onBridgeInitComplete(function(){ApiBridge.onNativeInitComplete();});};";
			mJS += "scriptBlock.onload=function(){console.log('--- jsBridgeClient onLoad ---');};";
			mJS += "document.getElementsByTagName('head')[0].appendChild(scriptBlock);";
		}

		KCJSExecutor.callJSOnMainThread(aWebview, mJS);
	}

	public static KCRegister getRegister()
	{
		return mRegister;
	}

	public static KCClass getClass(String aJSObjectName)
	{
		return mRegister.getClass(aJSObjectName);
	}

	public static String callNative(KCWebView webView, String aJSONStr)
	{
		if (!"".equals(aJSONStr))
		{
			KCClassParser parser = new KCClassParser(aJSONStr);
			try
			{
				String jsClzName = parser.getJSClzName();
				String methodName = parser.getJSMethodName();
				KCArgList argList = parser.getArgList();

				KCClass clz = mRegister.getClass(jsClzName);
				if (clz != null)
				{
					clz.addJSMethod(methodName, argList);
				}

				KCMethod method = null;

				KCLog.d(">>>>>>>>> callNative: " + jsClzName + "." + methodName + ", " + method + ", " + aJSONStr);

				boolean isArgList = true;

				try
				{
					method = clz.getMethod(methodName, KCWebView.class, KCArgList.class);
				}
				catch (Exception e)
				{
					isArgList = false;
					if (argList.size() == 0)
					{
						method = clz.getMethod(methodName, KCWebView.class);
					}
					else
					{
						method = clz.getMethod(methodName, KCWebView.class, JSONObject.class);
					}

				}

				boolean isStatic = method.isStatic();
				KCJSObject receiver = null;
				if(!isStatic)
				{
					// get KCJSObject
					receiver = mRegister.getJSObject(jsClzName);
				}

				String result;
				Object[] argValues = new Object[method.getArgsCount()];
				argValues[0] = webView;
				if (method.getArgsCount() == 2)
				{
					Object value = null;
					if (isArgList)
						value = argList;
					else
						value = parser.getArgsJSON();
					argValues[1] = value;
				}

				result = (String)method.invoke(receiver, argValues);
				return result == null ? "" : result;

			}
			catch (Exception e)
			{
				KCLog.e("ERROR JS call " + parser.getJSClzName() + "::" + parser.getJSMethodName());
				e.printStackTrace();
			}
		}
		return "";
	}

	public static void setIsOpenJSLog(KCWebView aWebview, boolean aIsOpenJSLog)
	{
		mIsOpenJSLog = aIsOpenJSLog;
		if (aIsOpenJSLog)
		{
			KCJSExecutor.callJSFunction(aWebview, "jsBridgeClient.openJSLog", null);
		}
		else
		{
			KCJSExecutor.callJSFunction(aWebview, "jsBridgeClient.closeJSLog", null);
		}
	}

	public static void JSLog(KCWebView aWebview, KCArgList aArgList)
	{
		KCLog.e(aArgList.getString("msg"));
	}

	public static void onBridgeInitComplete(KCWebView aWebView, KCArgList aArgList)
	{
		KCLog.e(aArgList.toString());
		aWebView.documentReady(true);
		String callbackId = aArgList.getString(KCJSDefine.kJS_callbackId);

		try
		{
			JSONObject jsonConfig = new JSONObject();
			jsonConfig.put("isOpenJSLog", mIsOpenJSLog);
			KCJSExecutor.callbackJS(aWebView, callbackId, jsonConfig);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	public static void compile(KCWebView aWebView, KCArgList aArgList)
	{
		Object returnValue = aArgList.getObject(KCJSDefine.kJS_returnValue);
		String identity = aArgList.getString(KCJSDefine.kJS_identity);
		String error = aArgList.getString(KCJSDefine.kJS_error);

		KCJSCompileExecutor.didCompile(Integer.valueOf(identity), returnValue, error);
	}

	/**
	 * @param aWebview
	 *            destroy webview the hackery way, this method is used to destroy webview. since on Android 4.1.x, directly calling WebView.destroy()
	 *            sometimes causes WebViewCoreThread to wait indefinitely, which stops the WebView from working as usual.
	 */
	public static void hackDestroyWebView(final KCWebView aWebview)
	{
		KCLog.d(">>>>>> hackDestroyWebView called.");
		long timeout = ViewConfiguration.getZoomControlsTimeout();
		KCTaskExecutor.scheduleTaskOnUiThread(timeout, new Runnable() {
			@Override
			public void run() {
				aWebview.doDestroy();
			}
		});
	}

	public static void setHitPageBottomThreshold(KCWebView aWebview, JSONObject aJSONObject)
	{
		try
		{
			aWebview.setHitPageBottomThreshold(aJSONObject.getInt("threshold"));
		}
		catch (JSONException e)
		{
		}
	}
	public static void callbackJSOnHitPageBottom(KCWebView aWebview, int aY)
	{
		String y = String.valueOf(aY);
//		String js = "if(jsBridgeClient && jsBridgeClient.onHitPageBottom) jsBridgeClient.onHitPageBottom(" + y +")";
		StringBuilder js = KCUtil.getThreadSafeStringBuilder();
		js.append("if(jsBridgeClient && jsBridgeClient.onHitPageBottom) jsBridgeClient.onHitPageBottom(").append(y).append(")");

		KCJSExecutor.callJS(aWebview, js.toString());
	}

	public static void setPageScroll(KCWebView aWebview, KCArgList aArgList)
	{
		boolean isScrollOn = aArgList.getBoolean("isScrollOn");
		aWebview.setIsPageScrollOn(isScrollOn);
	}

	public static void callbackJSOnPageScroll(KCWebView aWebview, int aX, int aY, int aWith, int aHight)
	{
		StringBuilder js = KCUtil.getThreadSafeStringBuilder();
		js.append("if(jsBridgeClient && jsBridgeClient.onPageScroll) jsBridgeClient.onPageScroll(");
		js.append(String.valueOf(aX)).append(",");
		js.append(String.valueOf(aY)).append(",");
		js.append(String.valueOf(aWith)).append(",");
		js.append(String.valueOf(aHight)).append(")");

		KCJSExecutor.callJS(aWebview, js.toString());
	}

}
