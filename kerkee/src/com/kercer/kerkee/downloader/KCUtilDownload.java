package com.kercer.kerkee.downloader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.kercer.kerkee.log.KCLog;
import com.kercer.kerkee.net.KCHttpClient;
import com.kercer.kerkee.util.KCUtil;

/**
 * 
 * @author zihong
 *
 */
public class KCUtilDownload
{
    
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final int MAX_REDIRECT_COUNT = 4;
    
    public static interface KCUtilDownloadListener
    {
        void onProgressUpdate(long downloadedBytes, long fileLength);
        void onComplete();
        void onError(Exception e);
    }
    
    // this API does not need those platform/version/uuid.. parameters in the URL
    // those parameters can be queried from User-Agent
    public static boolean downloadFile(String srcUrl, File destFile, KCUtilDownloadListener listener)
    {
        try
        {
            return downloadFile(srcUrl, new FileOutputStream(destFile), 1, listener);
        }
        catch (Exception e)
        {
        }
        return false;
    }

    public static boolean downloadFile(String srcUrl, OutputStream os, int retry, KCUtilDownloadListener listener)
    {
        if (retry <= 0)
        {
            retry = 1;
        }
        while (--retry >= 0)
        {
            InputStream is = null;
            HttpEntity entity = null;
            try
            {
                HttpGet httpGet = new HttpGet(URI.create(srcUrl));
                HttpResponse response = KCHttpClient.getHttpClient().execute(httpGet);
                StatusLine sl = response.getStatusLine();
                if (sl.getStatusCode() == HttpStatus.SC_OK)
                {
                    entity = response.getEntity();
                    is = entity.getContent();

                    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                    int lenRead;
                    long fileLength = 0;
                    long totalDownloadedBytes = 0;
                    String fileLengthStr = response.getFirstHeader("Content-Length").getValue();
                    if (fileLengthStr != null)
                    {
                        fileLength = Integer.parseInt(fileLengthStr);
                    }

                    while ((lenRead = is.read(buffer)) != -1)
                    {
                        totalDownloadedBytes += lenRead;
                        if (listener != null)
                            listener.onProgressUpdate(totalDownloadedBytes, fileLength);
                        os.write(buffer, 0, lenRead);
                    }

                    if (listener != null)
                        listener.onComplete();

                    return true;
                }
                else if (retry == 0 && listener != null)
                {
                    listener.onError(null);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                if (retry == 0 && listener != null)
                    listener.onError(e);
            }
            finally
            {
                KCUtil.closeCloseable(is);
                KCUtil.closeCloseable(os);
                KCUtil.closeHttpEntity(entity);
            }
        }

        return false;
    }


    public static boolean downloadFile(final URL aUrl, final File aCacheDir, final String aFileKey)
    {
        return downloadFile(aUrl, aCacheDir, aFileKey, 0);
    }

    private static boolean downloadFile(final URL aUrl, final File aCacheDir, final String aFileKey,  int aRedirectCount)
    {
        if (aRedirectCount > MAX_REDIRECT_COUNT)
        {
            KCLog.i("Too many redirects!");
            return false;
        }

        HttpClient httpClient = null;
        HttpEntity responseEntity = null;
        BufferedInputStream bufferedInputStream = null;
        BufferedOutputStream bufferedOutputStream = null;

        try
        {
            final String imageUrlString = aUrl.toString();
            if (imageUrlString == null || imageUrlString.length() == 0)
            {
                throw new Exception("Passed empty URL");
            }
            KCLog.i("Requesting image " + imageUrlString);
            httpClient = KCHttpClient.getHttpClient();
            final HttpGet httpGet = new HttpGet(imageUrlString);
            final HttpResponse response = httpClient.execute(httpGet);

            responseEntity = response.getEntity();
            if (responseEntity == null)
            {
                throw new Exception("No response entity for image " + aUrl.toString());
            }
            final StatusLine statusLine = response.getStatusLine();
            final int statusCode = statusLine.getStatusCode();
            switch (statusCode)
            {
            case HttpStatus.SC_OK:
                break;
            case HttpStatus.SC_MOVED_TEMPORARILY:
            case HttpStatus.SC_MOVED_PERMANENTLY:
            case HttpStatus.SC_SEE_OTHER:
                final String location = response.getFirstHeader("Location").getValue();
                KCLog.i("Image redirected to " + location);
                // Force close the connection now, otherwise we risk leaking too many open HTTP connections
                responseEntity.consumeContent();
                responseEntity = null;
                httpClient = null;
                return downloadFile( new URL(location), aCacheDir, aFileKey, aRedirectCount + 1);
            default:
                KCLog.i("Could not download image, got status code " + statusCode);
                return false;
            }

            bufferedInputStream = new BufferedInputStream(responseEntity.getContent());
            File cacheFile = File.createTempFile("image-", "tmp", aCacheDir);
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(cacheFile));

            long contentSize = responseEntity.getContentLength();
            long totalBytesRead = 0;
            try
            {
                int bytesRead;
                final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                do
                {
                    bytesRead = bufferedInputStream.read(buffer, 0, DEFAULT_BUFFER_SIZE);
                    if (bytesRead > 0)
                    {
                        bufferedOutputStream.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;
                    }
                } while (bytesRead > 0);
            }
            catch (IOException e)
            {
                return false;
            }

            if (totalBytesRead != contentSize)
            {
                KCLog.i("Short read! Expected " + contentSize + "b, got " + totalBytesRead);
                return false;
            }
            else
            {
                KCLog.i("Downloaded image " + imageUrlString + " to file cache");
                File outputFile = new File(aCacheDir, aFileKey);
                cacheFile.renameTo(outputFile);
            }
        }
        catch (IOException e)
        {
            return false;
        }
        catch (Exception e)
        {
            return false;
        }
        finally
        {
            try
            {
                if (bufferedInputStream != null)
                {
                    bufferedInputStream.close();
                }
                if (bufferedOutputStream != null)
                {
                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();
                }
                if (responseEntity != null)
                {
                    responseEntity.consumeContent();
                }
            }
            catch (IOException e)
            {
            }
        }

        return true;
    }

}
