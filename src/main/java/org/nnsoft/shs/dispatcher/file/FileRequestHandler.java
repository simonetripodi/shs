package org.nnsoft.shs.dispatcher.file;

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

import static org.nnsoft.shs.http.Headers.CONTENT_TYPE;
import static org.nnsoft.shs.http.Response.Status.NOT_FOUND;
import static org.nnsoft.shs.lang.Preconditions.checkArgument;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.nnsoft.shs.dispatcher.BaseRequestHandler;
import org.nnsoft.shs.http.Request;
import org.nnsoft.shs.http.Response;

/**
 * A simple request handler that serves static files.
 *
 * <b>NOTE</b> the current handler doesn't check any content negotiation.
 */
public final class FileRequestHandler
    extends BaseRequestHandler
{

    /**
     * The files content types, extracted from conf/web.xml in Tomcat.
     */
    private final Map<String, String> contentTypes = new HashMap<String, String>();

    /**
     * The main directory where static files are stored.
     */
    private final File baseDir;

    public FileRequestHandler( File baseDir )
    {
        checkArgument( baseDir != null, "Basedir where getting files must be not null" );
        checkArgument( baseDir.exists(), "Basedir where getting files must exist" );
        checkArgument( baseDir.isDirectory(), "Basedir where getting files must be a directory" );
        this.baseDir = baseDir;

        contentTypes.put( "abs", "audio/x-mpeg" );
        contentTypes.put( "ai", "application/postscript" );
        contentTypes.put( "aif", "audio/x-aiff" );
        contentTypes.put( "aifc", "audio/x-aiff" );
        contentTypes.put( "aiff", "audio/x-aiff" );
        contentTypes.put( "aim", "application/x-aim" );
        contentTypes.put( "art", "image/x-jg" );
        contentTypes.put( "asf", "video/x-ms-asf" );
        contentTypes.put( "asx", "video/x-ms-asf" );
        contentTypes.put( "au", "audio/basic" );
        contentTypes.put( "avi", "video/x-msvideo" );
        contentTypes.put( "avx", "video/x-rad-screenplay" );
        contentTypes.put( "bcpio", "application/x-bcpio" );
        contentTypes.put( "bin", "application/octet-stream" );
        contentTypes.put( "bmp", "image/bmp" );
        contentTypes.put( "body", "text/html" );
        contentTypes.put( "cdf", "application/x-cdf" );
        contentTypes.put( "cer", "application/x-x509-ca-cert" );
        contentTypes.put( "class", "application/java" );
        contentTypes.put( "cpio", "application/x-cpio" );
        contentTypes.put( "csh", "application/x-csh" );
        contentTypes.put( "css", "text/css" );
        contentTypes.put( "dib", "image/bmp" );
        contentTypes.put( "doc", "application/msword" );
        contentTypes.put( "dtd", "application/xml-dtd" );
        contentTypes.put( "dv", "video/x-dv" );
        contentTypes.put( "dvi", "application/x-dvi" );
        contentTypes.put( "eps", "application/postscript" );
        contentTypes.put( "etx", "text/x-setext" );
        contentTypes.put( "exe", "application/octet-stream" );
        contentTypes.put( "gif", "image/gif" );
        contentTypes.put( "gtar", "application/x-gtar" );
        contentTypes.put( "gz", "application/x-gzip" );
        contentTypes.put( "hdf", "application/x-hdf" );
        contentTypes.put( "hqx", "application/mac-binhex40" );
        contentTypes.put( "htc", "text/x-component" );
        contentTypes.put( "htm", "text/html" );
        contentTypes.put( "html", "text/html" );
        contentTypes.put( "hqx", "application/mac-binhex40" );
        contentTypes.put( "ief", "image/ief" );
        contentTypes.put( "jad", "text/vnd.sun.j2me.app-descriptor" );
        contentTypes.put( "jar", "application/java-archive" );
        contentTypes.put( "java", "text/plain" );
        contentTypes.put( "jnlp", "application/x-java-jnlp-file" );
        contentTypes.put( "jpe", "image/jpeg" );
        contentTypes.put( "jpeg", "image/jpeg" );
        contentTypes.put( "jpg", "image/jpeg" );
        contentTypes.put( "js", "text/javascript" );
        contentTypes.put( "jsf", "text/plain" );
        contentTypes.put( "jspf", "text/plain" );
        contentTypes.put( "kar", "audio/x-midi" );
        contentTypes.put( "latex", "application/x-latex" );
        contentTypes.put( "m3u", "audio/x-mpegurl" );
        contentTypes.put( "mac", "image/x-macpaint" );
        contentTypes.put( "man", "application/x-troff-man" );
        contentTypes.put( "mathml", "application/mathml+xml" );
        contentTypes.put( "me", "application/x-troff-me" );
        contentTypes.put( "mid", "audio/x-midi" );
        contentTypes.put( "midi", "audio/x-midi" );
        contentTypes.put( "mif", "application/x-mif" );
        contentTypes.put( "mov", "video/quicktime" );
        contentTypes.put( "movie", "video/x-sgi-movie" );
        contentTypes.put( "mp1", "audio/x-mpeg" );
        contentTypes.put( "mp2", "audio/x-mpeg" );
        contentTypes.put( "mp3", "audio/x-mpeg" );
        contentTypes.put( "mp4", "video/mp4" );
        contentTypes.put( "mpa", "audio/x-mpeg" );
        contentTypes.put( "mpe", "video/mpeg" );
        contentTypes.put( "mpeg", "video/mpeg" );
        contentTypes.put( "mpega", "audio/x-mpeg" );
        contentTypes.put( "mpg", "video/mpeg" );
        contentTypes.put( "mpv2", "video/mpeg2" );
        contentTypes.put( "ms", "application/x-wais-source" );
        contentTypes.put( "nc", "application/x-netcdf" );
        contentTypes.put( "oda", "application/oda" );
        contentTypes.put( "odb", "application/vnd.oasis.opendocument.database" );
        contentTypes.put( "odc", "application/vnd.oasis.opendocument.chart" );
        contentTypes.put( "odf", "application/vnd.oasis.opendocument.formula" );
        contentTypes.put( "odg", "application/vnd.oasis.opendocument.graphics" );
        contentTypes.put( "odi", "application/vnd.oasis.opendocument.image" );
        contentTypes.put( "odm", "application/vnd.oasis.opendocument.text-master" );
        contentTypes.put( "odp", "application/vnd.oasis.opendocument.presentation" );
        contentTypes.put( "ods", "application/vnd.oasis.opendocument.spreadsheet" );
        contentTypes.put( "odt", "application/vnd.oasis.opendocument.text" );
        contentTypes.put( "ogg", "application/ogg" );
        contentTypes.put( "otg", "application/vnd.oasis.opendocument.graphics-template" );
        contentTypes.put( "oth", "application/vnd.oasis.opendocument.text-web" );
        contentTypes.put( "otp", "application/vnd.oasis.opendocument.presentation-template" );
        contentTypes.put( "ots", "application/vnd.oasis.opendocument.spreadsheet-template" );
        contentTypes.put( "ott", "application/vnd.oasis.opendocument.text-template" );
        contentTypes.put( "pbm", "image/x-portable-bitmap" );
        contentTypes.put( "pct", "image/pict" );
        contentTypes.put( "pdf", "application/pdf" );
        contentTypes.put( "pgm", "image/x-portable-graymap" );
        contentTypes.put( "pic", "image/pict" );
        contentTypes.put( "pict", "image/pict" );
        contentTypes.put( "pls", "audio/x-scpls" );
        contentTypes.put( "png", "image/png" );
        contentTypes.put( "pnm", "image/x-portable-anymap" );
        contentTypes.put( "pnt", "image/x-macpaint" );
        contentTypes.put( "ppm", "image/x-portable-pixmap" );
        contentTypes.put( "ppt", "application/vnd.ms-powerpoint" );
        contentTypes.put( "pps", "application/vnd.ms-powerpoint" );
        contentTypes.put( "ps", "application/postscript" );
        contentTypes.put( "psd", "image/x-photoshop" );
        contentTypes.put( "qt", "video/quicktime" );
        contentTypes.put( "qti", "image/x-quicktime" );
        contentTypes.put( "qtif", "image/x-quicktime" );
        contentTypes.put( "ras", "image/x-cmu-raster" );
        contentTypes.put( "rdf", "application/rdf+xml" );
        contentTypes.put( "rgb", "image/x-rgb" );
        contentTypes.put( "rm", "application/vnd.rn-realmedia" );
        contentTypes.put( "roff", "application/x-troff" );
        contentTypes.put( "rtf", "application/rtf" );
        contentTypes.put( "rtx", "text/richtext" );
        contentTypes.put( "sh", "application/x-sh" );
        contentTypes.put( "shar", "application/x-shar" );
        contentTypes.put( "smf", "audio/x-midi" );
        contentTypes.put( "sit", "application/x-stuffit" );
        contentTypes.put( "snd", "audio/basic" );
        contentTypes.put( "src", "application/x-wais-source" );
        contentTypes.put( "sv4cpio", "application/x-sv4cpio" );
        contentTypes.put( "sv4crc", "application/x-sv4crc" );
        contentTypes.put( "svg", "image/svg+xml" );
        contentTypes.put( "svgz", "image/svg+xml" );
        contentTypes.put( "swf", "application/x-shockwave-flash" );
        contentTypes.put( "t", "application/x-troff" );
        contentTypes.put( "tar", "application/x-tar" );
        contentTypes.put( "tcl", "application/x-tcl" );
        contentTypes.put( "tex", "application/x-tex" );
        contentTypes.put( "texi", "application/x-texinfo" );
        contentTypes.put( "texinfo", "application/x-texinfo" );
        contentTypes.put( "tif", "image/tiff" );
        contentTypes.put( "tiff", "image/tiff" );
        contentTypes.put( "tr", "application/x-troff" );
        contentTypes.put( "tsv", "text/tab-separated-values" );
        contentTypes.put( "txt", "text/plain" );
        contentTypes.put( "ulw", "audio/basic" );
        contentTypes.put( "ustar", "application/x-ustar" );
        contentTypes.put( "vxml", "application/voicexml+xml" );
        contentTypes.put( "xbm", "image/x-xbitmap" );
        contentTypes.put( "xht", "application/xhtml+xml" );
        contentTypes.put( "xhtml", "application/xhtml+xml" );
        contentTypes.put( "xls", "application/vnd.ms-excel" );
        contentTypes.put( "xml", "application/xml" );
        contentTypes.put( "xpm", "image/x-xpixmap" );
        contentTypes.put( "xsl", "application/xml" );
        contentTypes.put( "xslt", "application/xslt+xml" );
        contentTypes.put( "xul", "application/vnd.mozilla.xul+xml" );
        contentTypes.put( "xwd", "image/x-xwindowdump" );
        contentTypes.put( "vsd", "application/x-visio" );
        contentTypes.put( "wav", "audio/x-wav" );
        contentTypes.put( "wbmp", "image/vnd.wap.wbmp" );
        contentTypes.put( "wml", "text/vnd.wap.wml" );
        contentTypes.put( "wmlc", "application/vnd.wap.wmlc" );
        contentTypes.put( "wmls", "text/vnd.wap.wmlscript" );
        contentTypes.put( "wmlscriptc", "application/vnd.wap.wmlscriptc" );
        contentTypes.put( "wmv", "video/x-ms-wmv" );
        contentTypes.put( "wrl", "x-world/x-vrml" );
        contentTypes.put( "wspolicy", "application/wspolicy+xml" );
        contentTypes.put( "Z", "application/x-compress" );
        contentTypes.put( "z", "application/x-compress" );
        contentTypes.put( "zip", "application/zip" );
    }

    /**
     * Provides static files as they are in the File System.
     */
    @Override
    protected void get( final Request request, final Response response )
        throws IOException
    {
        File requested = new File( baseDir, request.getPath() );
        if ( !requested.exists() )
        {
            response.setStatus( NOT_FOUND );
            return;
        }

        if ( requested.isDirectory() )
        {
            requested = new File( requested, "index.html" );
        }

        if ( requested.exists() )
        {
            response.setBody( new FileResponseBodyWriter( requested ) );

            String contentType = getContentType( requested );
            if ( contentType != null )
            {
                response.addHeader( CONTENT_TYPE, contentType );
            }
        }
        else
        {
            response.setStatus( NOT_FOUND );
        }
    }

    /**
     * Retrieves the input file Content-Type, depending on the file name extension.
     *
     * @param file the file for which the content type has to be retrieved.
     * @return the input file content type, null if not found.
     */
    private String getContentType( File file )
    {
        int extensionSeparator = file.getName().lastIndexOf( '.' );
        String extension = file.getName().substring( ++extensionSeparator );
        return contentTypes.get( extension );
    }

}
