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

import java.util.Set;

/**
 * In memory representation of HTTP cookie.
 */
public interface Cookie
{

    /**
     * The cookie domain.
     *
     * @return the cookie domain
     */
    String getDomain();

    /**
     * The cookie name.
     *
     * @return the cookie name
     */
    String getName();

    /**
     * The cookie value.
     *
     * @return the cookie value.
     */
    String getValue();

    /**
     * The cookie path.
     *
     * @return the cookie path.
     */
    String getPath();

    /**
     * The cookie max age.
     *
     * @return the cookie max age.
     */
    int getMaxAge();

    /**
     * Flag to mark the cookie is secure.
     *
     * @return true, if the cookie is secure, false otherwise.
     */
    boolean isSecure();

    /**
     * The cookie ports.
     *
     * @return the cookie ports.
     */
    Set<Integer> getPorts();

}
