/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2;


import com.opensymphony.xwork2.ActionContext;
import java.util.HashMap;
import java.util.Map;
import javax.portlet.PortletMode;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.struts2.mock.web.portlet.MockPortletContext;
import org.apache.struts2.mock.web.portlet.MockPortletRequest;
import org.apache.struts2.mock.web.portlet.MockPortletResponse;
import org.apache.struts2.mock.web.portlet.MockPortletSession;
import org.apache.struts2.mock.web.portlet.MockStateAwareResponse;
import org.apache.struts2.portlet.PortletConstants;
import org.apache.struts2.portlet.PortletPhase;

/*
 * Changes:  This is a copy of org.apache.struts2.StrutsPortletTestCase from the Struts 2 portlet-plugin, moved
 *           into the junit-plugin (same package org.apache.struts2).
 *           The import order above was changed to alphabetical.
 *
 * Note:     The assumption is that anyone utilizing StrutsPortletTestCase currently from the portlet-plugin will almost
 *           certainly be using the junit-plugin.  Under that assumption, the refactored-move of StrutsPortletTestCase
 *           should not cause issues for pre-existing usage of StrutsPortletTestCase.
 */

/**
 * Base class used to test action in portlet environment
 */
public abstract class StrutsPortletTestCase extends StrutsTestCase {

    private static final Logger LOG = LogManager.getLogger(StrutsPortletTestCase.class);

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
        actionContext.withSession(createSession());
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

