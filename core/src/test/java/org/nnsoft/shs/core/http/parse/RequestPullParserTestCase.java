package org.nnsoft.shs.core.http.parse;

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

import static org.nnsoft.shs.http.Headers.*;
import static org.nnsoft.shs.core.io.IOUtils.utf8Encode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.nnsoft.shs.http.Request.Method.GET;

import org.junit.Test;
import org.nnsoft.shs.core.http.CookieBuilder;
import org.nnsoft.shs.http.Cookie;
import org.nnsoft.shs.http.Request;

public final class RequestPullParserTestCase
{

    @Test
    public void parseMethod()
        throws Exception
    {
        String simpleRequest = "GET /index.html HTTP/1.1\n";
        Request request = parse( simpleRequest );

        assertEquals( GET, request.getMethod() );
        assertEquals( "/index.html", request.getPath() );
        assertEquals( "HTTP", request.getProtocolName() );
        assertEquals( "1.1", request.getProtocolVersion() );
    }

    @Test
    public void queryStringParameters()
        throws Exception
    {
        String simpleRequest = "GET /index.php?foo=xy&bar=zw HTTP/1.1\n";
        Request request = parse( simpleRequest );

        assertTrue( request.getQueryStringParameters().contains( "foo" ) );
        assertEquals( "xy", request.getQueryStringParameters().getFirstValue( "foo" ) );
        assertTrue( request.getQueryStringParameters().contains( "bar" ) );
        assertEquals( "zw", request.getQueryStringParameters().getFirstValue( "bar" ) );
    }

    @Test
    public void multiValuesQueryStringParameters()
        throws Exception
    {
        String simpleRequest = "GET /index.php?foo=xy&foo=zw HTTP/1.1\n";
        Request request = parse( simpleRequest );

        assertTrue( request.getQueryStringParameters().getValues( "foo" ).contains( "xy" ) );
        assertTrue( request.getQueryStringParameters().getValues( "foo" ).contains( "zw" ) );
    }

    @Test
    public void verifyParsedHeaders()
        throws Exception
    {
        String simpleRequest = "GET /index.html HTTP/1.1\n"
                                + "Host: www.google.nl\n"
                                + "Accept: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5\n"
                                + "Accept-Language: en-us,en;q=0.5\n"
                                + "Accept-Encoding: gzip,deflate\n"
                                + "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\n"
                                + "Keep-Alive: 300\n"
                                + "Connection: keep-alive\n"
                                + "Referer: http://www.google.nl/index.html\n";
        Request request = parse( simpleRequest );

        assertEquals( "www.google.nl", request.getHeaders().getFirstValue( "Host" ) );

        // Accept header
        assertTrue( request.getHeaders().getValues( ACCEPT ).contains( "text/xml" ) );
        assertTrue( request.getHeaders().getValues( ACCEPT ).contains( "application/xml" ) );
        assertTrue( request.getHeaders().getValues( ACCEPT ).contains( "application/xhtml+xml" ) );
        assertTrue( request.getHeaders().getValues( ACCEPT ).contains( "text/html;q=0.9" ) );
        assertTrue( request.getHeaders().getValues( ACCEPT ).contains( "text/plain;q=0.8" ) );
        assertTrue( request.getHeaders().getValues( ACCEPT ).contains( "image/png" ) );
        assertTrue( request.getHeaders().getValues( ACCEPT ).contains( "*/*;q=0.5" ) );

        // Accept-Language
        assertTrue( request.getHeaders().getValues( ACCEPT_LANGUAGE ).contains( "en-us" ) );
        assertTrue( request.getHeaders().getValues( ACCEPT_LANGUAGE ).contains( "en;q=0.5" ) );

        // Accept-Encoding
        assertTrue( request.getHeaders().getValues( ACCEPT_ENCODING ).contains( "gzip" ) );
        assertTrue( request.getHeaders().getValues( ACCEPT_ENCODING ).contains( "deflate" ) );

        // Accept-Charset
        assertTrue( request.getHeaders().getValues( ACCEPT_CHARSET ).contains( "ISO-8859-1" ) );
        assertTrue( request.getHeaders().getValues( ACCEPT_CHARSET ).contains( "utf-8;q=0.7" ) );
        assertTrue( request.getHeaders().getValues( ACCEPT_CHARSET ).contains( "*;q=0.7" ) );

        assertEquals( "300", request.getHeaders().getFirstValue( KEEP_ALIVE ) );
        assertEquals( "keep-alive", request.getHeaders().getFirstValue( CONNECTION ) );
        assertEquals( "http://www.google.nl/index.html", request.getHeaders().getFirstValue( REFERER ) );
    }

    @Test
    public void verifyUserAgentHeader()
        throws Exception
    {
        String simpleRequest = "GET /index.html HTTP/1.1\n"
                                + "User-Agent: Mozilla/5.0 (iPad; U; CPU OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko)\n";
        Request request = parse( simpleRequest );

        assertEquals( "Mozilla/5.0 (iPad; U; CPU OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko)",
                      request.getHeaders().getFirstValue( USER_AGENT ) );
    }

    @Test
    public void verifyCookiesHeader()
        throws Exception
    {
        Cookie expected1 = new CookieBuilder().setName( "name" ).setValue( "value" ).build();
        Cookie expected2 = new CookieBuilder().setName( "name2" ).setValue( "value2" ).build();

        String simpleRequest = "GET /index.html HTTP/1.1\n"
                                + "Cookie: name=value; name2=value2\n";
        Request request = parse( simpleRequest );

        assertTrue( request.getCookies().contains( expected1 ) );
        assertTrue( request.getCookies().contains( expected2 ) );
    }

    private Request parse( String mockRequestString )
        throws Exception
    {
        RequestPullParser pullParser = new RequestPullParser();

        for ( String chunk : mockRequestString.split("(?<=\\G.{4})") )
        {
            pullParser.onRequestPartRead( utf8Encode( chunk ) );
        }

        return pullParser.getParsedRequest();
    }

}
