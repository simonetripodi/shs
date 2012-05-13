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

import static java.lang.Runtime.getRuntime;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.exit;
import static java.lang.System.getProperty;
import static org.nnsoft.shs.dispatcher.RequestDispatcherFactory.newRequestDispatcher;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.util.Date;

import org.nnsoft.shs.dispatcher.AbstractRequestDispatcherConfiguration;
import org.nnsoft.shs.dispatcher.file.FileRequestHandler;
import org.slf4j.Logger;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;

/**
 * Simple HTTP Server based on Concurrent and NIO APIs.
 */
public final class SimpleHttpServerLauncher
{

    private final Logger logger = getLogger( getClass() );

    @Parameter( names = { "-h", "--help" }, description = "Display help information." )
    private boolean printHelp;

    @Parameter( names = { "-p", "--port" }, description = "The HTTP Server port." )
    private int port = 8080;

    @Parameter( names = { "-t", "--threads" }, description = "The number of listening thread (# of available processors by default)." )
    private int threads = getRuntime().availableProcessors();

    @Parameter(
        names = { "-s", "--sitedir" },
        description = "The directory containing the site has to be provided.",
        converter = FileConverter.class
    )
    private File siteDir = new File( getProperty( "basedir" ), "site" );

    /**
     * Hidden constructor, this class must not be instantiated.
     */
    private SimpleHttpServerLauncher()
    {
        // do nothing
    }

    private void execute( String...args )
    {
        // handle command lines
        final JCommander jCommander = new JCommander( this );
        jCommander.setProgramName( getProperty( "app.name" ) );
        jCommander.parse( args );

        if ( printHelp )
        {
            jCommander.usage();
            exit( -1 );
        }

        logger.info( "" );
        logger.info( "------------------------------------------------------------------------" );
        logger.info( "{} v{} (built on {})", new Object[] {
            getProperty( "project.artifactId" ),
            getProperty( "project.version" ),
            getProperty( "build.timestamp" )
        } );
        logger.info( "------------------------------------------------------------------------" );
        logger.info( "" );

        final HttpServer httpServer = new SimpleHttpServer();

        try
        {
            httpServer.init( port, threads, newRequestDispatcher( new AbstractRequestDispatcherConfiguration()
            {

                @Override
                protected void configure()
                {
                    serve( "/*" ).with( new FileRequestHandler( siteDir ) );
                }

            }  ) );
        }
        catch ( Throwable cause )
        {
            logger.error( "Server cannot be initialized", cause );
            exit( 1 );
        }

        final long start = currentTimeMillis();

        try
        {
            httpServer.start();
        }
        catch ( RunException se )
        {
            logger.error( "Server cannot be started", se );
            exit( 1 );
        }

        getRuntime().addShutdownHook( new Thread()
        {

            public void run()
            {
                logger.info( "" );
                logger.info( "------------------------------------------------------------------------" );

                try
                {
                    httpServer.stop();
                }
                catch ( ShutdownException e )
                {
                    logger.error( "Execution terminated with errors", e );
                }

                logger.info( "Total uptime: {}s", ( ( currentTimeMillis() - start ) / 1000 ) );
                logger.info( "Finished at: {}", new Date() );

                final Runtime runtime = getRuntime();
                final int megaUnit = 1024 * 1024;
                logger.info( "Final Memory: {}M/{}M",
                             ( runtime.totalMemory() - runtime.freeMemory() ) / megaUnit,
                             runtime.totalMemory() / megaUnit );

                logger.info( "------------------------------------------------------------------------" );
                logger.info( "" );
            }

        } );
    }

    /**
     * @param args
     */
    public static void main( String[] args )
        throws Exception
    {
        new SimpleHttpServerLauncher().execute( args );
    }

}
