/*
 * Copyright (c) 2002-2003, Atlassian Software Systems Pty Ltd All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *     * Neither the name of Atlassian Software Systems Pty Ltd nor the names of
 * its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.opensymphony.xwork2.util.profiling;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;


/**
 * A timer stack.
 *
 * <p />
 * 
 * <!-- START SNIPPET: profilingAspect_struts2 -->
 * 
 * Struts2 profiling aspects involves the following :-
 * <ul>
 *   <li>ActionContextCleanUp</li>
 *   <li>FreemarkerPageFilter</li>
 *   <li>DispatcherFilter</li>
 *   <ul>
 *      <li>Dispatcher</li>
 *      <ul>
 *          <li>creation of DefaultActionProxy</li>
 *          <ul>
 *              <li>creation of DefaultActionInvocation</li>
 *              <ul>
 *   	          <li>creation of Action</li>
 *              </ul>
 *          </ul>
 *          <li>execution of DefaultActionProxy</li>
 *          <ul>
 *              <li>invocation of DefaultActionInvocation</li>
 *              <ul>
 *                  <li>invocation of Interceptors</li>
 *                  <li>invocation of Action</li>
 *                  <li>invocation of PreResultListener</li>
 *                  <li>invocation of Result</li>
 *              </ul>
 *          </ul>
 *      </ul>
 *   </ul>
 * </ul>
 * 
 * <!-- END SNIPPET: profilingAspect_struts2 -->
 *
 *
 * <!-- START SNIPPET: profilingAspect_xwork -->
 * 
 * XWork2 profiling aspects involves the following :-
 * <ul>
 *   <ul>
 *      <li>creation of DefaultActionProxy</li>
 *      <ul>
 *         <li>creation of DefaultActionInvocation</li>
 *         <ul>
 *   	      <li>creation of Action</li>
 *        </ul>
 *      </ul>
 *      <li>execution of DefaultActionProxy</li>
 *      <ul>
 *         <li>invocation of DefaultActionInvocation</li>
 *         <ul>
 *           <li>invocation of Interceptors</li>
 *           <li>invocation of Action</li>
 *           <li>invocation of PreResultListener</li>
 *           <li>invocation of Result</li>
 *        </ul>
 *     </ul>
 *   </ul>
 * </ul>
 * 
 * <!-- END SNIPPET: profilingAspect_xwork -->
 * 
 * 
 * <!-- START SNIPPET: activationDescription -->
 * 
 * Activating / Deactivating of the profiling feature could be done through:- 
 * 
 * <!-- END SNIPPET: activationDescription -->
 * 
 * <p/>
 * 
 * System properties:- <p/>
 * <pre>
 * <!-- START SNIPPET: activationThroughSystemProperty -->
 * 
 *  -Dxwork.profile.activate=true
 *  
 * <!-- END SNIPPET: activationThroughSystemProperty --> 
 * </pre>
 * 
 * <!-- START SNIPPET: activationThroughSystemPropertyDescription -->
 * 
 * This could be done in the container startup script eg. CATALINA_OPTS in catalina.sh 
 * (tomcat) or using "java -Dxwork.profile.activate=true -jar start.jar" (jetty) 
 * 
 * <!-- END SNIPPET: activationThroughSystemPropertyDescription -->
 * 
 * <p/>
 * Code :- <p/>
 * <pre>
 * <!-- START SNIPPET: activationThroughCode -->
 *   
 *  UtilTimerStack.setActivate(true);
 *    
 * <!-- END SNIPPET: activationThroughCode --> 
 * </pre>
 * 
 * 
 * 
 * <!-- START SNIPPET: activationThroughCodeDescription -->
 * 
 * This could be done in a static block, in a Spring bean with lazy-init="false", 
 * in a Servlet with init-on-startup as some numeric value, in a Filter or 
 * Listener's init method etc.
 * 
 * <!-- END SNIPPET: activationThroughCodeDescription -->
 * 
 * <p/>
 * Parameter:- 
 * 
 * <pre>
 * <!-- START SNIPPET: activationThroughParameter -->
 * 
 * &lt;action ... &gt;  
 *  ...
 *  &lt;interceptor-ref name="profiling"&gt;
 *      &lt;param name="profilingKey"&gt;profiling&lt;/param&gt;
 *  &lt;/interceptor-ref&gt;
 *  ...
 * &lt;/action&gt;
 * 
 * or 
 * 
 * &lt;action .... &gt;
 * ...
 *  &lt;interceptor-ref name="profiling" /&gt;
 * ...
 * &lt;/action&gt;
 * 
 * through url
 * 
 * http://host:port/context/namespace/someAction.action?profiling=true
 * 
 * through code
 * 
 * ActionContext.getContext().getParameters().put("profiling", "true);
 * 
 * <!-- END SNIPPET: activationThroughParameter -->
 * </pre>
 * 
 * 
 * <!-- START SNIPPET: activationThroughParameterDescription -->
 * 
 * To use profiling activation through parameter, one will need to pass in through 
 * the 'profiling' parameter (which is the default) and could be changed through 
 * the param tag in the interceptor-ref. 
 * 
 * <!-- END SNIPPET: activationThroughParameterDescription -->
 * 
 * <p/>
 * Warning:<p/>
 * <!-- START SNIPPET: activationThroughParameterWarning -->
 * 
 * Profiling activation through a parameter requires the following:
 *
 * <ul>
 *  <li>Profiling interceptor in interceptor stack</li>
 *  <li>dev mode on (struts.devMode=true in struts.properties)
 * </ul>
 * 
 * <!-- END SNIPPET: activationThroughParameterWarning -->
 * 
 * <p/>
 * 
 * <!-- START SNIPPET: filteringDescription -->
 * 
 * One could filter out the profile logging by having a System property as follows. With this
 * 'xwork.profile.mintime' property, one could only log profile information when its execution time 
 * exceed those specified in 'xwork.profile.mintime' system property. If no such property is specified, 
 * it will be assumed to be 0, hence all profile information will be logged.
 * 
 * <!-- END SNIPPET: filteringDescription -->
 * 
 * <pre>
 * <!-- START SNIPPET: filteringCode -->
 * 
 *  -Dxwork.profile.mintime=10000
 * 
 * <!-- END SNIPPET: filteringCode -->
 * </pre>
 * 
 * <!-- START SNIPPET: methodDescription -->
 * 
 * One could extend the profiling feature provided by Struts2 in their web application as well. 
 * 
 * <!-- END SNIPPET: methodDescription -->
 * 
 * <pre>
 * <!-- START SNIPPET: method1 -->
 * 
 *    String logMessage = "Log message";
 *    UtilTimerStack.push(logMessage);
 *    try {
 *        // do some code
 *    }
 *    finally {
 *        UtilTimerStack.pop(logMessage); // this needs to be the same text as above
 *    }
 *    
 * <!-- END SNIPPET: method1 -->   
 * </pre>
 * 
 * or 
 * 
 * <pre>
 * <!-- START SNIPPET: method2 -->
 * 
 *   String result = UtilTimerStack.profile("purchaseItem: ", 
 *       new UtilTimerStack.ProfilingBlock<String>() {
 *            public String doProfiling() {
 *               // do some code
 *               return "Ok";
 *            }
 *       });
 *       
 * <!-- END SNIPPET: method2 -->      
 * </pre>
 * 
 * 
 * <!-- START SNIPPET: profileLogFile -->
 * 
 * Profiled result is logged using commons-logging under the logger named 
 * 'com.opensymphony.xwork2.util.profiling.UtilTimerStack'. Depending on the underlying logging implementation
 * say if it is Log4j, one could direct the log to appear in a different file, being emailed to someone or have 
 * it stored in the db.
 * 
 * <!-- END SNIPPET: profileLogFile -->
 * 
 * @version $Date$ $Id$
 */
