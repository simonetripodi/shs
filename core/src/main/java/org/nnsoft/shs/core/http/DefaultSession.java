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

import static java.lang.String.format;
import static java.util.UUID.randomUUID;
import static org.nnsoft.shs.core.lang.Preconditions.checkNotNull;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.nnsoft.shs.http.Session;

/**
 * Default {@link Session} implementation
 */
final class DefaultSession
    implements Session
{

    private final UUID id = randomUUID();

    private final ConcurrentMap<String, Object> attributes = new ConcurrentHashMap<String, Object>();

    private final Date creationTime = new Date();

    private Date lastAccessedTime = creationTime;

    private boolean isNew = true;

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID getId()
    {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <A> A getAttribute( String name )
    {
        Object attribute = attributes.get( name );
        if ( attribute != null )
        {
            @SuppressWarnings( "unchecked" ) // it would throw class cast exception anyway
            A value = (A) attribute;
            return value;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAttribute( String name )
    {
        attributes.remove( name );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <A> void setAttribute( String name, A value )
    {
        checkNotNull( name, "Null attribute name not allowed" );
        attributes.put( name, value );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getCreationTime()
    {
        return creationTime;
    }

    /**
     * Updates the session LastAccessedTime.
     */
    public synchronized void updateLastAccessedTime()
    {
        lastAccessedTime = new Date();
        isNew = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Date getLastAccessedTime()
    {
        return lastAccessedTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized boolean isNew()
    {
        return isNew;
    }

    @Override
    public String toString()
    {
        return format( "Session[id=%s, attributes=%s, creationTime=%s, lastAccessedTime=%s, isNew=%s]",
                       id, attributes, creationTime, lastAccessedTime, isNew );
    }

}
