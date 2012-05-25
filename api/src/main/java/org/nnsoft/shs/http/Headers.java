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

public interface Headers
{

    /**
     * The {@code Accept} HTTP Header constant.
     */
    static final String ACCEPT = "Accept";

    /**
     * The {@code Accept-Charset} HTTP Header constant.
     */
    static final String ACCEPT_CHARSET = "Accept-Charset";

    /**
     * The {@code Accept-Encoding} HTTP Header constant.
     */
    static final String ACCEPT_ENCODING = "Accept-Encoding";

    /**
     * The {@code Accept-Language} HTTP Header constant.
     */
    static final String ACCEPT_LANGUAGE = "Accept-Language";

    /**
     * The {@code Accept-Ranges} HTTP Header constant.
     */
    static final String ACCEPT_RANGES = "Accept-Ranges";

    /**
     * The {@code Age} HTTP Header constant.
     */
    static final String AGE = "Age";

    /**
     * The {@code Allow} HTTP Header constant.
     */
    static final String ALLOW = "Allow";

    /**
     * The {@code Authorization} HTTP Header constant.
     */
    static final String AUTHORIZATION = "Authorization";

    /**
     * The {@code Cache-Control} HTTP Header constant.
     */
    static final String CACHE_CONTROL = "Cache-Control";

    /**
     * The {@code Connection} HTTP Header constant.
     */
    static final String CONNECTION = "Connection";

    /**
     * The {@code Content-Encoding} HTTP Header constant.
     */
    static final String CONTENT_ENCODING = "Content-Encoding";

    /**
     * The {@code Content-Language} HTTP Header constant.
     */
    static final String CONTENT_LANGUAGE = "Content-Language";

    /**
     * The {@code Content-Length} HTTP Header constant.
     */
    static final String CONTENT_LENGTH = "Content-Length";

    /**
     * The {@code Content-Location} HTTP Header constant.
     */
    static final String CONTENT_LOCATION = "Content-Location";

    /**
     * The {@code Content-MD5} HTTP Header constant.
     */
    static final String CONTENT_MD5 = "Content-MD5";

    /**
     * The {@code Content-Range} HTTP Header constant.
     */
    static final String CONTENT_RANGE = "Content-Range";

    /**
     * The {@code Content-Type} HTTP Header constant.
     */
    static final String CONTENT_TYPE = "Content-Type";

    /**
     * The {@code Cookie} HTTP Header constant.
     */
    static final String COOKIE = "Cookie";

    /**
     * The {@code Date} HTTP Header constant.
     */
    static final String DATE = "Date";

    /**
     * The {@code ETag} HTTP Header constant.
     */
    static final String ETAG = "ETag";

    /**
     * The {@code Expect} HTTP Header constant.
     */
    static final String EXPECT = "Expect";

    /**
     * The {@code Expires} HTTP Header constant.
     */
    static final String EXPIRES = "Expires";

    /**
     * The {@code From} HTTP Header constant.
     */
    static final String FROM = "From";

    /**
     * The {@code Host} HTTP Header constant.
     */
    static final String HOST = "Host";

    /**
     * The {@code If-Match} HTTP Header constant.
     */
    static final String IF_MATCH = "If-Match";

    /**
     * The {@code If-Modified-Since} HTTP Header constant.
     */
    static final String IF_MODIFIED_SINCE = "If-Modified-Since";

    /**
     * The {@code If-None-Match} HTTP Header constant.
     */
    static final String IF_NONE_MATCH = "If-None-Match";

    /**
     * The {@code If-Range} HTTP Header constant.
     */
    static final String IF_RANGE = "If-Range";

    /**
     * The {@code If-Unmodified-Since} HTTP Header constant.
     */
    static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";

    /**
     * The {@code Keep-Alive} HTTP Header constant.
     */
    static final String KEEP_ALIVE = "Keep-Alive";

    /**
     * The {@code Last-Modified} HTTP Header constant.
     */
    static final String LAST_MODIFIED = "Last-Modified";

    /**
     * The {@code Location} HTTP Header constant.
     */
    static final String LOCATION = "Location";

    /**
     * The {@code Max-Forwards} HTTP Header constant.
     */
    static final String MAX_FORWARDS = "Max-Forwards";

    /**
     * The {@code Pragma} HTTP Header constant.
     */
    static final String PRAGMA = "Pragma";

    /**
     * The {@code Proxy-Authenticate} HTTP Header constant.
     */
    static final String PROXY_AUTHENTICATE = "Proxy-Authenticate";

    /**
     * The {@code Proxy-Authorization} HTTP Header constant.
     */
    static final String PROXY_AUTHORIZATION = "Proxy-Authorization";

    /**
     * The {@code Range} HTTP Header constant.
     */
    static final String RANGE = "Range";

    /**
     * The {@code Referer} HTTP Header constant.
     */
    static final String REFERER = "Referer";

    /**
     * The {@code Retry-After} HTTP Header constant.
     */
    static final String RETRY_AFTER = "Retry-After";

    /**
     * The {@code Server} HTTP Header constant.
     */
    static final String SERVER = "Server";

    /**
     * The {@code Set-Cookie} HTTP Header constant.
     */
    static final String SET_COOKIE = "Set-Cookie";

    /**
     * The {@code TE} HTTP Header constant.
     */
    static final String TE = "TE";

    /**
     * The {@code Trailer} HTTP Header constant.
     */
    static final String TRAILER = "Trailer";

    /**
     * The {@code Transfer-Encoding} HTTP Header constant.
     */
    static final String TRANSFER_ENCODING = "Transfer-Encoding";

    /**
     * The {@code Upgrade} HTTP Header constant.
     */
    static final String UPGRADE = "Upgrade";

    /**
     * The {@code User-Agent} HTTP Header constant.
     */
    static final String USER_AGENT = "User-Agent";

    /**
     * The {@code Vary} HTTP Header constant.
     */
    static final String VARY = "Vary";

    /**
     * The {@code Via} HTTP Header constant.
     */
    static final String VIA = "Via";

    /**
     * The {@code Warning} HTTP Header constant.
     */
    static final String WARNING = "Warning";

    /**
     * The {@code WWW-Authenticate} HTTP Header constant.
     */
    static final String WWW_AUTHENTICATE = "WWW-Authenticate";

}
