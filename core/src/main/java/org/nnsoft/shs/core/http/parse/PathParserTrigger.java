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

import static org.nnsoft.shs.core.http.parse.ParserStatus.PROTOCOL_VERSION;
import static org.nnsoft.shs.core.io.IOUtils.utf8URLDecode;
import static org.nnsoft.shs.http.Request.Method.GET;

import java.util.StringTokenizer;

import org.nnsoft.shs.core.http.RequestParseException;

final class PathParserTrigger
    implements ParserTrigger
{

    private static final char QUERY_STRING_SEPARATOR = '?';

    private static final String PARAMETER_SEPARATOR = "&";

    private static final String KEY_VALUE_SEPARATOR = "=";

    @Override
    public ParserStatus onToken( ParserStatus status, String token, MutableRequest request )
        throws RequestParseException
    {
        String path = token;

        if ( GET == request.getMethod() )
        {
            int queryStringSeparatorIndex = token.indexOf( QUERY_STRING_SEPARATOR );

            if ( queryStringSeparatorIndex != -1 )
            {
                path = token.substring( 0, queryStringSeparatorIndex++ );
                String queryString = token.substring( queryStringSeparatorIndex );

                StringTokenizer queryTokenizer = new StringTokenizer( queryString, PARAMETER_SEPARATOR );
                while ( queryTokenizer.hasMoreTokens() )
                {
                    String parameter = queryTokenizer.nextToken();

                    StringTokenizer parameterTokenizer = new StringTokenizer( parameter, KEY_VALUE_SEPARATOR );
                    String parameterName = utf8URLDecode( parameterTokenizer.nextToken() );
                    String parameterValue = utf8URLDecode( parameterTokenizer.nextToken() );

                    request.addQueryStringParameter( parameterName, parameterValue );
                }
            }
        }

        request.setPath( utf8URLDecode( path ) );
        return PROTOCOL_VERSION;
    }

}
