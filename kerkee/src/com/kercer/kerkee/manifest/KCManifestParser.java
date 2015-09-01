package com.kercer.kerkee.manifest;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.kercer.kerkee.util.KCUtilString;

public class KCManifestParser
{

	public final static String VERSION = "Version:";
	public final static String LIST = "List:";
	public final static String REQUIREDVERSION = "RequiredVersion:";
	public final static String ZIP = "Zip:";
	public final static String CACHE = "CACHE:";
	public final static String POUND_SIGN = "#";
	public final static String COLON = ":";
	public final static String SPACE = " ";
	public final static String COMMA = ",";
	public final static String EXTRA = "Extra:";

	public static KCManifestObject ParserManifest(InputStream aIn)
	{
		if (aIn != null)
		{
			try
			{

				String line;
				String trimline;
				String value;

				int cachelineIndex = -1;
				int lineIndex = 0;

				BufferedReader reader = new BufferedReader(new InputStreamReader(aIn), 1024);

				KCManifestObject mo = new KCManifestObject();
				ArrayList<String> cacheList = null;
				ArrayList<String> cacheDirs = null;
				while ((line = reader.readLine()) != null)
				{
					lineIndex++;
					trimline = line.replaceAll(" +", "");
					if (KCUtilString.isEmpty(trimline))
						continue;
					if (trimline.startsWith(POUND_SIGN))
					{
						if (trimline.contains(POUND_SIGN + VERSION))
						{
							value = getCommentValue(VERSION, trimline);
							mo.setVersion(value);
						}
						if (trimline.contains(POUND_SIGN + LIST))
						{
							value = getCommentValue(LIST, trimline);
							if (!KCUtilString.isEmpty(value))
							{
								mo.setSubManifests(value.split(COMMA));
							}
						}
						if (trimline.contains(POUND_SIGN + EXTRA))
						{
							value = getCommentValue(POUND_SIGN + EXTRA, trimline);
							if (!KCUtilString.isEmpty(value))
							{
								mo.setExtras(value.split(COMMA));
							}
						}
						if (trimline.contains(POUND_SIGN + REQUIREDVERSION))
						{
							value = getCommentValue(REQUIREDVERSION, trimline);
							mo.setRequiredVersion(value);
						}
						if (trimline.contains(POUND_SIGN + ZIP))
						{
							value = getCommentValue(ZIP, trimline);
							mo.setZipFile(value);
						}
						cachelineIndex = -1;

					}
					else if (trimline.startsWith(CACHE))
					{
						cachelineIndex = lineIndex;
						cacheList = new ArrayList<String>();
						cacheDirs = new ArrayList<String>();

					}
					else if (trimline.endsWith(COLON))
					{
						cachelineIndex = -1;
					}
					if (cachelineIndex != -1 && !trimline.equals(CACHE))
					{
						if (isInExtra(mo, trimline))
						{
							continue;
						}
						cacheList.add(trimline);
						String dir = File.separator;
						if (trimline.contains(File.separator))
						{
							dir = trimline.substring(0, trimline.lastIndexOf(File.separator) + 1);
						}
						if (!isInSubManifest(mo, dir) || File.separator.equals(dir))
						{
							if (!cacheDirs.contains(dir))
								cacheDirs.add(dir);

						}
					}
				}

				if (cacheList != null)
				{
					mo.setCacheList(cacheList);
				}
				if (cacheDirs != null)
				{
					mo.setCacheDirs(cacheDirs);
				}
				reader.close();
				return mo;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	private static boolean isInExtra(KCManifestObject aMO, String aFileName)
	{
		String[] extras = aMO.getExtras();
		if (extras != null)
		{
			for (int i = 0; i < extras.length; i++)
			{
				if (aFileName.contains(extras[i]))
				{
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isInSubManifest(KCManifestObject aMO, String aDir)
	{
		String[] subManifests = aMO.getSubManifests();
		if (subManifests != null)
		{
			for (int i = 0; i < subManifests.length; i++)
			{
				if (subManifests[i].contains(aDir))
				{
					return true;
				}
			}
		}
		return false;
	}

	public static String getCommentValue(String aKey, String aLine)
	{
		int startIndex = aLine.indexOf(aKey) + aKey.length();
		return aLine.substring(startIndex).trim();
	}
}
