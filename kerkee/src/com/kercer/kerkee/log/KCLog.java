package com.kercer.kerkee.log;

import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author zihong
 *
 */
public class KCLog
{
	private interface KCLogType
	{
		int d = Log.DEBUG;
		int i = Log.INFO;
		int w = Log.WARN;
		int e = Log.ERROR;
		int v = Log.VERBOSE;
		int wtf = 1000;
		
	}

	public static String TAG = "kerkee";

//	public static boolean DEBUG = BuildConfig.DEBUG;
	public static boolean DEBUG = Log.isLoggable(TAG, Log.VERBOSE);

	/**
	 * {@code adb shell setprop log.tag.&lt;tag&gt;}
	 */
	public static void setTag(String tag)
	{
		d("Changing log tag to %s", tag);
		TAG = tag;

		// Reinitialize the DEBUG "constant"
		DEBUG = Log.isLoggable(TAG, Log.VERBOSE);
	}

	private static String buildMessage(String aMessage)
	{
		if (!KCLog.DEBUG || aMessage == null)
			return aMessage;
		
		StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace(); //index begin 5
//		StackTraceElement[] stackTraces = new Throwable().fillInStackTrace().getStackTrace(); //index begin 3
		int indexBegin = 5;
		
		StackTraceElement stackTrace = stackTraces[indexBegin];
		
		String caller = "<unknown>";
		// It will be at least two frames up, so start there.
		for (int i = indexBegin; i < stackTraces.length; i++)
		{
			stackTrace = stackTraces[i];
//			Class<?> clazz = stackTrace.getClass();			
			String className = stackTraces[i].getClassName();
			
			String logClassName = KCLog.class.getName();
			if (!className.equals(logClassName))
			{
				
				className = className.substring(className.lastIndexOf('.') + 1);
				className = className.substring(className.lastIndexOf('$') + 1);

				caller = className + "." + stackTraces[i].getMethodName();
				break;
			}
		}
		
		return String.format(Locale.US, "[%d] %s#%d: \n%s", Thread.currentThread().getId(), caller,stackTrace.getLineNumber(), aMessage);
	}
	
	private static void log(int aType, String aTag, String format, Object... args)
	{
		String msg = (args == null) ? format : String.format(Locale.US, format, args);
		KCLog.log(aType, aTag, msg);
	}
	
	private static void log(int aType, String aTag, String aMessage)
	{
		aMessage = buildMessage(aMessage);
		
		switch (aType)
		{
		case KCLogType.d:
			Log.d(aTag, aMessage);
			break;
		case KCLogType.i:
			Log.i(aTag, aMessage);
			break;
		case KCLogType.w:
			Log.w(aTag, aMessage);
			break;
		case KCLogType.e:
			Log.e(aTag, aMessage);
			break;
		case KCLogType.v:
			Log.v(aTag, aMessage);
			break;
		case KCLogType.wtf:
			Log.wtf(aTag, aMessage);
			break;
		default:
			break;
		}
	}
	

	public static void d(String aMessage)
	{
		KCLog.log(KCLogType.d, TAG, aMessage);
	}	
	public static void d(String aFormat, Object... aArgs)
	{
		KCLog.log(KCLogType.d, TAG, aFormat, aArgs);
	}
	public static void d(String aTag, String aMessage)
	{
		Log.d(aTag, buildMessage(aMessage));
	}

	public static void i(String aMessage)
	{
		KCLog.log(KCLogType.i, TAG, aMessage);
	}
	public static void i(String aTag, String aMessage)
	{
		KCLog.log(KCLogType.i, aTag, aMessage);
	}
	
	
	public static void w(String aMessage)
	{
		KCLog.log(KCLogType.w, TAG, aMessage);
	}
	public static void w(String aTag, String aMessage)
	{
		KCLog.log(KCLogType.w, aTag, aMessage);
	}

