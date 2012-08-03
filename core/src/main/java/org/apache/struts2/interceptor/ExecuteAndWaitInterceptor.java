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

import java.util.Collections;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.util.TokenHelper;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.views.freemarker.FreemarkerManager;
import org.apache.struts2.views.freemarker.FreemarkerResult;

import javax.servlet.http.HttpSession;


/**
 * <!-- START SNIPPET: description -->
 *
 * The ExecuteAndWaitInterceptor is great for running long-lived actions in the background while showing the user a nice
 * progress meter. This also prevents the HTTP request from timing out when the action takes more than 5 or 10 minutes.
 *
 * <p/> Using this interceptor is pretty straight forward. Assuming that you are including struts-default.xml, this
 * interceptor is already configured but is not part of any of the default stacks. Because of the nature of this
 * interceptor, it must be the <b>last</b> interceptor in the stack.
 *
 * <p/> This interceptor works on a per-session basis. That means that the same action name (myLongRunningAction, in the
 * above example) cannot be run more than once at a time in a given session. On the initial request or any subsequent
 * requests (before the action has completed), the <b>wait</b> result will be returned. <b>The wait result is
 * responsible for issuing a subsequent request back to the action, giving the effect of a self-updating progress
 * meter</b>.
 *
 * <p/> If no "wait" result is found, Struts will automatically generate a wait result on the fly. This result is
 * written in FreeMarker and cannot run unless FreeMarker is installed. If you don't wish to deploy with FreeMarker, you
 * must provide your own wait result. This is generally a good thing to do anyway, as the default wait page is very
 * plain.
 *
 * <p/>Whenever the wait result is returned, the <b>action that is currently running in the background will be placed on
 * top of the stack</b>. This allows you to display progress data, such as a count, in the wait page. By making the wait
 * page automatically reload the request to the action (which will be short-circuited by the interceptor), you can give
 * the appearance of an automatic progress meter.
 *
 * <p/>This interceptor also supports using an initial wait delay. An initial delay is a time in milliseconds we let the
 * server wait before the wait page is shown to the user. During the wait this interceptor will wake every 100 millis
 * to check if the background process is done premature, thus if the job for some reason doesn't take to long the wait
 * page is not shown to the user.
 * <br/> This is useful for e.g. search actions that have a wide span of execution time. Using a delay time of 2000
 * millis we ensure the user is presented fast search results immediately and for the slow results a wait page is used.
 *
 * <p/><b>Important</b>: Because the action will be running in a seperate thread, you can't use ActionContext because it
 * is a ThreadLocal. This means if you need to access, for example, session data, you need to implement SessionAware
 * rather than calling ActionContext.getSession().
 *
 * <p/>The thread kicked off by this interceptor will be named in the form <b><u>actionName</u>BackgroundProcess</b>.
 * For example, the <i>search</i> action would run as a thread named <i>searchBackgroundProcess</i>.
 *
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Interceptor parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>threadPriority (optional) - the priority to assign the thread. Default is <code>Thread.NORM_PRIORITY</code>.</li>
 * <li>delay (optional) - an initial delay in millis to wait before the wait page is shown (returning <code>wait</code> as result code). Default is no initial delay.</li>
 * <li>delaySleepInterval (optional) - only used with delay. Used for waking up at certain intervals to check if the background process is already done. Default is 100 millis.</li>
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
 * If you wish to make special preparations before and/or after the invocation of the background thread, you can extend
 * the BackgroundProcess class and implement the beforeInvocation() and afterInvocation() methods. This may be useful
 * for obtaining and releasing resources that the background process will need to execute successfully. To use your
 * background process extension, extend ExecuteAndWaitInterceptor and implement the getNewBackgroundProcess() method.
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="completeStack"/&gt;
 *     &lt;interceptor-ref name="execAndWait"/&gt;
 *     &lt;result name="wait"&gt;longRunningAction-wait.jsp&lt;/result&gt;
 *     &lt;result name="success"&gt;longRunningAction-success.jsp&lt;/result&gt;
 * &lt;/action&gt;
 *
 * &lt;%@ taglib prefix="s" uri="/struts" %&gt;
 * &lt;html&gt;
 *   &lt;head&gt;
 *     &lt;title&gt;Please wait&lt;/title&gt;
 *     &lt;meta http-equiv="refresh" content="5;url=&lt;s:url includeParams="all" /&gt;"/&gt;
 *   &lt;/head&gt;
 *   &lt;body&gt;
 *     Please wait while we process your request.
 *     Click &lt;a href="&lt;s:url includeParams="all" /&gt;">&lt;/a&gt; if this page does not reload automatically.
 *   &lt;/body&gt;
 * &lt;/html&gt;
 * </pre>
 *
 * <p/> <u>Example code2:</u>
 * This example will wait 2 second (2000 millis) before the wait page is shown to the user. Therefore
 * if the long process didn't last long anyway the user isn't shown a wait page.
 *
 * <pre>
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="completeStack"/&gt;
 *     &lt;interceptor-ref name="execAndWait"&gt;
 *         &lt;param name="delay"&gt;2000&lt;param&gt;
 *     &lt;interceptor-ref&gt;
 *     &lt;result name="wait"&gt;longRunningAction-wait.jsp&lt;/result&gt;
 *     &lt;result name="success"&gt;longRunningAction-success.jsp&lt;/result&gt;
 * &lt;/action&gt;
 * </pre>
 *
 * <p/> <u>Example code3:</u>
 * This example will wait 1 second (1000 millis) before the wait page is shown to the user.
 * And at every 50 millis this interceptor will check if the background process is done, if so
 * it will return before the 1 second has elapsed, and the user isn't shown a wait page.
 *
 * <pre>
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="completeStack"/&gt;
 *     &lt;interceptor-ref name="execAndWait"&gt;
 *         &lt;param name="delay"&gt;1000&lt;param&gt;
 *         &lt;param name="delaySleepInterval"&gt;50&lt;param&gt;
 *     &lt;interceptor-ref&gt;
 *     &lt;result name="wait"&gt;longRunningAction-wait.jsp&lt;/result&gt;
 *     &lt;result name="success"&gt;longRunningAction-success.jsp&lt;/result&gt;
 * &lt;/action&gt;
 * </pre>
 *
 * <!-- END SNIPPET: example -->
 *
 */
