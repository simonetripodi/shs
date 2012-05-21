package org.nnsoft.shs.io;

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

import java.io.IOException;
import java.nio.ByteBuffer;

import org.nnsoft.shs.http.Request;

/**
 * {@link Request} body are read by RequestBodyReader implementations.
 *
 * This class takes inspiration from <a href="http://sonatype.github.com/async-http-client/request.html">async-http-client</a>
 *
 * @param <T> an arbitrary returned type
 */
public interface RequestBodyReader<T>
{

    /**
     * Invoked as soon as some {@link Request} body part are received.
     *
     * @param contentLength the request body content length
     * @param requestInputStream the request content body input stream
     * @return The object has to be mapped by reading the buffers
     * @throws IOException if any error occurs while reading the content body input stream
     */
    T onBodyPartReceived( ByteBuffer buffer )
        throws IOException;

}
