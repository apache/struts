/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
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
package org.apache.struts.action2.spring;

import org.apache.struts.action2.spring.lifecycle.ApplicationContextSessionListener;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.spring.SpringProxyableObjectFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * SessionContextSpringProxyableObjectFactory
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class SessionContextSpringProxyableObjectFactory extends SpringProxyableObjectFactory {
    private static final Log log = LogFactory.getLog(SessionContextSpringProxyableObjectFactory.class);

    protected ApplicationContext getApplicationContext(Map context) {
        if (log.isDebugEnabled()) {
            log.debug("Getting the session-scoped app context");
        }
        if (context == null) {
            return appContext;
        }
        Map session = (Map) context.get(ActionContext.SESSION);
        if (session == null) {
            log.warn("There is no session map in the ActionContext.");
            return appContext;
        }
        ApplicationContext sessionContext = (ApplicationContext) session.get(ApplicationContextSessionListener.APP_CONTEXT_SESSION_KEY);
        if (sessionContext == null) {
            throw new IllegalStateException("There is no application context in the user's session.");
        }
        return sessionContext;
    }

    public Object buildBean(String beanName, Map extraContext) throws Exception {
        Object bean = super.buildBean(beanName, extraContext);
        AutowireCapableBeanFactory autoWiringBeanFactory = findAutoWiringBeanFactory(getApplicationContext(extraContext));
        return autoWireBean(bean, autoWiringBeanFactory);
    }

}
