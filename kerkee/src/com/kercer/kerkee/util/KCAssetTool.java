package com.kercer.kerkee.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

/**
 * 
 * @author zihong
 *
 */
public class KCAssetTool
{
    Context mContext;

    public KCAssetTool(Context context)
    {
        mContext = context;
    }

    /**
     * Determine if SD card exists.
     * 
     * @return T=exists, F=not found
     */
    public boolean SDCardExists()
    {
        String sd_status = Environment.getExternalStorageState();
        boolean status = false;

        // If SD card is mounted
        if (sd_status.equals(Environment.MEDIA_MOUNTED))
        {
            status = true;
        }

        return status;
    }

    /**
     * Create a directory for the provided File object
     * 
     */
    public void createDir(File dir) throws IOException
    {
        if (dir.exists())
        {
            if (!dir.isDirectory())
            {
                throw new IOException("Can't create directory, a file is in the way");
            }
        }
        else
        {
            dir.mkdirs();
            if (!dir.isDirectory())
            {
                throw new IOException("Unable to create directory");
            }
        }
    }

    /**
     * Copy a file from app assets to SD Card
     * 
     * @return STRING path of destination file
     */
    public String copyFileToSDCard(String aAssetFilePath, String aDesFilePath) throws IOException
    {

        File sdPath = Environment.getExternalStorageDirectory(); // Path to SD Card

        File desFile = new File(sdPath + addLeadingSlash(aDesFilePath));
        File desDir = desFile.getParentFile();
        String desPath = desDir.getPath();
        String destination_file_name = desDir.getName();

        if (destination_file_name.length() <= 0)
        {
            throw new IOException("Destination file name is missing");
        }

        createDir(desDir);
        copyAssetFile(aAssetFilePath, desPath);

        return desPath;
    }

    public String copyDirToSDCard(String aAssetDirName, String aDesDirName) throws IOException
    {
      File sdPath =Environment.getExternalStorageDirectory(); // Path to SD Card
      String desDirPath = sdPath + addLeadingSlash(aDesDirName);
      return copyDir(aAssetDirName, desDirPath);
    }
    
    public String copyDir(String aAssetDirName, String aDesDirPath) throws IOException
    {
        String desDirPath = addLeadingSlash(aDesDirPath);
        File desDir = new File(desDirPath);

        createDir(desDir);

        AssetManager asset_manager = mContext.getAssets();
        String[] files = asset_manager.list(aAssetDirName);

        for (int i = 0; i < files.length; i++)
        {

            String abs_asset_file_path = addTrailingSlash(aAssetDirName) + files[i];
            String subFiles[] = asset_manager.list(abs_asset_file_path);

            if (subFiles.length == 0)
            {
                // It is a file
                String destFilePath = addTrailingSlash(desDirPath) + files[i];
                copyAssetFile(abs_asset_file_path, destFilePath);
            }
            else
            {
                // It is a sub directory
                copyDir(abs_asset_file_path, addTrailingSlash(aDesDirPath) + files[i]);
            }
        }

        return desDirPath;
    }

    /**
     * Copies asset file bytes to destination path
     */
    public void copyAssetFile(String aAssetPath, String aDesPath) throws IOException
    {
        InputStream in = mContext.getAssets().open(aAssetPath);
        OutputStream out = new FileOutputStream(aDesPath);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0)
            out.write(buf, 0, len);
        in.close();
        out.close();
    }

    // Adds a trailing slash to path if it doesn't exist
    public String addTrailingSlash(String path)
    {
        if (path.charAt(path.length() - 1) != '/')
        {
            path += "/";
        }
        return path;
    }

    // Adds a leading slash to path if it doesn't exist
    public String addLeadingSlash(String path)
    {
        if (path.charAt(0) != '/')
        {
            path = "/" + path;
        }
        return path;
    }

    public String readAssetText(String aAssetPath)
    {
        
        try
        {
            InputStream in = mContext.getAssets().open(aAssetPath);
            return getString(in, "utf-8");
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public String getString(InputStream inputStream, final String charsetName)
    {
        InputStreamReader inputStreamReader = null;
        try
        {
            inputStreamReader = new InputStreamReader(inputStream, charsetName);
        }
        catch (UnsupportedEncodingException e1)
        {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try
        {
            while ((line = reader.readLine()) != null)
            {
                sb.append(line);
                sb.append("\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }

}
