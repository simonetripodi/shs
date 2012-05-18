package org.nnsoft.shs.core.io;

/*
 * Copyright (c) 2012 Simone Tripodi (simonetripodi@apache.org)
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import static org.nnsoft.shs.core.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.Map;

import org.nnsoft.shs.io.ResponseBodyWriter;

/**
 * Generates a body response from a {@link File}.
 */
public final class FileResponseBodyWriter
    implements ResponseBodyWriter
{

    /**
     * The files content types, extracted from conf/web.xml in Tomcat.
     */
    private static final Map<String, String> CONTENT_TYPES = new HashMap<String, String>();

    static
    {
        CONTENT_TYPES.put( "abs", "audio/x-mpeg" );
        CONTENT_TYPES.put( "ai", "application/postscript" );
        CONTENT_TYPES.put( "aif", "audio/x-aiff" );
        CONTENT_TYPES.put( "aifc", "audio/x-aiff" );
        CONTENT_TYPES.put( "aiff", "audio/x-aiff" );
        CONTENT_TYPES.put( "aim", "application/x-aim" );
        CONTENT_TYPES.put( "art", "image/x-jg" );
        CONTENT_TYPES.put( "asf", "video/x-ms-asf" );
        CONTENT_TYPES.put( "asx", "video/x-ms-asf" );
        CONTENT_TYPES.put( "au", "audio/basic" );
        CONTENT_TYPES.put( "avi", "video/x-msvideo" );
        CONTENT_TYPES.put( "avx", "video/x-rad-screenplay" );
        CONTENT_TYPES.put( "bcpio", "application/x-bcpio" );
        CONTENT_TYPES.put( "bin", "application/octet-stream" );
        CONTENT_TYPES.put( "bmp", "image/bmp" );
        CONTENT_TYPES.put( "body", "text/html" );
        CONTENT_TYPES.put( "cdf", "application/x-cdf" );
        CONTENT_TYPES.put( "cer", "application/x-x509-ca-cert" );
        CONTENT_TYPES.put( "class", "application/java" );
        CONTENT_TYPES.put( "cpio", "application/x-cpio" );
        CONTENT_TYPES.put( "csh", "application/x-csh" );
        CONTENT_TYPES.put( "css", "text/css" );
        CONTENT_TYPES.put( "dib", "image/bmp" );
        CONTENT_TYPES.put( "doc", "application/msword" );
        CONTENT_TYPES.put( "dtd", "application/xml-dtd" );
        CONTENT_TYPES.put( "dv", "video/x-dv" );
        CONTENT_TYPES.put( "dvi", "application/x-dvi" );
        CONTENT_TYPES.put( "eps", "application/postscript" );
        CONTENT_TYPES.put( "etx", "text/x-setext" );
        CONTENT_TYPES.put( "exe", "application/octet-stream" );
        CONTENT_TYPES.put( "gif", "image/gif" );
        CONTENT_TYPES.put( "gtar", "application/x-gtar" );
        CONTENT_TYPES.put( "gz", "application/x-gzip" );
        CONTENT_TYPES.put( "hdf", "application/x-hdf" );
        CONTENT_TYPES.put( "hqx", "application/mac-binhex40" );
        CONTENT_TYPES.put( "htc", "text/x-component" );
        CONTENT_TYPES.put( "htm", "text/html" );
        CONTENT_TYPES.put( "html", "text/html" );
        CONTENT_TYPES.put( "hqx", "application/mac-binhex40" );
        CONTENT_TYPES.put( "ief", "image/ief" );
        CONTENT_TYPES.put( "jad", "text/vnd.sun.j2me.app-descriptor" );
        CONTENT_TYPES.put( "jar", "application/java-archive" );
        CONTENT_TYPES.put( "java", "text/plain" );
        CONTENT_TYPES.put( "jnlp", "application/x-java-jnlp-file" );
        CONTENT_TYPES.put( "jpe", "image/jpeg" );
        CONTENT_TYPES.put( "jpeg", "image/jpeg" );
        CONTENT_TYPES.put( "jpg", "image/jpeg" );
        CONTENT_TYPES.put( "js", "text/javascript" );
        CONTENT_TYPES.put( "jsf", "text/plain" );
        CONTENT_TYPES.put( "jspf", "text/plain" );
        CONTENT_TYPES.put( "kar", "audio/x-midi" );
        CONTENT_TYPES.put( "latex", "application/x-latex" );
        CONTENT_TYPES.put( "m3u", "audio/x-mpegurl" );
        CONTENT_TYPES.put( "mac", "image/x-macpaint" );
        CONTENT_TYPES.put( "man", "application/x-troff-man" );
        CONTENT_TYPES.put( "mathml", "application/mathml+xml" );
        CONTENT_TYPES.put( "me", "application/x-troff-me" );
        CONTENT_TYPES.put( "mid", "audio/x-midi" );
        CONTENT_TYPES.put( "midi", "audio/x-midi" );
        CONTENT_TYPES.put( "mif", "application/x-mif" );
        CONTENT_TYPES.put( "mov", "video/quicktime" );
        CONTENT_TYPES.put( "movie", "video/x-sgi-movie" );
        CONTENT_TYPES.put( "mp1", "audio/x-mpeg" );
        CONTENT_TYPES.put( "mp2", "audio/x-mpeg" );
        CONTENT_TYPES.put( "mp3", "audio/x-mpeg" );
        CONTENT_TYPES.put( "mp4", "video/mp4" );
        CONTENT_TYPES.put( "mpa", "audio/x-mpeg" );
        CONTENT_TYPES.put( "mpe", "video/mpeg" );
        CONTENT_TYPES.put( "mpeg", "video/mpeg" );
        CONTENT_TYPES.put( "mpega", "audio/x-mpeg" );
        CONTENT_TYPES.put( "mpg", "video/mpeg" );
        CONTENT_TYPES.put( "mpv2", "video/mpeg2" );
        CONTENT_TYPES.put( "ms", "application/x-wais-source" );
        CONTENT_TYPES.put( "nc", "application/x-netcdf" );
        CONTENT_TYPES.put( "oda", "application/oda" );
        CONTENT_TYPES.put( "odb", "application/vnd.oasis.opendocument.database" );
        CONTENT_TYPES.put( "odc", "application/vnd.oasis.opendocument.chart" );
        CONTENT_TYPES.put( "odf", "application/vnd.oasis.opendocument.formula" );
        CONTENT_TYPES.put( "odg", "application/vnd.oasis.opendocument.graphics" );
        CONTENT_TYPES.put( "odi", "application/vnd.oasis.opendocument.image" );
        CONTENT_TYPES.put( "odm", "application/vnd.oasis.opendocument.text-master" );
        CONTENT_TYPES.put( "odp", "application/vnd.oasis.opendocument.presentation" );
        CONTENT_TYPES.put( "ods", "application/vnd.oasis.opendocument.spreadsheet" );
        CONTENT_TYPES.put( "odt", "application/vnd.oasis.opendocument.text" );
        CONTENT_TYPES.put( "ogg", "application/ogg" );
        CONTENT_TYPES.put( "otg", "application/vnd.oasis.opendocument.graphics-template" );
        CONTENT_TYPES.put( "oth", "application/vnd.oasis.opendocument.text-web" );
        CONTENT_TYPES.put( "otp", "application/vnd.oasis.opendocument.presentation-template" );
        CONTENT_TYPES.put( "ots", "application/vnd.oasis.opendocument.spreadsheet-template" );
        CONTENT_TYPES.put( "ott", "application/vnd.oasis.opendocument.text-template" );
        CONTENT_TYPES.put( "pbm", "image/x-portable-bitmap" );
        CONTENT_TYPES.put( "pct", "image/pict" );
        CONTENT_TYPES.put( "pdf", "application/pdf" );
        CONTENT_TYPES.put( "pgm", "image/x-portable-graymap" );
        CONTENT_TYPES.put( "pic", "image/pict" );
        CONTENT_TYPES.put( "pict", "image/pict" );
        CONTENT_TYPES.put( "pls", "audio/x-scpls" );
        CONTENT_TYPES.put( "png", "image/png" );
        CONTENT_TYPES.put( "pnm", "image/x-portable-anymap" );
        CONTENT_TYPES.put( "pnt", "image/x-macpaint" );
        CONTENT_TYPES.put( "ppm", "image/x-portable-pixmap" );
        CONTENT_TYPES.put( "ppt", "application/vnd.ms-powerpoint" );
        CONTENT_TYPES.put( "pps", "application/vnd.ms-powerpoint" );
        CONTENT_TYPES.put( "ps", "application/postscript" );
        CONTENT_TYPES.put( "psd", "image/x-photoshop" );
        CONTENT_TYPES.put( "qt", "video/quicktime" );
        CONTENT_TYPES.put( "qti", "image/x-quicktime" );
        CONTENT_TYPES.put( "qtif", "image/x-quicktime" );
        CONTENT_TYPES.put( "ras", "image/x-cmu-raster" );
        CONTENT_TYPES.put( "rdf", "application/rdf+xml" );
        CONTENT_TYPES.put( "rgb", "image/x-rgb" );
        CONTENT_TYPES.put( "rm", "application/vnd.rn-realmedia" );
        CONTENT_TYPES.put( "roff", "application/x-troff" );
        CONTENT_TYPES.put( "rtf", "application/rtf" );
        CONTENT_TYPES.put( "rtx", "text/richtext" );
        CONTENT_TYPES.put( "sh", "application/x-sh" );
        CONTENT_TYPES.put( "shar", "application/x-shar" );
        CONTENT_TYPES.put( "smf", "audio/x-midi" );
        CONTENT_TYPES.put( "sit", "application/x-stuffit" );
        CONTENT_TYPES.put( "snd", "audio/basic" );
        CONTENT_TYPES.put( "src", "application/x-wais-source" );
        CONTENT_TYPES.put( "sv4cpio", "application/x-sv4cpio" );
        CONTENT_TYPES.put( "sv4crc", "application/x-sv4crc" );
        CONTENT_TYPES.put( "svg", "image/svg+xml" );
        CONTENT_TYPES.put( "svgz", "image/svg+xml" );
        CONTENT_TYPES.put( "swf", "application/x-shockwave-flash" );
        CONTENT_TYPES.put( "t", "application/x-troff" );
        CONTENT_TYPES.put( "tar", "application/x-tar" );
        CONTENT_TYPES.put( "tcl", "application/x-tcl" );
        CONTENT_TYPES.put( "tex", "application/x-tex" );
        CONTENT_TYPES.put( "texi", "application/x-texinfo" );
        CONTENT_TYPES.put( "texinfo", "application/x-texinfo" );
        CONTENT_TYPES.put( "tif", "image/tiff" );
        CONTENT_TYPES.put( "tiff", "image/tiff" );
        CONTENT_TYPES.put( "tr", "application/x-troff" );
        CONTENT_TYPES.put( "tsv", "text/tab-separated-values" );
        CONTENT_TYPES.put( "txt", "text/plain" );
        CONTENT_TYPES.put( "ulw", "audio/basic" );
        CONTENT_TYPES.put( "ustar", "application/x-ustar" );
        CONTENT_TYPES.put( "vxml", "application/voicexml+xml" );
        CONTENT_TYPES.put( "xbm", "image/x-xbitmap" );
        CONTENT_TYPES.put( "xht", "application/xhtml+xml" );
        CONTENT_TYPES.put( "xhtml", "application/xhtml+xml" );
        CONTENT_TYPES.put( "xls", "application/vnd.ms-excel" );
        CONTENT_TYPES.put( "xml", "application/xml" );
        CONTENT_TYPES.put( "xpm", "image/x-xpixmap" );
        CONTENT_TYPES.put( "xsl", "application/xml" );
        CONTENT_TYPES.put( "xslt", "application/xslt+xml" );
        CONTENT_TYPES.put( "xul", "application/vnd.mozilla.xul+xml" );
        CONTENT_TYPES.put( "xwd", "image/x-xwindowdump" );
        CONTENT_TYPES.put( "vsd", "application/x-visio" );
        CONTENT_TYPES.put( "wav", "audio/x-wav" );
        CONTENT_TYPES.put( "wbmp", "image/vnd.wap.wbmp" );
        CONTENT_TYPES.put( "wml", "text/vnd.wap.wml" );
        CONTENT_TYPES.put( "wmlc", "application/vnd.wap.wmlc" );
        CONTENT_TYPES.put( "wmls", "text/vnd.wap.wmlscript" );
        CONTENT_TYPES.put( "wmlscriptc", "application/vnd.wap.wmlscriptc" );
        CONTENT_TYPES.put( "wmv", "video/x-ms-wmv" );
        CONTENT_TYPES.put( "wrl", "x-world/x-vrml" );
        CONTENT_TYPES.put( "wspolicy", "application/wspolicy+xml" );
        CONTENT_TYPES.put( "Z", "application/x-compress" );
        CONTENT_TYPES.put( "z", "application/x-compress" );
        CONTENT_TYPES.put( "zip", "application/zip" );
    }

    /**
     * The file has to be transferred to the body response.
     */
    private final File toBeTransfered;

    /**
     * Creates a new ResponseBodyWriter given the file has to be transferred to the body response.
     *
     * @param toBeTransfered the file has to be transferred to the body response.
     */
    public FileResponseBodyWriter( File toBeTransfered )
    {
        this.toBeTransfered = toBeTransfered;
    }

    /**
     * {@inheritDoc}
     */
    public long getContentLength()
    {
        return toBeTransfered.length();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String contentType()
    {
        int extensionSeparator = toBeTransfered.getName().lastIndexOf( '.' );
        String extension = toBeTransfered.getName().substring( ++extensionSeparator );
        return CONTENT_TYPES.get( extension );
    }

    /**
     * {@inheritDoc}
     */
    public void write( WritableByteChannel target )
        throws IOException
    {
        FileChannel channel = new FileInputStream( toBeTransfered ).getChannel();
        try
        {
            channel.transferTo( 0, toBeTransfered.length(), target );
        }
        finally
        {
            closeQuietly( channel );
        }
    }

}
