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
package org.apache.struts2.views.tiles;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.ServletDispatcherResult;
import org.apache.tiles.*;
import org.apache.tiles.context.servlet.ServletTilesContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.LocaleProvider;

/**
 * <!-- START SNIPPET: description -->
 * Renders a view using struts-tiles.
 * <!-- END SNIPPET: description -->
 *
 * <!-- START SNIPPET: webxml -->
 * In your web.xml file, you need to add a servlet entry for TilesServlet to load the tiles
 * definitions into the ServletContext.
 *
 * &lt;servlet&gt;
 *      &lt;servlet-name&gt;tiles&lt;/servlet-name&gt;
 *      &lt;servlet-class&gt;org.apache.tiles.servlets.TilesServlet&lt;/servlet-class&gt;
 *      &lt;init-param&gt;
 *          &lt;param-name&gt;definitions-config&lt;/param-name&gt;
 *          &lt;param-value&gt;/WEB-INF/tiles-config.xml&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *      &lt;load-on-startup&gt;1&lt;/load-on-startup&gt;
 * &lt;/servlet&gt;
 * <!-- END SNIPPET: webxml -->
 *
 * <!-- START SNIPPET: strutsxml -->
 * In struts.xml, use type="tiles" on your &lt;result&gt;.
 *
 * &lt;action name="editUser" class="userAction" method="edit"&gt;
 *      &lt;result name="success" type="tiles"&gt;userForm&lt;/result&gt;
 *      &lt;result name="input" type="tiles"&gt;userList&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: strutsxml -->
 *
 *
 * <!-- START SNIPPET: packageconfig -->
 *
 * Making this result type the default for the current package.
 *
 * &lt;result-types&gt;
 *      &lt;result-type name="tiles"
 * class="org.apache.struts2.views.tiles.TilesResult" default="true" /&gt;
 * &lt;/result-types&gt;
 * <!-- END SNIPPET: packageconfig -->
 *
 */
public class TilesResult extends ServletDispatcherResult {

	private static final long serialVersionUID = -3806939435493086243L;

	private static final Log log = LogFactory.getLog(TilesResult.class);

    protected ActionInvocation invocation;
    private DefinitionsFactory definitionsFactory;

    public TilesResult() {
    	super();
    }
    
    public TilesResult(String location) {
    	super(location);
    }
    /**
     * Dispatches to the given location. Does its forward via a RequestDispatcher. If the
     * dispatch fails a 404 error will be sent back in the http response.
     *
     * @param location the location to dispatch to.
     * @param invocation    the execution state of the action
     * @throws Exception if an error occurs. If the dispatch fails the error will go back via the
     *                   HTTP request.
     */
    public void doExecute(String location, ActionInvocation invocation) throws Exception {
        setLocation(location);
        this.invocation = invocation;

        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ServletContext servletContext = ServletActionContext.getServletContext();
        TilesContext tilesContext = new ServletTilesContext(servletContext, request, response);

        this.definitionsFactory =
                (DefinitionsFactory) servletContext.getAttribute(TilesUtilImpl.DEFINITIONS_FACTORY);

        // get component definition
        ComponentDefinition definition = getComponentDefinition(location, this.definitionsFactory, request);
        if (definition == null) {
            throw new ServletException("No Tiles definition found for name '" + location + "'");
        }

        // get current component context
        ComponentContext context = getComponentContext(definition, tilesContext);
        ComponentContext.setContext(context, tilesContext);

        // execute component controller associated with definition, if any
        Controller controller = getController(definition, request);
        if (controller != null) {
            if (log.isDebugEnabled()) {
                log.debug("Executing Tiles controller [" + controller + "]");
            }
            executeController(controller, context, tilesContext);
        }

        // determine the path of the definition
        String path = getDispatcherPath(definition, request);
        if (path == null) {
            throw new ServletException(
                    "Could not determine a path for Tiles definition '" + definition.getName() + "'");
        }

        super.doExecute(path, invocation);
    }

    protected Locale deduceLocale(HttpServletRequest request) {
        if (invocation.getAction() instanceof LocaleProvider) {
            return ((LocaleProvider) invocation.getAction()).getLocale();
        } else {
            return request.getLocale();
        }
    }

    /**
     * Determine the Tiles component definition for the given Tiles
     * definitions factory.
     *
     * @param factory the Tiles definitions factory
     * @param request current HTTP request
     * @return the component definition
     */
    protected ComponentDefinition getComponentDefinition(String location, DefinitionsFactory factory, HttpServletRequest request)
            throws Exception {
        ComponentDefinitions definitions = factory.readDefinitions();
        return definitions.getDefinition(location, deduceLocale(request));
    }

    /**
     * Determine the Tiles component context for the given Tiles definition.
     *
     * @param definition the Tiles definition to render
     * @param tilesContext    current TilesContext
     * @return the component context
     * @throws Exception if preparations failed
     */
    protected ComponentContext getComponentContext(ComponentDefinition definition, TilesContext tilesContext)
            throws Exception {
        ComponentContext context = ComponentContext.getContext(tilesContext);
        if (context == null) {
            context = new ComponentContext(definition.getAttributes());
            ComponentContext.setContext(context, tilesContext);
        } else {
            context.addMissing(definition.getAttributes());
        }
        return context;
    }

    /**
     * Determine and initialize the Tiles component controller for the
     * given Tiles definition, if any.
     *
     * @param definition the Tiles definition to render
     * @param request    current HTTP request
     * @return the component controller to execute, or <code>null</code> if none
     * @throws Exception if preparations failed
     */
    protected Controller getController(ComponentDefinition definition, HttpServletRequest request)
            throws Exception {
        return definition.getOrCreateController();
    }

    /**
     * Execute the given Tiles controller.
     *
     * @param controller the component controller to execute
     * @param context    the component context
     * @param tilesContext   current tilesContext
     * @throws Exception if controller execution failed
     */
    protected void executeController(
            Controller controller, ComponentContext context, TilesContext tilesContext)
            throws Exception {
        controller.execute(tilesContext, context);
    }

    /**
     * Determine the dispatcher path for the given Tiles definition,
     * i.e. the request dispatcher path of the layout page.
     * @param definition the Tiles definition to render
     * @param request current HTTP request
     * @return the path of the layout page to render
     * @throws Exception if preparations failed
     */
    protected String getDispatcherPath(ComponentDefinition definition, HttpServletRequest request)
            throws Exception {
        Object pathAttr = null;
        return (pathAttr != null ? pathAttr.toString() : definition.getPath());
	}
}
