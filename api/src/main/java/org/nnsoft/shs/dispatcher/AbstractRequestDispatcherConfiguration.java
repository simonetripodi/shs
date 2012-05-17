package org.nnsoft.shs.dispatcher;

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

import static org.nnsoft.shs.lang.Preconditions.checkState;

import org.nnsoft.shs.http.Response.Status;

public abstract class AbstractRequestDispatcherConfiguration
    implements RequestDispatcherConfiguration
{

    private RequestDispatcherBinder binder;

    public final void configure( RequestDispatcherBinder binder )
    {
        checkState( this.binder == null, "Re-entry not allowed!" );

        this.binder = binder;

        try
        {
            configure();
        }
        finally
        {
            this.binder = null;
        }
    }

    protected abstract void configure();

    /**
     *
     * @param path
     * @return
     * @see RequestDispatcherBinder#serve(String)
     */
    protected final RequestDispatcherBuilder serve( String path )
    {
        return binder.serve( path );
    }

    /**
     *
     * @param status
     * @return
     * @see RequestDispatcherBinder#when(Status)
     */
    protected final DefaultResponseBuilder when( Status status )
    {
        return binder.when( status );
    }

}
