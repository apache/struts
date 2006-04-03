/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp;

import org.apache.struts.action2.views.jsp.iterator.SortIteratorTag;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;

/**
 * Test case to test SortIteratorTag.
 *
 * @author tm_jee (tm_jee(at)yahoo.co.uk)
 * @version $Date: 2005/11/21 16:28:26 $ $Id: SortIteratorTagTest.java,v 1.2 2005/11/21 16:28:26 tmjee Exp $
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
