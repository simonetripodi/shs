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
import static java.nio.ByteBuffer.allocateDirect;
import static java.util.Collections.unmodifiableList;
import static org.nnsoft.shs.lang.Preconditions.checkArgument;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.LinkedList;
import java.util.List;

import org.nnsoft.shs.collections.MultiValued;
import org.nnsoft.shs.core.collections.SimpleMultiValued;
import org.nnsoft.shs.http.Cookie;
import org.nnsoft.shs.http.Request;
import org.nnsoft.shs.http.Session;
import org.nnsoft.shs.io.RequestBodyReader;
import org.nnsoft.shs.io.StreamAlreadyConsumedException;

/**
 * Basic {@link Request} implementation.
 */
public final class MutableRequest
    implements Request
{

    private String clientHost;

    private String serverHost;

    private int serverPort;

    private Method method;

    private String path;

    private String protocolName;

    private String protocolVersion;

    private long contentLength = -1;

    private ReadableByteChannel contentBody;

    private boolean bodyConsumed = false;

    private Session session;

    private final SimpleMultiValued<String, String> headers = new SimpleMultiValued<String, String>();

    private final SimpleMultiValued<String, String> queryStringParameters = new SimpleMultiValued<String, String>();

    private final SimpleMultiValued<String, String> parameters = new SimpleMultiValued<String, String>();

    private final List<Cookie> cookies = new LinkedList<Cookie>();

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientHost()
    {
        return clientHost;
    }

    /**
     * Sets the client host connected to this server.
     *
     * @param clientHost the client host connected to this server.
     */
    public void setClientHost( String clientHost )
    {
        checkArgument( clientHost != null, "Null clientHost not allowed" );
        this.clientHost = clientHost;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerHost()
    {
        return serverHost;
    }

    /**
     * Sets this server host.
     *
     * @param clientHost this server host.
     */
    public void setServerHost( String serverHost )
    {
        checkArgument( serverHost != null, "Null serverHost not allowed" );
        this.serverHost = serverHost;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getServerPort()
    {
        return serverPort;
    }

    /**
     * Sets this server port.
     *
     * @param clientHost this server port.
     */
    public void setServerPort( int serverPort )
    {
        checkArgument( serverPort > 0, "Negative or zero serverPort not allowed" );
        this.serverPort = serverPort;
    }

    /**
     * {@inheritDoc}
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Sets the request path.
     *
     * @param path the request path.
     */
    public void setPath( String path )
    {
        checkArgument( path != null, "Null path not allowed" );
        this.path = path;
    }

    /**
     * {@inheritDoc}
     */
    public Method getMethod()
    {
        return method;
    }

    /**
     * Set the HTTP Method
     *
     * @param method
     */
    public void setMethod( Method method )
    {
        checkArgument( method != null, "Null method not allowed" );
        this.method = method;
    }

    /**
     * {@inheritDoc}
     */
    public String getProtocolName()
    {
        return protocolName;
    }

    /**
     * Sets the protocol name.
     *
     * @param protocolName the protocol name.
     */
    public void setProtocolName( String protocolName )
    {
        checkArgument( protocolName != null, "Null protocolName name not allowed" );
        this.protocolName = protocolName;
    }

    /**
     * {@inheritDoc}
     */
    public String getProtocolVersion()
    {
        return protocolVersion;
    }

    /**
     * Sets the protocol version.
     *
     * @param protocolVersion the protocol version.
     */
    public void setProtocolVersion( String protocolVersion )
    {
        checkArgument( protocolVersion != null, "Null protocolVersion name not allowed" );
        this.protocolVersion = protocolVersion;
    }

    /**
     * Allows adding a new HTTP Header.
     *
     * @param name a non null Header name
     * @param value a non null Header value
     */
    public void addHeader( String name, String value )
    {
        checkArgument( name != null, "Null Header name not allowed" );
        checkArgument( value != null, "Null Header values not allowed" );

        headers.addValue( name, value );
    }

    /**
     * {@inheritDoc}
     */
    public MultiValued<String, String> getHeaders()
    {
        return headers;
    }

    /**
     * Add a non null cookie.
     *
     * @param cookie a non null cookie.
     */
    public void addCookie( Cookie cookie )
    {
        checkArgument( cookie != null, "Null cookie name not allowed" );
        cookies.add( cookie );
    }

    /**
     * {@inheritDoc}
     */
    public List<Cookie> getCookies()
    {
        return unmodifiableList( cookies );
    }

    /**
     * Allows adding a new query string parameter.
     *
     * @param name a non null query string parameter name
     * @param value a non null query string parameter value
     */
    public void addQueryStringParameter( String name, String value )
    {
        checkArgument( name != null, "Null QueryStringParameter name not allowed" );
        checkArgument( value != null, "Null QueryStringParameter values not allowed" );

        queryStringParameters.addValue( name, value );
    }

    /**
     * {@inheritDoc}
     */
    public MultiValued<String, String> getQueryStringParameters()
    {
        return queryStringParameters;
    }

    /**
     * Allows adding a new parameter.
     *
     * @param name a non null parameter name
     * @param value a non null parameter value
     */
    public void addParameter( String name, String value )
    {
        checkArgument( name != null, "Null Parameter name not allowed" );
        checkArgument( value != null, "Null Parameter values not allowed" );

        parameters.addValue( name, value );
    }

    /**
     * {@inheritDoc}
     */
    public MultiValued<String, String> getParameters()
    {
        return parameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getContentLength()
    {
        return contentLength;
    }

    /**
     * Sets the request body content length.
     *
     * @param contentLength the request body content length.
     */
    public void setContentLength( long contentLength )
    {
        checkArgument( contentLength >= 0, "Negative contentLength not allowed" );
        this.contentLength = contentLength;
    }

    /**
     * Set the buffered request content body.
     *
     * @param contentBody the request content body.
     */
    public void setContentBody( ReadableByteChannel contentBody )
    {
        checkArgument( contentBody != null, "Null contentBody not allowed" );
        this.contentBody = contentBody;
    }

    /**
     * {@inheritDoc}
     */
    public <T> T readRequestBody( RequestBodyReader<T> requestBodyReader )
        throws IOException
    {
        checkArgument( requestBodyReader != null, "Null requestBodyReader not allowed" );

        if ( contentBody == null || bodyConsumed )
        {
            throw new StreamAlreadyConsumedException();
        }

        ByteBuffer buffer = allocateDirect( 1024 );

        while ( contentBody.read( buffer ) != -1 )
        {
            buffer.flip();

            while ( buffer.hasRemaining() )
            {
                requestBodyReader.onBodyPartReceived( buffer );
            }

            buffer.clear();
        }

        bodyConsumed = true;

        return requestBodyReader.onCompleted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Session getSession()
    {
        return session;
    }

    /**
     * Sets the current user session.
     *
     * @param session the current user session.
     */
    public void setSession( Session session )
    {
        checkArgument( session != null, "Null session not allowed" );
        this.session = session;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return format( "Request [clientHost=%s, serverHost=%s, serverPort=%s, method=%s, path=%s, protocolName=%s, protocolVersion=%s, headers=%s, cookies=%s, queryStringParameters=%s, parameters=%s]",
                       clientHost, serverHost, serverPort, method, path, protocolName, protocolVersion, headers, cookies, queryStringParameters, parameters );
    }

}
