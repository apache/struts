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

package org.apache.struts2.interceptor;

import java.io.Serializable;
import java.util.IdentityHashMap;
import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsException;
import org.apache.struts2.dispatcher.SessionMap;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * <!-- START SNIPPET: description -->
 *
 * This is designed to solve a few simple issues related to wizard-like functionality in Struts. One of those issues is
 * that some applications have a application-wide parameters commonly used, such <i>pageLen</i> (used for records per
 * page). Rather than requiring that each action check if such parameters are supplied, this interceptor can look for
 * specified parameters and pull them out of the session.
 *
 * <p/> This works by setting listed properties at action start with values from session/application attributes keyed
 * after the action's class, the action's name, or any supplied key. After action is executed all the listed properties
 * are taken back and put in session or application context.
 *
 * <p/> To make sure that each execution of the action is consistent it makes use of session-level locking. This way it
 * guarantees that each action execution is atomic at the session level. It doesn't guarantee application level
 * consistency however there has yet to be enough reasons to do so. Application level consistency would also be a big
 * performance overkill.
 *
 * <p/> Note that this interceptor takes a snapshot of action properties just before result is presented (using a {@link
 * PreResultListener}), rather than after action is invoked. There is a reason for that: At this moment we know that
 * action's state is "complete" as it's values may depend on the rest of the stack and specifically - on the values of
 * nested interceptors.
 *
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Interceptor parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>session - a list of action properties to be bound to session scope</li>
 *
 * <li>application - a list of action properties to be bound to application scope</li>
 *
 * <li>key - a session/application attribute key prefix, can contain following values:</li>
 *
 * <ul>
 *
 * <li>CLASS - that creates a unique key prefix based on action namespace and action class, it's a default value</li>
 *
 * <li>ACTION - creates a unique key prefix based on action namespace and action name</li>
 *
 * <li>any other value is taken literally as key prefix</li>
 *
 * </ul>
 *
 * <li>type - with one of the following</li>
 *
 * <ul>
 *
 * <li>start - means it's a start action of the wizard-like action sequence and all session scoped properties are reset
 * to their defaults</li>
 *
 * <li>end - means that session scoped properties are removed from session after action is run</li>
 *
 * <li>any other value throws IllegalArgumentException</li>
 *
 * </ul>
 *
 * <li>sessionReset - name of a parameter (defaults to 'session.reset') which if set, causes all session values to be reset to action's default values or application
 * scope values, note that it is similar to type="start" and in fact it does the same, but in our team it is sometimes
 * semantically preferred. We use session scope in two patterns - sometimes there are wizard-like action sequences that
 * have start and end, and sometimes we just want simply reset current session values.</li>
 *
 * <li>reset - boolean, defaults to false, if set, it has the same effect as setting all session values to be reset to action's default values or application.</li>
 *
 * <li>autoCreateSession - boolean value, sets if the session should be automatically created.</li>
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
 * There are no know extension points for this interceptor.
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;!-- As the filter and orderBy parameters are common for all my browse-type actions,
 *      you can move control to the scope interceptor. In the session parameter you can list
 *      action properties that are going to be automatically managed over session. You can
 *      do the same for application-scoped variables--&gt;
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="basicStack"/&gt;
 *     &lt;interceptor-ref name="hibernate"/&gt;
 *     &lt;interceptor-ref name="scope"&gt;
 *         &lt;param name="session"&gt;filter,orderBy&lt;/param&gt;
 *         &lt;param name="autoCreateSession"&gt;true&lt;/param&gt;
 *     &lt;/interceptor-ref&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 */
public class ScopeInterceptor extends AbstractInterceptor implements PreResultListener {

    private static final long serialVersionUID = 9120762699600054395L;

    private static final Logger LOG = LoggerFactory.getLogger(ScopeInterceptor.class);

    private String[] application = null;
    private String[] session = null;
    private String key;
    private String type = null;
    private boolean autoCreateSession = true;
    private String sessionReset = "session.reset";
    private boolean reset = false;

    /**
     * Sets a list of application scoped properties
     *
     * @param s A comma-delimited list
     */
    public void setApplication(String s) {
        if (s != null) {
            application = s.split(" *, *");
        }
    }

    /**
     * Sets a list of session scoped properties
     *
     * @param s A comma-delimited list
     */
    public void setSession(String s) {
        if (s != null) {
            session = s.split(" *, *");
        }
    }

    /**
     * Sets if the session should be automatically created
     *
     * @param value True if it should be created
     */
    public void setAutoCreateSession(String value) {
        if (value != null && value.length() > 0) {
            this.autoCreateSession = Boolean.valueOf(value).booleanValue();
        }
    }

    private String getKey(ActionInvocation invocation) {
        ActionProxy proxy = invocation.getProxy();
        if (key == null || "CLASS".equals(key)) {
            return "struts.ScopeInterceptor:" + proxy.getAction().getClass();
        } else if ("ACTION".equals(key)) {
            return "struts.ScopeInterceptor:" + proxy.getNamespace() + ":" + proxy.getActionName();
        }
        return key;
    }

    /**
     * The constructor
     */
    public ScopeInterceptor() {
        super();
    }

    // Since 2.0.7. Avoid null references on session serialization (WW-1803).
    private static class NULLClass implements Serializable {
      public String toString() {
        return "NULL";
      }
      public int hashCode() {
        return 1; // All instances of this class are equivalent
      }
      public boolean equals(Object obj) {
        return obj == null || (obj instanceof NULLClass);
      }
    }

