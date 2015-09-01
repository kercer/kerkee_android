package com.example.kerkee_test;

import java.io.File;

import com.example.kerkee_test.R;
import com.kercer.kerkee.browser.KCDefaultBrowser;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

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
//        KCApiBridge.registClass("jsBridgeClient", NewApiTest.class);
        browser.registJSBridgeClient(NewApiTest.class);
        
//        String resPath = browser.getResRootPath();
//        String filePath = resPath + "/jsbridge/test.html";
//        
//        File sdPath =Environment.getExternalStorageDirectory();
//        String desPath =  sdPath+"/test.html";
//        String url = "file://" + desPath;
//        boolean bResult = KCNativeUtil.copyFile(filePath, desPath);
        
//        browser.loadUrl(url);
        
        browser.loadTestPage();
//        browser.loadUrl("http://hd.enavi.189.cn/integral/active/listofnotice.htm");
//        browser.loadUrl("http://119.90.33.14//integral/active/listofnotice.htm");
//        browser.loadUrl("http://www.baidu.com");
//       browser.loadUrl("http://119.90.33.14/integral/index.jsp");
//        browser.loadUrl("http://182.92.184.220/hj/hi/index.html");
        
        

//        if (savedInstanceState == null)
//        {
//            getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
//        }
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

//    /**
//     * A placeholder fragment containing a simple view.
//     */
//    public static class PlaceholderFragment extends Fragment
//    {
//
//        public PlaceholderFragment()
//        {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//        {
//            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//            return rootView;
//        }
//    }

}
