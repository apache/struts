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
import java.util.Iterator;
import java.util.List;

import org.apache.struts2.util.SubsetIteratorFilter.Decider;
import org.apache.struts2.views.jsp.iterator.SubsetIteratorTag;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


/**
 * Test case for SubsetIteratorTag.
 *
 */
public class SubsetIteratorTagTest extends AbstractTagTest {


    public void testBasic() throws Exception {
        { // List as Source
            SubsetIteratorTag tag = new SubsetIteratorTag();
            tag.setPageContext(pageContext);
            tag.setSource("myList");

            tag.doStartTag();
            Iterator subsetIterator = (Iterator) stack.findValue("top");
            tag.doEndTag();

            assertEquals(subsetIterator.next(), new Integer(1));
            assertEquals(subsetIterator.next(), new Integer(2));
            assertEquals(subsetIterator.next(), new Integer(3));
            assertEquals(subsetIterator.next(), new Integer(4));
            assertEquals(subsetIterator.next(), new Integer(5));
        }

        { // Array as Source
            SubsetIteratorTag tag = new SubsetIteratorTag();
            tag.setPageContext(pageContext);
            tag.setSource("myArray");

            tag.doStartTag();
            Iterator subsetIterator = (Iterator) stack.findValue("top");
            tag.doEndTag();

            assertEquals(subsetIterator.next(), new Integer(1));
            assertEquals(subsetIterator.next(), new Integer(2));
            assertEquals(subsetIterator.next(), new Integer(3));
            assertEquals(subsetIterator.next(), new Integer(4));
            assertEquals(subsetIterator.next(), new Integer(5));
        }
    }

    public void testWithStartAttribute() throws Exception {
        { // List as source
            SubsetIteratorTag tag = new SubsetIteratorTag();
            tag.setPageContext(pageContext);
            tag.setSource("myList");
            tag.setStart("3");

            tag.doStartTag();
            Iterator subsetIterator = (Iterator) stack.findValue("top");
            tag.doEndTag();

            assertEquals(subsetIterator.next(), new Integer(4));
            assertEquals(subsetIterator.next(), new Integer(5));
        }

        { // Array as source
            SubsetIteratorTag tag = new SubsetIteratorTag();
            tag.setPageContext(pageContext);
            tag.setSource("myArray");
            tag.setStart("3");

            tag.doStartTag();
            Iterator subsetIterator = (Iterator) stack.findValue("top");
            tag.doEndTag();

            assertEquals(subsetIterator.next(), new Integer(4));
            assertEquals(subsetIterator.next(), new Integer(5));
        }
    }

    public void testWithCountAttribute() throws Exception {
        { // List as source
            SubsetIteratorTag tag = new SubsetIteratorTag();
            tag.setPageContext(pageContext);
            tag.setSource("myList");
            tag.setCount("3");

            tag.doStartTag();
            Iterator subsetIterator = (Iterator) stack.findValue("top");
            tag.doEndTag();

            assertEquals(subsetIterator.next(), new Integer(1));
            assertEquals(subsetIterator.next(), new Integer(2));
            assertEquals(subsetIterator.next(), new Integer(3));
        }

        { // array as source
            SubsetIteratorTag tag = new SubsetIteratorTag();
            tag.setPageContext(pageContext);
            tag.setSource("myArray");
            tag.setCount("3");

            tag.doStartTag();
            Iterator subsetIterator = (Iterator) stack.findValue("top");
            tag.doEndTag();

            assertEquals(subsetIterator.next(), new Integer(1));
            assertEquals(subsetIterator.next(), new Integer(2));
            assertEquals(subsetIterator.next(), new Integer(3));
        }
    }

    public void testWIthStartAndCountAttribute() throws Exception {
        { // List as source
            SubsetIteratorTag tag = new SubsetIteratorTag();
            tag.setPageContext(pageContext);
            tag.setSource("myList");
            tag.setStart("3");
            tag.setCount("3");

            tag.doStartTag();
            Iterator subsetIterator = (Iterator) stack.findValue("top");
            tag.doEndTag();

            assertEquals(subsetIterator.next(), new Integer("4"));
            assertEquals(subsetIterator.next(), new Integer("5"));
        }

        {   // Array as source
            SubsetIteratorTag tag = new SubsetIteratorTag();
            tag.setPageContext(pageContext);
            tag.setSource("myArray");
            tag.setStart("3");
            tag.setCount("3");

            tag.doStartTag();
            Iterator subsetIterator = (Iterator) stack.findValue("top");
            tag.doEndTag();

            assertEquals(subsetIterator.next(), new Integer("4"));
            assertEquals(subsetIterator.next(), new Integer("5"));
        }
    }

