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

/**
 * HTTP Response representation.
 */
public interface Response
{

    /**
     * HTTP response status representation.
     */
    public enum Status
    {

        // Informational 1xx
        /**
         * The {@code Continue} status.
         */
        CONTINUE( 100, "Continue" ),
        /**
         * The {@code Switching Protocols} status.
         */
        SWITCHING_PROTOCOLS( 101, "Switching Protocols" ),
        // Successful 2xx
        /**
         * The {@code OK} status.
         */
        OK( 200, "OK" ),
        /**
         * The {@code Created} status.
         */
        CREATED( 201, "Created" ),
        /**
         * The {@code Accepted} status.
         */
        ACCEPTED( 202, "Accepted" ),
        /**
         * The {@code Non-Authoritative Information} status.
         */
        NON_AUTHORATIVE_INORMATION( 203, "Non-Authoritative Information" ),
        /**
         * The {@code No Content} status.
         */
        NO_CONTENT( 204, "No Content" ),
        /**
         * The {@code Reset Content} status.
         */
        RESET_CONTENT( 205, "Reset Content" ),
        /**
         * The {@code Partial Content} status.
         */
        PARTIAL_CONTENT( 206, "Partial Content" ),
        // Redirection 3xx
        /**
         * The {@code Multiple Choices} status.
         */
        MUTLIPLE_CHOICES( 300, "Multiple Choices" ),
        /**
         * The {@code Moved Permanently} status.
         */
        MOVED_PERMANENTLY( 301, "Moved Permanently" ),
        /**
         * The {@code Found} status.
         */
        FOUND( 302, "Found" ),
        /**
         * The {@code See Other} status.
         */
        SEE_OTHER( 303, "See Other" ),
        /**
         * The {@code Not Modified} status.
         */
        NOT_MODIFIED( 304, "Not Modified" ),
        /**
         * The {@code Use Proxy} status.
         */
        USE_PROXY( 305, "Use Proxy" ),
        /**
         * The {@code Temporary Redirect} status.
         */
        TEMPORARY_REDIRECT( 307, "Temporary Redirect" ),
        // Client Error 4xx
        /**
         * The {@code Bad Request} status.
         */
        BAD_REQUEST( 400, "Bad Request" ),
        /**
         * The {@code Unauthorized} status.
         */
        UNAUTHORIZED( 401, "Unauthorized" ),
        /**
         * The {@code Payment Required} status.
         */
        PAYMENT_REQUIRED( 402, "Payment Required" ),
        /**
         * The {@code Forbidden} status.
         */
        FORBIDDEN( 403, "Forbidden" ),
        /**
         * The {@code Not Found} status.
         */
        NOT_FOUND( 404, "Not Found" ),
        /**
         * The {@code Method Not Allowed} status.
         */
        METHOD_NOT_ALLOWED( 405, "Method Not Allowed" ),
        /**
         * The {@code Not Acceptable} status.
         */
        NOT_ACCEPTABLE( 406, "Not Acceptable" ),
        /**
         * The {@code Proxy Authentication Required} status.
         */
        PROXY_AUTHENTICATION_REQUIRED( 407, "Proxy Authentication Required" ),
        /**
         * The {@code Request Timeout} status.
         */
        REQUEST_TIMEOUT( 408, "Request Timeout" ),
        /**
         * The {@code Conflict} status.
         */
        CONFLICT( 409, "Conflict" ),
        /**
         * The {@code Gone} status.
         */
        GONE( 410, "Gone" ),
        /**
         * The {@code Length Required} status.
         */
        LENGTH_REQUIRED( 411, "Length Required" ),
        /**
         * The {@code Precondition Failed} status.
         */
        PRECONDITION_FAILED( 412, "Precondition Failed" ),
        /**
         * The {@code Request Entity Too Large} status.
         */
        REQUEST_ENTITY_TOO_LARGE( 413, "Request Entity Too Large" ),
        /**
         * The {@code Request-URI Too Long} status.
         */
        REQUEST_URI_TOO_LONG( 414, "Request-URI Too Long" ),
        /**
         * The {@code Unsupported Media Type} status.
         */
        UNSUPPORTED_MEDIA_TYPE( 415, "Unsupported Media Type" ),
        /**
         * The {@code Requested Range Not Satisfiable} status.
         */
        REQUESTED_RANGE_NOT_SATISFIABLE( 416, "Requested Range Not Satisfiable" ),
        /**
         * The {@code Expectation Failed} status.
         */
        EXPECTATION_FAILED( 417, "Expectation Failed" ),
        // Server Error 5xx
        /**
         * The {@code Internal Server Error} status.
         */
        INTERNAL_SERVER_ERROR( 500, "Internal Server Error" ),
        /**
         * The {@code Not Implemented} status.
         */
        NOT_IMPLEMENTED( 501, "Not Implemented" ),
        /**
         * The {@code Bad Gateway} status.
         */
        BAD_GATEWAY( 502, "Bad Gateway" ),
        /**
         * The {@code Service Unavailable} status.
         */
        SERVICE_UNAVAILABLE( 503, "Service Unavailable" ),
        /**
         * The {@code Gateway Timeout} status.
         */
        GATEWAY_TIMEOUT( 504, "Gateway Timeout" ),
        /**
         * The {@code HTTP Version Not Supported} status.
         */
        HTTP_VERSION_NOT_SUPPORTED( 505, "HTTP Version Not Supported" );

