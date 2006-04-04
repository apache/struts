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
package org.apache.struts.action2.interceptor;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.config.entities.ResultConfig;
import com.opensymphony.xwork.interceptor.Interceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;
import java.util.Map;


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
 * rather than calling ActionContext.getSesion().
 *
 * <p/>The thread kicked off by this interceptor will be named in the form <b><u>actionName</u>BrackgroundProcess</b>.
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
 * &lt;%@ taglib prefix="ww" uri="/struts" %&gt;
 * &lt;html&gt;
 *   &lt;head&gt;
 *     &lt;title&gt;Please wait&lt;/title&gt;
 *     &lt;meta http-equiv="refresh" content="5;url=&lt;a:url includeParams="all" /&gt;"/&gt;
 *   &lt;/head&gt;
 *   &lt;body&gt;
 *     Please wait while we process your request.
 *     Click &lt;a href="&lt;a:url includeParams="all" /&gt;">&lt;/a&gt; if this page does not reload automatically.
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
 * @author <a href="plightbo@gmail.com">Pat Lightbody</a>
 * @author Rainer Hermanns
 * @author Claus Ibsen
 */
public class ExecuteAndWaitInterceptor implements Interceptor {
	
	private static final long serialVersionUID = -2754639196749652512L;

	private static final Log LOG = LogFactory.getLog(ExecuteAndWaitInterceptor.class);

    public static final String KEY = "__execWait";
    public static final String WAIT = "wait";
    protected int delay;
    protected int delaySleepInterval = 100; // default sleep 100 millis before checking if background process is done

    private int threadPriority = Thread.NORM_PRIORITY;

    public void init() {
    }

    protected BackgroundProcess getNewBackgroundProcess(String name, ActionInvocation actionInvocation, int threadPriority) {
        return new BackgroundProcess(name + "BackgroundThread", actionInvocation, threadPriority);
    }

    public String intercept(ActionInvocation actionInvocation) throws Exception {
        ActionProxy proxy = actionInvocation.getProxy();
        String name = proxy.getActionName();
        ActionContext context = actionInvocation.getInvocationContext();
        Map session = context.getSession();

        synchronized (session) {
            BackgroundProcess bp = (BackgroundProcess) session.get(KEY + name);

            if (bp == null) {
                bp = getNewBackgroundProcess(name, actionInvocation, threadPriority);
                session.put(KEY + name, bp);
                performInitialDelay(bp); // first time let some time pass before showing wait page
            }

            if (!bp.isDone()) {
                actionInvocation.getStack().push(bp.getAction());
                Map results = proxy.getConfig().getResults();
                if (!results.containsKey(WAIT)) {
                    LOG.warn("ExecuteAndWait interceptor has detected that no result named 'wait' is available. " +
                            "Defaulting to a plain built-in wait page. It is highly recommend you " +
                            "provide an action-specific or global result named '" + WAIT +
                            "'! This requires FreeMarker support and won't work if you don't have it installed");
                    // no wait result? hmm -- let's try to do dynamically put it in for you!
                    ResultConfig rc = new ResultConfig(WAIT, "org.apache.struts.action2.views.freemarker.FreemarkerResult",
                            Collections.singletonMap("location", "org/apache/struts/action2/interceptor/wait.ftl"));
                    results.put(WAIT, rc);
                }

                return WAIT;
            } else {
                session.remove(KEY + name);
                actionInvocation.getStack().push(bp.getAction());

                // if an exception occured during action execution, throw it here
                if (bp.getException() != null) {
                    throw bp.getException();
                }

                return bp.getResult();
            }
        }
    }


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
}
