package com.kercer.kerkee.net;

import java.io.File;
import java.io.IOException;

/**
 * 
 * @author zihong
 *
 */
public class KCHttpServer
{
	// server
	public static KCHTTPDPooled mHttpServer;
	public static int mPort = 0;
	public static File mWWWRoot;
	public static boolean mIsRunning = false;

	public static void startServer(int aPort, File aWWWRoot)
	{
		if (aWWWRoot == null) return;
		
		mPort = aPort;
		mWWWRoot = aWWWRoot;
		if (mHttpServer == null)
			mHttpServer = new KCHTTPDPooled(aWWWRoot);

		boolean startSuccess = mHttpServer.start(mPort);
		for (int i = 0; !startSuccess && i < 10; ++i)
		{
			startSuccess = mHttpServer.start(0);
		}
		
		if (startSuccess)
		{
			mPort = mHttpServer.getPort();
			mIsRunning = true;
		}

	}

	public static void stopServer()
	{
		if (mHttpServer != null)
			mHttpServer.stop();
	}

	public static int getPort()
	{
		return mPort;
	}

	public static String getLocalHostUrl()
	{
		if (mHttpServer == null) 
			return null;
		return mHttpServer.getServerName();
	}

	public static File getRootDir()
	{
		return mWWWRoot;
	}
	
	public static boolean isRunning()
	{
		return mIsRunning;
	}
}
