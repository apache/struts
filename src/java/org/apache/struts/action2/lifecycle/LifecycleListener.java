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
package org.apache.struts.action2.lifecycle;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * The default mechanism for initializing the session and application scopes for XWork's IoC
 * container. If for any reason you don't wish to use session-scoped objects, you can include
 * just the {@link ApplicationLifecycleListener} rather than this class, but generally this
 * class will be fine for almost all needs.
 *
 * @author Patrick Lightbody
 * @see ApplicationLifecycleListener
 * @see SessionLifecycleListener
 * @see org.apache.struts.action2.dispatcher.FilterDispatcher
 * @since 2.2
 * @deprecated XWork IoC has been deprecated in favor of Spring.
 *             Please refer to the Spring-Struts integration documentation for more info.
 */
public class LifecycleListener extends ApplicationLifecycleListener implements HttpSessionListener {
    SessionLifecycleListener session = new SessionLifecycleListener();

    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        session.sessionCreated(httpSessionEvent);
    }

    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        session.sessionDestroyed(httpSessionEvent);
    }
}