public class UtilTimerStack
{

    // A reference to the current ProfilingTimerBean
    protected static ThreadLocal<ProfilingTimerBean> current = new ThreadLocal<ProfilingTimerBean>();

    /**
     * System property that controls whether this timer should be used or not.  Set to "true" activates
     * the timer.  Set to "false" to disactivate.
     */
    public static final String ACTIVATE_PROPERTY = "xwork.profile.activate";

    /**
     * System property that controls the min time, that if exceeded will cause a log (at INFO level) to be
     * created.
     */
    public static final String MIN_TIME = "xwork.profile.mintime";
    
    private static final Logger LOG = LoggerFactory.getLogger(UtilTimerStack.class);

    /**
     * Initialized in a static block, it can be changed at runtime by calling setActive(...)
     */
    private static boolean active;

    static {
        active = "true".equalsIgnoreCase(System.getProperty(ACTIVATE_PROPERTY));
    }

    /**
     * Create and start a performance profiling with the <code>name</code> given. Deal with 
     * profile hierarchy automatically, so caller don't have to be concern about it.
     * 
     * @param name profile name
     */
    public static void push(String name)
    {
        if (!isActive())
            return;

        //create a new timer and start it
        ProfilingTimerBean newTimer = new ProfilingTimerBean(name);
        newTimer.setStartTime();

        //if there is a current timer - add the new timer as a child of it
        ProfilingTimerBean currentTimer = (ProfilingTimerBean) current.get();
        if (currentTimer != null)
        {
            currentTimer.addChild(newTimer);
        }

        //set the new timer to be the current timer
        current.set(newTimer);
    }

