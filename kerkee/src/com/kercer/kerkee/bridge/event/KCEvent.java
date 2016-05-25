package com.kercer.kerkee.bridge.event;

import com.kercer.kercore.util.KCUtilText;
import com.kercer.kerkee.bridge.KCArgList;
import com.kercer.kerkee.bridge.KCJSDefine;
import com.kercer.kerkee.bridge.type.KCJSCallback;
import com.kercer.kerkee.webview.KCWebView;

import java.util.HashMap;
import java.util.Map;

public class KCEvent
{
	private final static Map<String, Integer> mEvents = new HashMap<String, Integer>();


	/*****************************************************
	 * Native API,called by Native
	 *
	 ******************************************************/

	public static Map<String, Integer> events()
	{
		return mEvents;
	}

	public static KCJSCallback getCallback(String aEvent)
	{
		KCJSCallback callback = null;
		if (!KCUtilText.isEmpty(aEvent))
		{
			Integer callbackID = mEvents.get(aEvent);
			callback = new KCJSCallback(callbackID.toString());
		}
		return callback;
	}

	public static boolean hasEvent(String aEvent)
	{
		return mEvents.containsKey(aEvent);
	}

	public static boolean callEvent(KCWebView aWebView, String aEvent, Object... aArgs)
	{
		boolean isOk = false;
		KCJSCallback callback = getCallback(aEvent);
		if (callback != null)
		{
			callback.callbackToJS(aWebView, aArgs);
			isOk = true;
		}
		return isOk;
	}

	public static boolean callEvent(KCWebView aWebView, String aEvent)
	{
		boolean isOk = false;
		KCJSCallback callback = getCallback(aEvent);
		if (callback != null)
		{
			callback.callbackToJS(aWebView);
			isOk = true;
		}
		return isOk;
	}


	/*****************************************************
	 * JS API,called by JS
	 *
	 ******************************************************/


	/**
	 * addEventListener called by js
	 * @param aWebView
	 * @param aArgList
	 */
	public static void addEventListener(final KCWebView aWebView, KCArgList aArgList)
	{
		String callbackId = aArgList.getString(KCJSDefine.kJS_callbackId);
		Integer nCallbackId = Integer.valueOf(callbackId);
		String eventName = aArgList.getString(KCJSDefine.kJS_event);

		synchronized (mEvents)
		{
			mEvents.put(eventName, nCallbackId);
		}

	}


}
