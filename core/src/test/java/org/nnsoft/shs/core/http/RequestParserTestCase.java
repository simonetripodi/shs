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
import static org.nnsoft.shs.http.Request.Method.GET;

import java.io.ByteArrayInputStream;

import org.junit.Test;
import org.nnsoft.shs.http.Request;

public final class RequestParserTestCase
{

    @Test
    public void simpleHttpRequest()
        throws Exception
    {
        String simpleRequest = "GET /index.html HTTP/1.1\n"
                                + "Host: www.google.nl\n"
                                + "User-Agent: Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.1.3) Gecko/20070310 Iceweasel/2.0.0.3 (Debian-2.0.0.3-1) Mnenhy/0.7.5.666\n"
                                + "Accept: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5\n"
                                + "Accept-Language: en-us,en;q=0.5\n"
                                + "Accept-Encoding: gzip,deflate\n"
                                + "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\n"
                                + "Keep-Alive: 300\n"
                                + "Connection: keep-alive\n"
                                + "Referer: http://www.google.nl/index.html\n\n";
        Request request = parse( simpleRequest );

        assertEquals( GET, request.getMethod() );
        assertEquals( "/index.html", request.getPath() );
        assertEquals( "HTTP", request.getProtocolName() );
        assertEquals( "1.1", request.getProtocolVersion() );
        assertEquals( "www.google.nl", request.getHeaders().getFirstValue( "Host" ) );
        assertEquals( "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.1.3) Gecko/20070310 Iceweasel/2.0.0.3 (Debian-2.0.0.3-1) Mnenhy/0.7.5.666",
                      request.getHeaders().getFirstValue( "User-Agent" ) );
        assertTrue( request.getHeaders().getValues( "Accept-Language" ).contains( "en-us" ) );
        assertTrue( request.getHeaders().getValues( "Accept-Charset" ).contains( "utf-8;q=0.7" ) );
    }

    @Test
    public void queryStringParameters()
        throws Exception
    {
        String simpleRequest = "GET /index.php?foo=xy&bar=zw HTTP/1.1\n\n";
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
        String simpleRequest = "GET /index.php?foo=xy&foo=zw HTTP/1.1\n\n";
        Request request = parse( simpleRequest );

        assertTrue( request.getQueryStringParameters().getValues( "foo" ).contains( "xy" ) );
        assertTrue( request.getQueryStringParameters().getValues( "foo" ).contains( "zw" ) );
    }

    @Test
    public void urlEncodedQueryStringParameters()
        throws Exception
    {
        String simpleRequest = "GET /index.php?foo=x%20y&foo=z%20w HTTP/1.1\n\n";
        Request request = parse( simpleRequest );

        assertTrue( request.getQueryStringParameters().getValues( "foo" ).contains( "x y" ) );
        assertTrue( request.getQueryStringParameters().getValues( "foo" ).contains( "z w" ) );
    }

    @Test
    public void parametersSentViaPostMethods()
        throws Exception
    {
        String simpleRequest = "POST /demo.html HTTP/1.1\n"
                                + "User-Agent: dummy agent\n"
                                + "Host: localhost:8080\n"
                                + "Accept: */*\n"
                                + "Content-Length: 27\n"
                                + "Content-Type: application/x-www-form-urlencoded\n\n"
                                + "param1=value1&param2=value2";
        Request request = parse( simpleRequest );

        assertTrue( request.getParameters().contains( "param1" ) );
        assertEquals( "value1", request.getParameters().getFirstValue( "param1" ) );
        assertTrue( request.getParameters().contains( "param2" ) );
        assertEquals( "value2", request.getParameters().getFirstValue( "param2" ) );
    }

    private Request parse( String mockRequestString )
        throws Exception
    {
        return new RequestParser( new ByteArrayInputStream( mockRequestString.getBytes() ) ).parse();
    }

}
