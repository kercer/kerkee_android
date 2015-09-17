package com.kercer.kerkee.bridge.urd;

import java.util.List;

import com.kercer.kerkee.net.uri.KCNameValuePair;

/**
 * Created by liweisu on 15/9/6.
 * 
 * Optimization by zihong on 15/9/17
 * 
 */
public interface IUrlAction
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
