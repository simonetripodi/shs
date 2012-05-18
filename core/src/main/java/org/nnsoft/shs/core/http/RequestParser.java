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

import static java.net.URLDecoder.decode;
import static java.nio.channels.Channels.newInputStream;
import static java.util.Locale.US;
import static org.nnsoft.shs.core.io.IOUtils.UTF_8;
import static org.nnsoft.shs.http.Headers.CONTENT_TYPE;
import static org.nnsoft.shs.http.Headers.COOKIE;
import static org.nnsoft.shs.http.Headers.USER_AGENT;
import static org.nnsoft.shs.http.Request.Method.GET;
import static org.nnsoft.shs.http.Request.Method.POST;
import static org.nnsoft.shs.http.Request.Method.PUT;
import static org.nnsoft.shs.http.Request.Method.valueOf;
import static org.nnsoft.shs.http.Request.Method.values;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.ReadableByteChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.nnsoft.shs.http.Cookie;
import org.nnsoft.shs.http.Request;
import org.slf4j.Logger;

/**
 * Simple parser of HTTP Request.
 *
 * This class is NOT thread safe!
 */
public final class RequestParser
{

    private static final Logger logger = getLogger( RequestParser.class );

    /**
     * Extracted from
     * <a href="https://github.com/sonatype/async-http-client/blob/master/src/main/java/com/ning/http/util/AsyncHttpProviderUtils.java#L55">AsyncHttpClient</a>
     */
    private final static ThreadLocal<SimpleDateFormat[]> simpleDateFormat = new ThreadLocal<SimpleDateFormat[]>() {
        protected SimpleDateFormat[] initialValue()
        {
            return new SimpleDateFormat[] {
                new SimpleDateFormat( "EEE MMM d HH:mm:ss yyyy", US ), // ASCTIME
                new SimpleDateFormat( "EEEE, dd-MMM-yy HH:mm:ss zzz", US ), // RFC1036
                new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss z", US ),
                new SimpleDateFormat( "EEE, dd-MMM-yyyy HH:mm:ss z", US ),
                new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss Z", US ),
                new SimpleDateFormat( "EEE, dd-MMM-yyyy HH:mm:ss Z", US ),
                new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss zzz", US ) // RFC1123
            };
        }
    };

    private final ReadableByteChannel requestChannel;

    private final InputStream requestBodyInputStream;

    private final BufferedReader bufferedReader;

    private final DefaultRequest request = new DefaultRequest();

    public RequestParser( ReadableByteChannel requestChannel )
    {
        this.requestChannel = requestChannel;
        this.requestBodyInputStream = newInputStream( requestChannel );
        bufferedReader = new BufferedReader( new InputStreamReader( requestBodyInputStream, UTF_8 ) );
    }

    public Request parse()
        throws RequestParseException, IOException
    {
        parseReuqestURI();
        parseHeaders();
        parseBody();
        return request;
    }

    private void parseReuqestURI()
        throws RequestParseException, IOException
    {
        String requestURI = bufferedReader.readLine();

        debugParsedRequestLine( requestURI );

        StringTokenizer tokenizer = new StringTokenizer( requestURI, " " );
        if ( 3 != tokenizer.countTokens() )
        {
            throw new RequestParseException( "Request URI '%s' is not a valid HTTP Request URI, refer to http://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html",
                                             requestURI );
        }

        String method = tokenizer.nextToken();
        try
        {
            request.setMethod( valueOf( method ) );
        }
        catch ( IllegalArgumentException e )
        {
            throw new RequestParseException( "Custom method '%s' is not supported, only %s supported",
                                             method, Arrays.toString( values() ) );
        }

        String path = tokenizer.nextToken();

        if ( GET == request.getMethod() )
        {
            int queryStringSeparatorIndex = path.indexOf( '?' );

            if ( queryStringSeparatorIndex != -1 )
            {
                String queryString = path.substring( queryStringSeparatorIndex + 1 );

                path = path.substring( 0, queryStringSeparatorIndex );

                new ParameterParser()
                {

                    @Override
                    protected void onParameterFound( String parameterName, String parameterValue )
                    {
                        request.addQueryStringParameter( parameterName, parameterValue );
                    }

                }.parse( queryString );
            }
        }

        request.setPath( decode( path, UTF_8.displayName() ) );

        String protocol = tokenizer.nextToken();
        int versionSeparator = protocol.indexOf( '/' );
        String protocolName = protocol.substring( 0, versionSeparator++ );
        String protocolVersion = protocol.substring( versionSeparator );

        request.setProtocolName( protocolName );
        request.setProtocolVersion( protocolVersion );
    }

    private void parseHeaders()
        throws RequestParseException, IOException
    {
        String header = bufferedReader.readLine();
        while ( header != null && !header.isEmpty() )
        {
            debugParsedRequestLine( header );

            int colonIndex = header.indexOf( ':' );
            if ( colonIndex != -1 )
            {
                String name = header.substring( 0, colonIndex++ ).trim();
                String allValues = header.substring( colonIndex ).trim();

                if ( COOKIE.equals( name ) )
                {
                    request.addCookie( parseCookie( allValues ) );
                }
                else if ( USER_AGENT.equals( name ) )
                {
                    request.addHeader( name, allValues );
                }
                else
                {
                    StringTokenizer valuesTokenizer = new StringTokenizer( allValues, "," );
                    while ( valuesTokenizer.hasMoreTokens() )
                    {
                        request.addHeader( name, valuesTokenizer.nextToken().trim() );
                    }
                }
            }
            else
            {
                throw new RequestParseException( "Header %s is in the wrong format, expected ':' separator", header );
            }

            header = bufferedReader.readLine();
        }
    }

