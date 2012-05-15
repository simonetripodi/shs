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

import static java.nio.channels.SelectionKey.OP_ACCEPT;
import static java.nio.channels.ServerSocketChannel.open;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.nnsoft.shs.HttpServer.Status.INITIALIZED;
import static org.nnsoft.shs.HttpServer.Status.RUNNING;
import static org.nnsoft.shs.HttpServer.Status.STOPPED;
import static org.nnsoft.shs.lang.Preconditions.checkArgument;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import org.nnsoft.shs.dispatcher.RequestDispatcher;
import org.nnsoft.shs.http.SessionManager;
import org.slf4j.Logger;

/**
 * A simple {@link HttpServer} implementation.
 *
 * This class must NOT be shared across threads, consider it be used inside main(String...) method.
 */
public final class SimpleHttpServer
    implements HttpServer
{

    private final Logger logger = getLogger( getClass() );

    private ExecutorService requestsExecutor;

    private ServerSocketChannel server;

    private Selector selector;

    private RequestDispatcher dispatcher;

    private SessionManager sessionManager;

    private AtomicReference<Status> currentStatus = new AtomicReference<Status>();

    public SimpleHttpServer()
    {
        currentStatus.set( STOPPED );
    }

    /**
     * {@inheritDoc}
     */
    public void init( HttpServerConfig serverConfig )
        throws InitException
    {
        checkArgument( serverConfig.getPort() > 0, "Impossible to listening on port %s, it must be a positive number", serverConfig.getPort() );
        checkArgument( serverConfig.getThreads() > 0, "Impossible to serve requests with negative or none threads" );
        checkArgument( serverConfig.getRequestDispatcher() != null, "Impossible to serve requests with a null dispatcher" );
        checkArgument( serverConfig.getSessionMaxAge() > 0, "Sessions without timelive won't exist" );

        if ( STOPPED != currentStatus.get() )
        {
            throw new InitException( "Current server cannot be configured while in %s status.", currentStatus );
        }

        logger.info( "Initializing server using {} threads...", serverConfig.getThreads() );

        requestsExecutor = newFixedThreadPool( serverConfig.getThreads() );

        logger.info( "Done! Initializing the SessionManager ..." );

        sessionManager = new SessionManager( serverConfig.getSessionMaxAge() * 1000 );

        logger.info( "Done! listening on port {} ...", serverConfig.getPort() );

        try
        {
            server = open();
            server.socket().bind( new InetSocketAddress( serverConfig.getPort() ) );
            server.configureBlocking( false );

            selector = Selector.open();
            server.register( selector, OP_ACCEPT );
        }
        catch ( IOException e )
        {
            throw new InitException( "Impossible to start server on port %s (with %s threads): %s",
                                     serverConfig.getPort(), serverConfig.getThreads(), e.getMessage() );
        }

        this.dispatcher = serverConfig.getRequestDispatcher();

        logger.info( "Done! Server has been successfully initialized, it can be now started" );

        currentStatus.set( INITIALIZED );
    }

    /**
     * {@inheritDoc}
     */
    public void start()
        throws RunException
    {
        if ( INITIALIZED != currentStatus.get() )
        {
            throw new RunException( "Current server cannot be configured while in %s status, stop then init again before.",
                                    currentStatus );
        }

        logger.info( "Server successfully started! Waiting for new requests..." );

        currentStatus.set( RUNNING );

        try
        {
            while ( RUNNING == currentStatus.get() && selector.select() > 0 )
            {
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while ( keys.hasNext() )
                {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if ( key.isValid() && key.isAcceptable() )
                    {
                        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                        SocketChannel client = serverChannel.accept();

                        if ( client == null )
                        {
                            continue;
                        }

                        requestsExecutor.submit( new ClientSocketProcessor( sessionManager, dispatcher, client.socket() ) );
                    }
                    else
                    {
                        key.cancel();
                    }
                }
            }
        }
        catch ( Throwable t )
        {
            throw new RunException( "Something wrong happened while listening for connections", t );
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
            throw new RunException( "An error occurred while disposing server resources: %s", e.getMessage() );
        }
        finally
        {
            try
            {
                if ( server != null && server.isOpen() )
                {
                    server.close();
                }
            }
            catch ( IOException e )
            {
                throw new RunException( "An error occurred while disposing server resources: %s", e.getMessage() );
            }
            finally
            {
                requestsExecutor.shutdown();
                sessionManager.shutDown();

                requestsExecutor = null;
                server = null;
                selector = null;
                dispatcher = null;
                sessionManager = null;

                logger.info( "Done! Server is now stopped. Bye!" );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop()
        throws ShutdownException
    {
        if ( RUNNING != currentStatus.get() )
        {
            throw new ShutdownException( "Current server cannot be stopped while in %s status.",
                                         currentStatus );
        }

        currentStatus.set( STOPPED );
    }

    /**
     * {@inheritDoc}
     */
    public Status getStatus()
    {
        return currentStatus.get();
    }

}
