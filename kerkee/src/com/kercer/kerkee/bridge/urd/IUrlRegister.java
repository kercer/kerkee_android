package com.kercer.kerkee.bridge.urd;

import com.kercer.kerkee.net.uri.KCURI;


/**
 * Created by liweisu on 15/9/6.
 * 
 * Optimization by zihong on 15/9/17
 * 
 */
public interface IUrlRegister
{
	boolean registerAction(IUrlAction aAction);

	boolean unregisterAction(IUrlAction aAction);

	void dispatcher(KCURI aUriData);

	String scheme();
}
