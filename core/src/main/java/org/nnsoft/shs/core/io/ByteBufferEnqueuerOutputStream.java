package org.nnsoft.shs.core.io;

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

import static java.nio.ByteBuffer.allocateDirect;
import static org.nnsoft.shs.lang.Preconditions.checkArgument;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Queue;

public final class ByteBufferEnqueuerOutputStream
    extends OutputStream
{

    public static final ByteBuffer EOM = allocateDirect( 0 );

    private static final int DEFAULT_BUFFER_CHUNK_SIZE = 1024;

    private final Queue<ByteBuffer> buffers;

    private ByteBuffer currentPtr = allocateDirect( DEFAULT_BUFFER_CHUNK_SIZE );

    /**
     *
     *
     * @param buffers
     */
    public ByteBufferEnqueuerOutputStream( Queue<ByteBuffer> buffers )
    {
        checkArgument( buffers != null, "Impossible to send data to a null ByteBuffer queue" );
        this.buffers = buffers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write( int b )
        throws IOException
    {
        if ( !currentPtr.hasRemaining() )
        {
            flush();
        }

        currentPtr.put( (byte) ( b & 0xFF ) );
    }

    @Override
    public void flush()
        throws IOException
    {
        if ( currentPtr.position() == 0 )
        {
            return;
        }

        // resize the Buffer to discard extra bytes
        if ( currentPtr.position() < currentPtr.limit() )
        {
            currentPtr.limit( currentPtr.position() );
        }
        currentPtr.rewind();
        buffers.offer( currentPtr );

        currentPtr = allocateDirect( DEFAULT_BUFFER_CHUNK_SIZE );
    }

    @Override
    public void close()
        throws IOException
    {
        buffers.offer( EOM );
    }

}
