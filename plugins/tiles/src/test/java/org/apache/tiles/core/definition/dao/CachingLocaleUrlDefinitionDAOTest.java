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

package org.apache.tiles.core.definition.dao;

import junit.framework.TestCase;
import org.apache.tiles.api.Attribute;
import org.apache.tiles.api.Definition;
import org.apache.tiles.api.ListAttribute;
import org.apache.tiles.core.definition.DefinitionsReader;
import org.apache.tiles.core.definition.MockDefinitionsReader;
import org.apache.tiles.core.definition.digester.DigesterDefinitionsReader;
import org.apache.tiles.core.definition.pattern.BasicPatternDefinitionResolver;
import org.apache.tiles.core.definition.pattern.PatternDefinitionResolver;
import org.apache.tiles.core.definition.pattern.wildcard.WildcardDefinitionPatternMatcherFactory;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.request.locale.URLApplicationResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

/**
 * Tests {@link CachingLocaleUrlDefinitionDAO}.
 *
 * @version $Rev$ $Date$
 */
public class CachingLocaleUrlDefinitionDAOTest extends TestCase {

    /**
     * The object to test.
     */
    private CachingLocaleUrlDefinitionDAO definitionDao;

    private ApplicationContext applicationContext;

    private ApplicationResource url1;

    private ApplicationResource url2;

    private ApplicationResource url3;

    private ApplicationResource urlWildcard;

    private ApplicationResource url21;

    private ApplicationResource setupUrl(String filename, Locale... locales) {
        ApplicationResource url = new URLApplicationResource(
            "org/apache/tiles/core/config/" + filename + ".xml",
            Objects.requireNonNull(this.getClass().getClassLoader().getResource("org/apache/tiles/core/config/" + filename + ".xml"))
        );
        assertNotNull("Could not load " + filename + " file.", url);
        expect(applicationContext.getResource(url.getLocalePath())).andReturn(url).anyTimes();
        expect(applicationContext.getResource(url, Locale.ROOT)).andReturn(url).anyTimes();
        Map<Locale, ApplicationResource> localeResources = new HashMap<Locale, ApplicationResource>();
        for (Locale locale : locales) {
            ApplicationResource urlLocale = new URLApplicationResource(
                "org/apache/tiles/core/config/" + filename + "_" + locale.toString() + ".xml",
                Objects.requireNonNull(this.getClass().getClassLoader().getResource("org/apache/tiles/core/config/" + filename + "_" + locale + ".xml"))
            );
            assertNotNull("Could not load " + filename + "_" + locale + " file.", urlLocale);
            localeResources.put(locale, urlLocale);
        }
        for (Locale locale : new Locale[]{Locale.CANADA_FRENCH, Locale.FRENCH, Locale.US, Locale.ENGLISH,
            Locale.CHINA, Locale.CHINESE, Locale.ITALY, Locale.ITALIAN}) {
            ApplicationResource urlLocale = localeResources.get(locale);
            expect(applicationContext.getResource(url, locale)).andReturn(urlLocale).anyTimes();
        }
        return url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        applicationContext = createMock(ApplicationContext.class);
        url1 = setupUrl("defs1", Locale.FRENCH, Locale.CANADA_FRENCH, Locale.US);
        url2 = setupUrl("defs2");
        url3 = setupUrl("defs3");
        urlWildcard = setupUrl("defs-wildcard");
        url21 = setupUrl("tiles-defs-2.1", Locale.ITALIAN);
        replay(applicationContext);
        definitionDao = new CachingLocaleUrlDefinitionDAO(applicationContext);
        WildcardDefinitionPatternMatcherFactory definitionPatternMatcherFactory =
            new WildcardDefinitionPatternMatcherFactory();
        PatternDefinitionResolver<Locale> definitionResolver = new BasicPatternDefinitionResolver<>(
            definitionPatternMatcherFactory,
            definitionPatternMatcherFactory);
        definitionDao.setPatternDefinitionResolver(definitionResolver);
    }

