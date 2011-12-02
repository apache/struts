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
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * <!-- START SNIPPET: description -->
 * This interceptor logs the amount of time in milliseconds. In order for this interceptor to work properly, the
 * logging framework must be set to at least the <tt>INFO</tt> level.
 * This interceptor relies on the <a href="http://jakarta.apache.org/commons/logging/">Commons Logging API</a> to
 * report its execution-time value.
 * <!-- END SNIPPET: description -->
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>logLevel (optional) - what log level should we use (<code>trace, debug, info, warn, error, fatal</code>)? - defaut is <code>info</code></li>
 *
 * <li>logCategory (optional) - If provided we would use this category (eg. <code>com.mycompany.app</code>).
 * Default is to use <code>com.opensymphony.xwork2.interceptor.TimerInterceptor</code>.</li>
 *
 * </ul>
 *
 * The parameters above enables us to log all action execution times in our own logfile.
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <!-- START SNIPPET: extending -->
 * This interceptor can be extended to provide custom message format. Users should override the
 * <code>invokeUnderTiming</code> method.
 * <!-- END SNIPPET: extending -->
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;!-- records only the action's execution time --&gt;
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="completeStack"/&gt;
 *     &lt;interceptor-ref name="timer"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 *
 * &lt;!-- records action's execution time as well as other interceptors--&gt;
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="timer"/&gt;
 *     &lt;interceptor-ref name="completeStack"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * This second example uses our own log category at debug level.
 *
 * <pre>
 * <!-- START SNIPPET: example2 -->
 * &lt;!-- records only the action's execution time --&gt;
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="completeStack"/&gt;
 *     &lt;interceptor-ref name="timer"&gt;
 *         &lt;param name="logLevel"&gt;debug&lt;/param&gt;
 *         &lt;param name="logCategory"&gt;com.mycompany.myapp.actiontime&lt;/param&gt;
 *     &lt;interceptor-ref/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 *
 * &lt;!-- records action's execution time as well as other interceptors--&gt;
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="timer"/&gt;
 *     &lt;interceptor-ref name="completeStack"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example2 -->
 * </pre>
 *
 * @author Jason Carreira
 * @author Claus Ibsen
 */
public class TimerInterceptor extends AbstractInterceptor {
    protected static final Logger LOG = LoggerFactory.getLogger(TimerInterceptor.class);

    protected Logger categoryLogger;
    protected String logCategory;
    protected String logLevel;

    public String getLogCategory() {
        return logCategory;
    }

    public void setLogCategory(String logCatgory) {
        this.logCategory = logCatgory;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        if (! shouldLog()) {
            return invocation.invoke();
        } else {
            return invokeUnderTiming(invocation);
        }
    }

    /**
     * Is called to invoke the action invocation and time the execution time.
     *
     * @param invocation  the action invocation.
     * @return the result of the action execution.
     * @throws Exception  can be thrown from the action.
     */
    protected String invokeUnderTiming(ActionInvocation invocation) throws Exception {
        long startTime = System.currentTimeMillis();
        String result = invocation.invoke();
        long executionTime = System.currentTimeMillis() - startTime;

        StringBuilder message = new StringBuilder(100);
        message.append("Executed action [");
        String namespace = invocation.getProxy().getNamespace();
        if ((namespace != null) && (namespace.trim().length() > 0)) {
            message.append(namespace).append("/");
        }
        message.append(invocation.getProxy().getActionName());
        message.append("!");
        message.append(invocation.getProxy().getMethod());
        message.append("] took ").append(executionTime).append(" ms.");

        doLog(getLoggerToUse(), message.toString());

        return result;
    }

    /**
     * Determines if we should log the time.
     *
     * @return  true to log, false to not log.
     */
    protected boolean shouldLog() {
        // default check first
        if (logLevel == null && logCategory == null) {
            return LOG.isInfoEnabled();
        }

        // okay user have set some parameters
        return isLoggerEnabled(getLoggerToUse(), logLevel);
    }

    /**
     * Get's the logger to use.
     *
     * @return the logger to use.
     */
    protected Logger getLoggerToUse() {
        if (logCategory != null) {
            if (categoryLogger == null) {
                // init category logger
                categoryLogger = LoggerFactory.getLogger(logCategory);
                if (logLevel == null) {
                    logLevel = "info"; // use info as default if not provided
                }
            }
            return categoryLogger;
        }

        return LOG;
    }

    /**
     * Performs the actual logging.
     *
     * @param logger  the provided logger to use.
     * @param message  the message to log.
     */
    protected void doLog(Logger logger, String message) {
        if (logLevel == null) {
            logger.info(message);
            return;
        }

        if ("debug".equalsIgnoreCase(logLevel)) {
            logger.debug(message);
        } else if ("info".equalsIgnoreCase(logLevel)) {
            logger.info(message);
        } else if ("warn".equalsIgnoreCase(logLevel)) {
            logger.warn(message);
        } else if ("error".equalsIgnoreCase(logLevel)) {
            logger.error(message);
        } else if ("fatal".equalsIgnoreCase(logLevel)) {
            logger.fatal(message);
        } else if ("trace".equalsIgnoreCase(logLevel)) {
            logger.trace(message);
        } else {
            throw new IllegalArgumentException("LogLevel [" + logLevel + "] is not supported");
        }
    }

    /**
     * Is the given logger enalbed at the given level?
     *
     * @param logger  the logger.
     * @param level   the level to check if <code>isXXXEnabled</code>.
     * @return <tt>true</tt> if enabled, <tt>false</tt> if not.
     */
    private static boolean isLoggerEnabled(Logger logger, String level) {
        if ("debug".equalsIgnoreCase(level)) {
            return logger.isDebugEnabled();
        } else if ("info".equalsIgnoreCase(level)) {
            return logger.isInfoEnabled();
        } else if ("warn".equalsIgnoreCase(level)) {
            return logger.isWarnEnabled();
        } else if ("error".equalsIgnoreCase(level)) {
            return logger.isErrorEnabled();
        } else if ("fatal".equalsIgnoreCase(level)) {
            return logger.isFatalEnabled();
        } else if ("trace".equalsIgnoreCase(level)) {
            return logger.isTraceEnabled();
        } else {
            throw new IllegalArgumentException("LogLevel [" + level + "] is not supported");
        }
    }

}
