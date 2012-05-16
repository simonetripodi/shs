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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nnsoft.shs.http.Cookie;

public final class CookieBuilderTestCase
{

    private CookieBuilder cookieBuilder;

    @Before
    public void setUp()
    {
        cookieBuilder = new CookieBuilder();
    }

    @After
    public void tearDown()
    {
        cookieBuilder = null;
    }

    // domain

    @Test( expected = IllegalArgumentException.class )
    public void nullDomainNotAllowed()
    {
        cookieBuilder.setDomain( null );
    }

    @Test( expected = IllegalArgumentException.class )
    public void emptyDomainNotAllowed()
    {
        cookieBuilder.setDomain( "" );
    }

    // name

    @Test( expected = IllegalArgumentException.class )
    public void nullNameNotAllowed()
    {
        cookieBuilder.setName( null );
    }

    @Test( expected = IllegalArgumentException.class )
    public void emptyNameNotAllowed()
    {
        cookieBuilder.setName( "" );
    }

    // value

    @Test( expected = IllegalArgumentException.class )
    public void nullValueNotAllowed()
    {
        cookieBuilder.setValue( null );
    }

    @Test( expected = IllegalArgumentException.class )
    public void emptyValueNotAllowed()
    {
        cookieBuilder.setValue( "" );
    }

    // path

    @Test( expected = IllegalArgumentException.class )
    public void nullPathNotAllowed()
    {
        cookieBuilder.setPath( null );
    }

    @Test( expected = IllegalArgumentException.class )
    public void emptyPathNotAllowed()
    {
        cookieBuilder.setPath( "" );
    }

    // max age

    @Test( expected = IllegalArgumentException.class )
    public void negativeMaxAgeNotAllowedExceptMinusOne()
    {
        cookieBuilder.setMaxAge( -10 );
    }

    // port

    @Test( expected = IllegalArgumentException.class )
    public void negativePortOrZeroNotAllowed()
    {
        cookieBuilder.addPort( 0 );
    }

    // complete cookie

    @Test
    public void verifyBuiltCookie()
    {
        Cookie cookie = cookieBuilder.setName( "made_write_conn" )
                                     .setValue( "Rg3vHJZnehYLjVg7qi3bZjzg" )
                                     .setDomain( ".foo.com" )
                                     .setPath( "/" )
                                     .setMaxAge( 30 )
                                     .addPort( 1234 )
                                     .build();

        assertEquals( "made_write_conn", cookie.getName() );
        assertEquals( "Rg3vHJZnehYLjVg7qi3bZjzg", cookie.getValue());
        assertEquals( ".foo.com", cookie.getDomain() );
        assertEquals( "/", cookie.getPath() );
        assertEquals( 30, cookie.getMaxAge() );
        assertTrue( cookie.getPorts().contains( 1234 ) );
    }

}
