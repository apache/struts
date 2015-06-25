/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.util.location.Location;
import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;

/**
 * Test cases for {@link DomHelper}.
 */
public class DomHelperTest extends TestCase {

    private String xml = "<!DOCTYPE foo [\n" +
                         "<!ELEMENT foo (bar)>\n" +
                         "<!ELEMENT bar (#PCDATA)>\n" +
                         "]>\n" +
                         "<foo>\n" +
                         " <bar/>\n" +
                         "</foo>\n";
    
    public void testParse() throws Exception {
        InputSource in = new InputSource(new StringReader(xml));
        in.setSystemId("foo://bar");
        
        Document doc = DomHelper.parse(in);
        assertNotNull(doc);
        assertTrue("Wrong root node",
            "foo".equals(doc.getDocumentElement().getNodeName()));
        
        NodeList nl = doc.getElementsByTagName("bar");
        assertTrue(nl.getLength() == 1);
        
        
        
    }
    
    public void testGetLocationObject() throws Exception {
        InputSource in = new InputSource(new StringReader(xml));
        in.setSystemId("foo://bar");
        
        Document doc = DomHelper.parse(in);
        
        NodeList nl = doc.getElementsByTagName("bar");
        
        Location loc = DomHelper.getLocationObject((Element)nl.item(0));
        
        assertNotNull(loc);
        assertTrue("Should be line 6, was "+loc.getLineNumber(), 
            6==loc.getLineNumber());
    }
}
