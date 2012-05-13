package org.nnsoft.shs.dispatcher.file;

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

import static org.nnsoft.shs.io.IOUtils.closeQuietly;
import static java.nio.channels.Channels.newChannel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import org.nnsoft.shs.http.ResponseBodyWriter;

/**
 * Generates a body response from a {@link File}.
 */
final class FileResponseBodyWriter
    implements ResponseBodyWriter
{

    /**
     * The file has to be transferred to the body response.
     */
    private final File toBeTransfered;

    /**
     * Creates a new ResponseBodyWriter given the file has to be transferred to the body response.
     *
     * @param toBeTransfered the file has to be transferred to the body response.
     */
    public FileResponseBodyWriter( File toBeTransfered )
    {
        this.toBeTransfered = toBeTransfered;
    }

    /**
     * {@inheritDoc}
     */
    public long getContentLength()
    {
        return toBeTransfered.length();
    }

    /**
     * {@inheritDoc}
     */
    public void write( OutputStream target )
        throws IOException
    {
        FileChannel channel = new FileInputStream( toBeTransfered ).getChannel();
        try
        {
            channel.transferTo( 0, toBeTransfered.length(), newChannel( target ) );
        }
        finally
        {
            closeQuietly( channel );
        }
    }

}
