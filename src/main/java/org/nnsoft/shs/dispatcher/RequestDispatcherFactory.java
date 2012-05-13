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

import static org.nnsoft.shs.lang.Preconditions.checkArgument;
import static org.nnsoft.shs.lang.Preconditions.checkState;
import static org.slf4j.LoggerFactory.getLogger;
import static java.util.Arrays.asList;
import static java.util.ServiceLoader.load;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.slf4j.Logger;

/**
 * Factory for {@link RequestDispatcher} instances.
 */
public final class RequestDispatcherFactory
{

    private static final Logger logger = getLogger( RequestDispatcherFactory.class );

    /**
     * Creates a new {@link RequestDispatcher} instance by loading {@link RequestDispatcherConfiguration}
     * via the {@link ServiceLoader}.
     *
     * @return a new {@link RequestDispatcher} instance.
     */
    public static RequestDispatcher newRequestDispatcher()
    {
        return newRequestDispatcher( load( RequestDispatcherConfiguration.class ).iterator() );
    }

    /**
     * Creates a new {@link RequestDispatcher} instance, configured by input configurations.
     *
     * @param configurations the configurations containing the paths/{@link RequestHandler}s bindings
     * @return a new {@link RequestDispatcher} instance.
     */
    public static RequestDispatcher newRequestDispatcher( RequestDispatcherConfiguration...configurations )
    {
        checkArgument( configurations != null, "Impossible to create a new RequestDispatcher from configurations!" );
        return newRequestDispatcher( asList( configurations ) );
    }

    /**
     * Creates a new {@link RequestDispatcher} instance, configured by input configurations.
     *
     * @param configurations the configurations containing the paths/{@link RequestHandler}s bindings
     * @return a new {@link RequestDispatcher} instance.
     */
    public static RequestDispatcher newRequestDispatcher( Iterable<RequestDispatcherConfiguration> configurations )
    {
        checkArgument( configurations != null, "Impossible to create a new RequestDispatcher from configurations!" );
        return newRequestDispatcher( configurations.iterator() );
    }

    /**
     * Creates a new {@link RequestDispatcher} instance, configured by input configurations.
     *
     * @param configurations the configurations containing the paths/{@link RequestHandler}s bindings
     * @return a new {@link RequestDispatcher} instance.
     */
    public static RequestDispatcher newRequestDispatcher( Iterator<RequestDispatcherConfiguration> configurations )
    {
        checkArgument( configurations != null, "Impossible to create a new RequestDispatcher from configurations!" );

        boolean configured = false;
        DefaultRequestDispatcherBinder binder = new DefaultRequestDispatcherBinder();

        while ( configurations.hasNext() )
        {
            RequestDispatcherConfiguration configuration = configurations.next();

            if ( configuration != null )
            {
                logger.info( "Loading dispatch handling from {}...", configuration.getClass().getName() );

                configuration.configure( binder );

                if ( !configured )
                {
                    configured = true;
                }
            }
            else
            {
                logger.warn( "Detected null configuration, skipped from RequestDispatcher setup" );
            }
        }

        checkState( configured, "No useful configuration provided!" );

        return binder;
    }

    /**
     * Hidden constructor, this class cannot be instantiated directly.
     */
    private RequestDispatcherFactory()
    {
        // do nothing
    }

}
