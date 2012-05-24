package org.nnsoft.shs.core.http.parse;

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
import static org.nnsoft.shs.core.http.parse.ParserStatus.BODY_CONSUMING;
import static org.nnsoft.shs.core.http.parse.ParserStatus.COMPLETE;
import static org.nnsoft.shs.core.http.parse.ParserStatus.COOKIE_NAME;
import static org.nnsoft.shs.core.http.parse.ParserStatus.COOKIE_VALUE;
import static org.nnsoft.shs.core.http.parse.ParserStatus.HEADER_NAME;
import static org.nnsoft.shs.core.http.parse.ParserStatus.HEADER_USER_AGENT_VALUE;
import static org.nnsoft.shs.core.http.parse.ParserStatus.HEADER_VALUE;
import static org.nnsoft.shs.core.http.parse.ParserStatus.METHOD;
import static org.nnsoft.shs.core.http.parse.ParserStatus.PARAM_NAME;
import static org.nnsoft.shs.core.http.parse.ParserStatus.PARAM_VALUE;
import static org.nnsoft.shs.core.http.parse.ParserStatus.PATH;
import static org.nnsoft.shs.core.http.parse.ParserStatus.PROTOCOL_NAME;
import static org.nnsoft.shs.core.http.parse.ParserStatus.PROTOCOL_VERSION;
import static org.nnsoft.shs.core.http.parse.ParserStatus.QS_PARAM_NAME;
import static org.nnsoft.shs.core.http.parse.ParserStatus.QS_PARAM_VALUE;
import static org.nnsoft.shs.core.io.IOUtils.toUtf8CharBuffer;
import static org.nnsoft.shs.http.Headers.CONTENT_TYPE;
import static org.slf4j.LoggerFactory.getLogger;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.EnumMap;
import java.util.Map;

import org.nnsoft.shs.core.http.MutableRequest;
import org.nnsoft.shs.core.http.RequestParseException;
import org.nnsoft.shs.http.Request;
import org.slf4j.Logger;

/**
 * An LL(0) {@link Request} pull parser that incrementally rebuilds the HTTP Request.
 *
 * This class is not thread-safe!
 */
public final class RequestStreamingParser
{

    private static final Logger logger = getLogger( RequestStreamingParser.class );

    private static final char CARRIAGE_RETURN = '\r';

    private static final char NEW_LINE = '\n';

    private static final char TOKEN_SEPARATOR = ' ';

    private static final char HEADER_SEPARATOR = ';';

    private static final char QUERY_STRING_SEPARATOR = '?';

    private static final char PARAMETER_SEPARATOR = '&';

    private static final char KEY_VALUE_SEPARATOR = '=';

    private static final char PROTOCOL_VERSION_SEPARATOR = '/';

    private static final char HEADER_NAME_SEPARATOR = ':';

    private static final char HEADER_VALUES_SEPARATOR = ',';

    private static final String FORM_URLENCODED = "application/x-www-form-urlencoded";

    private final MutableRequest request = new MutableRequest();

    private final Map<ParserStatus, ParserTrigger> parserTriggers = new EnumMap<ParserStatus, ParserTrigger>( ParserStatus.class );

    private StringBuilder accumulator = new StringBuilder();

    private ParserStatus status = ParserStatus.METHOD;

    private long bodyConsumingCounter = -1; // -1 because the first will be triggered by \n

    private ByteBuffer requestBody;

    public RequestStreamingParser( String clientHost, String serverHost, int serverPort )
    {
        request.setClientHost( clientHost );
        request.setServerHost( serverHost );
        request.setServerPort( serverPort );

        registerTrigger( new MethodParserTrigger(), METHOD );
        registerTrigger( new PathParserTrigger(), PATH );
        registerTrigger( new ProtocolNameParserTrigger(), PROTOCOL_NAME );
        registerTrigger( new ProtocolVersionParserTrigger(), PROTOCOL_VERSION );
        registerTrigger( new QueryStringParametersParserTrigger(), QS_PARAM_NAME, QS_PARAM_VALUE );
        registerTrigger( new HeaderParserTrigger(), HEADER_NAME, HEADER_VALUE, HEADER_USER_AGENT_VALUE, COOKIE_VALUE );
        registerTrigger( new CookieParserTrigger(), COOKIE_NAME, COOKIE_VALUE );
        registerTrigger( new ParametersParserTrigger(), PARAM_NAME, PARAM_VALUE );
    }

    private void registerTrigger( ParserTrigger trigger, ParserStatus...parserStatuses )
    {
        for ( ParserStatus parserStatus : parserStatuses )
        {
            parserTriggers.put( parserStatus, trigger );
        }
    }

