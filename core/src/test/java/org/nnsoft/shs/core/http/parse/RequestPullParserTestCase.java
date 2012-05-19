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

import static org.nnsoft.shs.core.io.IOUtils.utf8Encode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.nnsoft.shs.http.Request.Method.GET;

import org.junit.Test;
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

    private Request parse( String mockRequestString )
        throws Exception
    {
        RequestPullParser pullParser = new RequestPullParser();
        pullParser.onRequestPartRead( utf8Encode( mockRequestString ) );
        return pullParser.getParsedRequest();
    }

}
