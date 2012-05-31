package org.apache.struts2;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.portlet.PortletConstants;
import org.apache.struts2.portlet.PortletPhase;
import org.springframework.mock.web.portlet.MockPortletContext;
import org.springframework.mock.web.portlet.MockPortletRequest;
import org.springframework.mock.web.portlet.MockPortletResponse;
import org.springframework.mock.web.portlet.MockPortletSession;
import org.springframework.mock.web.portlet.MockStateAwareResponse;

import javax.portlet.PortletMode;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class used to test action in portlet environment
 */
public abstract class StrutsPortletTestCase extends StrutsTestCase {

    private static final Logger LOG = LoggerFactory.getLogger(StrutsPortletTestCase.class);

    protected MockPortletSession portletSession;
    protected MockPortletRequest portletRequest;
    protected MockPortletResponse portletResponse;
    protected MockContext portletContext;

    @Override
    protected void initActionContext(ActionContext actionContext) {
        super.initActionContext(actionContext);
        initPortletContext(actionContext);
    }

    protected void initPortletContext(ActionContext actionContext) {
        LOG.debug("Initializing mock portlet environment");
        portletContext = new MockContext();
        portletContext.setMajorVersion(getMajorVersion());
        actionContext.put(StrutsStatics.STRUTS_PORTLET_CONTEXT, portletContext);

        portletRequest = new MockPortletRequest(portletContext);
        portletResponse = new MockStateAwareResponse();
        portletSession = new MockPortletSession();
        portletRequest.setSession(portletSession);
        actionContext.setSession(createSession());
        actionContext.put(PortletConstants.REQUEST, portletRequest);
        actionContext.put(PortletConstants.RESPONSE, portletResponse);
        actionContext.put(PortletConstants.MODE_NAMESPACE_MAP, new HashMap<PortletMode, String>());
        actionContext.put(PortletConstants.PHASE, PortletPhase.EVENT_PHASE);
    }

    /**
     * Override to define version of your portlet environment
     *
     * @return portlet version
     */
    protected int getMajorVersion() {
        return 2;
    }

    /**
     * Override to create your own session
     *
     * @return Map with session parameters
     */
    private Map<String, Object> createSession() {
        return new HashMap<String, Object>(portletRequest.getPortletSession().getAttributeMap());
    }

}

/**
 * Simple workaround to define Portlet version
 */
class MockContext extends MockPortletContext {

    private int majorVersion;

    @Override
    public int getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

}

