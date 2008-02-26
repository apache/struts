package org.apache.struts2.views.jsp;

import org.apache.struts2.TestAction;
import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.ServletActionContext;
import com.mockobjects.servlet.MockPageContext;
import com.mockobjects.servlet.MockJspWriter;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.ActionContext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class I18nTagTest extends StrutsTestCase {

    I18nTag tag;
    MockPageContext pageContext;
    ValueStack stack;

    protected void setUp() throws Exception {
        super.setUp();
        // create the needed objects
        tag = new I18nTag();
        stack = ActionContext.getContext().getValueStack();

        // create the mock http servlet request
        StrutsMockHttpServletRequest request = new StrutsMockHttpServletRequest();
        ActionContext.getContext().setValueStack(stack);
        request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);

        // create the mock page context
        pageContext = new MockPageContext();
        pageContext.setRequest(request);
        pageContext.setJspWriter(new MockJspWriter());

        // associate the tag with the mock page request
        tag.setPageContext(pageContext);
    }

    public void testSimple() throws Exception {

        // set the resource bundle
        tag.setName("testmessages");

        int result = 0;

        try {
            result = tag.doStartTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(TagSupport.EVAL_BODY_INCLUDE, result);

        try {
            result = tag.doEndTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }
    }
}
