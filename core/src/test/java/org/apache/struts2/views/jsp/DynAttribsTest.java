/*
 * $Id$
 *
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

package org.apache.struts2.views.jsp;

import java.io.File;
import java.net.URL;

import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.struts2.StrutsTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * DynAttribsTest test case.
 *
 * When a tag is declared in a TLD file as 
 * <dynamic-attributes>true</dynamic-attributes>
 * then the tag-class must implement the 
 * javax.servlet.jsp.tagext.DynamicAttributes interface.
 * If a tag's class does not implement this interface, 
 * the the application server will treat the tag as unsafe.
 * 
 * This test parses the struts-tag.tld file and checks
 * that each of the tags defined as accepting dynamic 
 * attributes implements DynamicAttributes.
 */
public class DynAttribsTest extends StrutsTestCase {
	
	private Document doc ;
	
    protected void setUp() throws Exception {
        super.setUp();
        
        // prepare to parse the TLD file using DOM
        DocumentBuilderFactory factory 
            = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder ;
        try {
            builder = factory.newDocumentBuilder();
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        URL s2Url = this.getClass().getResource("/META-INF/struts-tags.tld");
        if (s2Url == null ) {
            fail("unable to find struts-tags.tld");
        }
        File tldFile = new File(s2Url.toURI());
        doc = builder.parse(tldFile);
        
    }
    
    public void testHasDynParamInterface() {
        Element rootElem = doc.getDocumentElement();
        NodeList nl = rootElem.getElementsByTagName("tag");
        
        if(nl != null && nl.getLength() > 0) {
            
            for(int i = 0 ; i < nl.getLength();i++) {
        
                Element tag = (Element)nl.item(i);
                
                // for each tag, get the class name
                // and dynamic-attributes value
                NodeList tagClassNodes 
                    = tag.getElementsByTagName("tag-class");
                
                Element tagClassElement 
                    = (Element)tagClassNodes.item(0);
                String clazzName 
                    = tagClassElement.getFirstChild().getNodeValue();
                
                NodeList dynAttribsNodeList 
                    = tag.getElementsByTagName("dynamic-attributes");
                
                // skip if not marked as dynamic-attributes=true
                if (dynAttribsNodeList.getLength() < 1) {
                    continue;
                }
                
                Element dynAttribsElement 
                    = (Element)dynAttribsNodeList.item(0);
                
                String isDynAttribs 
                    = dynAttribsElement.getFirstChild().getNodeValue();
                
                if (isDynAttribs == null ) {
                    continue;
                }
                
                if (! isDynAttribs.equalsIgnoreCase("true") ) {
                    continue;
                }
                
                // load the class using reflection
                Class clazz = null;
                
                try {
                    clazz = Class.forName(clazzName);
                }
                catch (ClassNotFoundException e){
                    fail("unable to load class");
                    return;
                }
                
                // instantiate class
                Object o = null;
                try {
                    o = clazz.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                
                boolean hasDynAttribs = o instanceof DynamicAttributes;
                
                String failMsg = "Class - " + clazzName + " does not implement the DynamicAttributes interface";
                assertTrue(failMsg, hasDynAttribs);
            
            }
        }
    }
}
