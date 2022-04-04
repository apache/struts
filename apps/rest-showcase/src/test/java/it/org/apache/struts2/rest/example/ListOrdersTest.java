package it.org.apache.struts2.rest.example;

import net.sourceforge.jwebunit.junit.WebTestCase;

public class ListOrdersTest extends WebTestCase {

    public void setUp() throws Exception {
        getTestContext().setBaseUrl(ParameterUtils.getBaseUrl());
    }


    public void testListOrders() {
        beginAt("/orders");
        assertTextPresent("Bob");
        assertTextPresent("Sarah");
        assertTextPresent("Jim");
    }

    public void testListOrdersInHtml() {
        beginAt("/orders.xhtml");
        assertTextPresent("Bob");
        assertTextPresent("Sarah");
        assertTextPresent("Jim");
    }

    public void testListOrdersInXml() {
        beginAt("/orders.xml");
        assertTextPresent("<clientName>Bob");
        assertTextPresent("<clientName>Sarah");
        assertTextPresent("<clientName>Jim");
    }

    public void testListOrdersInJson() {
        beginAt("/orders.json");
        assertTextPresent("\"clientName\":\"Bob\"");
        assertTextPresent("\"clientName\":\"Sarah\"");
        assertTextPresent("\"clientName\":\"Jim\"");
    }
}
