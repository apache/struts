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

package org.apache.struts2.rest;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import java.util.HashMap;
import java.util.Map;

/**
 * <!-- START SNIPPET: description -->
 *
 * An interceptor that makes sure there are not validation errors before allowing the interceptor chain to continue.
 * <b>This interceptor does not perform any validation</b>.
 * 
 * <p>Copied from the {@link com.opensymphony.xwork2.interceptor.DefaultWorkflowInterceptor}, this interceptor adds support for error handling of Restful
 * operations.  For example, if an validation error is discovered, a map of errors is created and processed to be
 * returned, using the appropriate content handler for rendering the body.</p>
 *
 * <p/>This interceptor does nothing if the name of the method being invoked is specified in the <b>excludeMethods</b>
 * parameter. <b>excludeMethods</b> accepts a comma-delimited list of method names. For example, requests to
 * <b>foo!input.action</b> and <b>foo!back.action</b> will be skipped by this interceptor if you set the
 * <b>excludeMethods</b> parameter to "input, back".
 *
 * <b>Note:</b> As this method extends off MethodFilterInterceptor, it is capable of
 * deciding if it is applicable only to selective methods in the action class. This is done by adding param tags
 * for the interceptor element, naming either a list of excluded method names and/or a list of included method
 * names, whereby includeMethods overrides excludedMethods. A single * sign is interpreted as wildcard matching
 * all methods for both parameters.
 * See {@link MethodFilterInterceptor} for more info.
 *
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Interceptor parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>inputResultName - Default to "input". Determine the result name to be returned when
 * an action / field error is found.</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <p/> <u>Extending the interceptor:</u>
 *
 * <p/>
 *
 * <!-- START SNIPPET: extending -->
 *
 * There are no known extension points for this interceptor.
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * 
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="params"/&gt;
 *     &lt;interceptor-ref name="validation"/&gt;
 *     &lt;interceptor-ref name="workflow"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * 
 * &lt;-- In this case myMethod as well as mySecondMethod of the action class
 *        will not pass through the workflow process --&gt;
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="params"/&gt;
 *     &lt;interceptor-ref name="validation"/&gt;
 *     &lt;interceptor-ref name="workflow"&gt;
 *         &lt;param name="excludeMethods"&gt;myMethod,mySecondMethod&lt;/param&gt;
 *     &lt;/interceptor-ref name="workflow"&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 *
 * &lt;-- In this case, the result named "error" will be used when
 *        an action / field error is found --&gt;
 * &lt;-- The Interceptor will only be applied for myWorkflowMethod method of action
 *        classes, since this is the only included method while any others are excluded --&gt;
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="params"/&gt;
 *     &lt;interceptor-ref name="validation"/&gt;
 *     &lt;interceptor-ref name="workflow"&gt;
 *        &lt;param name="inputResultName"&gt;error&lt;/param&gt;
*         &lt;param name="excludeMethods"&gt;*&lt;/param&gt;
*         &lt;param name="includeMethods"&gt;myWorkflowMethod&lt;/param&gt;
 *     &lt;/interceptor-ref&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Jason Carreira
 * @author Rainer Hermanns
 * @author <a href='mailto:the_mindstorm[at]evolva[dot]ro'>Alexandru Popescu</a>
 * @author Philip Luppens
 * @author tm_jee
 */
public class RestWorkflowInterceptor extends MethodFilterInterceptor {
	
	private static final long serialVersionUID = 7563014655616490865L;

	private static final Logger LOG = LoggerFactory.getLogger(RestWorkflowInterceptor.class);
	
	private String inputResultName = Action.INPUT;
	
	private ContentTypeHandlerManager manager;

    private String postMethodName = "create";
    private String editMethodName = "edit";
    private String newMethodName = "editNew";
    private String putMethodName = "update";

    private int validationFailureStatusCode = SC_BAD_REQUEST;

    @Inject(required=false,value="struts.mapper.postMethodName")
    public void setPostMethodName(String postMethodName) {
        this.postMethodName = postMethodName;
    }

    @Inject(required=false,value="struts.mapper.editMethodName")
    public void setEditMethodName(String editMethodName) {
        this.editMethodName = editMethodName;
    }

    @Inject(required=false,value="struts.mapper.newMethodName")
    public void setNewMethodName(String newMethodName) {
        this.newMethodName = newMethodName;
    }

    @Inject(required=false,value="struts.mapper.putMethodName")
    public void setPutMethodName(String putMethodName) {
        this.putMethodName = putMethodName;
    }

    @Inject(required=false,value="struts.rest.validationFailureStatusCode")
    public void setValidationFailureStatusCode(String code) {
        this.validationFailureStatusCode = Integer.parseInt(code);
    }

    @Inject
	public void setContentTypeHandlerManager(ContentTypeHandlerManager mgr) {
	    this.manager = mgr;
	}
	
	/**
	 * Set the <code>inputResultName</code> (result name to be returned when 
	 * a action / field error is found registered). Default to {@link Action#INPUT}
	 * 
	 * @param inputResultName what result name to use when there was validation error(s).
	 */
	public void setInputResultName(String inputResultName) {
		this.inputResultName = inputResultName;
	}
	
	/**
	 * Intercept {@link ActionInvocation} and processes the errors using the {@link org.apache.struts2.rest.handler.ContentTypeHandler}
	 * appropriate for the request.  
	 * 
	 * @return String result name
	 */
    protected String doIntercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();

        if (action instanceof ValidationAware) {
            ValidationAware validationAwareAction = (ValidationAware) action;

            if (validationAwareAction.hasErrors()) {
            	if (LOG.isDebugEnabled()) {
            		LOG.debug("Errors on action "+validationAwareAction+", returning result name 'input'");
            	}
            	ActionMapping mapping = (ActionMapping) ActionContext.getContext().get(ServletActionContext.ACTION_MAPPING);
            	String method = inputResultName;
                if (postMethodName.equals(mapping.getMethod())) {
                   method = newMethodName;
                } else if (putMethodName.equals(mapping.getMethod())) {
                   method = editMethodName;
                }
                
                
            	HttpHeaders info = new DefaultHttpHeaders()
            	    .disableCaching()
            	    .renderResult(method)
            	    .withStatus(validationFailureStatusCode);
            	
            	Map errors = new HashMap();
            	
            	errors.put("actionErrors", validationAwareAction.getActionErrors());
            	errors.put("fieldErrors", validationAwareAction.getFieldErrors());
            	return manager.handleResult(invocation, info, errors);
            }
        }

        return invocation.invoke();
    }

}
