package com.kercer.kerkee.bridge;

public abstract class KCReturnCallback implements KCCallback
{	
	@Override
	public void callback(Object... args)
	{
		Object object = null;
		if (args.length > 0)
		{
			object = args[0];
		}
		
		returnCallback(object);
	}
	
	abstract public void returnCallback(Object aObject);
	

}
