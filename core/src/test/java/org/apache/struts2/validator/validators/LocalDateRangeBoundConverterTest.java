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
package org.apache.struts2.validator.validators;

import org.apache.struts2.conversion.TypeConversionException;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class LocalDateRangeBoundConverterTest {

    private final LocalDateRangeBoundConverter converter = new LocalDateRangeBoundConverter();

    @Test
    public void testParsesAnIsoBound() {
        Object value = converter.convertValue(new HashMap<>(), null, null, "min", " 2026-01-02 ", LocalDate.class);

        assertEquals(LocalDate.of(2026, 1, 2), value);
    }

    @Test
    public void testRejectsANonIsoBound() {
        TypeConversionException e = assertThrows(TypeConversionException.class,
                () -> converter.convertValue(new HashMap<>(), null, null, "max", "01/02/2026", LocalDate.class));

        assertEquals("Could not parse max as an ISO date (yyyy-MM-dd)", e.getMessage());
        assertTrue(e.getCause() instanceof DateTimeParseException);
    }

    @Test
    public void testPassesAnAlreadyConvertedBoundThrough() {
        LocalDate bound = LocalDate.of(2026, 1, 2);

        assertSame(bound, converter.convertValue(new HashMap<>(), null, null, "min", bound, LocalDate.class));
    }

}
