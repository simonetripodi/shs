package org.nnsoft.shs.core.http.parse;

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
