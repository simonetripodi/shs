package org.nnsoft.shs.demo;

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
import static java.lang.System.setProperty;
import static org.nnsoft.shs.http.Response.Status.INTERNAL_SERVER_ERROR;
import static org.nnsoft.shs.http.Response.Status.NOT_FOUND;
import static org.slf4j.LoggerFactory.getILoggerFactory;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.util.Date;
import java.util.Formatter;

import org.nnsoft.shs.AbstractHttpServerConfiguration;
import org.nnsoft.shs.HttpServer;
import org.nnsoft.shs.RunException;
import org.nnsoft.shs.ShutdownException;
import org.nnsoft.shs.core.SimpleHttpServer;
import org.slf4j.Logger;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;

/**
 * Simple HTTP Server based on Concurrent and NIO APIs.
 */
public final class SimpleHttpServerLauncher
    extends AbstractHttpServerConfiguration
{

    private final Logger logger = getLogger( getClass() );

    @Parameter( names = { "-h", "--help" }, description = "Display help information." )
    private boolean printHelp;

    @Parameter( names = { "-p", "--port" }, description = "The HTTP Server port." )
    private int port = 8080;

    @Parameter( names = { "-t", "--threads" }, description = "The number of listening thread (# of available processors by default)." )
    private int threads = getRuntime().availableProcessors();

    @Parameter( names = { "-m", "--session-max-age" }, description = "The maximum number of seconds of life of HTTP Sessions." )
    private int sessionMaxAge = 60 * 60; // 1h

    @Parameter( names = { "-H", "--host" }, description = "The host name or the textual representation of its IP address." )
    private String host = "localhost";

    @Parameter( names = { "-X", "--verbose" }, description = "Produce execution debug output." )
    private boolean verbose;

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

    @Override
    protected void configure()
    {
        bindServerToHost( host );
        bindServerToPort( port );
        serveRequestsWithThreads( threads );
        sessionsHaveMagAge( sessionMaxAge );

        serve( "/*" ).with( new FileRequestHandler( siteDir ) );
        when( NOT_FOUND ).serve( new File( siteDir, "404.html" ) );
        when( INTERNAL_SERVER_ERROR ).serve( new File( siteDir, "500.html" ) );
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

        // setup the logging stuff

        if ( verbose )
        {
            setProperty( "logging.level", "DEBUG" );
        }
        else
        {
            setProperty( "logging.level", "INFO" );
        }

        // assume SLF4J is bound to logback in the current environment
        final LoggerContext lc = (LoggerContext) getILoggerFactory();

        try
        {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext( lc );
            // the context was probably already configured by default configuration
            // rules
            lc.reset();
            configurator.doConfigure( getClass().getClassLoader().getResourceAsStream( "logback-config.xml" ) );
        }
        catch ( JoranException je )
        {
            // StatusPrinter should handle this
        }

        logger.info( "" );
        logger.info( "                         ''~``" );
        logger.info( "                        ( o o )" );
        logger.info( "+------------------.oooO--(_)--Oooo.------------------+" );
        logger.info( "" );
        logger.info( "{} v{} (built on {})",
                        new Object[] {
                            getProperty( "project.artifactId" ),
                            getProperty( "project.version" ),
                            getProperty( "build.timestamp" )
                        } );
        logger.info( "" );
        logger.info( "                     .oooO                            " );
        logger.info( "                     (   )   Oooo.                    " );
        logger.info( "+---------------------\\ (----(   )--------------------+" );
        logger.info( "                       \\_)    ) /" );
        logger.info( "                             (_/" );

        final HttpServer httpServer = new SimpleHttpServer();

        try
        {
            httpServer.init( this );
        }
        catch ( Throwable cause )
        {
            logger.error( "Server cannot be initialized", cause );
            exit( 1 );
        }

        final long start = currentTimeMillis();

        getRuntime().addShutdownHook( new Thread()
        {

            public void run()
            {
                logger.info( "" );
                logger.info( "                         ''~``" );
                logger.info( "                        ( o o )" );
                logger.info( "+------------------.oooO--(_)--Oooo.------------------+" );

                try
                {
                    httpServer.stop();
                }
                catch ( ShutdownException e )
                {
                    logger.error( "Execution terminated with errors", e );
                }

                // format the uptime string

                Formatter uptime = new Formatter().format( "Total uptime:" );

                long uptimeInSeconds = ( currentTimeMillis() - start ) / 1000;
                final long hours = uptimeInSeconds / 3600;

                if ( hours > 0 )
                {
                    uptime.format( " %s hour%s", hours, (hours > 1 ? "s" : "") );
                }

                uptimeInSeconds = uptimeInSeconds - (hours * 3600);
                final long minutes = uptimeInSeconds / 60;

                if ( minutes > 0 )
                {
                    uptime.format( " %s minute%s", minutes, (minutes > 1 ? "s" : "") );
                }

                uptimeInSeconds = uptimeInSeconds - (minutes * 60);

                if ( uptimeInSeconds > 0 )
                {
                    uptime.format( " %s second%s", uptimeInSeconds, (uptimeInSeconds > 1 ? "s" : "") );
                }

                logger.info( uptime.toString() );
                logger.info( "Finished at: {}", new Date() );

                final Runtime runtime = getRuntime();
                final int megaUnit = 1024 * 1024;
                logger.info( "Final Memory: {}M/{}M",
                             ( runtime.totalMemory() - runtime.freeMemory() ) / megaUnit,
                             runtime.totalMemory() / megaUnit );

                logger.info( "                     .oooO                            " );
                logger.info( "                     (   )   Oooo.                    " );
                logger.info( "+---------------------\\ (----(   )--------------------+" );
                logger.info( "                       \\_)    ) /" );
                logger.info( "                             (_/" );
            }

        } );

        try
        {
            httpServer.start();
        }
        catch ( RunException se )
        {
            logger.error( "Server cannot be started", se );
            exit( 1 );
        }
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
