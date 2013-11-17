package org.apache.struts2.components.template;

import org.apache.struts2.StrutsInternalTestCase;

public class JspTemplateEngineTest extends StrutsInternalTestCase {

    public void testEncodingGetsInjected() throws Exception {
        JspTemplateEngine jspTemplateEngine = new JspTemplateEngine();
        container.inject(jspTemplateEngine);
        assertNotNull(jspTemplateEngine.encoding);
    }
}
