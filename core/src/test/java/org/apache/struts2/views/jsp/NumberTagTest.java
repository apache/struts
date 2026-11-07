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

import org.apache.struts2.ActionContext;
import org.apache.struts2.TestAction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class NumberTagTest extends AbstractTagTest {

    public void testSimpleFloatFormat() throws Exception {
        // given
        context = ActionContext.of(context).withLocale(Locale.US).getContextMap();

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

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        NumberTag freshTag = new NumberTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testSimpleFloatFormat_clearTagStateSet() throws Exception {
        // given
        ActionContext.of(context).withLocale(Locale.US);

        TestAction testAction = (TestAction) action;
        testAction.setFloatNumber(120.0f);

        NumberTag tag = new NumberTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setName("floatNumber");

        // when
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        // then
        assertEquals("120", writer.toString());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        NumberTag freshTag = new NumberTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testSimpleCurrencyUSFormat() throws Exception {
        // given
        context = ActionContext.of(context).withLocale(Locale.US).getContextMap();

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

        NumberTag freshTag = new NumberTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testSimpleCurrencyUSFormat_clearTagStateSet() throws Exception {
        // given
        ActionContext.of(context).withLocale(Locale.US);

        TestAction testAction = (TestAction) action;
        testAction.setFloatNumber(120.0f);

        NumberTag tag = new NumberTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setName("floatNumber");
        tag.setType("currency");

        // when
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        // then
        assertEquals("$120.00", writer.toString());

        NumberTag freshTag = new NumberTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testSimpleCurrencyPLFormat() throws Exception {
        // given
        context = ActionContext.of(context).withLocale(new Locale("pl", "PL")).getContextMap();

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
        Locale locale = ActionContext.of(context).getLocale();
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        format.setRoundingMode(RoundingMode.CEILING);
        String expected = format.format(120.0f);

        assertEquals(expected, writer.toString());

        NumberTag freshTag = new NumberTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testSimpleCurrencyPLFormat_clearTagStateSet() throws Exception {
        // given
        ActionContext.of(context).withLocale(new Locale("pl", "PL"));

        TestAction testAction = (TestAction) action;
        testAction.setFloatNumber(120.0f);

        NumberTag tag = new NumberTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setName("floatNumber");
        tag.setType("currency");

        // when
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        // then
        NumberFormat format = NumberFormat.getCurrencyInstance(ActionContext.of(context).getLocale());
        format.setRoundingMode(RoundingMode.CEILING);
        String expected = format.format(120.0f);

        assertEquals(expected, writer.toString());

        NumberTag freshTag = new NumberTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testSimpleRoundingCeiling() throws Exception {
        // given
        context = ActionContext.of(context).withLocale(Locale.US).getContextMap();

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
        Locale locale = ActionContext.of(context).getLocale();
        NumberFormat format = NumberFormat.getInstance(locale);
        format.setRoundingMode(RoundingMode.DOWN);
        String expected = format.format(120.45f);

        assertEquals(expected, writer.toString());

        NumberTag freshTag = new NumberTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testSimpleRoundingCeiling_clearTagStateSet() throws Exception {
        // given
        ActionContext.of(context).withLocale(new Locale("pl", "PL"));

        TestAction testAction = (TestAction) action;
        testAction.setFloatNumber(120.45f);

        NumberTag tag = new NumberTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setName("floatNumber");
        tag.setRoundingMode("down");

        // when
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        // then
        NumberFormat format = NumberFormat.getInstance(ActionContext.of(context).getLocale());
        format.setRoundingMode(RoundingMode.DOWN);
        String expected = format.format(120.45f);

        assertEquals(expected, writer.toString());

        NumberTag freshTag = new NumberTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testBigDecimalIntegerDigitsAreBounded() throws Exception {
        // when the number of integer digits is within the bound
        // then the value is rendered in full
        BigDecimal withinBound = new BigDecimal(BigInteger.ONE, -339);
        assertEquals(NumberFormat.getInstance(Locale.US).format(withinBound), renderNumber(withinBound, null));

        // when the number of integer digits exceeds the bound
        // then the value is rendered in its compact form rather than padded out to its scale
        assertEquals("1E+340", renderNumber(new BigDecimal(BigInteger.ONE, -340), null));

        // when the scale sits at the edge of its range
        // then the bound still applies
        assertEquals("1E+2147483647", renderNumber(new BigDecimal(BigInteger.ONE, Integer.MIN_VALUE + 1), null));
    }

    public void testBigDecimalCurrencyWithinBoundIsUnchanged() throws Exception {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        BigDecimal value = new BigDecimal(BigInteger.ONE, -339);

        assertEquals(format.format(value), renderNumber(value, "currency"));
        assertEquals("$1,234.50", renderNumber(new BigDecimal("1234.5"), "currency"));
    }

    public void testBigDecimalCurrencyIntegerDigitsAreBounded() throws Exception {
        assertEquals("1E+340", renderNumber(new BigDecimal(BigInteger.ONE, -340), "currency"));
    }

    private String renderNumber(BigDecimal value, String type) throws Exception {
        context = ActionContext.of(context).withLocale(Locale.US).getContextMap();
        stack.push(new AmountHolder(value));

        NumberTag tag = new NumberTag();
        tag.setPageContext(pageContext);
        tag.setName("amount");
        tag.setType(type);

        writer.getBuffer().setLength(0);
        tag.doStartTag();
        tag.doEndTag();
        stack.pop();
        return writer.toString();
    }

    public static class AmountHolder {
        private final BigDecimal amount;

        public AmountHolder(BigDecimal amount) {
            this.amount = amount;
        }

        public BigDecimal getAmount() {
            return amount;
        }
    }

}
