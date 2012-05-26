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

import static org.nnsoft.shs.core.io.IOUtils.utf8ByteBuffer;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.channels.WritableByteChannel;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.nnsoft.shs.io.ResponseBodyWriter;

/**
 * A {@link ResponseBodyWriter} that streams the Velocity template in the response body.
 */
final class VelocityResponseBodyWriter
    implements ResponseBodyWriter
{

    /**
     * The Velocity context will be passed to the template.
     */
    private final VelocityContext velocityContext;

    /**
     * The Velocity template has to be rendered.
     */
    private final Template template;

    /**
     * Creates a new {@link ResponseBodyWriter} that renders the input context
     * via the template.
     *
     * @param velocityContext the Velocity context will be passed to the template.
     * @param template the Velocity template has to be rendered.
     */
    public VelocityResponseBodyWriter( VelocityContext velocityContext, Template template )
    {
        this.velocityContext = velocityContext;
        this.template = template;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String contentType()
    {
        return "text/html;charset=UTF-8";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write( WritableByteChannel output )
        throws IOException
    {
        StringWriter stringWriter = new StringWriter();

        template.merge( velocityContext, stringWriter );

        output.write( utf8ByteBuffer( stringWriter.toString() ) );
    }

}
