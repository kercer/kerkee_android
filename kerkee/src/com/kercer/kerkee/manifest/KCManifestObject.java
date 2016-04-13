package com.kercer.kerkee.manifest;

import com.kercer.kercore.util.KCUtilText;
import com.kercer.kernet.uri.KCURI;

import java.util.ArrayList;

public class KCManifestObject
{

	private String mVersion; // version
	private String[] mSubManifests; // sub manifests list
	private String[] mExtras; //extras files, don't delete files
	private ArrayList<String> mCacheList;
	private String mDekRelativePath;  //dek relative to manifest path, if dek path is url, it's null
	private String mRequiredVersion; //required version
	private ArrayList<String> mCacheDirs; //cache dirs, contains mCacheList' dir, not contains Extras and suManifests dir

	//if mDekRelativePath's scheme is not null, set it in Parser
	//if from server,set it in KCFetchManifest;
	//if from local,set it in KCManifestParser
	private String mDownloadUrl;
	//dek & manifest file Dir, if parser from server, the VAR is null
	private String mDestDir;

	public KCURI mBaseUri;  //root uri
	public String mRelativePath; //relative to base uri


	public KCManifestObject()
	{
		mRequiredVersion = KCUtilText.EMPTY_STR;
		mVersion = KCUtilText.EMPTY_STR;
		mDekRelativePath = KCUtilText.EMPTY_STR;
	}

	public String getVersion()
	{
		return mVersion;
	}

	public void setVersion(String aVersion)
	{
		this.mVersion = aVersion;
	}

	public String getRequiredVersion()
	{
		return mRequiredVersion;
	}

	public void setRequiredVersion(String aVersion)
	{
		this.mRequiredVersion = aVersion;
	}

	public void setSubManifests(String[] aSubManifests)
	{
		this.mSubManifests = aSubManifests;
	}

	public String[] getSubManifests()
	{
		return mSubManifests;
	}

	public void setExtras(String[] aExtras)
	{
		this.mExtras = aExtras;
	}

	public String[] getExtras()
	{
		return mExtras;
	}

	public ArrayList<String> getCacheList()
	{
		return mCacheList;
	}

	public void setCacheList(ArrayList<String> aCacheList)
	{
		this.mCacheList = aCacheList;
	}

	public ArrayList<String> getCacheDirs()
	{
		return mCacheDirs;
	}

	public void setCacheDirs(ArrayList<String> aCacheDirs)
	{
		this.mCacheDirs = aCacheDirs;
	}

	public String getDekRelativePath()
	{
		if (KCUtilText.isStringEmpty(mDekRelativePath))
			mDekRelativePath = "/update_" + this.mVersion + ".dek";
		return mDekRelativePath;
	}

	public void setDekRelativePath(String aRelativePath)
	{
		this.mDekRelativePath = aRelativePath;
	}

	public void setDownloadUrl(String aDownloadUrl)
	{
		this.mDownloadUrl = aDownloadUrl;
	}

	public String getDownloadUrl()
	{
		return mDownloadUrl;
	}

	public void setDestDir(String aDestDir)
	{
		this.mDestDir = aDestDir;
	}

	public String getDestDir()
	{
		return mDestDir;
	}



}
