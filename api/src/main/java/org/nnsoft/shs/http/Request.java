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

import java.io.IOException;
import java.util.List;

import org.nnsoft.shs.collections.MultiValued;
import org.nnsoft.shs.io.RequestBodyReader;

/**
 * HTTP Request representation.
 */
public interface Request
{

    /**
     * HTTP method representation.
     */
    public enum Method
    {

        /**
         * The {@code OPTIONS} method.
         */
        OPTIONS,
        /**
         * The {@code GET} method.
         */
        GET,
        /**
         * The {@code HEAD} method.
         */
        HEAD,
        /**
         * The {@code POST} method.
         */
        POST,
        /**
         * The {@code PUT} method.
         */
        PUT,
        /**
         * The {@code DELETE} method.
         */
        DELETE,
        /**
         * The {@code TRACE} method.
         */
        TRACE,
        /**
         * The {@code CONNECT} method.
         */
        CONNECT;

    }

    /**
     * Returns the request path.
     *
     * @return the request path
     */
    String getPath();

    /**
     * Returns the HTTP method.
     *
     * @return the HTTP method.
     */
    Method getMethod();

    /**
     * Returns the protocol name.
     *
     * @return the protocol name.
     */
    String getProtocolName();

    /**
     * Returns the protocol version.
     *
     * @return the protocol version.
     */
    String getProtocolVersion();

    /**
     * Returns the HTTP headers.
     *
     * @return the HTTP headers.
     */
    MultiValued<String, String> getHeaders();

    /**
     * Returns the list of cookies.
     *
     * @return the list of cookies.
     */
    List<Cookie> getCookies();

    /**
     * Returns the Query String parameters, populated only if HTTP method is {@link Method#GET}.
     *
     * @return the Query String parameters
     */
    MultiValued<String, String> getQueryStringParameters();

    /**
     * Returns the parameters, populated only if HTTP method is {@link Method#POST}
     * and {@code Content-Type} is set to {@code application/x-www-form-urlencoded}.
     *
     * @return the parameters
     */
    MultiValued<String, String> getParameters();

    /**
     * Returns the current HTTP Session.
     *
     * @return the current HTTP Session.
     */
    Session getSession();

    /**
     * Returns the request body content length.
     *
     * @return the request body content length, {@code -1} if it is unknown.
     */
    long getContentLength();

    /**
     * Reads and converts the request body input stream.
     *
     * @param the user defined
     * @return the request body input stream.
     * @throws IOException if any error occurs while opening the request body stream.
     */
    <T> T readRequestBody( RequestBodyReader<T> requestBodyReader )
                    throws IOException;

}
