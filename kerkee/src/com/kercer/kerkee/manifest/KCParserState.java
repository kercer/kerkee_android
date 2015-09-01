package com.kercer.kerkee.manifest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import com.kercer.kerkee.util.KCUtilString;

public class KCParserState
{

	public final static String CAHCHE_MANIFEST = "cache.manifest";

	
	private String urlToLocalPath(String aUrl)
	{
		/**
		 * ToDo
		 */
		return null;
	}

	public void parserServerManifests(HashMap<String, KCManifestObject> aServerMfs, String aBaseUrl)
	{
		String fullUrl;
		if (aBaseUrl != null && aBaseUrl.endsWith(File.separator))
		{
			fullUrl = aBaseUrl + CAHCHE_MANIFEST;
		}
		else
		{
			fullUrl = aBaseUrl + File.separator + CAHCHE_MANIFEST;
		}

		KCManifestObject mo = parserServerManifest(fullUrl);
		
		if (mo != null)
		{
			mo.setDownloadUrl(fullUrl.replace(CAHCHE_MANIFEST, mo.getZipFilePath()));
			fullUrl = urlToLocalPath(fullUrl);
			mo.setDestFile(fullUrl + File.separator + mo.getZipFilePath());
			mo.setDestDir(fullUrl);
			String key = fullUrl + File.separator + CAHCHE_MANIFEST;
			aServerMfs.put(key, mo);
			String[] mfUrl = mo.getSubManifests();
			if (mfUrl != null)
			{
				for (int i = 0; i < mfUrl.length; i++)
				{
					fullUrl = aBaseUrl + File.separator + mfUrl[i].replace("./", KCUtilString.EMPTY_STR);
					mo = parserServerManifest(fullUrl);
					if (mo != null)
					{
						mo.setDownloadUrl(fullUrl.replace(CAHCHE_MANIFEST, mo.getZipFilePath()));
						fullUrl = urlToLocalPath(fullUrl);
						mo.setDestFile(fullUrl + File.separator + mo.getZipFilePath());
						mo.setDestDir(fullUrl);
						key = fullUrl + File.separator + CAHCHE_MANIFEST;
						aServerMfs.put(key, mo);
					}
				}
			}
		}
	}
	
	public static KCManifestObject parserServerManifest(String aUrlStr)
	{
		HttpURLConnection urlConn = null;
		InputStream in = null;
		try
		{
			URL url = new URL(aUrlStr);
			urlConn = (HttpURLConnection) url.openConnection();
			in = urlConn.getInputStream();
			return KCManifestParser.ParserManifest(in);
		}
		catch (Exception e)
		{

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

	public static void parserLocalManifests(HashMap<String, KCManifestObject> aLocalMfs, String aLocalDir)
	{
		String realPath = aLocalDir + File.separator + CAHCHE_MANIFEST;
		KCManifestObject mo = parserLocalManifest(realPath);

		if (mo != null)
		{
			mo.setDestDir(realPath.substring(0, realPath.lastIndexOf(File.separator)));
			aLocalMfs.put(realPath, mo);
			String[] mfUrl = mo.getSubManifests();
			if (mfUrl != null)
			{
				for (int i = 0; i < mfUrl.length; i++)
				{
					realPath = aLocalDir + File.separator + mfUrl[i].replace("./", KCUtilString.EMPTY_STR);

					mo = parserLocalManifest(realPath);
					mo.setDestDir(realPath.substring(0, realPath.lastIndexOf(File.separator)));
					aLocalMfs.put(realPath, mo);
				}
			}
		}
	}

	public static KCManifestObject parserLocalManifest(String aFilePath)
	{
		File file = new File(aFilePath);

		if (file.exists())
		{
			try
			{
				InputStream in = new FileInputStream(file);
				KCManifestObject mf = KCManifestParser.ParserManifest(in);
				in.close();
				return mf;
			}
			catch (Exception e)
			{

			}
		}
		return null;
	}

}
