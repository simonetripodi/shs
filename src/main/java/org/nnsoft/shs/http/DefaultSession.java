package org.nnsoft.shs.http;

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

import static org.nnsoft.shs.lang.Preconditions.checkNotNull;
import static java.util.UUID.randomUUID;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Default {@link Session} implementation
 */
final class DefaultSession
    implements Session
{

    private final UUID id = randomUUID();

    private final Map<String, Object> attributes = new HashMap<String, Object>();

    private final Date creationTime = new Date();

    private Date lastAccessedTime = creationTime;

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
        checkNotNull( name, "NUll attribute name not allowed" );
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
    public void updateLastAccessedTime()
    {
        this.lastAccessedTime = new Date();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastAccessedTime()
    {
        return lastAccessedTime;
    }

}
