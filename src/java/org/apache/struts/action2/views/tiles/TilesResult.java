/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.tiles;

import org.apache.struts.action2.ServletActionContext;
import org.apache.struts.action2.dispatcher.ServletDispatcherResult;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.LocaleProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tiles.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * <!-- START SNIPPET: description -->
 * Renders a view using struts-tiles.
 * <!-- END SNIPPET: description -->
 *
 * <!-- START SNIPPET: webxml -->
 * In your web.xml file, you need to add a servlet entry for TilesServlet to load the tiles definitions into the ServletContext.
 *
 * <servlet>
 *      <servlet-name>tiles</servlet-name>
 *      <servlet-class>org.apache.tiles.servlets.TilesServlet</servlet-class>
 *      <init-param>
 *          <param-name>definitions-config</param-name>
 *          <param-value>/WEB-INF/tiles-config.xml</param-value>
 *      </init-param>
 *      <load-on-startup>1</load-on-startup>
 * </servlet>
 * <!-- END SNIPPET: webxml -->
 *
 * <!-- START SNIPPET: xworkxml -->
 * In xwork.xml, use type="tiles" on your <result>.
 *
 * <action name="editUser" class="userAction" method="edit">
 *      <result name="success" type="tiles">userForm</result>
 *      <result name="input" type="tiles">userList</result>
 * </action>
 * <!-- END SNIPPET: xworkxml -->
 *
 *
 * <!-- START SNIPPET: packageconfig -->
 *
 * Making this result type the default for the current package.
 *
 * <result-types>
 *      <result-type name="tiles" class="org.apache.struts.action2.views.tiles.TilesResult" default="true" />
 * </result-types>
 * <!-- END SNIPPET: packageconfig -->
 *
 * @author Matt Raible
 * @author Rainer Hermanns
 * @version $Id: TilesResult.java,v 1.1 2006/02/17 19:06:02 rainerh Exp $
 */
public class TilesResult extends ServletDispatcherResult {

	private static final long serialVersionUID = -3806939435493086243L;

	private static final Log log = LogFactory.getLog(TilesResult.class);

    protected ActionInvocation invocation;
    private DefinitionsFactory definitionsFactory;

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
        this.location = location;
        this.invocation = invocation;

        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ServletContext servletContext = ServletActionContext.getServletContext();

        this.definitionsFactory =
                (DefinitionsFactory) servletContext.getAttribute(TilesUtilImpl.DEFINITIONS_FACTORY);

        // get component definition
        ComponentDefinition definition = getComponentDefinition(this.definitionsFactory, request);
        if (definition == null) {
            throw new ServletException("No Tiles definition found for name '" + location + "'");
        }

        // get current component context
        ComponentContext context = getComponentContext(definition, request);
        ComponentContext.setContext(context, request);

        // execute component controller associated with definition, if any
        Controller controller = getController(definition, request);
        if (controller != null) {
            if (log.isDebugEnabled()) {
                log.debug("Executing Tiles controller [" + controller + "]");
            }
            executeController(controller, context, request, response);
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
    protected ComponentDefinition getComponentDefinition(DefinitionsFactory factory, HttpServletRequest request)
            throws Exception {
        ComponentDefinitions definitions = factory.readDefinitions();
        return definitions.getDefinition(location, deduceLocale(request));
    }

    /**
     * Determine the Tiles component context for the given Tiles definition.
     *
     * @param definition the Tiles definition to render
     * @param request    current HTTP request
     * @return the component context
     * @throws Exception if preparations failed
     */
    protected ComponentContext getComponentContext(ComponentDefinition definition, HttpServletRequest request)
            throws Exception {
        ComponentContext context = ComponentContext.getContext(request);
        if (context == null) {
            context = new ComponentContext(definition.getAttributes());
            ComponentContext.setContext(context, request);
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
     * @param request    current HTTP request
     * @param response   current HTTP response
     * @throws Exception if controller execution failed
     */
    protected void executeController(
            Controller controller, ComponentContext context, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        controller.execute(context, request, response, ServletActionContext.getServletContext());
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
