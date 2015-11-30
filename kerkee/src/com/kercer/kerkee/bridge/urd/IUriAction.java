package com.kercer.kerkee.bridge.urd;

import com.kercer.kernet.uri.KCNameValuePair;

import java.util.List;

/**
 * Created by liweisu on 15/9/6.
 *
 * Optimization by zihong on 15/9/17
 *
 */
public interface IUriAction
{
	/**
	 * Determine whether to perform custom operations protocol,if accept return true,else return false.
	 */
	boolean accept(String aHost, String aPath);

	/**
	 * if accept function return true,invokeAction can be called,you call do something here
	 */
	void invokeAction(List<KCNameValuePair> aParams);
}
