package org.nnsoft.shs.core;

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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nnsoft.shs.http.Request;
import org.nnsoft.shs.http.RequestHandler;
import org.nnsoft.shs.http.Response;

public final class RequestBinderTestCase
{

    private RequestDispatcher dispatcher;

    private RequestHandler mock1;

    private RequestHandler mock2;

    @Before
    public void setUp()
    {
        mock1 = mock( RequestHandler.class );
        mock2 = mock( RequestHandler.class );

        dispatcher = new RequestDispatcher();
        dispatcher.addRequestHandler( "/mock1", mock1 );
        dispatcher.addRequestHandler( "/mock2*", mock2 );
    }

    @After
    public void tearDown()
    {
        mock1 = null;
        mock2 = null;
        dispatcher = null;
    }

    @Test
    public void firstOneMatches()
        throws Exception
    {
        Request request = newMockRequest( "/mock1" );
        Response response = mock( Response.class );

        dispatcher.dispatch( request, response );

        verify( mock1, times( 1 ) ).handle( request, response );
        verify( mock2, never() ).handle( request, response );
    }

    @Test
    public void secondOneMatches()
        throws Exception
    {
        Request request = newMockRequest( "/mock2" );
        Response response = mock( Response.class );

        dispatcher.dispatch( request, response );

        verify( mock1, never() ).handle( request, response );
        verify( mock2, times( 1 ) ).handle( request, response );
    }

    @Test
    public void secondOneMatchesWithLongerPattern()
        throws Exception
    {
        Request request = newMockRequest( "/mock2/some/extra/path.jsp" );
        Response response = mock( Response.class );

        dispatcher.dispatch( request, response );

        verify( mock1, never() ).handle( request, response );
        verify( mock2, times( 1 ) ).handle( request, response );
    }

    @Test
    public void noOneMatches()
        throws Exception
    {
        Request request = newMockRequest( "/mock3" );
        Response response = mock( Response.class );

        dispatcher.dispatch( request, response );

        verify( mock1, never() ).handle( request, response );
        verify( mock2, never() ).handle( request, response );
    }

    private static Request newMockRequest( String path )
    {
        final Request request = mock( Request.class );
        when( request.getPath() ).thenReturn( path );
        return request;
    }

}
