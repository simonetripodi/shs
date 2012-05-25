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

import static org.nnsoft.shs.lang.Preconditions.checkArgument;

import java.io.File;

import org.nnsoft.shs.DefaultResponseBuilder;
import org.nnsoft.shs.HttpServerConfigurator;
import org.nnsoft.shs.RequestHandlerBuilder;
import org.nnsoft.shs.http.RequestHandler;
import org.nnsoft.shs.http.Response.Status;

/**
 * Default {@link HttpServerConfigurator} implementation
 */
final class DefaultHttpServerConfigurator
    implements HttpServerConfigurator
{

    private String host;

    private int port;

    private int threads;

    private int sessionMaxAge;

    private int keepAliveTimeOut;

    private final RequestDispatcher requestDispatcher = new RequestDispatcher();

    /**
     * The host name or the textual representation of its IP address.
     *
     * @return the host name or the textual representation of its IP address.
     */
    public String getHost()
    {
        return host;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindServerToHost( String host )
    {
        this.host = host;
    }

    /**
     * The port number where binding the server.
     *
     * @return the port number where binding the server.
     */
    public int getPort()
    {
        return port;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindServerToPort( int port )
    {
        this.port = port;
    }

    /**
     * The number of threads that will serve the HTTP requests.
     *
     * @return the number of threads that will serve the HTTP requests.
     */
    public int getThreads()
    {
        return threads;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serveRequestsWithThreads( int threads )
    {
        this.threads = threads;
    }

    /**
     * The maximum number of seconds of life of HTTP Sessions.
     *
     * @return the maximum number of seconds of life of HTTP Sessions.
     */
    public int getSessionMaxAge()
    {
        return sessionMaxAge;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionsHaveMagAge( int sessionMaxAge )
    {
        this.sessionMaxAge = sessionMaxAge;
    }

    public int getKeepAliveTimeOut()
    {
        return keepAliveTimeOut;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keepAliveConnectionsHaveTimeout( int keepAliveTimeOut )
    {
        checkArgument( keepAliveTimeOut >= 0, "Negative connection keep alive timeout not allowed" );
        this.keepAliveTimeOut = keepAliveTimeOut * 1000;
    }

    public RequestDispatcher getRequestDispatcher()
    {
        return requestDispatcher;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestHandlerBuilder serve( final String path )
    {
        checkArgument( path != null, "Null path cannot be served." );
        checkArgument( !path.isEmpty(), "Empty path not allowed." );

        return new RequestHandlerBuilder()
        {

            public void with( final RequestHandler requestHandler )
            {
                checkArgument( requestHandler != null, "Null requestHandler not allowed." );

                requestDispatcher.addRequestHandler( path, requestHandler );
            }

        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultResponseBuilder when( final Status status )
    {

        checkArgument( status != null, "Null status cannot be served." );

        return new DefaultResponseBuilder()
        {

            @Override
            public void serve( File defaultReply )
            {
                checkArgument( defaultReply != null, "Null defaultReply cannot provide reply for %s.", status );
                checkArgument( defaultReply.exists(), "Cannot provide defaultReply reply for %s because file %s doesn't exist.", status, defaultReply );
                checkArgument( defaultReply.isFile(), "Cannot provide defaultReply reply for %s because file %s is not a regular file.", status, defaultReply );

                requestDispatcher.addDefaultResponse( status, defaultReply );
            }

        };
    }

}
