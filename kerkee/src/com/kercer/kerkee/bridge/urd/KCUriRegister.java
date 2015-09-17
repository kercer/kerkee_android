package com.kercer.kerkee.bridge.urd;

import java.util.ArrayList;

import com.kercer.kerkee.net.uri.KCURI;

/**
 * Created by liweisu on 15/9/6.
 * 
 * Optimization by zihong on 15/9/17
 * 
 */

public class KCUriRegister implements IUrlRegister
{
	private ArrayList<IUrlAction> mUriActions = new ArrayList<IUrlAction>();
	
	private String mScheme;
	
	public KCUriRegister(String aScheme)
	{
		mScheme = aScheme;
	}

	/**
	 * Determine whether there is contains the action
	 * 
	 * @param aAction
	 * @return if has action in register return true, else return false
	 */
	public boolean containsAction(IUrlAction aAction)
	{
		return (aAction != null && mUriActions.contains(aAction));
	}

	
	
	@Override
	public boolean registerAction(IUrlAction aAction)
	{
		if (aAction != null && !mUriActions.contains(aAction))
		{
			mUriActions.add(aAction);
			return true;
		}
		return false;
	}

	@Override
	public boolean unregisterAction(IUrlAction aAction)
	{
		if (aAction != null && mUriActions.contains(aAction))
		{
			mUriActions.remove(aAction);
			return true;
		}
		return false;
	}

	@Override
	public void dispatcher(KCURI aUriData)
	{
		for (IUrlAction action : mUriActions)
		{
			if (action.accept(aUriData.getHost(), aUriData.getPath()))
			{
				action.invokeAction(aUriData.getQueries());
			}
		}
	}

	@Override
	public String scheme()
	{
		return mScheme;
	}
}
