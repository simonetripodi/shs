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

import static org.nnsoft.shs.lang.Preconditions.checkArgument;
import static java.nio.channels.SelectionKey.OP_ACCEPT;
import static java.nio.channels.ServerSocketChannel.open;
import static java.nio.channels.spi.SelectorProvider.provider;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.nnsoft.shs.HttpServer.Status.INITIALIZED;
import static org.nnsoft.shs.HttpServer.Status.RUNNING;
import static org.nnsoft.shs.HttpServer.Status.STOPPED;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;

import org.nnsoft.shs.dispatcher.RequestDispatcher;
import org.slf4j.Logger;

/**
 * A simple {@link HttpServer} implementation.
 *
 * This class must NOT be shared across threads, consider it be used inside main(String...) method.
 */
public final class SimpleHttpServer
    implements HttpServer, Runnable
{

    private final Logger logger = getLogger( getClass() );

    private ExecutorService requestsExecutor;

    private RequestDispatcher dispatcher;

    private Selector selector;

    private Status currentStatus = STOPPED;

    /**
     * {@inheritDoc}
     */
    public void init( int port, int threads, RequestDispatcher dispatcher )
        throws InitException
    {
        checkArgument( port > 0, "Impossible to listening on port %s, it must be a positive number", port );
        checkArgument( threads > 0, "Impossible to serve requests with negative or none threads" );
        checkArgument( dispatcher != null, "Impossible to serve requests with a null dispatcher" );

        if ( currentStatus != STOPPED )
        {
            throw new InitException( "Current server cannot be configured while in %s status.", currentStatus );
        }

        logger.info( "Initializing server using {} threads...", threads );

        requestsExecutor = newFixedThreadPool( threads );

        logger.info( "Done! Initializing the request dispatcher..." );

        this.dispatcher = dispatcher;

        logger.info( "Done! listening on port {} ...", port );

        ServerSocketChannel server;
        try
        {
            server = open();
            server.socket().bind( new InetSocketAddress( port ) );
            server.configureBlocking( false );

            selector = provider().openSelector();
            server.register( selector, OP_ACCEPT );
        }
        catch ( IOException e )
        {
            throw new InitException( "Impossible to start server on port %s (with %s threads): %s",
                                     port, threads, e.getMessage() );
        }

        logger.info( "Done! Server has been successfully initialized, it can be now started" );

        currentStatus = INITIALIZED;
    }

    /**
     * {@inheritDoc}
     */
    public void start()
        throws RunException
    {
        if ( currentStatus != INITIALIZED )
        {
            throw new RunException( "Current server cannot be configured while in %s status, stop then init again before.",
                                    currentStatus );
        }

        logger.info( "Server is starting up..." );

        requestsExecutor.submit( this );

        logger.info( "Server successfully started! Waiting for new requests..." );

        currentStatus = RUNNING;
    }

    /**
     * {@inheritDoc}
     */
    public void run()
    {
        while ( currentStatus == RUNNING )
        {
            // Wait for an event one of the registered channels
            try
            {
                selector.select();
            }
            catch ( IOException ioe )
            {
                logger.error( "Something wrong happened while listening for connections: {}", ioe.getMessage() );

                try
                {
                    stop();
                }
                catch ( ShutdownException se )
                {
                    logger.error( "Server not correctly shutdow, see nested exceptions", se );
                }

                throw new RuntimeException( new RunException( "A fatal error occurred while waiting for clients: %s",
                                                              ioe.getMessage() ) );
            }

            // Iterate over the set of keys for which events are available
            Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
            while ( selectedKeys.hasNext() )
            {
                SelectionKey key = selectedKeys.next();
                selectedKeys.remove();

                if ( !key.isValid() )
                {
                    continue;
                }

                // Check what event is available and deal with it
                if ( key.isAcceptable() )
                {
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                    try
                    {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        Socket socket = socketChannel.socket();
                        requestsExecutor.submit( new SocketRunnable( dispatcher, socket ) );
                    }
                    catch ( IOException e )
                    {
                        logger.warn( "Impossible to accept client request: {}", e.getMessage() );
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop()
        throws ShutdownException
    {
        if ( currentStatus != RUNNING )
        {
            throw new ShutdownException( "Current server cannot be stopped while in %s status.",
                                         currentStatus );
        }

        logger.info( "Server is shutting down..." );

        try
        {
            if ( selector != null && selector.isOpen() )
            {
                selector.close();
            }
        }
        catch ( IOException e )
        {
            throw new ShutdownException( "An error occurred while shutting down the server: %s", e.getMessage() );
        }
        finally
        {
            requestsExecutor.shutdown();

            logger.info( "Server is now stopped. Bye!" );

            currentStatus = STOPPED;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Status getStatus()
    {
        return currentStatus;
    }

}