    public void testGetDefinition() {
        List<ApplicationResource> sourceURLs = new ArrayList<>();
        sourceURLs.add(url1);
        sourceURLs.add(url2);
        sourceURLs.add(url3);
        definitionDao.setSources(sourceURLs);
        DefinitionsReader reader = new DigesterDefinitionsReader();
        definitionDao.setReader(reader);

        assertNotNull("test.def1 definition not found.", definitionDao
            .getDefinition("test.def1", null));
        assertNotNull("test.def2 definition not found.", definitionDao
            .getDefinition("test.def2", null));
        assertNotNull("test.def3 definition not found.", definitionDao
            .getDefinition("test.def3", null));
        assertNotNull("test.common definition not found.", definitionDao
            .getDefinition("test.common", null));
        assertNotNull("test.common definition in US locale not found.",
            definitionDao.getDefinition("test.common", Locale.US));
        assertNotNull("test.common definition in FRENCH locale not found.",
            definitionDao.getDefinition("test.common", Locale.FRENCH));
        assertNotNull("test.common definition in CHINA locale not found.",
            definitionDao.getDefinition("test.common", Locale.CHINA));
        assertNotNull(
            "test.common.french definition in FRENCH locale not found.",
            definitionDao.getDefinition("test.common.french",
                Locale.FRENCH));
        assertNotNull(
            "test.common.french definition in CANADA_FRENCH locale not found.",
            definitionDao.getDefinition("test.common.french",
                Locale.CANADA_FRENCH));
        assertNotNull("test.def.toextend definition not found.", definitionDao
            .getDefinition("test.def.toextend", null));
        assertNotNull("test.def.overridden definition not found.",
            definitionDao.getDefinition("test.def.overridden", null));
        assertNotNull(
            "test.def.overridden definition in FRENCH locale not found.",
            definitionDao.getDefinition("test.def.overridden",
                Locale.FRENCH));

        assertEquals("Incorrect default country value", "default",
            definitionDao.getDefinition("test.def1", null).getAttribute(
                "country").getValue());
        assertEquals("Incorrect US country value", "US", definitionDao
            .getDefinition("test.def1", Locale.US).getAttribute("country")
            .getValue());
        assertEquals("Incorrect France country value", "France", definitionDao
            .getDefinition("test.def1", Locale.FRENCH).getAttribute(
                "country").getValue());
        assertEquals("Incorrect Chinese country value (should be default)",
            "default", definitionDao.getDefinition("test.def1",
                Locale.CHINA).getAttribute("country").getValue());
        assertEquals("Incorrect default country value", "default",
            definitionDao.getDefinition("test.def.overridden", null)
                .getAttribute("country").getValue());
        assertEquals("Incorrect default title value",
            "Definition to be overridden", definitionDao.getDefinition(
                    "test.def.overridden", null).getAttribute("title")
                .getValue());
        assertEquals("Incorrect France country value", "France", definitionDao
            .getDefinition("test.def.overridden", Locale.FRENCH)
            .getAttribute("country").getValue());
        assertNull("Definition in French not found", definitionDao
            .getDefinition("test.def.overridden", Locale.FRENCH)
            .getAttribute("title"));
    }

    public void testGetDefinitions() {
        List<ApplicationResource> sourceURLs = new ArrayList<>();
        sourceURLs.add(url1);
        sourceURLs.add(url2);
        sourceURLs.add(url3);
        definitionDao.setSources(sourceURLs);
        DefinitionsReader reader = new DigesterDefinitionsReader();
        definitionDao.setReader(reader);

        Map<String, Definition> defaultDefinitions = definitionDao
            .getDefinitions(null);
        Map<String, Definition> usDefinitions = definitionDao
            .getDefinitions(Locale.US);
        Map<String, Definition> frenchDefinitions = definitionDao
            .getDefinitions(Locale.FRENCH);
        Map<String, Definition> chinaDefinitions = definitionDao
            .getDefinitions(Locale.CHINA);
        Map<String, Definition> canadaFrenchDefinitions = definitionDao
            .getDefinitions(Locale.CANADA_FRENCH);

        assertNotNull("test.def1 definition not found.", defaultDefinitions
            .get("test.def1"));
        assertNotNull("test.def2 definition not found.", defaultDefinitions
            .get("test.def2"));
        assertNotNull("test.def3 definition not found.", defaultDefinitions
            .get("test.def3"));
        assertNotNull("test.common definition not found.", defaultDefinitions
            .get("test.common"));
        assertNotNull("test.common definition in US locale not found.",
            usDefinitions.get("test.common"));
        assertNotNull("test.common definition in FRENCH locale not found.",
            frenchDefinitions.get("test.common"));
        assertNotNull("test.common definition in CHINA locale not found.",
            chinaDefinitions.get("test.common"));
        assertNotNull(
            "test.common.french definition in FRENCH locale not found.",
            frenchDefinitions.get("test.common.french"));
        assertNotNull(
            "test.common.french definition in CANADA_FRENCH locale not found.",
            canadaFrenchDefinitions.get("test.common.french"));
        assertNotNull("test.def.toextend definition not found.",
            defaultDefinitions.get("test.def.toextend"));
        assertNotNull("test.def.overridden definition not found.",
            defaultDefinitions.get("test.def.overridden"));
        assertNotNull(
            "test.def.overridden definition in FRENCH locale not found.",
            frenchDefinitions.get("test.def.overridden"));

        assertEquals("Incorrect default country value", "default",
            defaultDefinitions.get("test.def1").getAttribute("country")
                .getValue());
        assertEquals("Incorrect US country value", "US", usDefinitions.get(
            "test.def1").getAttribute("country").getValue());
        assertEquals("Incorrect France country value", "France",
            frenchDefinitions.get("test.def1").getAttribute("country")
                .getValue());
        assertEquals("Incorrect Chinese country value (should be default)",
            "default", chinaDefinitions.get("test.def1").getAttribute(
                "country").getValue());
        assertEquals("Incorrect default country value", "default",
            defaultDefinitions.get("test.def.overridden").getAttribute(
                "country").getValue());
        assertEquals("Incorrect default title value",
            "Definition to be overridden", defaultDefinitions.get(
                "test.def.overridden").getAttribute("title").getValue());
        assertEquals("Incorrect France country value", "France",
            frenchDefinitions.get("test.def.overridden").getAttribute(
                "country").getValue());
        assertNull("Definition in French not found", frenchDefinitions.get(
            "test.def.overridden").getAttribute("title"));
    }

