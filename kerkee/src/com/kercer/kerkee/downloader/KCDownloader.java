package com.kercer.kerkee.downloader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * Provides retrieving of {@link InputStream} of image by URI.<br />
 * Implementations have to be thread-safe.
 * 
 * @author zihong
 */
public interface KCDownloader
{

    /**
     * Retrieves {@link InputStream} of image by URI.
     *
     * @param imageUri Image URI
     * @param extra    Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
     *                 DisplayImageOptions.extraForDownloader(Object)}; can be null
     * @return {@link InputStream} of image
     * @throws IOException                   if some I/O error occurs during getting image stream
     * @throws UnsupportedOperationException if image URI has unsupported scheme(protocol)
     */
    InputStream getStream(String imageUri, Object extra) throws IOException;

    /** Represents supported schemes(protocols) of URI. Provides convenient methods for work with schemes and URIs. */
    public enum KCScheme
    {
        HTTP("http"), HTTPS("https"), FILE("file"), CONTENT("content"), ASSETS("assets"), DRAWABLE("drawable"), UNKNOWN("");

        private String scheme;
        private String uriPrefix;

        KCScheme(String scheme)
        {
            this.scheme = scheme;
            uriPrefix = scheme + "://";
        }

        /**
         * Defines scheme of incoming URI
         *
         * @param uri URI for scheme detection
         * @return Scheme of incoming URI
         */
        public static KCScheme ofUri(String uri)
        {
            if (uri != null)
            {
                for (KCScheme s : values())
                {
                    if (s.belongsTo(uri))
                    {
                        return s;
                    }
                }
            }
            return UNKNOWN;
        }

        private boolean belongsTo(String uri)
        {
            return uri.toLowerCase(Locale.US).startsWith(uriPrefix);
        }

        /** Appends scheme to incoming path */
        public String wrap(String path)
        {
            return uriPrefix + path;
        }

        /** Removed scheme part ("scheme://") from incoming URI */
        public String crop(String uri)
        {
            if (!belongsTo(uri))
            {
                throw new IllegalArgumentException(String.format("URI [%1$s] doesn't have expected scheme [%2$s]", uri, scheme));
            }
            return uri.substring(uriPrefix.length());
        }
    }

}
