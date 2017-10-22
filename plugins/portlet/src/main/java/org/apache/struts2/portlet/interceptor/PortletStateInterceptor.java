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
package org.apache.struts2.portlet.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.portlet.PortletConstants;
import org.apache.struts2.portlet.PortletPhase;
import org.apache.struts2.portlet.dispatcher.DirectRenderFromEventAction;

import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import java.util.Map;

import static org.apache.struts2.portlet.PortletConstants.*;

public class PortletStateInterceptor extends AbstractInterceptor {

	private final static Logger LOG = LogManager.getLogger(PortletStateInterceptor.class);

	private static final long serialVersionUID = 6138452063353911784L;

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		PortletPhase phase = (PortletPhase) invocation.getInvocationContext().get(PortletConstants.PHASE);
		if (phase.isRender()) {
			restoreStack(invocation);
			return invocation.invoke();
		} else if (phase.isAction()) {
			try {
				return invocation.invoke();
			} finally {
				saveStack(invocation);
			}
		} else {
			return invocation.invoke();
		}
	}

	@SuppressWarnings("unchecked")
	private void saveStack(ActionInvocation invocation) {
		Map session = invocation.getInvocationContext().getSession();
		session.put(STACK_FROM_EVENT_PHASE, invocation.getStack());
		ActionResponse actionResponse = (ActionResponse) invocation.getInvocationContext().get(RESPONSE);
		actionResponse.setRenderParameter(EVENT_ACTION, "true");
	}

	@SuppressWarnings("unchecked")
	private void restoreStack(ActionInvocation invocation) {
		RenderRequest request = (RenderRequest) invocation.getInvocationContext().get(REQUEST);
		if (StringUtils.isNotEmpty(request.getParameter(EVENT_ACTION))) {
			if(!isProperPrg(invocation)) {
				if (LOG.isDebugEnabled()) LOG.debug("Restoring value stack from event phase");
				ValueStack oldStack = (ValueStack) invocation.getInvocationContext().getSession().get(
				STACK_FROM_EVENT_PHASE);
				if (oldStack != null) {
					CompoundRoot oldRoot = oldStack.getRoot();
					ValueStack currentStack = invocation.getStack();
					CompoundRoot root = currentStack.getRoot();
					root.addAll(0, oldRoot);
					if (LOG.isDebugEnabled()) LOG.debug("Restored stack");
				}
			}
			else {
				if (LOG.isDebugEnabled()) LOG.debug("Won't restore stack from event phase since it's a proper PRG request");
			}
		}
	}

	private boolean isProperPrg(ActionInvocation invocation) {
		return !(invocation.getAction() instanceof DirectRenderFromEventAction);
	}

}
