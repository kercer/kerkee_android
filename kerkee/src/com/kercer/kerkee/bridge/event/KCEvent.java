package com.kercer.kerkee.bridge.event;

import com.kercer.kerkee.bridge.KCArgList;
import com.kercer.kerkee.bridge.KCJSDefine;
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

	/**
	 * get all events
	 *
	 * @return
	 */
	public static Map<String, Integer> events()
	{
		return mEvents;
	}


	public static boolean hasEvent(String aEvent)
	{
		return mEvents.containsKey(aEvent);
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
