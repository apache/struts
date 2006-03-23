package com.opensymphony.webwork.lifecycle;

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
 * @see com.opensymphony.webwork.dispatcher.FilterDispatcher
 * @since 2.2
 * @deprecated XWork IoC has been deprecated in favor of Spring.
 *             Please refer to the Spring-WebWork integration documentation for more info.
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
