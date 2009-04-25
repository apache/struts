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

package org.apache.struts2.views.jsp.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.struts2.TestAction;
import org.apache.struts2.views.jsp.AbstractUITagTest;

/**
 * Test case for DoubleSelectTag.
 *
 */
public class DoubleSelectTest extends AbstractUITagTest {

    public void testDouble() throws Exception {
        TestAction testAction = (TestAction) action;

        Region antwerp = new Region("Antwerp", "AN");
        Region gent = new Region("Gent", "GN");
        Region brugge = new Region("Brugge", "BRG");
        ArrayList belgiumRegions = new ArrayList();
        belgiumRegions.add(antwerp);
        belgiumRegions.add(gent);
        belgiumRegions.add(brugge);
        Country belgium = new Country("Belgium", "BE", belgiumRegions);

        Region paris = new Region("Paris", "PA");
        Region bordeaux = new Region("Bordeaux", "BOR");
        ArrayList franceRegions = new ArrayList();
        franceRegions.add(paris);
        franceRegions.add(bordeaux);
        Country france = new Country("France", "FR", franceRegions);

        Collection collection = new ArrayList(2);
        collection.add("AN");
        testAction.setCollection(collection);

        List countries = new ArrayList();
        countries.add(belgium);
        countries.add(france);

        testAction.setList2(countries);

        DoubleSelectTag tag = new DoubleSelectTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("foo");
        tag.setDoubleName("region");

        tag.setList("list2");
        tag.setDoubleList("regions");

        tag.setListKey("iso");
        tag.setDoubleListKey("key");
        tag.setListValue("name");
        tag.setDoubleListValue("name");

        tag.setFormName("inputForm");

        tag.setOnmousedown("window.status='onmousedown';");
        tag.setOnmousemove("window.status='onmousemove';");
        tag.setOnmouseout("window.status='onmouseout';");
        tag.setOnmouseover("window.status='onmouseover';");
        tag.setOnmouseup("window.status='onmouseup';");

        //css style and class
        tag.setCssClass("c1");
        tag.setCssStyle("s1");
        tag.setDoubleCssClass("c2");
        tag.setDoubleCssStyle("s2");
        
        tag.doStartTag();
        tag.doEndTag();

        verify(SelectTag.class.getResource("DoubleSelect-1.txt"));
    }
    
    public void testOnchange() throws Exception {
        TestAction testAction = (TestAction) action;

        Region antwerp = new Region("Antwerp", "AN");
        Region gent = new Region("Gent", "GN");
        Region brugge = new Region("Brugge", "BRG");
        ArrayList belgiumRegions = new ArrayList();
        belgiumRegions.add(antwerp);
        belgiumRegions.add(gent);
        belgiumRegions.add(brugge);
        Country belgium = new Country("Belgium", "BE", belgiumRegions);

        Region paris = new Region("Paris", "PA");
        Region bordeaux = new Region("Bordeaux", "BOR");
        ArrayList franceRegions = new ArrayList();
        franceRegions.add(paris);
        franceRegions.add(bordeaux);
        Country france = new Country("France", "FR", franceRegions);

        Collection collection = new ArrayList(2);
        collection.add("AN");
        testAction.setCollection(collection);

        List countries = new ArrayList();
        countries.add(belgium);
        countries.add(france);

        testAction.setList2(countries);

        DoubleSelectTag tag = new DoubleSelectTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("foo");
        tag.setDoubleName("region");

        tag.setList("list2");
        tag.setDoubleList("regions");

        tag.setListKey("iso");
        tag.setDoubleListKey("key");
        tag.setListValue("name");
        tag.setDoubleListValue("name");

        tag.setFormName("inputForm");

        tag.setOnmousedown("window.status='onmousedown';");
        tag.setOnmousemove("window.status='onmousemove';");
        tag.setOnmouseout("window.status='onmouseout';");
        tag.setOnmouseover("window.status='onmouseover';");
        tag.setOnmouseup("window.status='onmouseup';");
        tag.setOnchange("window.status='onchange';");

        //css style and class
        tag.setCssClass("c1");
        tag.setCssStyle("s1");
        tag.setDoubleCssClass("c2");
        tag.setDoubleCssStyle("s2");
        
        tag.doStartTag();
        tag.doEndTag();

        verify(SelectTag.class.getResource("DoubleSelect-4.txt"));
    }


