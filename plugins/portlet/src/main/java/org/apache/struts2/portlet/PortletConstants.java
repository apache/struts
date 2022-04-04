package org.apache.struts2.portlet;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.portlet.dispatcher.DispatcherServlet;

public class PortletConstants {
	/**
     * Default action name to use when no default action has been configured in the portlet
     * init parameters.
     */
    public static String DEFAULT_ACTION_NAME = "default";

    /**
     * Action name parameter name
     */
    public static String ACTION_PARAM = "struts.portlet.action";

    /**
     * Key for parameter holding the last executed portlet mode.
     */
    public static String MODE_PARAM = "struts.portlet.mode";

    /**
     * Key used for looking up and storing the portlet phase
     */
    public static String PHASE = "struts.portlet.phase";

    /**
     * Key used for looking up and storing the
     * {@link javax.portlet.PortletRequest}
     */
    public static String REQUEST = "struts.portlet.request";

    /**
     * Key used for looking up and storing the
     * {@link javax.portlet.PortletResponse}
     */
    public static String RESPONSE = "struts.portlet.response";

    /**
     * Key used for looking up and storing the action that was invoked in the action or event phase.
     */
    public static String EVENT_ACTION = "struts.portlet.eventAction";

    /**
     * Key used for looking up and storing the
     * {@link javax.portlet.PortletConfig}
     */
    public static String PORTLET_CONFIG = "struts.portlet.config";

    /**
     * Name of the action used as error handler
     */
    public static String ERROR_ACTION = "errorHandler";

    /**
     * Key for the portlet namespace stored in the
     * {@link org.apache.struts2.portlet.context.PortletActionContext}.
     */
    public static String PORTLET_NAMESPACE = "struts.portlet.portletNamespace";

    /**
     * Key for the mode-to-namespace map stored in the
     * {@link org.apache.struts2.portlet.context.PortletActionContext}.
     */
    public static String MODE_NAMESPACE_MAP = "struts.portlet.modeNamespaceMap";

    /**
     * Key for the mode-to-default-action map stored in the
     * {@link org.apache.struts2.portlet.context.PortletActionContext}.
     */
    public static String DEFAULT_ACTION_MAP = "struts.portlet.modeActionMap";

    /**
     * Key for the default action name for the portlet, stored in the
     * {@link org.apache.struts2.portlet.context.PortletActionContext}.
     */
    public static String DEFAULT_ACTION_FOR_MODE = "struts.portlet.defaultActionForMode";
    
    /**
     * Key for request attribute indicating if the action has been reset. 
     */
    public static String ACTION_RESET = "struts.portlet.actionReset";
    
    /**
     * Key for session attribute indicating the location of the render direct action.
     */
    public static String RENDER_DIRECT_LOCATION = "struts.portlet.renderDirectLocation";

    /**
     * Namespace use for executing the renderDirect action. Necessary to get the namespaces on s:urls correct.
     */
    public static String RENDER_DIRECT_NAMESPACE = "struts.portlet.renderDirectNamespace";

    /**
     * Key for the dispatch instruction for the {@link DispatcherServlet}
     */
	public static String DISPATCH_TO = "struts.portlet.dispatchTo";
	
	/**
	 * Session key where the value stack from the event phase is stored.
	 */
	public static String STACK_FROM_EVENT_PHASE = "struts.portlet.valueStackFromEventPhase";

	/**
	 * Default name of dispatcher servlet in web.xml
	 */
	public static String DEFAULT_DISPATCHER_SERVLET_NAME = "Struts2PortletDispatcherServlet";

    /**
   	 * Key for the action mapping in the context
       */
   	public static String ACTION_MAPPING = ServletActionContext.ACTION_MAPPING;

}
