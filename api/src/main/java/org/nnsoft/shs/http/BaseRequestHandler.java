package org.nnsoft.shs.http;

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

import static org.nnsoft.shs.http.Response.Status.METHOD_NOT_ALLOWED;

import java.io.IOException;


/**
 * Provides an abstract class to be subclassed to create
 * an HTTP handler.
 *
 * This class is inspired from HttpServlet.
 *
 * A subclass should override at least one method, usually one of:
 *
 * <ul>
 * <li><code>options</code>, for HTTP OPTIONS requests</li>
 * <li><code>get</code>, for HTTP GET requests</li>
 * <li><code>head</code>, for HTTP HEAD requests</li>
 * <li><code>post</code>, for HTTP POST requests</li>
 * <li><code>put</code>, for HTTP PUT requests</li>
 * <li><code>delete</code>, for HTTP DELETE requests</li>
 * <li><code>trace</code>, for HTTP TRACE requests</li>
 * <li><code>connect</code>, for HTTP CONNECT requests</li>
 * </ul>
 */
public abstract class BaseRequestHandler
    implements RequestHandler
{

    /**
     * {@inheritDoc}
     */
    public final void handle( Request request, Response response )
        throws IOException
    {
        switch ( request.getMethod() )
        {
            case OPTIONS:
                options( request, response );
                break;

            case GET:
                get( request, response );
                break;

            case HEAD:
                head( request, response );
                break;

            case POST:
                post( request, response );
                break;

            case PUT:
                put( request, response );
                break;

            case DELETE:
                delete( request, response );
                break;

            case TRACE:
                trace( request, response );
                break;

            case CONNECT:
                connect( request, response );
                break;
        }
    }

    /**
     * HTTP OPTIONS request.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @throws IOException if any error occurs while HTTP negotiation
     */
    protected void options( Request request, Response response )
        throws IOException
    {
        notAllowed( response );
    }

    /**
     * HTTP GET request.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @throws IOException if any error occurs while HTTP negotiation
     */
    protected void get( Request request, Response response )
        throws IOException
    {
        notAllowed( response );
    }

    /**
     * HTTP HEAD request.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @throws IOException if any error occurs while HTTP negotiation
     */
    protected void head( Request request, Response response )
        throws IOException
    {
        notAllowed( response );
    }

    /**
     * HTTP POST request.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @throws IOException if any error occurs while HTTP negotiation
     */
    protected void post( Request request, Response response )
        throws IOException
    {
        notAllowed( response );
    }

    /**
     * HTTP PUT request.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @throws IOException if any error occurs while HTTP negotiation
     */
    protected void put( Request request, Response response )
        throws IOException
    {
        notAllowed( response );
    }

    /**
     * HTTP DELETE request.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @throws IOException if any error occurs while HTTP negotiation
     */
    protected void delete( Request request, Response response )
        throws IOException
    {
        notAllowed( response );
    }

    /**
     * HTTP TRACE request.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @throws IOException if any error occurs while HTTP negotiation
     */
    protected void trace( Request request, Response response )
        throws IOException
    {
        notAllowed( response );
    }

    /**
     * HTTP CONNECT request.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @throws IOException if any error occurs while HTTP negotiation
     */
    protected void connect( Request request, Response response )
        throws IOException
    {
        notAllowed( response );
    }

    /**
     * Initializes the status of the response as METHOD_NOT_ALLOWED
     *
     * @param response the HTTP response
     */
    private final void notAllowed( Response response )
    {
        response.setStatus( METHOD_NOT_ALLOWED );
    }

}