    public void testDoubleWithDefaultSelectedValues() throws Exception {

        TestAction testAction = (TestAction) action;

        Region antwerp = new Region("Antwerp", "AN");
        Region gent = new Region("Gent", "GN");
        Region brugge = new Region("Brugge", "BRG");
        ArrayList belgiumRegions = new ArrayList();
        belgiumRegions.add(antwerp);
        belgiumRegions.add(gent);
        belgiumRegions.add(brugge);
        Country belgium = new Country("Belgium", "BE", belgiumRegions);

        Region paris = new Region("Paris", "PA");
        Region bordeaux = new Region("Bordeaux", "BOR");
        ArrayList franceRegions = new ArrayList();
        franceRegions.add(paris);
        franceRegions.add(bordeaux);
        Country france = new Country("France", "FR", franceRegions);

        Collection collection = new ArrayList(2);
        collection.add("AN");
        testAction.setCollection(collection);

        List countries = new ArrayList();
        countries.add(belgium);
        countries.add(france);

        testAction.setList2(countries);

        DoubleSelectTag tag = new DoubleSelectTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("foo");
        tag.setDoubleName("region");

        tag.setValue("'FR'");
        tag.setDoubleValue("'BOR'");

        tag.setList("list2");
        tag.setDoubleList("regions");

        tag.setListKey("iso");
        tag.setDoubleListKey("key");
        tag.setListValue("name");
        tag.setDoubleListValue("name");

        tag.setFormName("inputForm");

        tag.setOnmousedown("window.status='onmousedown';");
        tag.setOnmousemove("window.status='onmousemove';");
        tag.setOnmouseout("window.status='onmouseout';");
        tag.setOnmouseover("window.status='onmouseover';");
        tag.setOnmouseup("window.status='onmouseup';");

        tag.doStartTag();
        tag.doEndTag();

        verify(SelectTag.class.getResource("DoubleSelect-2.txt"));


    }
    
    public void testDoubleWithDotName() throws Exception {
        TestAction testAction = (TestAction) action;

        Region antwerp = new Region("Antwerp", "AN");
        Region gent = new Region("Gent", "GN");
        Region brugge = new Region("Brugge", "BRG");
        ArrayList belgiumRegions = new ArrayList();
        belgiumRegions.add(antwerp);
        belgiumRegions.add(gent);
        belgiumRegions.add(brugge);
        Country belgium = new Country("Belgium", "BE", belgiumRegions);

        Region paris = new Region("Paris", "PA");
        Region bordeaux = new Region("Bordeaux", "BOR");
        ArrayList franceRegions = new ArrayList();
        franceRegions.add(paris);
        franceRegions.add(bordeaux);
        Country france = new Country("France", "FR", franceRegions);

        Collection collection = new ArrayList(2);
        collection.add("AN");
        testAction.setCollection(collection);

        List countries = new ArrayList();
        countries.add(belgium);
        countries.add(france);

        testAction.setList2(countries);

        DoubleSelectTag tag = new DoubleSelectTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("foo.bar");
        tag.setDoubleName("region");

        tag.setList("list2");
        tag.setDoubleList("regions");

        tag.setListKey("iso");
        tag.setDoubleListKey("key");
        tag.setListValue("name");
        tag.setDoubleListValue("name");

        tag.setFormName("inputForm");

        tag.setOnmousedown("window.status='onmousedown';");
        tag.setOnmousemove("window.status='onmousemove';");
        tag.setOnmouseout("window.status='onmouseout';");
        tag.setOnmouseover("window.status='onmouseover';");
        tag.setOnmouseup("window.status='onmouseup';");

        tag.doStartTag();
        tag.doEndTag();

        verify(SelectTag.class.getResource("DoubleSelect-3.txt"));
    }

    public void testGenericSimple() throws Exception {
        DoubleSelectTag tag = new DoubleSelectTag();
        prepareTagGeneric(tag);
        verifyGenericProperties(tag, "simple", new String[]{"value"});
    }

    public void testGenericXhtml() throws Exception {
        DoubleSelectTag tag = new DoubleSelectTag();
        prepareTagGeneric(tag);
        verifyGenericProperties(tag, "xhtml", new String[]{"value"});
    }

    private void prepareTagGeneric(DoubleSelectTag tag) {
        TestAction testAction = (TestAction) action;
        Region antwerp = new Region("Antwerp", "AN");
        Region gent = new Region("Gent", "GN");
        Region brugge = new Region("Brugge", "BRG");
        ArrayList belgiumRegions = new ArrayList();
        belgiumRegions.add(antwerp);
        belgiumRegions.add(gent);
        belgiumRegions.add(brugge);
        Country belgium = new Country("Belgium", "BE", belgiumRegions);

        Region paris = new Region("Paris", "PA");
        Region bordeaux = new Region("Bordeaux", "BOR");
        ArrayList franceRegions = new ArrayList();
        franceRegions.add(paris);
        franceRegions.add(bordeaux);
        Country france = new Country("France", "FR", franceRegions);

        Collection collection = new ArrayList(2);
        collection.add("AN");
        testAction.setCollection(collection);

        tag.setList("collection");

        List countries = new ArrayList();
        countries.add(belgium);
        countries.add(france);

        testAction.setList2(countries);

        tag.setValue("'FR'");
        tag.setDoubleValue("'BOR'");

        tag.setList("list2");
        tag.setDoubleList("regions");
        tag.setDoubleName("region");

        tag.setListKey("iso");
        tag.setDoubleListKey("key");
        tag.setListValue("name");
        tag.setDoubleListValue("name");

        tag.setFormName("inputForm");
    }

    public class Country {
        String name;
        String iso;
        Collection regions;

        public Country(String name, String iso, Collection regions) {
            this.name = name;
            this.iso = iso;
            this.regions = regions;
        }

        public String getName() {
            return name;
        }

        public String getIso() {
            return iso;
        }

        public Collection getRegions() {
            return regions;
        }
    }

    public class Region {
        String name;
        String key;

        public Region(String name, String key) {
            this.name = name;
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public String getKey() {
            return key;
        }
    }
}
