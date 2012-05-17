package org.nnsoft.shs.core.collections;

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
import static org.nnsoft.shs.lang.Preconditions.checkArgument;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.nnsoft.shs.collections.MultiValued;

public final class SimpleMultiValued<K, V>
    implements MultiValued<K, V>
{

    private final Map<K, List<V>> adaptedMap = new HashMap<K, List<V>>();

    /**
     * {@inheritDoc}
     */
    public boolean contains( K key )
    {
        checkArgument( key != null, "null key not admitted" );
        return adaptedMap.containsKey( key );
    }

    /**
     * {@inheritDoc}
     */
    public V getFirstValue( K key )
    {
        checkArgument( key != null, "null key not admitted" );
        List<V> storedValues = adaptedMap.get( key );
        if ( storedValues == null || storedValues.isEmpty() )
        {
            return null;
        }
        return storedValues.iterator().next();
    }

    /**
     * {@inheritDoc}
     */
    public List<V> getValues( K key )
    {
        checkArgument( key != null, "null key not admitted" );
        List<V> storedValues = adaptedMap.get( key );
        if ( storedValues != null )
        {
            return unmodifiableList( storedValues );
        }
        return null;
    }

    public SimpleMultiValued<K, V> addValue( K key, V value )
    {
        checkArgument( key != null, "null key not admitted" );
        List<V> storedValues = adaptedMap.get( key );
        if ( storedValues == null )
        {
            storedValues = new LinkedList<V>();
            adaptedMap.put( key, storedValues );
        }

        storedValues.add( value );
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Set<K> getAllKeys()
    {
        return unmodifiableSet( adaptedMap.keySet() );
    }

    /**
     * {@inheritDoc}
     */
    public Iterable<Entry<K, List<V>>> getAllEntries()
    {
        return new Iterable<Map.Entry<K, List<V>>>()
        {

            public Iterator<Entry<K, List<V>>> iterator()
            {
                return new UnmodifiableIterator<K, V>( adaptedMap.entrySet().iterator() );
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

        SimpleMultiValued<?, ?> other = (SimpleMultiValued<?, ?>) obj;
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
    private static final class UnmodifiableIterator<K, V>
        implements Iterator<Entry<K, List<V>>>
    {

        private final Iterator<Entry<K, List<V>>> adapted;

        public UnmodifiableIterator( Iterator<Entry<K, List<V>>> adapted )
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
        public Entry<K, List<V>> next()
        {
            return new UnmodifiableEntry<K, V>( adapted.next() );
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
    private static final class UnmodifiableEntry<K, V>
        implements Entry<K, List<V>>
    {

        private final Entry<K, List<V>> adapted;

        public UnmodifiableEntry( Entry<K, List<V>> adapted )
        {
            this.adapted = adapted;
        }

        /**
         * {@inheritDoc}
         */
        public K getKey()
        {
            return adapted.getKey();
        }

        /**
         * {@inheritDoc}
         */
        public List<V> getValue()
        {
            return unmodifiableList( adapted.getValue() );
        }

        /**
         * {@inheritDoc}
         */
        public List<V> setValue( List<V> value )
        {
            throw new UnsupportedOperationException();
        }

    }

}
