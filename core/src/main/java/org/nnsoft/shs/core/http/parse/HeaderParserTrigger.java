package org.nnsoft.shs.core.http.parse;

import static org.nnsoft.shs.http.Headers.USER_AGENT;
import static org.nnsoft.shs.http.Headers.CONTENT_LENGTH;
import static org.nnsoft.shs.http.Headers.COOKIE;
import org.nnsoft.shs.core.http.RequestParseException;

import static org.nnsoft.shs.core.http.parse.ParserStatus.*;

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
            else if ( CONTENT_LENGTH.equals( headerNamePtr ) )
            {
                return HEADER_CONTENT_LENGTH_VALUE;
            }
            else if ( COOKIE.equals( headerNamePtr ) )
            {
                return HEADER_COOKIE_VALUE;
            }
        }
        else
        {
            request.addHeader( headerNamePtr, token );
        }
        return HEADER_VALUE;
    }

}