    /**
     * End a preformance profiling with the <code>name</code> given. Deal with
     * profile hierarchy automatically, so caller don't have to be concern about it.
     * 
     * @param name profile name
     */
    public static void pop(String name)
    {
        if (!isActive())
            return;

        ProfilingTimerBean currentTimer = (ProfilingTimerBean) current.get();

        //if the timers are matched up with each other (ie push("a"); pop("a"));
        if (currentTimer != null && name != null && name.equals(currentTimer.getResource()))
        {
            currentTimer.setEndTime();
            ProfilingTimerBean parent = currentTimer.getParent();
            //if we are the root timer, then print out the times
            if (parent == null)
            {
                printTimes(currentTimer);
                current.set(null); //for those servers that use thread pooling
            }
            else
            {
                current.set(parent);
            }
        }
        else
        {
            //if timers are not matched up, then print what we have, and then print warning.
            if (currentTimer != null)
            {
                printTimes(currentTimer);
                current.set(null); //prevent printing multiple times
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Unmatched Timer.  Was expecting " + currentTimer.getResource() + ", instead got " + name);
                }
            }
        }


    }

    /**
     * Do a log (at INFO level) of the time taken for this particular profiling.
     * 
     * @param currentTimer profiling timer bean
     */
    private static void printTimes(ProfilingTimerBean currentTimer)
    {
        if (LOG.isInfoEnabled()) {
            LOG.info(currentTimer.getPrintable(getMinTime()));
        }
    }

    /**
     * Get the min time for this profiling, it searches for a System property
     * 'xwork.profile.mintime' and default to 0.
     * 
     * @return long
     */
    private static long getMinTime()
    {
        try
        {
            return Long.parseLong(System.getProperty(MIN_TIME, "0"));
        }
        catch (NumberFormatException e)
        {
           return -1;
        }
    }

    /**
     * Determine if profiling is being activated, by searching for a system property
     * 'xwork.profile.activate', default to false (profiling is off).
     * 
     * @return <tt>true</tt>, if active, <tt>false</tt> otherwise.
     */
    public static boolean isActive()
    {
        return active;
    }

    /**
     * Turn profiling on or off.
     * 
     * @param active
     */
    public static void setActive(boolean active)
    {
        if (active)
            System.setProperty(ACTIVATE_PROPERTY, "true");
        else
        	System.clearProperty(ACTIVATE_PROPERTY);

        UtilTimerStack.active = active; 
    }


    /**
     * A convenience method that allows <code>block</code> of code subjected to profiling to be executed 
     * and avoid the need of coding boiler code that does pushing (UtilTimeBean.push(...)) and 
     * poping (UtilTimerBean.pop(...)) in a try ... finally ... block.
     * 
     * <p/>
     * 
     * Example of usage:
     * <pre>
     * 	 // we need a returning result
     *   String result = UtilTimerStack.profile("purchaseItem: ", 
     *       new UtilTimerStack.ProfilingBlock<String>() {
     *            public String doProfiling() {
     *               getMyService().purchaseItem(....)
     *               return "Ok";
     *            }
     *       });
     * </pre>
     * or
     * <pre>
     *   // we don't need a returning result
     *   UtilTimerStack.profile("purchaseItem: ", 
     *       new UtilTimerStack.ProfilingBlock<String>() {
     *            public String doProfiling() {
     *               getMyService().purchaseItem(....)
     *               return null;
     *            }
     *       });
     * </pre>
     * 
     * @param <T> any return value if there's one.
     * @param name profile name
     * @param block code block subjected to profiling
     * @return T
     * @throws Exception
     */
    public static <T> T profile(String name, ProfilingBlock<T> block) throws Exception {
    	UtilTimerStack.push(name);
    	try {
    		return block.doProfiling();
    	}
    	finally {
    		UtilTimerStack.pop(name);
    	}
    }
    
    /**
     * A callback interface where code subjected to profile is to be executed. This eliminates the need
     * of coding boiler code that does pushing (UtilTimerBean.push(...)) and poping (UtilTimerBean.pop(...))
     * in a try ... finally ... block.
     * 
     * @version $Date$ $Id$
     * 
     * @param <T>
     */
    public static interface ProfilingBlock<T> {
    	
    	/**
    	 * Method that execute the code subjected to profiling.
    	 * 
    	 * @return  profiles Type
    	 * @throws Exception
    	 */
    	T doProfiling() throws Exception;
    }
}
