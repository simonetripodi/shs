package org.nnsoft.shs.core.http.parse;

import org.nnsoft.shs.core.http.RequestParseException;

public interface ParserTrigger
{

    void onToken( ParserStatus status, String token, MutableRequest request )
        throws RequestParseException;

}