    private static void debugParsedRequestLine( String line )
    {
        if ( logger.isDebugEnabled() )
        {
            logger.debug( "< {}", line );
        }
    }

    /**
     * Parses the request body
     *
     * @throws RequestParseException if any syntax error occurs
     * @throws IOException if any transport error occurs
     */
    private void parseBody()
        throws RequestParseException, IOException
    {
        if ( logger.isDebugEnabled() )
        {
            logger.debug( "Processing request body" );
        }

        if ( POST == request.getMethod() || PUT == request.getMethod() )
        {
            request.setContentBody( requestChannel );

            if ( request.getHeaders().contains( CONTENT_TYPE )
                        && request.getHeaders().getFirstValue( CONTENT_TYPE ).contains( "application/x-www-form-urlencoded" ) )
            {
                if ( logger.isDebugEnabled() )
                {
                    logger.debug( "Processing application/x-www-form-urlencoded request body" );
                }

                // TODO request.readRequestBody(  );
            }
        }
    }

    /**
     * This method has bee extracted from
     * <a href="https://github.com/sonatype/async-http-client/blob/master/src/main/java/com/ning/http/util/AsyncHttpProviderUtils.java#L505">AsyncHttpProvider</a>
     *
     * @param value
     * @return
     */
    private static Cookie parseCookie( String value )
    {
        String[] fields = value.split( ";\\s*" );
        String[] cookie = fields[0].split( "=" );
        String cookieName = cookie[0];
        String cookieValue = ( cookie.length == 1 ) ? null : cookie[1];

        int maxAge = -1;
        String path = null;
        String domain = null;
        boolean secure = false;

        boolean maxAgeSet = false;
        boolean expiresSet = false;

        for ( int j = 1; j < fields.length; j++ )
        {
            if ( "secure".equalsIgnoreCase( fields[j] ) )
            {
                secure = true;
            }
            else if ( fields[j].indexOf( '=' ) > 0 )
            {
                String[] f = fields[j].split( "=" );
                if ( f.length == 1 )
                    continue; // Add protection against null field values

                // favor 'max-age' field over 'expires'
                if ( !maxAgeSet && "max-age".equalsIgnoreCase( f[0] ) )
                {
                    try
                    {
                        maxAge = Integer.valueOf( removeQuote( f[1] ) );
                    }
                    catch ( NumberFormatException e1 )
                    {
                        // ignore failure to parse -> treat as session cookie
                        // invalidate a previously parsed expires-field
                        maxAge = -1;
                    }
                    maxAgeSet = true;
                }
                else if ( !maxAgeSet && !expiresSet && "expires".equalsIgnoreCase( f[0] ) )
                {
                    try
                    {
                        maxAge = convertExpireField( f[1] );
                    }
                    catch ( Exception e )
                    {
                        // original behavior, is this correct at all (expires field with max-age semantics)?
                        try
                        {
                            maxAge = Integer.valueOf( f[1] );
                        }
                        catch ( NumberFormatException e1 )
                        {
                            // ignore failure to parse -> treat as session cookie
                        }
                    }
                    expiresSet = true;
                }
                else if ( "domain".equalsIgnoreCase( f[0] ) )
                {
                    domain = f[1];
                }
                else if ( "path".equalsIgnoreCase( f[0] ) )
                {
                    path = f[1];
                }
            }
        }

        if ( maxAge < -1 )
        {
            maxAge = -1;
        }

        return new CookieBuilder()
                   .setDomain( domain )
                   .setName( cookieName )
                   .setValue( cookieValue )
                   .setPath( path )
                   .setMaxAge( maxAge )
                   .setSecure( secure )
                   .build();
    }

    /**
     * This method has bee extracted from
     * <a href="https://github.com/sonatype/async-http-client/blob/master/src/main/java/com/ning/http/util/AsyncHttpProviderUtils.java#L559">AsyncHttpProvider</a>
     *
     * @param timestring
     * @return
     * @throws Exception
     */
    private static int convertExpireField( String timestring )
        throws Exception
    {
        Exception exception = null;
        for ( SimpleDateFormat sdf : simpleDateFormat.get() )
        {
            try
            {
                long expire = sdf.parse( removeQuote( timestring.trim() ) ).getTime();
                return (int) ( ( expire - System.currentTimeMillis() ) / 1000 );
            }
            catch ( ParseException e )
            {
                exception = e;
            }
            catch ( NumberFormatException e )
            {
                exception = e;
            }
        }

        throw exception;
    }

    /**
     * This method has bee extracted from
     * <a href="https://github.com/sonatype/async-http-client/blob/master/src/main/java/com/ning/http/util/AsyncHttpProviderUtils.java#L575">AsyncHttpProvider</a>
     *
     * @param s
     * @return
     * @throws Exception
     */
    private static String removeQuote( String s )
    {
        if ( s.startsWith( "\"" ) )
        {
            s = s.substring( 1 );
        }

        if ( s.endsWith( "\"" ) )
        {
            s = s.substring( 0, s.length() - 1 );
        }
        return s;
    }

    private static abstract class ParameterParser
    {

        public final void parse( String input )
            throws IOException
        {
            StringTokenizer queryTokenizer = new StringTokenizer( input, "&" );
            while ( queryTokenizer.hasMoreTokens() )
            {
                String parameter = queryTokenizer.nextToken();

                StringTokenizer parameterTokenizer = new StringTokenizer( parameter, "=" );
                String parameterName = decode( parameterTokenizer.nextToken(), UTF_8.displayName() );
                String parameterValue = decode( parameterTokenizer.nextToken(), UTF_8.displayName() );

                onParameterFound( parameterName, parameterValue );
            }
        }

        protected abstract void onParameterFound( String parameterName, String parameterValue );

    }

}
