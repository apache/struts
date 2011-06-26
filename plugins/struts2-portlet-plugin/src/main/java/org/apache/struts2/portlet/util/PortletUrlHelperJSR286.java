package org.apache.struts2.portlet.util;

import org.apache.struts2.portlet.context.PortletActionContext;
import javax.portlet.PortletRequest;
import javax.portlet.MimeResponse;
import javax.portlet.BaseURL;
import javax.portlet.PortletSecurityException;
import java.util.Map;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * PortletUrlJSR286Helper.
 *
 * @author Rene Gielen
 */
public class PortletUrlHelperJSR286 extends PortletUrlHelper {

    private static final Logger LOG = LoggerFactory.getLogger(PortletUrlHelperJSR286.class);

    protected String encodeUrl( StringBuffer sb, PortletRequest req ) {
        MimeResponse resp = (MimeResponse) PortletActionContext.getResponse();
        return resp.encodeURL(req.getContextPath() + sb.toString());
    }

    protected Object createUrl( String scheme, String type, Map<String, String[]> portletParams ) {
        MimeResponse response = (MimeResponse) PortletActionContext.getResponse();
        BaseURL url;
        if (URLTYPE_NAME_ACTION.equalsIgnoreCase(type)) {
            if (LOG.isDebugEnabled()) LOG.debug("Creating action url");
            url = response.createActionURL();
        }
        else if(URLTYPE_NAME_RESOURCE.equalsIgnoreCase(type)) {
        	if (LOG.isDebugEnabled()) LOG.debug("Creating resource url");
        	url = response.createResourceURL();
        }
        else {
            if (LOG.isDebugEnabled()) LOG.debug("Creating render url");
            url = response.createRenderURL();
        }

        url.setParameters(portletParams);

        if ("HTTPS".equalsIgnoreCase(scheme)) {
            try {
                url.setSecure(true);
            } catch ( PortletSecurityException e) {
                LOG.error("Cannot set scheme to https", e);
            }
        }
        return url;
    }

}
