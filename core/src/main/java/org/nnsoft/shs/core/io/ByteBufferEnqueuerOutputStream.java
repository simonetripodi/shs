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

import static java.nio.ByteBuffer.allocate;
import static org.nnsoft.shs.lang.Preconditions.checkArgument;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Queue;

/**
 * A special {@link OutputStream} instances that, while writing bytes,
 * creates chunks of fixed size that will be enqueued in the given queue;
 * moreover, it takes the count of written bytes.
 */
public final class ByteBufferEnqueuerOutputStream
    extends OutputStream
{

    public static final ByteBuffer EOM = allocate( 0 );

    private static final int DEFAULT_BUFFER_CHUNK_SIZE = 1024;

    private final Queue<ByteBuffer> buffers;

    private ByteBuffer currentPtr;

    private final int chunkSize;

    private long writtenBytes = 0;

    /**
     * Creates a new {@code ByteBufferEnqueuerOutputStream} instance.
     *
     * @param buffers the queue where storing the chunks.
     */
    public ByteBufferEnqueuerOutputStream( Queue<ByteBuffer> buffers )
    {
        this( buffers, DEFAULT_BUFFER_CHUNK_SIZE );
    }

    public ByteBufferEnqueuerOutputStream( Queue<ByteBuffer> buffers, int chunkSize )
    {
        checkArgument( buffers != null, "Impossible to send data to a null ByteBuffer queue" );
        checkArgument( chunkSize > 0, "chunk size must be a positive integer" );
        this.buffers = buffers;
        this.chunkSize = chunkSize;

        newChunk();
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

        writtenBytes++;
        currentPtr.put( (byte) ( b & 0xFF ) );
    }

    /**
     * {@inheritDoc}
     */
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

        newChunk();
    }

    /**
     * Allocates a new ByteBuffer pointer.
     */
    private void newChunk()
    {
        currentPtr = allocate( chunkSize );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close()
        throws IOException
    {
        buffers.offer( EOM );
    }

    /**
     * Returns the number of written bytes.
     *
     * @return the number of written bytes.
     */
    public long getWrittenBytes()
    {
        return writtenBytes;
    }

}
