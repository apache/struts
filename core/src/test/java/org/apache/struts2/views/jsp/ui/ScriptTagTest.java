package org.apache.struts2.views.jsp.ui;

import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.util.TokenHelper;
import org.apache.struts2.views.jsp.AbstractUITagTest;

import javax.servlet.jsp.JspException;
import java.util.HashMap;
import java.util.Map;

public class ScriptTagTest extends AbstractUITagTest {

    private static final String nonceVal = "r4andom";

    public void testScriptTagAttributes() {
        ScriptTag tag = new ScriptTag();


        tag.setSrc("mysrc.js");
        tag.setAsync("false");
        tag.setType("module");
        tag.setAsync("false");
        tag.setNomodule("true");
        tag.setReferrerpolicy("same-origin");
        tag.setCrossorigin("anonymous");

        doScriptTest(tag);
        String s = writer.toString();

        System.out.println(s);
        assertTrue(s.contains("nonce=\"" + nonceVal));
    }

    private void doScriptTest(ScriptTag tag) {
        //creating nonce value like the CspInterceptor does
        stack.getActionContext().getSession().put("nonce", nonceVal);
        tag.setPageContext(pageContext);

        try {
            tag.doStartTag();
            tag.doEndTag();
//            assertNotNull();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

    }
}
