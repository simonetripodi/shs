package org.nnsoft.shs.dispatcher;

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

import static org.nnsoft.shs.http.Response.Status.OK;
import static org.nnsoft.shs.lang.Preconditions.checkArgument;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.nnsoft.shs.http.Request;
import org.nnsoft.shs.http.Response;
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

                logger.info( "Configuring dispatch: {} -> {}", path, requestHandler.getClass().getName() );

                handlers.add( new MatchingRequestHandler( path, requestHandler ) );
            }

        };
    }

    /**
     * {@inheritDoc}
     */
    public void dispatch( Request request, Response response )
        throws IOException
    {
        logger.info( "Choosing the right handler to dispatch {} request...", request.getPath() );

        for ( MatchingRequestHandler handler : handlers )
        {
            if ( handler.shouldServe( request.getPath() ) )
            {
                logger.info( "Request {} will be dispatched by {}",
                             request.getPath(), handler.getRequestHandler().getClass().getName() );

                // found right handler to address the request
                response.setStatus( OK );

                // exception can be thrown by the method - loop would be blocked anyway
                handler.getRequestHandler().handle( request, response );
                // return the server request
                return;
            }
        }

        logger.warn( "No handler found for path {}, request will just return NOT_FOUND", request.getPath() );
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
