package org.nnsoft.shs.core.dispatcher;

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

import static org.nnsoft.shs.core.lang.Preconditions.checkArgument;
import static org.nnsoft.shs.http.Response.Status.NOT_FOUND;
import static org.nnsoft.shs.http.Response.Status.OK;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nnsoft.shs.core.io.FileResponseBodyWriter;
import org.nnsoft.shs.dispatcher.DefaultResponseBuilder;
import org.nnsoft.shs.dispatcher.RequestDispatcher;
import org.nnsoft.shs.dispatcher.RequestDispatcherBinder;
import org.nnsoft.shs.dispatcher.RequestDispatcherBuilder;
import org.nnsoft.shs.dispatcher.RequestHandler;
import org.nnsoft.shs.http.Request;
import org.nnsoft.shs.http.Response;
import org.nnsoft.shs.http.Response.Status;
import org.slf4j.Logger;

/**
 * A concrete {@link RequestDispatcher} implementation which exposes
 * {@link RequestDispatcherBinder} methods to configure paths/{@link RequestHandler}s
 * bindings.
 */
final class DefaultRequestDispatcherBinder
    implements RequestDispatcherBinder, RequestDispatcher
{

    private final Logger logger = getLogger( getClass() );

    private final List<MatchingRequestHandler> handlers = new LinkedList<MatchingRequestHandler>();

    private final Map<Status, File> defaultResponses = new EnumMap<Status, File>( Status.class );

    /**
     * {@inheritDoc}
     */
    public RequestDispatcherBuilder serve( final String path )
    {
        checkArgument( path != null, "Null path cannot be served." );
        checkArgument( path.length() > 0, "Empty path not allowed." );

        return new RequestDispatcherBuilder()
        {

            public void with( final RequestHandler requestHandler )
            {
                checkArgument( requestHandler != null, "Null requestHandler not allowed." );

                if ( logger.isDebugEnabled() )
                {
                    logger.debug( "Configuring dispatch: {} -> {}", path, requestHandler.getClass().getName() );
                }

                handlers.add( new MatchingRequestHandler( path, requestHandler ) );
            }

        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultResponseBuilder when( final Status status )
    {
        checkArgument( status != null, "Null status cannot be served." );

        return new DefaultResponseBuilder()
        {

            @Override
            public void serve( File defaultReply )
            {
                checkArgument( defaultReply != null, "Null defaultReply cannot provide reply for %s.", status );
                checkArgument( defaultReply.exists(), "Cannot provide defaultReply reply for %s because file %s doesn't exist.", status, defaultReply );
                checkArgument( defaultReply.isFile(), "Cannot provide defaultReply reply for %s because file %s is not a regular file.", status, defaultReply );

                defaultResponses.put( status, defaultReply );
            }

        };
    }

    /**
     * {@inheritDoc}
     */
    public void dispatch( Request request, Response response )
        throws IOException
    {
        if ( logger.isDebugEnabled() )
        {
            logger.debug( "Choosing the right handler to dispatch {} request...", request.getPath() );
        }

        boolean found = false;
        Iterator<MatchingRequestHandler> handlersIterator = handlers.iterator();

        while ( !found && handlersIterator.hasNext() )
        {
            MatchingRequestHandler handler = handlersIterator.next();

            if ( handler.shouldServe( request.getPath() ) )
            {
                if ( logger.isDebugEnabled() )
                {
                    logger.debug( "Request {} will be dispatched by {}",
                                  request.getPath(), handler.getRequestHandler().getClass().getName() );
                }

                // found right handler to address the request
                response.setStatus( OK );

                // exception can be thrown by the method - loop would be blocked anyway
                handler.getRequestHandler().handle( request, response );
                // return the server request
                found = true;
            }
        }

        if ( !found )
        {
            response.setStatus( NOT_FOUND );
        }

        // check a default response has to be provided

        File defaultResponse = defaultResponses.get( response.getStatus() );
        if ( defaultResponse != null )
        {
            logger.info( "Default response {} configured to reply to status {}", defaultResponse, response.getStatus() );
            response.setBody( new FileResponseBodyWriter( defaultResponse ) );
        }
        else
        {
            logger.info( "No default response configured to reply to status {}", response.getStatus() );
        }

        if ( logger.isDebugEnabled() )
        {
            logger.debug( "No handler found for path {}, request will just return NOT_FOUND", request.getPath() );
        }
    }

    /**
     * Matches URIs using the pattern grammar of the Servlet API and web.xml.
     *
     * This class has been borrowed from <a href="http://code.google.com/p/google-guice">Google Guice</a>,
     * see the original <a href="http://code.google.com/p/google-guice/source/browse/extensions/servlet/src/com/google/inject/servlet/UriPatternType.java">source</a>
     */
    private static final class MatchingRequestHandler
    {

        private static enum Kind
        {
            PREFIX,
            SUFFIX,
            LITERAL;
        }

        private final String pattern;

        private final Kind patternKind;

        private final RequestHandler requestHandler;

        public MatchingRequestHandler( String pattern, RequestHandler requestHandler )
        {
            if ( pattern.startsWith( "*" ) )
            {
                this.pattern = pattern.substring( 1 );
                this.patternKind = Kind.PREFIX;
            }
            else if ( pattern.endsWith( "*" ) )
            {
                this.pattern = pattern.substring( 0, pattern.length() - 1 );
                this.patternKind = Kind.SUFFIX;
            }
            else
            {
                this.pattern = pattern;
                this.patternKind = Kind.LITERAL;
            }
            this.requestHandler = requestHandler;
        }

        public boolean shouldServe( String uri )
        {
            if ( null == uri )
            {
                return false;
            }

            if ( patternKind == Kind.PREFIX )
            {
                return uri.endsWith( pattern );
            }
            else if ( patternKind == Kind.SUFFIX )
            {
                return uri.startsWith( pattern );
            }

            // else treat as a literal
            return pattern.equals( extractPath( uri ) );
        }

        public RequestHandler getRequestHandler()
        {
            return requestHandler;
        }

        private String extractPath( String path )
        {
            if ( patternKind == Kind.PREFIX )
            {
                return null;
            }
            else if ( patternKind == Kind.SUFFIX )
            {
                String extract = pattern;

                // trim the trailing '/'
                if ( extract.endsWith( "/" ) )
                {
                    extract = extract.substring( 0, extract.length() - 1 );
                }

                return extract;
            }

            // else treat as literal
            return path;
        }

    }

}
