package org.apache.struts2.views.jsp;

import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.TestAction;

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
        /*
        TODO lukaszlenart: enable when switched to Java 1.6
        format.setRoundingMode(RoundingMode.CEILING);
        */
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
        tag.setRoundingMode("ceiling");

        // when
        tag.doStartTag();
        tag.doEndTag();

        // then
        NumberFormat format = NumberFormat.getInstance((Locale) context.get(ActionContext.LOCALE));
        /*
        TODO lukaszlenart: enable when switched to Java 1.6
        format.setRoundingMode(RoundingMode.CEILING);
        */
        String expected = format.format(120.45f);

        assertEquals(expected, writer.toString());
    }

}