public class ExecuteAndWaitInterceptor extends MethodFilterInterceptor {

    private static final long serialVersionUID = -2754639196749652512L;

    private static final Logger LOG = LoggerFactory.getLogger(ExecuteAndWaitInterceptor.class);

    public static final String KEY = "__execWait";
    public static final String WAIT = "wait";
    protected int delay;
    protected int delaySleepInterval = 100; // default sleep 100 millis before checking if background process is done
    protected boolean executeAfterValidationPass = false;

    private int threadPriority = Thread.NORM_PRIORITY;

    private Container container;

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    /* (non-Javadoc)
    * @see com.opensymphony.xwork2.interceptor.Interceptor#init()
    */
    public void init() {
    }

    /**
     * Creates a new background process
     *
     * @param name The process name
     * @param actionInvocation The action invocation
     * @param threadPriority The thread priority
     * @return The new process
     */
    protected BackgroundProcess getNewBackgroundProcess(String name, ActionInvocation actionInvocation, int threadPriority) {
        return new BackgroundProcess(name + "BackgroundThread", actionInvocation, threadPriority);
    }

    /**
     * Returns the name to associate the background process.  Override to change the way background processes
     * are mapped to requests.
     *
     * @return the name of the background thread
     */
    protected String getBackgroundProcessName(ActionProxy proxy) {
        return proxy.getActionName();
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.interceptor.MethodFilterInterceptor#doIntercept(com.opensymphony.xwork2.ActionInvocation)
     */
    protected String doIntercept(ActionInvocation actionInvocation) throws Exception {
        ActionProxy proxy = actionInvocation.getProxy();
        String name = getBackgroundProcessName(proxy);
        ActionContext context = actionInvocation.getInvocationContext();
        Map session = context.getSession();
        HttpSession httpSession = ServletActionContext.getRequest().getSession(true);

        Boolean secondTime  = true;
        if (executeAfterValidationPass) {
            secondTime = (Boolean) context.get(KEY);
            if (secondTime == null) {
                context.put(KEY, true);
                secondTime = false;
            } else {
                secondTime = true;
                context.put(KEY, null);
            }
        }

        //sync on the real HttpSession as the session from the context is a wrap that is created
        //on every request
        synchronized (httpSession) {
            BackgroundProcess bp = (BackgroundProcess) session.get(KEY + name);

            if ((!executeAfterValidationPass || secondTime) && bp == null) {
                bp = getNewBackgroundProcess(name, actionInvocation, threadPriority);
                session.put(KEY + name, bp);
                performInitialDelay(bp); // first time let some time pass before showing wait page
                secondTime = false;
            }

            if ((!executeAfterValidationPass || !secondTime) && bp != null && !bp.isDone()) {
                actionInvocation.getStack().push(bp.getAction());

				final String token = TokenHelper.getToken();
				if (token != null) {
					TokenHelper.setSessionToken(TokenHelper.getTokenName(), token);
                }

                Map results = proxy.getConfig().getResults();
                if (!results.containsKey(WAIT)) {
                    if (LOG.isWarnEnabled()) {
                	LOG.warn("ExecuteAndWait interceptor has detected that no result named 'wait' is available. " +
                            "Defaulting to a plain built-in wait page. It is highly recommend you " +
                            "provide an action-specific or global result named '" + WAIT +
                            "'.");
                    }
                    // no wait result? hmm -- let's try to do dynamically put it in for you!

                    //we used to add a fake "wait" result here, since the configuration is unmodifiable, that is no longer
                    //an option, see WW-3068
                    FreemarkerResult waitResult = new FreemarkerResult();
                    container.inject(waitResult);
                    waitResult.setLocation("/org/apache/struts2/interceptor/wait.ftl");
                    waitResult.execute(actionInvocation);

                    return Action.NONE;
                }

                return WAIT;
            } else if ((!executeAfterValidationPass || !secondTime) && bp != null && bp.isDone()) {
                session.remove(KEY + name);
                actionInvocation.getStack().push(bp.getAction());

                // if an exception occured during action execution, throw it here
                if (bp.getException() != null) {
                    throw bp.getException();
                }

                return bp.getResult();
            } else {
                // this is the first instance of the interceptor and there is no existing action
                // already run in the background, so let's just let this pass through. We assume
                // the action invocation will be run in the background on the subsequent pass through
                // this interceptor
                return actionInvocation.invoke();
            }
        }
    }


    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.interceptor.Interceptor#destroy()
     */
    public void destroy() {
    }

    /**
     * Performs the initial delay.
     * <p/>
     * When this interceptor is executed for the first time this methods handles any provided initial delay.
     * An initial delay is a time in miliseconds we let the server wait before we continue.
     * <br/> During the wait this interceptor will wake every 100 millis to check if the background
     * process is done premature, thus if the job for some reason doesn't take to long the wait
     * page is not shown to the user.
     *
     * @param bp the background process
     * @throws InterruptedException is thrown by Thread.sleep
     */
    protected void performInitialDelay(BackgroundProcess bp) throws InterruptedException {
        if (delay <= 0 || delaySleepInterval <= 0) {
            return;
        }

        int steps = delay / delaySleepInterval;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Delaying for " + delay + " millis. (using " + steps + " steps)");
        }
        int step;
        for (step = 0; step < steps && !bp.isDone(); step++) {
            Thread.sleep(delaySleepInterval);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Sleeping ended after " + step + " steps and the background process is " + (bp.isDone() ? " done" : " not done"));
        }
    }

    /**
     * Sets the thread priority of the background process.
     *
     * @param threadPriority the priority from <code>Thread.XXX</code>
     */
    public void setThreadPriority(int threadPriority) {
        this.threadPriority = threadPriority;
    }

    /**
     * Sets the initial delay in millis (msec).
     *
     * @param delay in millis. (0 for not used)
     */
    public void setDelay(int delay) {
        this.delay = delay;
    }

    /**
     * Sets the sleep interval in millis (msec) when performing the initial delay.
     *
     * @param delaySleepInterval in millis (0 for not used)
     */
    public void setDelaySleepInterval(int delaySleepInterval) {
        this.delaySleepInterval = delaySleepInterval;
    }

    /**
     * Whether to start the background process after the second pass (first being validation)
     * or not
     *
     * @param executeAfterValidationPass the executeAfterValidationPass to set
     */
    public void setExecuteAfterValidationPass(boolean executeAfterValidationPass) {
        this.executeAfterValidationPass = executeAfterValidationPass;
    }


}
