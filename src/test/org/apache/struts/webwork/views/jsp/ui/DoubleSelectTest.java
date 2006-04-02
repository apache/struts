package org.apache.struts.webwork.views.jsp.ui;

import org.apache.struts.webwork.TestAction;
import org.apache.struts.webwork.views.jsp.AbstractUITagTest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Test case for DoubleSelectTag.
 * 
 * @author <a href="mailto:m.bogaert@memenco.com">Mathias Bogaert</a>
 * @author tm_jee
 * @version $Date: 2006/02/03 12:45:24 $ $Id: DoubleSelectTest.java,v 1.6 2006/02/03 12:45:24 rgielen Exp $
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

        tag.doStartTag();
        tag.doEndTag();

        verify(SelectTag.class.getResource("DoubleSelect-1.txt"));
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

    public void testGenericAjax() throws Exception {
        DoubleSelectTag tag = new DoubleSelectTag();
        prepareTagGeneric(tag);
        verifyGenericProperties(tag, "ajax", new String[]{"value"});
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
