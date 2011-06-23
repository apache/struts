package org.apache.struts2.portlet.result;

import javax.portlet.*;
import java.io.IOException;

/**
 * PortletResultHelper abstracts Portlet API result functions specific to the used API spec version.
 *
 * @author Rene Gielen
 */

public interface PortletResultHelper {

    /**
     * Set a render parameter, abstracted from the used Portlet API version
     *
     * @param response The response to set the parameter on.
     * @param key      The parameter key to set.
     * @param value    The parameter value to set.
     */
    void setRenderParameter( PortletResponse response, String key, String value );

    /**
     * Set a portlet mode, abstracted from the used Portlet API version
     *
     * @param response    The response to set the portlet mode on.
     * @param portletMode The portlet mode to set.
     */
    void setPortletMode( PortletResponse response, PortletMode portletMode ) throws PortletModeException;

    /**
     * Call a dispatcher's include method, abstracted from the used Portlet API version.
     *
     * @param dispatcher  The dispatcher to call the include method on.
     * @param contentType The content type to set for the response.
     * @param request     The request to use for including
     * @param response    The response to use for including
     */
    void include( PortletRequestDispatcher dispatcher, String contentType, PortletRequest request,
                  PortletResponse response ) throws IOException, PortletException;
}
