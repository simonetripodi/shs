package org.nnsoft.shs.http;

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
import static java.util.Collections.unmodifiableList;
import static org.nnsoft.shs.lang.Preconditions.checkArgument;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import org.nnsoft.shs.io.RequestBodyReader;
import org.nnsoft.shs.io.StreamAlreadyConsumedException;

/**
 * Basic {@link Request} implementation.
 */
final class DefaultRequest
    implements Request
{

    private Method method;

    private String path;

    private String protocolName;

    private String protocolVersion;

    private ByteBuffer contentBody;

    private Session session;

    private final SimpleMultiValued headers = new SimpleMultiValued();

    private final SimpleMultiValued queryStringParameters = new SimpleMultiValued();

    private final SimpleMultiValued parameters = new SimpleMultiValued();

    private final List<Cookie> cookies = new LinkedList<Cookie>();

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
    public Headers getHeaders()
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
    public QueryStringParameters getQueryStringParameters()
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
    public Parameters getParameters()
    {
        return parameters;
    }

    /**
     * Set the buffered request content body.
     *
     * @param contentBody the buffered request content body.
     */
    public void setContentBody( ByteBuffer contentBody )
    {
        checkArgument( contentBody != null, "Null contentBody not allowed" );
        this.contentBody = contentBody;
    }

    /**
     * {@inheritDoc}
     */
    public <T> T readRequestBodyInputStream( RequestBodyReader<T> requestBodyReader )
        throws IOException
    {
        checkArgument( requestBodyReader != null, "Null requestBodyReader not allowed" );
        if ( contentBody == null )
        {
            throw new StreamAlreadyConsumedException();
        }
        return requestBodyReader.read( contentBody );
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
        return format( "Response [method=%s, path=%s, protocolName=%s, protocolVersion=%s, headers=%s, cookies=%s]",
                       method, path, protocolName, protocolVersion, headers, cookies );
    }

}
