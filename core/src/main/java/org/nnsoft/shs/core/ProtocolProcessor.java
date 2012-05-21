package org.nnsoft.shs.core;

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

import static org.nnsoft.shs.core.http.ResponseFactory.newResponse;
import static org.nnsoft.shs.core.io.IOUtils.closeQuietly;
import static org.nnsoft.shs.http.Headers.ACCEPT_ENCODING;
import static org.nnsoft.shs.http.Headers.CONNECTION;
import static org.nnsoft.shs.http.Headers.CONTENT_LENGTH;
import static org.nnsoft.shs.http.Headers.CONTENT_TYPE;
import static org.nnsoft.shs.http.Headers.DATE;
import static org.nnsoft.shs.http.Headers.SERVER;
import static org.nnsoft.shs.http.Response.Status.INTERNAL_SERVER_ERROR;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.nnsoft.shs.core.http.SessionManager;
import org.nnsoft.shs.core.http.serialize.ResponseSerializer;
import org.nnsoft.shs.http.Request;
import org.nnsoft.shs.http.Response;
import org.slf4j.Logger;

/**
 * Asynchronous socket handler to serve current server request.
 */
final class ProtocolProcessor
    implements Runnable
{

    private static final Logger logger = getLogger( ProtocolProcessor.class );

    private static final String DEFAULT_SERVER_NAME = "Simple HttpServer";

    private static final String GZIP = "gzip";

    private static final String HTTP_11 = "1.1";

    private static final String KEEP_ALIVE = "Keep-Alive";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss zzz" );

    private final SessionManager sessionManager;

    private final RequestDispatcher requestDispatcher;

    private final Request request;

    private WritableByteChannel target;

    public ProtocolProcessor( SessionManager sessionManager,
                              RequestDispatcher requestDispatcher,
                              Request request,
                              WritableByteChannel target )
    {
        this.sessionManager = sessionManager;
        this.requestDispatcher = requestDispatcher;
        this.request = request;
        this.target = target;
    }

    public void run()
    {
        Response response = newResponse();
        response.addHeader( DATE, dateFormat.format( new Date() ) );
        response.addHeader( SERVER, DEFAULT_SERVER_NAME );

        boolean keepAlive = HTTP_11.equals( request.getProtocolVersion() )
                            || ( request.getHeaders().contains( CONNECTION )
                                 && KEEP_ALIVE.equals( request.getHeaders().getFirstValue( KEEP_ALIVE ) ) );
        if ( keepAlive )
        {
            response.addHeader( CONNECTION, KEEP_ALIVE );
            // response.addHeader( KEEP_ALIVE, format( "timeout=%s", socket.getSoTimeout() / 1000 ) );
        }
        boolean gzipCompressionAccepted =  request.getHeaders().contains( ACCEPT_ENCODING )
                                           && request.getHeaders().getValues( ACCEPT_ENCODING ).contains( GZIP );

        try
        {
            sessionManager.manageSession( request, response );
            requestDispatcher.dispatch( request, response );

            response.setProtocolName( request.getProtocolName() );
            response.setProtocolVersion( request.getProtocolVersion() );

            if ( response.getBodyWriter().getContentLength() > 0 )
            {
                response.addHeader( CONTENT_LENGTH, String.valueOf( response.getBodyWriter().getContentLength() ) );

                if ( response.getBodyWriter().contentType() != null )
                {
                    response.addHeader( CONTENT_TYPE, response.getBodyWriter().contentType() );
                }

                /* if ( gzipCompressionAccepted )
                {
                    response.addHeader( CONTENT_ENCODING, GZIP );
                } */
            }
        }
        catch ( IOException e )
        {
            logger.error( "Request cannot be satisfied due to internal I/O error", e );

            response.setStatus( INTERNAL_SERVER_ERROR );
        }
        finally
        {
            try
            {
                new ResponseSerializer( target, false ).serialize( response );
            }
            catch ( IOException e )
            {
                logger.error( "Impossible to stream Response to the client", e );
            }
            finally
            {
                closeQuietly( target );
            }
        }
    }

}
