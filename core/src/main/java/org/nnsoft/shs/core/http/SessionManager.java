package org.nnsoft.shs.core.http;

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

import static java.util.UUID.fromString;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.nnsoft.shs.http.Cookie;
import org.nnsoft.shs.http.Request;
import org.nnsoft.shs.http.Response;

/**
 * The SessionRegistry is the HTTP {@link org.nnsoft.shs.http.Session} manager.
 */
public final class SessionManager
{

    private static final String SESSION_NAME = "SHSSESSIONID";

    /**
     * The registry where sessions have to be stored.
     */
    private final ConcurrentMap<UUID, DefaultSession> sessionsRegistry = new ConcurrentHashMap<UUID, DefaultSession>();

    /**
     * The timer delegated to clean the expired sessions.
     */
    private final Timer sessionsCleaner = new Timer( true );

    /**
     * The sessions max age (in milliseconds).
     */
    private final int sessionMaxAge;

    /**
     *
     * @param sessionMaxAge the max
     */
    public SessionManager( int sessionMaxAge )
    {
        this.sessionMaxAge = sessionMaxAge;
    }

    /**
     *
     *
     * @param request
     * @param response
     */
    public void manageSession( Request request, Response response )
    {
        DefaultSession session = null;

        // check first the session is present in the registry

        Iterator<Cookie> cookies = request.getCookies().iterator();
        while ( session != null || cookies.hasNext() )
        {
            Cookie cookie = cookies.next();
            if ( SESSION_NAME.equals( cookie.getName() ) )
            {
                UUID sessionId = fromString( SESSION_NAME );
                session = sessionsRegistry.get( sessionId );
                if ( session != null )
                {
                    session.updateLastAccessedTime();
                }
            }
        }

        // creates a new session, puts it in the registry and schedule for deletion

        if ( session == null )
        {
            session = new DefaultSession();

            sessionsRegistry.put( session.getId(), session );

            sessionsCleaner.schedule( new SessionRemoverTimerTask( sessionsRegistry, session.getId() ), sessionMaxAge );

            response.addCookie( new CookieBuilder()
                                    .setDomain( request.getServerHost() )
                                    .addPort( request.getServerPort() )
                                    .setMaxAge( sessionMaxAge )
                                    .setName( SESSION_NAME )
                                    .setValue( session.getId().toString() )
                                    .setPath( "/" )
                                    .build() );
        }

        ((MutableRequest) request).setSession( session );
    }

    /**
     * Turns off the timer to clean the registered sessions.
     */
    public void shutDown()
    {
        sessionsCleaner.cancel();
    }

    /**
     * A timer task to remove sessions from registry when expired.
     */
    private static final class SessionRemoverTimerTask
        extends TimerTask
    {

        /**
         * The registry where session has to be removed from.
         */
        private final ConcurrentMap<UUID, DefaultSession> sessionsRegistry;

        /**
         * The session id to remove.
         */
        private final UUID sessionId;

        /**
         * Creates a new task to remove sessions from registry when expired.
         *
         * @param sessionsRegistry the registry where session has to be removed from
         * @param sessionId the session id to remove
         */
        public SessionRemoverTimerTask( ConcurrentMap<UUID, DefaultSession> sessionsRegistry, UUID sessionId )
        {
            this.sessionsRegistry = sessionsRegistry;
            this.sessionId = sessionId;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run()
        {
            sessionsRegistry.remove( sessionId );
        }

    }

}
