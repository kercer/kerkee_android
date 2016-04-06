package com.kercer.kerkee.manifest;

import com.kercer.kercore.util.KCUtilText;
import com.kercer.kernet.uri.KCURI;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class KCFetchManifest
{

	//retrun the key is url of manifest, the value is manifest object
	public static Map<String, KCManifestObject> fetchServerManifests(String aUrlManifest)
	{
		Map mapManifestObjects = new HashMap<String, KCManifestObject>();
		fetchServerManifests(mapManifestObjects, aUrlManifest);
		return mapManifestObjects;
	}

	public static void fetchServerManifests(Map<String, KCManifestObject> aOutMapManifestObjects, String aUrlManifest)
	{
		try
		{
			KCURI uri = KCURI.parse(aUrlManifest);
			String segment = uri.getLastPathSegment();
			uri.removeLastPathSegment();

			fetchServerManifests(aOutMapManifestObjects, uri, File.separator+segment, aUrlManifest);
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}

	}

	private static void fetchServerManifests(Map<String, KCManifestObject> aOutMapManifestObjects, KCURI aBaseUri, String aRelativePath, String aUrlManifest)
	{
		if (aOutMapManifestObjects == null) return;

		KCManifestObject mo = fetchOneServerManifest(aUrlManifest, aBaseUri, aRelativePath);
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
				String relativeBase = aRelativePath.substring(0, aRelativePath.lastIndexOf(File.separator));
				for (int i = 0; i < mfUrl.length; i++)
				{
					String m = mfUrl[i].replace("./", KCUtilText.EMPTY_STR);
					String subUrlManifest = urlDir + File.separator + m;
					String relativePath = relativeBase +File.separator+ m;
					fetchServerManifests(aOutMapManifestObjects, aBaseUri, relativePath, subUrlManifest);
				}
			}
		}
	}




//	public static void fetchOneServerManifest(KCHttpListener aListener, String aUrlManifest)
//	{
//		KCRequestRunner requestRunner = KerNet.newRequestRunner(null);
//		KCHttpRequest<KCManifestObject> request = new KCHttpRequest<KCManifestObject>(KCHttpRequest.Method.GET, aUrlManifest, new KCHttpListener() {
//			@Override
//			public void onHttpComplete(KCHttpRequest<?> request, KCHttpResponse response) {
//				ByteArrayInputStream in = new ByteArrayInputStream(response.getContent());
//			}
//
//			@Override
//			public void onHttpError(KCNetError error) {
//
//			}
//
//			@Override
//			public void onResponseHeaders(KCStatusLine aStatusLine, KCHeaderGroup aHeaderGroup) {
//
//			}
//		}) {
//		};
//		requestRunner.startAsyn(request);
//	}

	public static KCManifestObject fetchOneServerManifest(String aUrlManifest)
	{
		try
		{
			KCURI uri = KCURI.parse(aUrlManifest);
			String segment = uri.getLastPathSegment();
			uri.removeLastPathSegment();

			return fetchOneServerManifest(aUrlManifest, uri, File.separator+segment);
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private static KCManifestObject fetchOneServerManifest(String aUrlManifest, KCURI aBaseUri, String aRelativePath)
	{
		HttpURLConnection urlConn = null;
		InputStream in = null;
		try
		{
			URL url = new URL(aUrlManifest);
			urlConn = (HttpURLConnection) url.openConnection();
			in = urlConn.getInputStream();
			KCManifestObject manifestObject = KCManifestParser.ParserManifest(in);
			manifestObject.mBaseUri = aBaseUri;
			manifestObject.mRelativePath = aRelativePath;
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
		try
		{
			KCURI uri = KCURI.parse(aLocalManifestPath);
			String segment = uri.getLastPathSegment();
			uri.removeLastPathSegment();

			fetchLocalManifests(aOutMapManifestObjects, uri, File.separator + segment, aLocalManifestPath);
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}

	}

	public static void fetchLocalManifests(Map<String, KCManifestObject> aOutMapManifestObjects,KCURI aBaseUri, String aRelativePath, String aLocalManifestPath)
	{
		if (aOutMapManifestObjects == null) return;

		KCManifestObject mo = fetchOneLocalManifest(aLocalManifestPath, aBaseUri, aRelativePath);
		if (mo != null)
		{
			aOutMapManifestObjects.put(aLocalManifestPath, mo);
			String[] mfUrl = mo.getSubManifests();
			if (mfUrl != null)
			{
				String relativeBase = aRelativePath.substring(0, aRelativePath.lastIndexOf(File.separator));
				for (int i = 0; i < mfUrl.length; i++)
				{
					String m = mfUrl[i].replace("./", KCUtilText.EMPTY_STR);
					String subPath = mo.getDestDir() + File.separator + m;
					String relativePath = relativeBase +File.separator+ m;
					fetchLocalManifests(aOutMapManifestObjects,aBaseUri, relativePath, subPath);
				}
			}
		}
	}

	public static KCManifestObject fetchOneLocalManifest(String aFilePath)
	{
		try
		{
			KCURI uri = KCURI.parse(aFilePath);
			String segment = uri.getLastPathSegment();
			uri.removeLastPathSegment();

			return fetchOneLocalManifest(aFilePath, uri, File.separator + segment);
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private static KCManifestObject fetchOneLocalManifest(String aFilePath, KCURI aBaseUri, String aRelativePath)
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
				mf.mBaseUri = aBaseUri;
				mf.mRelativePath = aRelativePath;
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
