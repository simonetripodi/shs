package org.nnsoft.shs.core.http.serialize;

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
import static java.nio.channels.Channels.newChannel;
import static java.nio.channels.SelectionKey.OP_WRITE;
import static java.util.Locale.US;
import static org.nnsoft.shs.core.io.IOUtils.utf8ByteBuffer;
import static org.nnsoft.shs.lang.Preconditions.checkArgument;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.WritableByteChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.zip.GZIPOutputStream;

import org.nnsoft.shs.core.io.ByteBufferEnqueuerOutputStream;
import org.nnsoft.shs.http.Cookie;
import org.nnsoft.shs.http.Response;

/**
 * Serializes an HTTP {@link Response} to the target output stream
 *
 * This class is not thread safe, create a new instance for each serialization.
 */
public final class ResponseSerializer
{

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss zzz", US ); // RFC1123

    private static final String END_PADDING = "\r\n";

    private final Queue<ByteBuffer> responseBuffers = new ConcurrentLinkedQueue<ByteBuffer>();

    private final SelectionKey key;

    private final boolean gzipEnabled;

    private Response response;

    public ResponseSerializer( SelectionKey key )
    {
        this( key, false );
    }

    /**
     * Creates a new serializer instance.
     */
    public ResponseSerializer( SelectionKey key, boolean gzipEnabled )
    {
        checkArgument( key != null, "Null SelectionKey not allowd." );
        this.key = key;
        this.gzipEnabled = gzipEnabled;
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

        key.attach( responseBuffers );
        key.interestOps( OP_WRITE );

        printProtocol();
        printHeaders();
        printCookies();
        responseBuffers.offer( utf8ByteBuffer( END_PADDING ) );
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
        print( "%s/%s %s %s%n",
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

            formatter.format( "%n" );

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
                                     .format( "Set-Cookie: %s=%s; Path=%s; Domain=%s;",
                                              cookie.getName(), cookie.getValue(), cookie.getPath(), cookie.getDomain() );

            if ( !cookie.getPorts().isEmpty() )
            {
                formatter.format( " Port=\"" );
                int i = 0;
                for ( Integer port : cookie.getPorts() )
                {
                    formatter.format( "%s%s", ( i++ > 0 ? "," : "" ), port );
                }
                formatter.format( "\";" );
            }

            if ( cookie.getMaxAge() != -1 )
            {
                Date expirationDate = new Date( cookie.getMaxAge() * 1000 + currentTimeMillis() );
                String expires = dateFormat.format( expirationDate );

                formatter.format( " Expires=%s;", expires );
            }

            // secure field ignored since HTTPs is not supported in this version

            print( formatter.format( " HttpOnly%n" ).toString() );
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
        OutputStream target = new ByteBufferEnqueuerOutputStream( responseBuffers );

        if ( gzipEnabled )
        {
            target = new GZIPOutputStream( target );
        }

        WritableByteChannel responseChannel = newChannel( target );

        response.getBodyWriter().write( responseChannel );

        if ( gzipEnabled )
        {
            ( (GZIPOutputStream) target ).finish();
        }

        target.flush();
        target.close();
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
        responseBuffers.offer( utf8ByteBuffer( format( messageTemplate, args ) ) );
    }

}
