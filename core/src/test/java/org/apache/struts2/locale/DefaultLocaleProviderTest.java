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
package org.apache.struts2.locale;

import org.apache.struts2.ActionContext;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DefaultLocaleProviderTest {

    private DefaultLocaleProvider provider;

    @Before
    public void setUp() throws Exception {
        provider = new DefaultLocaleProvider();
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        ActionContext.of().bind();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        ActionContext.clear();
    }

    @Test
    public void getLocale() {
        // given
        ActionContext.getContext().withLocale(Locale.ITALY);

        // when
        Locale actual = provider.getLocale();

        // then
        assertEquals(Locale.ITALY, actual);
    }

    @Test
    public void getLocaleNull() {
        // given
        ActionContext backup = ActionContext.getContext();
        ActionContext.clear();

        // when
        Locale actual = provider.getLocale();

        // then
        assertNull(actual);
        ActionContext.bind(backup);
    }

    @Test
    public void toLocale() {
        // given
        ActionContext.getContext().withLocale(Locale.GERMAN);

        // when
        Locale actual = provider.toLocale("it");

        // then
        assertEquals(Locale.ITALIAN, actual);
    }

    @Test
    public void toLocaleFull() {
        // given
        ActionContext.getContext().withLocale(Locale.GERMAN);

        // when
        Locale actual = provider.toLocale("it_IT");

        // then
        assertEquals(Locale.ITALY, actual);
    }

    @Test
    public void toLocaleTrimEndOfLine() {
        // given
        ActionContext.getContext().withLocale(Locale.GERMAN);

        // when
        Locale actual = provider.toLocale("it_IT\n");

        // then
        assertEquals(Locale.ITALY, actual);
    }

    @Test
    public void toLocaleTrimEmptySpace() {
        // given
        ActionContext.getContext().withLocale(Locale.GERMAN);

        // when
        Locale actual = provider.toLocale(" it_IT ");

        // then
        assertEquals(Locale.ITALY, actual);
    }

    @Test
    public void isValidLocaleNull() {
        // given
        ActionContext.getContext().withLocale(Locale.GERMAN);

        // when
        boolean actual = provider.isValidLocale(null);

        // then
        assertFalse(actual);
    }

    @Test
    public void isValidLocale() {
        // given
        ActionContext.getContext().withLocale(Locale.GERMAN);

        // when
        boolean actual = provider.isValidLocale(Locale.ITALIAN);

        // then
        assertTrue(actual);
    }

    @Test
    public void isValidLocaleString() {
        // given
        ActionContext.getContext().withLocale(Locale.GERMAN);

        // when
        boolean actual = provider.isValidLocaleString("it");

        // then
        assertTrue(actual);
    }

    @Test
    public void isValidLocaleStringNot() {
        // given
        ActionContext.getContext().withLocale(Locale.GERMAN);

        // when
        boolean actual = provider.isValidLocaleString("italy");

        // then
        assertFalse(actual);
    }

}
