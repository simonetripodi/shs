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

import static java.lang.String.format;
import static java.net.URLDecoder.decode;
import static java.nio.charset.Charset.forName;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * Utility class for I/O management.
 */
public final class IOUtils
{

    private static final Charset UTF_8 = forName( "UTF-8" );

    /**
     * Hidden constructor, this class must not be instantiated.
     */
    private IOUtils()
    {
        // do nothing
    }

    /**
     * Builds a message replacing placeholders in the input template
     * and encodes the result.
     *
     * @param messageTemplate a format string
     * @param args Arguments referenced by the format specifiers in the format string
     * @return the UTF-8 encoded formatted ByteBuffer
     * @see String#format(String, Object...)
     */
    public static ByteBuffer utf8ByteBuffer( String messageTemplate, Object...args )
    {
        return UTF_8.encode( format( messageTemplate, args ) );
    }

    public static CharBuffer toUtf8CharBuffer( ByteBuffer buffer )
    {
        return UTF_8.decode( buffer );
    }

    public static String toUtf8String( ByteBuffer buffer )
    {
        return UTF_8.decode( buffer ).toString();
    }

    /**
     * Decodes the URL encoded input string, in UTF-8.
     *
     * @param input the string has to be decoded.
     * @return the decoded version of input string.
     */
    public static String utf8URLDecode( String input )
    {
        try
        {
            return decode( input, UTF_8.displayName() );
        }
        catch ( UnsupportedEncodingException e )
        {
            // should not happen
            return "";
        }
    }

    /**
     * Close quietly the input closeable.
     *
     * @param closeable the closeable instance has to be closed.
     */
    public static final void closeQuietly( Closeable closeable )
    {
        if ( closeable != null )
        {
            try
            {
                closeable.close();
            }
            catch ( IOException e )
            {
                // swallow
            }
        }
    }

}
