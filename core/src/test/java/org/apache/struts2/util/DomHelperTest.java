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
package org.apache.struts2.util;

import org.apache.struts2.util.location.Location;
import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import java.io.StringReader;
import java.util.Objects;

/**
 * Test cases for {@link DomHelper}.
 */
public class DomHelperTest extends TestCase {

    public void testParse() {
        String xml = "<!DOCTYPE foo [<!ELEMENT foo (bar)><!ELEMENT bar (#PCDATA)>]>\n<foo>\n<bar/>\n</foo>\n";
        InputSource in = new InputSource(new StringReader(xml));
        in.setSystemId("foo://bar");

        Document doc = DomHelper.parse(in);
        assertNotNull(doc);
        assertEquals("Wrong root node", "foo", doc.getDocumentElement().getNodeName());

        NodeList nl = doc.getElementsByTagName("bar");
        assertEquals(1, nl.getLength());
    }

    public void testGetLocationObject() {
        String xml = "<!DOCTYPE foo [<!ELEMENT foo (bar)><!ELEMENT bar (#PCDATA)>]>\n<foo>\n<bar/>\n</foo>\n";
        InputSource in = new InputSource(new StringReader(xml));
        in.setSystemId("foo://bar");

        Document doc = DomHelper.parse(in);

        NodeList nl = doc.getElementsByTagName("bar");

        Location loc = DomHelper.getLocationObject((Element) nl.item(0));

        assertNotNull(loc);
        assertEquals("Should be line 3, was " + loc.getLineNumber(), 3, loc.getLineNumber());
    }

    public void testExternalEntities() {
        String dtdFile = Objects.requireNonNull(getClass().getResource("/author.dtd")).getPath();
        String xml = "<!DOCTYPE foo [<!ELEMENT foo (bar)><!ELEMENT bar (#PCDATA)><!ENTITY writer SYSTEM \"file://" + dtdFile + "\">]><foo><bar>&writer;</bar></foo>";
        InputSource in = new InputSource(new StringReader(xml));
        in.setSystemId("foo://bar");

        Document doc = DomHelper.parse(in);
        assertNotNull(doc);
        assertEquals("Wrong root node", "foo", doc.getDocumentElement().getNodeName());

        NodeList nl = doc.getElementsByTagName("bar");
        assertEquals(1, nl.getLength());
        assertNull(nl.item(0).getNodeValue());
    }

    /**
     * Tests that the parser is protected against Billion Laughs (XML Entity Expansion) attack.
     * The FEATURE_SECURE_PROCESSING flag and the JDK's built-in entity expansion limit (64K
     * since JDK 7u45) both cap entity expansion to prevent DoS.
     * See: <a href="https://en.wikipedia.org/wiki/Billion_laughs_attack">Billion laughs attack</a>
     */
    public void testBillionLaughsProtection() {
        String xml = "<?xml version=\"1.0\"?>" +
            "<!DOCTYPE root [" +
            "<!ENTITY lol0 \"lol\">" +
            "<!ENTITY lol1 \"&lol0;&lol0;&lol0;&lol0;&lol0;&lol0;&lol0;&lol0;&lol0;&lol0;\">" +
            "<!ENTITY lol2 \"&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;\">" +
            "<!ENTITY lol3 \"&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;\">" +
            "<!ENTITY lol4 \"&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;\">" +
            "<!ENTITY lol5 \"&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;\">" +
            "]>" +
            "<root>&lol5;</root>";

        InputSource in = new InputSource(new StringReader(xml));
        in.setSystemId("test://billion-laughs");

        try {
            DomHelper.parse(in);
            fail("Parser should reject excessive entity expansion");
        } catch (Exception e) {
            assertNotNull(e.getCause());
            assertTrue(e.getCause() instanceof SAXParseException);
        }
    }
}
