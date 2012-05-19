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

import static org.slf4j.LoggerFactory.getLogger;
import static org.nnsoft.shs.core.io.IOUtils.utf8Decode;
import static org.nnsoft.shs.core.http.parse.ParserStatus.*;
import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.Map;

import org.nnsoft.shs.core.http.RequestParseException;
import org.nnsoft.shs.http.Request;
import org.slf4j.Logger;

/**
 * A {@link Request} pull parser that incrementally rebuilds the HTTP Request.
 */
public final class RequestPullParser
{

    private static final Logger logger = getLogger( RequestPullParser.class );

    private static final char CARRIAGE_RETURN = '\r';

    private static final char NEW_LINE = '\n';

    private static final char TOKEN_SEPARATOR = ' ';

    private static final char HEADER_SEPARATOR = ';';

    private final MutableRequest request = new MutableRequest();

    private final Map<ParserStatus, ParserTrigger> parserTriggers = new EnumMap<ParserStatus, ParserTrigger>( ParserStatus.class );

    private StringBuilder accumulator = new StringBuilder();

    private ParserStatus status = ParserStatus.METHOD;

    private boolean messageComplete = false;

    public RequestPullParser()
    {
        parserTriggers.put( METHOD, new MethodParserTrigger() );
        parserTriggers.put( PATH, new PathParserTrigger() );
        parserTriggers.put( PROTOCOL_VERSION, new VersionParserTrigger() );
    }

    public void onRequestPartRead( ByteBuffer messageBuffer )
        throws RequestParseException
    {
        String message = utf8Decode( messageBuffer );
        dance: for ( char current : message.toCharArray() )
        {
            if ( messageComplete )
            {
                break dance;
            }

            switch ( current )
            {
                case CARRIAGE_RETURN:
                    break;

                case TOKEN_SEPARATOR:
                case NEW_LINE:
                    String token = getToken();

                    if ( token.isEmpty() )
                    {
                        // TODO check Content-Length header
                        messageComplete = true;
                        break;
                    }

                    if ( logger.isDebugEnabled() )
                    {
                        logger.debug( "New token consumed: {} ({})", token, status );
                    }

                    parserTriggers.get( status ).onToken( status, token, request );

                    status = status.getNext();
                    break;

                case HEADER_SEPARATOR:
                    break;

                default:
                    accumulator.append( current );
                    break;
            }
        }
    }

    private String getToken()
    {
        String token = accumulator.toString();
        accumulator = new StringBuilder();
        return token;
    }

    public boolean isMessageReceivedCompletely()
    {
        return messageComplete;
    }

    public Request getParsedRequest()
    {
        return request;
    }

}
