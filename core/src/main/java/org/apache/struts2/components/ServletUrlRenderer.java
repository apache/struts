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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.StrutsException;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.views.util.UrlHelper;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementation of the {@link UrlRenderer} interface that creates URLs suitable in a servlet environment.
 * 
 */
public class ServletUrlRenderer implements UrlRenderer {
    /**
     * Provide a logging instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ServletUrlRenderer.class);

    private ActionMapper actionMapper;

    @Inject
    public void setActionMapper(ActionMapper mapper) {
        this.actionMapper = mapper;
    }


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
	                result = urlComponent.determineActionURL(urlComponent.action, urlComponent.namespace, urlComponent.method, urlComponent.req, urlComponent.res, urlComponent.parameters, scheme, urlComponent.includeContext, urlComponent.encode, urlComponent.forceAddSchemeHostAndPort, urlComponent.escapeAmp);
	        } else {
	                String _value = urlComponent.value;

	                // We don't include the request parameters cause they would have been
	                // prioritised before this [in start(Writer) method]
	                if (_value != null && _value.indexOf("?") > 0) {
	                    _value = _value.substring(0, _value.indexOf("?"));
	                }
	                result = UrlHelper.buildUrl(_value, urlComponent.req, urlComponent.res, urlComponent.parameters, scheme, urlComponent.includeContext, urlComponent.encode, urlComponent.forceAddSchemeHostAndPort, urlComponent.escapeAmp);
	        }
	        if ( urlComponent.anchor != null && urlComponent.anchor.length() > 0 ) {
	        	result += '#' + urlComponent.findString(urlComponent.anchor);
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
		String action;

		if(formComponent.action != null) {
			action = formComponent.findString(formComponent.action);
		} else {
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

        ActionMapping nameMapping = actionMapper.getMappingFromActionName(action);
        String actionName = nameMapping.getName();
        String actionMethod = nameMapping.getMethod();

		final ActionConfig actionConfig = formComponent.configuration.getRuntimeConfiguration().getActionConfig(
				namespace, actionName);
		if (actionConfig != null) {

			ActionMapping mapping = new ActionMapping(actionName, namespace, actionMethod, formComponent.parameters);
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
				formComponent.addParameter("name", actionName);
			}

			// if the id isn't specified, use the action name
			if (formComponent.getId() == null  && actionName!=null ) {
				formComponent.addParameter("id", formComponent.escape(actionName));
			}
		} else if (action != null) {
			// Since we can't find an action alias in the configuration, we just
			// assume the action attribute supplied is the path to be used as
			// the URI this form is submitting to.

            // Warn user that the specified namespace/action combo
            // was not found in the configuration.
            if (namespace != null) {
              LOG.warn("No configuration found for the specified action: '" + actionName + "' in namespace: '" + namespace + "'. Form action defaulting to 'action' attribute's literal value.");
            }

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
		// interceptor does allow validation eg. method is not filtered out)
		formComponent.evaluateClientSideJsEnablement(actionName, namespace, actionMethod);
	}


	public void beforeRenderUrl(URL urlComponent) {
		if (urlComponent.value != null) {
            urlComponent.value = urlComponent.findString(urlComponent.value);
        }

        // no explicit url set so attach params from current url, do
        // this at start so body params can override any of these they wish.
        try {
            // ww-1266
            String includeParams = (urlComponent.urlIncludeParams != null ? urlComponent.urlIncludeParams.toLowerCase() : URL.GET);

            if (urlComponent.includeParams != null) {
                includeParams = urlComponent.findString(urlComponent.includeParams);
            }

            if (URL.NONE.equalsIgnoreCase(includeParams)) {
                mergeRequestParameters(urlComponent.value, urlComponent.parameters, Collections.EMPTY_MAP);
            } else if (URL.ALL.equalsIgnoreCase(includeParams)) {
                mergeRequestParameters(urlComponent.value, urlComponent.parameters, urlComponent.req.getParameterMap());

                // for ALL also include GET parameters
                includeGetParameters(urlComponent);
                includeExtraParameters(urlComponent);
            } else if (URL.GET.equalsIgnoreCase(includeParams) || (includeParams == null && urlComponent.value == null && urlComponent.action == null)) {
                includeGetParameters(urlComponent);
                includeExtraParameters(urlComponent);
            } else if (includeParams != null) {
                LOG.warn("Unknown value for includeParams parameter to URL tag: " + includeParams);
            }
        } catch (Exception e) {
            LOG.warn("Unable to put request parameters (" + urlComponent.req.getQueryString() + ") into parameter map.", e);
        }

		
	}
	
    private void includeExtraParameters(URL urlComponent) {
        if (urlComponent.extraParameterProvider != null) {
            mergeRequestParameters(urlComponent.value, urlComponent.parameters, urlComponent.extraParameterProvider.getExtraParameters());
        }
    }
    private void includeGetParameters(URL urlComponent) {
    	String query = extractQueryString(urlComponent);
    	mergeRequestParameters(urlComponent.value, urlComponent.parameters, UrlHelper.parseQueryString(query));
    }

    private String extractQueryString(URL urlComponent) {
        // Parse the query string to make sure that the parameters come from the query, and not some posted data
        String query = urlComponent.req.getQueryString();
        if (query == null) {
            query = (String) urlComponent.req.getAttribute("javax.servlet.forward.query_string");
        }

        if (query != null) {
            // Remove possible #foobar suffix
            int idx = query.lastIndexOf('#');

            if (idx != -1) {
                query = query.substring(0, idx);
            }
        }
        return query;
    }
    
    /**
     * Merge request parameters into current parameters. If a parameter is
     * already present, than the request parameter in the current request and value atrribute
     * will not override its value.
     *
     * The priority is as follows:-
     * <ul>
     *  <li>parameter from the current request (least priority)</li>
     *  <li>parameter form the value attribute (more priority)</li>
     *  <li>parameter from the param tag (most priority)</li>
     * </ul>
     *
     * @param value the value attribute (url to be generated by this component)
     * @param parameters component parameters
     * @param contextParameters request parameters
     */
    protected void mergeRequestParameters(String value, Map parameters, Map contextParameters){

        Map mergedParams = new LinkedHashMap(contextParameters);

        // Merge contextParameters (from current request) with parameters specified in value attribute
        // eg. value="someAction.action?id=someId&venue=someVenue"
        // where the parameters specified in value attribute takes priority.

        if (value != null && value.trim().length() > 0 && value.indexOf("?") > 0) {
            mergedParams = new LinkedHashMap();

            String queryString = value.substring(value.indexOf("?")+1);

            mergedParams = UrlHelper.parseQueryString(queryString);
            for (Iterator iterator = contextParameters.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Object key = entry.getKey();

                if (!mergedParams.containsKey(key)) {
                    mergedParams.put(key, entry.getValue());
                }
            }
        }


        // Merge parameters specified in value attribute
        // eg. value="someAction.action?id=someId&venue=someVenue"
        // with parameters specified though param tag
        // eg. <param name="id" value="%{'someId'}" />
        // where parameters specified through param tag takes priority.

        for (Iterator iterator = mergedParams.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object key = entry.getKey();

            if (!parameters.containsKey(key)) {
                parameters.put(key, entry.getValue());
            }
        }
    }
}