    public void testWithId() throws Exception {
        {   // List as Source
            SubsetIteratorTag tag = new SubsetIteratorTag();
            tag.setPageContext(pageContext);
            tag.setSource("myList");
            tag.setId("myPageContextId1");

            tag.doStartTag();
            Iterator subsetIterator1 = (Iterator) stack.findValue("top");
            tag.doEndTag();

            Iterator subsetIterator2 = (Iterator) pageContext.getAttribute("myPageContextId1");

            assertNotNull(subsetIterator1);
            assertNotNull(subsetIterator2);
            assertEquals(subsetIterator1, subsetIterator2);
            assertEquals(subsetIterator2.next(), new Integer(1));
            assertEquals(subsetIterator2.next(), new Integer(2));
            assertEquals(subsetIterator2.next(), new Integer(3));
            assertEquals(subsetIterator2.next(), new Integer(4));
            assertEquals(subsetIterator2.next(), new Integer(5));
        }

        {   // Array as source
            SubsetIteratorTag tag = new SubsetIteratorTag();
            tag.setPageContext(pageContext);
            tag.setSource("myArray");
            tag.setId("myPageContextId2");

            tag.doStartTag();
            Iterator subsetIterator1 = (Iterator) stack.findValue("top");
            tag.doEndTag();

            Iterator subsetIterator2 = (Iterator) pageContext.getAttribute("myPageContextId2");

            assertNotNull(subsetIterator1);
            assertNotNull(subsetIterator2);
            assertEquals(subsetIterator1, subsetIterator2);
            assertEquals(subsetIterator2.next(), new Integer(1));
            assertEquals(subsetIterator2.next(), new Integer(2));
            assertEquals(subsetIterator2.next(), new Integer(3));
            assertEquals(subsetIterator2.next(), new Integer(4));
            assertEquals(subsetIterator2.next(), new Integer(5));
        }
    }

    public void testWithDecider() throws Exception {
        {   // List as source
            SubsetIteratorTag tag = new SubsetIteratorTag();
            tag.setPageContext(pageContext);
            tag.setSource("myList");
            tag.setDecider("myDecider");

            tag.doStartTag();
            Iterator subsetIterator = (Iterator) stack.findValue("top");
            tag.doEndTag();

            assertEquals(subsetIterator.next(), new Integer(2));
            assertEquals(subsetIterator.next(), new Integer(4));
        }

        {   // Array As source
            SubsetIteratorTag tag = new SubsetIteratorTag();
            tag.setPageContext(pageContext);
            tag.setSource("myList");
            tag.setDecider("myDecider");

            tag.doStartTag();
            Iterator subsetIterator = (Iterator) stack.findValue("top");
            tag.doEndTag();

            assertEquals(subsetIterator.next(), new Integer(2));
            assertEquals(subsetIterator.next(), new Integer(4));
        }
    }



    public Action getAction() {
        return new ActionSupport() {
            public List getMyList() {
                List l = new ArrayList();
                l.add(new Integer(1));
                l.add(new Integer(2));
                l.add(new Integer(3));
                l.add(new Integer(4));
                l.add(new Integer(5));
                return l;
            }

            public Integer[] getMyArray() {
                Integer[] integers = new Integer[5];
                integers[0] = new Integer(1);
                integers[1] = new Integer(2);
                integers[2] = new Integer(3);
                integers[3] = new Integer(4);
                integers[4] = new Integer(5);
                return integers;
            }

            public Decider getMyDecider() {
                return new Decider() {
                    public boolean decide(Object element) throws Exception {
                        int integer = ((Integer)element).intValue();
                        return (((integer % 2) == 0)?true:false);
                    }
                };
            }
        };
    }
}
