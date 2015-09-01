package com.kercer.kerkee.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.kercer.kerkee.BuildConfig;

import android.util.Log;

/**
 * 
 * @author zihong
 *
 */
public class KCLog
{
	public final static boolean DEBUG = BuildConfig.DEBUG;

	private static void log(int aType, String aMessage)
	{
		if (!KCLog.DEBUG || aMessage == null)
			return;
		StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[4];
		String className = stackTrace.getClassName();
		String tag = className.substring(className.lastIndexOf('.') + 1) + "." + stackTrace.getMethodName() + "#" + stackTrace.getLineNumber();
		switch (aType)
		{
		case Log.DEBUG:
			Log.d(tag, aMessage);
			break;
		case Log.INFO:
			Log.i(tag, aMessage);
			break;
		case Log.WARN:
			Log.w(tag, aMessage);
			break;
		case Log.ERROR:
			Log.e(tag, aMessage);
			break;
		case Log.VERBOSE:
			Log.v(tag, aMessage);
			break;
		}
	}

	public static void d(String aMessage)
	{
		log(Log.DEBUG, aMessage);
	}

	public static void i(String aMessage)
	{
		log(Log.INFO, aMessage);
	}

	public static void w(String aMessage)
	{
		log(Log.WARN, aMessage);
	}

	public static void e(String aMessage)
	{
		log(Log.ERROR, aMessage);
	}

	public static void v(String aMessage)
	{
		log(Log.VERBOSE, aMessage);
	}

	private static void writeToFile(File aFile, String aContent)
	{
		if (!aFile.exists())
		{
			try
			{
				aFile.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		if (aFile.exists())
		{
			FileWriter writer;
			try
			{
				writer = new FileWriter(aFile.getAbsolutePath(), true);
				writer.write("\r\n");
				writer.write(aContent);
				writer.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
