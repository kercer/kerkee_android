package com.kercer.kerkee.bridge.urd;

import com.kercer.kerkee.net.uri.KCURI;
import com.kercer.kerkee.util.KCTaskExecutor;

import java.util.ArrayList;

/**
 * Created by liweisu on 15/9/6.
 *
 * Optimization by zihong on 15/9/17
 *
 */

public class KCUriRegister implements IUriRegister
{
	private ArrayList<IUriAction> mUriActions = new ArrayList<IUriAction>();
	private IUriAction mDefaultAction;
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
	public boolean containsAction(IUriAction aAction)
	{
		return (aAction != null && mUriActions.contains(aAction));
	}


	@Override
	public boolean setDefaultAction(final IUriAction aDefaultAction)
	{
		mDefaultAction = aDefaultAction;
		return true;
	}

	@Override
	public boolean registerAction(final IUriAction aAction)
	{
		if (aAction != null && !mUriActions.contains(aAction))
		{
			mUriActions.add(aAction);
			return true;
		}
		return false;
	}

	@Override
	public boolean unregisterAction(final IUriAction aAction)
	{
		if (aAction != null && mUriActions.contains(aAction))
		{
			mUriActions.remove(aAction);
			return true;
		}
		return false;
	}

	@Override
	public void dispatcher(final KCURI aUriData)
	{
		boolean isSupported = false;
		for (final IUriAction action : mUriActions)
		{
			if (action.accept(aUriData.getHost(), aUriData.getPath()))
			{
				isSupported = true;

				KCTaskExecutor.runTaskOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						action.invokeAction(aUriData.getQueries());
					}
				});
			}
		}

		if (!isSupported && mDefaultAction !=null && mDefaultAction.accept(aUriData.getHost(),aUriData.getPath()))
		{
			KCTaskExecutor.runTaskOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					mDefaultAction.invokeAction(aUriData.getQueries());
				}
			});
		}
	}

	@Override
	public String scheme()
	{
		return mScheme;
	}
}
