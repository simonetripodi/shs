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

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;
import static org.nnsoft.shs.lang.Objects.eq;
import static org.nnsoft.shs.lang.Objects.hash;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

final class SimpleMultiValued
    implements Headers, QueryStringParameters
{

    private final Map<String, List<String>> adaptedMap = new HashMap<String, List<String>>();

    /**
     * {@inheritDoc}
     */
    public String getFirstValue( String name )
    {
        List<String> headerValues = adaptedMap.get( name );
        if ( headerValues == null || headerValues.isEmpty() )
        {
            return null;
        }
        return headerValues.iterator().next();
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getValues( String name )
    {
        List<String> storedValues = adaptedMap.get( name );
        if ( storedValues != null )
        {
            return unmodifiableList( storedValues );
        }
        return null;
    }

    public SimpleMultiValued addValue( String name, String value )
    {
        List<String> storedValues = adaptedMap.get( name );
        if ( storedValues == null )
        {
            storedValues = new LinkedList<String>();
            adaptedMap.put( name, storedValues );
        }

        storedValues.add( value );
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getAllKeys()
    {
        return unmodifiableSet( adaptedMap.keySet() );
    }

    /**
     * {@inheritDoc}
     */
    public Iterable<Entry<String, List<String>>> getAllEntries()
    {
        return new Iterable<Map.Entry<String,List<String>>>()
        {

            public Iterator<Entry<String, List<String>>> iterator()
            {
                return new UnmodifiableIterator( adaptedMap.entrySet().iterator() );
            }

        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        return hash( 1, 31, adaptedMap );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }

        if ( obj == null || getClass() != obj.getClass() )
        {
            return false;
        }

        SimpleMultiValued other = (SimpleMultiValued) obj;
        return eq( adaptedMap, other.adaptedMap );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return adaptedMap.toString();
    }

    // internal collections utilities

    /**
     * An iterator implementation from which is impossible remove elements.
     */
    private static final class UnmodifiableIterator
        implements Iterator<Entry<String, List<String>>>
    {

        private final Iterator<Entry<String, List<String>>> adapted;

        public UnmodifiableIterator( Iterator<Entry<String, List<String>>> adapted )
        {
            this.adapted = adapted;
        }

        /**
         * {@inheritDoc}
         */
        public boolean hasNext()
        {
            return adapted.hasNext();
        }

        /**
         * {@inheritDoc}
         */
        public Entry<String, List<String>> next()
        {
            return new UnmodifiableEntry( adapted.next() );
        }

        /**
         * {@inheritDoc}
         */
        public void remove()
        {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * An entry implementation from which is impossible replace the value
     * and value itself is unmodifiable.
     */
    private static final class UnmodifiableEntry
        implements Entry<String, List<String>>
    {

        private final Entry<String, List<String>> adapted;

        public UnmodifiableEntry( Entry<String, List<String>> adapted )
        {
            this.adapted = adapted;
        }

        /**
         * {@inheritDoc}
         */
        public String getKey()
        {
            return adapted.getKey();
        }

        /**
         * {@inheritDoc}
         */
        public List<String> getValue()
        {
            return unmodifiableList( adapted.getValue() );
        }

        /**
         * {@inheritDoc}
         */
        public List<String> setValue( List<String> value )
        {
            throw new UnsupportedOperationException();
        }

    }

}
