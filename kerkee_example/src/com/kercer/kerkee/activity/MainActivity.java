package com.kercer.kerkee.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.kercer.kerkee.api.KCRegistMgr;
import com.kercer.kerkee.browser.KCDefaultBrowser;
import com.kercer.kerkee_example.R;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class MainActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        KCDefaultBrowser browser = new KCDefaultBrowser(this);
        View view = browser.getView();
        setContentView(view);

        //regist classes to JSBridge，the relationship between binding js objects and native classes
        //and you can use default browser'b registJSBridgeClient function
        KCRegistMgr.registClass();
//        browser.registJSBridgeClient(KCApiJSBridgeClient.class);

        Method[] targetMethods = getClass().getDeclaredMethods();
        String s = Modifier.toString(targetMethods[3].getModifiers()) ;

        browser.loadTestPage();
//        browser.loadUrl("http://www.baidu.com");

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

    public static void test()
    {

    }

}
