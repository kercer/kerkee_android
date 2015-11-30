package com.kercer.kerkee.bridge.urd;

import android.text.TextUtils;

import com.kercer.kernet.uri.KCURI;

import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * Created by liweisu on 15/9/6.
 *
 * Optimization by zihong on 15/9/17
 *
 */
public final class KCUriDispatcher
{
	private static String mDefaultScheme;
	private static HashMap<String, IUriRegister> mUriRgisterMap = new HashMap<String, IUriRegister>();

	private KCUriDispatcher()
	{
	}

	public static void dispatcher(String url)
	{
		KCURI uri = null;
		try
		{
			uri = KCURI.parse(url);
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
		if (uri != null)
		{
			IUriRegister uriRegister = getUrlRegister(uri.getScheme());
			if (uriRegister != null)
			{
				uriRegister.dispatcher(uri);
			}
		}
	}

	public static boolean isUrdProtocol(String url)
	{
		KCURI uri = null;
		try
		{
			uri = KCURI.parse(url);
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
		if(uri!=null)
		{
			if (mUriRgisterMap.containsKey(uri.getScheme()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * before call defaultUriRegister,should set setDefaultScheme
	 * @param aScheme
	 */
	public static KCUriRegister markDefaultRegister(String aScheme)
	{
		KCUriRegister uriRegister = null;
		if (!TextUtils.isEmpty(aScheme))
		{
			mDefaultScheme = aScheme;
			uriRegister = addUriRegisterWithScheme(aScheme);
		}

		return uriRegister;
	}

	/**
	 * if you call markDefaultRegister to set defaultUriRegister once before you call defaultUriRegister
	 */
	public static KCUriRegister defaultUriRegister()
	{
		if (TextUtils.isEmpty(mDefaultScheme) || !mUriRgisterMap.containsKey(mDefaultScheme))
		{
			return null;
		}

		KCUriRegister uriRegister = (KCUriRegister)mUriRgisterMap.get(mDefaultScheme);
		if (uriRegister == null)
		{
			uriRegister = markDefaultRegister(mDefaultScheme);
		}
		return uriRegister;
	}

	/**
	 * add Uri Register
	 */
	public static KCUriRegister addUriRegisterWithScheme(String aScheme)
	{
		KCUriRegister uriRegister = null;
		if (!TextUtils.isEmpty(aScheme))
		{
			uriRegister = new KCUriRegister(aScheme);
			mUriRgisterMap.put(aScheme, uriRegister);
		}
		return uriRegister;
	}

	/**
	 * return a Uri Register
	 */
	public static synchronized IUriRegister getUrlRegister(String aScheme)
	{
		if (TextUtils.isEmpty(aScheme)) return null;

		return mUriRgisterMap.get(aScheme);
	}

	public boolean addUriRegister(IUriRegister aUriRegister)
	{
		if (aUriRegister != null)
		{
			String scheme = aUriRegister.scheme();
			if (!TextUtils.isEmpty(scheme))
			{
				mUriRgisterMap.put(scheme, aUriRegister);
				return true;
			}
		}
		return false;
	}
}
