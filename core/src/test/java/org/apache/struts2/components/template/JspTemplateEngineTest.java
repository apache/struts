package org.apache.struts2.components.template;

import org.apache.struts2.StrutsTestCase;

/**
 * JspTemplateEngineTest.
 *
 * @author Rene Gielen
 */
public class JspTemplateEngineTest extends StrutsTestCase {

	public void testEncodingGetsInjected() throws Exception {
		JspTemplateEngine jspTemplateEngine = new JspTemplateEngine();
		container.inject(jspTemplateEngine);
		assertNotNull(jspTemplateEngine.encoding);
	}
}
