package org.nnsoft.shs.core.http.parse;

import static org.nnsoft.shs.http.Request.Method.valueOf;
import static org.nnsoft.shs.http.Request.Method.values;

import java.util.Arrays;

import org.nnsoft.shs.core.http.RequestParseException;

final class MethodParserTrigger
    implements ParserTrigger
{

    @Override
    public void onToken( ParserStatus status, String token, MutableRequest request )
        throws RequestParseException
    {
        try
        {
            request.setMethod( valueOf( token ) );
        }
        catch ( IllegalArgumentException e )
        {
            throw new RequestParseException( "Custom method '%s' is not supported, only %s supported",
                                             token, Arrays.toString( values() ) );
        }
    }

}
