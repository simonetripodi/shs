package org.nnsoft.shs.lang;

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

import static java.lang.String.format;

/**
 * Code inspired from <a href="http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/base/Preconditions.html">Google Guava</a>
 */
public final class Preconditions
{

    /**
     * Hidden constructor, this class must not be instantiated.
     */
    private Preconditions()
    {
        // do nothing
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the
     * calling method.
     *
     * @param expression a boolean expression
     * @param errorMessageTemplate a template for the exception message should the
     *     check fail. The message is formed by replacing each {@code %s}
     *     placeholder in the template with an argument. These are matched by
     *     position - the first {@code %s} gets {@code errorMessageArgs[0]}, etc.
     *     Unmatched arguments will be appended to the formatted message in square
     *     braces. Unmatched placeholders will be left as-is.
     * @param errorMessageArgs the arguments to be substituted into the message
     *     template. Arguments are converted to strings using
     *     {@link String#valueOf(Object)}.
     * @throws IllegalArgumentException if {@code expression} is false
     * @throws NullPointerException if the check fails and either {@code
     *     errorMessageTemplate} or {@code errorMessageArgs} is null (don't let
     *     this happen)
     */
    public static void checkArgument( boolean expression, String errorMessageTemplate, Object... errorMessageArgs )
    {
        if ( !expression )
        {
            throw new IllegalArgumentException( format( errorMessageTemplate, errorMessageArgs ) );
        }
    }

    /**
     * Ensures the truth of an expression involving the state of the calling
     * instance, but not involving any parameters to the calling method.
     *
     * @param expression a boolean expression
     * @param errorMessageTemplate a template for the exception message should the
     *     check fail. The message is formed by replacing each {@code %s}
     *     placeholder in the template with an argument. These are matched by
     *     position - the first {@code %s} gets {@code errorMessageArgs[0]}, etc.
     *     Unmatched arguments will be appended to the formatted message in square
     *     braces. Unmatched placeholders will be left as-is.
     * @param errorMessageArgs the arguments to be substituted into the message template.
     * @throws IllegalStateException if {@code expression} is false
     * @throws NullPointerException if the check fails and either {@code
     *     errorMessageTemplate} or {@code errorMessageArgs} is null (don't let
     *     this happen)
     */
    public static void checkState( boolean expression, String errorMessageTemplate, Object... errorMessageArgs )
    {
        if ( !expression )
        {
            throw new IllegalStateException( format( errorMessageTemplate, errorMessageArgs ) );
        }
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling
     * method is not null.
     *
     * @param reference an object reference
     * @param errorMessageTemplate a template for the exception message should the
     *     check fail. The message is formed by replacing each {@code %s}
     *     placeholder in the template with an argument. These are matched by
     *     position - the first {@code %s} gets {@code errorMessageArgs[0]}, etc.
     *     Unmatched arguments will be appended to the formatted message in square
     *     braces. Unmatched placeholders will be left as-is.
     * @param errorMessageArgs the arguments to be substituted into the message
     *     template. Arguments are converted to strings using
     *     {@link String#valueOf(Object)}.
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull( T reference, String errorMessageTemplate, Object... errorMessageArgs )
    {
        if ( reference == null )
        {
            // If either of these parameters is null, the right thing happens anyway
            throw new NullPointerException( format( errorMessageTemplate, errorMessageArgs ) );
        }
        return reference;
    }

}
