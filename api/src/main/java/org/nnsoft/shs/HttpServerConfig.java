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

import org.nnsoft.shs.dispatcher.RequestDispatcher;

/**
 * The Server configuration.
 *
 * It has been designed as interface so users are free to implement their proxies on
 * Properties, XML, JSON, YAML, ...
 */
public interface HttpServerConfig
{

    /**
     * The host name or the textual representation of its IP address.
     *
     * @return the host name or the textual representation of its IP address.
     */
    String getHost();

    /**
     * The port number where binding the server.
     *
     * @return the port number where binding the server.
     */
    int getPort();

    /**
     * The number of threads that will serve the HTTP requests.
     *
     * @return the number of threads that will serve the HTTP requests.
     */
    int getThreads();

    /**
     * The maximum number of seconds of life of HTTP Sessions.
     *
     * @return the maximum number of seconds of life of HTTP Sessions.
     */
    int getSessionMaxAge();

    /**
     * The request dispatcher to handle HTTP requests.
     *
     * @return the request dispatcher to handle HTTP requests.
     */
    RequestDispatcher getRequestDispatcher();

}
