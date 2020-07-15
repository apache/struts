package org.apache.struts2.views.java.simple;

import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.components.Script;
import org.apache.struts2.components.Token;
import org.apache.struts2.components.UIBean;

import java.util.HashMap;
import java.util.regex.Pattern;

public class ScriptTest extends AbstractTest {

    private Script tag;

    public void testRenderScriptTag() {
//        tag.setType("text/javascript");
       
        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();

        System.out.println(output);
        assertTrue(output.contains("nonce="));
    }

    @Override
    protected UIBean getUIBean() throws Exception {
        return tag;
    }

    @Override
    protected String getTagName() {
        return "script";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.tag = new Script(stack, request, response);

        ActionContext actionContext = ActionContext.of(new HashMap<>()).bind();
        actionContext.setSession(new HashMap<>());
    }
}
