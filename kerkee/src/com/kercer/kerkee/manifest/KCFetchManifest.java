package com.kercer.kerkee.manifest;

import com.kercer.kerkee.util.KCUtilString;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class KCFetchManifest
{
	
	public static Map<String, KCManifestObject> fetchServerManifests(String aUrlManifest)
	{
		Map mapManifestObjects = new HashMap<String, KCManifestObject>();

		fetchServerManifests(mapManifestObjects, aUrlManifest);

		return mapManifestObjects;
	}

	public static void fetchServerManifests(Map<String, KCManifestObject> aOutMapManifestObjects, String aUrlManifest)
	{
		KCManifestObject mo = fetchOneServerManifest(aUrlManifest);

		if (mo != null)
		{
			String urlDir = aUrlManifest.substring(0, aUrlManifest.lastIndexOf(File.separator));
			if (mo.getDownloadUrl() == null)
			{
				mo.setDownloadUrl(urlDir + mo.getDekRelativePath());
			}

			aOutMapManifestObjects.put(aUrlManifest, mo);
			String[] mfUrl = mo.getSubManifests();
			if (mfUrl != null)
			{
				for (int i = 0; i < mfUrl.length; i++)
				{
					String subUrlManifest = urlDir + File.separator + mfUrl[i].replace("./", KCUtilString.EMPTY_STR);
					fetchServerManifests(aOutMapManifestObjects, subUrlManifest);
				}
			}
		}
	}

	public static KCManifestObject fetchOneServerManifest(String aUrlManifest)
	{
		HttpURLConnection urlConn = null;
		InputStream in = null;
		try
		{
			URL url = new URL(aUrlManifest);
			urlConn = (HttpURLConnection) url.openConnection();
			in = urlConn.getInputStream();
			KCManifestObject manifestObject = KCManifestParser.ParserManifest(in);
			return manifestObject;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (in != null)
					in.close();
				if (urlConn != null)
					urlConn.disconnect();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	//retrun the key is local full path of manifest, the value is manifest object
	public static Map<String, KCManifestObject> fetchLocalManifests(String aLocalManifestPath)
	{
		Map mapManifestObjects = new HashMap<String, KCManifestObject>();

		fetchLocalManifests(mapManifestObjects, aLocalManifestPath);

		return mapManifestObjects;
	}

	public static void fetchLocalManifests(Map<String, KCManifestObject> aOutMapManifestObjects, String aLocalManifestPath)
	{
		KCManifestObject mo = fetchOneLocalManifest(aLocalManifestPath);

		if (mo != null)
		{
			aOutMapManifestObjects.put(aLocalManifestPath, mo);
			String[] mfUrl = mo.getSubManifests();
			if (mfUrl != null)
			{
				for (int i = 0; i < mfUrl.length; i++)
				{
					String subPath = mo.getDestDir() + File.separator + mfUrl[i].replace("./", KCUtilString.EMPTY_STR);
					fetchLocalManifests(aOutMapManifestObjects, subPath);
				}
			}
		}
	}

	public static KCManifestObject fetchOneLocalManifest(String aFilePath)
	{
		File file = new File(aFilePath);

		if (file.exists())
		{
			try
			{
				InputStream in = new FileInputStream(file);
				KCManifestObject mf = KCManifestParser.ParserManifest(in);
				if (mf != null)
				{
					mf.setDestDir(aFilePath.substring(0, aFilePath.lastIndexOf(File.separator)));
				}

				in.close();
				return mf;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

}
