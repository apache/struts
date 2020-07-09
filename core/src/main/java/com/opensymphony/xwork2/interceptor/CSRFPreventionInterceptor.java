package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class CSRFPreventionInterceptor extends AbstractInterceptor {

    private List<String> exemptedPaths = new ArrayList<String>();
    private DefaultResourceIsolationPolicy fmPolicy = new DefaultResourceIsolationPolicy();

    public List<String> getExemptedPaths(){
        return this.exemptedPaths;
    }

    public void setExemptedPaths(List<String> paths){
        this.exemptedPaths = paths;
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        ActionContext context = invocation.getInvocationContext();
        HttpServletRequest request = context.getServletRequest();

        fmPolicy.setExemptedPaths(this.exemptedPaths);

        if (!fmPolicy.isRequestAllowed(request)){
            throw new RuntimeException("Request is not allowed");
        }

        return invocation.invoke();

        //TODO add Vary headers

//        context = invocation.getInvocationContext();
//        HttpServletResponse response = (HttpServletResponse) context.get(StrutsStatics.HTTP_RESPONSE);
//        response.addHeader("Vary", "Sec-Fetch-Dest");
//        System.out.println(response);
//        System.out.println(response.getClass());
    }
}
