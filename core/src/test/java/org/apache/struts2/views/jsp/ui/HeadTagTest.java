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

package org.apache.struts2.views.jsp.ui;

import org.apache.struts2.views.jsp.AbstractUITagTest;

/**
 * Unit test for {@link HeadTag}.
 * <br>
 * Note: If unit test fails with encoding difference check the src/test/struts.properties
 * and adjust the .txt files accordingly
 *
 */
public class HeadTagTest extends AbstractUITagTest {

    public void testHead1() throws Exception {
        HeadTag tag = new HeadTag();
        tag.setPageContext(pageContext);
        
        tag.doStartTag();
        tag.doEndTag();

        verify(HeadTagTest.class.getResource("HeadTagTest-1.txt"));
    }
}
