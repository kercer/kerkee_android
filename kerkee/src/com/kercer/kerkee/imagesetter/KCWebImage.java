package com.kercer.kerkee.imagesetter;

import java.io.InputStream;

public class KCWebImage
{
	private InputStream mInputStream;
	
	public void setInputStream(final InputStream aInputStream)
	{
		mInputStream = aInputStream;
	}
	
	public InputStream getInputStream()
	{
		return mInputStream;
	}
}
