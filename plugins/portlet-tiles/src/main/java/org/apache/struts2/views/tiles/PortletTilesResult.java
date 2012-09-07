package org.apache.struts2.views.tiles;

import com.opensymphony.xwork2.ActionInvocation;
import freemarker.template.TemplateException;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.ServletDispatcherResult;
import org.apache.struts2.portlet.PortletConstants;
import org.apache.struts2.portlet.context.PortletActionContext;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.TilesException;
import org.apache.tiles.access.TilesAccess;

import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * JIRA WW-2749 (STRUTS).
 */
public class PortletTilesResult extends ServletDispatcherResult {

    private static final long serialVersionUID = -3806939435493086244L;

    public PortletTilesResult() {
        super();
    }

    public PortletTilesResult(String location) {
        super(location);
    }

    // FIXME PATCH du JIRA WW-2749 (STRUTS)
    public void doExecute(String location, ActionInvocation invocation)
            throws IOException, TemplateException, PortletException, TilesException {

        if (PortletActionContext.getPhase().isAction() || PortletActionContext.getPhase().isEvent()) {
            executeActionResult(location, invocation);
        } else {
            executeRenderResult(location);
        }
    }

    /**
     * @param location
     * @throws TilesException
     */
    protected void executeRenderResult(String location) throws TilesException {
        setLocation(location);

        ServletContext servletContext = ServletActionContext.getServletContext();
        TilesContainer container = TilesAccess.getContainer(servletContext);

        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();

        container.render(location, request, response);
    }

    /**
     * @param location
     * @param invocation
     */
    protected void executeActionResult(String location, ActionInvocation invocation) {
        ActionResponse res = PortletActionContext.getActionResponse();

        res.setRenderParameter(PortletConstants.ACTION_PARAM, "tilesDirect");

        Map<String, Object> sessionMap = invocation.getInvocationContext().getSession();
        sessionMap.put(PortletConstants.RENDER_DIRECT_LOCATION, location);

        res.setRenderParameter(PortletConstants.MODE_PARAM, PortletActionContext.getRequest().getPortletMode().toString());
    }

}
