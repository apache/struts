/*
 * Copyright 2002-2007,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.Validateable;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.opensymphony.xwork2.interceptor.PrefixMethodInvocationUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * <!-- START SNIPPET: description -->
 *
 * This interceptor runs the action through the standard validation framework, which in turn checks the action against
 * any validation rules (found in files such as <i>ActionClass-validation.xml</i>) and adds field-level and action-level
 * error messages (provided that the action implements {@link com.opensymphony.xwork2.ValidationAware}). This interceptor
 * is often one of the last (or second to last) interceptors applied in a stack, as it assumes that all values have
 * already been set on the action.
 *
 * <p/>This interceptor does nothing if the name of the method being invoked is specified in the <b>excludeMethods</b>
 * parameter. <b>excludeMethods</b> accepts a comma-delimited list of method names. For example, requests to
 * <b>foo!input.action</b> and <b>foo!back.action</b> will be skipped by this interceptor if you set the
 * <b>excludeMethods</b> parameter to "input, back".
 * 
 * </ol>
 * 
 * <p/> The workflow of the action request does not change due to this interceptor. Rather,
 * this interceptor is often used in conjuction with the <b>workflow</b> interceptor.
 *
 * <p/>
 * 
 * <b>NOTE:</b> As this method extends off MethodFilterInterceptor, it is capable of
 * deciding if it is applicable only to selective methods in the action class. See
 * <code>MethodFilterInterceptor</code> for more info.
 *
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Interceptor parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>alwaysInvokeValidate - Defaults to true. If true validate() method will always
 * be invoked, otherwise it will not.</li>
 *
 * <li>programmatic - Defaults to true. If true and the action is Validateable call validate(),
 * and any method that starts with "validate".
 * </li>
 * 
 * <li>declarative - Defaults to true. Perform validation based on xml or annotations.</li>
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
 * &lt;-- in the following case myMethod of the action class will not
 *        get validated --&gt;
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="params"/&gt;
 *     &lt;interceptor-ref name="validation"&gt;
 *         &lt;param name="excludeMethods"&gt;myMethod&lt;/param&gt;
 *     &lt;/interceptor-ref&gt;
 *     &lt;interceptor-ref name="workflow"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * 
 * &lt;-- in the following case only annotated methods of the action class will
 *        be validated --&gt;
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="params"/&gt;
 *     &lt;interceptor-ref name="validation"&gt;
 *         &lt;param name="validateAnnotatedMethodOnly"&gt;true&lt;/param&gt;
 *     &lt;/interceptor-ref&gt;
 *     &lt;interceptor-ref name="workflow"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 *
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Jason Carreira
 * @author Rainer Hermanns
 * @author <a href='mailto:the_mindstorm[at]evolva[dot]ro'>Alexandru Popescu</a>
 * @see ActionValidatorManager
 * @see com.opensymphony.xwork2.interceptor.DefaultWorkflowInterceptor
 */
public class ValidationInterceptor extends MethodFilterInterceptor {

    private boolean validateAnnotatedMethodOnly;
    
    private ActionValidatorManager actionValidatorManager;
    
    private static final Logger LOG = LoggerFactory.getLogger(ValidationInterceptor.class);
    
    private final static String VALIDATE_PREFIX = "validate";
    private final static String ALT_VALIDATE_PREFIX = "validateDo";
    
    private boolean alwaysInvokeValidate = true;
    private boolean programmatic = true;
    private boolean declarative = true;

    @Inject
    public void setActionValidatorManager(ActionValidatorManager mgr) {
        this.actionValidatorManager = mgr;
    }
    
    /**
     * Determines if {@link Validateable}'s <code>validate()</code> should be called,
     * as well as methods whose name that start with "validate". Defaults to "true".
     * 
     * @param programmatic <tt>true</tt> then <code>validate()</code> is invoked.
     */
    public void setProgrammatic(boolean programmatic) {
        this.programmatic = programmatic;
    }

    /**
     * Determines if validation based on annotations or xml should be performed. Defaults 
     * to "true".
     * 
     * @param declarative <tt>true</tt> then perform validation based on annotations or xml.
     */
    public void setDeclarative(boolean declarative) {
        this.declarative = declarative;
    }