        /**
         * The numerical HTTP status code.
         */
        private final int statusCode;

        /**
         * The string representation of HTTP status text.
         */
        private final String statusText;

        /**
         * Builds a new HTTP status.
         *
         * @param statusCode the numerical HTTP status code.
         * @param statusText the string representation of HTTP status text.
         */
        Status( int statusCode, String statusText )
        {
            this.statusCode = statusCode;
            this.statusText = statusText;
        }

        /**
         * Returns the numerical HTTP status code.
         *
         * @return the numerical HTTP status code.
         */
        public int getStatusCode()
        {
            return statusCode;
        }

        /**
         * Returns the string representation of HTTP status text.
         *
         * @return the string representation of HTTP status text.
         */
        public String getStatusText()
        {
            return statusText;
        }

    }

    /**
     * Returns the HTTP Reponse status.
     *
     * @return the HTTP Reponse status.
     */
    Status getStatus();

    /**
     * Sets the HTTP Reponse status.
     *
     * @param status the HTTP Reponse status.
     */
    void setStatus( Status status );

    /**
     * Returns the protocol name (typically HTTP)
     *
     * @return the protocol name (typically HTTP)
     */
    String getProtocolName();

    /**
     * Returns the protocol name (typically HTTP)
     *
     * @param protocolName the protocol name (typically HTTP)
     */
    void setProtocolName( String protocolName );

    /**
     * Returns the protocol version.
     *
     * @return the protocol version.
     */
    String getProtocolVersion();

    /**
     * Sets the protocol version.
     *
     * @param protocolVersion the protocol version.
     */
    void setProtocolVersion( String protocolVersion );

    /**
     * Allows add a response HTTP Header
     *
     * @param name the HTTP Header name.
     * @param value the HTTP Header value.
     */
    void addHeader( String name, String value );

    /**
     * Returns the HTTP headers.
     *
     * @return the HTTP headers.
     */
    MultiValued<String, String> getHeaders();

    /**
     * Allows adding a cookie has to be sent back to the client.
     *
     * @param cookie the cookie has to be sent back to the client.
     */
    void addCookie( Cookie cookie );

    /**
     * Returns the list of cookies have to be sent back to the client.
     *
     * @return the list of cookies have to be sent back to the client.
     */
    Collection<Cookie> getCookies();

    /**
     * Gets the body writer instance will be used to stream the body response to the client.
     *
     * @return the body writer instance will be used to stream the body response to the client.
     */
    ResponseBodyWriter getBodyWriter();

    /**
     * Sets the body writer instance will be used to stream the body response to the client.
     *
     * @param bodyWriter the body writer instance will be used to stream the body response to the client.
     */
    void setBody( ResponseBodyWriter bodyWriter );

}
