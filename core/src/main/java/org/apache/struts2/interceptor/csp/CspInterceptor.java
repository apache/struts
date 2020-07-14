package org.apache.struts2.interceptor.csp;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;


public class CspInterceptor extends AbstractInterceptor implements PreResultListener {

    private static final Logger LOG = LoggerFactory.getLogger(CspInterceptor.class);

    private CspSettings settings = new DefaultCspSettings();

    public void setSettings(CspSettings settings) {
        this.settings = settings;
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        // TODO : check content-type and uri for csp reports and logCspViolation()
//        HttpServletRequest request = invocation.getInvocationContext().getServletRequest();
        invocation.addPreResultListener(this);
        return invocation.invoke();

    }

    public void beforeResult(ActionInvocation invocation, String resultCode) {
        HttpServletResponse response = invocation.getInvocationContext().getServletResponse();
        settings.addCspHeaders(response);
    }

    private void logCspViolation() {

    }
}
