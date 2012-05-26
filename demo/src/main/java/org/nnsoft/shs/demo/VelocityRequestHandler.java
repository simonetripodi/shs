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

import static org.apache.velocity.app.Velocity.getTemplate;
import static org.apache.velocity.app.Velocity.init;
import static org.nnsoft.shs.http.Response.Status.OK;
import static org.nnsoft.shs.lang.Preconditions.checkArgument;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.nnsoft.shs.http.BaseRequestHandler;
import org.nnsoft.shs.http.Request;
import org.nnsoft.shs.http.Response;

/**
 * A simple Apache Velocity based handler.
 */
public final class VelocityRequestHandler
    extends BaseRequestHandler
{

    /**
     * Creates a new handler, based on Velocity, that serves templates found
     * in the input directory.
     *
     * @param baseDir the base directory containing the Velocity templates to be served.
     */
    public VelocityRequestHandler( File baseDir )
    {
        checkArgument( baseDir != null, "Basedir where getting files must be not null" );
        checkArgument( baseDir.exists(), "Basedir where getting files must exist" );
        checkArgument( baseDir.isDirectory(), "Basedir where getting files must be a directory" );

        Properties properties = new Properties();
        properties.setProperty( "file.resource.loader.path", baseDir.getAbsolutePath() );
        init( properties );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void get( Request request, Response response )
        throws IOException
    {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put( "request", request );

        Template template = getTemplate( request.getPath() );

        response.setStatus( OK );
        response.setBody( new VelocityResponseBodyWriter( velocityContext, template ) );
    }

}
