package org.nnsoft.shs.core.http;

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

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Locale.US;
import static org.nnsoft.shs.core.io.IOUtils.UTF_8;
import static org.nnsoft.shs.lang.Preconditions.checkArgument;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;

import org.nnsoft.shs.http.Cookie;
import org.nnsoft.shs.http.Response;
import org.slf4j.Logger;

/**
 * Serializes an HTTP {@link Response} to the target output stream
 *
 * This class is not thread safe, create a new instance for each serialization.
 */
public final class ResponseSerializer
{

    private static final Logger logger = getLogger( ResponseSerializer.class );

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss zzz", US ); // RFC1123

    private static final byte[] END_PADDING = "\r\n".getBytes( UTF_8 );

    private final OutputStream target;

    private final boolean gzipCompressionAccepted;

    private Response response;

    /**
     * Creates a new serializer instance given the non null output stream.
     *
     * @param target the non null output stream.
     * @param gzipCompressionAccepted if client supports gzip compression
     */
    public ResponseSerializer( OutputStream target, boolean gzipCompressionAccepted )
    {
        checkArgument( target != null, "Null OutputStream target not allowd." );
        this.target = target;
        this.gzipCompressionAccepted = gzipCompressionAccepted;
    }

    /**
     * Streams the input {@link Response} instance to the target output stream.
     *
     * @param response the response has to be serialized
     * @throws IOException if any error occurs while streaming
     */
    public void serialize( Response response )
        throws IOException
    {
        checkArgument( response != null, "Null Response cannot be serialized." );
        this.response = response;

        printProtocol();
        printHeaders();
        printCookies();
        target.write( END_PADDING );
        printBody();
    }

    /**
     * Writes the protocol reply.
     *
     * @throws IOException if any error occurs while streaming
     */
    private void printProtocol()
        throws IOException
    {
        print( "%s/%s %s %s",
               response.getProtocolName(),
               response.getProtocolVersion(),
               response.getStatus().getStatusCode(),
               response.getStatus().getStatusText() );
    }

    /**
     * Writes the HTTP Headers.
     *
     * @throws IOException if any error occurs while streaming
     */
    private void printHeaders()
        throws IOException
    {
        for ( Entry<String, List<String>> header : response.getHeaders().getAllEntries() )
        {
            Formatter formatter = new Formatter().format( "%s: ", header.getKey() );

            int counter = 0;
            for ( String headerValue : header.getValue() )
            {
                formatter.format( "%s%s", (counter++ > 0 ? ", " : ""), headerValue );
            }

            print( formatter.toString() );
        }
    }

    /**
     * Writes the HTTP Cookies.
     *
     * @throws IOException if any error occurs while streaming
     */
    private void printCookies()
        throws IOException
    {
        for ( Cookie cookie : response.getCookies() )
        {
            Formatter formatter = new Formatter()
                                     .format( "Set-Cookie: %s:%s; Path=%s; Domain=%s;",
                                              cookie.getName(), cookie.getValue(), cookie.getPath(), cookie.getDomain() );
            if ( cookie.getMaxAge() != -1 )
            {
                Date expirationDate = new Date( cookie.getMaxAge() * 1000 + currentTimeMillis() );
                String expires = dateFormat.format( expirationDate );

                formatter.format( " Expires=%s;", expires );
            }

            // secure field ignored since HTTPs is not supported in this version

            print( formatter.format( " HttpOnly" ).toString() );
        }
    }

    /**
     * Streams the HTTP Response Body.
     *
     * @throws IOException if any error occurs while streaming
     */
    private void printBody()
        throws IOException
    {
        OutputStream responseOutputStream = target;
        if ( gzipCompressionAccepted )
        {
            if ( logger.isDebugEnabled() )
            {
                logger.debug( "Streaming response using GZip compression, accepted by client" );
            }

            responseOutputStream = new GZIPOutputStream( target );
        }

        response.getBodyWriter().write( responseOutputStream );

        if ( responseOutputStream instanceof GZIPOutputStream )
        {
            ( (GZIPOutputStream) responseOutputStream ).finish();
        }
    }

    /**
     * Generic method to print UTF message to the target output stream.
     *
     * @param messageTemplate the String message format
     * @param args the message template placeholders variables
     * @throws IOException if any error occurs while streaming
     */
    private void print( String messageTemplate, Object...args )
        throws IOException
    {
        String message = format( messageTemplate, args );

        if ( logger.isDebugEnabled() )
        {
            logger.debug( "> {}", message );
        }

        target.write( message.getBytes( UTF_8 ) );
        target.write( END_PADDING );
    }

}
