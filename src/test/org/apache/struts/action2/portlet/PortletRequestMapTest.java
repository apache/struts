/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.portlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletRequest;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Constraint;


/**
 * PortletRequestMapTest. Insert description.
 * 
 * @author Nils-Helge Garli
 * @version $Revision: 1.3 $ $Date: 2006/03/10 09:39:24 $
 */
public class PortletRequestMapTest extends MockObjectTestCase {

    public void testSetAttribute() {
        
    }
    
    public void testGet() {
        Mock mockRequest = mock(PortletRequest.class, "testGet");
        mockRequest.expects(once()).method("getAttribute").with(eq("testAttribute")).will(returnValue("testValue"));
        PortletRequestMap map = new PortletRequestMap((PortletRequest)mockRequest.proxy());
        String value = (String)map.get("testAttribute");
        mockRequest.verify();
        assertEquals("testValue", value);
    }
    
    public void testPut() {
        Mock mockRequest = mock(PortletRequest.class, "testPut");
        Object value = new String("testValue");
        Constraint[] params = new Constraint[]{eq("testAttribute"), eq(value)};
        mockRequest.expects(once()).method("setAttribute").with(params);
        mockRequest.expects(once()).method("getAttribute").with(eq("testAttribute")).will(returnValue(value));
        PortletRequestMap map = new PortletRequestMap((PortletRequest)mockRequest.proxy());
        Object obj = map.put("testAttribute", value);
        mockRequest.verify();
        assertEquals(obj, value);
    }
    
    public void testClear() {
        Mock mockRequest = mock(PortletRequest.class, "testClear");

        mockRequest.expects(once()).method("removeAttribute").with(eq("a"));
        mockRequest.expects(once()).method("removeAttribute").with(eq("b"));
        
        ArrayList dummy = new ArrayList();
        dummy.add("a");
        dummy.add("b");
        
        mockRequest.expects(once()).method("getAttributeNames").will(returnValue(Collections.enumeration(dummy)));
        
        PortletRequestMap map = new PortletRequestMap((PortletRequest)mockRequest.proxy());
        map.clear();
        mockRequest.verify();
    }
    
    public void testRemove() {
        Mock mockRequest = mock(PortletRequest.class);
        
        PortletRequest req = (PortletRequest)mockRequest.proxy();
        
        
        mockRequest.expects(once()).method("getAttribute").with(eq("dummyKey")).will(returnValue("dummyValue"));
        
        mockRequest.expects(once()).method("removeAttribute").with(eq("dummyKey"));
        
        PortletRequestMap map = new PortletRequestMap(req);
        Object ret = map.remove("dummyKey");
        assertEquals("dummyValue", ret);
    }
    
    public void testEntrySet() {
        Mock mockRequest = mock(PortletRequest.class);
        
        PortletRequest req = (PortletRequest)mockRequest.proxy();
        
        Enumeration names = new Enumeration() {

            List keys = Arrays.asList(new Object[]{"key1", "key2"});
            Iterator it = keys.iterator();
            
            public boolean hasMoreElements() {
                return it.hasNext();
            }

            public Object nextElement() {
                return it.next();
            }
            
        };
        
        mockRequest.stubs().method("getAttributeNames").will(returnValue(names));
        mockRequest.stubs().method("getAttribute").with(eq("key1")).will(returnValue("value1"));
        mockRequest.stubs().method("getAttribute").with(eq("key2")).will(returnValue("value2"));
        
        PortletRequestMap map = new PortletRequestMap(req);
        Set entries = map.entrySet();
        
        assertEquals(2, entries.size());
        Iterator it = entries.iterator();
        Map.Entry entry = (Map.Entry)it.next();
        assertEquals("key2", entry.getKey());
        assertEquals("value2", entry.getValue());
        entry = (Map.Entry)it.next();
        assertEquals("key1", entry.getKey());
        assertEquals("value1", entry.getValue());
        
    }
    
}
