package org.nnsoft.shs.collections;

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

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A MultiValued instance allows associate multiple values to a same key.
 */
public interface MultiValued<K, V>
{

    /**
     * Returns the first associated value to the given key, if found, null otherwise.
     *
     * @param key the non null key for which retrieve the value
     * @return the first associated value to the given key, if found, null otherwise.
     */
    V getFirstValue( K key );

    /**
     * Returns an unmodifiable list with all the associated values to the given key, if found, null otherwise.
     *
     * @param key the non null key for which retrieve the values
     * @return an unmodifiable list with all the associated values to the given key, if found, null otherwise.
     */
    List<V> getValues( K key );

    /**
     * Returns an unmodifiable set containing all the stored keys.
     *
     * @return an unmodifiable set all the stored keys.
     */
    Set<K> getAllKeys();

    /**
     * Returns an unmodifiable iterable instance over all stored entries.
     *
     * @return an unmodifiable iterable instance over all stored entries.
     */
    Iterable<Entry<K, List<V>>> getAllEntries();

}
