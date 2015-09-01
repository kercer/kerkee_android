package com.kercer.kerkee.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Video;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.webkit.MimeTypeMap;

/**
 * 
 * @author zihong
 *
 */
public class KCUtilFile
{

    /**
     * Whether the URI is a local one.
     * 
     * @param uri
     * @return
     */
    public static boolean isLocal(String uri)
    {
        if (uri != null && !uri.startsWith("http://"))
        {
            return true;
        }
        return false;
    }

    public static String getFileMimeType(File file)
    {
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(getExtension(file));
        if (type == null)
            return "*/*";
        return type;
    }

    /**
     * Gets the extension of a file name, like "png" or "jpg".
     * 
     * @param uri
     * @return Extension excluding the dot("."); "" if there is no extension;
     *         null if uri was null.
     */
    public static String getExtension(String uri)
    {
        if (uri == null)
        {
            return null;
        }

        int dot = uri.lastIndexOf(".");
        if (dot >= 0)
        {
            return uri.substring(dot + 1);
        }
        else
        {
            // No extension.
            return "";
        }
    }

    public static String getExtension(File file)
    {
        return getExtension(file.getName());
    }

    /**
     * Returns true if uri is a media uri.
     * 
     * @param uri
     * @return
     */
    public static boolean isMediaUri(String uri)
    {
        if (uri.startsWith(Audio.Media.INTERNAL_CONTENT_URI.toString()) || uri.startsWith(Audio.Media.EXTERNAL_CONTENT_URI.toString()) || uri.startsWith(Video.Media.INTERNAL_CONTENT_URI.toString()) || uri.startsWith(Video.Media.EXTERNAL_CONTENT_URI.toString()))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Convert File into Uri.
     * @param file
     * @return uri
     */
    public static Uri getUri(File file)
    {
        if (file != null)
        {
            return Uri.fromFile(file);
        }
        return null;
    }

    /**
     * Convert Uri into File.
     * @param uri
     * @return file
     */
    public static File getFile(Uri uri)
    {
        if (uri != null)
        {
            String filepath = uri.getPath();
            if (filepath != null)
            {
                return new File(filepath);
            }
        }
        return null;
    }

    /**
     * Returns the path only (without file name).
     * @param file
     * @return
     */
    public static File getPathWithoutFilename(File file)
    {
        if (file != null)
        {
            if (file.isDirectory())
            {
                // no file to be split off. Return everything
                return file;
            }
            else
            {
                String filename = file.getName();
                String filepath = file.getAbsolutePath();

                // Construct path without file name.
                String pathwithoutname = filepath.substring(0, filepath.length() - filename.length());
                if (pathwithoutname.endsWith("/"))
                {
                    pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length() - 1);
                }
                return new File(pathwithoutname);
            }
        }
        return null;
    }

    /**
     * Constructs a file from a path and file name.
     * 
     * @param curdir
     * @param file
     * @return
     */
    public static File getFile(String curdir, String file)
    {
        String separator = "/";
        if (curdir.endsWith("/"))
        {
            separator = "";
        }
        File clickedFile = new File(curdir + separator + file);
        return clickedFile;
    }

    public static File getFile(File curdir, String file)
    {
        return getFile(curdir.getAbsolutePath(), file);
    }

    public static String formatSize(Context context, long sizeInBytes)
    {
        return Formatter.formatFileSize(context, sizeInBytes);
    }

    public static long folderSize(File directory)
    {
        long length = 0;
        File[] files = directory.listFiles();
        if (files != null)
            for (File file : files)
                if (file.isFile())
                    length += file.length();
                else
                    length += folderSize(file);
        return length;
    }

    public static String formatDate(Context context, long dateTime)
    {
        return DateFormat.getDateFormat(context).format(new Date(dateTime));
    }

    public static void copyFile(File src, File dst) throws IOException 
    {
        if (src.isDirectory())
            throw new IOException("Source is a directory");
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
    

	public static void deleteRecyle(File file) {
		if(file.isDirectory()){
			for (File childFile : file.listFiles()) {
				deleteRecyle(childFile);
			}
		}
		file.delete();
	}
	
    public static int deleteFiles(Collection<File> files)
    {
        int n=0;
        for (File file : files)
        {
            if (file.isDirectory())
            {
                n += deleteFiles(Arrays.asList(file.listFiles()));
            }
            if (file.delete()) n++;
        }
        return n;
    }
    
    
    public static void rename(String oldPath, String newPath)
    {
        File oldFile = new File(oldPath);
        File newFile = new File(newPath);
        if (oldFile.exists())
            oldFile.renameTo(newFile);
//        if (!KCNativeUtil.renameExt(oldPath, newPath)) {
//            File oldFile = new File(oldPath);
//            try {
//                InputStream is = new FileInputStream(oldFile);
//                OutputStream os = new FileOutputStream(oldPath);
//                byte[] buffer = getThreadSafeByteBuffer();
//
//                int lenRead;
//                while ((lenRead = is.read(buffer)) != -1) {
//                    os.write(buffer, 0, lenRead);
//                }
//
//                is.close();
//                os.close();
//
//                if (oldFile.exists())
//                    oldFile.delete();
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//            }
//        }
    }
    
    public static List<String> getFiles(String aDirPath, Boolean aIsGetRelativePath, boolean aIsContainSubDir, FilenameFilter aFilenameFilter)
    {
        List<String> aOutList = new ArrayList<String>();
        File dirPath = new File(aDirPath);
        getFiles(dirPath, aIsGetRelativePath, dirPath, aIsContainSubDir, aFilenameFilter, aOutList);
        return aOutList;
    }
    private static void getFiles(File aRootDir, Boolean aIsGetRelativePath, File aDirPath, boolean aIsContainSubDir, FilenameFilter aFilenameFilter, Collection<String> aOutList)
    {
        if (aOutList == null)
        {
            aOutList = new ArrayList<String>();
        }
        
        File[] files = aDirPath.listFiles(aFilenameFilter);

        for (int i = 0; i < files.length; i++)
        {
            File f = files[i];
            if (f.isFile())
            {
                String strPath = f.getPath();
                if (aRootDir != null && aIsGetRelativePath)
                {
                    strPath = strPath.substring(aRootDir.getPath().length());
                }
                aOutList.add(strPath);
                if (!aIsContainSubDir)
                    break;
            }
            else if (f.isDirectory() && f.getPath().indexOf("/.") == -1)
                getFiles(aRootDir, aIsGetRelativePath, f, aIsContainSubDir, aFilenameFilter, aOutList);
        }
    }
    
}