    /**
     * Determines if {@link Validateable}'s <code>validate()</code> should always 
     * be invoked. Default to "true".
     * 
     * @param alwaysInvokeValidate <tt>true</tt> then <code>validate()</code> is always invoked.
     */
    public void setAlwaysInvokeValidate(String alwaysInvokeValidate) {
            this.alwaysInvokeValidate = Boolean.parseBoolean(alwaysInvokeValidate);
    }

    /**
     * Gets if <code>validate()</code> should always be called or only per annotated method.
     *
     * @return <tt>true</tt> to only validate per annotated method, otherwise <tt>false</tt> to always validate.
     */
    public boolean isValidateAnnotatedMethodOnly() {
        return validateAnnotatedMethodOnly;
    }

    /**
     * Determine if <code>validate()</code> should always be called or only per annotated method.
     * Default to <tt>false</tt>.
     *
     * @param validateAnnotatedMethodOnly  <tt>true</tt> to only validate per annotated method, otherwise <tt>false</tt> to always validate.
     */
    public void setValidateAnnotatedMethodOnly(boolean validateAnnotatedMethodOnly) {
        this.validateAnnotatedMethodOnly = validateAnnotatedMethodOnly;
    }

    /**
     * Gets the current action and its context and delegates to {@link ActionValidatorManager} proper validate method.
     *
     * @param invocation  the execution state of the Action.
     * @throws Exception if an error occurs validating the action.
     */
    protected void doBeforeInvocation(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        ActionProxy proxy = invocation.getProxy();

        //the action name has to be from the url, otherwise validators that use aliases, like
        //MyActio-someaction-validator.xml will not be found, see WW-3194
        //UPDATE:  see WW-3753
        String context = this.getValidationContext(proxy);
        String method = proxy.getMethod();

        if (log.isDebugEnabled()) {
            log.debug("Validating "
                    + invocation.getProxy().getNamespace() + "/" + invocation.getProxy().getActionName() + " with method "+ method +".");
        }
        

        if (declarative) {
           if (validateAnnotatedMethodOnly) {
               actionValidatorManager.validate(action, context, method);
           } else {
               actionValidatorManager.validate(action, context);
           }
       }    
        
        if (action instanceof Validateable && programmatic) {
            // keep exception that might occured in validateXXX or validateDoXXX
            Exception exception = null; 
            
            Validateable validateable = (Validateable) action;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Invoking validate() on action "+validateable);
            }
            
            try {
                PrefixMethodInvocationUtil.invokePrefixMethod(
                                invocation, 
                                new String[] { VALIDATE_PREFIX, ALT_VALIDATE_PREFIX });
            }
            catch(Exception e) {
                // If any exception occurred while doing reflection, we want 
                // validate() to be executed
                if (LOG.isWarnEnabled()) {
                    LOG.warn("an exception occured while executing the prefix method", e);
                }
                exception = e;
            }
            
            
            if (alwaysInvokeValidate) {
                validateable.validate();
            }
            
            if (exception != null) { 
                // rethrow if something is wrong while doing validateXXX / validateDoXXX 
                throw exception;
            }
        }
    }

    @Override
    protected String doIntercept(ActionInvocation invocation) throws Exception {
        doBeforeInvocation(invocation);
        
        return invocation.invoke();
    }
    
    /**
     * Returns the context that will be used by the
     * {@link ActionValidatorManager} to associate the action invocation with
     * the appropriate {@link ValidatorConfig ValidatorConfigs}.
     * <p>
     * The context returned is used in the pattern
     * <i>ActionClass-context-validation.xml</i>
     * <p>
     * The default context is the action name from the URL, but the method can
     * be overridden to implement custom contexts.
     * <p>
     * This can be useful in cases in which a single action and a single model
     * require vastly different validation based on some condition.
     * 
     * @return the Context
     */
    protected String getValidationContext(ActionProxy proxy) {
        // This method created for WW-3753
        return proxy.getActionName();
    }

}
