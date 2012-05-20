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

import static org.nnsoft.shs.core.http.parse.ParserStatus.PATH;
import static org.nnsoft.shs.http.Request.Method.valueOf;
import static org.nnsoft.shs.http.Request.Method.values;

import java.util.Arrays;

import org.nnsoft.shs.core.http.MutableRequest;
import org.nnsoft.shs.core.http.RequestParseException;

/**
 * Trigger for the HTTP request method.
 */
final class MethodParserTrigger
    implements ParserTrigger
{

    /**
     * {@inheritDoc}
     */
    @Override
    public ParserStatus onToken( ParserStatus status, String token, MutableRequest request )
        throws RequestParseException
    {
        try
        {
            request.setMethod( valueOf( token ) );
            return PATH;
        }
        catch ( IllegalArgumentException e )
        {
            throw new RequestParseException( "Custom method '%s' is not supported, only %s supported",
                                             token, Arrays.toString( values() ) );
        }
    }

}
