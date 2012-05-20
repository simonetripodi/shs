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

import static java.lang.Long.parseLong;
import static org.nnsoft.shs.core.http.parse.ParserStatus.HEADER_COOKIE_NAME;
import static org.nnsoft.shs.core.http.parse.ParserStatus.HEADER_NAME;
import static org.nnsoft.shs.core.http.parse.ParserStatus.HEADER_USER_AGENT_VALUE;
import static org.nnsoft.shs.core.http.parse.ParserStatus.HEADER_VALUE;
import static org.nnsoft.shs.http.Headers.CONTENT_LENGTH;
import static org.nnsoft.shs.http.Headers.COOKIE;
import static org.nnsoft.shs.http.Headers.USER_AGENT;

import org.nnsoft.shs.core.http.RequestParseException;

final class HeaderParserTrigger
    implements ParserTrigger
{

    private String headerNamePtr;

    @Override
    public ParserStatus onToken( ParserStatus status, String token, MutableRequest request )
        throws RequestParseException
    {
        if ( HEADER_NAME == status )
        {
            headerNamePtr = token;

            if ( USER_AGENT.equals( headerNamePtr ) )
            {
                return HEADER_USER_AGENT_VALUE;
            }
            else if ( COOKIE.equals( headerNamePtr ) )
            {
                return HEADER_COOKIE_NAME;
            }
        }
        else
        {
            request.addHeader( headerNamePtr, token );

            if ( CONTENT_LENGTH.equals( headerNamePtr ) )
            {
                try
                {
                    request.setContentLength( parseLong( token ) );
                }
                catch ( NumberFormatException e )
                {
                    throw new RequestParseException( "{} header value {} is not a numeric format",
                                                     CONTENT_LENGTH, token );
                }
            }

            if ( HEADER_USER_AGENT_VALUE == status )
            {
                return HEADER_NAME;
            }
        }

        return HEADER_VALUE;
    }

}
