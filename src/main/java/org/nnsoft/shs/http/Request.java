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

import java.io.InputStream;
import java.util.List;

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
    Headers getHeaders();

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
    QueryStringParameters getQueryStringParameters();

    /**
     * Returns the parameters, populated only if HTTP method is {@link Method#POST}
     * and {@code Content-Type} is set to {@code application/x-www-form-urlencoded}.
     *
     * @return the parameters
     */
    Parameters getParameters();

    /**
     * Returns the request body input stream.
     *
     * @return the request body input stream.
     */
    InputStream getRequestBodyInputStream();

}
