package org.apache.struts2.components;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.portlet.PortletConstants;
import org.springframework.mock.web.portlet.MockMimeResponse;
import org.springframework.mock.web.portlet.MockPortletContext;
import org.springframework.mock.web.portlet.MockPortletRequest;

import javax.portlet.PortletContext;
import javax.portlet.PortletMode;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;

public class PortletUrlRendererTest extends StrutsTestCase {

    private ValueStack stack;

    public void setUp() throws Exception {
        super.setUp();

        ActionProxy actionProxy = getActionProxy("/portlettest/test"); // creates new empty ActionContext
        ActionContext.getContext().put(ActionContext.ACTION_INVOCATION, actionProxy.getInvocation());

        PortletContext portletCtx = new MockPortletContext();
        ActionContext.getContext().put(StrutsStatics.STRUTS_PORTLET_CONTEXT, portletCtx);
        ActionContext.getContext().put(PortletConstants.REQUEST, new MockPortletRequest(portletCtx));
        ActionContext.getContext().put(PortletConstants.RESPONSE, new MockMimeResponse());
        ActionContext.getContext().put(PortletConstants.MODE_NAMESPACE_MAP, Collections.emptyMap());

        stack = actionProxy.getInvocation().getStack();
    }

    public void testRenderUrlWithNamespace() throws Exception {
        // given
        PortletUrlRenderer renderer = new PortletUrlRenderer();
        UrlProvider component = new URL(stack, request, response).getUrlProvider();
        Writer writer = new StringWriter();

        // when
        renderer.renderUrl(writer, component);

        // then
        assertTrue("/portlettest".equals(component.getNamespace()));
    }
    
    public void testIsPortelModeChanged() {
    	PortletUrlRenderer renderer = new PortletUrlRenderer();
    	PortletMode mode = new PortletMode("test");
    	UrlProvider provider = new ComponentUrlProvider(null, null);
    	provider.setPortletMode("test2");
    	
    	assertTrue(renderer.isPortletModeChange(provider, mode));
    }

}