	public static void e(String aMessage)
	{
		KCLog.log(KCLogType.e, TAG, aMessage);
	}
	public static void e(String aFormat, Object... aArgs)
	{
		KCLog.log(KCLogType.e, TAG, aFormat, aArgs);
	}
//	public static void e(Throwable tr, String format, Object... args)
//	{
//		Log.e(TAG, buildMessage(format, args), tr);
//	}
	public static void e(String aTag, String aMessage)
	{
		KCLog.log(KCLogType.e, aTag, aMessage);
	}


	public static void v(String aMessage)
	{
		KCLog.log(KCLogType.v, TAG, aMessage);
	}
	public static void v(String aFormat, Object... aArgs)
	{
		if (DEBUG)
		{
			KCLog.log(KCLogType.v, TAG, aFormat, aArgs);
		}
	}
	public static void v(String aTag, String aMessage)
	{
		KCLog.log(KCLogType.v, aTag, aMessage);
	}

	public static void wtf(String aFormat, Object... aArgs)
	{
		KCLog.log(KCLogType.wtf, TAG, aFormat, aArgs);
	}
//	public static void wtf(Throwable tr, String format, Object... args)
//	{
//		Log.wtf(TAG, buildMessage(format, args), tr);
//	}
	

//	private static void writeToFile(File aFile, String aContent)
//	{
//		if (!aFile.exists())
//		{
//			try
//			{
//				aFile.createNewFile();
//			}
//			catch (IOException e)
//			{
//				e.printStackTrace();
//			}
//		}
//		if (aFile.exists())
//		{
//			FileWriter writer;
//			try
//			{
//				writer = new FileWriter(aFile.getAbsolutePath(), true);
//				writer.write("\r\n");
//				writer.write(aContent);
//				writer.close();
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
//	}

	/**
	 * A simple event log with records containing a name, thread ID, and timestamp.
	 */
	public static class KCMarkerLog
	{
		public static final boolean ENABLED = KCLog.DEBUG;

		/** Minimum duration from first marker to last in an marker log to warrant logging. */
		private static final long MIN_DURATION_FOR_LOGGING_MS = 0;

		private static class KCMarker
		{
			public final String name;
			public final long thread;
			public final long time;

			public KCMarker(String name, long thread, long time)
			{
				this.name = name;
				this.thread = thread;
				this.time = time;
			}
		}

		private final List<KCMarker> mMarkers = new ArrayList<KCMarker>();
		private boolean mFinished = false;

		/** Adds a marker to this log with the specified name. */
		public synchronized void add(String name, long threadId)
		{
			if (mFinished)
			{
				throw new IllegalStateException("Marker added to finished log");
			}

			mMarkers.add(new KCMarker(name, threadId, SystemClock.elapsedRealtime()));
		}

		/**
		 * Closes the log, dumping it to logcat if the time difference between the first and last markers is greater than
		 * {@link #MIN_DURATION_FOR_LOGGING_MS}.
		 *
		 * @param header
		 *            Header string to print above the marker log.
		 */
		public synchronized void finish(String header)
		{
			mFinished = true;

			long duration = getTotalDuration();
			if (duration <= MIN_DURATION_FOR_LOGGING_MS)
			{
				return;
			}

			long prevTime = mMarkers.get(0).time;
			d("(%-4d ms) %s", duration, header);
			for (KCMarker marker : mMarkers)
			{
				long thisTime = marker.time;
				d("(+%-4d) [%2d] %s", (thisTime - prevTime), marker.thread, marker.name);
				prevTime = thisTime;
			}
		}

		@Override
		protected void finalize() throws Throwable
		{
			// Catch requests that have been collected (and hence end-of-lifed)
			// but had no debugging output printed for them.
			if (!mFinished)
			{
				finish("Request on the loose");
				e("Marker log finalized without finish() - uncaught exit point for request");
			}
		}

		/** Returns the time difference between the first and last events in this log. */
		private long getTotalDuration()
		{
			if (mMarkers.size() == 0)
			{
				return 0;
			}

			long first = mMarkers.get(0).time;
			long last = mMarkers.get(mMarkers.size() - 1).time;
			return last - first;
		}
	}

}
