package com.kercer.kerkee.bridge.urd;


import com.kercer.kernet.uri.KCURI;

/**
 * Created by liweisu on 15/9/6.
 *
 * Optimization by zihong on 15/9/17
 *
 */
public interface IUriRegister
{
	boolean setDefaultAction(final IUriAction aDefaultAction);

	boolean registerAction(final IUriAction aAction);

	boolean unregisterAction(final IUriAction aAction);

	void dispatcher(final KCURI aUriData);

	String scheme();
}