    public void testSetSourceURLs() {
        List<ApplicationResource> sourceURLs = new ArrayList<>();
        sourceURLs.add(url1);
        sourceURLs.add(url2);
        sourceURLs.add(url3);
        definitionDao.setSources(sourceURLs);
        assertEquals("The source URLs are not set correctly", sourceURLs,
            definitionDao.sources);
    }

    public void testSetReader() {
        DefinitionsReader reader = createMock(DefinitionsReader.class);
        definitionDao.setReader(reader);
        assertEquals("There reader has not been set correctly", reader,
            definitionDao.reader);
    }

    public void testInit() {
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        Set<ApplicationResource> urlSet = new HashSet<>();
        urlSet.add(url1);
        expect(applicationContext.getResources("/WEB-INF/tiles.xml"))
            .andReturn(urlSet);
        replay(applicationContext);
        DefinitionsReader reader = new DigesterDefinitionsReader();
        definitionDao.setReader(reader);
        List<ApplicationResource> sourceURLs = new ArrayList<>();
        sourceURLs.add(url1);
        definitionDao.setSources(sourceURLs);
        assertEquals("The reader is not of the correct class",
            DigesterDefinitionsReader.class, definitionDao.reader
                .getClass());
        assertEquals("The source URLs are not correct", sourceURLs,
            definitionDao.sources);
        reset(applicationContext);

        definitionDao.setReader(new MockDefinitionsReader());
        assertEquals("The reader is not of the correct class",
            MockDefinitionsReader.class, definitionDao.reader.getClass());
        sourceURLs = new ArrayList<>();
        sourceURLs.add(url1);
        sourceURLs.add(url2);
        sourceURLs.add(url3);
        definitionDao.setSources(sourceURLs);
        assertEquals("The source URLs are not correct", sourceURLs,
            definitionDao.sources);
    }

    /**
     * Tests wildcard mappings.
     */
    public void testWildcardMapping() {
        List<ApplicationResource> urls = new ArrayList<>();
        urls.add(urlWildcard);
        definitionDao.setSources(urls);
        definitionDao.setReader(new DigesterDefinitionsReader());

        Definition definition = definitionDao.getDefinition("test.defName.subLayered", Locale.ITALY);
        assertEquals("The template is not correct", "/testName.jsp", definition
            .getTemplateAttribute().getValue());
        assertEquals("The header attribute is not correct",
            "/common/headerLayered.jsp", definition.getAttribute("header")
                .getValue());
        definition = definitionDao.getDefinition("test.defName.subLayered", Locale.ITALIAN);
        assertEquals("The template is not correct", "/testName.jsp", definition
            .getTemplateAttribute().getValue());
        assertEquals("The header attribute is not correct",
            "/common/headerLayered.jsp", definition.getAttribute("header")
                .getValue());
        definition = definitionDao.getDefinition("test.defName.subLayered", null);
        assertEquals("The template is not correct", "/testName.jsp", definition
            .getTemplateAttribute().getValue());
        assertEquals("The header attribute is not correct",
            "/common/headerLayered.jsp", definition.getAttribute("header")
                .getValue());
        definition = definitionDao.getDefinition("test.defName.noAttribute", null);
        assertEquals("/testName.jsp", definition.getTemplateAttribute().getValue());
        assertNull(definition.getLocalAttributeNames());
        definition = definitionDao.getDefinition("test.def3", null);
        assertNotNull("The simple definition is null", definition);

        definition = definitionDao.getDefinition("test.extended.defName.subLayered", null);
        assertEquals("test.defName.subLayered", definition.getExtends());
        assertNull(definition.getTemplateAttribute().getValue());
        assertEquals(1, definition.getLocalAttributeNames().size());
        assertEquals("Overridden Title", definition.getAttribute("title").getValue());
    }

    /**
     * Tests
     * {@link ResolvingLocaleUrlDefinitionDAO#getDefinition(String, Locale)}
     * when loading multiple files for a locale.
     */
    public void testListAttributeLocaleInheritance() {
        List<ApplicationResource> urls = new ArrayList<>();
        urls.add(url21);
        definitionDao.setSources(urls);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        definitionDao.setReader(new DigesterDefinitionsReader());
        replay(applicationContext);

        Definition definition = definitionDao.getDefinition(
            "test.inherit.list", Locale.ITALIAN);
        ListAttribute listAttribute = (ListAttribute) definition
            .getAttribute("list");
        List<Attribute> attributes = listAttribute.getValue();
        // It is right not to resolve inheritance in this DAO.
        assertEquals(1, attributes.size());
        verify(applicationContext);
    }
}
