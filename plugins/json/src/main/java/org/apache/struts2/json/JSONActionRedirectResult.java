package org.apache.struts2.json;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.ServletActionRedirectResult;

/**
 * Specialized form of {@link ServletActionRedirectResult} which takes care of
 * situation that browser has a JS/AJAX context, there are no validation errors
 * and action is executed. In this case a http redirect is harmful as browsers
 * don't pass them to JS handlers. So this result produces a JSON response
 * containing redirect data.
 *
 * <p>To be used along with {@link JSONValidationInterceptor}.</p>
 *
 * <p>Response JSON looks like this:
 * 
 *     <pre>{"location": "$redirect url$"}</pre>
 * </p>
 *
 */
public class JSONActionRedirectResult extends ServletActionRedirectResult {

    private static final long serialVersionUID = 3107276294073879542L;

    @Override
    protected void sendRedirect(HttpServletResponse response, String finalLocation) throws IOException {
        if (sendJsonInsteadOfRedirect()) {
            printJson(response, finalLocation);
        } else {
            super.sendRedirect(response, finalLocation);
        }
    }

    /**
     * If browser has called action in a JS/AJAX context we cannot send a
     * redirect as response.
     *
     * @return true if a JSON response shall be generated, false if a redirect
     *         shall be sent.
     */
    protected boolean sendJsonInsteadOfRedirect() {
        HttpServletRequest request = ServletActionContext.getRequest();
        return isJsonEnabled(request) && !isValidateOnly(request);
    }

    protected void printJson(HttpServletResponse response, String finalLocation) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setHeader("Location", finalLocation);
        PrintWriter writer = response.getWriter();
        writer.write("{\"location\": \"");
        writer.write(finalLocation);
        writer.write("\"}");
        writer.close();
    }

    protected boolean isJsonEnabled(HttpServletRequest request) {
        return "true".equals(request.getParameter(JSONValidationInterceptor.VALIDATE_JSON_PARAM));
    }

    protected boolean isValidateOnly(HttpServletRequest request) {
        return "true".equals(request.getParameter(JSONValidationInterceptor.VALIDATE_ONLY_PARAM));
    }
}
