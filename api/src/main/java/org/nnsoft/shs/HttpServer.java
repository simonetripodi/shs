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

/**
 * An Simple Server implementation able to serve
 */
public interface HttpServer
{

    /**
     * Statuses that the Server can assume.
     */
    public static enum Status
    {

        /**
         * Marks the server has been initialized.
         */
        INITIALIZED,

        /**
         * The server is currently running.
         */
        RUNNING,

        /**
         * The server is not serving requests.
         */
        STOPPED;

    }

    /**
     * Initializes the current server.
     *
     * @param serverConfig the server configuration
     * @throws InitException if any error occurs while starting up.
     */
    void init( HttpServerConfig serverConfig )
        throws InitException;

    /**
     * Starts the current server instance.
     *
     * @throws RunException if any fatal error occurs while running.
     */
    void start()
        throws RunException;

    /**
     * Stops the current server execution.
     *
     * @throws ShutdownException if any error occurs while shutting down.
     */
    void stop()
        throws ShutdownException;

    /**
     * Returns the current server status.
     *
     * @return the current server status.
     */
    Status getStatus();

}
