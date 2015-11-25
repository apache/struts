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

import org.apache.struts2.views.jsp.iterator.MergeIteratorTag;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Test case for MergeIteratorTag.
 *
 */
public class MergeIteratorTagTest extends AbstractTagTest {

    public void testMergingIteratorWithArrayAsSource() throws Exception {
        MergeIteratorTag tag = new MergeIteratorTag();
        tag.setPageContext(pageContext);
        tag.setVar("myMergedIterator");

        ParamTag iterator1ParamTag = new ParamTag();
        iterator1ParamTag.setPageContext(pageContext);
        iterator1ParamTag.setValue("myArr1");

        ParamTag iterator2ParamTag = new ParamTag();
        iterator2ParamTag.setPageContext(pageContext);
        iterator2ParamTag.setValue("myArr2");

        ParamTag iterator3ParamTag = new ParamTag();
        iterator3ParamTag.setPageContext(pageContext);
        iterator3ParamTag.setValue("myArr3");


        tag.doStartTag();
        iterator1ParamTag.doStartTag();
        iterator1ParamTag.doEndTag();
        iterator2ParamTag.doStartTag();
        iterator2ParamTag.doEndTag();
        iterator3ParamTag.doStartTag();
        iterator3ParamTag.doEndTag();
        tag.doEndTag();

        Iterator mergedIterator = (Iterator) stack.findValue("#myMergedIterator"); // if not iterator, let CCE surface

        assertNotNull(mergedIterator);
        assertTrue(mergedIterator.hasNext());
        assertEquals(mergedIterator.next(), "1");
        assertTrue(mergedIterator.hasNext());
        assertEquals(mergedIterator.next(), "a");
        assertTrue(mergedIterator.hasNext());
        assertEquals(mergedIterator.next(), "A");
        assertTrue(mergedIterator.hasNext());
        assertEquals(mergedIterator.next(), "2");
        assertTrue(mergedIterator.hasNext());
        assertEquals(mergedIterator.next(), "b");
        assertTrue(mergedIterator.hasNext());
        assertEquals(mergedIterator.next(), "B");
        assertTrue(mergedIterator.hasNext());
        assertEquals(mergedIterator.next(), "3");
        assertTrue(mergedIterator.hasNext());
        assertEquals(mergedIterator.next(), "c");
        assertTrue(mergedIterator.hasNext());
        assertEquals(mergedIterator.next(), "C");
        assertFalse(mergedIterator.hasNext());
    }


    public void testMergingIteratorsWithListAsSource() throws Exception {
        MergeIteratorTag tag = new MergeIteratorTag();
        tag.setPageContext(pageContext);
        tag.setVar("myMergedIterator");

        ParamTag iterator1ParamTag = new ParamTag();
        iterator1ParamTag.setPageContext(pageContext);
        iterator1ParamTag.setValue("myList1");

        ParamTag iterator2ParamTag = new ParamTag();
        iterator2ParamTag.setPageContext(pageContext);
        iterator2ParamTag.setValue("myList2");

        ParamTag iterator3ParamTag = new ParamTag();
        iterator3ParamTag.setPageContext(pageContext);
        iterator3ParamTag.setValue("myList3");


        tag.doStartTag();
        iterator1ParamTag.doStartTag();
        iterator1ParamTag.doEndTag();
        iterator2ParamTag.doStartTag();
        iterator2ParamTag.doEndTag();
        iterator3ParamTag.doStartTag();
        iterator3ParamTag.doEndTag();
        tag.doEndTag();

        Iterator mergedIterator = (Iterator) stack.findValue("#myMergedIterator"); // if not iterator, let CCE surface

        assertNotNull(mergedIterator);
        assertTrue(mergedIterator.hasNext());
        assertEquals(mergedIterator.next(), "1");
        assertTrue(mergedIterator.hasNext());
        assertEquals(mergedIterator.next(), "a");
        assertTrue(mergedIterator.hasNext());
        assertEquals(mergedIterator.next(), "A");
        assertTrue(mergedIterator.hasNext());
        assertEquals(mergedIterator.next(), "2");
        assertTrue(mergedIterator.hasNext());
        assertEquals(mergedIterator.next(), "b");
        assertTrue(mergedIterator.hasNext());
        assertEquals(mergedIterator.next(), "B");
        assertTrue(mergedIterator.hasNext());
        assertEquals(mergedIterator.next(), "3");
        assertTrue(mergedIterator.hasNext());
        assertEquals(mergedIterator.next(), "c");
        assertTrue(mergedIterator.hasNext());
        assertEquals(mergedIterator.next(), "C");
        assertFalse(mergedIterator.hasNext());
    }




    public Action getAction() {
        return new ActionSupport() {
            public List getMyList1() {
                List l = new ArrayList();
                l.add("1");
                l.add("2");
                l.add("3");
                return l;
            }

            public List getMyList2() {
                List l = new ArrayList();
                l.add("a");
                l.add("b");
                l.add("c");
                return l;
            }

            public List getMyList3() {
                List l = new ArrayList();
                l.add("A");
                l.add("B");
                l.add("C");
                return l;
            }

            public String[] getMyArr1() {
                return new String[] { "1", "2", "3" };
            }

            public String[] getMyArr2() {
                return new String[] { "a", "b", "c" };
            }

            public String[] getMyArr3() {
                return new String[] {"A", "B", "C"};
            }
        };
    }
}
