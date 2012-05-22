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

import static java.lang.System.currentTimeMillis;
import static java.nio.channels.SelectionKey.OP_WRITE;
import static org.nnsoft.shs.core.http.ResponseFactory.newResponse;
import static org.nnsoft.shs.http.Headers.ACCEPT_ENCODING;
import static org.nnsoft.shs.http.Headers.CONNECTION;
import static org.nnsoft.shs.http.Headers.CONTENT_LENGTH;
import static org.nnsoft.shs.http.Headers.CONTENT_TYPE;
import static org.nnsoft.shs.http.Headers.DATE;
import static org.nnsoft.shs.http.Headers.SERVER;
import static org.nnsoft.shs.http.Response.Status.INTERNAL_SERVER_ERROR;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Map.Entry;

import org.nnsoft.shs.core.http.SessionManager;
import org.nnsoft.shs.http.Cookie;
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

    private SelectionKey key;

    public ProtocolProcessor( SessionManager sessionManager,
                              RequestDispatcher requestDispatcher,
                              Request request,
                              SelectionKey key )
    {
        this.sessionManager = sessionManager;
        this.requestDispatcher = requestDispatcher;
        this.request = request;
        this.key = key;
    }

    public void run()
    {
        if ( logger.isDebugEnabled() )
        {
            logger.debug( "< {} {} {}/{}", new Object[] {
                                               request.getMethod(),
                                               request.getPath(),
                                               request.getProtocolName(),
                                               request.getProtocolVersion() } );

            for ( Entry<String, List<String>> header : request.getHeaders().getAllEntries() )
            {
                Formatter headerValues = new Formatter();
                int i = 0;
                for ( String value : header.getValue() )
                {
                    headerValues.format( "%s%s", ( i++ > 0 ? ", " : "" ), value );
                }

                logger.debug( "< {}: {}", header.getKey(), headerValues.toString() );
            }

            if ( !request.getCookies().isEmpty() )
            {
                Formatter cookies = new Formatter();
                int counter = 0;
                for ( Cookie cookie : request.getCookies() )
                {
                    cookies.format( "%s%s=%s", ( counter++ > 0 ? ", " : "" ), cookie.getName(), cookie.getValue() );
                }
                logger.debug( "< Cookie: {}", cookies.toString() );
            }
        }

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
            if ( logger.isDebugEnabled() )
            {
                // protocol
                logger.debug( "> {} {} {}/{}",
                              new Object[] {
                                  response.getProtocolName(),
                                  response.getProtocolVersion(),
                                  response.getStatus().getStatusCode(),
                                  response.getStatus().getStatusText() } );
                // headers
                for ( Entry<String, List<String>> header : response.getHeaders().getAllEntries() )
                {
                    Formatter headerValues = new Formatter();

                    int counter = 0;
                    for ( String headerValue : header.getValue() )
                    {
                        headerValues.format( "%s%s", (counter++ > 0 ? ", " : ""), headerValue );
                    }

                    logger.debug( "> {}: {}", header.getKey(), headerValues.toString() );
                }
                // cookies
                for ( Cookie cookie : response.getCookies() )
                {
                    Formatter cookieFormatter = new Formatter()
                                             .format( "%s=%s; Path=%s; Domain=%s;",
                                                      cookie.getName(), cookie.getValue(), cookie.getPath(), cookie.getDomain() );

                    if ( !cookie.getPorts().isEmpty() )
                    {
                        cookieFormatter.format( " Port=\"" );
                        int i = 0;
                        for ( Integer port : cookie.getPorts() )
                        {
                            cookieFormatter.format( "%s%s", ( i++ > 0 ? "," : "" ), port );
                        }
                        cookieFormatter.format( "\";" );
                    }

                    if ( cookie.getMaxAge() != -1 )
                    {
                        Date expirationDate = new Date( cookie.getMaxAge() * 1000 + currentTimeMillis() );
                        String expires = dateFormat.format( expirationDate );

                        cookieFormatter.format( " Expires=%s;", expires );
                    }

                    // secure field ignored since HTTPs is not supported in this version

                    logger.debug( "> Set-Cookie: {} HttpOnly", cookieFormatter.toString() );
                }
            }

            key.attach( response );
            key.interestOps( OP_WRITE );
        }
    }

}
