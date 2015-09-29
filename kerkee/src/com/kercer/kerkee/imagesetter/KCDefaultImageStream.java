package com.kercer.kerkee.imagesetter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import android.content.Context;
import android.util.Base64;

public class KCDefaultImageStream
{

	//data:image/png;base64,
	//Transparent image
	private final static String mDefaultImageBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAYAAACNMs+9AAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAAaSURBVHjaYvz//z8DMYCJgUgwqpA6CgECDADVEgMRar6RXAAAAABJRU5ErkJggg==";
	private byte[] data = new byte[0];

	public KCDefaultImageStream(Context aContext)
	{			
		data = Base64.decode(mDefaultImageBase64, Base64.DEFAULT);  
	}

	public InputStream getInputStream()
	{
		return new ByteArrayInputStream(data);
	}
}
