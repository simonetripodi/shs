package org.nnsoft.shs.http;

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

import java.util.Collection;

import org.nnsoft.shs.collections.MultiValued;
import org.nnsoft.shs.io.ResponseBodyWriter;

public interface Response
{

    public enum Status
    {

        // Informational 1xx
        CONTINUE( 100, "Continue" ),
        SWITCHING_PROTOCOLS( 101, "Switching Protocols" ),
        // Successful 2xx
        OK( 200, "OK" ),
        CREATED( 201, "Created" ),
        ACCEPTED( 202, "Accepted" ),
        NON_AUTHORATIVE_INORMATION( 203, "Non-Authoritative Information" ),
        NO_CONTENT( 204, "No Content" ),
        RESET_CONTENT( 205, "Reset Content" ),
        PARTIAL_CONTENT( 206, "Partial Content" ),
        // Redirection 3xx
        MUTLIPLE_CHOICES( 300, "Multiple Choices" ),
        MOVED_PERMANENTLY( 301, "Moved Permanently" ),
        FOUND( 302, "Found" ),
        SEE_OTHER( 303, "See Other" ),
        NOT_MODIFIED( 304, "Not Modified" ),
        USE_PROXY( 305, "Use Proxy" ),
        TEMPORARY_REDIRECT( 307, "Temporary Redirect" ),
        // Client Error 4xx
        BAD_REQUEST( 400, "Bad Request" ),
        UNAUTHORIZED( 401, "Unauthorized" ),
        PAYMENT_REQUIRED( 402, "Payment Required" ),
        FORBIDDEN( 403, "Forbidden" ),
        NOT_FOUND( 404, "Not Found" ),
        METHOD_NOT_ALLOWED( 405, "Method Not Allowed" ),
        NOT_ACCEPTABLE( 406, "Not Acceptable" ),
        PROXY_AUTHENTICATION_REQUIRED( 407, "Proxy Authentication Required" ),
        REQUEST_TIMEOUT( 408, "Request Timeout" ),
        CONFLICT( 409, "Conflict" ),
        GONE( 410, "Gone" ),
        LENGTH_REQUIRED( 411, "Length Required" ),
        PRECONDITION_FAILED( 412, "Precondition Failed" ),
        REQUEST_ENTITY_TOO_LARGE( 413, "Request Entity Too Large" ),
        REQUEST_URI_TOO_LONG( 414, "Request-URI Too Long" ),
        UNSUPPORTED_MEDIA_TYPE( 415, "Unsupported Media Type" ),
        REQUESTED_RANGE_NOT_SATISFIABLE( 416, "Requested Range Not Satisfiable" ),
        EXPECTATION_FAILED( 417, "Expectation Failed" ),
        // Server Error 5xx
        INTERNAL_SERVER_ERROR( 500, "Internal Server Error" ),
        NOT_IMPLEMENTED( 501, "Not Implemented" ),
        BAD_GATEWAY( 502, "Bad Gateway" ),
        SERVICE_UNAVAILABLE( 503, "Service Unavailable" ),
        GATEWAY_TIMEOUT( 504, "Gateway Timeout" ),
        HTTP_VERSION_NOT_SUPPORTED( 505, "HTTP Version Not Supported" );

        private final int statusCode;

        private final String statusText;

        Status( int statusCode, String statusText )
        {
            this.statusCode = statusCode;
            this.statusText = statusText;
        }

        public int getStatusCode()
        {
            return statusCode;
        }

        public String getStatusText()
        {
            return statusText;
        }

    }

    Status getStatus();

    void setStatus( Status status );

    String getProtocolName();

    void setProtocolName( String protocolName );

    String getProtocolVersion();

    void setProtocolVersion( String protocolVersion );

    void addHeader( String name, String value );

    MultiValued<String, String> getHeaders();

    /**
     * Checks if the GZip compression is supported.
     *
     * @return true, if the GZip compression is supported, false otherwise
     */
    boolean isGZipCompressionEnabled();

    void enableGZipCompression( boolean gzipCompressionEnabled );

    void addCookie( Cookie cookie );

    Collection<Cookie> getCookies();

    ResponseBodyWriter getBodyWriter();

    void setBody( ResponseBodyWriter bodyWriter );

}
