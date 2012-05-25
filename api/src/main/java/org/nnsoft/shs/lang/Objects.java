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

/**
 * Simple Object-related utility methods.
 *
 * This class has been contributed to Apache Commons-Graph
 */
public final class Objects
{

    /**
     * Hidden constructor, this class cannot be instantiated directly.
     */
    private Objects()
    {
        // do nothing
    }

    /**
     * Computes a hashCode given the input objects.
     *
     * @param initialNonZeroOddNumber a non-zero, odd number used as the initial value.
     * @param multiplierNonZeroOddNumber a non-zero, odd number used as the multiplier.
     * @param objs the objects to compute hash code.
     * @return the computed hashCode.
     */
    public static int hash( int initialNonZeroOddNumber, int multiplierNonZeroOddNumber, Object...objs )
    {
        int result = initialNonZeroOddNumber;
        for ( Object obj : objs )
        {
            result = multiplierNonZeroOddNumber * result + ( obj != null ? obj.hashCode() : 0 );
        }
        return result;
    }

    /**
     * Verifies input objects are equal.
     *
     * @param <O> the object type under comparison
     * @param o1 the first argument to compare
     * @param o2 the second argument to compare
     * @return true, if the input arguments are equal, false otherwise.
     */
    public static <O> boolean eq( O o1, O o2 )
    {
        return o1 != null ? o1.equals( o2 ) : o2 == null;
    }

}
