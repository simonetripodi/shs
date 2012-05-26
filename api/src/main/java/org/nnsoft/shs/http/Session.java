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

import java.util.Date;
import java.util.UUID;

/**
 * Represents an HTTP user session, inspired by the
 * <a href="http://docs.oracle.com/javaee/5/api/javax/servlet/http/HttpSession.html">HttpSession</a>
 */
public interface Session
{

    /**
     * Returns the unique identifier assigned to this session.
     *
     * @return the unique identifier assigned to this session
     */
    UUID getId();

    /**
     * Returns the object bound with the specified name in this session, or null if no object is bound under the name.
     *
     * @param <A> the the attribute returned type
     * @param name a string specifying the name of the object
     * @return the object bound with the specified name in this session, or null if no object is bound under the name.
     */
    <A> A getAttribute( String name );

    /**
     * Removes the object bound with the specified name from this session.
     *
     * @param name the name of the object to remove from this session
     */
    void removeAttribute( String name );

    /**
     * Binds an object to this session, using the name specified.
     *
     * @param <A> the the attribute returned type
     * @param name the non-null name to which the object is bound
     * @param value the object to be bound
     */
    <A> void setAttribute( String name, A value );

    /**
     * Returns the time when this session was created.
     *
     * @return the time when this session was created.
     */
    Date getCreationTime();

    /**
     * Returns the last time the client sent a request associated with this session.
     *
     * @return the last time the client sent a request associated with this session.
     */
    Date getLastAccessedTime();

    /**
     * Marks the session is a new session or already existing one.
     *
     * @return true if the client does not yet know about the session or if the
     *         client chooses not to join the session.
     */
    boolean isNew();

}
