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

import javax.servlet.jsp.JspException;


/**
 */
public class BeanTagTest extends AbstractUITagTest {

    public void testSimple() {
        BeanTag tag = new BeanTag();
        tag.setPageContext(pageContext);
        tag.setName("org.apache.struts2.TestAction");

        try {
            tag.doStartTag();
            tag.component.addParameter("result", "success");

            assertEquals("success", stack.findValue("result"));
            // TestAction from bean tag, Action from execution and DefaultTextProvider
            assertEquals(3, stack.size());
            tag.doEndTag();
            assertEquals(2, stack.size());
        } catch (JspException ex) {
            ex.printStackTrace();
            fail();
        }

        request.verify();
        pageContext.verify();
    }
}
