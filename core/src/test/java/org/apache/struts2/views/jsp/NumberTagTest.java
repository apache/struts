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
package org.apache.struts2.views.jsp;

import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.TestAction;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class NumberTagTest extends AbstractTagTest {

    public void testSimpleFloatFormat() throws Exception {
        // given
        context.put(ActionContext.LOCALE, Locale.US);

        TestAction testAction = (TestAction) action;
        testAction.setFloatNumber(120.0f);

        NumberTag tag = new NumberTag();
        tag.setPageContext(pageContext);
        tag.setName("floatNumber");

        // when
        tag.doStartTag();
        tag.doEndTag();

        // then
        assertEquals("120", writer.toString());
    }
    
    public void testSimpleCurrencyUSFormat() throws Exception {
        // given
        context.put(ActionContext.LOCALE, Locale.US);
        
        TestAction testAction = (TestAction) action;
        testAction.setFloatNumber(120.0f);

        NumberTag tag = new NumberTag();
        tag.setPageContext(pageContext);
        tag.setName("floatNumber");
        tag.setType("currency");

        // when
        tag.doStartTag();
        tag.doEndTag();

        // then
        assertEquals("$120.00", writer.toString());
    }
    
    public void testSimpleCurrencyPLFormat() throws Exception {
        // given
        context.put(ActionContext.LOCALE, new Locale("pl", "PL"));
        
        TestAction testAction = (TestAction) action;
        testAction.setFloatNumber(120.0f);

        NumberTag tag = new NumberTag();
        tag.setPageContext(pageContext);
        tag.setName("floatNumber");
        tag.setType("currency");

        // when
        tag.doStartTag();
        tag.doEndTag();

        // then
        NumberFormat format = NumberFormat.getCurrencyInstance((Locale) context.get(ActionContext.LOCALE));
        format.setRoundingMode(RoundingMode.CEILING);
        String expected = format.format(120.0f);

        assertEquals(expected, writer.toString());
    }

    public void testSimpleRoundingCeiling() throws Exception {
        // given
        context.put(ActionContext.LOCALE, Locale.US);

        TestAction testAction = (TestAction) action;
        testAction.setFloatNumber(120.45f);

        NumberTag tag = new NumberTag();
        tag.setPageContext(pageContext);
        tag.setName("floatNumber");
        tag.setRoundingMode("down");

        // when
        tag.doStartTag();
        tag.doEndTag();

        // then
        NumberFormat format = NumberFormat.getInstance((Locale) context.get(ActionContext.LOCALE));
        format.setRoundingMode(RoundingMode.DOWN);
        String expected = format.format(120.45f);

        assertEquals(expected, writer.toString());
    }

}
