package com.kercer.kerkee.manifest;

import java.util.ArrayList;

import com.kercer.kerkee.util.KCUtilString;

public class KCManifestObject
{

	private String mVersion;
	private String[] mSubManifests;
	private String[] mExtras;
	private ArrayList<String> mCacheList;
	private String mZipFile;
	private String mRequiredVersion;
	private ArrayList<String> mCacheDirs;
	private String mDownloadUrl;
	private String mDestDir;
	private String mDestFile;
	private int mTotalSize;

	public KCManifestObject()
	{
		mRequiredVersion = KCUtilString.EMPTY_STR;
		mVersion = KCUtilString.EMPTY_STR;
		mZipFile = KCUtilString.EMPTY_STR;
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

	public String getZipFilePath()
	{
		if (KCUtilString.isEmpty(mZipFile))
			mZipFile = "update_" + this.mVersion + ".zip";
		return mZipFile;
	}

	public void setZipFile(String aZipFile)
	{
		this.mZipFile = aZipFile;
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

	public void setDestFile(String aDestFile)
	{
		this.mDestFile = aDestFile;
	}

	public String getDestFile()
	{
		return mDestFile;
	}

	public void setTotalSize(int aTotalSize)
	{
		this.mTotalSize = aTotalSize;
	}

	public int getTotalSize()
	{
		return mTotalSize;
	}

}
