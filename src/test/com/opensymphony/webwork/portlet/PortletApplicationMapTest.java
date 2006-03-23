/*
 * Created on Mar 10, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.opensymphony.webwork.portlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Constraint;

/**
 * @author Nils-Helge Garli
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class PortletApplicationMapTest extends MockObjectTestCase {

    Mock mockPortletContext;

    PortletContext portletContext;

    public void setUp() throws Exception {
        super.setUp();
        mockPortletContext = mock(PortletContext.class);
        portletContext = (PortletContext) mockPortletContext.proxy();
    }

    public void testGetFromAttributes() {
        mockPortletContext.stubs().method("getAttribute").with(eq("dummyKey"))
                .will(returnValue("dummyValue"));

        PortletApplicationMap map = new PortletApplicationMap(
                (PortletContext) mockPortletContext.proxy());

        assertEquals("dummyValue", map.get("dummyKey"));
    }

    public void testGetFromInitParameters() {
        mockPortletContext.stubs().method("getAttribute").with(eq("dummyKey"));
        mockPortletContext.stubs().method("getInitParameter").with(
                eq("dummyKey")).will(returnValue("dummyValue"));

        PortletApplicationMap map = new PortletApplicationMap(
                (PortletContext) mockPortletContext.proxy());

        assertEquals("dummyValue", map.get("dummyKey"));
    }

    public void testPut() {
        mockPortletContext.expects(once()).method("setAttribute").with(
                new Constraint[] { eq("dummyKey"), eq("dummyValue") });
        mockPortletContext.expects(once()).method("getAttribute").with(
                eq("dummyKey")).will(returnValue("dummyValue"));
        PortletApplicationMap map = new PortletApplicationMap(portletContext);
        Object val = map.put("dummyKey", "dummyValue");
        assertEquals("dummyValue", val);
    }

    public void testRemove() {
        mockPortletContext.expects(once()).method("getAttribute").with(
                eq("dummyKey")).will(returnValue("dummyValue"));
        mockPortletContext.expects(once()).method("removeAttribute").with(
                eq("dummyKey"));
        PortletApplicationMap map = new PortletApplicationMap(portletContext);
        Object val = map.remove("dummyKey");
        assertEquals("dummyValue", val);
    }

    public void testEntrySet() {

        Enumeration names = new Enumeration() {

            List keys = Arrays.asList(new Object[] { "key1", "key2" });

            Iterator it = keys.iterator();

            public boolean hasMoreElements() {
                return it.hasNext();
            }

            public Object nextElement() {
                return it.next();
            }

        };
        Enumeration initParamNames = new Enumeration() {

            List keys = Arrays.asList(new Object[] { "key3" });

            Iterator it = keys.iterator();

            public boolean hasMoreElements() {
                return it.hasNext();
            }

            public Object nextElement() {
                return it.next();
            }

        };

        mockPortletContext.stubs().method("getAttributeNames").will(
                returnValue(names));
        mockPortletContext.stubs().method("getInitParameterNames").will(
                returnValue(initParamNames));
        mockPortletContext.stubs().method("getAttribute").with(eq("key1"))
                .will(returnValue("value1"));
        mockPortletContext.stubs().method("getAttribute").with(eq("key2"))
                .will(returnValue("value2"));
        mockPortletContext.stubs().method("getInitParameter").with(eq("key3"))
                .will(returnValue("value3"));

        PortletApplicationMap map = new PortletApplicationMap(portletContext);
        Set entries = map.entrySet();

        assertEquals(3, entries.size());
        Iterator it = entries.iterator();
        Map.Entry entry = (Map.Entry) it.next();
        assertEquals("key2", entry.getKey());
        assertEquals("value2", entry.getValue());
        entry = (Map.Entry) it.next();
        assertEquals("key1", entry.getKey());
        assertEquals("value1", entry.getValue());
        entry = (Map.Entry) it.next();
        assertEquals("key3", entry.getKey());
        assertEquals("value3", entry.getValue());

    }

    public void testClear() {
        
        mockPortletContext.expects(once()).method("removeAttribute").with(eq("key1"));
        mockPortletContext.expects(once()).method("removeAttribute").with(eq("key2"));

        ArrayList dummy = new ArrayList();
        dummy.add("key1");
        dummy.add("key2");

        mockPortletContext.expects(once()).method("getAttributeNames").will(
                returnValue(Collections.enumeration(dummy)));

        PortletApplicationMap map = new PortletApplicationMap(portletContext);
        map.clear();
    }
}
