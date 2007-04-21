package org.apache.struts2.dojo.views.jsp.ui;

import org.apache.struts2.dojo.TestAction;

public class BindTest extends AbstractUITagTest {
    public void testAll() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        BindTag tag = new BindTag();
        tag.setPageContext(pageContext);

        tag.setId("a");
        tag.setHref("b");
        tag.setLoadingText("c");
        tag.setErrorText("d");
        tag.setListenTopics("e");
        tag.setBeforeNotifyTopics("f");
        tag.setAfterNotifyTopics("g");
        tag.setHandler("h");
        tag.setNotifyTopics("k");
        tag.setIndicator("l");
        tag.setShowLoadingText("true");
        tag.setErrorNotifyTopics("m");
        tag.setSources("n");
        tag.setEvents("o");
        tag.setHighlightColor("p");
        tag.setHighlightDuration("q");
        tag.doStartTag();
        tag.doEndTag();

        verify(BindTest.class.getResource("Bind-1.txt"));
    }
}
