package com.kercer.kerkee.webview;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

/**
 * 
 * @author zihong
 *
 */
public class KCContext extends Context
{
    Context mContext;
    public KCContext(Context aContext)
    {
        mContext = aContext;
    }

    @Override
    public AssetManager getAssets()
    {
        return mContext.getAssets();
    }

    @Override
    public Resources getResources()
    {
        return mContext.getResources();
    }

    @Override
    public PackageManager getPackageManager()
    {
        return mContext.getPackageManager();
    }

    @Override
    public ContentResolver getContentResolver()
    {
        return mContext.getContentResolver();
    }

    @Override
    public Looper getMainLooper()
    {
        return mContext.getMainLooper();
    }

    @Override
    public Context getApplicationContext()
    {
        return mContext.getApplicationContext();
    }

    @Override
    public void setTheme(int aResId)
    {
        mContext.setTheme(aResId);
    }

    @Override
    public Theme getTheme()
    {
        return mContext.getTheme();
    }

    @Override
    public ClassLoader getClassLoader()
    {
        return mContext.getClassLoader();
    }

    @Override
    public String getPackageName()
    {
        return mContext.getPackageName();
    }

    @Override
    public ApplicationInfo getApplicationInfo()
    {
        return mContext.getApplicationInfo();
    }

    @Override
    public String getPackageResourcePath()
    {
        return mContext.getPackageResourcePath();
    }

    @Override
    public String getPackageCodePath()
    {
        return mContext.getPackageCodePath();
    }

    @Override
    public SharedPreferences getSharedPreferences(String aName, int aMode)
    {
        return mContext.getSharedPreferences(aName, aMode);
    }

    @Override
    public FileInputStream openFileInput(String aName) throws FileNotFoundException
    {
        return mContext.openFileInput(aName);
    }

    @Override
    public FileOutputStream openFileOutput(String aName, int aMode) throws FileNotFoundException
    {
        return mContext.openFileOutput(aName, aMode);
    }

    @Override
    public boolean deleteFile(String aName)
    {
        return mContext.deleteFile(aName);
    }

    @Override
    public File getFileStreamPath(String aName)
    {
        return mContext.getFileStreamPath(aName);
    }

    @Override
    public File getFilesDir()
    {
        return mContext.getFilesDir();
    }

    @Override
    public File getExternalFilesDir(String aType)
    {
        return mContext.getExternalFilesDir(aType);
    }

    @Override
    public File getObbDir()
    {
        return mContext.getObbDir();
    }

    @Override
    public File getCacheDir()
    {
        return mContext.getCacheDir();
    }

    @Override
    public File getExternalCacheDir()
    {
        return mContext.getExternalCacheDir();
    }

    @Override
    public String[] fileList()
    {
        return mContext.fileList();
    }

    @Override
    public File getDir(String aName, int aMode)
    {
        return mContext.getDir(aName, aMode);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String aName, int aMode, CursorFactory aFactory)
    {
        return mContext.openOrCreateDatabase(aName, aMode, aFactory);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String aName, int aMode, CursorFactory aFactory, DatabaseErrorHandler aErrorHandler)
    {
        return mContext.openOrCreateDatabase(aName, aMode, aFactory, aErrorHandler);
    }

    @Override
    public boolean deleteDatabase(String aName)
    {
        return mContext.deleteDatabase(aName);
    }

    @Override
    public File getDatabasePath(String aName)
    {
        return mContext.getDatabasePath(aName);
    }

    @Override
    public String[] databaseList()
    {
        return mContext.databaseList();
    }

    @Override
    public Drawable getWallpaper()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Drawable peekWallpaper()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getWallpaperDesiredMinimumWidth()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getWallpaperDesiredMinimumHeight()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setWallpaper(Bitmap bitmap) throws IOException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setWallpaper(InputStream data) throws IOException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void clearWallpaper() throws IOException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void startActivity(Intent intent)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void startActivities(Intent[] intents)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws SendIntentException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendBroadcast(Intent intent)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendBroadcast(Intent intent, String receiverPermission)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendOrderedBroadcast(Intent intent, String receiverPermission)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendOrderedBroadcast(Intent intent, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendStickyBroadcast(Intent intent)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeStickyBroadcast(Intent intent)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public ComponentName startService(Intent service)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean stopService(Intent service)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void unbindService(ServiceConnection conn)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean startInstrumentation(ComponentName className, String profileFile, Bundle arguments)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Object getSystemService(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int checkPermission(String permission, int pid, int uid)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int checkCallingPermission(String permission)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int checkCallingOrSelfPermission(String permission)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void enforcePermission(String permission, int pid, int uid, String message)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void enforceCallingPermission(String permission, String message)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void enforceCallingOrSelfPermission(String permission, String message)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void grantUriPermission(String toPackage, Uri uri, int modeFlags)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void revokeUriPermission(Uri uri, int modeFlags)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int checkCallingUriPermission(Uri uri, int modeFlags)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int checkCallingOrSelfUriPermission(Uri uri, int modeFlags)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int checkUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid, int modeFlags)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void enforceUriPermission(Uri uri, int pid, int uid, int modeFlags, String message)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void enforceCallingUriPermission(Uri uri, int modeFlags, String message)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void enforceCallingOrSelfUriPermission(Uri uri, int modeFlags, String message)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void enforceUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid, int modeFlags, String message)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public Context createPackageContext(String packageName, int flags) throws NameNotFoundException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
