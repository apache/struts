/*
 * $Id: ActionContextCleanUp.java 454720 2006-10-10 12:31:52Z tmjee $
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
package org.apache.struts2.dispatcher;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An abstract for superclass for Struts2 filter, encapsulating common logics and 
 * helper methods usefull to subclass, to avoid duplication. 
 * 
 * Common logics encapsulated:-
 * <ul>
 * 	<li>
 * 		Dispatcher instance creation through <code>createDispatcher</code> method that acts
 * 		as a hook subclass could override. By default it creates an instance of Dispatcher.
 *  </li>
 *  <li>
 *  	<code>postInit(FilterConfig)</code> is a hook subclass may use to add post initialization
 *      logics in <code>{@link javax.servlet.Filter#init(FilterConfig)}</code>. It is called before
 *      {@link javax.servlet.Filter#init(FilterConfig)} method ends.
 *  </li>
 *  <li>
 *  	A default <code>{@link javax.servlet.Filter#destroy()}</code> that clean up Dispatcher by 
 *      calling <code>dispatcher.cleanup()</code>
 *  </li>
 *  <li>
 *  	<code>prepareDispatcherAndWrapRequest(HttpServletRequest, HttpServletResponse)</code> helper method
 *      that basically called <code>dispatcher.prepare()</code>, wrap the HttpServletRequest and return the 
 *      wrapped version.
 *  </li>
 *  <li>
 *  	Various other common helper methods like
 *      <ul>
 *      	<li>getFilterConfig</li>
 *      	<li>getServletContext</li>
 *      </ul>
 *  </li>
 * </ul>
 * 
 * 
 * @see Dispatcher
 * @see FilterDispatcher
 * @see ActionContextCleanUp
 * 
 * @version $Date$ $Id$
 */
public abstract class AbstractFilter implements Filter {

	private static final Log LOG = LogFactory.getLog(AbstractFilter.class);
	
	/** 
     * Internal copy of dispatcher, created when Filter instance gets initialized. 
     * This is needed when ActionContextCleanUp filter is used (Sitemesh integration), 
     * such that a {@link Dispatcher} instance could be prepared and request wrapped before
     * Sitemesh is given a chance to handle the request. This results in Sitemesh 
     * have access to internals of Struts2. With the following filter configuration
     * <ol>
     *   <li>ActionContextCleanUp</li>
     *   <li>Sitemesh filter</li>
     *   <li>FilterDispatcher</li>
     * </ol>
     * Both {@link ActionContextCleanUp} and {@link FilterDispatcher} will have a 
     * copy of a {@link Dispatcher}, however only one will be copied to 
     * {@link Dispatcher} ThreadLocal.
     * 
     * @see {@link #prepareDispatcherAndWrapRequest(HttpServletRequest, HttpServletResponse)}
     */
	private Dispatcher _internalCopyOfDispatcher; 
	
	protected FilterConfig filterConfig;
	
	/** Dispatcher instance to be used by subclass. */
	protected Dispatcher dispatcher;
	
	
	/**
     * Initializes the filter
     * 
     * @param filterConfig The filter configuration
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        _internalCopyOfDispatcher = createDispatcher(filterConfig);
        postInit(filterConfig);
    }
    
    /**
     * Cleans up the dispatcher. Calls dispatcher.cleanup,
     * which in turn releases local threads and destroys any DispatchListeners.
     * 
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        if (_internalCopyOfDispatcher == null) {
        	LOG.warn("something is seriously wrong, Dispatcher is not initialized (null) ");
        } else {
        	_internalCopyOfDispatcher.cleanup();
        }
    }
    
    /**
     * Hook for subclass todo custom initialization, called after 
     * <code>javax.servlet.Filter.init(FilterConfig)</code>.
     * 
     * @param filterConfig
     * @throws ServletException
     */
    protected abstract void postInit(FilterConfig filterConfig) throws ServletException;
	
    
    /**
     * Create a default {@link Dispatcher} that subclasses can override
     * with a custom Dispatcher, if needed.
     *
     * @param filterConfig Our FilterConfig
     * @return Initialized Dispatcher 
     */
    protected Dispatcher createDispatcher(FilterConfig filterConfig) {
        Map<String,String> params = new HashMap<String,String>();
        for (Enumeration e = filterConfig.getInitParameterNames(); e.hasMoreElements(); ) {
            String name = (String) e.nextElement();
            String value = filterConfig.getInitParameter(name);
            params.put(name, value);
        }
        return new Dispatcher(filterConfig.getServletContext(), params);
    }
    
    
    /**
     * Provide a workaround for some versions of WebLogic.
     * <p/>
     * Servlet 2.3 specifies that the servlet context can be retrieved from the session. Unfortunately, some versions of
     * WebLogic can only retrieve the servlet context from the filter config. Hence, this method enables subclasses to
     * retrieve the servlet context from other sources.
     *
     * @return the servlet context.
     */
    protected ServletContext getServletContext() {
        return filterConfig.getServletContext();
    }
    
    /**
     * Expose the FilterConfig instance.
     *
     * @return Our FilterConfit instance
     */
    protected FilterConfig getFilterConfig() {
        return filterConfig;
    }
    
    /**
     * Wrap and return the given request, if needed, so as to to transparently
     * handle multipart data as a wrapped class around the given request.
     * 
     * <p/>
     * 
     * Helper method that prepare <code>Dispatcher</code> 
     * (by calling <code>Dispatcher.prepare(HttpServletRequest, HttpServletResponse)</code>)
     * following by wrapping and returning  the wrapping <code>HttpServletRequest</code> [ through 
     * <code>dispatcher.wrapRequest(HttpServletRequest, ServletContext)</code> ]
     * 
     * @param request Our ServletRequest object
     * @param response Our ServerResponse object
     * @return Wrapped HttpServletRequest object
     * @throws ServletException on any error
     */
    protected HttpServletRequest prepareDispatcherAndWrapRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    	
    	Dispatcher du = Dispatcher.getInstance();
        
    	// Prepare and wrap the request if the cleanup filter hasn't already, cleanup filter should be
    	// configured first before struts2 dispatcher filter, hence when its cleanup filter's turn, 
    	// static instance of Dispatcher should be null.
    	if (du == null) {
    		dispatcher = _internalCopyOfDispatcher;
    		
    		Dispatcher.setInstance(dispatcher);
    		
    		// prepare the request no matter what - this ensures that the proper character encoding
    		// is used before invoking the mapper (see WW-9127)
    		dispatcher.prepare(request, response);

    		try {
    			// Wrap request first, just in case it is multipart/form-data 
    			// parameters might not be accessible through before encoding (ww-1278)
    			request = dispatcher.wrapRequest(request, getServletContext());
    		} catch (IOException e) {
    			String message = "Could not wrap servlet request with MultipartRequestWrapper!";
    			LOG.error(message, e);
    			throw new ServletException(message, e);
    		}
    	}
    	else {
    		dispatcher = du;
    	}
		return request;
    }
}