    private static final Object NULL = new NULLClass();

    private static final Object nullConvert(Object o) {
        if (o == null) {
            return NULL;
        }

        if (o == NULL || NULL.equals(o)) {
            return null;
        }

        return o;
    }

    private static Map locks = new IdentityHashMap();

    static final void lock(Object o, ActionInvocation invocation) throws Exception {
        synchronized (o) {
            int count = 3;
            Object previous = null;
            while ((previous = locks.get(o)) != null) {
                if (previous == invocation) {
                    return;
                }
                if (count-- <= 0) {
                    locks.remove(o);
                    o.notify();

                    throw new StrutsException("Deadlock in session lock");
                }
                o.wait(10000);
            }
            ;
            locks.put(o, invocation);
        }
    }

    static final void unlock(Object o) {
        synchronized (o) {
            locks.remove(o);
            o.notify();
        }
    }

    protected void after(ActionInvocation invocation, String result) throws Exception {
        Map ses = ActionContext.getContext().getSession();
        if ( ses != null) {
            unlock(ses);
        }
    }


    protected void before(ActionInvocation invocation) throws Exception {
        invocation.addPreResultListener(this);
        Map ses = ActionContext.getContext().getSession();
        if (ses == null && autoCreateSession) {
            ses = new SessionMap(ServletActionContext.getRequest());
            ActionContext.getContext().setSession(ses);
        }

        if ( ses != null) {
            lock(ses, invocation);
        }

        String key = getKey(invocation);
        Map app = ActionContext.getContext().getApplication();
        final ValueStack stack = ActionContext.getContext().getValueStack();

        if (LOG.isDebugEnabled()) {
            LOG.debug("scope interceptor before");
        }

        if (application != null)
            for (int i = 0; i < application.length; i++) {
                String string = application[i];
                Object attribute = app.get(key + string);
                if (attribute != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("application scoped variable set " + string + " = " + String.valueOf(attribute));
                    }

                    stack.setValue(string, nullConvert(attribute));
                }
            }

        if (ActionContext.getContext().getParameters().get(sessionReset) != null) {
            return;
        }

        if (reset) {
            return;
        }

        if (ses == null) {
            LOG.debug("No HttpSession created... Cannot set session scoped variables");
            return;
        }

        if (session != null && (!"start".equals(type))) {
            for (int i = 0; i < session.length; i++) {
                String string = session[i];
                Object attribute = ses.get(key + string);
                if (attribute != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("session scoped variable set " + string + " = " + String.valueOf(attribute));
                    }
                    stack.setValue(string, nullConvert(attribute));
                }
            }
        }
    }

    public void setKey(String key) {
        this.key = key;
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.interceptor.PreResultListener#beforeResult(com.opensymphony.xwork2.ActionInvocation, java.lang.String)
     */
    public void beforeResult(ActionInvocation invocation, String resultCode) {
        String key = getKey(invocation);
        Map app = ActionContext.getContext().getApplication();
        final ValueStack stack = ActionContext.getContext().getValueStack();

        if (application != null)
            for (int i = 0; i < application.length; i++) {
                String string = application[i];
                Object value = stack.findValue(string);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("application scoped variable saved " + string + " = " + String.valueOf(value));
                }

                //if( value != null)
                app.put(key + string, nullConvert(value));
            }

        boolean ends = "end".equals(type);

        Map ses = ActionContext.getContext().getSession();
        if (ses != null) {

            if (session != null) {
                for (int i = 0; i < session.length; i++) {
                    String string = session[i];
                    if (ends) {
                        ses.remove(key + string);
                    } else {
                        Object value = stack.findValue(string);

                        if (LOG.isDebugEnabled()) {
                            LOG.debug("session scoped variable saved " + string + " = " + String.valueOf(value));
                        }

                        // Null value should be scoped too
                        //if( value != null)
                        ses.put(key + string, nullConvert(value));
                    }
                }
            }
            unlock(ses);
        } else {
            LOG.debug("No HttpSession created... Cannot save session scoped variables.");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("scope interceptor after (before result)");
        }
    }

    /**
     * @return The type of scope operation, "start" or "end"
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of scope operation
     *
     * @param type Either "start" or "end"
     */
    public void setType(String type) {
        type = type.toLowerCase();
        if ("start".equals(type) || "end".equals(type)) {
            this.type = type;
        } else {
            throw new IllegalArgumentException("Only start or end are allowed arguments for type");
        }
    }

    /**
     * @return Gets the session reset parameter name
     */
    public String getSessionReset() {
        return sessionReset;
    }

    /**
     * @param sessionReset The session reset parameter name
     */
    public void setSessionReset(String sessionReset) {
        this.sessionReset = sessionReset;
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.interceptor.Interceptor#intercept(com.opensymphony.xwork2.ActionInvocation)
     */
    public String intercept(ActionInvocation invocation) throws Exception {
        String result = null;
        Map ses = ActionContext.getContext().getSession();
        before(invocation);
        try {
            result = invocation.invoke();
            after(invocation, result);
        } finally {
            if (ses != null) {
                unlock(ses);
            }
        }

        return result;
    }

    /**
     * @return True if the scope is reset
     */
    public boolean isReset() {
        return reset;
    }

    /**
     * @param reset True if the scope should be reset
     */
    public void setReset(boolean reset) {
        this.reset = reset;
    }
}
