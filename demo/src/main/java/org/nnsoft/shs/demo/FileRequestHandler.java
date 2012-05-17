package org.nnsoft.shs.demo;

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

import static org.nnsoft.shs.http.Response.Status.NOT_FOUND;
import static org.nnsoft.shs.lang.Preconditions.checkArgument;

import java.io.File;
import java.io.IOException;

import org.nnsoft.shs.http.BaseRequestHandler;
import org.nnsoft.shs.http.Request;
import org.nnsoft.shs.http.Response;
import org.nnsoft.shs.core.io.FileResponseBodyWriter;

/**
 * A simple request handler that serves static files.
 *
 * <b>NOTE</b> the current handler doesn't check any content negotiation.
 */
public final class FileRequestHandler
    extends BaseRequestHandler
{

    /**
     * The main directory where static files are stored.
     */
    private final File baseDir;

    public FileRequestHandler( File baseDir )
    {
        checkArgument( baseDir != null, "Basedir where getting files must be not null" );
        checkArgument( baseDir.exists(), "Basedir where getting files must exist" );
        checkArgument( baseDir.isDirectory(), "Basedir where getting files must be a directory" );
        this.baseDir = baseDir;
    }

    /**
     * Provides static files as they are in the File System.
     */
    @Override
    protected void get( final Request request, final Response response )
        throws IOException
    {
        File requested = new File( baseDir, request.getPath() );
        if ( !requested.exists() )
        {
            response.setStatus( NOT_FOUND );
            return;
        }

        if ( requested.isDirectory() )
        {
            requested = new File( requested, "index.html" );
        }

        if ( requested.exists() )
        {
            response.setBody( new FileResponseBodyWriter( requested ) );
        }
        else
        {
            response.setStatus( NOT_FOUND );
        }
    }

}
