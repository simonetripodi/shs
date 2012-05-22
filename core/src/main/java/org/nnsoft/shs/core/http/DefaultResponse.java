package org.nnsoft.shs.core.http;

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
import static java.util.Collections.unmodifiableCollection;
import static org.nnsoft.shs.lang.Preconditions.checkArgument;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.util.Collection;
import java.util.LinkedList;

import org.nnsoft.shs.collections.MultiValued;
import org.nnsoft.shs.core.collections.SimpleMultiValued;
import org.nnsoft.shs.http.Cookie;
import org.nnsoft.shs.http.Response;
import org.nnsoft.shs.io.ResponseBodyWriter;

/**
 * Basic {@link Response} implementation.
 */
final class DefaultResponse
    implements Response
{

    private static final String DEFAULT_PROTOCOL_NAME = "HTTP";

    private static final String DEFAULT_PROTOCOL_VERSION = "1.1";

    private Status status;

    private String protocolName = DEFAULT_PROTOCOL_NAME;

    private String protocolVersion = DEFAULT_PROTOCOL_VERSION;

    private boolean gzipCompressionEnabled;

    private final SimpleMultiValued<String, String> headers = new SimpleMultiValued<String, String>();

    private final Collection<Cookie> cookies = new LinkedList<Cookie>();

    private ResponseBodyWriter bodyWriter = new NoOpResponseBodyWriter();

    public void setStatus( Status status )
    {
        checkArgument( status != null, "Null status not allowed in HTTP Response." );
        this.status = status;
    }

    public Status getStatus()
    {
        return status;
    }

    public void setProtocolName( String protocolName )
    {
        checkArgument( protocolName != null, "Null protocolName not allowed in HTTP Response." );
        this.protocolName = protocolName;
    }

    public String getProtocolName()
    {
        return protocolName;
    }

    public String getProtocolVersion()
    {
        return protocolVersion;
    }

    public void setProtocolVersion( String protocolVersion )
    {
        checkArgument( protocolVersion != null, "Null protocolVersion cannot be set" );
        this.protocolVersion = protocolVersion;
    }

    public void addHeader( String name, String value )
    {
        checkArgument( name != null, "Null Header name not allowed" );
        checkArgument( value != null, "Null Header values not allowed" );

        headers.addValue( name, value );
    }

    public MultiValued<String, String> getHeaders()
    {
        return headers;
    }

    @Override
    public boolean isGZipCompressionEnabled()
    {
        return gzipCompressionEnabled;
    }

    @Override
    public void enableGZipCompression( boolean gzipCompressionEnabled )
    {
        this.gzipCompressionEnabled = gzipCompressionEnabled;
    }

    public void addCookie( Cookie cookie )
    {
        checkArgument( cookie != null, "Null Cookie cannot be added" );
        cookies.add( cookie );
    }

    public Collection<Cookie> getCookies()
    {
        return unmodifiableCollection( cookies );
    }

    public ResponseBodyWriter getBodyWriter()
    {
        return bodyWriter;
    }

    public void setBody( ResponseBodyWriter bodyWriter )
    {
        checkArgument( bodyWriter != null, "Null bodyWriter cannot be added" );
        this.bodyWriter = bodyWriter;
    }

    @Override
    public String toString()
    {
        return format( "Response [status=%s, protocolName=%s, protocolVersion=%s, headers=%s]",
                       status, protocolName, protocolVersion, headers );
    }

    /**
     * NO-OP {@link ResponseBodyWriter} implementation.
     */
    private static final class NoOpResponseBodyWriter
        implements ResponseBodyWriter
    {

        /**
         * {@inheritDoc}
         */
        public long getContentLength()
        {
            return -1;
        }

        @Override
        public String contentType()
        {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public void write( WritableByteChannel output )
            throws IOException
        {
            // do nothing
        }

    }

}
