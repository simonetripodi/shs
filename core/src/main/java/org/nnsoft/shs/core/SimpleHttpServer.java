
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

import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.SelectionKey.OP_ACCEPT;
import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.channels.SelectionKey.OP_WRITE;
import static java.nio.channels.ServerSocketChannel.open;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.nnsoft.shs.HttpServer.Status.INITIALIZED;
import static org.nnsoft.shs.HttpServer.Status.RUNNING;
import static org.nnsoft.shs.HttpServer.Status.STOPPED;
import static org.nnsoft.shs.core.http.ResponseFactory.newResponse;
import static org.nnsoft.shs.core.http.serialize.ResponseSerializer.EOM;
import static org.nnsoft.shs.core.io.IOUtils.closeQuietly;
import static org.nnsoft.shs.http.Response.Status.BAD_REQUEST;
import static org.nnsoft.shs.http.Response.Status.INTERNAL_SERVER_ERROR;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import org.nnsoft.shs.HttpServer;
import org.nnsoft.shs.HttpServerConfiguration;
import org.nnsoft.shs.InitException;
import org.nnsoft.shs.RunException;
import org.nnsoft.shs.ShutdownException;
import org.nnsoft.shs.core.http.RequestParseException;
import org.nnsoft.shs.core.http.SessionManager;
import org.nnsoft.shs.core.http.parse.RequestStreamingParser;
import org.nnsoft.shs.http.Response;
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

    private final AtomicReference<Status> currentStatus = new AtomicReference<Status>();

    public SimpleHttpServer()
    {
        currentStatus.set( STOPPED );
    }

    /**
     * {@inheritDoc}
     */
    public void init( HttpServerConfiguration configuratoruration )
        throws InitException
    {
        checkInitParameter( configuratoruration != null, "Impossible configure the server with a null configuration" );
        checkInitParameter( STOPPED == currentStatus.get(), "Current server cannot be configured while in %s status.", currentStatus );

        DefaultHttpServerConfigurator configurator = new DefaultHttpServerConfigurator();
        configuratoruration.configure( configurator );
        checkInitParameter( configurator.getHost() != null, "Impossible bind server to a null host" );
        checkInitParameter( configurator.getPort() > 0, "Impossible to listening on port %s, it must be a positive number", configurator.getPort() );
        checkInitParameter( configurator.getThreads() > 0, "Impossible to serve requests with negative or none threads" );
        checkInitParameter( configurator.getSessionMaxAge() > 0, "Sessions without timelive won't exist" );

        logger.info( "Initializing server using {} threads...", configurator.getThreads() );

        requestsExecutor = newFixedThreadPool( configurator.getThreads() );

        logger.info( "Done! Initializing the SessionManager ..." );

        sessionManager = new SessionManager( configurator.getSessionMaxAge() * 1000 );

        logger.info( "Done! Binding host {} listening on port {} ...", configurator.getHost(), configurator.getPort() );

        try
        {
            server = open();
            server.socket().bind( new InetSocketAddress( configurator.getHost(), configurator.getPort() ) );
            server.configureBlocking( false );

            selector = Selector.open();
            server.register( selector, OP_ACCEPT );
        }
        catch ( IOException e )
        {
            throw new InitException( "Impossible to start server on port %s (with %s threads): %s",
                                     configurator.getPort(), configurator.getThreads(), e.getMessage() );
        }

        this.dispatcher = configurator.getRequestDispatcher();

        logger.info( "Done! Server has been successfully initialized, it can be now started" );

        currentStatus.set( INITIALIZED );
    }

    /**
     * Verifies a configuration parameter condition, throwing {@link InitException} if not verified.
     *
     * @param condition the condition to check
     * @param messageTemplate the message template string
     * @param args the message template arguments
     * @throws InitException if the input condition is not verified
     */
    private static void checkInitParameter( boolean condition, String messageTemplate, Object...args )
        throws InitException
    {
        if ( !condition )
        {
            throw new InitException( messageTemplate, args );
        }
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

        while ( RUNNING == currentStatus.get() )
        {
            try
            {
                selector.select();
            }
            catch ( Throwable t )
            {
                throw new RunException( "Something wrong happened while listening for connections", t );
            }

            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while ( keys.hasNext() )
            {
                SelectionKey key = keys.next();

                if ( !key.isValid() )
                {
                    continue;
                }

                if ( !key.isReadable() )
                {
                    keys.remove();
                }

                try
                {
                    if ( key.isAcceptable() )
                    {
                        accept( key );
                    }
                    else if ( key.isReadable() )
                    {
                        read( key, keys );
                    }
                    else if ( key.isWritable() )
                    {
                        write( key );
                    }
                }
                catch ( IOException e )
                {
                    logger.error( "An error occurred wile negotiation", e );

                    key.cancel();
                }
            }
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

    private void accept( SelectionKey key )
        throws IOException
    {
        if ( logger.isDebugEnabled() )
        {
            logger.debug( "Key {} is in OP_ACCEPT status", key );
        }

        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverChannel.accept();

        if ( socketChannel == null )
        {
            return;
        }

        socketChannel.configureBlocking( false );

        Socket socket = socketChannel.socket();
        socketChannel.register( selector, OP_READ, new RequestStreamingParser( socket.getInetAddress().getHostName(),
                                                                               socket.getLocalAddress().getHostName(),
                                                                               socket.getLocalPort() ) );
    }

    private void read( SelectionKey key, Iterator<SelectionKey> keys )
        throws IOException
    {
        if ( logger.isDebugEnabled() )
        {
            logger.debug( "Key {} is in OP_READ status", key );
        }

        SocketChannel serverChannel = (SocketChannel) key.channel();

        RequestStreamingParser requestParser = (RequestStreamingParser) key.attachment();

        ByteBuffer data = allocate( 100 );

        try
        {
            push: while ( serverChannel.read( data ) != -1 && !requestParser.isRequestMessageComplete() )
            {
                data.flip();

                try
                {
                    requestParser.onRequestPartRead( data );
                }
                catch ( RequestParseException e )
                {
                    Response response = newResponse();
                    response.setStatus( BAD_REQUEST );
                    keys.remove();
                    key.attach( response );
                    key.interestOps( OP_WRITE );
                    break push;
                }

                data.clear();
            }

            if ( requestParser.isRequestMessageComplete() )
            {
                keys.remove();
                key.interestOps( 0 );

                requestsExecutor.submit( new ProtocolProcessor( sessionManager, dispatcher, requestParser.getParsedRequest(), key ) );
            }
        }
        catch ( IOException e )
        {
            Response response = newResponse();
            response.setStatus( INTERNAL_SERVER_ERROR );
            keys.remove();
            key.attach( response );
            key.interestOps( OP_WRITE );
        }
    }

    private void write( SelectionKey key )
        throws IOException
    {
        WritableByteChannel serverChannel = (WritableByteChannel) key.channel();

        @SuppressWarnings( "unchecked" ) // type is driven by the ProtocolProcessor
        Queue<ByteBuffer> responseBuffers = ( Queue<ByteBuffer> ) key.attachment();

        ByteBuffer current = responseBuffers.poll();

        if ( current != null )
        {
            if ( EOM == current )
            {
                closeQuietly( serverChannel );
            }
            else
            {
                serverChannel.write( current );
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
