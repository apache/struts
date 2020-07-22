package org.apache.struts2.views.jsp.ui;

import org.apache.struts2.views.jsp.AbstractUITagTest;

import javax.servlet.jsp.JspException;

public class LinkTagTest extends AbstractUITagTest {

    private static final String nonceVal = "r4andom";

    public void testLinkTagAttributes() {
        LinkTag tag = new LinkTag();

        tag.setHref("mysrc.js");
        tag.setHreflang("test");
        tag.setRel("module");
        tag.setMedia("foo");
        tag.setReferrerpolicy("test");
        tag.setSizes("foo");
        tag.setCrossorigin("same-origin");
        tag.setType("anonymous");
        tag.setAs("test");
        tag.setIntegrity("test");
        tag.setDisabled("false");
        tag.setImportance("test");
        tag.setTitle("test");

        doLinkTest(tag);
        String s = writer.toString();

        assertTrue("Incorrect href attribute for link tag", s.contains("href=\"mysrc.js\""));
        assertTrue("Incorrect hreflang attribute for link tag", s.contains("hreflang=\"test\""));
        assertTrue("Incorrect rel attribute for link tag", s.contains("rel=\"module\""));
        assertTrue("Incorrect media attribute for link tag", s.contains("media=\"foo\""));
        assertTrue("Incorrect referrerpolicy attribute for link tag", s.contains("referrerpolicy=\"test\""));
        assertTrue("Incorrect sizes attribute for link tag", s.contains("sizes=\"foo\""));
        assertTrue("Incorrect crossorigin attribute for link tag", s.contains("crossorigin=\"same-origin\""));
        assertTrue("Incorrect type attribute for link tag", s.contains("type=\"anonymous\""));
        assertTrue("Incorrect as attribute for link tag", s.contains("as=\"test\""));
        assertTrue("Incorrect integrity attribute for link tag", s.contains("integrity=\"test\""));
        assertFalse("Non-existent disabled attribute for link tag", s.contains("disabled"));
        assertTrue("Incorrect importance attribute for link tag", s.contains("importance=\"test\""));
        assertTrue("Incorrect title attribute for link tag", s.contains("title=\"test\""));
        assertTrue("Incorrect nonce attribute for link tag", s.contains("nonce=\"" + nonceVal+"\""));
    }

    private void doLinkTest(LinkTag tag) {
        //creating nonce value like the CspInterceptor does
        stack.getActionContext().getSession().put("nonce", nonceVal);
        tag.setPageContext(pageContext);

        try {
            tag.doStartTag();
            tag.doEndTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

    }
}
