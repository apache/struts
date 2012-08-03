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
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionChainResult;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.Unchainable;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;

import java.util.*;


/**
 * <!-- START SNIPPET: description -->
 * <p/>
 * An interceptor that copies all the properties of every object in the value stack to the currently executing object,
 * except for any object that implements {@link Unchainable}. A collection of optional <i>includes</i> and
 * <i>excludes</i> may be provided to control how and which parameters are copied. Only includes or excludes may be
 * specified. Specifying both results in undefined behavior. See the javadocs for {@link ReflectionProvider#copy(Object, Object,
 * java.util.Map, java.util.Collection, java.util.Collection)} for more information.
 * <p/>
 * <p/>
 * <b>Note:</b> It is important to remember that this interceptor does nothing if there are no objects already on the stack.
 * <br/>This means two things:
 * <br/><b>One</b>, you can safely apply it to all your actions without any worry of adverse affects.
 * <br/><b/>Two</b>, it is up to you to ensure an object exists in the stack prior to invoking this action. The most typical way this is done
 * is through the use of the <b>chain</b> result type, which combines with this interceptor to make up the action
 * chaining feature.
 * <p/>
 * <b>Note:</b> By default Errors, Field errors and Message aren't copied during chaining, to change the behaviour you can specify
 * the below three constants in struts.properties or struts.xml:
 * <ul>
 * <li>struts.xwork.chaining.copyErrors - set to true to copy Action Errors</li>
 * <li>struts.xwork.chaining.copyFieldErrors - set to true to copy Field Errors</li>
 * <li>struts.xwork.chaining.copyMessages - set to true to copy Action Messages</li>
 * </ul>
 * <p>
 * <p>
 * <u>Example:</u>
 * <pre>
 * &lt;constant name="struts.xwork.chaining.copyErrors" value="true"/&gt;
 * </pre>
 * </p>
 * <b>Note:</b> By default actionErrors and actionMessages are excluded when copping object's properties.
 * </p>
 * <!-- END SNIPPET: description -->
 * <u>Interceptor parameters:</u>
 * <!-- START SNIPPET: parameters -->
 * <ul>
 * <li>excludes (optional) - the list of parameter names to exclude from copying (all others will be included).</li>
 * <li>includes (optional) - the list of parameter names to include when copying (all others will be excluded).</li>
 * </ul>
 * <!-- END SNIPPET: parameters -->
 * <u>Extending the interceptor:</u>
 * <!-- START SNIPPET: extending -->
 * <p>
 * There are no known extension points to this interceptor.
 * </p>
 * <!-- END SNIPPET: extending -->
 * <u>Example code:</u>
 * <pre>
 * <!-- START SNIPPET: example -->
 * <p/>
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="basicStack"/&gt;
 *     &lt;result name="success" type="chain"&gt;otherAction&lt;/result&gt;
 * &lt;/action&gt;
 * <p/>
 * &lt;action name="otherAction" class="com.examples.OtherAction"&gt;
 *     &lt;interceptor-ref name="chain"/&gt;
 *     &lt;interceptor-ref name="basicStack"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <p/>
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author mrdon
 * @author tm_jee ( tm_jee(at)yahoo.co.uk )
 * @see com.opensymphony.xwork2.ActionChainResult
 */
public class ChainingInterceptor extends AbstractInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(ChainingInterceptor.class);

    private static final String ACTION_ERRORS = "actionErrors";
    private static final String FIELD_ERRORS = "fieldErrors";
    private static final String ACTION_MESSAGES = "actionMessages";

    private boolean copyMessages = false;
    private boolean copyErrors = false;
    private boolean copyFieldErrors = false;

    protected Collection<String> excludes;

    protected Collection<String> includes;
    protected ReflectionProvider reflectionProvider;

    @Inject
    public void setReflectionProvider(ReflectionProvider prov) {
        this.reflectionProvider = prov;
    }

    @Inject(value = "struts.xwork.chaining.copyErrors", required = false)
    public void setCopyErrors(String copyErrors) {
        this.copyErrors = "true".equalsIgnoreCase(copyErrors);
    }

    @Inject(value = "struts.xwork.chaining.copyFieldErrors", required = false)
    public void setCopyFieldErrors(String copyFieldErrors) {
        this.copyFieldErrors = "true".equalsIgnoreCase(copyFieldErrors);
    }

    @Inject(value = "struts.xwork.chaining.copyMessages", required = false)
    public void setCopyMessages(String copyMessages) {
        this.copyMessages = "true".equalsIgnoreCase(copyMessages);
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        ValueStack stack = invocation.getStack();
        CompoundRoot root = stack.getRoot();
        if (shouldCopyStack(invocation, root)) {
            copyStack(invocation, root);
        }
        return invocation.invoke();
    }

    private void copyStack(ActionInvocation invocation, CompoundRoot root) {
        List list = prepareList(root);
        Map<String, Object> ctxMap = invocation.getInvocationContext().getContextMap();
        for (Object object : list) {
            if (shouldCopy(object)) {
                reflectionProvider.copy(object, invocation.getAction(), ctxMap, prepareExcludes(), includes);
            }
        }
    }

    private Collection<String> prepareExcludes() {
        Collection<String> localExcludes = excludes;
        if (!copyErrors || !copyMessages ||!copyFieldErrors) {
            if (localExcludes == null) {
                localExcludes = new HashSet<String>();
                if (!copyErrors) {
                    localExcludes.add(ACTION_ERRORS);
                }
                if (!copyMessages) {
                    localExcludes.add(ACTION_MESSAGES);
                }
                if (!copyFieldErrors) {
                    localExcludes.add(FIELD_ERRORS);
                }
            }
        }
        return localExcludes;
    }

    private boolean shouldCopy(Object o) {
        return o != null && !(o instanceof Unchainable);
    }

    @SuppressWarnings("unchecked")
    private List prepareList(CompoundRoot root) {
        List list = new ArrayList(root);
        list.remove(0);
        Collections.reverse(list);
        return list;
    }

    private boolean shouldCopyStack(ActionInvocation invocation, CompoundRoot root) throws Exception {
        Result result = invocation.getResult();
        return root.size() > 1 && (result == null || ActionChainResult.class.isAssignableFrom(result.getClass()));
    }

    /**
     * Gets list of parameter names to exclude
     *
     * @return the exclude list
     */
    public Collection<String> getExcludes() {
        return excludes;
    }

    /**
     * Sets the list of parameter names to exclude from copying (all others will be included).
     *
     * @param excludes the excludes list
     */
    public void setExcludes(Collection<String> excludes) {
        this.excludes = excludes;
    }

    /**
     * Gets list of parameter names to include
     *
     * @return the include list
     */
    public Collection<String> getIncludes() {
        return includes;
    }

    /**
     * Sets the list of parameter names to include when copying (all others will be excluded).
     *
     * @param includes the includes list
     */
    public void setIncludes(Collection<String> includes) {
        this.includes = includes;
    }

}