    public void onRequestPartRead( ByteBuffer messageBuffer )
        throws RequestParseException
    {
        if ( isRequestMessageComplete() )
        {
            return;
        }

        if ( BODY_CONSUMING == status )
        {
            consumeBody( messageBuffer );
        }
        else
        {
            CharBuffer charBuffer = toUtf8CharBuffer( messageBuffer );
            dance: while ( charBuffer.hasRemaining() )
            {
                char current = charBuffer.get();

                if ( logger.isDebugEnabled() )
                {
                    logger.debug( "{} consuming char: `{}'", status, current );
                }

                if ( isRequestMessageComplete() )
                {
                    break dance;
                }

                switch ( current )
                {
                    case CARRIAGE_RETURN:
                        break;

                    case TOKEN_SEPARATOR:
                        if ( !isConsumingToken() ) // trim initial spaces
                        {
                            break;
                        }

                        if ( HEADER_VALUE == status
                             || HEADER_USER_AGENT_VALUE == status
                             || COOKIE_VALUE == status )
                        {
                            append( current );
                        }
                        else
                        {
                            tokenFound();
                            if ( QS_PARAM_NAME == status )
                            {
                                forceSwitch( current, PROTOCOL_NAME );
                            }
                        }
                        break;

                    case PROTOCOL_VERSION_SEPARATOR:
                        if ( PROTOCOL_NAME == status )
                        {
                            tokenFound();
                        }
                        else
                        {
                            append( current );
                        }
                        break;

                    case KEY_VALUE_SEPARATOR:
                        if ( HEADER_VALUE == status || COOKIE_VALUE == status )
                        {
                            append( current );
                        }
                        else
                        {
                            tokenFound();
                        }
                        break;

                    case HEADER_NAME_SEPARATOR:
                        if ( HEADER_VALUE == status )
                        {
                            append( current );
                        }
                        else
                        {
                            tokenFound();
                        }
                        break;

                    case HEADER_VALUES_SEPARATOR:
                        if ( HEADER_USER_AGENT_VALUE == status || COOKIE_VALUE == status )
                        {
                            append( current );
                        }
                        else
                        {
                            tokenFound();
                        }
                        break;

                    case PARAMETER_SEPARATOR:
                        tokenFound();
                        break;

                    case QUERY_STRING_SEPARATOR:
                        tokenFound();
                        forceSwitch( current, QS_PARAM_NAME );
                        break;

                    case NEW_LINE:
                        if ( isConsumingToken() )
                        {
                            tokenFound();
                            if ( HEADER_VALUE == status
                                 || HEADER_USER_AGENT_VALUE == status
                                 || COOKIE_VALUE == status )
                            {
                                forceSwitch( current, HEADER_NAME );
                            }
                        }
                        else if ( request.getContentLength() > 0 )
                        {
                            if ( logger.isDebugEnabled() )
                            {
                                logger.debug( "Consuming request body..." );
                            }

                            if ( request.getHeaders().contains( CONTENT_TYPE )
                                 && request.getHeaders().getFirstValue( CONTENT_TYPE ).contains( FORM_URLENCODED ) )
                            {
                                forceSwitch( current, PARAM_NAME );
                            }
                            else
                            {
                                forceSwitch( current, BODY_CONSUMING );
                                requestBody = allocate( (int) request.getContentLength() );

                                messageBuffer.position( charBuffer.position() );

                                consumeBody( messageBuffer );
                                break dance;
                            }
                        }
                        else
                        {
                            status = COMPLETE;
                        }
                        break;

                    case HEADER_SEPARATOR:
                        if ( HEADER_VALUE == status || HEADER_USER_AGENT_VALUE == status )
                        {
                            append( current );
                        }
                        else
                        {
                            tokenFound();
                        }
                        break;

                    default:
                        append( current );
                        break;
                }

                if ( PARAM_NAME == status || PARAM_VALUE == status )
                {
                    bodyConsumingCounter++;

                    if ( PARAM_VALUE == status && bodyConsumingCounter == request.getContentLength() )
                    {
                        tokenFound();
                        status = COMPLETE;

                        if ( logger.isDebugEnabled() )
                        {
                            logger.debug( "Request body consumed" );
                        }
                    }
                }
            }
        }
    }

    private void forceSwitch( char trigger, ParserStatus newStatus )
    {
        if ( logger.isDebugEnabled() )
        {
            logger.debug( "{} trigger char: `{}' -> next status {}", new Object[] { status, trigger, newStatus } );
        }
        status = newStatus;
    }

    private boolean isConsumingToken()
    {
        return accumulator.length() > 0;
    }

    private void append( char current )
    {
        accumulator.append( current );
    }

    private void tokenFound()
        throws RequestParseException
    {
        String token = accumulator.toString();
        ParserStatus newStatus = parserTriggers.get( status ).onToken( status, token, request );

        if ( logger.isDebugEnabled() )
        {
            logger.debug( "{} consuming token: `{}' -> next status {}", new Object[] { status, token, newStatus } );
        }

        accumulator = new StringBuilder();
        status = newStatus;
    }

    private void consumeBody( ByteBuffer buffer )
    {
        while ( buffer.hasRemaining() && requestBody.hasRemaining() )
        {
            requestBody.put( buffer.get() );

            if ( logger.isDebugEnabled() )
            {
                logger.debug( "{} Consuming request body {}/{}",
                              new Object[] { status, requestBody.position(), requestBody.limit() } );
            }
        }

        if ( request.getContentLength() == requestBody.capacity() )
        {
            status = COMPLETE;
            request.setRequestBody( requestBody );
        }
    }

    public boolean isRequestMessageComplete()
    {
        return COMPLETE == status;
    }

    public Request getParsedRequest()
    {
        return request;
    }

}
