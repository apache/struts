package org.apache.struts2.portlet.result;

import javax.portlet.*;
import java.io.IOException;

/**
 * PortletResultHelperJSR286 implements PortletResultHelper for Portlet 2.0 API (JSR286).
 *
 * @author Rene Gielen
 */
public class PortletResultHelperJSR286 implements PortletResultHelper {

    /**
     * Set a render parameter, abstracted from the used Portlet API version. This implementation assumes that the given
     * response is a {@link javax.portlet.StateAwareResponse}, as JSR286 implies.
     *
     * @param response The response to set the parameter on.
     * @param key      The parameter key to set.
     * @param value    The parameter value to set.
     */
    public void setRenderParameter( PortletResponse response, String key, String value ) {
        ((StateAwareResponse) response).setRenderParameter(key, value);
    }

    /**
     * Set a portlet mode, abstracted from the used Portlet API version. This implementation assumes that the given
     * response is a {@link javax.portlet.StateAwareResponse}, as JSR286 implies.
     *
     * @param response    The response to set the portlet mode on.
     * @param portletMode The portlet mode to set.
     */
    public void setPortletMode( PortletResponse response, PortletMode portletMode ) throws PortletModeException {
        ((StateAwareResponse) response).setPortletMode(portletMode);
    }

    /**
     * Call a dispatcher's include method, abstracted from the used Portlet API version. This implementation assumes
     * that the response is a {@link javax.portlet.MimeResponse}, as JSR286 implies.
     *
     * @param dispatcher  The dispatcher to call the include method on.
     * @param contentType The content type to set for the response.
     * @param request     The request to use for including
     * @param response    The response to use for including
     */
    public void include( PortletRequestDispatcher dispatcher, String contentType, PortletRequest request,
                         PortletResponse response ) throws IOException, PortletException {
        MimeResponse res = (MimeResponse) response;
        res.setContentType(contentType);
        dispatcher.include(request, res);
    }

}
