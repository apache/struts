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
import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <!-- START SNIPPET: description -->
 *
 * This interceptor forms the core functionality of the exception handling feature. Exception handling allows you to map
 * an exception to a result code, just as if the action returned a result code instead of throwing an unexpected
 * exception. When an exception is encountered, it is wrapped with an {@link ExceptionHolder} and pushed on the stack,
 * providing easy access to the exception from within your result.
 *
 * <b>Note:</b> While you can configure exception mapping in your configuration file at any point, the configuration
 * will not have any effect if this interceptor is not in the interceptor stack for your actions. It is recommended that
 * you make this interceptor the first interceptor on the stack, ensuring that it has full access to catch any
 * exception, even those caused by other interceptors.
 *
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Interceptor parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>logEnabled (optional) - Should exceptions also be logged? (boolean true|false)</li>
 * 
 * <li>logLevel (optional) - what log level should we use (<code>trace, debug, info, warn, error, fatal</code>)? - defaut is <code>debug</code></li>
 * 
 * <li>logCategory (optional) - If provided we would use this category (eg. <code>com.mycompany.app</code>).
 * Default is to use <code>com.opensymphony.xwork2.interceptor.ExceptionMappingInterceptor</code>.</li>
 *
 * </ul>
 *
 * The parameters above enables us to log all thrown exceptions with stacktace in our own logfile,
 * and present a friendly webpage (with no stacktrace) to the end user.
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <p/> <u>Extending the interceptor:</u>
 *
 * <p/>
 *
 * <!-- START SNIPPET: extending -->
 *
 * If you want to add custom handling for publishing the Exception, you may override
 * {@link #publishException(com.opensymphony.xwork2.ActionInvocation, ExceptionHolder)}. The default implementation
 * pushes the given ExceptionHolder on value stack. A custom implementation could add additional logging etc.
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;xwork&gt;
 *     &lt;package name="default" extends="xwork-default"&gt;
 *         &lt;global-results&gt;
 *             &lt;result name="error" type="freemarker"&gt;error.ftl&lt;/result&gt;
 *         &lt;/global-results&gt;
 *
 *         &lt;global-exception-mappings&gt;
 *             &lt;exception-mapping exception="java.lang.Exception" result="error"/&gt;
 *         &lt;/global-exception-mappings&gt;
 *
 *         &lt;action name="test"&gt;
 *             &lt;interceptor-ref name="exception"/&gt;
 *             &lt;interceptor-ref name="basicStack"/&gt;
 *             &lt;exception-mapping exception="com.acme.CustomException" result="custom_error"/&gt;
 *             &lt;result name="custom_error"&gt;custom_error.ftl&lt;/result&gt;
 *             &lt;result name="success" type="freemarker"&gt;test.ftl&lt;/result&gt;
 *         &lt;/action&gt;
 *     &lt;/package&gt;
 * &lt;/xwork&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 * 
 * <p/>
 * This second example will also log the exceptions using our own category
 * <code>com.mycompany.app.unhandled<code> at WARN level. 
 * 
 * <pre>
 * <!-- START SNIPPET: example2 -->
 * &lt;xwork&gt;
 *   &lt;package name="something" extends="xwork-default"&gt;
 *      &lt;interceptors&gt;
 *          &lt;interceptor-stack name="exceptionmappingStack"&gt;
 *              &lt;interceptor-ref name="exception"&gt;
 *                  &lt;param name="logEnabled"&gt;true&lt;/param&gt;
 *                  &lt;param name="logCategory"&gt;com.mycompany.app.unhandled&lt;/param&gt;
 *                  &lt;param name="logLevel"&gt;WARN&lt;/param&gt;	        		
 *              &lt;/interceptor-ref&gt;	
 *              &lt;interceptor-ref name="i18n"/&gt;
 *              &lt;interceptor-ref name="staticParams"/&gt;
 *              &lt;interceptor-ref name="params"/&gt;
 *              &lt;interceptor-ref name="validation"&gt;
 *                  &lt;param name="excludeMethods"&gt;input,back,cancel,browse&lt;/param&gt;
 *              &lt;/interceptor-ref&gt;
 *          &lt;/interceptor-stack&gt;
 *      &lt;/interceptors&gt;
 *
 *      &lt;default-interceptor-ref name="exceptionmappingStack"/&gt;
 *    
 *      &lt;global-results&gt;
 *           &lt;result name="unhandledException"&gt;/unhandled-exception.jsp&lt;/result&gt;
 *      &lt;/global-results&gt;
 *
 *      &lt;global-exception-mappings&gt;
 *           &lt;exception-mapping exception="java.lang.Exception" result="unhandledException"/&gt;
 *      &lt;/global-exception-mappings&gt;
 *        
 *      &lt;action name="exceptionDemo" class="org.apache.struts2.showcase.exceptionmapping.ExceptionMappingAction"&gt;
 *          &lt;exception-mapping exception="org.apache.struts2.showcase.exceptionmapping.ExceptionMappingException"
 *                             result="damm"/&gt;
 *          &lt;result name="input"&gt;index.jsp&lt;/result&gt;
 *          &lt;result name="success"&gt;success.jsp&lt;/result&gt;            
 *          &lt;result name="damm"&gt;damm.jsp&lt;/result&gt;
 *      &lt;/action&gt;
 *
 *   &lt;/package&gt;
 * &lt;/xwork&gt;
 * <!-- END SNIPPET: example2 -->
 * </pre>
 *
 * @author Matthew E. Porter (matthew dot porter at metissian dot com) 
 * @author Claus Ibsen
 */
public class ExceptionMappingInterceptor extends AbstractInterceptor {
    
    protected static final Logger LOG = LoggerFactory.getLogger(ExceptionMappingInterceptor.class);

    protected Logger categoryLogger;
    protected boolean logEnabled = false;
    protected String logCategory;
    protected String logLevel;
    

    public boolean isLogEnabled() {
        return logEnabled;
    }

    public void setLogEnabled(boolean logEnabled) {
        this.logEnabled = logEnabled;
    }

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
        String result;

        try {
            result = invocation.invoke();
        } catch (Exception e) {
            if (isLogEnabled()) {
                handleLogging(e);
            }
            List<ExceptionMappingConfig> exceptionMappings = invocation.getProxy().getConfig().getExceptionMappings();
            ExceptionMappingConfig mappingConfig = this.findMappingFromExceptions(exceptionMappings, e);
            if (mappingConfig != null && mappingConfig.getResult()!=null) {
                Map parameterMap = mappingConfig.getParams();
                // create a mutable HashMap since some interceptors will remove parameters, and parameterMap is immutable
                invocation.getInvocationContext().setParameters(new HashMap<String, Object>(parameterMap));
                result = mappingConfig.getResult();
                publishException(invocation, new ExceptionHolder(e));
            } else {
                throw e;
            }
        }

        return result;
    }

    /**
     * Handles the logging of the exception.
     * 
     * @param e  the exception to log.
     */
    protected void handleLogging(Exception e) {
    	if (logCategory != null) {
        	if (categoryLogger == null) {
        		// init category logger
        		categoryLogger = LoggerFactory.getLogger(logCategory);
        	}
        	doLog(categoryLogger, e);
    	} else {
    		doLog(LOG, e);
    	}
    }
    
    /**
     * Performs the actual logging.
     * 
     * @param logger  the provided logger to use.
     * @param e  the exception to log.
     */
    protected void doLog(Logger logger, Exception e) {
    	if (logLevel == null) {
    		logger.debug(e.getMessage(), e);
    		return;
    	}
    	
    	if ("trace".equalsIgnoreCase(logLevel)) {
    		logger.trace(e.getMessage(), e);
    	} else if ("debug".equalsIgnoreCase(logLevel)) {
    		logger.debug(e.getMessage(), e);
    	} else if ("info".equalsIgnoreCase(logLevel)) {
    		logger.info(e.getMessage(), e);
    	} else if ("warn".equalsIgnoreCase(logLevel)) {
    		logger.warn(e.getMessage(), e);
    	} else if ("error".equalsIgnoreCase(logLevel)) {
    		logger.error(e.getMessage(), e);
    	} else if ("fatal".equalsIgnoreCase(logLevel)) {
    		logger.fatal(e.getMessage(), e);
    	} else {
    		throw new IllegalArgumentException("LogLevel [" + logLevel + "] is not supported");
    	}
    }

    /**
     * @deprecated since 2.3.15 please use #findMappingFromExceptions directly instead
     */
    protected String findResultFromExceptions(List<ExceptionMappingConfig> exceptionMappings, Throwable t) {
    	ExceptionMappingConfig result = findMappingFromExceptions(exceptionMappings, t);
        return result==null?null:result.getResult();
    }

    /**
     * Try to find appropriate {@link ExceptionMappingConfig} based on provided Throwable
     *
     * @param exceptionMappings list of defined exception mappings
     * @param t caught exception
     * @return appropriate mapping or null
     */
    protected ExceptionMappingConfig findMappingFromExceptions(List<ExceptionMappingConfig> exceptionMappings, Throwable t) {
    	ExceptionMappingConfig config = null;
        // Check for specific exception mappings.
        if (exceptionMappings != null) {
            int deepest = Integer.MAX_VALUE;
            for (Object exceptionMapping : exceptionMappings) {
                ExceptionMappingConfig exceptionMappingConfig = (ExceptionMappingConfig) exceptionMapping;
                int depth = getDepth(exceptionMappingConfig.getExceptionClassName(), t);
                if (depth >= 0 && depth < deepest) {
                    deepest = depth;
                    config = exceptionMappingConfig;
                }
            }
        }
        return config;
    }

    /**
     * Return the depth to the superclass matching. 0 means ex matches exactly. Returns -1 if there's no match.
     * Otherwise, returns depth. Lowest depth wins.
     *
     * @param exceptionMapping  the mapping classname
     * @param t  the cause
     * @return the depth, if not found -1 is returned.
     */
    public int getDepth(String exceptionMapping, Throwable t) {
        return getDepth(exceptionMapping, t.getClass(), 0);
    }

    private int getDepth(String exceptionMapping, Class exceptionClass, int depth) {
        if (exceptionClass.getName().contains(exceptionMapping)) {
            // Found it!
            return depth;
        }
        // If we've gone as far as we can go and haven't found it...
        if (exceptionClass.equals(Throwable.class)) {
            return -1;
        }
        return getDepth(exceptionMapping, exceptionClass.getSuperclass(), depth + 1);
    }

    /**
     * Default implementation to handle ExceptionHolder publishing. Pushes given ExceptionHolder on the stack.
     * Subclasses may override this to customize publishing.
     *
     * @param invocation The invocation to publish Exception for.
     * @param exceptionHolder The exceptionHolder wrapping the Exception to publish.
     */
    protected void publishException(ActionInvocation invocation, ExceptionHolder exceptionHolder) {
        invocation.getStack().push(exceptionHolder);
    }

}
