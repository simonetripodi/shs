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
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import org.nnsoft.shs.io.ResponseBodyWriter;

public final class MessageResponseBodyWriter
    implements ResponseBodyWriter
{

    private final ByteBuffer message;

    public MessageResponseBodyWriter( String messageTemplate, Object...args )
    {
        message = utf8ByteBuffer( messageTemplate, args );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write( WritableByteChannel output )
        throws IOException
    {
        output.write( message );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getContentLength()
    {
        return message.limit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String contentType()
    {
        return "text/plain; charset=utf-8";
    }

}
