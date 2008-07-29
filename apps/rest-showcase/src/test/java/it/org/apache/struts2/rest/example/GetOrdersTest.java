package it.org.apache.struts2.rest.example;

import net.sourceforge.jwebunit.junit.WebTestCase;

public class GetOrdersTest extends WebTestCase {

    public void setUp() throws Exception {
        getTestContext().setBaseUrl("http://localhost:8080/struts2-rest-showcase");
    }


    public void testGetOrders() {
        beginAt("/orders/3");
        assertTextPresent("Bob");
        assertTextNotPresent("Sarah");
    }

    public void testGetOrdersInHtml() {
        beginAt("/orders/3.xhtml");
        assertTextPresent("Bob");
    }

    public void testGetOrdersInXml() {
        beginAt("/orders/3.xml");
        assertTextPresent("<clientName>Bob");
    }

    public void testGetOrdersInJson() {
        beginAt("/orders/3.json");
        assertTextPresent("\"clientName\":\"Bob\"");
    }
}