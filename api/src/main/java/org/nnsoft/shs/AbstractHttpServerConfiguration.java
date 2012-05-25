package org.nnsoft.shs;

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

import static org.nnsoft.shs.lang.Preconditions.checkState;

import org.nnsoft.shs.http.Response.Status;

/**
 * An abstract RequestDispatcherConfiguration implementation that helps on dropping the
 * boilerplate code of repeating the binder variable reference when binding.
 */
public abstract class AbstractHttpServerConfiguration
    implements HttpServerConfiguration
{

    private HttpServerConfigurator configurator;

    /**
     * {@inheritDoc}
     */
    public final void configure( HttpServerConfigurator configurator )
    {
        checkState( this.configurator == null, "Re-entry not allowed!" );

        this.configurator = configurator;

        try
        {
            configure();
        }
        finally
        {
            this.configurator = null;
        }
    }

    /**
     * @see HttpServerConfiguration#configure(HttpServerConfigurator)
     */
    protected abstract void configure();

    /**
     * Configure the host name or the textual representation of its IP address the server has to be bound.
     *
     * @param host the host name or the textual representation of its IP address.
     * @see HttpServerConfigurator#bindServerToHost(String)
     */
    protected final void bindServerToHost( String host )
    {
        configurator.bindServerToHost( host );
    }

    /**
     * Configure the port number where binding the server.
     *
     * @param port the port number where binding the server.
     * @see HttpServerConfigurator#bindServerToPort(int)
     */
    protected final void bindServerToPort( int port )
    {
        configurator.bindServerToPort( port );
    }

    /**
     * Configure the number of threads that will serve the HTTP requests.
     *
     * @param threads the number of threads that will serve the HTTP requests.
     * @see HttpServerConfigurator#serveRequestsWithThreads(int)
     */
    protected final void serveRequestsWithThreads( int threads )
    {
        configurator.serveRequestsWithThreads( threads );
    }

    /**
     * Configure the maximum number of seconds of life of HTTP Sessions.
     *
     * @param sessionMaxAge the maximum number of seconds of life of HTTP Sessions.
     * @see HttpServerConfigurator#sessionsHaveMagAge(int)
     */
    protected final void sessionsHaveMagAge( int sessionMaxAge )
    {
        configurator.sessionsHaveMagAge( sessionMaxAge );
    }

    /**
     * Configure the connections keep-alive timeout, in seconds.
     *
     * @param keepAliveTimeOut the connections keep-alive timeout, in seconds.
     */
    protected final void keepAliveConnectionsHaveTimeout( int keepAliveTimeOut )
    {
        configurator.keepAliveConnectionsHaveTimeout( keepAliveTimeOut );
    }

    /**
     *Starts binding a request path, can be expressed using the {@code web.xml} grammar,
     * to a {@link org.nnsoft.shs.http.RequestHandler}.
     *
     * @param path the path for handling calls.
     * @return the builder to associate a request dispatcher
     * @see HttpServerConfigurator#serve(String)
     */
    protected final RequestHandlerBuilder serve( String path )
    {
        return configurator.serve( path );
    }

    /**
     *
     * Allows defining the default response has to be shown when
     * replying to clients with specified status.
     *
     * @param status the status the server is replying to clients
     * @return the builder to associate a fixed file to the given status
     */
    protected final DefaultResponseBuilder when( Status status )
    {
        return configurator.when( status );
    }

}
