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
package org.apache.struts2.views.jsp.ui;

import org.apache.struts2.TestAction;
import org.apache.struts2.views.jsp.AbstractUITagTest;

import java.util.ArrayList;
import java.util.List;

public class InputTransferSelectTagTest extends AbstractUITagTest {

    public void testWithRequired() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("Item One");
        list.add("Item Two");

        TestAction testaction = (TestAction) action;
        testaction.setCollection(list);

        InputTransferSelectTag tag = new InputTransferSelectTag();
        tag.setPageContext(pageContext);

        tag.setName("collection");
        tag.setList("collection");
        stack.getActionContext().getSession().put("nonce", "r4nd0m");

        tag.doStartTag();
        tag.doEndTag();

        verify(InputTransferSelectTagTest.class.getResource("inputtransferselect-1.txt"));
    }

    public void testDynamicAttributes() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("Item One");
        list.add("Item Two");

        TestAction testaction = (TestAction) action;
        testaction.setCollection(list);

        InputTransferSelectTag tag = new InputTransferSelectTag();
        tag.setPageContext(pageContext);
        tag.setDynamicAttribute(null, "input-collection-name", "inputName");
        tag.setDynamicAttribute(null, "collection-name", "collectionName");

        tag.setName("collection");
        tag.setList("collection");

        tag.doStartTag();
        tag.doEndTag();

        verify(InputTransferSelectTagTest.class.getResource("inputtransferselect-2.txt"));
    }
}
