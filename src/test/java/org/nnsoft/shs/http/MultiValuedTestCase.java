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

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nnsoft.shs.collections.SimpleMultiValued;

public final class MultiValuedTestCase
{

    private SimpleMultiValued multiValued;

    @Before
    public void setUp()
    {
        multiValued = new SimpleMultiValued()
                          .addValue( "Accept-Charset", "ISO-8859-1" )
                          .addValue( "Accept-Charset", "utf-8;q=0.7" )
                          .addValue( "Accept-Charset", "*;q=0.7" );
    }

    @After
    public void tearDown()
    {
        multiValued = null;
    }

    @Test( expected = UnsupportedOperationException.class )
    public void cannotOverrideValuesDirectly()
    {
        List<String> values = multiValued.getValues( "Accept-Charset" );
        values.add( "user preferred value" );
    }

    @Test( expected = UnsupportedOperationException.class )
    public void cannotRemoveElementsFromIterator()
    {
        multiValued.getAllEntries().iterator().remove();
    }

    @Test( expected = UnsupportedOperationException.class )
    public void cannotOverrideEntryValue()
    {
        multiValued.getAllEntries().iterator().next().setValue( new ArrayList<String>() );
    }

}
