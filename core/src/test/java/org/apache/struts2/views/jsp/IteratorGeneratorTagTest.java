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

import java.util.Iterator;

import org.apache.struts2.util.IteratorGenerator.Converter;
import org.apache.struts2.views.jsp.iterator.IteratorGeneratorTag;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Test case for IteratorGeneratorTag.
 *
 */
public class IteratorGeneratorTagTest extends AbstractTagTest {

    public void testGeneratorBasic() throws Exception {
        IteratorGeneratorTag tag = new IteratorGeneratorTag();

        tag.setPageContext(pageContext);
        tag.setVal("%{'aaa,bbb,ccc,ddd,eee'}");
        tag.doStartTag();
        Object topOfStack = stack.findValue("top");


        assertTrue(topOfStack instanceof Iterator);
        // 1
        assertTrue(((Iterator)topOfStack).hasNext());
        assertEquals(((Iterator)topOfStack).next(), "aaa");
        // 2
        assertTrue(((Iterator)topOfStack).hasNext());
        assertEquals(((Iterator)topOfStack).next(), "bbb");
        // 3
        assertTrue(((Iterator)topOfStack).hasNext());
        assertEquals(((Iterator)topOfStack).next(), "ccc");
        // 4
        assertTrue(((Iterator)topOfStack).hasNext());
        assertEquals(((Iterator)topOfStack).next(), "ddd");
        // 5
        assertTrue(((Iterator)topOfStack).hasNext());
        assertEquals(((Iterator)topOfStack).next(),"eee");

        assertFalse(((Iterator)topOfStack).hasNext());

        tag.doEndTag();
        Object afterTopOfStack = stack.findValue("top");


        assertNotSame(afterTopOfStack, topOfStack);
    }

    public void testGeneratorWithSeparator() throws Exception {
        IteratorGeneratorTag tag = new IteratorGeneratorTag();

        tag.setPageContext(pageContext);
        tag.setVal("%{'aaa|bbb|ccc|ddd|eee'}");
        tag.setSeparator("|");
        tag.doStartTag();
        Object topOfStack = stack.findValue("top");
        tag.doEndTag();
        Object afterTopOfStack = stack.findValue("top");

        assertTrue(topOfStack instanceof Iterator);
        // 1
        assertTrue(((Iterator)topOfStack).hasNext());
        assertEquals(((Iterator)topOfStack).next(), "aaa");
        // 2
        assertTrue(((Iterator)topOfStack).hasNext());
        assertEquals(((Iterator)topOfStack).next(), "bbb");
        // 3
        assertTrue(((Iterator)topOfStack).hasNext());
        assertEquals(((Iterator)topOfStack).next(), "ccc");
        // 4
        assertTrue(((Iterator)topOfStack).hasNext());
        assertEquals(((Iterator)topOfStack).next(), "ddd");
        // 5
        assertTrue(((Iterator)topOfStack).hasNext());
        assertEquals(((Iterator)topOfStack).next(), "eee");

        assertFalse(((Iterator)topOfStack).hasNext());
        assertNotSame(afterTopOfStack, topOfStack);
    }

    public void testGeneratorWithConverter() throws Exception {
        IteratorGeneratorTag tag = new IteratorGeneratorTag();

        tag.setPageContext(pageContext);
        tag.setVal("%{'aaa, bbb, ccc, ddd, eee'}");
        tag.setConverter("myConverter");
        tag.doStartTag();
        Object topOfStack = stack.findValue("top");
        tag.doEndTag();
        Object afterTopOfStack = stack.findValue("top");

        assertTrue(topOfStack instanceof Iterator);
        // 1.
        assertTrue(((Iterator)topOfStack).hasNext());
        assertEquals(((Iterator)topOfStack).next(), "myConverter-aaa");
        // 2
        assertTrue(((Iterator)topOfStack).hasNext());
        assertEquals(((Iterator)topOfStack).next(), "myConverter-bbb");
        // 3
        assertTrue(((Iterator)topOfStack).hasNext());
        assertEquals(((Iterator)topOfStack).next(), "myConverter-ccc");
        // 4.
        assertTrue(((Iterator)topOfStack).hasNext());
        assertEquals(((Iterator)topOfStack).next(), "myConverter-ddd");
        // 5.
        assertTrue(((Iterator)topOfStack).hasNext());
        assertEquals(((Iterator)topOfStack).next(), "myConverter-eee");

        assertFalse(((Iterator)topOfStack).hasNext());
        assertNotSame(afterTopOfStack, topOfStack);
    }

    public void testGeneratorWithId() throws Exception {
        IteratorGeneratorTag tag = new IteratorGeneratorTag();
        tag.setPageContext(pageContext);
        tag.setVal("%{'aaa,bbb,ccc,ddd,eee'}");
        tag.setId("myPageContextAttId");
        tag.doStartTag();
        tag.doEndTag();

        Object pageContextIterator = stack.findValue("myPageContextAttId");

        assertTrue(pageContextIterator instanceof Iterator);
        // 1
        assertTrue(((Iterator)pageContextIterator).hasNext());
        assertEquals(((Iterator)pageContextIterator).next(), "aaa");
        // 2.
        assertTrue(((Iterator)pageContextIterator).hasNext());
        assertEquals(((Iterator)pageContextIterator).next(), "bbb");
        // 3.
        assertTrue(((Iterator)pageContextIterator).hasNext());
        assertEquals(((Iterator)pageContextIterator).next(), "ccc");
        // 4
        assertTrue(((Iterator)pageContextIterator).hasNext());
        assertEquals(((Iterator)pageContextIterator).next(), "ddd");
        // 5
        assertTrue(((Iterator)pageContextIterator).hasNext());
        assertEquals(((Iterator)pageContextIterator).next(), "eee");

        assertFalse(((Iterator)pageContextIterator).hasNext());
    }

    public void testGeneratorWithCount() throws Exception {
        IteratorGeneratorTag tag = new IteratorGeneratorTag();

        tag.setPageContext(pageContext);
        tag.setVal("%{'aaa,bbb,ccc,ddd,eee'}");
        tag.setCount("myCount");
        tag.doStartTag();
        Object topOfStack = stack.findValue("top");
        tag.doEndTag();
        Object afterTopOfStack = stack.findValue("top");


        assertTrue(topOfStack instanceof Iterator);
        // 1
        assertTrue(((Iterator)topOfStack).hasNext());
        assertEquals(((Iterator)topOfStack).next(), "aaa");
        // 2
        assertTrue(((Iterator)topOfStack).hasNext());
        assertEquals(((Iterator)topOfStack).next(), "bbb");
        // 3.
        assertTrue(((Iterator)topOfStack).hasNext());
        assertEquals(((Iterator)topOfStack).next(), "ccc");

        assertFalse(((Iterator)topOfStack).hasNext());
        assertNotSame(topOfStack, afterTopOfStack);
    }


    public Action getAction() {
        return new ActionSupport() {
            public Converter getMyConverter() {
                return new Converter() {
                    public Object convert(String value) throws Exception {
                        return "myConverter-"+value;
                    }
                };
            }

            public int getMyCount() {
                return 3;
            }
        };
    }
}

