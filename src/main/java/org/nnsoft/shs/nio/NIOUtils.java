package org.nnsoft.shs.nio;

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

import static java.nio.charset.Charset.forName;

import java.io.IOException;
import java.nio.channels.InterruptibleChannel;
import java.nio.charset.Charset;

/**
 * Utility class for New I/O management.
 */
public final class NIOUtils
{

    public static final Charset UTF_8 = forName( "UTF-8" );

    /**
     * Hidden constructor, this class must not be instantiated.
     */
    private NIOUtils()
    {
        // do nothing
    }

    /**
     * Close quietly the given {@link InterruptibleChannel} instance, if not null,
     * swallowing the thrown exception.
     *
     * @param channel the {@link InterruptibleChannel} instance has to be closed.
     */
    public static void closeQuietly( InterruptibleChannel channel )
    {
        if ( channel != null && channel.isOpen() )
        {
            try
            {
                channel.close();
            }
            catch ( IOException e )
            {
                // swallow it
            }
        }
    }

}
