package org.apache.struts2.components.template;

import org.apache.struts2.StrutsTestCase;

public class JspTemplateEngineTest extends StrutsTestCase {

    public void testEncodingGetsInjected() throws Exception {
        JspTemplateEngine jspTemplateEngine = new JspTemplateEngine();
        container.inject(jspTemplateEngine);
        assertNotNull(jspTemplateEngine.encoding);
    }
}
