/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.conversion.impl;

import org.apache.struts2.ActionContext;
import org.apache.struts2.conversion.TypeConverter;
import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.util.ValueStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CollectionConverterTest extends XWorkTestCase {

    /**
     * WW-5701: the marker constant's value is ordinary text, so an element that genuinely holds
     * that text converts successfully and must be kept.
     * <p>
     * The value is built at runtime rather than written as a literal on purpose: a literal would be
     * interned to the very same instance as the constant's value, which no request-derived
     * parameter ever is. A servlet container builds parameter values from the request bytes.
     */
    public void testElementWhoseTextEqualsTheMarkerIsKept() {
        String asSubmittedByAUser = new String("ognl.NoConversionPossible".toCharArray());
        assertNotSame("fixture must not be interned", TypeConverter.NO_CONVERSION_POSSIBLE, asSubmittedByAUser);

        Holder holder = new Holder();
        ValueStack vs = ActionContext.getContext().getValueStack();
        vs.push(holder);

        vs.setValue("names", new String[]{"alpha", asSubmittedByAUser, "omega"});

        assertEquals(Arrays.asList("alpha", "ognl.NoConversionPossible", "omega"), holder.getNames());
    }

    /**
     * The guard must still do its job: a genuinely unconvertible element is dropped.
     */
    public void testUnconvertibleElementIsStillDropped() {
        Holder holder = new Holder();
        ValueStack vs = ActionContext.getContext().getValueStack();
        vs.push(holder);

        vs.setValue("numbers", new String[]{"1", "not-a-number", "3"});

        assertEquals(Arrays.asList(1L, 3L), holder.getNumbers());
    }

    /**
     * The same guard on the path taken when the submitted value is itself a collection rather than
     * an array - here a List feeding a Set-typed property.
     */
    public void testUnconvertibleElementIsDroppedFromACollectionSource() {
        Holder holder = new Holder();
        ValueStack vs = ActionContext.getContext().getValueStack();
        vs.push(holder);

        vs.setValue("numberSet", Arrays.asList("1", "not-a-number", "3"));

        assertEquals(new HashSet<>(Arrays.asList(1L, 3L)), holder.getNumberSet());
    }

    /**
     * The same guard on the path taken when a single value is assigned to a collection property.
     * The property is seeded first so that a setter which is never called cannot pass vacuously.
     */
    public void testUnconvertibleSingleValueIsDropped() {
        Holder holder = new Holder();
        holder.setNumbers(new ArrayList<>(Arrays.asList(99L)));
        ValueStack vs = ActionContext.getContext().getValueStack();
        vs.push(holder);

        vs.setValue("numbers", "not-a-number");

        assertEquals(List.of(), holder.getNumbers());
    }

    public static class Holder {
        private List<String> names = new ArrayList<>();
        private List<Long> numbers = new ArrayList<>();
        private Set<Long> numberSet = new LinkedHashSet<>();

        public List<String> getNames() {
            return names;
        }

        public void setNames(List<String> names) {
            this.names = names;
        }

        public List<Long> getNumbers() {
            return numbers;
        }

        public void setNumbers(List<Long> numbers) {
            this.numbers = numbers;
        }

        public Set<Long> getNumberSet() {
            return numberSet;
        }

        public void setNumberSet(Set<Long> numberSet) {
            this.numberSet = numberSet;
        }
    }
}
