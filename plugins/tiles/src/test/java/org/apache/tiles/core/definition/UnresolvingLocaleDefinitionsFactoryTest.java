/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.tiles.core.definition;

import org.apache.tiles.api.Definition;
import org.apache.tiles.core.definition.dao.DefinitionDAO;
import org.apache.tiles.core.locale.LocaleResolver;
import org.apache.tiles.request.Request;
import org.junit.Test;

import java.util.Locale;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

/**
 * Tests {@link UnresolvingLocaleDefinitionsFactory}.
 */
public class UnresolvingLocaleDefinitionsFactoryTest {

    /**
     * Test method for {@link UnresolvingLocaleDefinitionsFactory#getDefinition(String, Request)}.
     */
    @Test
    public void testGetDefinition() {
        DefinitionDAO<Locale> dao = createMock(DefinitionDAO.class);
        LocaleResolver localeResolver = createMock(LocaleResolver.class);
        UnresolvingLocaleDefinitionsFactory factory = new UnresolvingLocaleDefinitionsFactory();
        Request request = createMock(Request.class);
        Definition definition = createMock(Definition.class);
        Locale locale = Locale.ITALY;

        expect(localeResolver.resolveLocale(request)).andReturn(locale);
        expect(dao.getDefinition("myDefinition", locale)).andReturn(definition);

        replay(dao, localeResolver, request, definition);
        factory.setDefinitionDAO(dao);
        factory.setLocaleResolver(localeResolver);
        assertEquals(definition, factory.getDefinition("myDefinition", request));
        verify(dao, localeResolver, request, definition);
    }

}
