package org.nnsoft.shs.demo;

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

import static javax.xml.bind.JAXBContext.newInstance;
import static org.nnsoft.shs.http.Response.Status.INTERNAL_SERVER_ERROR;

import java.io.IOException;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.nnsoft.shs.http.BaseRequestHandler;
import org.nnsoft.shs.http.Request;
import org.nnsoft.shs.http.Response;

/**
 * A {@link BaseRequestHandler} that serializes XML using JAXB.
 */
final class JaxbHandler
    extends BaseRequestHandler
{

    private final JAXBContext jaxbContext;

    public JaxbHandler()
    {
        try
        {
            jaxbContext = newInstance( Demo.class );
        }
        catch ( JAXBException e )
        {
            // it shouldn't happen in a demo
            throw new RuntimeException( e );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void get( Request request, Response response )
        throws IOException
    {
        Demo jaxbElement = new Demo();
        jaxbElement.setRequestPath( request.getPath() );
        jaxbElement.setDate( new Date() );
        jaxbElement.setSessionId( request.getSession().getId().toString() );

        try
        {
            Marshaller marshaller = jaxbContext.createMarshaller();
            response.setBody( new JaxbResponseBodyWriter( marshaller, jaxbElement ) );
        }
        catch ( JAXBException e )
        {
            response.setStatus( INTERNAL_SERVER_ERROR );
        }
    }

}
