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
package org.apache.struts2.dispatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.mock.web.MockHttpServletRequest;

import junit.framework.TestCase;

import com.mockobjects.constraint.Constraint;
import com.mockobjects.constraint.IsAnything;
import com.mockobjects.constraint.IsEqual;
import com.mockobjects.dynamic.Mock;
import java.util.AbstractMap;


/**
 */
public class SessionMapTest extends TestCase {

    private Mock requestMock;
    private Mock sessionMock;


    public void testClearInvalidatesTheSession() throws Exception {
        List<String> attributeNames = new ArrayList<>();
        attributeNames.add("test");
        attributeNames.add("anotherTest");
        Enumeration<String> attributeNamesEnum = Collections.enumeration(attributeNames);

        MockSessionMap sessionMap = new MockSessionMap((HttpServletRequest) requestMock.proxy());
        // Note: getAttribute() calls no longer expected after fix to ensure descendant (not ancestor) calls are made for
        //   the SessionMap Map methods (i.e. the overrides are called, as expected).
        sessionMock.expect("setAttribute",
                new Constraint[] {
                    new IsEqual("test"), new IsEqual("test value")
                });
        sessionMock.expect("setAttribute",
                new Constraint[] {
                    new IsEqual("anotherTest"), new IsEqual("another test value")
                });
        sessionMock.expectAndReturn("getAttributeNames", attributeNamesEnum);
        sessionMock.expect("removeAttribute",
                new Constraint[]{
                    new IsEqual("test")
                });
        sessionMock.expect("removeAttribute",
                new Constraint[]{
                    new IsEqual("anotherTest")
                });
        sessionMap.put("test", "test value");
        sessionMap.put("anotherTest", "another test value");
        sessionMap.clear();
        assertNull(sessionMap.get("test"));
        assertNull(sessionMap.get("anotherTest"));
        sessionMock.verify();
    }

    public void testGetOnSessionMapUsesWrappedSessionsGetAttribute() throws Exception {
        Object value = new Object();
        sessionMock.expectAndReturn("getAttribute", new Constraint[]{
                new IsEqual("KEY")
        }, value);

        SessionMap sessionMap = new SessionMap((HttpServletRequest) requestMock.proxy());
        assertEquals("Expected the get using KEY to return the value object setup in the mockSession", value, sessionMap.get("KEY"));
        sessionMock.verify();
    }

    public void testPutOnSessionMapUsesWrappedSessionsSetsAttribute() throws Exception {
        Object value = new Object();
        sessionMock.expect("getAttribute", new Constraint[]{new IsAnything()});
        sessionMock.expect("setAttribute", new Constraint[]{
                new IsEqual("KEY"), new IsEqual(value)
        });

        SessionMap sessionMap = new SessionMap((HttpServletRequest) requestMock.proxy());
        sessionMap.put("KEY", value);
        sessionMock.verify();
    }

    public void testGetObjectOnSessionMapUsesWrappedSessionsGetAttributeWithStringValue() throws Exception {
        String key = "theKey";
        Object value = new Object();
        sessionMock.expectAndReturn("getAttribute", new Constraint[]{
                new IsEqual(key)
        }, value);

        SessionMap sessionMap = new SessionMap((HttpServletRequest) requestMock.proxy());
        assertEquals("Expected the get using KEY to return the value object setup in the mockSession", value, sessionMap.get(key));
        sessionMock.verify();
    }

    public void testPutObjectOnSessionMapUsesWrappedSessionsSetsAttributeWithStringValue() throws Exception {
        String key = "theKey";
        Object value = new Object();
        sessionMock.expect("getAttribute", new Constraint[]{new IsAnything()});
        sessionMock.expect("setAttribute", new Constraint[]{
                new IsEqual(key), new IsEqual(value)
        });

        SessionMap sessionMap = new SessionMap((HttpServletRequest) requestMock.proxy());
        sessionMap.put(key, value);
        sessionMock.verify();
    }
    
    public void testContainsKeyWillFindAnObjectPutOnSessionMap() throws Exception {
    	
    	MockHttpServletRequest request = new MockHttpServletRequest();
    	
        String key = "theKey";
        Object value = new Object();
        
        SessionMap sessionMap = new SessionMap(request);
        sessionMap.put(key, value);
        assertTrue(sessionMap.containsKey(key));
    }

    public void testContainsKeyWillReturnFalseIfObjectNotFoundOnSessionMap() throws Exception {
    	
    	MockHttpServletRequest request = new MockHttpServletRequest();
    	
        String key = "theKey";
        String someOtherKey = "someOtherKey";
        Object value = new Object();
        
        SessionMap sessionMap = new SessionMap(request);
        sessionMap.put(key, value);
        
        assertFalse(sessionMap.containsKey(someOtherKey));
    }

    public void testPuttingObjectInMapReturnsNullForPreviouslyUnusedKey() throws Exception {
        Object value = new Object();
        sessionMock.expectAndReturn("getAttribute", new Constraint[]{
                new IsEqual("KEY")
        }, null);
        sessionMock.expect("setAttribute", new Constraint[]{
                new IsEqual("KEY"), new IsEqual(value)
        });

        SessionMap sessionMap = new SessionMap((HttpServletRequest) requestMock.proxy());
        assertNull("should be null, as the contract for Map says that put returns the previous value in the map for the key", sessionMap.put("KEY", value));
        sessionMock.verify();
    }

