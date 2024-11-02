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
package org.apache.struts2;

import org.apache.struts2.locale.LocaleProvider;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

public class LocaleProviderTest {

    @Test
    public void toLocale() {
        // given
        DummyLocale locale = new DummyLocale();

        // when
        Locale actual = locale.toLocale("de");

        // then
        assertEquals(Locale.GERMAN, actual);
    }

    @Test
    public void toLocaleTrim() {
        // given
        DummyLocale locale = new DummyLocale();

        // when
        Locale actual = locale.toLocale(" de_DE ");

        // then
        assertEquals(Locale.GERMANY, actual);
    }

    @Test
    public void toLocaleNull() {
        // given
        DummyLocale locale = new DummyLocale();

        // when
        Locale actual = locale.toLocale("germany");

        // then
        assertNull(actual);
    }

}

class DummyLocale implements LocaleProvider {
    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public boolean isValidLocaleString(String localeStr) {
        return false;
    }

    @Override
    public boolean isValidLocale(Locale locale) {
        return false;
    }
}
