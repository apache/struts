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
package org.apache.struts2.portlet.dispatcher;

import com.opensymphony.xwork2.Action;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.portlet.PortletConstants;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>
 * When a portlet is targetted for an <code>event</code>, the portlet will receive two
 * portlet requests, one for the <code>event</code> phase, and then followed by a <code>render</code>
 * operation. When in the <code>event</code> phase, the action that is executed can't render
 * any output. This means that if an action in the XWork configuration is executed in the event
 * phase, and the action is set up with a result that should render something, the result can't
 * immediately be executed. The portlet needs to "wait" to the render phase to do the
 * rendering.
 * </p>
 *
 * <p>
 * When the {@link org.apache.struts2.portlet.result.PortletResult} detects such a
 * scenario, instead of executing the actual view, it prepares a couple of render parameters
 * specifying this action and the location of the view, which then will be executed in the
 * following render request.
 * </p>
 */
public class DirectRenderFromEventAction implements SessionAware, Action, Serializable {

    private static final long serialVersionUID = -1814807772308405785L;

    private String location = null;

    /**
     * Get the location of the view.
     *
     * @return Returns the location.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Always return success.
     *
     * @return SUCCESS
     */
    public String execute() throws Exception {
        return SUCCESS;
    }

	public void setSession(Map session) {
		location = (String)session.get(PortletConstants.RENDER_DIRECT_LOCATION);
	}
}