    public void testPuttingObjectInMapReturnsPreviousValueForKey() throws Exception {
        Object originalValue = new Object();
        Object value = new Object();
        sessionMock.expectAndReturn("getAttribute", new Constraint[]{
                new IsEqual("KEY")
        }, null);
        sessionMock.expect("setAttribute", new Constraint[]{
                new IsEqual("KEY"), new IsEqual(originalValue)
        });
        sessionMock.expectAndReturn("getAttribute", new Constraint[]{
                new IsEqual("KEY")
        }, originalValue);
        sessionMock.expect("setAttribute", new Constraint[]{
                new IsEqual("KEY"), new IsEqual(value)
        });

        SessionMap sessionMap = new SessionMap((HttpServletRequest) requestMock.proxy());
        sessionMap.put("KEY", originalValue);
        assertEquals("should be the OriginalValue, as the contract for Map says that put returns the previous value in the map for the key", originalValue, sessionMap.put("KEY", value));
        sessionMock.verify();
    }

    public void testRemovePassThroughCallToRemoveAttribute() throws Exception {
        Object value = new Object();
        sessionMock.expectAndReturn("getAttribute", new Constraint[]{
                new IsEqual("KEY")
        }, value);
        sessionMock.expect("removeAttribute", new Constraint[]{
                new IsEqual("KEY")
        });

        SessionMap sessionMap = new SessionMap((HttpServletRequest) requestMock.proxy());
        assertEquals(value, sessionMap.remove("KEY"));
        sessionMock.verify();
    }

    /** 
     * Attempt to detect any changes that would make the attribute handling for puts produce different results
     * for the SessionMap and underlying HttpSession attributes.
     * 
     * @throws Exception 
     */
    public void testPutResultInSessionAttributes() throws Exception {
        Object value = new Object();

        //HttpSession httpSessionMock = ((HttpServletRequest) requestMock.proxy()).getSession(false);
        HttpSession httpSessionMock = (HttpSession) sessionMock.proxy();
        sessionMock.expectAndReturn("getAttribute", new Constraint[]{
                new IsEqual("KEY")
        }, null);
        sessionMock.expect("setAttribute", new Constraint[]{
                new IsEqual("KEY"), new IsEqual(value)
        });
        sessionMock.expectAndReturn("getAttribute", new Constraint[]{
                new IsEqual("KEY")
        }, value);
        sessionMock.expectAndReturn("getAttribute", new Constraint[]{
                new IsEqual("KEY")
        }, value);

        SessionMap sessionMap = new SessionMap((HttpServletRequest) requestMock.proxy());
        AbstractMap<String, Object> abstractMap = (AbstractMap<String, Object>) sessionMap;
        abstractMap.put("KEY", value);
        assertEquals("Underlying HttpSession attribute does not match after SessionMap put ?", abstractMap.get("KEY"), httpSessionMock.getAttribute("KEY"));
        sessionMock.verify();
    }

    /**
     * Attempt to detect any changes that would make the attribute handling for removes produce different results
     * for the SessionMap and underlying HttpSession attributes.
     * 
     * @throws Exception 
     */
    public void testRemoveResultInSessionAttributes() throws Exception {
        Object value = new Object();
        Object removedValue;

        //HttpSession httpSessionMock = ((HttpServletRequest) requestMock.proxy()).getSession(false);
        HttpSession httpSessionMock = (HttpSession) sessionMock.proxy();
        sessionMock.expectAndReturn("getAttribute", new Constraint[]{
                new IsEqual("KEY")
        }, null);
        sessionMock.expect("setAttribute", new Constraint[]{
                new IsEqual("KEY"), new IsEqual(value)
        });
        sessionMock.expectAndReturn("getAttribute", new Constraint[]{
                new IsEqual("KEY")
        }, value);
        sessionMock.expect("removeAttribute", new Constraint[]{
                new IsEqual("KEY")
        });
        sessionMock.expectAndReturn("getAttribute", new Constraint[]{
                new IsEqual("KEY")
        }, null);
        sessionMock.expectAndReturn("getAttribute", new Constraint[]{
                new IsEqual("KEY")
        }, null);

        SessionMap sessionMap = new SessionMap((HttpServletRequest) requestMock.proxy());
        AbstractMap<String, Object> abstractMap = (AbstractMap<String, Object>) sessionMap;
        abstractMap.put("KEY", value);
        removedValue = abstractMap.remove("KEY");
        assertEquals("Removed attribute not equal to put attribute ?", value, removedValue);
        assertNull("Removed attribute still present in SessionMap ?", abstractMap.get("KEY"));
        assertNull("Removed attribute still present in HttpSessionMock ?", httpSessionMock.getAttribute("KEY"));
        sessionMock.verify();
    }

    @Override
    protected void setUp() throws Exception {
        sessionMock = new Mock(HttpSession.class);
        sessionMock.matchAndReturn("getId", "1");
        requestMock = new Mock(HttpServletRequest.class);
        requestMock.matchAndReturn("getSession", new Constraint[]{new IsEqual(Boolean.FALSE)}, sessionMock.proxy());
    }


    /**
     * class that extends session map, making the values available in a local map -- useful
     * for confirming put and get calls in the superclass. ie useful for testing that the get is done before
     * putting new data into the map.
     */
    private class MockSessionMap extends SessionMap {

        private static final long serialVersionUID = 8783604360786273764L;

        private final Map<String, Object> map;

        public MockSessionMap(HttpServletRequest request) {
            super(request);
            this.map = new HashMap<>();
        }

        @Override
        public Object get(Object key) {
            return map.get(key);
        }
        
        @Override
        public Object put(String key, Object value) {
            Object originalValue = super.put(key, value);
            map.put(key, value); //put the value into our map after putting it in the superclass map to avoid polluting the get call.

            return originalValue;
        }

        @Override
        public void clear() {
            super.clear();
            map.clear();
        }
    }
}
