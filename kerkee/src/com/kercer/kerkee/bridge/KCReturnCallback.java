package com.kercer.kerkee.bridge;

public abstract class KCReturnCallback implements KCCallback
{
	@Override
	public void callback(Object... args)
	{
		Object object = null;
		KCJSError error = null;
		if (args.length > 0)
		{
			object = args[0];
		}

		if (args.length > 1)
		{
			error = (KCJSError) args[1];
		}

		returnCallback(object, error);
	}

	abstract public void returnCallback(Object aObject, KCJSError aError);


}
