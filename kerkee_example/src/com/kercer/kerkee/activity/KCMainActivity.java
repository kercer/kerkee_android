package com.kercer.kerkee.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.kercer.kercore.debug.KCLog;
import com.kercer.kercore.io.KCAssetTool;
import com.kercer.kerkee.api.KCRegistMgr;
import com.kercer.kerkee.bridge.KCJSObject;
import com.kercer.kerkee.bridge.KerkeeMethod;
import com.kercer.kerkee.browser.KCDefaultBrowser;
import com.kercer.kerkee.webview.KCWebPath;
import com.kercer.kerkee_example.R;

import java.io.File;
import java.io.IOException;

public class KCMainActivity extends Activity
{
	public class KCTest extends KCJSObject
	{
		@Override
		public String getJSObjectName()
		{
			return "test";
		}

		@KerkeeMethod
		public String testString()
		{
			return null;
		}

	}

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        //copy asset to html dir first
        //if you use zip, you can unzip to html dir
        if (!isExitAsset())
            copyAssetHtmlDir();


        //create browser that use KCWebview
        KCDefaultBrowser browser = new KCDefaultBrowser(this);
        View view = browser.getView();
        setContentView(view);

        //regist classes to JSBridge，the relationship between binding js objects and native classes
        //and you can use default browser'b registJSBridgeClient function
        KCRegistMgr.registClass();
        //you can registObject here;
//        KCJSBridge.registObject(new KCTest());

        browser.loadTestPage();
//        browser.loadUrl("http://www.baidu.com");

    }




    private boolean isExitAsset()
    {
        KCWebPath webPath = new KCWebPath(this);
        String cfgPath = webPath.getCfgPath();
        File file = new File(cfgPath);
        if (file.exists())
            return true;
        return false;
    }

    private void copyAssetHtmlDir()
    {
        KCAssetTool assetTool = new KCAssetTool(this);
        try
        {
            assetTool.copyDir("html", new KCWebPath(this).getResRootPath());
        }
        catch (IOException e)
        {
            KCLog.e(e);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void test(String a, int b, boolean c)
    {

    }

}
