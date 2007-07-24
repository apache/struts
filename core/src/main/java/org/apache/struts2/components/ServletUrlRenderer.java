/*
 * $Id$
 *
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
package org.apache.struts2.components;

import java.io.IOException;
import java.io.Writer;

import org.apache.struts2.StrutsException;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.views.util.UrlHelper;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.entities.ActionConfig;

/**
 * Implementation of the {@link UrlRenderer} interface that creates URLs suitable in a servlet environment.
 * 
 */
public class ServletUrlRenderer implements UrlRenderer {

	/**
	 * {@inheritDoc}
	 */
	public void renderUrl(Writer writer, URL urlComponent) {
		String scheme = urlComponent.req.getScheme();

		if (urlComponent.scheme != null) {
			scheme = urlComponent.scheme;
		}

	       String result;
	        if (urlComponent.value == null && urlComponent.action != null) {
	                result = urlComponent.determineActionURL(urlComponent.action, urlComponent.namespace, urlComponent.method, urlComponent.req, urlComponent.res, urlComponent.parameters, scheme, urlComponent.includeContext, urlComponent.encode, false, urlComponent.escapeAmp);
	        } else {
	                String _value = urlComponent.value;

	                // We don't include the request parameters cause they would have been
	                // prioritised before this [in start(Writer) method]
	                if (_value != null && _value.indexOf("?") > 0) {
	                    _value = _value.substring(0, _value.indexOf("?"));
	                }
	                result = UrlHelper.buildUrl(_value, urlComponent.req, urlComponent.res, urlComponent.parameters, scheme, urlComponent.includeContext, urlComponent.encode, false, urlComponent.escapeAmp);
	        }
	        if ( urlComponent.anchor != null && urlComponent.anchor.length() > 0 ) {
	            result += '#' + urlComponent.anchor;
	        }

	        String var = urlComponent.getVar();

	        if (var != null) {
	        	urlComponent.putInContext(result);

	            // add to the request and page scopes as well
	        	urlComponent.req.setAttribute(var, result);
	        } else {
	            try {
	                writer.write(result);
	            } catch (IOException e) {
	                throw new StrutsException("IOError: " + e.getMessage(), e);
	            }
	        }
	}

	/**
	 * {@inheritDoc}
	 */
	public void renderFormUrl(Form formComponent) {
		String namespace = formComponent.determineNamespace(formComponent.namespace, formComponent.getStack(),
				formComponent.request);
		String action = null;

		if(formComponent.action != null) {
			action = formComponent.findString(formComponent.action);
		}
		
		if (formComponent.action == null) {
			// no action supplied? ok, then default to the current request
			// (action or general URL)
			ActionInvocation ai = (ActionInvocation) formComponent.getStack().getContext().get(
					ActionContext.ACTION_INVOCATION);
			if (ai != null) {
				action = ai.getProxy().getActionName();
				namespace = ai.getProxy().getNamespace();
			} else {
				// hmm, ok, we need to just assume the current URL cut down
				String uri = formComponent.request.getRequestURI();
				action = uri.substring(uri.lastIndexOf('/'));
			}
		}

		String actionMethod = "";
		// FIXME: our implementation is flawed - the only concept of ! should be
		// in DefaultActionMapper
		// handle "name!method" convention.
		if (formComponent.enableDynamicMethodInvocation) {
			if (action.indexOf("!") != -1) {
				int endIdx = action.lastIndexOf("!");
				actionMethod = action.substring(endIdx + 1, action.length());
				action = action.substring(0, endIdx);
			}
		}

		final ActionConfig actionConfig = formComponent.configuration.getRuntimeConfiguration().getActionConfig(
				namespace, action);
		String actionName = action;
		if (actionConfig != null) {

			ActionMapping mapping = new ActionMapping(action, namespace, actionMethod, formComponent.parameters);
			String result = UrlHelper.buildUrl(formComponent.actionMapper.getUriFromActionMapping(mapping),
					formComponent.request, formComponent.response, null);
			formComponent.addParameter("action", result);

			// let's try to get the actual action class and name
			// this can be used for getting the list of validators
			formComponent.addParameter("actionName", actionName);
			try {
				Class clazz = formComponent.objectFactory.getClassInstance(actionConfig.getClassName());
				formComponent.addParameter("actionClass", clazz);
			} catch (ClassNotFoundException e) {
				// this is OK, we'll just move on
			}

			formComponent.addParameter("namespace", namespace);

			// if the name isn't specified, use the action name
			if (formComponent.name == null) {
				formComponent.addParameter("name", action);
			}

			// if the id isn't specified, use the action name
			if (formComponent.getId() == null) {
				formComponent.addParameter("id", action);
			}
		} else if (action != null) {
			// Since we can't find an action alias in the configuration, we just
			// assume
			// the action attribute supplied is the path to be used as the uri
			// this
			// form is submitting to.

			String result = UrlHelper.buildUrl(action, formComponent.request, formComponent.response, null);
			formComponent.addParameter("action", result);

			// namespace: cut out anything between the start and the last /
			int slash = result.lastIndexOf('/');
			if (slash != -1) {
				formComponent.addParameter("namespace", result.substring(0, slash));
			} else {
				formComponent.addParameter("namespace", "");
			}

			// name/id: cut out anything between / and . should be the id and
			// name
			String id = formComponent.getId();
			if (id == null) {
				slash = result.lastIndexOf('/');
				int dot = result.indexOf('.', slash);
				if (dot != -1) {
					id = result.substring(slash + 1, dot);
				} else {
					id = result.substring(slash + 1);
				}
				formComponent.addParameter("id", formComponent.escape(id));
			}
		}

		// WW-1284
		// evaluate if client-side js is to be enabled. (if validation
		// interceptor
		// does allow validation eg. method is not filtered out)
		formComponent.evaluateClientSideJsEnablement(actionName, namespace, actionMethod);
	}

}
