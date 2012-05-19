package org.nnsoft.shs.core.http.parse;

/**
 * Represents the different statuses of the parse steps.
 */
public enum ParserStatus
{

    HEADER_VALUE( null ),
    HEADER_NAME( HEADER_VALUE ),
    PROTOCOL_VERSION( HEADER_NAME ),
    PATH( PROTOCOL_VERSION ),
    METHOD( PATH );

    private final ParserStatus next;

    ParserStatus( ParserStatus next )
    {
        this.next = next;
    }

    public ParserStatus getNext()
    {
        return next;
    }

}
