package com.opensymphony.webwork.views.jsp.ui;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.webwork.TestAction;
import com.opensymphony.webwork.views.jsp.AbstractUITagTest;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JspTemplateTest
 * Date: Sep 29, 2004 12:14:34 PM
 *
 * @author jcarreira
 */
public class JspTemplateTest extends AbstractUITagTest {
    public void testCheckBox() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("true");

        CheckboxTag tag = new CheckboxTag();
        Mock rdMock = new Mock(RequestDispatcher.class);
        rdMock.expect("include",C.args(C.isA(HttpServletRequest.class), C.isA(HttpServletResponse.class)));
        RequestDispatcher dispatcher = (RequestDispatcher) rdMock.proxy();
        request.setupGetRequestDispatcher(dispatcher);
        tag.setPageContext(pageContext);
        tag.setTemplate("/test/checkbox.jsp");
        tag.doStartTag();
        tag.doEndTag();
        rdMock.verify();
    }
}
