/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
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

package com.opensymphony.xwork2.spring.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.spring.SpringObjectFactory;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;

/**
 * <!-- START SNIPPET: description -->
 * TODO: Give a description of the Interceptor.
 * <!-- END SNIPPET: description -->
 *
 * <!-- START SNIPPET: parameters -->
 * TODO: Describe the paramters for this Interceptor.
 * <!-- END SNIPPET: parameters -->
 *
 * <!-- START SNIPPET: extending -->
 * TODO: Discuss some possible extension of the Interceptor.
 * <!-- END SNIPPET: extending -->
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;!-- TODO: Describe how the Interceptor reference will effect execution --&gt;
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *      TODO: fill in the interceptor reference.
 *     &lt;interceptor-ref name=""/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 * 
 * Autowires action classes to Spring beans.  The strategy for autowiring the beans can be configured
 * by setting the parameter on the interceptor.  Actions that need access to the <code>ActionContext</code>
 * can implements the <code>ApplicationContextAware</code> interface.  The context will also be placed on
 * the action context under the APPLICATION_CONTEXT attribute.
 *
 * @author Simon Stewart
 * @author Eric Hauser
 */
public class ActionAutowiringInterceptor extends AbstractInterceptor implements ApplicationContextAware {
    private static final Logger LOG = LoggerFactory.getLogger(ActionAutowiringInterceptor.class);

    public static final String APPLICATION_CONTEXT = "com.opensymphony.xwork2.spring.interceptor.ActionAutowiringInterceptor.applicationContext";

    private boolean initialized = false;
    private ApplicationContext context;
    private SpringObjectFactory factory;
    private Integer autowireStrategy;

    /**
     * @param autowireStrategy
     */
    public void setAutowireStrategy(Integer autowireStrategy) {
        this.autowireStrategy = autowireStrategy;
    }

    /**
     * Looks for the <code>ApplicationContext</code> under the attribute that the Spring listener sets in
     * the servlet context.  The configuration is done the first time here instead of in init() since the
     * <code>ActionContext</code> is not available during <code>Interceptor</code> initialization.
     * <p/>
     * Autowires the action to Spring beans and places the <code>ApplicationContext</code>
     * on the <code>ActionContext</code>
     * <p/>
     * TODO Should this check to see if the <code>SpringObjectFactory</code> has already been configured
     * instead of instantiating a new one?  Or is there a good reason for the interceptor to have it's own
     * factory?
     *
     * @param invocation
     * @throws Exception
     */
    @Override public String intercept(ActionInvocation invocation) throws Exception {
        if (!initialized) {
            ApplicationContext applicationContext = (ApplicationContext) ActionContext.getContext().getApplication().get(
                    WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

            if (applicationContext == null) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("ApplicationContext could not be found.  Action classes will not be autowired.");
                }
            } else {
                setApplicationContext(applicationContext);
                factory = new SpringObjectFactory();
                factory.setApplicationContext(getApplicationContext());
                if (autowireStrategy != null) {
                    factory.setAutowireStrategy(autowireStrategy.intValue());
                }
            }
            initialized = true;
        }

        if (factory != null) {
            Object bean = invocation.getAction();
            factory.autoWireBean(bean);
    
            ActionContext.getContext().put(APPLICATION_CONTEXT, context);
        }
        return invocation.invoke();
    }

    /**
     * @param applicationContext
     * @throws BeansException
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     * @return context
     */
    protected ApplicationContext getApplicationContext() {
        return context;
    }

}
