package org.apache.struts2.components;

import java.io.IOException;
import java.io.Writer;

import org.apache.struts2.StrutsException;
import org.apache.struts2.components.URL;
import org.apache.struts2.components.UrlRenderer;
import org.apache.struts2.portlet.util.PortletUrlHelper;

import com.opensymphony.xwork2.util.TextUtils;

/**
 * Implementation of the {@link URLRenderer} interface that renders URLs for portlet environments.
 * 
 * @see URLRenderer
 *
 */
public class PortletUrlRenderer implements UrlRenderer {
	
	/**
	 * {@inheritDoc}
	 */
	public void renderUrl(Writer writer, URL urlComponent) {
		String scheme = urlComponent.req.getScheme();

		if (urlComponent.scheme != null) {
			scheme = urlComponent.scheme;
		}

	       String result;
	        if (urlComponent.value == null && urlComponent.action != null) {
	                result = PortletUrlHelper.buildUrl(urlComponent.action, urlComponent.namespace, urlComponent.parameters, urlComponent.portletUrlType, urlComponent.portletMode, urlComponent.windowState);
	        } else {
	                result = PortletUrlHelper.buildResourceUrl(urlComponent.value, urlComponent.parameters);
	        }
	        if ( urlComponent.anchor != null && urlComponent.anchor.length() > 0 ) {
	            result += '#' + urlComponent.anchor;
	        }

	        String var = urlComponent.getVar();

	        if (var != null) {
	        	urlComponent.putInContext(result);

	            // add to the request and page scopes as well
	        	urlComponent.req.setAttribute(var, result);
	        } else {
	            try {
	                writer.write(result);
	            } catch (IOException e) {
	                throw new StrutsException("IOError: " + e.getMessage(), e);
	            }
	        }
	}

	/**
	 * {@inheritDoc}
	 */
	public void renderFormUrl(Form formComponent) {
		String action = null;
        if (formComponent.action != null) {
            // if it isn't specified, we'll make somethig up
            action = formComponent.findString(formComponent.action);
        }

        String type = "action";
        if (TextUtils.stringSet(formComponent.method)) {
            if ("GET".equalsIgnoreCase(formComponent.method.trim())) {
                type = "render";
            }
        }
        if (action != null) {
            String result = PortletUrlHelper.buildUrl(action, formComponent.namespace,
                    formComponent.getParameters(), type, formComponent.portletMode, formComponent.windowState);
            formComponent.addParameter("action", result);

            // namespace: cut out anything between the start and the last /
            int slash = result.lastIndexOf('/');
            if (slash != -1) {
                formComponent.addParameter("namespace", result.substring(0, slash));
            } else {
                formComponent.addParameter("namespace", "");
            }

            // name/id: cut out anything between / and . should be the id and
            // name
            String id = formComponent.getId();
            if (id == null) {
                slash = action.lastIndexOf('/');
                int dot = action.indexOf('.', slash);
                if (dot != -1) {
                    id = action.substring(slash + 1, dot);
                } else {
                    id = action.substring(slash + 1);
                }
                formComponent.addParameter("id", formComponent.escape(id));
            }
        }

		
	}

}
