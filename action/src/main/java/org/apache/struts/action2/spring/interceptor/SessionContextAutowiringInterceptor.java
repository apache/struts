/*
 * Copyright (c) 2005 ePlus Corporation. All Rights Reserved.
 */
package org.apache.struts.action2.spring.interceptor;

import com.opensymphony.xwork.interceptor.AroundInterceptor;
import com.opensymphony.xwork.ActionInvocation;
import org.apache.struts.action2.spring.lifecycle.ApplicationContextSessionListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.Map;

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
 * SessionContextAutowiringInterceptor
 * Created : Aug 21, 2005 12:34:20 AM
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class SessionContextAutowiringInterceptor extends AroundInterceptor {
	
	private static final long serialVersionUID = 4352460072796174410L;
	
	private Integer autowireStrategy = new Integer(AutowireCapableBeanFactory.AUTOWIRE_BY_NAME);

    public void setAutowireStrategy(Integer autowireStrategy) {
        this.autowireStrategy = autowireStrategy;
    }

    protected void after(ActionInvocation dispatcher, String result) throws Exception {
    }

    protected void before(ActionInvocation invocation) throws Exception {
        Map session = invocation.getInvocationContext().getSession();
        ApplicationContext applicationContext = (ApplicationContext) session.get(ApplicationContextSessionListener.APP_CONTEXT_SESSION_KEY);
        AutowireCapableBeanFactory factory = findAutoWiringBeanFactory(applicationContext);
        factory.autowireBeanProperties(invocation.getAction(),autowireStrategy.intValue(),false);
    }

    protected AutowireCapableBeanFactory findAutoWiringBeanFactory(ApplicationContext context) {
		if (context instanceof AutowireCapableBeanFactory) {
			// Check the context
			return (AutowireCapableBeanFactory) context;
		} else if (context instanceof ConfigurableApplicationContext) {
			// Try and grab the beanFactory
			return ((ConfigurableApplicationContext) context).getBeanFactory();
		} else if (context.getParent() != null) {
			// And if all else fails, try again with the parent context
			return findAutoWiringBeanFactory(context.getParent());
		}
        return null;
    }
}
