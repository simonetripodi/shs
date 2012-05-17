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
import static java.util.Collections.unmodifiableSet;
import static org.nnsoft.shs.lang.Objects.eq;
import static org.nnsoft.shs.lang.Objects.hash;
import static org.nnsoft.shs.lang.Preconditions.checkArgument;

import java.util.HashSet;
import java.util.Set;

import org.nnsoft.shs.http.Cookie;

/**
 * A builder to simplify the {@link Cookie} instantiation.
 */
final class CookieBuilder
{

    private String domain;

    private String name;

    private String value;

    private String path;

    private int maxAge = -1;

    private boolean secure = false;

    private Set<Integer> ports = new HashSet<Integer>();

    /**
     * Set the cookie domain - must be not null.
     *
     * @param domain the cookie domain
     * @return this builder instance
     */
    public CookieBuilder setDomain( String domain )
    {
        checkArgument( domain != null, "Cookie domain must be not null" );
        checkArgument( !domain.isEmpty(), "Cookie domain must be not empty" );
        this.domain = domain;
        return this;
    }

    /**
     * Set the cookie name - must be not null.
     *
     * @param name
     * @return this builder instance
     */
    public CookieBuilder setName( String name )
    {
        checkArgument( name != null, "Cookie name must be not null" );
        checkArgument( !name.isEmpty(), "Cookie name must be not empty" );
        this.name = name;
        return this;
    }

    /**
     * Set the cookie value - must be not null.
     *
     * @param value the cookie value
     * @return this builder instance
     */
    public CookieBuilder setValue( String value )
    {
        checkArgument( value != null, "Cookie value must be not null" );
        checkArgument( !value.isEmpty(), "Cookie value must be not empty" );
        this.value = value;
        return this;
    }

    /**
     * Set the cookie path - must be not null.
     *
     * @param path the cookie path
     * @return this builder instance
     */
    public CookieBuilder setPath( String path )
    {
        checkArgument( path != null, "Cookie path must be not null" );
        checkArgument( !path.isEmpty(), "Cookie path must be not empty" );
        this.path = path;
        return this;
    }

    /**
     * Set the cookie max age - consider using -1 for non expiration.
     *
     * @param maxAge the cookie max age
     * @return this builder instance
     */
    public CookieBuilder setMaxAge( int maxAge )
    {
        checkArgument( maxAge >= -1, "Cookie maxAge must be not less than -1" );
        this.maxAge = maxAge;
        return this;
    }

    /**
     * Set the flag to mark the cookie is secure.
     *
     * @param secure the flag to mark the cookie is secure
     * @return this builder instance
     */
    public CookieBuilder setSecure( boolean secure )
    {
        this.secure = secure;
        return this;
    }

    /**
     * Add a cookie port - must be not 0 nor negative.
     *
     * @param port a cookie port
     * @return this builder instance
     */
    public CookieBuilder addPort( int port )
    {
        checkArgument( port > 0, "Cookie port must be not 0 nor negative" );
        this.ports.add( port );
        return this;
    }

    /**
     * Creates a new {@link Cookie} instance given the set parameters.
     *
     * @return a new {@link Cookie} instance.
     */
    public Cookie build()
    {
        return new DefaultCookie( domain, name, value, path, maxAge, secure, ports );
    }

    /**
     * Default cookie implementation.
     */
    private static final class DefaultCookie
        implements Cookie
    {

        private final String domain;

        private final String name;

        private final String value;

        private final String path;

        private final int maxAge;

        private final boolean secure;

        private final Set<Integer> ports;

        private DefaultCookie( String domain,
                               String name,
                               String value,
                               String path,
                               int maxAge,
                               boolean secure,
                               Set<Integer> ports )
        {
            this.domain = domain;
            this.name = name;
            this.value = value;
            this.path = path;
            this.maxAge = maxAge;
            this.secure = secure;
            this.ports = ports;
        }

        /**
         * {@inheritDoc}
         */
        public String getDomain()
        {
            return domain;
        }

        /**
         * {@inheritDoc}
         */
        public String getName()
        {
            return name;
        }

        /**
         * {@inheritDoc}
         */
        public String getValue()
        {
            return value;
        }

        /**
         * {@inheritDoc}
         */
        public String getPath()
        {
            return path;
        }

        /**
         * {@inheritDoc}
         */
        public int getMaxAge()
        {
            return maxAge;
        }

        /**
         * {@inheritDoc}
         */
        public boolean isSecure()
        {
            return secure;
        }

        /**
         * {@inheritDoc}
         */
        public Set<Integer> getPorts()
        {
            return unmodifiableSet( ports );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode()
        {
            return hash( 1, 31, domain, maxAge, name, path, ports, secure, value );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals( Object obj )
        {
            if ( this == obj )
            {
                return true;
            }

            if ( obj == null || getClass() != obj.getClass() )
            {
                return false;
            }

            DefaultCookie other = (DefaultCookie) obj;
            return eq( domain, other.getDomain() )
                && eq( maxAge, other.getMaxAge() )
                && eq( name, other.getName() )
                && eq( path, other.getPath() )
                && eq( ports, other.getPorts() )
                && eq( secure, other.isSecure() )
                && eq( value, other.getValue() );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString()
        {
            return format( "Cookie[domain=%s, name=%s, value=%s, path=%s, maxAge=%s, secure=%s, ports=%s]",
                           domain, name, value, path, maxAge, secure, ports );
        }

    }

}
