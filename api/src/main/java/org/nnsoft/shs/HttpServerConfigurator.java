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

import org.nnsoft.shs.http.Response.Status;

/**
 * The Server configuration.
 *
 * It has been designed as interface so users are free to implement their proxies on
 * Properties, XML, JSON, YAML, ...
 */
public interface HttpServerConfigurator
{

    /**
     * Configure the host name or the textual representation of its IP address the server has to be bound.
     *
     * @param host the host name or the textual representation of its IP address.
     */
    void bindServerToHost( String host );

    /**
     * Configure the port number where binding the server.
     *
     * @param port the port number where binding the server.
     */
    void bindServerToPort( int port );

    /**
     * Configure the number of threads that will serve the HTTP requests.
     *
     * @param threads the number of threads that will serve the HTTP requests.
     */
    void serveRequestsWithThreads( int threads );

    /**
     * Configure the maximum number of seconds of life of HTTP Sessions.
     *
     * @param sessionMaxAge the maximum number of seconds of life of HTTP Sessions.
     */
    void sessionsHaveMagAge( int sessionMaxAge );

    /**
     * Configure the connections keep-alive timeout, in seconds.
     *
     * @param keepAliveTimeOut the connections keep-alive timeout, in seconds.
     */
    void keepAliveConnectionsHaveTimeout( int keepAliveTimeOut );

    /**
     * Starts binding a request path, can be expressed using the {@code web.xml} grammar,
     * to a {@link org.nnsoft.shs.http.RequestHandler}.
     *
     * @param path the path for handling calls.
     * @return the builder to associate a request dispatcher.
     */
    RequestHandlerBuilder serve( String path );

    /**
     * Allows defining the default response has to be shown when
     * replying to clients with specified status.
     *
     * @param status the status the server is replying to clients
     * @return the builder to associate a fixed file to the given status
     */
    DefaultResponseBuilder when( Status status );

}
