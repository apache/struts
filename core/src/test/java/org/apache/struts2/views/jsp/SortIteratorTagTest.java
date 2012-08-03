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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.apache.struts2.views.jsp.iterator.SortIteratorTag;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Test case to test SortIteratorTag.
 *
 */
public class SortIteratorTagTest extends AbstractTagTest {

    public void testSortWithoutId() throws Exception {
        SortIteratorTag tag = new SortIteratorTag();

        tag.setComparator("comparator");
        tag.setSource("source");

        tag.setPageContext(pageContext);
        tag.doStartTag();

        // if not an Iterator, just let the ClassCastException be thrown as error instead of failure
        Iterator sortedIterator = (Iterator) stack.findValue("top");

        assertNotNull(sortedIterator);
        // 1
        assertTrue(sortedIterator.hasNext());
        assertEquals(sortedIterator.next(), new Integer(1));
        // 2
        assertTrue(sortedIterator.hasNext());
        assertEquals(sortedIterator.next(), new Integer(2));
        // 3.
        assertTrue(sortedIterator.hasNext());
        assertEquals(sortedIterator.next(), new Integer(3));
        // 4.
        assertTrue(sortedIterator.hasNext());
        assertEquals(sortedIterator.next(), new Integer(4));
        // 5
        assertTrue(sortedIterator.hasNext());
        assertEquals(sortedIterator.next(), new Integer(5));

        assertFalse(sortedIterator.hasNext());
        tag.doEndTag();
    }

    public void testSortWithIdIteratorAvailableInStackTop() throws Exception {

        SortIteratorTag tag = new SortIteratorTag();

        tag.setId("myId");
        tag.setComparator("comparator");
        tag.setSource("source");

        tag.setPageContext(pageContext);
        tag.doStartTag();

        {
            Iterator sortedIterator = (Iterator) stack.findValue("top");

            assertNotNull(sortedIterator);
            // 1
            assertTrue(sortedIterator.hasNext());
            assertEquals(sortedIterator.next(), new Integer(1));
            // 2
            assertTrue(sortedIterator.hasNext());
            assertEquals(sortedIterator.next(), new Integer(2));
            // 3
            assertTrue(sortedIterator.hasNext());
            assertEquals(sortedIterator.next(), new Integer(3));
            // 4
            assertTrue(sortedIterator.hasNext());
            assertEquals(sortedIterator.next(), new Integer(4));
            // 5
            assertTrue(sortedIterator.hasNext());
            assertEquals(sortedIterator.next(), new Integer(5));

            assertFalse(sortedIterator.hasNext());
        }

        tag.doEndTag();
    }


    public void testSortWithIdIteratorAvailableInPageContext() throws Exception {
        SortIteratorTag tag = new SortIteratorTag();

        tag.setId("myId");
        tag.setComparator("comparator");
        tag.setSource("source");

        tag.setPageContext(pageContext);
        tag.doStartTag();

        {
            Iterator sortedIterator = (Iterator) pageContext.getAttribute("myId");

            assertNotNull(sortedIterator);
            // 1
            assertTrue(sortedIterator.hasNext());
            assertEquals(sortedIterator.next(), new Integer(1));
            // 2
            assertTrue(sortedIterator.hasNext());
            assertEquals(sortedIterator.next(), new Integer(2));
            // 3
            assertTrue(sortedIterator.hasNext());
            assertEquals(sortedIterator.next(), new Integer(3));
            // 4
            assertTrue(sortedIterator.hasNext());
            assertEquals(sortedIterator.next(), new Integer(4));
            // 5
            assertTrue(sortedIterator.hasNext());
            assertEquals(sortedIterator.next(), new Integer(5));

            assertFalse(sortedIterator.hasNext());
        }

        tag.doEndTag();
    }

    public void testSortWithIllegalSource() throws Exception {
        SortIteratorTag tag = new SortIteratorTag();

        tag.setComparator("comparator");
        tag.setSource("badSource");

        try {
            tag.setPageContext(pageContext);
            tag.doStartTag();
            tag.doEndTag();
            fail("JspException expected");
        }
        catch (JspException e) {
            // ok
            assertTrue(true);
        }
    }

    public void testSortWithIllegalComparator() throws Exception {
        SortIteratorTag tag = new SortIteratorTag();

        tag.setComparator("badComparator");
        tag.setSource("source");

        try {
            tag.setPageContext(pageContext);
            tag.doStartTag();
            tag.doEndTag();
            fail("JspException expected");
        }
        catch (JspException e) {
            // good
            assertTrue(true);
        }

    }

    public Action getAction() {
        return new ActionSupport() {
            public Comparator getComparator() {
                return new Comparator() {
                    public int compare(Object o1, Object o2) {
                        Integer i1 = (Integer) o1;
                        Integer i2 = (Integer) o2;

                        return (i1.intValue() - i2.intValue());
                    }
                };
            }

            public List getSource() {
                List l = new ArrayList();
                l.add(new Integer(3));
                l.add(new Integer(1));
                l.add(new Integer(2));
                l.add(new Integer(5));
                l.add(new Integer(4));
                return l;
            }

            public Object getBadComparator() {
                return new Object();
            }

            public Object getBadSource() {
                return new Object();
            }
        };
    }
}
