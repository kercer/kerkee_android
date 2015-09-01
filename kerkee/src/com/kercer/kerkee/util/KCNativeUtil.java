package com.kercer.kerkee.util;

/**
 * 
 * @author zihong
 *
 */
public class KCNativeUtil
{
    static
    {
        System.loadLibrary("kerkee_util");
    }
    
    // this function is twice as fast as new File().exists()
    //  public native static boolean fileExists (String path);
    public native static boolean fileExists(String path);

    public native static long freeDiskSpace(String path);

    public native static long lastAccessTime(String path);

    // this method sometimes causes JNI crash on "libc#fwrite"
    //fixed,but must be a lot of testing
//    @Deprecated
    public native static boolean copyFile(String srcPath, String destPath);

    // this method causes UnsatisfiedLinkError on some devices.
    @Deprecated
    public native static boolean renameExt(String oldPath, String newPath);

    public native static boolean createSparseFile(String path, long size);

    public native static void testCrash();

    public native static String getMd5(String para);

    public native static String getMd5Treated(String para);
    
    /*
     * FileSys
     */
    public native static int fileOpen(String pFileName, int openMode);
    public native static int fileLength(int fp);
    public native static int fileSeek(int fp, int offset, int origin);
    public native static KCCusBuffer fileRead(int fp, int nCount);
    public native static int fileWrite(int fp, byte[] buf, int nCount);
    public native static int fileClose(int fp);
}
